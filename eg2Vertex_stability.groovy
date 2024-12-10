import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.clas.physics.*;
import org.jlab.clas.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*; // new import for ntuples
import org.jlab.groot.math.*;
import org.jlab.groot.fitter.DataFitter;

import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.eg2Runs;

GStyle.getAxisAttributesX().setTitleFontSize(24);
GStyle.getAxisAttributesY().setTitleFontSize(24);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

OptionParser p = new OptionParser("eg2Vertex_stability.groovy");

p.addOption("-M", "-1", "Max. Events");
p.addOption("-o", "eg2Vertex_stability_Hists.hipo", "output file name");
String userTgt = "C";
p.addOption("-s", userTgt, "Solid Target (C, Fe, Pb)");

p.parse(args);
int maxEvents = p.getOption("-M").intValue();
String outFile = p.getOption("-o").stringValue();
userTgt = p.getOption("-s").stringValue();

long st = System.currentTimeMillis(); // start time

eg2Runs myRuns = new eg2Runs();  // create the eg2 runs object
List<Integer> myRunList = myRuns.getRunList(userTgt);
System.out.println("Target: " + userTgt + ", Number of runs: " + myRunList.size());
//System.exit(0);

String[] fitName = ["fgaus_electron","fgaus_proton","fgaus_dVz"];
F1D[] fgaus = new F1D[fitName.size()];

// generic fit ranges
double[] fitLo = [-26.0,-26.0,-2.0];
double[] fitHi = [-23.5,-23.5,2.0];
switch(userTgt){
  case "C":  // Carbon fit ranges
    fitLo = [-25.5,-25.5,-0.9];
    fitHi = [-24.0,-24.0,0.9];
    break;
  case "Fe": // Iron fit ranges
    fitLo = [-25.6,-25.6,-0.9];
    fitHi = [-24.4,-24.4,0.9];
    break;
  case "Pb":  // Lead fit ranges
    fitLo = [-25.6,-25.5,-0.9];
    fitHi = [-24.4,-24.3,0.9];
    break;
  default:
    System.out.println("eg2Vertex_stability.groovy: Unknown target " + userTgt);
    System.exit(-1);
    break;
}

String hTitle = "Experiment: eg2";
String[] xLabel = ["Vertex z (cm)","Vertex z (cm)","#Deltaz (cm)"];
String[] suffix = ["electron","proton","dVz"];
String[] histsVz = ["hVz-electron","hVz-proton","hdVz"];
int[] xBins = [400,400,400];
double[] xLo = [-33.0,-33.0,-5.0];
double[] xHi = [-22.0,-22.0,5.0];
H1F[] hVz = new H1F[histsVz.size()];
H1F[] hVz_all = new H1F[histsVz.size()];
GraphErrors[] grMeanVsRun = new GraphErrors[histsVz.size()];
GraphErrors[] grSigmaVsRun = new GraphErrors[histsVz.size()];
H1F[] hMean = new H1F[histsVz.size()];
double[] xLo_Mean = [-26.0,-26.0,-1.0];
double[] xHi_Mean = [-24.0,-24.0,1.0];
H1F[] hSigma = new H1F[histsVz.size()];
histsVz.eachWithIndex {hname, ih ->
  hVz_all[ih] = new H1F(histsVz[ih] + "_all",xLabel[ih],"Counts",xBins[ih],xLo[ih],xHi[ih]);
  hVz_all[ih].setTitle(hTitle);

  grMeanVsRun[ih] = new GraphErrors("grMeanVsRun" + ih);
  grSigmaVsRun[ih] = new GraphErrors("grSigmaVsRun" + ih);

  hMean[ih] = new H1F("hMean-" + suffix[ih],"Mean","Counts",200,xLo_Mean[ih],xHi_Mean[ih]);
  hMean[ih].setTitle(hTitle + ": " + suffix[ih]);

  hSigma[ih] = new H1F("hSigma-" + suffix[ih],"Sigma","Counts",200,0.0,2.0);
  hSigma[ih].setTitle(hTitle + ": " + suffix[ih]);
}

TDirectory dir = new TDirectory();
String[] dirLabel = ["/Electron","/Proton","/dVz"];
dirLabel.each { val ->
  dir.mkdir(val);
}

