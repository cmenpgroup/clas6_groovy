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

def cli = new CliBuilder(usage:'eg2Proton_MR2pT2_jawhists.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.o(longOpt:'output', args:1, argName:'Histogram output file', type: String, 'Output file name')
cli.s(longOpt:'solid', args:1, argName:'Solid Target', type: String, 'Solid Target (C, Fe, Pb)')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def outFile = "eg2Proton_MR2oT2_jawHists.hipo";
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
List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();

YieldsForMR2pT2 myMR = new YieldsForMR2pT2();
List<String> zhCuts = myMR.getZhCuts();

TDirectory dir = new TDirectory();
dir.readFile(fileName);

String[] DirLabel = ["LD2/","Solid/","multiplicity/"];
H1F[][] h1_nProton = new H1F[zhCuts.size()][TgtLabel.size()];
H1F[] h1_mrProton = new H1F[zhCuts.size()];
GraphErrors[] gr_mrProton = new GraphErrors[zhCuts.size()];

TCanvas[] can = new TCanvas[zhCuts.size()];
int c_title_size = 24;

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

zhCuts.eachWithIndex { nZh, iZh->
  String canName = "c" + iZh;
  can[iZh] = new TCanvas(canName,1200,600);
  can[iZh].divide(3,1);
  DirLabel.eachWithIndex{nDir, iDir->
    outDir.cd(nDir);
    can[iZh].cd(iDir);
    can[iZh].getPad().setTitleFontSize(c_title_size);
    if(iDir<2){
      hname = "hYlds_" + TgtLabel[iDir] + "_pT2_" + iZh;
      String jawHist = "h1" + iZh + iDir;
      println jawHist;
      h1_nProton[iZh][iDir]= dir.getObject("jaw/",jawHist);
      h1_nProton[iZh][iDir].setName(hname);
      h1_nProton[iZh][iDir].setTitleX("pT^2 (GeV^2)");
      h1_nProton[iZh][iDir].setTitleY("Counts");
      can[iZh].draw(h1_nProton[iZh][iDir]);
      outDir.addDataSet(h1_nProton[iZh][iDir]); // add to the histogram file
    }else{
      h1_mrProton[iZh] = H1F.divide(h1_nProton[iZh][1],h1_nProton[iZh][0]);
      h1_mrProton[iZh].setName("h1_mrProton_pT2_" + iZh);
      h1_mrProton[iZh].setFillColor(GREEN);
      gr_mrProton[iZh] = h1_mrProton[iZh].getGraph();
      gr_mrProton[iZh].setName("gr_mrProton_pT2_" + iZh);
      gr_mrProton[iZh].setTitle("eg2 - " + solidTgt[indexTgt] + "/D2");
      gr_mrProton[iZh].setTitleX("pT^2 (GeV^2)");
      gr_mrProton[iZh].setTitleY("R^p");
      gr_mrProton[iZh].setMarkerColor(3);
      gr_mrProton[iZh].setLineColor(3);
      gr_mrProton[iZh].setMarkerSize(3);
      can[iZh].draw(gr_mrProton[iZh]);
      outDir.addDataSet(gr_mrProton[iZh]); // add to the histogram file
    }
    outDir.cd();
  }
}

outDir.writeFile(outFile); // write the histograms to the file
