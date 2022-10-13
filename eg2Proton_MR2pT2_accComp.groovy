//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.base.GStyle;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

HistInfo myHI = new HistInfo();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<String> sigmaLabel = myHI.getSigmaLabel();

YieldsForMR2pT2 myMR = new YieldsForMR2pT2();
List<String> zhCuts = myMR.getZhCuts();
String xLabel = myMR.getXlabel();

GraphErrors[][][] gr_rat = new GraphErrors[zhCuts.size()][solidTgt.size()][sigmaLabel.size()];
GraphErrors[][][] gr_comp = new GraphErrors[zhCuts.size()][solidTgt.size()][sigmaLabel.size()];

// create a TDirectory objects and read in the histogram file
TDirectory[] dir = new TDirectory[sigmaLabel.size()];
sigmaLabel.eachWithIndex {nSig, iSig->
  dir[iSig] = new TDirectory();
  dir[iSig].readFile("acc_ratio_hists/MR2pT2/acc_ratio_hists_MR2pT2_allTgts_sig" + nSig + ".hipo");
}

int c1_title_size = 22;
TCanvas[] can = new TCanvas[zhCuts.size()];

zhCuts.eachWithIndex { nZh, iZh->
  String cname = "can_" + nZh;
  can[iZh] = new TCanvas(cname,900,600);
  can[iZh].divide(3,2);
  solidTgt.eachWithIndex {nTgt, iTgt->
    sigmaLabel.eachWithIndex {nSig, iSig->
      gr_rat[iZh][iTgt][iSig] = dir[iSig].getObject(nZh,"gr_rat" + nTgt + "_" + iZh);
      gr_rat[iZh][iTgt][iSig].setMarkerColor(iSig+1);
      gr_rat[iZh][iTgt][iSig].setLineColor(iSig+1);
      gr_rat[iZh][iTgt][iSig].setMarkerSize(5);
      gr_rat[iZh][iTgt][iSig].setMarkerStyle(iSig);

      can[iZh].getPad().setTitleFontSize(c1_title_size);
      if(iSig==0){
        can[iZh].cd(iTgt).draw(gr_rat[iZh][iTgt][iSig]);
      }else{
        can[iZh].cd(iTgt).draw(gr_rat[iZh][iTgt][iSig],"same");
      }
    }
    sigmaLabel.eachWithIndex {nSig, iSig->
      gr_comp[iZh][iTgt][iSig] = new GraphErrors();
      gr_comp[iZh][iTgt][iSig] = gr_rat[iZh][iTgt][iSig].divide(gr_rat[iZh][iTgt][2]);
      gr_comp[iZh][iTgt][iSig].setName("gr_comp" + nTgt + "_" + iZh + "_sig" + nSig);
      gr_comp[iZh][iTgt][iSig].setMarkerColor(iSig+1);
      gr_comp[iZh][iTgt][iSig].setLineColor(iSig+1);
      gr_comp[iZh][iTgt][iSig].setMarkerSize(5);
      gr_comp[iZh][iTgt][iSig].setMarkerStyle(iSig);
      gr_comp[iZh][iTgt][iSig].setTitle("eg2 - " + nTgt + "/D");
      gr_comp[iZh][iTgt][iSig].setTitleX(xLabel);
      gr_comp[iZh][iTgt][iSig].setTitleY("Super Ratio");

      can[iZh].getPad().setTitleFontSize(c1_title_size);
      if(iSig==0){
        can[iZh].cd(iTgt+solidTgt.size()).draw(gr_comp[iZh][iTgt][iSig]);
      }else{
        can[iZh].cd(iTgt+solidTgt.size()).draw(gr_comp[iZh][iTgt][iSig],"same");
      }
    }
  }
}
