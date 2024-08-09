import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*;
import org.jlab.groot.base.GStyle
//---- imports for PHYSICS library
//import org.jlab.jnp.physics.*;
import org.jlab.clas.physics.*;
//---- imports for PDG library
//import org.jlab.jnp.pdg.PhysicsConstants;
import org.jlab.clas.pdg.PhysicsConstants;
import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.pdg.PDGParticle;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

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

OptionParser p = new OptionParser("eg2Proton_AnaTree_MR_OneTgt.groovy");

int maxEvents = -1;
p.addOption("-M", Integer.toString(maxEvents), "Number of events to analyze.");
String outFile = "eg2Proton_AnaTree_MR_OneTgt_Hists.hipo";
p.addOption("-o", outFile, "Output file name");
String userTgt = "C";
p.addOption("-s", userTgt, "Solid Target (C, Fe, Pb)");
//String userSigmaCut = "2.0";
//p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (1.0, 1.5, 2.0, 2.5, 3.0)");
//int bGraph = 1;
//p.addOption("-g",Integer.toString(bGraph), "Graph monitor histograms. (0=quiet)");

p.parse(args);
maxEvents = p.getOption("-M").intValue();
outFile = p.getOption("-o").stringValue();
userTgt = p.getOption("-s").stringValue();
//userSigmaCut = p.getOption("-c").stringValue();
//bGraph = p.getOption("-g").intValue();

String fileName;
if(p.getInputList().size()==1){
    fileName = p.getInputList().get(0);
}else{
    System.out.println("*** Wrong number of inputs.  Only one input for the path to the text files. ***")
    p.printUsage();
    System.exit(0);
}

//def cli = new CliBuilder(usage:'eg2Proton_AnaTree.groovy [options] infile1 infile2 ...')
//cli.h(longOpt:'help', 'Print this message.')
//cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
//cli.o(longOpt:'output', args:1, argName:'Histogram output file', type: String, 'Output file name')
//cli.s(longOpt:'solid', args:1, argName:'Solid Target', type: String, 'Solid Target (C, Fe, Pb)')
//
//def options = cli.parse(args);
//if (!options) return;
//if (options.h){ cli.usage(); return; }
//
//def maxEvents = -1;
//if(options.M) maxEvents = options.M;
//
//def outFile = "eg2Proton_AnaTree_MR_OneTgt_Hists.hipo";
//if(options.o) outFile = options.o;
//
//String userTgt = "C";
//if(options.s) userTgt = options.s;
//
//def extraArguments = options.arguments()
//if (extraArguments.isEmpty()){
//  println "No input file!";
//  cli.usage();
//  return;
//}
//
//String fileName;
//extraArguments.each { infile ->
//  fileName = infile;
//}

long st = System.currentTimeMillis(); // start time

HistInfo myHI = new HistInfo();
YieldsForMR myMR = new YieldsForMR(fileName);
myMR.setMaxEvents(maxEvents);

TDirectory dir = new TDirectory();

List<String> Var = myHI.getVariables();
List<String> xLabel = myHI.getXlabel();
List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();

int indexTgt = solidTgt.indexOf(userTgt);
if(indexTgt==-1){
  println "Target " + userTgt + " is unavailable! Please choose one of the following:";
  println solidTgt;
  return;
}

String[] DirLabel = ["/LD2","/Solid","/multiplicity"];
H1F[][] h1_nProton = new H1F[Var.size()][TgtLabel.size()];

TgtLabel.eachWithIndex{nTgt,iTgt->
  myMR.createHistograms(iTgt);
  dir.mkdir(DirLabel[iTgt]);
  dir.cd(DirLabel[iTgt]);
  Var.eachWithIndex { nVar, iVar->
    h1_nProton[iVar][iTgt] = myMR.getHistogram(iVar);
    dir.addDataSet(h1_nProton[iVar][iTgt]); // add to the histogram file
  }
}

dir.mkdir(DirLabel[2]);
dir.cd(DirLabel[2]);

TCanvas[] can = new TCanvas[Var.size()];
int c_title_size = 24;

H1F[] h1_mrProton = new H1F[Var.size()];
GraphErrors[] gr_mrProton = new GraphErrors[Var.size()];
Var.eachWithIndex{nVar, iVar->
  String canName = "c" + iVar;
  can[iVar] = new TCanvas(canName,600,600);
  can[iVar].cd(0);
  can[iVar].getPad().setTitleFontSize(c_title_size);
  h1_mrProton[iVar] = H1F.divide(h1_nProton[iVar][1],h1_nProton[iVar][0]);
  h1_mrProton[iVar].setName("h1_mrProton_" + nVar);
  h1_mrProton[iVar].setFillColor(GREEN);
  gr_mrProton[iVar] = h1_mrProton[iVar].getGraph();
  gr_mrProton[iVar].setName("gr_mrProton_" + nVar);
  gr_mrProton[iVar].setTitle("eg2 - " + solidTgt[indexTgt] + "/D2");
  gr_mrProton[iVar].setTitleX(xLabel[iVar]);
  gr_mrProton[iVar].setTitleY("R^p");
  gr_mrProton[iVar].setMarkerColor(3);
  gr_mrProton[iVar].setLineColor(3);
  gr_mrProton[iVar].setMarkerSize(3);
  can[iVar].draw(gr_mrProton[iVar],"line");
  dir.addDataSet(gr_mrProton[iVar]); // add to the histogram file
}

dir.writeFile(outFile); // write the histograms to the file

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen

System.exit(0);
