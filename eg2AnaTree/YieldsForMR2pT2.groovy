package eg2AnaTree

import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*;

import eg2AnaTree.HistInfo;

class YieldsForMR2pT2 {

  HistInfo myHI = new HistInfo();
  List<String> TgtLabel = myHI.getTgtlabel();
  String xLabel = "pT^2 (GeV^2 )";

  int maxEvents = -1; //default is to analyze all events
  String VarList = "pT2";
  String FidCuts = "pFidCut==1&&eFidCut==1&&eFidEC==1";
  int zhBins = 9;
  double zhMin = 0.3;
  double zhMax = 1.2;
  List<String> zhCuts = [];
  int[] nbins_eg2 = [15,21,26,28,29,29,28,21,15];
  double[] xlo_eg2 = [0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0];
  double[] xhi_eg2 = [1.0,1.4,1.7333333,1.86666667,1.93333333,1.93333333,1.86666667,1.4,1.0];

  int[] nbins_full = [30,30,30,30,30,30,30,30,30];
  double[] xlo_full = [0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0];
  double[] xhi_full = [2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0];

  int[] nbins;
  double[] xlo;
  double[] xhi;

  TreeHipo tree;
  H1F[] hYlds = new H1F[zhBins];

  YieldsForMR2pT2(){
    System.out.println("Starting YieldsForMR2pT2.  The ntuple/tree file has not been loaded.");
    setZhCuts();
    setBinning(1);
  }

  YieldsForMR2pT2(String treeFile){
    tree = new TreeHipo(treeFile,"protonTree::tree"); // the writer adds ::tree to the name of the tree
    System.out.println(" ENTRIES = " + tree.getEntries());
    setZhCuts();
    setBinning(1);
  }

  void setMaxEvents(int nMax){
    maxEvents = nMax;
  }

  List<String> getZhCuts(){
    return zhCuts;
  }

  void setZhCuts(){
    double lo, hi;
    double zhStep = (zhMax - zhMin)/zhBins;
    for(int i=0; i<zhBins; i++){
      lo = (zhMin + i*zhStep).round(3);
      hi = (lo + zhStep).round(3);
      zhCuts.add("zh>" + lo + "&&zh<" + hi);
    }
  }

  void createHistograms(int iTgt){
    zhCuts.eachWithIndex { nZh, iZh->
      tree.reset();
      String Cuts = FidCuts + "&&" + nZh + "&&iTgt==" + iTgt;
      List vec = tree.getDataVectors(VarList,Cuts,maxEvents);
      System.out.println("Cuts " + Cuts);
      System.out.println("Vector Size (" + TgtLabel[iTgt] + ")= " + vec.get(0).getSize());
      System.out.println("Bins " + nbins[iZh] + " from " + xlo[iZh] + " to " + xhi[iZh]);

      String hname = "hYlds_" + TgtLabel[iTgt] + "_" + VarList + "_" + iZh;
      hYlds[iZh] = new H1F().create(hname,nbins[iZh],vec.get(0),xlo[iZh],xhi[iZh]);
      hYlds[iZh].setTitleX(xLabel);
      hYlds[iZh].setTitleY("Counts");
      hYlds[iZh].setTitle("eg2 - " + TgtLabel[iTgt]);
      vec = null;
    }
  }

  H1F getHistogram(int num){
    return hYlds[num];
  }

  List<Double> getXlo(){
    return xlo;
  }
  void setXlo(int iSet){
    if(iSet==1){
      xlo = xlo_eg2;
    }else{
      xlo = xlo_full;
    }
  }
  List<Double> getXhi(){
    return xhi;
  }
  void setXhi(int iSet){
    if(iSet==1){
      xhi = xhi_eg2;
    }else{
      xhi = xhi_full;
    }
  }
  List<Integer> getNbins(){
    return nbins;
  }
  void setNbins(int iSet){
    if(iSet==1){
      nbins = nbins_eg2;
    }else{
      nbins = nbins_full;
    }
  }
  void setBinning(int iSet){
    setNbins(iSet);
    setXlo(iSet);
    setXhi(iSet);
  }
  String getXlabel(){
    return xLabel;
  }
}
