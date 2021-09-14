import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*;

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

def cli = new CliBuilder(usage:'eg2Proton_AnaTree_MR2pT2_OneTgt.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
cli.o(longOpt:'output', args:1, argName:'Histogram output file', type: String, 'Output file name')
cli.s(longOpt:'solid', args:1, argName:'Solid Target', type: String, 'Solid Target (C, Fe, Pb)')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def maxEvents = -1;
if(options.M) maxEvents = options.M;

def outFile = "eg2Proton_AnaTree_MR2pT2_OneTgt_Hists.hipo";
if(options.o) outFile = options.o;

String userTgt = "C";
if(options.s) userTgt = options.s;

def extraArguments = options.arguments()
if (extraArguments.isEmpty()){
  println "No input file!";
  cli.usage();
  return;
}

String fileName;
extraArguments.each { infile ->
  fileName = infile;
}

long st = System.currentTimeMillis(); // start time

HistInfo myHI = new HistInfo();
YieldsForMR2pT2 myMR = new YieldsForMR2pT2(fileName);
myMR.setMaxEvents(maxEvents);

List<String> zhCuts = myMR.getZhCuts();

TDirectory dir = new TDirectory();

List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();

int indexTgt = solidTgt.indexOf(userTgt);
if(indexTgt==-1){
  println "Target " + userTgt + " is unavailable! Please choose one of the following:";
  println solidTgt;
  return;
}

String[] DirLabel = ["/LD2","/Solid","/multiplicity"];
H1F[][] h1_nProton = new H1F[zhCuts.size()][TgtLabel.size()];

TgtLabel.eachWithIndex{nTgt,iTgt->
  myMR.createHistograms(iTgt);
  dir.mkdir(DirLabel[iTgt]);
  dir.cd(DirLabel[iTgt]);

  zhCuts.eachWithIndex { nZh, iZh->
    h1_nProton[iZh][iTgt] = myMR.getHistogram(iZh);
    dir.addDataSet(h1_nProton[iZh][iTgt]); // add to the histogram file
  }
}

dir.mkdir(DirLabel[2]);
dir.cd(DirLabel[2]);

int c_title_size = 24;
TCanvas can = new TCanvas("can",900,900);
can.divide(3,3);

H1F[] h1_mrProton = new H1F[zhCuts.size()];
GraphErrors[] gr_mrProton = new GraphErrors[zhCuts.size()];

zhCuts.eachWithIndex { nZh, iZh->
  can.cd(iZh);
  can.getPad().setTitleFontSize(c_title_size);
  h1_mrProton[iZh] = H1F.divide(h1_nProton[iZh][1],h1_nProton[iZh][0]);
  h1_mrProton[iZh].setName("h1_mrProton_pT2_" + iZh);
  h1_mrProton[iZh].setFillColor(GREEN);
  gr_mrProton[iZh] = h1_mrProton[iZh].getGraph();
  gr_mrProton[iZh].setName("gr_mrProton_pT2_" + iZh);
  gr_mrProton[iZh].setTitle("eg2 - " + solidTgt[indexTgt] + "/D2");
  gr_mrProton[iZh].setTitleX(myMR.getXlabel());
  gr_mrProton[iZh].setTitleY("R^p");
  gr_mrProton[iZh].setMarkerColor(3);
  gr_mrProton[iZh].setLineColor(3);
  gr_mrProton[iZh].setMarkerSize(3);
  can.draw(gr_mrProton[iZh],"line");
  dir.addDataSet(gr_mrProton[iZh]); // add to the histogram file
}

dir.writeFile(outFile); // write the histograms to the file

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen
