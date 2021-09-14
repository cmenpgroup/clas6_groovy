import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

import eg2AnaTree.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;

def cli = new CliBuilder(usage:'eg2Proton_MR_jawhists.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.o(longOpt:'output', args:1, argName:'Histogram output file', type: String, 'Output file name')
cli.s(longOpt:'solid', args:1, argName:'Solid Target', type: String, 'Solid Target (C, Fe, Pb)')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def outFile = "eg2Proton_MR_jawHists.hipo";
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
  can[iVar] = new TCanvas(canName,1200,600);
  can[iVar].divide(3,1);
  DirLabel.eachWithIndex{nDir, iDir->
    outDir.cd(nDir);
    can[iVar].cd(iDir);
    can[iVar].getPad().setTitleFontSize(c_title_size);
    if(iDir<2){
      hname = "hYlds_" + TgtLabel[iDir] + "_" + nVar;
      h1_nProton[iVar][iDir]= dir.getObject("jaw/",jawHist[iDir][iVar]);
      h1_nProton[iVar][iDir].setName(hname);
      h1_nProton[iVar][iDir].setTitleX(xLabel[iVar]);
      h1_nProton[iVar][iDir].setTitleY("Counts");
      can[iVar].draw(h1_nProton[iVar][iDir]);
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
      can[iVar].draw(gr_mrProton[iVar]);
      outDir.addDataSet(gr_mrProton[iVar]); // add to the histogram file
    }
    outDir.cd();    
  }
}

outDir.writeFile(outFile); // write the histograms to the file
