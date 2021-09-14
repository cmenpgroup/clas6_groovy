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

def cli = new CliBuilder(usage:'eg2Proton_MR2pT2_comp.groovy')
cli.h(longOpt:'help', 'Print this message.')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

HistInfo myHI = new HistInfo();
List<String> solidTgt = myHI.getSolidTgtLabel();

YieldsForMR2pT2 myMR = new YieldsForMR2pT2();
List<String> zhCuts = myMR.getZhCuts();

TCanvas can = new TCanvas("can",900,900);
can.divide(3,3);
int c1_title_size = 22;

TDirectory[] dir = new TDirectory[solidTgt.size()];
solidTgt.eachWithIndex { nTgt, iTgt ->
  String fileName = "eg2Proton_MR2pT2_corr_hists_" + nTgt + ".hipo";
  println fileName;
  dir[iTgt] = new TDirectory();
  dir[iTgt].readFile(fileName);
}

GraphErrors[][] gr_mrProtonCorr = new GraphErrors[zhCuts.size()][solidTgt.size()];

zhCuts.eachWithIndex { nZh, iZh->
  can.cd(iZh);
  can.getPad().setTitleFontSize(c1_title_size);

  String grcorr = "gr_mrProtonCorr_pT2_" + iZh;

  solidTgt.eachWithIndex { nTgt, iTgt ->
    gr_mrProtonCorr[iZh][iTgt] = dir[iTgt].getObject("MR2pT2/",grcorr);
    gr_mrProtonCorr[iZh][iTgt].setTitleX("pT^2 (GeV^2)");
    gr_mrProtonCorr[iZh][iTgt].setTitleY("R^p");
    gr_mrProtonCorr[iZh][iTgt].setMarkerColor(iTgt+1);
    gr_mrProtonCorr[iZh][iTgt].setLineColor(iTgt+1);
    gr_mrProtonCorr[iZh][iTgt].setMarkerSize(5);
    if(iTgt==0){
      can.draw(gr_mrProtonCorr[iZh][iTgt]);
    }else{
      can.draw(gr_mrProtonCorr[iZh][iTgt],"same");
    }
  }
}

TCanvas canAcc = new TCanvas("canAcc",900,900);
canAcc.divide(3,3);
GraphErrors[][] grAcc = new GraphErrors[zhCuts.size()][solidTgt.size()];

zhCuts.eachWithIndex { nZh, iZh->
  canAcc.cd(iZh);
  canAcc.getPad().setTitleFontSize(c1_title_size);

  String grAccRatio = "gr_acc_" + iZh;

  solidTgt.eachWithIndex { nTgt, iTgt ->
    grAcc[iZh][iTgt] = dir[iTgt].getObject("MR2pT2/",grAccRatio);
    grAcc[iZh][iTgt].setTitleX("pT^2 (GeV^2)");
    grAcc[iZh][iTgt].setTitleY("R^p");
    grAcc[iZh][iTgt].setMarkerColor(iTgt+1);
    grAcc[iZh][iTgt].setLineColor(iTgt+1);
    grAcc[iZh][iTgt].setMarkerSize(5);
    if(iTgt==0){
      canAcc.draw(grAcc[iZh][iTgt]);
    }else{
      canAcc.draw(grAcc[iZh][iTgt],"same");
    }
  }
}
