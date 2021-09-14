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

double x, y, err;
String[] str;

def cli = new CliBuilder(usage:'eg2Proton_MR2pT2_corr.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.o(longOpt:'output', args:1, argName:'Histogram output file', type: String, 'Output file name')
cli.s(longOpt:'solid', args:1, argName:'Solid Target', type: String, 'Solid Target (C, Fe, Pb)')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def outFile = "eg2Proton_MR2pT2_corr_hists.hipo";
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
int indexTgt = 0;

YieldsForMR2pT2 myMR = new YieldsForMR2pT2();
List<Double> xlo = myMR.getXlo();
List<Double> xhi = myMR.getXhi();
List<Integer> nbins = myMR.getNbins();
List<String> zhCuts = myMR.getZhCuts();

TDirectory dir = new TDirectory();
dir.readFile(fileName);

TDirectory outDir = new TDirectory();
outDir.mkdir("MR2pT2")
outDir.cd("MR2pT2");

String[] DirLabel = ["LD2/","Solid/","multiplicity/"];
GraphErrors[] gr_mrProton = new GraphErrors[zhCuts.size()];

int c_title_size = 22;

TCanvas canAcc = new TCanvas("canAcc",900,900);
canAcc.divide(3,3);

TCanvas canComp = new TCanvas("canComp",900,900);
canComp.divide(3,3);

zhCuts.eachWithIndex { nZh, iZh->
  DataVector accX = new DataVector();
  DataVector accY = new DataVector();
  DataVector accYerr = new DataVector();
  String grAcc = "gr_acc_" + iZh;
  GraphErrors gr_acc = new GraphErrors(grAcc);

  int jZh = iZh+1;
  String accPath = "/Users/wood5/jlab/clas12/eg2_proton_acceptance/MR2pT2/";
  String accFile = accPath + "acc_ratio_hists_Pt2Zh_" + userTgt +"_" + jZh +".txt";
  println "Analyzing " + accFile;
  new File(accFile).eachLine { line ->
    str = line.split('\t');
    str.eachWithIndex{ val, ival->
      switch(ival){
        case 0: x = Double.parseDouble(val); break;
        case 1: y = Double.parseDouble(val); break;
        case 2: err = Double.parseDouble(val); break;
        default: break;
      }
    }
    if(x>xlo[iZh] && x<=xhi[iZh]) gr_acc.addPoint(x,y,0.0,err);
  }
  accX = gr_acc.getVectorX();
  accY = gr_acc.getVectorY();
  for(int j=0; j<gr_acc.getDataSize(0);j++){
    accYerr.add(gr_acc.getDataEY(j));
  }

  String grname = "gr_mrProton_pT2_" + iZh;
  gr_mrProton[iZh] = dir.getObject(DirLabel[2],grname);
  System.out.println(grname + " " + gr_mrProton[iZh].getDataSize(0) + " bins");

  String grcorr = "gr_mrProtonCorr_pT2_" + iZh;
  GraphErrors gr_mrCorr = new GraphErrors(grcorr);

  DataVector dataX = new DataVector();
  DataVector dataY = new DataVector();
  dataX = gr_mrProton[iZh].getVectorX();
  dataY = gr_mrProton[iZh].getVectorY();
  for(int i=0; i<gr_mrProton[iZh].getDataSize(0);i++){
    corrMR = dataY.getValue(i)*accY.getValue(i);
    if(gr_mrProton[iZh].getDataEY(i)>0.0 && accYerr.getValue(i)>0.0){
      errMR = Math.abs(corrMR)*Math.sqrt((gr_mrProton[iZh].getDataEY(i)/dataY.getValue(i))**2 + (accYerr.getValue(i)/accY.getValue(i))**2);
    }else{
      errMR = 0.0;
    }
    gr_mrCorr.addPoint(dataX.getValue(i),corrMR,0.0,errMR);
  }

  canAcc.cd(iZh);
  canAcc.getPad().setTitleFontSize(c_title_size);
  gr_acc.setTitleX(myMR.getXlabel());
  gr_acc.setTitleY("Acceptance Ratio");
  gr_acc.setMarkerColor(2);
  gr_acc.setLineColor(2);
  gr_acc.setMarkerSize(5);
  canAcc.draw(gr_acc);
  outDir.addDataSet(gr_acc); // add to the histogram file

  canComp.cd(iZh);
  canComp.getPad().setTitleFontSize(c_title_size);
  gr_mrProton[iZh].setMarkerSize(5);
  canComp.draw(gr_mrProton[iZh]);
  outDir.addDataSet(gr_mrProton[iZh]); // add to the histogram file

  gr_mrCorr.setTitleX(myMR.getXlabel());
  gr_mrCorr.setTitleY("R^p");
  gr_mrCorr.setMarkerColor(4);
  gr_mrCorr.setLineColor(4);
  gr_mrCorr.setMarkerSize(5);
  canComp.draw(gr_mrCorr,"same");
  outDir.addDataSet(gr_mrCorr); // add to the histogram file
}

outDir.writeFile(outFile);
