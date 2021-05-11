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
cli.dC(longOpt:'carbonDIS', args:1, argName:'Carbon input file', type: String, 'Carbon DIS file')
cli.dFe(longOpt:'ironDIS', args:1, argName:'Iron DIS file', type: String, 'Iron DIS file')
cli.dPb(longOpt:'leadDIS', args:1, argName:'Lead DIS file', type: String, 'Lead DIS file')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def maxEvents = -1;
if(options.M) maxEvents = options.M;

def outFile = "eg2Proton_AnaTree_MR_AllTgts_Hists.hipo";
if(options.o) outFile = options.o;

def inCarbon = "ntuple_C_Npos.hipo";
if(options.tC) inCarbon = options.tC;

def inIron = "ntuple_Fe_Npos.hipo";
if(options.tFe) inIron = options.tFe;

def inLead = "ntuple_Pb_Npos.hipo";
if(options.tPb) inLead = options.tPb;

def inCarbonDIS = "ntuple_C_DIS.hipo";
if(options.dC) inCarbonDIS = options.dC;

def inIronDIS = "ntuple_Fe_DIS.hipo";
if(options.dFe) inIronDIS = options.dFe;

def inLeadDIS = "ntuple_Pb_DIS.hipo";
if(options.dPb) inLeadDIS = options.dPb;

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

String[] dirMR = ["/Carbon_MR","/Iron_MR","/Lead_MR"];
String[] DirLabel = ["/Carbon_LD2","/Carbon","/Iron_LD2","/Iron","/Lead_LD2","/Lead"];
String[] DirLabelDIS = ["/Carbon_DIS","/Iron_DIS","/Lead_DIS"];

TDirectory dir = new TDirectory();

//**********************
// Analyzing DIS data
//**********************
YieldsForDIS myDISC = new YieldsForDIS(inCarbonDIS);
YieldsForDIS myDISFe = new YieldsForDIS(inIronDIS);
YieldsForDIS myDISPb = new YieldsForDIS(inLeadDIS);
H1F[][] h1_ratDIS = new H1F[solidTgt.size()][Var.size()];

solidTgt.eachWithIndex{nSolid,iSolid->
  if(iSolid==0) myDISC.createHistogramRatios(iSolid);
  if(iSolid==1) myDISFe.createHistogramRatios(iSolid);
  if(iSolid==2) myDISPb.createHistogramRatios(iSolid);
  dir.mkdir(DirLabelDIS[iSolid]);
  dir.cd(DirLabelDIS[iSolid]);
  Var.eachWithIndex { nVar, iVar->
    if(iSolid==0) h1_ratDIS[iSolid][iVar] = myDISC.getHistogramRatios(iVar);
    if(iSolid==1) h1_ratDIS[iSolid][iVar] = myDISFe.getHistogramRatios(iVar);
    if(iSolid==2) h1_ratDIS[iSolid][iVar] = myDISPb.getHistogramRatios(iVar);
    dir.addDataSet(h1_ratDIS[iSolid][iVar]); // add to the histogram file
  }
}

//**********************
// Analyzing Target data
//**********************
YieldsForMR myYldsC = new YieldsForMR(inCarbon);
YieldsForMR myYldsFe = new YieldsForMR(inIron);
YieldsForMR myYldsPb = new YieldsForMR(inLead);
H1F[][][] h1_Ylds = new H1F[solidTgt.size()][Var.size()][TgtLabel.size()];
H1F[][] h1_mrTgt = new H1F[solidTgt.size()][Var.size()];
GraphErrors[][] gr_mrTgt = new GraphErrors[solidTgt.size()][Var.size()];
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

  dir.mkdir(dirMR[iSolid]);
  dir.cd(dirMR[iSolid]);
  Var.eachWithIndex{nVar, iVar->
    h1_mrTgt[iSolid][iVar] = H1F.divide(h1_Ylds[iSolid][iVar][1],h1_Ylds[iSolid][iVar][0]);
    h1_mrTgt[iSolid][iVar].setName("h1_mr" + nSolid + "_" + nVar);
    h1_mrTgt[iSolid][iVar].setFillColor(GREEN);
//    h1_mrTgt[iSolid][iVar].divide(1.0/normDIS[iSolid]);
    h1_mrTgt[iSolid][iVar].divide(h1_ratDIS[iSolid][iVar]);
    gr_mrTgt[iSolid][iVar] = h1_mrTgt[iSolid][iVar].getGraph();
    gr_mrTgt[iSolid][iVar].setName("gr_mr" + nSolid + "_" + nVar);
    gr_mrTgt[iSolid][iVar].setTitle("eg2 - " + nSolid + "/D2");
    gr_mrTgt[iSolid][iVar].setTitleX(xLabel[iVar]);
    gr_mrTgt[iSolid][iVar].setTitleY("R^p");
    gr_mrTgt[iSolid][iVar].setMarkerColor(iSolid+1);
    gr_mrTgt[iSolid][iVar].setLineColor(iSolid+1);
    gr_mrTgt[iSolid][iVar].setMarkerSize(3);
    dir.addDataSet(gr_mrTgt[iSolid][iVar]); // add to the histogram file
  }
}

TCanvas[] can = new TCanvas[Var.size()];
int c_title_size = 24;

Var.eachWithIndex{nVar, iVar->
  String canName = "c" + iVar;
  can[iVar] = new TCanvas(canName,600,600);
  can[iVar].cd(0);
  can[iVar].getPad().setTitleFontSize(c_title_size);
  solidTgt.eachWithIndex{nSolid,iSolid->
    if(iSolid==0){
      can[iVar].draw(gr_mrTgt[iSolid][iVar]);
    }else{
      can[iVar].draw(gr_mrTgt[iSolid][iVar],"same");
    }
  }
}

TCanvas c1 = new TCanvas("c1",600,600);
c1.cd(0);
c1.getPad().setTitleFontSize(c_title_size);
c1.draw(cdis);

dir.writeFile(outFile); // write the histograms to the file

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen
