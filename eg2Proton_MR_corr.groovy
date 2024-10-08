//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.base.GStyle
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;
import eg2Cuts.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

Double corrMR, errMR, relErrSq;

OptionParser p = new OptionParser("eg2Proton_MR_corr.groovy");

String outFile = "eg2Proton_MR_corrHists.hipo";
p.addOption("-o", outFile, "Output file name");
String userTgt = "C";
p.addOption("-s", userTgt, "Solid Target (C, Fe, Pb)");
String userSigmaCut = "2.0";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (1.0, 1.5, 2.0, 2.5, 3.0)");
int bGraph = 1;
p.addOption("-g",Integer.toString(bGraph), "Graph monitor histograms. (0=quiet)");

p.parse(args);
outFile = p.getOption("-o").stringValue();
userTgt = p.getOption("-s").stringValue();
userSigmaCut = p.getOption("-c").stringValue();
bGraph = p.getOption("-g").intValue();

String fileName;
if(p.getInputList().size()==1){
    fileName = p.getInputList().get(0);
}else{
    System.out.println("*** Wrong number of inputs.  Only one input file. ***")
    p.printUsage();
    System.exit(0);
}

TDirectory dirData = new TDirectory();
dirData.readFile(fileName);

HistInfo myHI = new HistInfo();
List<String> Var = myHI.getVariables();
List<String> xLabel = myHI.getXlabel();
List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<Double> xlo = myHI.getXlo();
List<Double> xhi = myHI.getXhi();
List<Integer> nbins = myHI.getNbins();

eg2Target myEG2Tgt = new eg2Target();
List<Double> normCorr = myEG2Tgt.Get_ElectronNorm_LiquidOverSolid();
List<Double> invNormCorr = myEG2Tgt.Get_ElectronNorm_SolidOverLiquid();

int indexTgt = solidTgt.indexOf(userTgt);
if(indexTgt==-1){
  println "Target " + userTgt + " is unavailable! Please choose one of the following:";
  println solidTgt;
  return;
}

String fileDIS = "DIScorr/eg2DIS_AnaTree_" + userTgt + "_Hists.hipo";
TDirectory dirNorm = new TDirectory();
dirNorm.readFile(fileDIS);

TDirectory dir = new TDirectory();

int c1_title_size = 22;
TCanvas[] can = new TCanvas[Var.size()];

GraphErrors[] gr_mrProton = new GraphErrors[Var.size()];
GraphErrors[] gr_mrProtonCorr = new GraphErrors[Var.size()];
GraphErrors[] gr_mrNorm = new GraphErrors[Var.size()];

DataVector normX = new DataVector();
DataVector normY = new DataVector();

// create a TDirectory objects and read in the histogram file
TDirectory dirAcc = new TDirectory();
dirAcc = new TDirectory();
dirAcc.readFile("acc_ratio_hists/MR/acc_ratio_hists_MR_allTgts_sig" + userSigmaCut + ".hipo");

Var.eachWithIndex { nVar, iVar->
    DataVector accX = new DataVector();
    DataVector accY = new DataVector();
    DataVector accYerr = new DataVector();

    String grAcc = "gr_acc_" + nVar;
    GraphErrors gr_acc = new GraphErrors(grAcc);
    gr_acc = dirAcc.getObject(nVar,"gr_rat" + userTgt + "_" + nVar);
    System.out.println(grAcc + " " + gr_acc.getDataSize(0) + " bins");

    accX = gr_acc.getVectorX();
    accY = gr_acc.getVectorY();
    for(int j=0; j<gr_acc.getDataSize(0);j++){
      accYerr.add(gr_acc.getDataEY(j));
    }

    if(nVar=="q2" || nVar=="nu"){
      String gr_eNorm = "gr_rDIS_" + nVar;
      gr_mrNorm[iVar] = dirNorm.getObject("multiplicity/",gr_eNorm);
      System.out.println(gr_eNorm + " " + gr_mrNorm[iVar].getDataSize(0) + " bins");
      normX = gr_mrNorm[iVar].getVectorX();
      normY = gr_mrNorm[iVar].getVectorY();
    }

    String grcorr = "gr_mrProtonCorr_" + nVar;
    GraphErrors gr_mrCorr = new GraphErrors(grcorr);
    String grname = "gr_mrProton_" + nVar;
    gr_mrProton[iVar] = dirData.getObject("multiplicity/",grname);
    System.out.println(grname + " " + gr_mrProton[iVar].getDataSize(0) + " bins");
    DataVector dataX = new DataVector();
    DataVector dataY = new DataVector();
    dataX = gr_mrProton[iVar].getVectorX();
    dataY = gr_mrProton[iVar].getVectorY();

    for(int i=0; i<gr_mrProton[iVar].getDataSize(0);i++){
      corrMR = dataY.getValue(i)/accY.getValue(i);
      if(nVar=="q2" || nVar=="nu"){
        corrMR = corrMR/normY.getValue(i);
      }else{
        corrMR = corrMR/invNormCorr[indexTgt];
      }

      if(gr_mrProton[iVar].getDataEY(i)>0.0 && accYerr.getValue(i)>0.0){
        relErrSq = (gr_mrProton[iVar].getDataEY(i)/dataY.getValue(i))**2 + (accYerr.getValue(i)/accY.getValue(i))**2
        if(nVar=="q2" || nVar=="nu") relErrSq += (gr_mrNorm[iVar].getDataEY(i)/normY.getValue(i))**2;
        errMR = Math.abs(corrMR)*Math.sqrt(relErrSq);
      }else{
        errMR = 0.0;
      }
      gr_mrCorr.addPoint(dataX.getValue(i),corrMR,0.0,errMR);
    }

    dir.mkdir(nVar);
    dir.cd(nVar);

    gr_acc.setTitleX(xLabel[iVar]);
    gr_acc.setTitleY("Acceptance Ratio");
    gr_acc.setMarkerColor(2);
    gr_acc.setLineColor(2);
    gr_acc.setMarkerSize(5);
    dir.addDataSet(gr_acc); // add to the histogram file

    gr_mrProton[iVar].setMarkerSize(5);
    dir.addDataSet(gr_mrProton[iVar]); // add to the histogram file

    gr_mrCorr.setTitleX(xLabel[iVar]);
    gr_mrCorr.setTitleY("R^p");
    gr_mrCorr.setMarkerColor(4);
    gr_mrCorr.setLineColor(4);
    gr_mrCorr.setMarkerSize(5);
    dir.addDataSet(gr_mrCorr); // add to the histogram file

    if(bGraph){
      String cname = "can" + iVar;
      can[iVar] = new TCanvas(cname,1000,500);
      can[iVar].divide(2,1);
      can[iVar].cd(0);
      can[iVar].getPad().setTitleFontSize(c1_title_size);
      can[iVar].draw(gr_acc);

      can[iVar].cd(1);
      can[iVar].getPad().setTitleFontSize(c1_title_size);
      can[iVar].draw(gr_mrProton[iVar]);
      can[iVar].draw(gr_mrCorr,"same");
    }
    dir.cd(); // return to the top directory
}

dir.writeFile(outFile);
