//import org.jlab.jnp.hipo4.data.*;
//import org.jlab.jnp.hipo4.io.*;
//import org.jlab.jnp.physics.*;
//import org.jlab.jnp.pdg.PhysicsConstants;
//import org.jlab.jnp.pdg.PDGDatabase;
//import org.jlab.jnp.pdg.PDGParticle;

//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.base.GStyle;
import org.jlab.jnp.groot.graphics.TDataCanvas;
//import org.jlab.jnp.groot.graphics.Legend;
import org.jlab.jnp.groot.graphics.LegendNode2D;
import org.jlab.jnp.groot.graphics.LegendNode2D.LegendStyle;
import org.jlab.jnp.groot.settings.GRootColorPalette;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

OptionParser p = new OptionParser("eg2Proton_MR2pT2_comp.groovy");

String userSigmaCut = "std";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (std, sigma_1_0, sigma_1_5, sigma_2_0, sigma_2_5, sigma_3_0)");

p.parse(args);
userSigmaCut = p.getOption("-c").stringValue();

HistInfo myHI = new HistInfo();
List<String> solidTgt = myHI.getSolidTgtLabel();

YieldsForMR2pT2 myMR = new YieldsForMR2pT2();
List<String> zhCuts = myMR.getZhCuts();

TDirectory[] dir = new TDirectory[solidTgt.size()];
solidTgt.eachWithIndex { nTgt, iTgt ->
  String fileName = "MR2pT2/coatjavaHipo/eg2Proton_MR2pT2_corr_hists_" + nTgt + "_" + userSigmaCut + "_csv.hipo";
  println fileName;
  dir[iTgt] = new TDirectory();
  dir[iTgt].readFile(fileName);
}

TDataCanvas can = new TDataCanvas("can",900,900);
can.divide(3,3);
LegendNode2D[] legend = new LegendNode2D[zhCuts.size()];
GraphErrors[][] gr_mrProtonCorr = new GraphErrors[zhCuts.size()][solidTgt.size()];

zhCuts.eachWithIndex { nZh, iZh->
//  can.getPad().setTitleFontSize(c1_title_size);
  legend[iZh] = new LegendNode2D(350,50);
//  legend[iZh] = new LegendNode2D(legX[iZh],legY[iZh]);
  String grcorr = "gr_mrProtonCorr_pT2_" + iZh;

  solidTgt.eachWithIndex { nTgt, iTgt ->
    gr_mrProtonCorr[iZh][iTgt] = dir[iTgt].getObject("MR2pT2/",grcorr);
    gr_mrProtonCorr[iZh][iTgt].setTitleX("pT^2 (GeV^2)");
    gr_mrProtonCorr[iZh][iTgt].setTitleY("R_p");
    gr_mrProtonCorr[iZh][iTgt].setMarkerColor(iTgt+1);
    gr_mrProtonCorr[iZh][iTgt].setLineColor(iTgt+1);
    gr_mrProtonCorr[iZh][iTgt].setMarkerSize(5);
    gr_mrProtonCorr[iZh][iTgt].setMarkerStyle(iTgt);
    legend[iZh].add(gr_mrProtonCorr[iZh][iTgt],nTgt);
    can.getDataCanvas().cd(iZh).draw(gr_mrProtonCorr[iZh][iTgt],"same");
  }
  can.getDataCanvas().getRegion(0).addNode(legend[iZh]);
//  can.setMargins(50,25,50,75);
}
can.repaint();

int c1_title_size = 22;
TCanvas canAcc = new TCanvas("canAcc",900,900);
canAcc.divide(3,3);
GraphErrors[][] grAcc = new GraphErrors[zhCuts.size()][solidTgt.size()];

zhCuts.eachWithIndex { nZh, iZh->
  canAcc.cd(iZh);
  canAcc.getPad().setTitleFontSize(c1_title_size);

  solidTgt.eachWithIndex { nTgt, iTgt ->
    String grAccRatio = "gr_rat" + nTgt + "_" + iZh;
    grAcc[iZh][iTgt] = dir[iTgt].getObject("MR2pT2/",grAccRatio);
    grAcc[iZh][iTgt].setTitleX("pT^2 (GeV^2)");
    grAcc[iZh][iTgt].setTitleY("R_p");
    grAcc[iZh][iTgt].setMarkerColor(iTgt+1);
    grAcc[iZh][iTgt].setLineColor(iTgt+1);
    grAcc[iZh][iTgt].setMarkerSize(5);
    grAcc[iZh][iTgt].setMarkerStyle(iTgt);
    canAcc.draw(grAcc[iZh][iTgt],"same");
  }
}
