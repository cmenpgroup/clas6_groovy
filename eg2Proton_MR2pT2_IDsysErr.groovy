import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;
import org.jlab.jnp.pdg.PDGDatabase;
import org.jlab.jnp.pdg.PDGParticle;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

GStyle.getAxisAttributesX().setTitleFontSize(22);
GStyle.getAxisAttributesY().setTitleFontSize(22);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

Double ratio, err, relErrSq;

OptionParser p = new OptionParser("eg2Proton_MR2pT2_IDsysErr.groovy");

p.parse(args);

def cutList = [];
if(p.getInputList().size()>1){
    cutList = p.getInputList();
}else{
    System.out.println("*** Wrong number of inputs.  At least 2 ID cuts needed. ***")
    p.printUsage();
    System.exit(0);
}

println cutList;

HistInfo myHI = new HistInfo();
List<String> solidTgt = myHI.getSolidTgtLabel();

YieldsForMR2pT2 myMR = new YieldsForMR2pT2();
List<String> zhCuts = myMR.getZhCuts();
List<String> zhCutsLabels = myMR.getZhCutsLabels();

TCanvas can = new TCanvas("can",900,900);
can.divide(3,3);

TCanvas canSys = new TCanvas("canSys",900,900);
canSys.divide(3,3);

int c1_title_size = 22;

TDirectory[][] dir = new TDirectory[solidTgt.size()][cutList.size()];
cutList.eachWithIndex { nCut, iCut ->
  solidTgt.eachWithIndex { nTgt, iTgt ->
    String path = "MR2pT2/";
    String fileName = path + "eg2Proton_MR2pT2_corr_hists_" + nTgt + "_" + nCut + ".hipo";
    println fileName;
    dir[iTgt][iCut] = new TDirectory();
    dir[iTgt][iCut].readFile(fileName);
  }
}

File csvFile = new File("eg2Proton_MR2pT2_IDsysErr_"+cutList[1]+".csv");

GraphErrors gr_mr = new GraphErrors();
GraphErrors gr_sysErr= new GraphErrors();

def stdMarkerStyle = [0,1,2];
def stdMarkerColor = [1,2,3];
def cutMarkerStyle = [3,3,3];

def yMin = [0.85,0.85,0.85,0.85,0.55,0.55,0.50,0.50,0.50];
def yMax = [1.2,1.2,1.2,1.2,1.5,1.5,2.0,2.0,2.0];

zhCuts.eachWithIndex { nZh, iZh->
  can.cd(iZh);
  can.getPad().setTitleFontSize(c1_title_size);
//  can.getPad().getAxisY().setRange(0.0,2.0);

  canSys.cd(iZh);
  canSys.getPad().getAxisY().setRange(yMin[iZh],yMax[iZh]);

  String grcorr = "gr_mrProtonCorr_pT2_" + iZh;

  solidTgt.eachWithIndex { nTgt, iTgt ->
    DataVector denomX = new DataVector();
    DataVector denomY = new DataVector();
    DataVector denomYerr = new DataVector();
    cutList.eachWithIndex { nCut, iCut ->
      gr_mr = new GraphErrors();
      gr_mr = dir[iTgt][iCut].getObject("MR2pT2/",grcorr);
      gr_mr.setTitleX("pT^2 (GeV^2)");
      gr_mr.setTitleY("R_p");

      if(iCut==0){
        gr_mr.setMarkerStyle(stdMarkerStyle[iTgt]);
      }else{
        gr_mr.setMarkerStyle(cutMarkerStyle[iTgt]);
      }
      gr_mr.setMarkerColor(stdMarkerColor[iTgt]+iCut*solidTgt.size());
      gr_mr.setLineColor(stdMarkerColor[iTgt]+iCut*solidTgt.size());
      gr_mr.setMarkerSize(5);

      can.getPad().setTitle(zhCutsLabels[iZh]);
      if(iTgt==0 && iCut==0){
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
        gr_sysErr.setTitleY("R_p(cut)/R_p(std)");
        gr_sysErr.setMarkerColor(stdMarkerColor[iTgt]+iCut*solidTgt.size());
        gr_sysErr.setLineColor(stdMarkerColor[iTgt]+iCut*solidTgt.size());
        gr_sysErr.setMarkerSize(5);
        gr_sysErr.setMarkerStyle(stdMarkerStyle[iTgt]);

        canSys.getPad().setTitleFontSize(c1_title_size);
        canSys.getPad().setTitle(zhCutsLabels[iZh]);
        if(iTgt==0){
          canSys.draw(gr_sysErr);
        }else{
          canSys.draw(gr_sysErr,"same");
        }

        double sysDiff = 1.0 - gr_sysErr.getVectorY().getMean();
        csvFile << nZh + "," + nTgt + "," + nCut + "," + gr_sysErr.getVectorY().getMean() + "," +sysDiff + "\n";

      }
    }
  }
}
