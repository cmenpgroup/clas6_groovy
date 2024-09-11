//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.base.GStyle;
import org.jlab.jnp.groot.graphics.TDataCanvas;
import org.jlab.jnp.groot.graphics.LegendNode2D;
import org.jlab.jnp.groot.graphics.LegendNode2D.LegendStyle;
import org.jlab.jnp.groot.settings.GRootColorPalette;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

GStyle.getAxisAttributesX().setTitleFontSize(24);
GStyle.getAxisAttributesY().setTitleFontSize(24);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

OptionParser p = new OptionParser("eg2Proton_MRcomp.groovy");

String userSigmaCut = "std";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (std, sigma_1_0, sigma_1_5, sigma_2_0, sigma_2_5, sigma_3_0)");

p.parse(args);
userSigmaCut = p.getOption("-c").stringValue();

HistInfo myHI = new HistInfo();
List<String> Var = myHI.getVariables();
List<String> xLabel = myHI.getXlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();

int c1_title_size = 22;
TDataCanvas[] can = new TDataCanvas[Var.size()];
LegendNode2D[] legend = new LegendNode2D[Var.size()];
TDirectory[] dir = new TDirectory[solidTgt.size()];
solidTgt.eachWithIndex { nTgt, iTgt ->
  String fileName = "MR1D/coatjavaHipo/eg2Proton_MR_corr_hists_" + nTgt + "_" + userSigmaCut + "_csv.hipo";
  println fileName;
  dir[iTgt] = new TDirectory();
  dir[iTgt].readFile(fileName);
}

GraphErrors[][] gr_mrProtonCorr = new GraphErrors[Var.size()][solidTgt.size()];

Var.eachWithIndex { nVar, iVar->
  String cname = "can" + iVar;
  can[iVar] = new TDataCanvas(600,600);
  can[iVar].cd(0);
//  can[iVar].getPad().setTitleFontSize(c1_title_size);
//    if(nVar=="zh") can[iVar].getPad().getAxisY().setLog(true);

  legend[iVar] = new LegendNode2D(400,30);

  String imgRp = "eg2Proton_MR_comp_" + nVar + "_" + userSigmaCut + ".png";

  solidTgt.eachWithIndex { nTgt, iTgt ->
    String grcorr = "gr_mrProtonCorr_" + nVar;
    gr_mrProtonCorr[iVar][iTgt] = dir[iTgt].getObject(nVar,grcorr);
    gr_mrProtonCorr[iVar][iTgt].setTitleX(xLabel[iVar]);
    gr_mrProtonCorr[iVar][iTgt].setTitleY("R_p");
    gr_mrProtonCorr[iVar][iTgt].setMarkerColor(iTgt+1);
    gr_mrProtonCorr[iVar][iTgt].setLineColor(iTgt+1);
    gr_mrProtonCorr[iVar][iTgt].setMarkerSize(5);
    gr_mrProtonCorr[iVar][iTgt].setMarkerStyle(iTgt);
    legend[iVar].add(gr_mrProtonCorr[iVar][iTgt],nTgt);
    can[iVar].draw(gr_mrProtonCorr[iVar][iTgt],"same");
  }
  can[iVar].getDataCanvas().getRegion(0).addNode(legend[iVar]);
  can[iVar].repaint();
//  can[iVar].save(imgRp);
}

TCanvas[] canAcc = new TCanvas[Var.size()];
GraphErrors[][] grAcc = new GraphErrors[Var.size()][solidTgt.size()];

// loop over the 1-D variables
Var.eachWithIndex { nVar, iVar->
  String cname = "canAcc" + iVar;
  canAcc[iVar] = new TCanvas(cname,600,600);
  canAcc[iVar].cd(0);
  canAcc[iVar].getPad().setTitleFontSize(c1_title_size);

  String imgAccRat = "eg2Proton_MR_comp_" + nVar + "_" + userSigmaCut + "_AccRat.png";

  solidTgt.eachWithIndex { nTgt, iTgt ->
    String grcorr = "gr_rat" + nTgt + "_" + nVar;
    grAcc[iVar][iTgt] = dir[iTgt].getObject(nVar,grcorr);
    grAcc[iVar][iTgt].setTitleX(xLabel[iVar]);
    grAcc[iVar][iTgt].setTitleY("Acceptance Ratio");
    grAcc[iVar][iTgt].setMarkerColor(iTgt+1);
    grAcc[iVar][iTgt].setLineColor(iTgt+1);
    grAcc[iVar][iTgt].setMarkerSize(5);
    grAcc[iVar][iTgt].setMarkerStyle(iTgt);
    if(iTgt==0){
      canAcc[iVar].draw(grAcc[iVar][iTgt]);
    }else{
      canAcc[iVar].draw(grAcc[iVar][iTgt],"same");
    }
  }
  canAcc[iVar].save(imgAccRat);
}
