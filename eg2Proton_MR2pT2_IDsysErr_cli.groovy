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

import groovy.cli.commons.CliBuilder;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

Double ratio, err, relErrSq;

def cli = new CliBuilder(usage:'eg2Proton_MR2pT2_IDsysErr.groovy [options] cut1 cut2 ...')
cli.h(longOpt:'help', 'Print this message.')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def extraArguments = options.arguments()
if (extraArguments.isEmpty()){
  println "No input path!";
  cli.usage();
  return;
}

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

YieldsForMR2pT2 myMR = new YieldsForMR2pT2();
List<String> zhCuts = myMR.getZhCuts();

TCanvas can = new TCanvas("can",900,900);
can.divide(3,3);

TCanvas canSys = new TCanvas("canSys",900,900);
canSys.divide(3,3);

int c1_title_size = 22;

TDirectory[][] dir = new TDirectory[solidTgt.size()][cutList.size()];
cutList.eachWithIndex { nCut, iCut ->
  solidTgt.eachWithIndex { nTgt, iTgt ->
    String fileName = "eg2Proton_MR2pT2_corr_hists_" + nTgt + "_" + nCut + ".hipo";
    println fileName;
    dir[iTgt][iCut] = new TDirectory();
    dir[iTgt][iCut].readFile(fileName);
  }
}

GraphErrors gr_mr = new GraphErrors();
GraphErrors gr_sysErr= new GraphErrors();

int ctr;

zhCuts.eachWithIndex { nZh, iZh->
  can.cd(iZh);
  can.getPad().setTitleFontSize(c1_title_size);
  can.getPad().getAxisY().setRange(0.0,2.0);

  canSys.cd(iZh);
  canSys.getPad().getAxisY().setRange(0.85,1.2);

  String grcorr = "gr_mrProtonCorr_pT2_" + iZh;

  ctr = 0;
  solidTgt.eachWithIndex { nTgt, iTgt ->
    DataVector denomX = new DataVector();
    DataVector denomY = new DataVector();
    DataVector denomYerr = new DataVector();
    cutList.eachWithIndex { nCut, iCut ->
      gr_mr = new GraphErrors();
      gr_mr = dir[iTgt][iCut].getObject("MR2pT2/",grcorr);
      gr_mr.setTitleX("pT^2 (GeV^2)");
      gr_mr.setTitleY("R^p");
      gr_mr.setMarkerColor(ctr+1);
      gr_mr.setLineColor(ctr+1);
      gr_mr.setMarkerSize(5);
      gr_mr.setMarkerStyle(ctr);

      if(ctr==0){
        can.draw(gr_mr);
      }else{
        can.draw(gr_mr,"same");
      }

      gr_sysErr = new GraphErrors();
      if(iCut==0){
        denomX = gr_mr.getVectorX();
        denomY = gr_mr.getVectorY();
        for(int j=0; j<gr_mr.getDataSize(0);j++){
          denomYerr.add(gr_mr.getDataEY(j));
        }
      }else{
        for(int i=0; i<gr_mr.getDataSize(0);i++){
          if(gr_mr.getDataEY(i)>0.0 && denomYerr.getValue(i)>0.0 && denomY.getValue(i)>0.0){
            ratio = gr_mr.getDataY(i)/denomY.getValue(i);
            relErrSq = (gr_mr.getDataEY(i)/gr_mr.getDataY(i))**2 + (denomYerr.getValue(i)/denomY.getValue(i))**2;
            err = Math.abs(ratio)*Math.sqrt(relErrSq);
            gr_sysErr.addPoint(gr_mr.getDataX(i),ratio,0.0,err);
          }
        }
        gr_sysErr.setTitleX("pT^2 (GeV^2)");
        gr_sysErr.setTitleY("R^p(cut)/R^p(std)");
        gr_sysErr.setMarkerColor(ctr);
        gr_sysErr.setLineColor(ctr);
        gr_sysErr.setMarkerSize(5);
        gr_sysErr.setMarkerStyle(ctr);

        if(ctr==0){
          canSys.draw(gr_sysErr);
        }else{
          canSys.draw(gr_sysErr,"same");
        }
      }
      ctr++;
    }
  }
}
