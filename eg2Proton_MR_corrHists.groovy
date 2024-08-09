import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.base.GStyle
//---- imports for PHYSICS library
import org.jlab.clas.physics.*;
//---- imports for PDG library
import org.jlab.clas.pdg.PhysicsConstants;
import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.pdg.PDGParticle;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

Double corrMR, errMR;
Double x, y, err, ytemp;
Double relErrSq;
String[] str;
double[] normCorr = [1.11978 ,  1.0609 ,  2.19708];

OptionParser p = new OptionParser("eg2Proton_MR_corrHists.groovy");

String outFile = "eg2Proton_MR_corrHists.hipo";
p.addOption("-o", outFile, "Output file name");
String userTgt = "C";
p.addOption("-s", userTgt, "Solid Target (C, Fe, Pb)");
String userSigmaCut = "2.0";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (1.0, 1.5, 2.0, 2.5, 3.0)");
int bGraph = 1;
p.addOption("-g",Integer.toString(bGraph), "Graph monitor histograms. (0=quiet)");

p.parse(args);
outFile = p.getOption("-o").stringValue();
userTgt = p.getOption("-s").stringValue();
userSigmaCut = p.getOption("-c").stringValue();
bGraph = p.getOption("-g").intValue();

String fileName;
if(p.getInputList().size()==1){
    fileName = p.getInputList().get(0);
}else{
    System.out.println("*** Wrong number of inputs.  Only one input file. ***")
    p.printUsage();
    System.exit(0);
}

TDirectory dirData = new TDirectory();
dirData.readFile(fileName);

HistInfo myHI = new HistInfo();
List<String> Var = myHI.getVariables();
List<String> xLabel = myHI.getXlabel();
List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<Double> xlo = myHI.getXlo();
List<Double> xhi = myHI.getXhi();
List<Integer> nbins = myHI.getNbins();

int indexTgt = solidTgt.indexOf(userTgt);
if(indexTgt==-1){
  println "Target " + userTgt + " is unavailable! Please choose one of the following:";
  println solidTgt;
  return;
}

String fileDIS = "eg2DIS_AnaTree_" + userTgt + "_Hists.hipo";
TDirectory dirNorm = new TDirectory();
dirNorm.readFile(fileDIS);

TDirectory dirAcc = new TDirectory();
dirAcc.readFile("acc_ratio_hists/MR/acc_ratio_hists_MR_allTgts_sig" + userSigmaCut + ".hipo");

TDirectory dir = new TDirectory();

int c1_title_size = 22;
TCanvas[] can = new TCanvas[Var.size()];

GraphErrors[] gr_mrProton = new GraphErrors[Var.size()];
GraphErrors[] gr_mrCorr = new GraphErrors[Var.size()];
GraphErrors[] gr_mrNorm = new GraphErrors[Var.size()];
GraphErrors[] gr_acc = new GraphErrors[Var.size()];

Var.eachWithIndex { nVar, iVar->
  String gr_eNorm = "gr_rDIS_" + nVar;
  gr_mrNorm[iVar] = dirNorm.getObject("multiplicity/",gr_eNorm);
  System.out.println(gr_eNorm + " " + gr_mrNorm[iVar].getDataSize(0) + " bins");

  String grAcc = "gr_rat" + userTgt + "_" + nVar;
  gr_acc[iVar]= dirAcc.getObject(nVar,grAcc);
  System.out.println(grAcc + " " + gr_acc[iVar].getDataSize(0) + " bins");

  String grname = "gr_mrProton_" + nVar;
  gr_mrProton[iVar] = dirData.getObject("multiplicity/",grname);
  System.out.println(grname + " " + gr_mrProton[iVar].getDataSize(0) + " bins");

  String grCorr = "gr_mrProtonCorr_" + nVar;
  gr_mrCorr[iVar] = new GraphErrors();
  gr_mrCorr[iVar] = gr_mrProton[iVar].divide(gr_acc[iVar]);
  gr_mrCorr[iVar].setName(grCorr);

  dir.mkdir(nVar);
  dir.cd(nVar);

  gr_acc[iVar].setTitleX(xLabel[iVar]);
  gr_acc[iVar].setTitleY("Acceptance Ratio");
  gr_acc[iVar].setMarkerColor(2);
  gr_acc[iVar].setLineColor(2);
  gr_acc[iVar].setMarkerSize(5);
  dir.addDataSet(gr_acc[iVar]); // add to the histogram file

  gr_mrProton[iVar].setMarkerSize(5);
  dir.addDataSet(gr_mrProton[iVar]); // add to the histogram file

  gr_mrCorr[iVar].setTitleX(xLabel[iVar]);
  gr_mrCorr[iVar].setTitleY("R^p");
  gr_mrCorr[iVar].setMarkerColor(4);
  gr_mrCorr[iVar].setLineColor(4);
  gr_mrCorr[iVar].setMarkerSize(5);
  dir.addDataSet(gr_mrCorr[iVar]); // add to the histogram file

  if(bGraph){
    String cname = "can" + iVar;
    can[iVar] = new TCanvas(cname,1000,500);
    can[iVar].divide(2,1);
    can[iVar].cd(0);
    can[iVar].getPad().setTitleFontSize(c1_title_size);
    can[iVar].draw(gr_acc[iVar]);

    can[iVar].cd(1);
    can[iVar].getPad().setTitleFontSize(c1_title_size);
    can[iVar].draw(gr_mrProton[iVar]);
    can[iVar].draw(gr_mrCorr[iVar],"same");
  }
  dir.cd(); // return to the top directory
}

dir.writeFile(outFile);
