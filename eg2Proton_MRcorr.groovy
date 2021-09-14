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

Double corrMR, errMR;
Double x, y, err;
Double relErrSq;
String[] str;

def cli = new CliBuilder(usage:'eg2Proton_MRcorr.groovy [options] infile1')
cli.h(longOpt:'help', 'Print this message.')
cli.o(longOpt:'output', args:1, argName:'Histogram output file', type: String, 'Output file name')
cli.s(longOpt:'solid', args:1, argName:'Solid Target', type: String, 'Solid Target (C, Fe, Pb)')
cli.p(longOpt:'path', args:1, argName:'Acceptance Path', type: String, 'Acceptance Text Files Path')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def outFile = "eg2Proton_MRcorr_hists.hipo";
if(options.o) outFile = options.o;

def pathName = ".";
if(options.p) pathName = options.p;

String userTgt = "C";
if(options.s) userTgt = options.s;

def extraArguments = options.arguments()
if (extraArguments.isEmpty()){
  println "No input path!";
  cli.usage();
  return;
}

String fileName;
extraArguments.each { input ->
  fileName = input;
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

int indexTgt = solidTgt.indexOf(userTgt);
if(indexTgt==-1){
  println "Target " + userTgt + " is unavailable! Please choose one of the following:";
  println solidTgt;
  return;
}

String fileDIS = "eg2DIS_AnaTree_" + userTgt + "_Hists.hipo";
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

Var.eachWithIndex { nVar, iVar->
  if(nVar!="phiPQ"){
    DataVector accX = new DataVector();
    DataVector accY = new DataVector();
    DataVector accYerr = new DataVector();
    String grAcc = "gr_acc_" + nVar;
    GraphErrors gr_acc = new GraphErrors(grAcc);

    String path = pathName + "/MR" + nVar + "/";
    String accFile = path + "acc_ratio_hists_" + nVar + "_" + solidTgt[indexTgt] + ".txt";
    println "Analyzing " + accFile;
    new File(accFile).eachLine { line ->
      str = line.split('\t');
      x = 0.0; y = 0.0; err = 0.0;
      str.eachWithIndex{ val, ival->
        switch(ival){
          case 0: x = Double.parseDouble(val); break;
          case 1: y = Double.parseDouble(val); break;
          case 2: err = Double.parseDouble(val); break;
          default: break;
        }
      }
      if(x>xlo[iVar] && x<=xhi[iVar]){
        gr_acc.addPoint(x,y,0.0,err);
      }
    }
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
      corrMR = dataY.getValue(i)*accY.getValue(i);
      if(nVar=="q2" || nVar=="nu") corrMR = corrMR/normY.getValue(i);

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
    String cname = "can" + iVar;
    can[iVar] = new TCanvas(cname,1000,500);
    can[iVar].divide(2,1);
    can[iVar].cd(0);
    can[iVar].getPad().setTitleFontSize(c1_title_size);
    gr_acc.setTitleX(xLabel[iVar]);
    gr_acc.setTitleY("Acceptance Ratio");
    gr_acc.setMarkerColor(2);
    gr_acc.setLineColor(2);
    gr_acc.setMarkerSize(5);
    can[iVar].draw(gr_acc);
    dir.addDataSet(gr_acc); // add to the histogram file

    can[iVar].cd(1);
    can[iVar].getPad().setTitleFontSize(c1_title_size);
    gr_mrProton[iVar].setMarkerSize(5);
    can[iVar].draw(gr_mrProton[iVar]);
    dir.addDataSet(gr_mrProton[iVar]); // add to the histogram file

    gr_mrCorr.setTitleX(xLabel[iVar]);
    gr_mrCorr.setTitleY("R^p");
    gr_mrCorr.setMarkerColor(4);
    gr_mrCorr.setLineColor(4);
    gr_mrCorr.setMarkerSize(5);
    can[iVar].draw(gr_mrCorr,"same");
    dir.addDataSet(gr_mrCorr); // add to the histogram file

    dir.cd(); // return to the top directory
  }
}

dir.writeFile(outFile);
