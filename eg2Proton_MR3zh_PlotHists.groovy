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

def cli = new CliBuilder(usage:'eg2Proton_MR3zh_PlotHists.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

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
String[] Q2Cuts = ["q2>1.0&&q2<1.33","q2>1.33&&q2<1.76","q2>1.76&&q2<4.1"];
String[] nuCuts = ["nu>2.2&&nu<3.2","nu>3.2&&nu<3.73","nu>3.73&&nu<4.25"];
int indexTgt = 0;

TDirectory dir = new TDirectory();
dir.readFile(fileName);

String[] DirLabel = ["LD2/","Solid/","multiplicity/"];
H1F[][][] h1_nProton = new H1F[Q2Cuts.size()][nuCuts.size()][TgtLabel.size()];
GraphErrors[][] gr_mrProton = new GraphErrors[Q2Cuts.size()][nuCuts.size()];

Q2Cuts.eachWithIndex { nQ2, iQ2->
  nuCuts.eachWithIndex { nNu, iNu->
    DirLabel.eachWithIndex{nDir, iDir->
      if(iDir<2){
        hname = "hYlds_" + TgtLabel[iDir] + "_zh_" + iQ2 + iNu;
        h1_nProton[iQ2][iNu][iDir]= dir.getObject(nDir,hname);
      }else{
        hname = "gr_mrProton_zh_" + iQ2 + iNu;
        gr_mrProton[iQ2][iNu] = dir.getObject(nDir,hname);
      }
    }
  }
}

def dirname = "Carbon/";
TDirectory dirAcc = new TDirectory();
dirAcc.readFile("acc_ratio_hists_C.hipo");
H1F[][] h1_ratioAcc = new H1F[Q2Cuts.size()][nuCuts.size()];
Q2Cuts.eachWithIndex { nQ2, iQ2->
  nuCuts.eachWithIndex { nNu, iNu->
      hname = "acc_ratio_C_" + iQ2 + iNu;
      h1_ratioAcc[iQ2][iNu] = dirAcc.getObject(dirname,hname);
  }
}

int iCount = 0;
int c_title_size = 22;
TCanvas can = new TCanvas("can",900,900);
can.divide(3,3);

TCanvas canAcc = new TCanvas("canAcc",900,900);
canAcc.divide(3,3);

TCanvas canNorm = new TCanvas("canNorm",900,900);
canNorm.divide(3,3);

TCanvas canComp = new TCanvas("canComp",900,900);
canComp.divide(3,3);

TCanvas canGr = new TCanvas("canComp",900,900);
canGr.divide(3,3);

H1F[][] h1_mrProton = new H1F[Q2Cuts.size()][nuCuts.size()];
H1F[][] h1_mrNorm = new H1F[Q2Cuts.size()][nuCuts.size()];
GraphErrors[][] gr_mrNorm = new GraphErrors[Q2Cuts.size()][nuCuts.size()];
Q2Cuts.eachWithIndex { nQ2, iQ2->
  nuCuts.eachWithIndex { nNu, iNu->
    can.cd(iCount);
    can.getPad().setTitleFontSize(c_title_size);
    h1_mrProton[iQ2][iNu] = H1F.divide(h1_nProton[iQ2][iNu][1],h1_nProton[iQ2][iNu][0]);
    h1_mrProton[iQ2][iNu].setName("h1_mrProton_zh" + iQ2 + iNu);
    h1_mrProton[iQ2][iNu].setLineColor(BLUE);
    h1_mrProton[iQ2][iNu].setLineWidth(3);
    h1_mrProton[iQ2][iNu].setTitleX("z_h");
    h1_mrProton[iQ2][iNu].setTitleY("R^p");
    can.draw(h1_mrProton[iQ2][iNu]);

    canAcc.cd(iCount);
    canAcc.getPad().setTitleFontSize(c_title_size);
    h1_ratioAcc[iQ2][iNu].setTitleX("z_h");
    h1_ratioAcc[iQ2][iNu].setTitleY("Ratio");
    canAcc.draw(h1_ratioAcc[iQ2][iNu]);

    canNorm.cd(iCount);
    canNorm.getPad().setTitleFontSize(c_title_size);
    h1_mrNorm[iQ2][iNu] = H1F.divide(h1_mrProton[iQ2][iNu],h1_ratioAcc[iQ2][iNu]);
    h1_mrNorm[iQ2][iNu].setLineColor(YELLOW);
    h1_mrNorm[iQ2][iNu].setLineWidth(3);
    canNorm.draw(h1_mrNorm[iQ2][iNu]);

    canComp.cd(iCount);
    canComp.getPad().setTitleFontSize(c_title_size);
    canComp.draw(h1_mrProton[iQ2][iNu]);
    canComp.draw(h1_mrNorm[iQ2][iNu],"same");

    canGr.cd(iCount);
    gr_mrProton[iQ2][iNu].setTitle("eg2 - " + solidTgt[indexTgt] + "/D2");
    gr_mrProton[iQ2][iNu].setTitleX("z_h");
    gr_mrProton[iQ2][iNu].setTitleY("R^p");
    gr_mrProton[iQ2][iNu].setMarkerColor(3);
    gr_mrProton[iQ2][iNu].setLineColor(3);
    gr_mrProton[iQ2][iNu].setMarkerSize(3);
    canGr.draw(gr_mrProton[iQ2][iNu]);

    gr_mrNorm[iQ2][iNu] = h1_mrNorm[iQ2][iNu].getGraph();
    gr_mrNorm[iQ2][iNu].setName("gr_mrNorm_zh_" + iQ2 + iNu);
    gr_mrNorm[iQ2][iNu].setTitle("eg2 - " + solidTgt[indexTgt] + "/D2");
    gr_mrNorm[iQ2][iNu].setTitleX("z_h");
    gr_mrNorm[iQ2][iNu].setTitleY("R^p");
    gr_mrNorm[iQ2][iNu].setMarkerColor(4);
    gr_mrNorm[iQ2][iNu].setLineColor(4);
    gr_mrNorm[iQ2][iNu].setMarkerSize(3);
    canGr.draw(gr_mrNorm[iQ2][iNu],"same");

    iCount++;
  }
}
