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

OptionParser p = new OptionParser("eg2Proton_MR_csv2graph.groovy");
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
List<String> Var = myHI.getVariables();
List<String> xLabel = myHI.getXlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();

TDirectory[] dir = new TDirectory[solidTgt.size()];
solidTgt.eachWithIndex { nTgt, iTgt ->
  dir[iTgt] = new TDirectory();
  Var.eachWithIndex { nVar, iVar->
    dir[iTgt].mkdir(nVar);
  }
}

int c1_title_size = 22;
TCanvas[] can = new TCanvas[Var.size()];
GraphErrors[][] gr_mrProtonCorr = new GraphErrors[Var.size()][solidTgt.size()];

Var.eachWithIndex { nVar, iVar->
  if(bGraph){
    String cname = "can" + iVar;
    can[iVar] = new TCanvas(cname,600,600);
    can[iVar].cd(0);
    can[iVar].getPad().setTitleFontSize(c1_title_size);
  }

  solidTgt.eachWithIndex { nTgt, iTgt ->
    dir[iTgt].cd(nVar);
    String grcorr = "gr_mrProtonCorr_" + nVar;
    String fileName = "MR1D/csvFiles/" + userSigmaCut + "/" + grcorr + "_" + nTgt + "_" + userSigmaCut + ".csv";
    gr_mrProtonCorr[iVar][iTgt] = new GraphErrors(grcorr).csvGraphXYEY(fileName,0,1,3,0);
    gr_mrProtonCorr[iVar][iTgt].setTitleX(xLabel[iVar]);
    gr_mrProtonCorr[iVar][iTgt].setTitleY("R^p");
    gr_mrProtonCorr[iVar][iTgt].setMarkerColor(iTgt+1);
    gr_mrProtonCorr[iVar][iTgt].setLineColor(iTgt+1);
    gr_mrProtonCorr[iVar][iTgt].setMarkerSize(5);
    gr_mrProtonCorr[iVar][iTgt].setMarkerStyle(iTgt);
    gr_mrProtonCorr[iVar][iTgt].setName(grcorr);
    if(bGraph) can[iVar].draw(gr_mrProtonCorr[iVar][iTgt],"same");
    dir[iTgt].addDataSet(gr_mrProtonCorr[iVar][iTgt]); // add to the histogram file
    dir[iTgt].cd();
  }
}

TCanvas[] canUnCorr = new TCanvas[Var.size()];
GraphErrors[][] gr_mrProton = new GraphErrors[Var.size()][solidTgt.size()];

Var.eachWithIndex { nVar, iVar->
  if(bGraph){
    String cname = "canUnCorr" + iVar;
    canUnCorr[iVar] = new TCanvas(cname,600,600);
    canUnCorr[iVar].cd(0);
    canUnCorr[iVar].getPad().setTitleFontSize(c1_title_size);
  }

  solidTgt.eachWithIndex { nTgt, iTgt ->
    dir[iTgt].cd(nVar);
    String grUnCorr = "gr_mrProton_" + nVar;
    String fileName = "MR1D/csvFiles/" + userSigmaCut + "/" + grUnCorr + "_" + nTgt + "_" + userSigmaCut + ".csv";
    gr_mrProton[iVar][iTgt] = new GraphErrors(grUnCorr).csvGraphXYEY(fileName,0,1,3,0);
    gr_mrProton[iVar][iTgt].setTitleX(xLabel[iVar]);
    gr_mrProton[iVar][iTgt].setTitleY("R^p (uncorrected)");
    gr_mrProton[iVar][iTgt].setMarkerColor(iTgt+1);
    gr_mrProton[iVar][iTgt].setLineColor(iTgt+1);
    gr_mrProton[iVar][iTgt].setMarkerSize(5);
    gr_mrProton[iVar][iTgt].setMarkerStyle(iTgt);
    gr_mrProton[iVar][iTgt].setName(grUnCorr);
    if(bGraph) canUnCorr[iVar].draw(gr_mrProton[iVar][iTgt],"same");
    dir[iTgt].addDataSet(gr_mrProton[iVar][iTgt]); // add to the histogram file
    dir[iTgt].cd();
  }
}

TCanvas[] canAcc = new TCanvas[Var.size()];
GraphErrors[][] grAcc = new GraphErrors[Var.size()][solidTgt.size()];

Var.eachWithIndex { nVar, iVar->
  if(bGraph){
    String cname = "canAcc" + iVar;
    canAcc[iVar] = new TCanvas(cname,600,600);
    canAcc[iVar].cd(0);
    canAcc[iVar].getPad().setTitleFontSize(c1_title_size);
  }

  solidTgt.eachWithIndex { nTgt, iTgt ->
    dir[iTgt].cd(nVar);
    String grAccRatio = "gr_rat" + nTgt +"_" + nVar;
    String fileName = "MR1D/csvFiles/" + userSigmaCut + "/" + grAccRatio + "_" + nTgt + "_" + userSigmaCut + ".csv";
    grAcc[iVar][iTgt] = new GraphErrors(grAccRatio).csvGraphXYEY(fileName,0,1,3,0);
    grAcc[iVar][iTgt].setTitleX(xLabel[iVar]);
    grAcc[iVar][iTgt].setTitleY("Acceptance Ratio");
    grAcc[iVar][iTgt].setMarkerColor(iTgt+1);
    grAcc[iVar][iTgt].setLineColor(iTgt+1);
    grAcc[iVar][iTgt].setMarkerSize(5);
    grAcc[iVar][iTgt].setMarkerStyle(iTgt);
    grAcc[iVar][iTgt].setName(grAccRatio);
    if(bGraph) canAcc[iVar].draw(grAcc[iVar][iTgt],"same");
    dir[iTgt].addDataSet(grAcc[iVar][iTgt]); // add to the histogram file
    dir[iTgt].cd();
  }
}

solidTgt.eachWithIndex { nTgt, iTgt ->
  String outFile = "eg2Proton_MR_corr_hists_" + nTgt + "_" + userSigmaCut + "_csv.hipo"
  dir[iTgt].writeFile(outFile); // write the histograms to the file
}
