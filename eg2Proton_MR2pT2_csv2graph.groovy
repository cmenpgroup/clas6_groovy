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

OptionParser p = new OptionParser("eg2Proton_MR2pT2_csv2graph.groovy");
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
YieldsForMR2pT2 myMR = new YieldsForMR2pT2();
List<String> zhCuts = myMR.getZhCuts();
List<String> solidTgt = myHI.getSolidTgtLabel();

TDirectory[] dir = new TDirectory[solidTgt.size()];
solidTgt.eachWithIndex { nTgt, iTgt ->
  dir[iTgt] = new TDirectory();
  dir[iTgt].mkdir("MR2pT2");
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

zhCuts.eachWithIndex { nZh, iZh->
  solidTgt.eachWithIndex { nTgt, iTgt ->
    dir[iTgt].cd("MR2pT2");
    String grcorr = "gr_mrProtonCorr_pT2_" + iZh;
    String fileCorr = "MR2pT2/csvFiles/" + userSigmaCut + "/" + grcorr + "_" + nTgt + "_" + userSigmaCut + ".csv";
    gr_mrProtonCorr[iZh][iTgt] = new GraphErrors(grcorr).csvGraphXYEY(fileCorr,0,1,3,0);
    gr_mrProtonCorr[iZh][iTgt].setTitleX(myMR.getXlabel());
    gr_mrProtonCorr[iZh][iTgt].setTitleY("R^p");
    gr_mrProtonCorr[iZh][iTgt].setMarkerColor(iTgt+1);
    gr_mrProtonCorr[iZh][iTgt].setLineColor(iTgt+1);
    gr_mrProtonCorr[iZh][iTgt].setMarkerSize(5);
    gr_mrProtonCorr[iZh][iTgt].setMarkerStyle(iTgt);
    gr_mrProtonCorr[iZh][iTgt].setName(grcorr);
    canCorr.cd(iZh).draw(gr_mrProtonCorr[iZh][iTgt],"same");
    dir[iTgt].addDataSet(gr_mrProtonCorr[iZh][iTgt]); // add to the histogram file
    dir[iTgt].cd();

    dir[iTgt].cd("MR2pT2");
    String grUnCorr = "gr_mrProton_pT2_" + iZh;
    String fileUnCorr = "MR2pT2/csvFiles/" + userSigmaCut + "/" + grUnCorr + "_" + nTgt + "_" + userSigmaCut + ".csv";
    gr_mrProton[iZh][iTgt] = new GraphErrors(grUnCorr).csvGraphXYEY(fileUnCorr,0,1,3,0);
    gr_mrProton[iZh][iTgt].setTitleX(myMR.getXlabel());
    gr_mrProton[iZh][iTgt].setTitleY("R^p (uncorrected)");
    gr_mrProton[iZh][iTgt].setMarkerColor(iTgt+1);
    gr_mrProton[iZh][iTgt].setLineColor(iTgt+1);
    gr_mrProton[iZh][iTgt].setMarkerSize(5);
    gr_mrProton[iZh][iTgt].setMarkerStyle(iTgt);
    gr_mrProton[iZh][iTgt].setName(grUnCorr);
    canUnCorr.cd(iZh).draw(gr_mrProton[iZh][iTgt],"same");
    dir[iTgt].addDataSet(gr_mrProton[iZh][iTgt]); // add to the histogram file
    dir[iTgt].cd();

    dir[iTgt].cd("MR2pT2");
    String grAccRatio = "gr_rat" + nTgt + "_" + iZh;
    String fileAcc = "MR2pT2/csvFiles/" + userSigmaCut + "/" + grAccRatio + "_pT2_" + nTgt + "_" + userSigmaCut + ".csv";
    grAcc[iZh][iTgt] = new GraphErrors(grAccRatio).csvGraphXYEY(fileAcc,0,1,3,0);
    grAcc[iZh][iTgt].setTitleX(myMR.getXlabel());
    grAcc[iZh][iTgt].setTitleY("Acceptance Ratio");
    grAcc[iZh][iTgt].setMarkerColor(iTgt+1);
    grAcc[iZh][iTgt].setLineColor(iTgt+1);
    grAcc[iZh][iTgt].setMarkerSize(5);
    grAcc[iZh][iTgt].setMarkerStyle(iTgt);
    grAcc[iZh][iTgt].setName(grAccRatio);
    canAcc.cd(iZh).draw(grAcc[iZh][iTgt],"same");
    dir[iTgt].addDataSet(grAcc[iZh][iTgt]); // add to the histogram file
    dir[iTgt].cd();
  }
}

solidTgt.eachWithIndex { nTgt, iTgt ->
  String outFile = "eg2Proton_MR2pT2_corr_hists_" + nTgt + "_" + userSigmaCut + "_csv.hipo"
  dir[iTgt].writeFile(outFile); // write the histograms to the file
}
