//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.base.GStyle;
import org.jlab.groot.math.*;
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
List<String> xLabel = myHI.getXlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<String> sigmaLabel = myHI.getSigmaLabel();

solidTgt.add(0,"D"); // add the D target to the List
println solidTgt;

String[] accType = ["gen","rec","acc"];
H1F[][][] h1_acc = new H1F[Var.size()][accType.size()][solidTgt.size()];
GraphErrors[][] gr_rat = new GraphErrors[Var.size()][solidTgt.size()];
GraphErrors[][] gr_acc = new GraphErrors[Var.size()][solidTgt.size()];

String nSig = "std";
// create a TDirectory objects and read in the histogram file
TDirectory dir = new TDirectory();
dir.readFile("acc_ratio_hists/MR/acc_ratio_hists_MR_allTgts_sig" + nSig + ".hipo");

int c1_title_size = 18;
TCanvas[] can = new TCanvas[Var.size()];
TCanvas[] canAcc = new TCanvas[Var.size()];
TCanvas[] canRat = new TCanvas[Var.size()];

Var.eachWithIndex { nVar, iVar->
  String cAccName = "canAcc_" + nVar;
  canAcc[iVar] = new TCanvas(cAccName,600,600);

  String cRatName = "canRat_" + nVar;
  canRat[iVar] = new TCanvas(cRatName,600,600);

  String cname = "can_" + nVar;
  can[iVar] = new TCanvas(cname,1200,900);
  can[iVar].divide(3,4);

  int iAccCtr = 0;
  solidTgt.eachWithIndex {nTgt, iTgt->
    accType.eachWithIndex {nAcc, iAcc->
      String hname = nAcc + nTgt + "_" + nVar;
      h1_acc[iVar][iAcc][iTgt] = dir.getObject(nVar,hname);
      can[iVar].getPad().setTitleFontSize(c1_title_size);
      can[iVar].cd(iAccCtr).draw(h1_acc[iVar][iAcc][iTgt]);
      iAccCtr++;
    }

    canAcc[iVar].cd(0);
    gr_acc[iVar][iTgt] = dir.getObject(nVar,"gr_acc" + nTgt + "_" + nVar);
    gr_acc[iVar][iTgt].setMarkerColor(iTgt+1);
    gr_acc[iVar][iTgt].setLineColor(iTgt+1);
    gr_acc[iVar][iTgt].setMarkerSize(5);
    gr_acc[iVar][iTgt].setMarkerStyle(iTgt);
    canAcc[iVar].getPad().setTitle(" ");
    canAcc[iVar].getPad().setTitleFontSize(c1_title_size);
    if(iTgt==0){
      canAcc[iVar].draw(gr_acc[iVar][iTgt]);
    }else{
      canAcc[iVar].draw(gr_acc[iVar][iTgt],"same");
    }

    if(iTgt>0){
      canRat[iVar].cd(0);
      gr_rat[iVar][iTgt] = dir.getObject(nVar,"gr_rat" + nTgt + "_" + nVar);
      gr_rat[iVar][iTgt].setMarkerColor(iTgt+1);
      gr_rat[iVar][iTgt].setLineColor(iTgt+1);
      gr_rat[iVar][iTgt].setMarkerSize(5);
      gr_rat[iVar][iTgt].setMarkerStyle(iTgt);
      gr_rat[iVar][iTgt].setTitleY("#eps_S_T/#eps_D");
      canRat[iVar].getPad().setTitle(" ");
      canRat[iVar].getPad().setTitleFontSize(c1_title_size);
      if(iTgt==1){
        canRat[iVar].cd(iTgt).draw(gr_rat[iVar][iTgt]);
        }else{
        canRat[iVar].cd(iTgt).draw(gr_rat[iVar][iTgt],"same");
      }
    }
  }
}
