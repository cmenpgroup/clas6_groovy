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
List<String> Var = myHI.getVariables();
List<String> xLabel = myHI.getXlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<String> sigmaLabel = myHI.getSigmaLabel();

GraphErrors[][][] gr_rat = new GraphErrors[Var.size()][solidTgt.size()][sigmaLabel.size()];
GraphErrors[][][] gr_comp = new GraphErrors[Var.size()][solidTgt.size()][sigmaLabel.size()];

// create a TDirectory objects and read in the histogram file
TDirectory[] dir = new TDirectory[sigmaLabel.size()];
sigmaLabel.eachWithIndex {nSig, iSig->
  dir[iSig] = new TDirectory();
  dir[iSig].readFile("acc_ratio_hists/MR/acc_ratio_hists_MR_allTgts_sig" + nSig + ".hipo");
}

int c1_title_size = 22;
TCanvas[] can = new TCanvas[Var.size()];

Var.eachWithIndex { nVar, iVar->
  String cname = "can_" + nVar;
  can[iVar] = new TCanvas(cname,900,600);
  can[iVar].divide(3,2);
  solidTgt.eachWithIndex {nTgt, iTgt->
    sigmaLabel.eachWithIndex {nSig, iSig->
      gr_rat[iVar][iTgt][iSig] = dir[iSig].getObject(nVar,"gr_rat" + nTgt + "_" + nVar);
      gr_rat[iVar][iTgt][iSig].setMarkerColor(iSig+1);
      gr_rat[iVar][iTgt][iSig].setLineColor(iSig+1);
      gr_rat[iVar][iTgt][iSig].setMarkerSize(5);
      gr_rat[iVar][iTgt][iSig].setMarkerStyle(iSig);

      can[iVar].getPad().setTitleFontSize(c1_title_size);
      if(iSig==0){
        can[iVar].cd(iTgt).draw(gr_rat[iVar][iTgt][iSig]);
      }else{
        can[iVar].cd(iTgt).draw(gr_rat[iVar][iTgt][iSig],"same");
      }
    }
    sigmaLabel.eachWithIndex {nSig, iSig->
      gr_comp[iVar][iTgt][iSig] = new GraphErrors();
      gr_comp[iVar][iTgt][iSig] = gr_rat[iVar][iTgt][iSig].divide(gr_rat[iVar][iTgt][2]);
      gr_comp[iVar][iTgt][iSig].setName("gr_comp" + nTgt + "_" + nVar + "_sig" + nSig);
      gr_comp[iVar][iTgt][iSig].setMarkerColor(iSig+1);
      gr_comp[iVar][iTgt][iSig].setLineColor(iSig+1);
      gr_comp[iVar][iTgt][iSig].setMarkerSize(5);
      gr_comp[iVar][iTgt][iSig].setMarkerStyle(iSig);
      gr_comp[iVar][iTgt][iSig].setTitle("eg2 - " + nTgt + "/D");
      gr_comp[iVar][iTgt][iSig].setTitleX(xLabel[iVar]);
      gr_comp[iVar][iTgt][iSig].setTitleY("Super Ratio");

      can[iVar].getPad().setTitleFontSize(c1_title_size);
      if(iSig==0){
        can[iVar].cd(iTgt+solidTgt.size()).draw(gr_comp[iVar][iTgt][iSig]);
      }else{
        can[iVar].cd(iTgt+solidTgt.size()).draw(gr_comp[iVar][iTgt][iSig],"same");
      }
    }
  }
}
