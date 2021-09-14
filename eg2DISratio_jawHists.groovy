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

def cli = new CliBuilder(usage:'eg2DISratio_jawhists.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.o(longOpt:'output', args:1, argName:'Histogram output file', type: String, 'Output file name')
cli.s(longOpt:'solid', args:1, argName:'Solid Target', type: String, 'Solid Target (C, Fe, Pb)')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def outFile = "eg2DISratio_jawHists.hipo";
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

String[] Var = ["q2","nu"];
String[] xLabel = ["Q^2 (GeV^2)","#nu (GeV)"];

TDirectory dir = new TDirectory();
dir.readFile(fileName);

String[] DirLabel = ["LD2/","Solid/","multiplicity/"];
H1F[][] h1_nDIS = new H1F[Var.size()][TgtLabel.size()];
GraphErrors[] gr_rDIS = new GraphErrors[Var.size()];
H1F[] h1_rDIS = new H1F[Var.size()];
TCanvas[] can = new TCanvas[Var.size()];
int c_title_size = 24;

String[][] jawHist = [["h300","h400"],["h310","h410"]];

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
      hname = "hDIS_" + TgtLabel[iDir] + "_" + nVar;
      h1_nDIS[iVar][iDir]= dir.getObject("jaw/",jawHist[iDir][iVar]);
      h1_nDIS[iVar][iDir].setName(hname);
      h1_nDIS[iVar][iDir].setTitleX(xLabel[iVar]);
      h1_nDIS[iVar][iDir].setTitleY("Counts");
      can[iVar].draw(h1_nDIS[iVar][iDir]);
      outDir.addDataSet(h1_nDIS[iVar][iDir]); // add to the histogram file
    }else{
      h1_rDIS[iVar] = H1F.divide(h1_nDIS[iVar][1],h1_nDIS[iVar][0]);
      h1_rDIS[iVar].setName("h1_rDIS_" + nVar);
      h1_rDIS[iVar].setFillColor(GREEN);
      gr_rDIS[iVar] = h1_rDIS[iVar].getGraph();
      gr_rDIS[iVar].setName("gr_rDIS_" + nVar);
      gr_rDIS[iVar].setTitle("eg2 - " + solidTgt[indexTgt] + "/D2");
      gr_rDIS[iVar].setTitleX(xLabel[iVar]);
      gr_rDIS[iVar].setTitleY("N_e(" + solidTgt[indexTgt] + ")/N_e(D)");
      gr_rDIS[iVar].setMarkerColor(3);
      gr_rDIS[iVar].setLineColor(3);
      gr_rDIS[iVar].setMarkerSize(3);
      can[iVar].draw(gr_rDIS[iVar]);
      outDir.addDataSet(gr_rDIS[iVar]); // add to the histogram file
    }
    outDir.cd();
  }
}

outDir.writeFile(outFile); // write the histograms to the file
