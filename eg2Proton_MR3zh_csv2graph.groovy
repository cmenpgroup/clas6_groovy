import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.io.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

OptionParser p = new OptionParser("eg2Proton_MR3zh_csv2graph.groovy");
//String userTgt = "C";
//p.addOption("-s", userTgt, "Solid Target (C, Fe, Pb)");
String userSigmaCut = "std";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (std, sigma_1_0, sigma_1_5, sigma_2_0, sigma_2_5, sigma_3_0)");
int bGraph = 1;
p.addOption("-g",Integer.toString(bGraph), "Graph monitor histograms. (0=quiet)");
p.parse(args);
//userTgt = p.getOption("-s").stringValue();
userSigmaCut = p.getOption("-c").stringValue();
bGraph = p.getOption("-g").intValue();

HistInfo myHI = new HistInfo();
YieldsForMR3zh myMR = new YieldsForMR3zh();
List<String> Q2Cuts = myMR.getQ2Cuts();
List<String> nuCuts = myMR.getNuCuts();
List<String> solidTgt = myHI.getSolidTgtLabel();

TDirectory[] dir = new TDirectory[solidTgt.size()];
solidTgt.eachWithIndex { nTgt, iTgt ->
  dir[iTgt] = new TDirectory();
  dir[iTgt].mkdir("MR3zh");
}

int c1_title_size = 22;
TCanvas canAcc = new TCanvas("canAcc",900,900);
canAcc.divide(3,3);

TCanvas canCorr = new TCanvas("canCorr",900,900);
canCorr.divide(3,3);

TCanvas canUnCorr = new TCanvas("canUnCorr",900,900);
canUnCorr.divide(3,3);

GraphErrors[][] gr_mrProtonCorr = new GraphErrors[zhCuts.size()][solidTgt.size()];
GraphErrors[][] gr_mrProton = new GraphErrors[zhCuts.size()][solidTgt.size()];
GraphErrors[][] grAcc = new GraphErrors[zhCuts.size()][solidTgt.size()];

int iCount = 0;
nuCuts.eachWithIndex { nNu, iNu->
  Q2Cuts.eachWithIndex { nQ2, iQ2->
    solidTgt.eachWithIndex { nTgt, iTgt ->
      dir[iTgt].cd("MR3zh");
      String grcorr = "gr_mrProtonCorr_" + iQ2 + iNu;
      String fileCorr = "MR3zh/csvFiles/" + userSigmaCut + "/" + grcorr + "_" + nTgt + "_" + userSigmaCut + ".csv";
      gr_mrProtonCorr[iQ2][iNu][iTgt] = new GraphErrors(grcorr).csvGraphXYEY(fileCorr,0,1,3,0);
      gr_mrProtonCorr[iQ2][iNu][iTgt].setTitleX(myMR.getXlabel());
      gr_mrProtonCorr[iQ2][iNu][iTgt].setTitleY("R^p");
      gr_mrProtonCorr[iQ2][iNu][iTgt].setMarkerColor(iTgt+1);
      gr_mrProtonCorr[iQ2][iNu][iTgt].setLineColor(iTgt+1);
      gr_mrProtonCorr[iQ2][iNu][iTgt].setMarkerSize(5);
      gr_mrProtonCorr[iQ2][iNu][iTgt].setMarkerStyle(iTgt);
      gr_mrProtonCorr[iQ2][iNu][iTgt].setName(grcorr);
      canCorr.cd(iCount).draw(gr_mrProtonCorr[iQ2][iNu][iTgt],"same");
      dir[iTgt].addDataSet(gr_mrProtonCorr[iQ2][iNu][iTgt]); // add to the histogram file
      dir[iTgt].cd();

      dir[iTgt].cd("MR3zh");
      String grUnCorr = "gr_mrProton_" + iQ2 + iNu;
      String fileUnCorr = "MR3zh/csvFiles/" + userSigmaCut + "/" + grUnCorr + "_" + nTgt + "_" + userSigmaCut + ".csv";
      gr_mrProton[iQ2][iNu][iTgt] = new GraphErrors(grUnCorr).csvGraphXYEY(fileUnCorr,0,1,3,0);
      gr_mrProton[iQ2][iNu][iTgt].setTitleX(myMR.getXlabel());
      gr_mrProton[iQ2][iNu][iTgt].setTitleY("R^p (uncorrected)");
      gr_mrProton[iQ2][iNu][iTgt].setMarkerColor(iTgt+1);
      gr_mrProton[iQ2][iNu][iTgt].setLineColor(iTgt+1);
      gr_mrProton[iQ2][iNu][iTgt].setMarkerSize(5);
      gr_mrProton[iQ2][iNu][iTgt].setMarkerStyle(iTgt);
      gr_mrProton[iQ2][iNu][iTgt].setName(grUnCorr);
      canUnCorr.cd(iCount).draw(gr_mrProton[iQ2][iNu][iTgt],"same");
      dir[iTgt].addDataSet(gr_mrProton[iQ2][iNu][iTgt]); // add to the histogram file
      dir[iTgt].cd();

      dir[iTgt].cd("MR3zh");
      String grAccRatio = "gr_rat" + nTgt + "_" + iQ2 + iNu;
      String fileAcc = "MR3zh/csvFiles/" + userSigmaCut + "/" + grAccRatio + "_pT2_" + nTgt + "_" + userSigmaCut + ".csv";
      grAcc[iQ2][iNu][iTgt] = new GraphErrors(grAccRatio).csvGraphXYEY(fileAcc,0,1,3,0);
      grAcc[iQ2][iNu][iTgt].setTitleX(myMR.getXlabel());
      grAcc[iQ2][iNu][iTgt].setTitleY("Acceptance Ratio");
      grAcc[iQ2][iNu][iTgt].setMarkerColor(iTgt+1);
      grAcc[iQ2][iNu][iTgt].setLineColor(iTgt+1);
      grAcc[iQ2][iNu][iTgt].setMarkerSize(5);
      grAcc[iQ2][iNu][iTgt].setMarkerStyle(iTgt);
      grAcc[iQ2][iNu][iTgt].setName(grAccRatio);
      canAcc.cd(iCount).draw(grAcc[iQ2][iNu][iTgt],"same");
      dir[iTgt].addDataSet(grAcc[iQ2][iNu][iTgt]); // add to the histogram file
      dir[iTgt].cd();
    }
    iCount++;
  }
}

solidTgt.eachWithIndex { nTgt, iTgt ->
  String outFile = "eg2Proton_MR3zh_corr_hists_" + nTgt + "_" + userSigmaCut + "_csv.hipo"
  dir[iTgt].writeFile(outFile); // write the histograms to the file
}
