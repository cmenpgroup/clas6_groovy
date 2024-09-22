import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;
import org.jlab.jnp.pdg.PDGDatabase;
import org.jlab.jnp.pdg.PDGParticle;

import groovy.cli.commons.CliBuilder;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

Double ratio, err, relErrSq;

def cli = new CliBuilder(usage:'eg2Proton_MR3zh_IDsysErr.groovy [options] cut1 cut2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.L(longOpt:'log', 'Set log scale')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

boolean setLog = false;
if(options.L) setLog = true;

def cutList = [];
extraArguments.each { input ->
  cutList.add(input);
}

if(cutList.size()<2){
  println "Too few cuts: " + cutList.size() + "\n";
  cli.usage();
  return;
}

println cutList;

HistInfo myHI = new HistInfo();
List<String> solidTgt = myHI.getSolidTgtLabel();

YieldsForMR3zh myMR = new YieldsForMR3zh();
List<String> Q2Cuts = myMR.getQ2Cuts();
List<String> nuCuts = myMR.getNuCuts();

TCanvas can = new TCanvas("can",900,900);
can.divide(Q2Cuts.size(),nuCuts.size());
int c1_title_size = 22;
int canCount = 0;

TDirectory[][] dir = new TDirectory[solidTgt.size()][cutList.size()];
cutList.eachWithIndex { nCut, iCut ->
  solidTgt.eachWithIndex { nTgt, iTgt ->
    String fileName = "eg2Proton_MR3zh_corr_hists_" + nTgt + "_" + nCut + ".hipo";
    println fileName;
    dir[iTgt][iCut] = new TDirectory();
    dir[iTgt][iCut].readFile(fileName);
  }
}

GraphErrors gr_mr = new GraphErrors();
GraphErrors gr_mrTgt = new GraphErrors();
GraphErrors gr_sysErr= new GraphErrors();

int ctr;

nuCuts.eachWithIndex { nNu, iNu->
  Q2Cuts.eachWithIndex { nQ2, iQ2->
    can.cd(canCount);
    can.getPad().setTitleFontSize(c1_title_size);
    if(setLog) can.getPad().getAxisY().setLog(true);

    String grcorr = "gr_mrProtonCorr_" + iQ2 + iNu;

    solidTgt.eachWithIndex { nTgt, iTgt ->
      gr_mr = dir[iTgt].getObject("MR3zh/",grcorr);
      gr_mr.setTitleX("z_h");
      gr_mr.setTitleY("R^p");
      gr_mr.setMarkerColor(iTgt+1);
      gr_mr.setLineColor(iTgt+1);
      gr_mr.setMarkerSize(5);
      gr_mr.setMarkerStyle(iTgt);
      if(iTgt==0){
        can.draw(gr_mr);
      }else{
        can.draw(gr_mr,"same");
      }
    }
    canCount++;
  }
}

canCount = 0;
TCanvas canTgt = new TCanvas("canTgt",900,900);
canTgt.divide(Q2Cuts.size(),solidTgt.size());

Q2Cuts.eachWithIndex { nQ2, iQ2->
  solidTgt.eachWithIndex { nTgt, iTgt ->
    canTgt.cd(canCount);
    canTgt.getPad().setTitleFontSize(c1_title_size);
    if(setLog) canTgt.getPad().getAxisY().setLog(true);

    nuCuts.eachWithIndex { nNu, iNu->
      gr_mrTgt = new GraphErrors("gr_copy_"+iQ2+iNu+iTgt);
      gr_mrTgt.copy(gr_mr);
      gr_mrTgt.setTitleX("z_h");
      gr_mrTgt.setTitleY("R^p");
      gr_mrTgt.setMarkerColor(iNu+1);
      gr_mrTgt.setLineColor(iNu+1);
      gr_mrTgt.setMarkerSize(5);
      gr_mrTgt.setMarkerStyle(iNu);
      if(iNu==0){
        canTgt.draw(gr_mrTgt);
      }else{
        canTgt.draw(gr_mrTgt,"same");
      }
    }
    canCount++;
  }
}
