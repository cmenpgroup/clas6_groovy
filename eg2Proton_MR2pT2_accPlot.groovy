//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.base.GStyle;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

GStyle.getAxisAttributesX().setTitleFontSize(24);
GStyle.getAxisAttributesY().setTitleFontSize(24);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

HistInfo myHI = new HistInfo();
List<String> solidTgt = myHI.getSolidTgtLabel();

YieldsForMR2pT2 myMR = new YieldsForMR2pT2();
List<String> zhCuts = myMR.getZhCuts();
List<String> zhCutsLabels = myMR.getZhCutsLabels();

solidTgt.add(0,"D"); // add the D target to the List
println solidTgt;

String[] accType = ["gen","rec","acc"];
H1F[][][] h1_acc = new H1F[zhCuts.size()][accType.size()][solidTgt.size()];
GraphErrors[][] gr_rat = new GraphErrors[zhCuts.size()][solidTgt.size()];
GraphErrors[][] gr_acc = new GraphErrors[zhCuts.size()][solidTgt.size()];

String nSig = "std";
// create a TDirectory objects and read in the histogram file
TDirectory dir = new TDirectory();
dir.readFile("acc_ratio_hists/MR2pT2/acc_ratio_hists_MR2pT2_allTgts_sig" + nSig + ".hipo");

int c1_title_size = 18;
int c2_title_size = 24;
TCanvas[] can = new TCanvas[zhCuts.size()];
TCanvas[] canAcc = new TCanvas[zhCuts.size()];
TCanvas[] canRat = new TCanvas[zhCuts.size()];
TCanvas canAccAll = new TCanvas("canAccAll",1000,1000);
canAccAll.divide(3,3);
TCanvas canRatAll = new TCanvas("canRatAll",1000,1000);
canRatAll.divide(3,3);

zhCuts.eachWithIndex { nZh, iZh->
  String cAccName = "canAcc_" + iZh;
  canAcc[iZh] = new TCanvas(cAccName,600,600);

  String cRatName = "canRat_" + iZh;
  canRat[iZh] = new TCanvas(cRatName,600,600);

  String cname = "can_" + iZh;
  can[iZh] = new TCanvas(cname,1200,900);
  can[iZh].divide(3,4);

  int iAccCtr = 0;
  solidTgt.eachWithIndex {nTgt, iTgt->
    accType.eachWithIndex {nAcc, iAcc->
      String hname = nAcc + nTgt + "_MR2pT2_" + iZh;
      h1_acc[iZh][iAcc][iTgt] = dir.getObject(nZh,hname);
      can[iZh].getPad().setTitleFontSize(c1_title_size);
      can[iZh].cd(iAccCtr).draw(h1_acc[iZh][iAcc][iTgt]);
      iAccCtr++;
    }

    canAcc[iZh].cd(0);
    gr_acc[iZh][iTgt] = dir.getObject(nZh,"gr_acc" + nTgt + "_" + iZh);
    gr_acc[iZh][iTgt].setMarkerColor(iTgt+1);
    gr_acc[iZh][iTgt].setLineColor(iTgt+1);
    gr_acc[iZh][iTgt].setMarkerSize(5);
    gr_acc[iZh][iTgt].setMarkerStyle(iTgt);
    canAcc[iZh].getPad().setTitle(zhCutsLabels[iZh]);
    canAcc[iZh].getPad().setTitleFontSize(c2_title_size);
    canAccAll.cd(iZh);
    canAccAll.getPad().setTitle(zhCutsLabels[iZh]);
    canAccAll.getPad().setTitleFontSize(c2_title_size);
    if(iTgt==0){
      canAcc[iZh].draw(gr_acc[iZh][iTgt]);
      canAccAll.cd(iZh).draw(gr_acc[iZh][iTgt]);
    }else{
      canAcc[iZh].draw(gr_acc[iZh][iTgt],"same");
      canAccAll.cd(iZh).draw(gr_acc[iZh][iTgt],"same");
    }

    if(iTgt>0){
      canRat[iZh].cd(0);
      gr_rat[iZh][iTgt] = dir.getObject(nZh,"gr_rat" + nTgt + "_" + iZh);
      gr_rat[iZh][iTgt].setMarkerColor(iTgt+1);
      gr_rat[iZh][iTgt].setLineColor(iTgt+1);
      gr_rat[iZh][iTgt].setMarkerSize(5);
      gr_rat[iZh][iTgt].setMarkerStyle(iTgt);
      gr_rat[iZh][iTgt].setTitleY("#eps_S_T/#eps_D");
      canRat[iZh].getPad().setTitle(zhCutsLabels[iZh]);
      canRat[iZh].getPad().setTitleFontSize(c2_title_size);
      canRatAll.cd(iZh);      
      canRatAll.getPad().setTitle(zhCutsLabels[iZh]);
      canRatAll.getPad().setTitleFontSize(c2_title_size);
      if(iTgt==1){
        canRat[iZh].draw(gr_rat[iZh][iTgt]);
        canRatAll.cd(iZh).draw(gr_rat[iZh][iTgt]);
      }else{
        canRat[iZh].draw(gr_rat[iZh][iTgt],"same");
        canRatAll.cd(iZh).draw(gr_rat[iZh][iTgt],"same");
      }
    }
  }
}
