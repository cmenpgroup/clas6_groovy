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

YieldsForMR3zh myMR = new YieldsForMR3zh();
List<String> Q2Cuts = myMR.getQ2Cuts();
List<String> nuCuts = myMR.getNuCuts();
String xLabel = myMR.getXlabel();

GraphErrors[][][] gr_rat = new GraphErrors[Q2Cuts.size()*nuCuts.size()][solidTgt.size()][sigmaLabel.size()];
GraphErrors[][][] gr_comp = new GraphErrors[Q2Cuts.size()*nuCuts.size()][solidTgt.size()][sigmaLabel.size()];

// create a TDirectory objects and read in the histogram file
TDirectory[] dir = new TDirectory[sigmaLabel.size()];
sigmaLabel.eachWithIndex {nSig, iSig->
  dir[iSig] = new TDirectory();
  dir[iSig].readFile("acc_ratio_hists/MR3zh/acc_ratio_hists_MR3zh_allTgts_sig" + nSig + ".hipo");
}

int c1_title_size = 22;
TCanvas[][] can = new TCanvas[Q2Cuts.size()][nuCuts.size()];

Q2Cuts.eachWithIndex { nQ2, iQ2->
  nuCuts.eachWithIndex { nNu, iNu->
    hNum = iQ2*Q2Cuts.size() + iNu;
    String cname = "c" + iQ2 + iNu;
    can[iQ2][iNu] = new TCanvas(cname,900,600);
    can[iQ2][iNu].divide(3,2);
    solidTgt.eachWithIndex {nTgt, iTgt->
      sigmaLabel.eachWithIndex {nSig, iSig->
        gr_rat[hNum][iTgt][iSig] = dir[iSig].getObject("Qsq"+iQ2+"_nu"+iNu,"gr_rat"+nTgt+"_"+iQ2+iNu);
        gr_rat[hNum][iTgt][iSig].setMarkerColor(iSig+1);
        gr_rat[hNum][iTgt][iSig].setLineColor(iSig+1);
        gr_rat[hNum][iTgt][iSig].setMarkerSize(5);
        gr_rat[hNum][iTgt][iSig].setMarkerStyle(iSig);

        can[iQ2][iNu].getPad().setTitleFontSize(c1_title_size);
        if(iSig==0){
          can[iQ2][iNu].cd(iTgt).draw(gr_rat[hNum][iTgt][iSig]);
        }else{
          can[iQ2][iNu].cd(iTgt).draw(gr_rat[hNum][iTgt][iSig],"same");
        }
      }
      sigmaLabel.eachWithIndex {nSig, iSig->
        gr_comp[hNum][iTgt][iSig] = new GraphErrors();
        gr_comp[hNum][iTgt][iSig] = gr_rat[hNum][iTgt][iSig].divide(gr_rat[hNum][iTgt][2]);
        gr_comp[hNum][iTgt][iSig].setName("gr_comp" + nTgt + "_" + iQ2 + iNu+ "_sig" + nSig);
        gr_comp[hNum][iTgt][iSig].setMarkerColor(iSig+1);
        gr_comp[hNum][iTgt][iSig].setLineColor(iSig+1);
        gr_comp[hNum][iTgt][iSig].setMarkerSize(5);
        gr_comp[hNum][iTgt][iSig].setMarkerStyle(iSig);
        gr_comp[hNum][iTgt][iSig].setTitle("eg2 - " + nTgt + "/D");
        gr_comp[hNum][iTgt][iSig].setTitleX(xLabel);
        gr_comp[hNum][iTgt][iSig].setTitleY("Super Ratio");

        can[iQ2][iNu].getPad().setTitleFontSize(c1_title_size);
        if(iSig==0){
          can[iQ2][iNu].cd(iTgt+solidTgt.size()).draw(gr_comp[hNum][iTgt][iSig]);
        }else{
          can[iQ2][iNu].cd(iTgt+solidTgt.size()).draw(gr_comp[hNum][iTgt][iSig],"same");
        }
      }
    }
  }
}
