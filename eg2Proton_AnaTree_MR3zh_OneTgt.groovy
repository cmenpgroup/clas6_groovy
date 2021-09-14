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

def cli = new CliBuilder(usage:'eg2Proton_AnaTree_MR3zh_OneTgt.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
cli.o(longOpt:'output', args:1, argName:'Histogram output file', type: String, 'Output file name')
cli.s(longOpt:'solid', args:1, argName:'Solid Target', type: String, 'Solid Target (C, Fe, Pb)')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def maxEvents = -1;
if(options.M) maxEvents = options.M;

def outFile = "eg2Proton_AnaTree_MR3zh_OneTgt_Hists.hipo";
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
YieldsForMR3zh myMR = new YieldsForMR3zh(fileName);
myMR.setMaxEvents(maxEvents);

List<String> Q2Cuts = myMR.getQ2Cuts();
List<String> nuCuts = myMR.getNuCuts();

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
H1F[][][] h1_nProton = new H1F[Q2Cuts.size()][nuCuts.size()][TgtLabel.size()];

TgtLabel.eachWithIndex{nTgt,iTgt->
  myMR.createHistograms(iTgt);
  dir.mkdir(DirLabel[iTgt]);
  dir.cd(DirLabel[iTgt]);

  Q2Cuts.eachWithIndex { nQ2, iQ2->
    nuCuts.eachWithIndex { nNu, iNu->
      h1_nProton[iQ2][iNu][iTgt] = myMR.getHistogram(iQ2,iNu);
      dir.addDataSet(h1_nProton[iQ2][iNu][iTgt]); // add to the histogram file
    }
  }
}

dir.mkdir(DirLabel[2]);
dir.cd(DirLabel[2]);

int canCount = 0;
int c_title_size = 24;
TCanvas can = new TCanvas("can",900,900);
can.divide(Q2Cuts.size(),nuCuts.size())

H1F[][] h1_mrProton = new H1F[Q2Cuts.size()][nuCuts.size()];
GraphErrors[][] gr_mrProton = new GraphErrors[Q2Cuts.size()][nuCuts.size()];

Q2Cuts.eachWithIndex { nQ2, iQ2->
  nuCuts.eachWithIndex { nNu, iNu->
    can.cd(canCount);
    can.getPad().setTitleFontSize(c_title_size);
    h1_mrProton[iQ2][iNu] = H1F.divide(h1_nProton[iQ2][iNu][1],h1_nProton[iQ2][iNu][0]);
    h1_mrProton[iQ2][iNu].setName("h1_mrProton_zh_" + iQ2 + iNu);
    h1_mrProton[iQ2][iNu].setFillColor(GREEN);
    gr_mrProton[iQ2][iNu] = h1_mrProton[iQ2][iNu].getGraph();
    gr_mrProton[iQ2][iNu].setName("gr_mrProton_zh_" + iQ2 + iNu);
    gr_mrProton[iQ2][iNu].setTitle("eg2 - " + solidTgt[indexTgt] + "/D2");
    gr_mrProton[iQ2][iNu].setTitleX("z_h");
    gr_mrProton[iQ2][iNu].setTitleY("R^p");
    gr_mrProton[iQ2][iNu].setMarkerColor(3);
    gr_mrProton[iQ2][iNu].setLineColor(3);
    gr_mrProton[iQ2][iNu].setMarkerSize(3);
    can.draw(gr_mrProton[iQ2][iNu],"line");
    dir.addDataSet(gr_mrProton[iQ2][iNu]); // add to the histogram file
    canCount++;
  }
}

dir.writeFile(outFile); // write the histograms to the file

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen
