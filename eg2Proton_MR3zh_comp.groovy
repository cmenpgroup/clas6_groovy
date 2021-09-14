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

def cli = new CliBuilder(usage:'eg2Proton_MR3zh_comp.groovy')
cli.h(longOpt:'help', 'Print this message.')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

HistInfo myHI = new HistInfo();
List<String> solidTgt = myHI.getSolidTgtLabel();

YieldsForMR3zh myMR = new YieldsForMR3zh();
List<String> Q2Cuts = myMR.getQ2Cuts();
List<String> nuCuts = myMR.getNuCuts();

TCanvas can = new TCanvas("can",900,900);
can.divide(Q2Cuts.size(),nuCuts.size());
int c1_title_size = 22;
int canCount = 0;

TDirectory[] dir = new TDirectory[solidTgt.size()];
solidTgt.eachWithIndex { nTgt, iTgt ->
  String fileName = "eg2Proton_MR3zh_corr_hists_" + nTgt + ".hipo";
  println fileName;
  dir[iTgt] = new TDirectory();
  dir[iTgt].readFile(fileName);
}

GraphErrors[][][] gr_mrProtonCorr = new GraphErrors[Q2Cuts.size()][nuCuts.size()][solidTgt.size()];

Q2Cuts.eachWithIndex { nQ2, iQ2->
  nuCuts.eachWithIndex { nNu, iNu->
    can.cd(canCount);
    can.getPad().setTitleFontSize(c1_title_size);

    String grcorr = "gr_mrProtonCorr_" + iQ2 + iNu;

    solidTgt.eachWithIndex { nTgt, iTgt ->
      gr_mrProtonCorr[iQ2][iNu][iTgt] = dir[iTgt].getObject("MR3zh/",grcorr);
      gr_mrProtonCorr[iQ2][iNu][iTgt].setTitleX("z_h");
      gr_mrProtonCorr[iQ2][iNu][iTgt].setTitleY("R^p");
      gr_mrProtonCorr[iQ2][iNu][iTgt].setMarkerColor(iTgt+1);
      gr_mrProtonCorr[iQ2][iNu][iTgt].setLineColor(iTgt+1);
      gr_mrProtonCorr[iQ2][iNu][iTgt].setMarkerSize(5);
      if(iTgt==0){
        can.draw(gr_mrProtonCorr[iQ2][iNu][iTgt]);
      }else{
        can.draw(gr_mrProtonCorr[iQ2][iNu][iTgt],"same");
      }
    }
    canCount++;
  }
}

canCount = 0;
TCanvas canAcc = new TCanvas("canAcc",900,900);
canAcc.divide(Q2Cuts.size(),nuCuts.size());
GraphErrors[][][] grAcc = new GraphErrors[Q2Cuts.size()][nuCuts.size()][solidTgt.size()];

Q2Cuts.eachWithIndex { nQ2, iQ2->
  nuCuts.eachWithIndex { nNu, iNu->
    canAcc.cd(canCount);
    canAcc.getPad().setTitleFontSize(c1_title_size);

    String grAccRatio = "gr_acc_" + iQ2 + iNu;

    solidTgt.eachWithIndex { nTgt, iTgt ->
      grAcc[iQ2][iNu][iTgt] = dir[iTgt].getObject("MR3zh/",grAccRatio);
      grAcc[iQ2][iNu][iTgt].setTitleX("z_h");
      grAcc[iQ2][iNu][iTgt].setTitleY("Acceptance Ratio");
      grAcc[iQ2][iNu][iTgt].setMarkerColor(iTgt+1);
      grAcc[iQ2][iNu][iTgt].setLineColor(iTgt+1);
      grAcc[iQ2][iNu][iTgt].setMarkerSize(5);
      if(iTgt==0){
        canAcc.draw(grAcc[iQ2][iNu][iTgt]);
      }else{
        canAcc.draw(grAcc[iQ2][iNu][iTgt],"same");
      }
    }
    canCount++;
  }
}
