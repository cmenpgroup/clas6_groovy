import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.math.*;
import org.jlab.groot.fitter.*;

import groovy.cli.commons.CliBuilder;

//import org.jlab.jnp.groot.graphics.Legend;

import eg2Cuts.clas6Proton;
clas6Proton myProton = new clas6Proton(); // create the proton object

GStyle.getAxisAttributesX().setTitleFontSize(32);
GStyle.getAxisAttributesY().setTitleFontSize(32);
GStyle.getAxisAttributesX().setLabelFontSize(24);
GStyle.getAxisAttributesY().setLabelFontSize(24);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

double CUT_PROTON_P = 0.2; // cut on proton low momentum

def cli = new CliBuilder(usage:'clas6ProtonTOF-sysErr.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
cli.c(longOpt:'counter', args:1, argName:'count by events', type: int, 'Event progress counter')
cli.P(longOpt:'protonIDcut', args:1, argName:'cut index', type: int, 'Proton ID Cut index')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def printCounter = 20000;
if(options.c) printCounter = options.c;

def maxEvents = 0;
if(options.M) maxEvents = options.M;

def iCut = 0;
if(options.P){
  iCut = options.P;
  if(iCut<0 || iCut>=clas6Proton.ProtonIDCuts.values().size()){
    int maxIndex = clas6Proton.ProtonIDCuts.values().size()-1;
    println "Proton ID Cut Index must be between 0 and " + maxIndex;
    cli.usage();
    return;
  }
}

myProton.SetCuts(clas6Proton.ProtonIDCuts.values()[iCut]);
println myProton.GetCutName();

def extraArguments = options.arguments()
if (extraArguments.isEmpty()){
  println "No input file!";
  cli.usage();
  return;
}

HipoChain reader = new HipoChain();

extraArguments.each { infile ->
  reader.addFile(infile);
}
reader.open();

LorentzVector partLV = new LorentzVector(0,0,0,0);

PhysicsConstants PhyConsts= new PhysicsConstants();
double LIGHTSPEED = PhyConsts.speedOfLight(); // speed of light in cm/ns
println "Speed of light = " + LIGHTSPEED + " cm/ns";

double P_full_lo = 0.0;
double P_full_hi = 3.0;
double P_bin_width = 0.03;
int P_full_bins = (P_full_hi - P_full_lo)/P_bin_width;
H2F h2_dTOF_VS_P = new H2F("h2_dTOF_VS_P",P_full_bins,P_full_lo,P_full_hi,120,-12.0,12.0);
h2_dTOF_VS_P.setTitle("Experiment: eg2");
h2_dTOF_VS_P.setTitleX("Momentum (GeV/c)");
h2_dTOF_VS_P.setTitleY("#DeltaTOF (ns)");

H2F h2_dTOF_VS_Pcut = new H2F("h2_dTOF_VS_Pcut",P_full_bins,P_full_lo,P_full_hi,120,-12.0,12.0);
h2_dTOF_VS_Pcut.setTitle("Experiment: eg2");
h2_dTOF_VS_Pcut.setTitleX("Momentum (GeV/c)");
h2_dTOF_VS_Pcut.setTitleY("#DeltaTOF (ns)");

Event      event  = new Event();
Bank       bank   = new Bank(reader.getSchemaFactory().getSchema("EVENT::particle"));
Bank       scpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::scpb"));

int counterFile = 0;
def PosChargedList = [];

while(reader.hasNext()){   // Loop over all events
  if(counterFile % printCounter == 0) println counterFile;
  if(maxEvents!=0 && counterFile >= maxEvents) break;

  reader.nextEvent(event);
  event.read(bank);
  event.read(scpb);

  PosChargedList.clear();
  boolean firstElectron = false;

  int rows = bank.getRows();
  for(int i = 0; i < rows; i++){
    def px = bank.getFloat("px",i);
    def py = bank.getFloat("py",i);
    def pz = bank.getFloat("pz",i);
    int pid = bank.getInt("pid",i);
    int charge = bank.getInt("charge",i);

    if(pid==11 && !firstElectron){
      firstElectron = true;
      if(bank.getInt("scstat",i)>0 && scpb.getRows()>0){
        scTime = scpb.getFloat("time",bank.getInt("scstat",i)-1);
        scPath = scpb.getFloat("path",bank.getInt("scstat",i)-1);
        tofElectron = scTime - (scPath/LIGHTSPEED);
      }
    }
    if(charge>0) PosChargedList.add(i);
  }
  if(PosChargedList.size()>0 && firstElectron){
    PosChargedList.each { val ->
      px = bank.getFloat("px",val);
      py = bank.getFloat("py",val);
      pz = bank.getFloat("pz",val);
      partLV.setPxPyPzM(px, py, pz, PhyConsts.massProton());

      if(bank.getInt("scstat",val)>0 && scpb.getRows()>0 && partLV.p()>CUT_PROTON_P){
        scTimeProton = scpb.getFloat("time",bank.getInt("scstat",val)-1);
        scPathProton = scpb.getFloat("path",bank.getInt("scstat",val)-1);
        def Psq = partLV.p()*partLV.p();
        def Msq = partLV.mass()*partLV.mass();
        tofProton = scTimeProton - (scPathProton/LIGHTSPEED)*Math.sqrt(Psq+Msq)/partLV.p();
        h2_dTOF_VS_P.fill(partLV.p(),tofProton-tofElectron);
        if(myProton.Get_ProtonTOF_Cut(partLV.p(),tofProton-tofElectron)){
          h2_dTOF_VS_Pcut.fill(partLV.p(),tofProton-tofElectron);
        }
      }
    }
  }

  counterFile++;
}
System.out.println("processed (total) = " + counterFile);

TCanvas c1 = new TCanvas("c1",600,600);
c1.getPad().setTitleFontSize(32);
c1.getPad().getAxisZ().setLog(true);
c1.draw(h2_dTOF_VS_P);

String[] CutLabel = ["std","1_0","1_5","2_0","2_5","3_0"];
//String[] CutLabel = ["std","1_5","3_0"];
String[] CutType = ["bot","top"];
String[] MomType = ["lo","hi"];
F1D[][][] fcnCuts = new F1D[CutLabel.size()][MomType.size()][CutType.size()];

int canCount = 0;

//LegendNode2D myLegend = new LegendNode2D(10,10);

CutLabel.eachWithIndex { fCutLabel,iCutLabel->
  MomType.eachWithIndex { fMomType,iMomType->
    CutType.eachWithIndex { fCutType,iCutType->
      fcnCuts[iCutLabel][iMomType][iCutType] = myProton.fcnMomentumCuts(fCutLabel,fMomType,fCutType);
      fcnCuts[iCutLabel][iMomType][iCutType].setLineWidth(3);
      fcnCuts[iCutLabel][iMomType][iCutType].setLineStyle(1);
      fcnCuts[iCutLabel][iMomType][iCutType].setOptStat(0);
      fcnCuts[iCutLabel][iMomType][iCutType].setLineColor(canCount);
      c1.draw(fcnCuts[iCutLabel][iMomType][iCutType],"same");
    }
  }
  canCount++;
}

TCanvas c2 = new TCanvas("c2",600,600);
c2.getPad().setTitleFontSize(32);
c2.getPad().getAxisZ().setLog(true);
c2.draw(h2_dTOF_VS_Pcut);
