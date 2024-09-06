import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
//---- imports for PDG library
import org.jlab.clas.pdg.PhysicsConstants;
import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.pdg.PDGParticle;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

//import org.jlab.jnp.pdg.PhysicsConstants;
//import org.jlab.jnp.pdg.PDGDatabase;
//import org.jlab.jnp.pdg.PDGParticle;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.io.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

OptionParser p = new OptionParser("eg2Proton_MR_csv2graph.groovy");
p.parse(args);

HistInfo myHI = new HistInfo();
List<String> Var = myHI.getVariables();
List<String> xLabel = myHI.getXlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();

TDirectory[] dir = new TDirectory[solidTgt.size()];
solidTgt.eachWithIndex { nTgt, iTgt ->
  dir[iTgt] = new TDirectory();
}

int c1_title_size = 22;
TCanvas[] can = new TCanvas[Var.size()];
GraphErrors[][] gr_mrProtonCorr = new GraphErrors[Var.size()][solidTgt.size()];

Var.eachWithIndex { nVar, iVar->
  String cname = "can" + iVar;
  can[iVar] = new TCanvas(cname,600,600);
  can[iVar].cd(0);
  can[iVar].getPad().setTitleFontSize(c1_title_size);

  solidTgt.eachWithIndex { nTgt, iTgt ->
    dir[iTgt].mkdir(nVar);
    dir[iTgt].cd(nVar);
    String grcorr = "gr_mrProtonCorr_" + nVar;
    String fileName = "MR1D/graph2txt/" + grcorr + "_" + nTgt + ".csv";
    gr_mrProtonCorr[iVar][iTgt] = new GraphErrors(grcorr).csvGraphXYEY(fileName,0,1,3,0);
    gr_mrProtonCorr[iVar][iTgt].setTitleX(xLabel[iVar]);
    gr_mrProtonCorr[iVar][iTgt].setTitleY("R^p");
    gr_mrProtonCorr[iVar][iTgt].setMarkerColor(iTgt+1);
    gr_mrProtonCorr[iVar][iTgt].setLineColor(iTgt+1);
    gr_mrProtonCorr[iVar][iTgt].setMarkerSize(5);
    gr_mrProtonCorr[iVar][iTgt].setMarkerStyle(iTgt);
    gr_mrProtonCorr[iVar][iTgt].setName(grcorr);
    if(iTgt==0){
      can[iVar].draw(gr_mrProtonCorr[iVar][iTgt]);
    }else{
      can[iVar].draw(gr_mrProtonCorr[iVar][iTgt],"same");
    }
    dir[iTgt].addDataSet(gr_mrProtonCorr[iVar][iTgt]); // add to the histogram file
  }
}

TCanvas[] canUnCorr = new TCanvas[Var.size()];
GraphErrors[][] gr_mrProton = new GraphErrors[Var.size()][solidTgt.size()];

Var.eachWithIndex { nVar, iVar->
  String cname = "canUnCorr" + iVar;
  canUnCorr[iVar] = new TCanvas(cname,600,600);
  canUnCorr[iVar].cd(0);
  canUnCorr[iVar].getPad().setTitleFontSize(c1_title_size);

  solidTgt.eachWithIndex { nTgt, iTgt ->
    dir[iTgt].mkdir(nVar);
    dir[iTgt].cd(nVar);
    String grUnCorr = "gr_mrProton_" + nVar;
    String fileName = "MR1D/graph2txt/" + grUnCorr + "_" + nTgt + ".csv";
    gr_mrProton[iVar][iTgt] = new GraphErrors(grUnCorr).csvGraphXYEY(fileName,0,1,3,0);
    gr_mrProton[iVar][iTgt].setTitleX(xLabel[iVar]);
    gr_mrProton[iVar][iTgt].setTitleY("R^p (uncorrected)");
    gr_mrProton[iVar][iTgt].setMarkerColor(iTgt+1);
    gr_mrProton[iVar][iTgt].setLineColor(iTgt+1);
    gr_mrProton[iVar][iTgt].setMarkerSize(5);
    gr_mrProton[iVar][iTgt].setMarkerStyle(iTgt);
    gr_mrProton[iVar][iTgt].setName(grcorr);
    if(iTgt==0){
      can[iVar].draw(gr_mrProton[iVar][iTgt]);
    }else{
      can[iVar].draw(gr_mrProton[iVar][iTgt],"same");
    }
    dir[iTgt].addDataSet(gr_mrProton[iVar][iTgt]); // add to the histogram file
  }
}

TCanvas[] canAcc = new TCanvas[Var.size()];
GraphErrors[][] grAcc = new GraphErrors[Var.size()][solidTgt.size()];

Var.eachWithIndex { nVar, iVar->
  String cname = "canAcc" + iVar;
  canAcc[iVar] = new TCanvas(cname,600,600);
  canAcc[iVar].cd(0);
  canAcc[iVar].getPad().setTitleFontSize(c1_title_size);

  solidTgt.eachWithIndex { nTgt, iTgt ->
    dir[iTgt].cd(nVar);
    String grcorr = "gr_acc_" + nVar;
    String fileName = "MR1D/graph2txt/" + grcorr + "_" + nTgt + ".csv";
    grAcc[iVar][iTgt] = new GraphErrors(grcorr).csvGraphXYEY(fileName,0,1,3,0);
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
    dir[iTgt].addDataSet(grAcc[iVar][iTgt]); // add to the histogram file
  }
}

solidTgt.eachWithIndex { nTgt, iTgt ->
  String outFile = "eg2Proton_MR_corr_hists_" + nTgt + "_std_csv.hipo"
  dir[iTgt].writeFile(outFile); // write the histograms to the file
}
