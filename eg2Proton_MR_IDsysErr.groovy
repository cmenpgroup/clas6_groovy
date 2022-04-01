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

def cli = new CliBuilder(usage:'eg2Proton_MR_IDsysErr.groovy [options] cut1 cut2 ...')
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
List<String> Var = myHI.getVariables();
List<String> xLabel = myHI.getXlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();

int c1_title_size = 22;
TCanvas[] can = new TCanvas[Var.size()];
TDirectory[][] dir = new TDirectory[solidTgt.size()][cutList.size()];
cutList.eachWithIndex { nCut, iCut ->
  solidTgt.eachWithIndex { nTgt, iTgt ->
    String fileName = "eg2Proton_MRcorr_hists_" + nTgt + "_" + nCut + ".hipo";
    println fileName;
    dir[iTgt][iCut] = new TDirectory();
    dir[iTgt][iCut].readFile(fileName);
  }
}

GraphErrors[][][] gr_mrProtonCorr = new GraphErrors[Var.size()][solidTgt.size()][cutList.size()];
GraphErrors[][][] gr_sysErr= new GraphErrors[Var.size()][solidTgt.size()][cutList.size()];

int ctr;

Var.eachWithIndex { nVar, iVar->
  if(nVar!="phiPQ"){
    String cname = "can" + iVar;
    can[iVar] = new TCanvas(cname,600,600);
    can[iVar].cd(0);
    can[iVar].getPad().setTitleFontSize(c1_title_size);
//    if(nVar=="zh") can[iVar].getPad().getAxisY().setLog(true);

    solidTgt.eachWithIndex { nTgt, iTgt ->
      ctr = 0;
      cutList.eachWithIndex { nCut, iCut ->
        String grcorr = "gr_mrProtonCorr_" + nVar;
        gr_mrProtonCorr[iVar][iTgt][iCut] = dir[iTgt][iCut].getObject(nVar,grcorr);
        gr_mrProtonCorr[iVar][iTgt][iCut].setTitleX(xLabel[iVar]);
        gr_mrProtonCorr[iVar][iTgt][iCut].setTitleY("R^p");
        gr_mrProtonCorr[iVar][iTgt][iCut].setMarkerColor(ctr+1);
        gr_mrProtonCorr[iVar][iTgt][iCut].setLineColor(ctr+1);
        gr_mrProtonCorr[iVar][iTgt][iCut].setMarkerSize(5);
        gr_mrProtonCorr[iVar][iTgt][iCut].setMarkerStyle(ctr);

        gr_sysErr[iVar][iTgt][iCut].copy(gr_mrProtonCorr[iVar][iTgt][iCut]);
        gr_sysErr[iVar][iTgt][iCut].divide(gr_mrProtonCorr[iVar][iTgt][0]);

        if(iCut==0){
          can[iVar].draw(gr_sysErr[iVar][iTgt][iCut]);
        }else{
          can[iVar].draw(gr_sysErr[iVar][iTgt][iCut],"same");
        }
      }
      ctr++;
    }
  }
}
