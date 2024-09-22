import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
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

double x, y, err;
String[] str;

int[][] nbins = [[37,28,24],[37,28,24],[34,29,23]];
double[][] xlo = [[0.3,0.25,0.225],[0.3,0.25,0.225],[0.3,0.25,0.25]];
double[][] xhi = [[1.225,0.95,0.825],[1.225,0.95,0.825],[1.15,0.975,0.825]];

//int[][] nbins = [[38,29,25],[38,29,25],[35,30,24]];
//double[][] xlo = [[0.3,0.25,0.225],[0.3,0.25,0.225],[0.3,0.25,0.25]];
//double[][] xhi = [[1.25,0.975,0.85],[1.25,0.975,0.85],[1.175,1.0,0.85]];

OptionParser p = new OptionParser("eg2Proton_MR3zh_corr.groovy");

String outFile = "eg2Proton_MR3zh_corr_hists.hipo";
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

HistInfo myHI = new HistInfo();
List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();
String[] Q2Cuts = ["q2>1.0&&q2<1.33","q2>1.33&&q2<1.76","q2>1.76&&q2<4.1"];
String[] nuCuts = ["nu>2.2&&nu<3.2","nu>3.2&&nu<3.73","nu>3.73&&nu<4.25"];
int indexTgt = 0;

TDirectory dir = new TDirectory();
dir.readFile(fileName);

TDirectory outDir = new TDirectory();
outDir.mkdir("MR3zh")
outDir.cd("MR3zh");

String[] DirLabel = ["LD2/","Solid/","multiplicity/"];
GraphErrors[][] gr_mrProton = new GraphErrors[Q2Cuts.size()][nuCuts.size()];

int iCount = 0;
int c_title_size = 22;

TCanvas canAcc;
TCanvas canComp;
if(bGraph){
  canAcc = new TCanvas("canAcc",900,900);
  canComp = new TCanvas("canComp",900,900);
  canAcc.divide(3,3);
  canComp.divide(3,3);
}

Q2Cuts.eachWithIndex { nQ2, iQ2->
  nuCuts.eachWithIndex { nNu, iNu->

    DataVector accX = new DataVector();
    DataVector accY = new DataVector();
    DataVector accYerr = new DataVector();
    String grAcc = "gr_acc_" + iQ2 + iNu;
    GraphErrors gr_acc = new GraphErrors(grAcc);

    int jQ2 = iQ2+1;
    int jNu = iNu+1;
    String accPath = "/Users/wood5/jlab/clas12/eg2_proton_acceptance/MR3zh/";
    String accFile = accPath + "acc_ratio_hists_" + userTgt +"_" + jQ2 + jNu +".txt";
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
      if(x>xlo[iQ2][iNu] && x<=xhi[iQ2][iNu]){
        gr_acc.addPoint(x,y,0.0,err);
      }
    }
    accX = gr_acc.getVectorX();
    accY = gr_acc.getVectorY();
    for(int j=0; j<gr_acc.getDataSize(0);j++){
      accYerr.add(gr_acc.getDataEY(j));
    }

    String grname = "gr_mrProton_zh_" + iQ2 + iNu;
    gr_mrProton[iQ2][iNu] = dir.getObject(DirLabel[2],grname);
    System.out.println(grname + " " + gr_mrProton[iQ2][iNu].getDataSize(0) + " bins");

    String grcorr = "gr_mrProtonCorr_" + iQ2 + iNu;
    GraphErrors gr_mrCorr = new GraphErrors(grcorr);

    DataVector dataX = new DataVector();
    DataVector dataY = new DataVector();
    dataX = gr_mrProton[iQ2][iNu].getVectorX();
    dataY = gr_mrProton[iQ2][iNu].getVectorY();
    for(int i=0; i<gr_mrProton[iQ2][iNu].getDataSize(0);i++){
      corrMR = dataY.getValue(i)/accY.getValue(i);
      if(gr_mrProton[iQ2][iNu].getDataEY(i)>0.0 && accYerr.getValue(i)>0.0){
        errMR = Math.abs(corrMR)*Math.sqrt((gr_mrProton[iQ2][iNu].getDataEY(i)/dataY.getValue(i))**2 + (accYerr.getValue(i)/accY.getValue(i))**2);
      }else{
        errMR = 0.0;
      }
      gr_mrCorr.addPoint(dataX.getValue(i),corrMR,0.0,errMR);
    }

    gr_acc.setTitleX("z_h");
    gr_acc.setTitleY("Acceptance Ratio");
    gr_acc.setMarkerColor(2);
    gr_acc.setLineColor(2);
    gr_acc.setMarkerSize(5);
    outDir.addDataSet(gr_acc); // add to the histogram file

    gr_mrProton[iQ2][iNu].setMarkerSize(5);
    outDir.addDataSet(gr_mrProton[iQ2][iNu]); // add to the histogram file

    gr_mrCorr.setTitleX("z_h");
    gr_mrCorr.setTitleY("R^p");
    gr_mrCorr.setMarkerColor(4);
    gr_mrCorr.setLineColor(4);
    gr_mrCorr.setMarkerSize(5);
    outDir.addDataSet(gr_mrCorr); // add to the histogram file

    if(bGraph){
      canAcc.cd(iCount);
      canAcc.getPad().setTitleFontSize(c_title_size);
      canAcc.draw(gr_acc);

      canComp.cd(iCount);
      canComp.getPad().setTitleFontSize(c_title_size);
      canComp.draw(gr_mrProton[iQ2][iNu]);
      canComp.draw(gr_mrCorr,"same");
    }
    iCount++;
  }
}

outDir.writeFile(outFile);
