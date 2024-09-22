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
List<String> Var = myHI.getVariables();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<String> sigmaLabel = myHI.getSigmaLabel();

YieldsForMR3zh myMR = new YieldsForMR3zh();
List<String> Q2Cuts = myMR.getQ2Cuts();
List<String> nuCuts = myMR.getNuCuts();
List<String> Q2CutsLabels = myMR.getQ2CutsLabels();
List<String> nuCutsLabels = myMR.getNuCutsLabels();
String xLabel = myMR.getXlabel();

solidTgt.add(0,"D"); // add the D target to the List
println solidTgt;

String[] accType = ["gen","rec","acc"];
H1F[][][] h1_acc = new H1F[Q2Cuts.size()*nuCuts.size()][accType.size()][solidTgt.size()];
GraphErrors[][] gr_rat = new GraphErrors[Q2Cuts.size()*nuCuts.size()][solidTgt.size()];
GraphErrors[][] gr_acc = new GraphErrors[Q2Cuts.size()*nuCuts.size()][solidTgt.size()];

String nSig = "std";
// create a TDirectory objects and read in the histogram file
TDirectory dir = new TDirectory();
dir.readFile("acc_ratio_hists/MR3zh/acc_ratio_hists_MR3zh_allTgts_sig" + nSig + ".hipo");

int c1_title_size = 18;
int c2_title_size = 24;
int c3_title_size = 18;
TCanvas[] can = new TCanvas[Q2Cuts.size()*nuCuts.size()];
TCanvas[] canAcc = new TCanvas[Q2Cuts.size()*nuCuts.size()];
TCanvas[] canRat = new TCanvas[Q2Cuts.size()*nuCuts.size()];
TCanvas canAccAll = new TCanvas("canAccAll",1400,1000);
canAccAll.divide(3,3);
TCanvas canRatAll = new TCanvas("canRatAll",1400,1000);
canRatAll.divide(3,3);

Q2Cuts.eachWithIndex { nQ2, iQ2->
  nuCuts.eachWithIndex { nNu, iNu->
    hNum = iQ2*Q2Cuts.size() + iNu;

    String cAccName = "canAcc_" + iQ2 + iNu;
    canAcc[hNum] = new TCanvas(cAccName,600,600);

    String cRatName = "canRat_" + iQ2 + iNu;
    canRat[hNum] = new TCanvas(cRatName,600,600);

    String cname = "can_" + iQ2 + iNu;
    can[hNum] = new TCanvas(cname,1200,900);
    can[hNum].divide(3,4);

    String binTitle = Q2CutsLabels[iQ2] + " , " + nuCutsLabels[iNu];

    int iAccCtr = 0;
    solidTgt.eachWithIndex {nTgt, iTgt->
      accType.eachWithIndex {nAcc, iAcc->
        String hname = nAcc + nTgt + "_MR3zh_" + iQ2 + iNu;
        h1_acc[hNum][iAcc][iTgt] = dir.getObject("Qsq"+iQ2+"_nu"+iNu+"/",hname);
        can[hNum].getPad().setTitleFontSize(c1_title_size);
        can[hNum].cd(iAccCtr).draw(h1_acc[hNum][iAcc][iTgt]);
        iAccCtr++;
      }

      canAcc[hNum].cd(0);
      gr_acc[hNum][iTgt] = dir.getObject("Qsq"+iQ2+"_nu"+iNu+"/","gr_acc" + nTgt + "_" + iQ2 + iNu);
      gr_acc[hNum][iTgt].setMarkerColor(iTgt+1);
      gr_acc[hNum][iTgt].setLineColor(iTgt+1);
      gr_acc[hNum][iTgt].setMarkerSize(5);
      gr_acc[hNum][iTgt].setMarkerStyle(iTgt);
      canAcc[hNum].getPad().setTitle(binTitle);
      canAcc[hNum].getPad().setTitleFontSize(c2_title_size);
      canAccAll.cd(hNum);
      canAccAll.getPad().setTitle(binTitle);
      canAccAll.getPad().setTitleFontSize(c3_title_size);
      if(iTgt==0){
        canAcc[hNum].draw(gr_acc[hNum][iTgt]);
        canAccAll.cd(hNum).draw(gr_acc[hNum][iTgt]);
      }else{
        canAcc[hNum].draw(gr_acc[hNum][iTgt],"same");
        canAccAll.cd(hNum).draw(gr_acc[hNum][iTgt],"same");
      }

      if(iTgt>0){
        canRat[hNum].cd(0);
        gr_rat[hNum][iTgt] = dir.getObject("Qsq"+iQ2+"_nu"+iNu+"/","gr_rat" + nTgt + "_" + iQ2 + iNu);
        gr_rat[hNum][iTgt].setMarkerColor(iTgt+1);
        gr_rat[hNum][iTgt].setLineColor(iTgt+1);
        gr_rat[hNum][iTgt].setMarkerSize(5);
        gr_rat[hNum][iTgt].setMarkerStyle(iTgt);
        gr_rat[hNum][iTgt].setTitleY("#eps_S_T/#eps_D");
        canRat[hNum].getPad().setTitle(binTitle);
        canRat[hNum].getPad().setTitleFontSize(c2_title_size);
        canRatAll.cd(hNum);
        canRatAll.getPad().setTitle(binTitle);
        canRatAll.getPad().setTitleFontSize(c3_title_size);
        if(iTgt==1){
          canRat[hNum].cd(iTgt).draw(gr_rat[hNum][iTgt]);
          canRatAll.cd(hNum).draw(gr_rat[hNum][iTgt]);
        }else{
          canRat[hNum].cd(iTgt).draw(gr_rat[hNum][iTgt],"same");
          canRatAll.cd(hNum).draw(gr_rat[hNum][iTgt],"same");
        }
      }
    }
  }
}