String inPath = "Npos_Yoda/" + myRuns.getTargetName(userTgt) + "/ntuples_protonID_sysErr/std/";
myRunList.each { runno ->
  String inFile = inPath + "ntuple_" + runno + ".pass2.All.filtered_Npos.hipo";
  TreeHipo tree = new TreeHipo(inFile,"protonTree::tree"); // the writer adds ::tree to the name of the tree
  int entries = tree.getEntries();   // get the number of events in the file
  System.out.println(" ENTRIES = " + entries);  // print the number of events in the file to the screen

  // to analyze the entire file, set max Events less than zero
  if(maxEvents < 0){
    maxEvents = entries;
  }

  // Select the target in the cuts
  // iTgt = 0 (deuterium)
  // iTgt = 1 (solid - C, Fe, or Pb)
  List vec_vert = tree.getDataVectors("eVx:eVy:eVz:ePhi:pVx:pVy:pVz:pPhi","eVy<1.4&&eVy>-1.4&&pVy<1.4&&pVy>-1.4",maxEvents);

  DataVector eVz = new DataVector();
  eVz.copy(vec_vert.get(2));
  String histName = histsVz[0] + "_" + runno;
  hVz[0] = new H1F().create(histName,xBins[0],eVz,xLo[0],xHi[0]);

  DataVector pVz = new DataVector();
  pVz.copy(vec_vert.get(6));
  histName = histsVz[1] + "_" + runno;
  hVz[1] = new H1F().create(histName,xBins[1],pVz,xLo[1],xHi[1]);

  DataVector diffVz = new DataVector();
  DataVector pVz_minus = new DataVector();
  diffVz.copy(eVz);
  pVz_minus.copy(pVz);
  pVz_minus.mult(-1.0);
  diffVz.addDataVector(pVz_minus);
  histName = histsVz[2]  + "-" + runno;
  hVz[2] = new H1F().create(histName,xBins[2],diffVz,xLo[2],xHi[2]);

  histsVz.eachWithIndex { hname, ih->
    hVz[ih].setTitle(hTitle + ": run " + runno);
    hVz[ih].setTitleX(xLabel[ih]);
    hVz[ih].setTitleY("Counts");

    fgaus[ih] = new F1D(fitName[ih],"[amp]*gaus(x,[mean],[sigma])", fitLo[ih], fitHi[ih]);
    fgaus[ih].setParameter(0, hVz[ih].getMax());
    fgaus[ih].setParameter(1, 0.5*(fitLo[ih]+fitHi[ih]));
    fgaus[ih].setParameter(2, 0.1);
    DataFitter.fit(fgaus[ih], hVz[ih], "Q"); //No options uses error for sigma
    grMeanVsRun[ih].addPoint(runno,fgaus[ih].getParameter(1),0.0,0.0);
    grSigmaVsRun[ih].addPoint(runno,fgaus[ih].getParameter(2),0.0,0.0);
    hMean[ih].fill(fgaus[ih].getParameter(1));
    hSigma[ih].fill(fgaus[ih].getParameter(2));
    hVz_all[ih].add(hVz[ih]);
    dir.cd(dirLabel[ih]);
    dir.addDataSet(hVz[ih]);
  }
}

// create the canvas for the display
int can_title_size = 24;
TCanvas can = new TCanvas("can",1200,600);
can.divide(3,1);

TCanvas canMean = new TCanvas("canMean",1200,600);
canMean.divide(1,3);

TCanvas canSigma = new TCanvas("canSigma",1200,600);
canSigma.divide(1,3);

F1D[] fgaus_all = new F1D[histsVz.size()];
histsVz.eachWithIndex { hname, ih->
  dir.cd(dirLabel[ih]);
  can.cd(ih);
  can.getPad().setTitleFontSize(can_title_size);
  can.draw(hVz_all[ih]);
  fgaus_all[ih] = new F1D(fitName[ih] + "-all","[amp]*gaus(x,[mean],[sigma])", fitLo[ih], fitHi[ih]);
  fgaus_all[ih].setParameter(0, hVz_all[ih].getMax());
  fgaus_all[ih].setParameter(1, 0.5*(fitLo[ih]+fitHi[ih]));
  fgaus_all[ih].setParameter(2, 0.1);
  DataFitter.fit(fgaus_all[ih], hVz_all[ih], "Q"); //No options uses error for sigma
  fgaus_all[ih].setLineColor(32);
  fgaus_all[ih].setLineWidth(5);
  fgaus_all[ih].setLineStyle(1);
  fgaus_all[ih].setOptStat(1111);
  can.draw(fgaus_all[ih],"same");
  System.out.println(fitName[ih] + " " + fgaus_all[ih].getParameter(1) + " " + fgaus_all[ih].getParameter(2));
  dir.addDataSet(hVz_all[ih]);

  canMean.cd(ih);
  canMean.getPad().setTitleFontSize(can_title_size);
  grMeanVsRun[ih].setTitle(hTitle);
  grMeanVsRun[ih].setTitleX("Run");
  grMeanVsRun[ih].setTitleY("Mean");
  canMean.draw(grMeanVsRun[ih]);
  dir.addDataSet(grMeanVsRun[ih]);

  canSigma.cd(ih);
  canSigma.getPad().setTitleFontSize(can_title_size);
  grSigmaVsRun[ih].setTitle(hTitle);
  grSigmaVsRun[ih].setTitleX("Run");
  grSigmaVsRun[ih].setTitleY("Sigma");
  canSigma.draw(grSigmaVsRun[ih]);
  dir.addDataSet(grSigmaVsRun[ih]);

  dir.addDataSet(hMean[ih]);
  dir.addDataSet(hSigma[ih]);
}

dir.writeFile(outFile); // write the histograms to the file

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen
