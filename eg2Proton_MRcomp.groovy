import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;
import org.jlab.jnp.pdg.PDGDatabase;
import org.jlab.jnp.pdg.PDGParticle;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

def cli = new CliBuilder(usage:'eg2Proton_MRcomp.groovy')
cli.h(longOpt:'help', 'Print this message.')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

HistInfo myHI = new HistInfo();
List<String> Var = myHI.getVariables();
List<String> xLabel = myHI.getXlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();

int c1_title_size = 22;
TCanvas[] can = new TCanvas[Var.size()];
TDirectory[] dir = new TDirectory[solidTgt.size()];
solidTgt.eachWithIndex { nTgt, iTgt ->
  String fileName = "eg2Proton_MRcorr_hists_" + nTgt + ".hipo";
  println fileName;
  dir[iTgt] = new TDirectory();
  dir[iTgt].readFile(fileName);
}

GraphErrors[][] gr_mrProtonCorr = new GraphErrors[Var.size()][solidTgt.size()];

Var.eachWithIndex { nVar, iVar->
  if(nVar!="phiPQ"){
    String cname = "can" + iVar;
    can[iVar] = new TCanvas(cname,600,600);
    can[iVar].cd(0);
    can[iVar].getPad().setTitleFontSize(c1_title_size);
//    if(nVar=="zh") can[iVar].getPad().getAxisY().setLog(true);

    solidTgt.eachWithIndex { nTgt, iTgt ->
      String grcorr = "gr_mrProtonCorr_" + nVar;
      gr_mrProtonCorr[iVar][iTgt] = dir[iTgt].getObject(nVar,grcorr);
      gr_mrProtonCorr[iVar][iTgt].setTitleX(xLabel[iVar]);
      gr_mrProtonCorr[iVar][iTgt].setTitleY("R^p");
      gr_mrProtonCorr[iVar][iTgt].setMarkerColor(iTgt+1);
      gr_mrProtonCorr[iVar][iTgt].setLineColor(iTgt+1);
      gr_mrProtonCorr[iVar][iTgt].setMarkerSize(5);
      if(iTgt==0){
        can[iVar].draw(gr_mrProtonCorr[iVar][iTgt]);
      }else{
        can[iVar].draw(gr_mrProtonCorr[iVar][iTgt],"same");
      }
    }
  }
}

TCanvas[] canAcc = new TCanvas[Var.size()];
GraphErrors[][] grAcc = new GraphErrors[Var.size()][solidTgt.size()];

Var.eachWithIndex { nVar, iVar->
  if(nVar!="phiPQ"){
    String cname = "canAcc" + iVar;
    canAcc[iVar] = new TCanvas(cname,600,600);
    canAcc[iVar].cd(0);
    canAcc[iVar].getPad().setTitleFontSize(c1_title_size);

    solidTgt.eachWithIndex { nTgt, iTgt ->
      String grcorr = "gr_acc_" + nVar;
      grAcc[iVar][iTgt] = dir[iTgt].getObject(nVar,grcorr);
      grAcc[iVar][iTgt].setTitleX(xLabel[iVar]);
      grAcc[iVar][iTgt].setTitleY("Acceptance Ratio");
      grAcc[iVar][iTgt].setMarkerColor(iTgt+1);
      grAcc[iVar][iTgt].setLineColor(iTgt+1);
      grAcc[iVar][iTgt].setMarkerSize(5);
      if(iTgt==0){
        canAcc[iVar].draw(grAcc[iVar][iTgt]);
      }else{
        canAcc[iVar].draw(grAcc[iVar][iTgt],"same");
      }
    }
  }
}
