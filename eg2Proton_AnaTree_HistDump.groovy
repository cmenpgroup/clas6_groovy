import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*; // new import for ntuples

import eg2AnaTree.*;
import eg2Cuts.eg2Target;
eg2Target myTarget = new eg2Target();  // create the eg2 target object

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;

double beamEnergy = myTarget.Get_Beam_Energy();
println "Beam " + beamEnergy + " GeV";
double W_DIS = myTarget.Get_W_DIS();
double Q2_DIS = myTarget.Get_Q2_DIS();
double YB_DIS = myTarget.Get_YB_DIS();

double[] normDIS = [1.11978 ,  1.0609 ,  2.19708]; // DIS e- normalization factors (Acc Corr, Rad Corr and Coulomb)

def cli = new CliBuilder(usage:'eg2Proton_AnaTree_MR_AllTgts.groovy [options]')
cli.h(longOpt:'help', 'Print this message.')
cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
cli.o(longOpt:'output', args:1, argName:'Histogram output file', type: String, 'Output file name')
cli.tC(longOpt:'carbon', args:1, argName:'Carbon input file', type: String, 'Carbon input file')
cli.tFe(longOpt:'iron', args:1, argName:'Iron input file', type: String, 'Iron input file')
cli.tPb(longOpt:'lead', args:1, argName:'Lead input file', type: String, 'Lead input file')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def maxEvents = -1;
if(options.M) maxEvents = options.M;

def outFile = "eg2Proton_AnaTree_HistDump.hipo";
if(options.o) outFile = options.o;

def inCarbon = "ntuple_C_Npos.hipo";
if(options.tC) inCarbon = options.tC;

def inIron = "ntuple_Fe_Npos.hipo";
if(options.tFe) inIron = options.tFe;

def inLead = "ntuple_Pb_Npos.hipo";
if(options.tPb) inLead = options.tPb;

def extraArguments = options.arguments()
if (!extraArguments.isEmpty()){
  println "No inputs without a flag!";
  cli.usage();
  return;
}

long st = System.currentTimeMillis(); // start time

HistInfo myHI = new HistInfo();
List<String> Var = myHI.getVariables();
List<String> xLabel = myHI.getXlabel();
List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<Integer> nbins = myHI.getNbins();
List<Double> xlo = myHI.getXlo();
List<Double> xhi = myHI.getXhi();

String[] DirLabel = ["/Carbon_LD2","/Carbon","/Iron_LD2","/Iron","/Lead_LD2","/Lead"];

TDirectory dir = new TDirectory();

//*****************************
// Analyzing Target histograms
//*****************************
H1F[][][] h1_Ylds = new H1F[solidTgt.size()][Var.size()][TgtLabel.size()];
int dirCount = 0; // counter for the histogram directory labels

solidTgt.eachWithIndex{nSolid,iSolid->
  TgtLabel.eachWithIndex{nTgt,iTgt->
    if(iSolid==0) myYldsC.createHistograms(iTgt);
    if(iSolid==1) myYldsFe.createHistograms(iTgt);
    if(iSolid==2) myYldsPb.createHistograms(iTgt);
    dir.mkdir(DirLabel[dirCount]);
    dir.cd(DirLabel[dirCount]);
    dirCount++;
    Var.eachWithIndex { nVar, iVar->
      if(iSolid==0) h1_Ylds[iSolid][iVar][iTgt] = myYldsC.getHistogram(iVar);
      if(iSolid==1) h1_Ylds[iSolid][iVar][iTgt] = myYldsFe.getHistogram(iVar);
      if(iSolid==2) h1_Ylds[iSolid][iVar][iTgt] = myYldsPb.getHistogram(iVar);
      dir.addDataSet(h1_Ylds[iSolid][iVar][iTgt]); // add to the histogram file
    }
  }
}

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen
