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

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;

OptionParser p = new OptionParser("eg2Proton_MR_jawHists.groovy");

String outFile = "eg2Proton_MR_jawHists.hipo";
p.addOption("-o", outFile, "Output file name");
String userTgt = "C";
p.addOption("-s", userTgt, "Solid Target (C, Fe, Pb)");
int bGraph = 1;
p.addOption("-g",Integer.toString(bGraph), "Graph monitor histograms. (0=quiet)");

p.parse(args);
outFile = p.getOption("-o").stringValue();
userTgt = p.getOption("-s").stringValue();
bGraph = p.getOption("-g").intValue();

String fileName;
if(p.getInputList().size()==1){
    fileName = p.getInputList().get(0);
}else{
    System.out.println("*** Wrong number of inputs.  Only one input file. ***")
    p.printUsage();
    System.exit(0);
}

HistInfo myHI = new HistInfo();
List<String> Var = myHI.getVariables();
List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<String> xLabel = myHI.getXlabel();

TDirectory dir = new TDirectory();
dir.readFile(fileName);

String[] DirLabel = ["LD2/","Solid/","multiplicity/"];
H1F[][] h1_nProton = new H1F[Var.size()][TgtLabel.size()];
GraphErrors[] gr_mrProton = new GraphErrors[Var.size()];
H1F[] h1_mrProton = new H1F[Var.size()];
TCanvas[] can = new TCanvas[Var.size()];
int c_title_size = 24;

String[][] jawHist = [["h300","h400","h100","h500","h200","h220","h600"],["h310","h410","h110","h510","h210","h230","h610"]];

int indexTgt = solidTgt.indexOf(userTgt);
if(indexTgt==-1){
  println "Target " + userTgt + " is unavailable! Please choose one of the following:";
  println solidTgt;
  return;
}

TDirectory outDir = new TDirectory();
DirLabel.each {nDir ->
  outDir.mkdir(nDir);
}

Var.eachWithIndex{nVar, iVar->
  String canName = "c" + iVar;
  if(bGraph){
    can[iVar] = new TCanvas(canName,1200,600);
    can[iVar].divide(3,1);
  }
  DirLabel.eachWithIndex{nDir, iDir->
    outDir.cd(nDir);
    if(bGraph){
      can[iVar].cd(iDir);
      can[iVar].getPad().setTitleFontSize(c_title_size);
    }
    if(iDir<2){
      hname = "hYlds_" + TgtLabel[iDir] + "_" + nVar;
      h1_nProton[iVar][iDir]= dir.getObject("jaw/",jawHist[iDir][iVar]);
      h1_nProton[iVar][iDir].setName(hname);
      h1_nProton[iVar][iDir].setTitleX(xLabel[iVar]);
      h1_nProton[iVar][iDir].setTitleY("Counts");
      if(bGraph) can[iVar].draw(h1_nProton[iVar][iDir]);
      outDir.addDataSet(h1_nProton[iVar][iDir]); // add to the histogram file
    }else{
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
      if(bGraph) can[iVar].draw(gr_mrProton[iVar]);
      outDir.addDataSet(gr_mrProton[iVar]); // add to the histogram file
    }
    outDir.cd();
  }
}

outDir.writeFile(outFile); // write the histograms to the file
