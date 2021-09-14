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

def cli = new CliBuilder(usage:'eg2Proton_MR3zh_jawhists.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.o(longOpt:'output', args:1, argName:'Histogram output file', type: String, 'Output file name')
cli.s(longOpt:'solid', args:1, argName:'Solid Target', type: String, 'Solid Target (C, Fe, Pb)')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

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

HistInfo myHI = new HistInfo();
List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();

YieldsForMR3zh myMR = new YieldsForMR3zh();
List<String> Q2Cuts = myMR.getQ2Cuts();
List<String> nuCuts = myMR.getNuCuts();

TDirectory dir = new TDirectory();
dir.readFile(fileName);

String[] DirLabel = ["LD2/","Solid/","multiplicity/"];
H1F[][][] h1_nProton = new H1F[Q2Cuts.size()][nuCuts.size()][TgtLabel.size()];
H1F[][] h1_mrProton = new H1F[Q2Cuts.size()][nuCuts.size()];
GraphErrors[][] gr_mrProton = new GraphErrors[Q2Cuts.size()][nuCuts.size()];

int canCount = 0;
int c_title_size = 24;
TCanvas[][] can = new TCanvas[Q2Cuts.size()][nuCuts.size()];

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

Q2Cuts.eachWithIndex { nQ2, iQ2->
  nuCuts.eachWithIndex { nNu, iNu->
    String canName = "c" + iQ2 + iNu;
    can[iQ2][iNu] = new TCanvas(canName,1200,600);
    can[iQ2][iNu].divide(3,1);
    DirLabel.eachWithIndex{nDir, iDir->
      outDir.cd(nDir);
      can[iQ2][iNu].cd(iDir);
      can[iQ2][iNu].getPad().setTitleFontSize(c_title_size);
      if(iDir<2){
        hname = "hYlds_" + TgtLabel[iDir] + "_zh_" + iQ2 + iNu;
        String jawHist = "h1" + canCount + iDir;
        println jawHist;
        h1_nProton[iQ2][iNu][iDir]= dir.getObject("jaw/",jawHist);
        h1_nProton[iQ2][iNu][iDir].setName(hname);
        h1_nProton[iQ2][iNu][iDir].setTitleX("z_h");
        h1_nProton[iQ2][iNu][iDir].setTitleY("Counts");
        can[iQ2][iNu].draw(h1_nProton[iQ2][iNu][iDir]);
        outDir.addDataSet(h1_nProton[iQ2][iNu][iDir]); // add to the histogram file
      }else{
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
        can[iQ2][iNu].draw(gr_mrProton[iQ2][iNu],"line");
        outDir.addDataSet(gr_mrProton[iQ2][iNu]); // add to the histogram file
      }
      outDir.cd();      
    }
    canCount++;
  }
}

outDir.writeFile(outFile); // write the histograms to the file
