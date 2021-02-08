import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.math.*;
import org.jlab.groot.fitter.*;

GStyle.getAxisAttributesX().setTitleFontSize(32);
GStyle.getAxisAttributesY().setTitleFontSize(32);
GStyle.getAxisAttributesX().setLabelFontSize(24);
GStyle.getAxisAttributesY().setLabelFontSize(24);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

double LIGHTSPEED = 30.0; // speed of light in cm/ns
double CUT_PROTON_P = 0.2; // cut on proton low momentum

def cli = new CliBuilder(usage:'clas6ProtonTOF.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
cli.c(longOpt:'counter', args:1, argName:'count by events', type: int, 'Event progress counter')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def printCounter = 20000;
if(options.c) printCounter = options.c;

def maxEvents = 0;
if(options.M) maxEvents = options.M;

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

double P_full_lo = 0.0;
double P_full_hi = 3.0;
double P_bin_width = 0.03;
int P_full_bins = (P_full_hi - P_full_lo)/P_bin_width;
H2F h2_dTOF_VS_P = new H2F("h2_dTOF_VS_P",P_full_bins,P_full_lo,P_full_hi,120,-12.0,12.0);
h2_dTOF_VS_P.setTitle("Experiment: eg2");
h2_dTOF_VS_P.setTitleX("Momentum (GeV/c)");
h2_dTOF_VS_P.setTitleY("#DeltaTOF (ns)");

int Pl_bins = 4;
double Pl_lo = 0.2;
double Pl_hi = Pl_bins*P_bin_width + Pl_lo;
H2F h2_dTOF_VS_P_lo = new H2F("h2_dTOF_VS_P_lo",Pl_bins,Pl_lo,Pl_hi,140,-14.0,14.0);
h2_dTOF_VS_P_lo.setTitle("Experiment: eg2");
h2_dTOF_VS_P_lo.setTitleX("Momentum (GeV/c)");
h2_dTOF_VS_P_lo.setTitleY("#DeltaTOF (ns)");

int Pm1_bins = 12;
double Pm1_lo = Pl_hi;
double Pm1_hi = Pm1_bins*P_bin_width + Pm1_lo;
H2F h2_dTOF_VS_P_mid1 = new H2F("h2_dTOF_VS_P_mid1",Pm1_bins,Pm1_lo,Pm1_hi,140,-14.0,14.0);
h2_dTOF_VS_P_mid1.setTitle("Experiment: eg2");
h2_dTOF_VS_P_mid1.setTitleX("Momentum (GeV/c)");
h2_dTOF_VS_P_mid1.setTitleY("#DeltaTOF (ns)");

int Pm2_bins = 60;
double Pm2_lo = Pm1_hi;
double Pm2_hi = Pm2_bins*P_bin_width + Pm2_lo;
H2F h2_dTOF_VS_P_mid2 = new H2F("h2_dTOF_VS_P_mid2",Pm2_bins,Pm2_lo,Pm2_hi,120,-12.0,12.0);
h2_dTOF_VS_P_mid2.setTitle("Experiment: eg2");
h2_dTOF_VS_P_mid2.setTitleX("Momentum (GeV/c)");
h2_dTOF_VS_P_mid2.setTitleY("#DeltaTOF (ns)");

double Pu_lo = Pm2_hi;
double Pu_hi = 2.9;
int Pu_bins = (Pu_hi - Pu_lo)/P_bin_width;
H2F h2_dTOF_VS_P_hi = new H2F("h2_dTOF_VS_P_hi",Pu_bins,Pu_lo,Pu_hi,100,-5.0,5.0);
h2_dTOF_VS_P_hi.setTitle("Experiment: eg2");
h2_dTOF_VS_P_hi.setTitleX("Momentum (GeV/c)");
h2_dTOF_VS_P_hi.setTitleY("#DeltaTOF (ns)");

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
        if(partLV.p()>=Pl_lo && partLV.p()<Pl_hi) h2_dTOF_VS_P_lo.fill(partLV.p(),tofProton-tofElectron);
        if(partLV.p()>=Pm1_lo && partLV.p()<Pm1_hi) h2_dTOF_VS_P_mid1.fill(partLV.p(),tofProton-tofElectron);
        if(partLV.p()>=Pm2_lo && partLV.p()<Pm2_hi) h2_dTOF_VS_P_mid2.fill(partLV.p(),tofProton-tofElectron);
        if(partLV.p()>=Pu_lo && partLV.p()<=Pu_hi) h2_dTOF_VS_P_hi.fill(partLV.p(),tofProton-tofElectron);
//        System.out.println("dTOF " + (tofProton-tofElectron));
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

TCanvas c2 = new TCanvas("c2",600,600);
ParallelSliceFitter fitterLo = new ParallelSliceFitter(h2_dTOF_VS_P_lo);
//fitterLo.setRange(-5.0,5.0);
fitterLo.setBackgroundOrder(ParallelSliceFitter.P1_BG);
fitterLo.fitSlicesX();
//fitterLo.setLineColor(32);
//fitterLo.setLineWidth(5);
c2.draw(fitterLo.getMeanSlices());
fitterLo.getInspectFitsPane();
fitterLo.inspectFits();

TCanvas c3 = new TCanvas("c3",600,600);
ParallelSliceFitter fitterMid1 = new ParallelSliceFitter(h2_dTOF_VS_P_mid1);
fitterMid1.setRange(-5.0,5.0);
fitterMid1.setBackgroundOrder(ParallelSliceFitter.P1_BG);
fitterMid1.fitSlicesX();
//fitterMid1.setLineColor(32);
//fitterMid1.setLineWidth(5);
c3.draw(fitterMid1.getMeanSlices());
fitterMid1.getInspectFitsPane();
fitterMid1.inspectFits();

TCanvas c4 = new TCanvas("c4",600,600);
ParallelSliceFitter fitterMid2 = new ParallelSliceFitter(h2_dTOF_VS_P_mid2);
fitterMid2.setRange(-3.0,3.0);
fitterMid2.setBackgroundOrder(ParallelSliceFitter.P1_BG);
fitterMid2.fitSlicesX();
//fitterMid2.setLineColor(32);
//fitterMid2.setLineWidth(5);
c4.draw(fitterMid2.getMeanSlices());
fitterMid2.getInspectFitsPane();
fitterMid2.inspectFits();

TCanvas c5 = new TCanvas("c5",600,600);
ParallelSliceFitter fitterHi = new ParallelSliceFitter(h2_dTOF_VS_P_hi);
fitterHi.setRange(-1.5,1.5);
fitterHi.setBackgroundOrder(ParallelSliceFitter.P1_BG);
fitterHi.fitSlicesX();
//fitterHi.setLineColor(32);
//fitterHi.setLineWidth(5);
c5.draw(fitterHi.getMeanSlices());
fitterHi.getInspectFitsPane();
fitterHi.inspectFits();

DataVector xMean = fitterLo.getMeanSlices().getVectorX();
fitterMid1.getMeanSlices().getVectorX().getArray().each { val ->
  xMean.add(val);
}
fitterMid2.getMeanSlices().getVectorX().getArray().each { val ->
  xMean.add(val);
}
fitterHi.getMeanSlices().getVectorX().getArray().each { val ->
  xMean.add(val);
}

DataVector xErr = new DataVector();
DataVector yErr = new DataVector();
DataVector yMean = new DataVector();
fitterLo.getMeanSlices().getVectorY().getArray().eachWithIndex { val, ind ->
  yMean.add(val);
  yErr.add(fitterLo.getMeanSlices().getDataEY(ind));
  xErr.add(0.0);
}
fitterMid1.getMeanSlices().getVectorY().getArray().eachWithIndex { val, ind ->
  yMean.add(val);
  yErr.add(fitterMid1.getMeanSlices().getDataEY(ind));
  xErr.add(0.0);
}
fitterMid2.getMeanSlices().getVectorY().getArray().eachWithIndex { val, ind ->
  yMean.add(val);
  yErr.add(fitterMid2.getMeanSlices().getDataEY(ind));
  xErr.add(0.0);
}
fitterHi.getMeanSlices().getVectorY().getArray().eachWithIndex { val, ind ->
  yMean.add(val);
  yErr.add(fitterHi.getMeanSlices().getDataEY(ind));
  xErr.add(0.0);
}
DataVector ySigma = new DataVector();
fitterLo.getSigmaSlices().getVectorY().getArray().each { val ->
  ySigma.add(Math.abs(val));
}
fitterMid1.getSigmaSlices().getVectorY().getArray().each { val ->
  ySigma.add(Math.abs(val));
}
fitterMid2.getSigmaSlices().getVectorY().getArray().each { val ->
  ySigma.add(Math.abs(val));
}
fitterHi.getSigmaSlices().getVectorY().getArray().each { val ->
  ySigma.add(Math.abs(val));
}

DataVector yLower = new DataVector();
DataVector yLowerSigma = new DataVector();
yLower.copy(yMean);
yLowerSigma.copy(ySigma);
yLowerSigma.mult(-2.0);
yLower.addDataVector(yLowerSigma);

DataVector yUpper = new DataVector();
DataVector yUpperSigma = new DataVector();
yUpper.copy(yMean);
yUpperSigma.copy(ySigma);
yUpperSigma.mult(2.0);

yUpper.addDataVector(yUpperSigma);

//GraphErrors grSliceResults = new GraphErrors("grSliceResults",xValues,yValues,xErrors,yErrors);
GraphErrors grLower = new GraphErrors("grSliceResults",xMean,yLower,xErr,yErr);
grLower.setMarkerSize(3);
GraphErrors grUpper = new GraphErrors("grSliceResults",xMean,yUpper,xErr,yErr);
grUpper.setMarkerSize(3);
TCanvas c6 = new TCanvas("c6",600,600);
c6.getPad().getAxisZ().setLog(true);
c6.draw(h2_dTOF_VS_P);
c6.draw(grLower,"same");
c6.draw(grUpper,"same");

TCanvas c7 = new TCanvas("c7",600,600);
c7.draw(grLower);
String fcn9 = "[a]+[b]*x+[c]*x*x+[d]*x*x*x+[e]*x*x*x*x+[f]*x*x*x*x*x+[g]*x*x*x*x*x*x+[h]*x*x*x*x*x*x*x+[i]*x*x*x*x*x*x*x*x+[j]*x*x*x*x*x*x*x*x*x";
F1D f1 = new F1D("f1",fcn9, 0.2, 0.8);
for(int ii = 0; ii < 9; ii++){
  f1.setParameter(ii,1.0);
}
DataFitter.fit(f1, grLower, "Q"); //No options uses error for sigma
f1.setLineColor(32);
f1.setLineWidth(5);
f1.setLineStyle(1);
//f1.setOptStat(1111);
println "Parameters: f1"
for(int j=0; j<f1.getNPars(); j++) System.out.println(" par = " + f1.parameter(j).value() + " error = " + f1.parameter(j).error());;
c7.draw(f1,"same");

String fcn3 = "[p0]+[p1]*x+[p2]*x*x+[p3]*x*x*x";
F1D f2 = new F1D("f2",fcn3, 0.8, 3.0);
for(int ii = 0; ii < 3; ii++){
  f2.setParameter(ii,1.0);
}
DataFitter.fit(f2, grLower, "Q"); //No options uses error for sigma
f2.setLineColor(34);
f2.setLineWidth(5);
f2.setLineStyle(1);
//f2.setOptStat(1111);
println "Parameters: f2"
for(int j=0; j<f2.getNPars(); j++) System.out.println(" par = " + f2.parameter(j).value() + " error = " + f2.parameter(j).error());;
c7.draw(f2,"same");

TCanvas c8 = new TCanvas("c8",600,600);
c8.draw(grUpper);
F1D f3 = new F1D("f3",fcn9, 0.2, 0.8);
for(int ii = 0; ii < 9; ii++){
  f3.setParameter(ii,1.0);
}
DataFitter.fit(f3, grUpper, "Q"); //No options uses error for sigma
f3.setLineColor(32);
f3.setLineWidth(5);
f3.setLineStyle(1);
//f3.setOptStat(1111);
println "Parameters: f3"
for(int j=0; j<f3.getNPars(); j++) System.out.println(" par = " + f3.parameter(j).value() + " error = " + f3.parameter(j).error());;
c8.draw(f3,"same");

F1D f4 = new F1D("f4",fcn3, 0.8, 3.0);
for(int ii = 0; ii < 3; ii++){
  f4.setParameter(ii,1.0);
}
DataFitter.fit(f4, grUpper, "Q"); //No options uses error for sigma
f4.setLineColor(34);
f4.setLineWidth(5);
f4.setLineStyle(1);
//f4.setOptStat(1111);
println "Parameters: f4"
for(int j=0; j<f4.getNPars(); j++) System.out.println(" par = " + f4.parameter(j).value() + " error = " + f4.parameter(j).error());;
c8.draw(f4,"same");
