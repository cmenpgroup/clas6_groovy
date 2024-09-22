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

OptionParser p = new OptionParser("eg2Proton_MR3zh_IDsysErr.groovy");
int setLog = 0;
p.addOption("-L",Integer.toString(setLog),"Set the y-axis to log scale.");

p.parse(args);
setLog = p.getOption("-L").intValue();

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

YieldsForMR3zh myMR = new YieldsForMR3zh();
List<String> Q2Cuts = myMR.getQ2Cuts();
List<String> nuCuts = myMR.getNuCuts();
List<String> Q2CutsLabels = myMR.getQ2CutsLabels();
List<String> nuCutsLabels = myMR.getNuCutsLabels();

TCanvas can = new TCanvas("can",1200,900);
can.divide(Q2Cuts.size(),nuCuts.size());
TCanvas canSys = new TCanvas("canSys",1200,900);
canSys.divide(Q2Cuts.size(),nuCuts.size());

int c1_title_size = 18;
int canCount = 0;

TDirectory[][] dir = new TDirectory[solidTgt.size()][cutList.size()];
cutList.eachWithIndex { nCut, iCut ->
  solidTgt.eachWithIndex { nTgt, iTgt ->
    String path = "MR3zh/";
    String fileName = path + "eg2Proton_MR3zh_corr_hists_" + nTgt + "_" + nCut + ".hipo";
    println fileName;
    dir[iTgt][iCut] = new TDirectory();
    dir[iTgt][iCut].readFile(fileName);
  }
}

File csvFile = new File("eg2Proton_MR3zh_IDsysErr_"+cutList[1]+".csv");

GraphErrors gr_mr = new GraphErrors();
GraphErrors gr_mrTgt = new GraphErrors();
GraphErrors gr_sysErr= new GraphErrors();

def stdMarkerStyle = [0,1,2];
def stdMarkerColor = [1,2,3];
def cutMarkerStyle = [3,3,3];

String binTitle;

nuCuts.eachWithIndex { nNu, iNu->
  Q2Cuts.eachWithIndex { nQ2, iQ2->
    can.cd(canCount);
    can.getPad().setTitleFontSize(c1_title_size);
    if(setLog) can.getPad().getAxisY().setLog(true);

    canSys.cd(canCount);
    canSys.getPad().setTitleFontSize(c1_title_size);
    canSys.getPad().getAxisY().setRange(0.8,1.2);

    String grcorr = "gr_mrProtonCorr_" + iQ2 + iNu;

    binTitle = Q2CutsLabels[iQ2] + " , " + nuCutsLabels[iNu];
    solidTgt.eachWithIndex { nTgt, iTgt ->
      DataVector denomX = new DataVector();
      DataVector denomY = new DataVector();
      DataVector denomYerr = new DataVector();
      cutList.eachWithIndex { nCut, iCut ->
        gr_mr = new GraphErrors();
        gr_mr = dir[iTgt][iCut].getObject("MR3zh/",grcorr);
        gr_mr.setTitleX("z_h");
        gr_mr.setTitleY("R_p");

        if(iCut==0){
          gr_mr.setMarkerStyle(stdMarkerStyle[iTgt]);
        }else{
          gr_mr.setMarkerStyle(cutMarkerStyle[iTgt]);
        }
        gr_mr.setMarkerColor(stdMarkerColor[iTgt]+iCut*solidTgt.size());
        gr_mr.setLineColor(stdMarkerColor[iTgt]+iCut*solidTgt.size());
        gr_mr.setMarkerSize(5);

        can.getPad().setTitle(binTitle);
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
          gr_sysErr.setTitleX("z_h");
          gr_sysErr.setTitleY("R_p(cut)/R_p(std)");
          gr_sysErr.setMarkerColor(stdMarkerColor[iTgt]+iCut*solidTgt.size());
          gr_sysErr.setLineColor(stdMarkerColor[iTgt]+iCut*solidTgt.size());
          gr_sysErr.setMarkerSize(5);
          gr_sysErr.setMarkerStyle(stdMarkerStyle[iTgt]);

          canSys.getPad().setTitleFontSize(c1_title_size);
          canSys.getPad().setTitle(binTitle);
          if(iTgt==0){
            canSys.draw(gr_sysErr);
          }else{
            canSys.draw(gr_sysErr,"same");
          }

          double sysDiff = 1.0 - gr_sysErr.getVectorY().getMean();
          csvFile << binTitle + "," + nTgt + "," + nCut + "," + gr_sysErr.getVectorY().getMean() + "," +sysDiff + "\n";

        }
      }
    }
    canCount++;
  }
}

/*
canCount = 0;
TCanvas canTgt = new TCanvas("canTgt",1200,900);
canTgt.divide(Q2Cuts.size(),solidTgt.size());

Q2Cuts.eachWithIndex { nQ2, iQ2->
  solidTgt.eachWithIndex { nTgt, iTgt ->
    canTgt.cd(canCount);
    canTgt.getPad().setTitleFontSize(c1_title_size);
    if(setLog) canTgt.getPad().getAxisY().setLog(true);

    ctr = 0;
    binTitle = nTgt + ": " + Q2CutsLabels[iQ2];
    nuCuts.eachWithIndex { nNu, iNu->
      cutList.eachWithIndex { nCut, iCut ->
        gr_mrTgt = new GraphErrors("gr_copy_"+iQ2+iNu+iTgt);
        gr_mrTgt.copy(gr_mr);
        gr_mrTgt.setTitleX("z_h");
        gr_mrTgt.setTitleY("R_p");
        gr_mrTgt.setMarkerColor(ctr+1);
        gr_mrTgt.setLineColor(ctr+1);
        gr_mrTgt.setMarkerSize(5);
        gr_mrTgt.setMarkerStyle(ctr);
        can.getPad().setTitle(binTitle);
        if(iNu==0){
          canTgt.draw(gr_mrTgt);
        }else{
          canTgt.draw(gr_mrTgt,"same");
        }
        ctr++;
      }
    }
    canCount++;
  }
}
*/
