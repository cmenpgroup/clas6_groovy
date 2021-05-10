package eg2AnaTree

import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*;

import eg2AnaTree.HistInfo;

class YieldsForMR {

  HistInfo myHI = new HistInfo();
  List<String> Var = myHI.getVariables();
  List<String> TgtLabel = myHI.getTgtlabel();
  List<String> xLabel = myHI.getXlabel();
  List<Double> xlo = myHI.getXlo();
  List<Double> xhi = myHI.getXhi();
  List<Integer> nbins = myHI.getNbins();

  int maxEvents = -1; //default is to analyze all events
  String VarList = "q2:nu:zh:pT2:zLC";
  String[] Cuts = ["pFidCut==1&&eFidCut==1&&iTgt==0","pFidCut==1&&eFidCut==1&&iTgt==1"];

  TreeHipo tree;
  H1F[] hYlds = new H1F[Var.size()];

  YieldsForMR(String treeFile){
    tree = new TreeHipo(treeFile,"protonTree::tree"); // the writer adds ::tree to the name of the tree
    System.out.println(" ENTRIES = " + tree.getEntries());
  }

  void setMaxEvents(in nMax){
    maxEvents = nMax;
  }

  void createHistograms(int iTgt){
    tree.reset();
    List vec = tree.getDataVectors(VarList,Cuts[iTgt],maxEvents);
    System.out.println("Vector Size (" + TgtLabel[iTgt] + ")= " + vec.get(0).getSize());

    Var.eachWithIndex { nVar, iVar->
      String hname = "hYlds_" + TgtLabel[iTgt] + "_" + nVar;
      hYlds[iVar] = new H1F().create(hname,nbins[iVar],vec.get(iVar),xlo[iVar],xhi[iVar]);
      hYlds[iVar].setTitleX(xLabel[iVar]);
      hYlds[iVar].setTitleY("Counts");
      hYlds[iVar].setTitle("eg2 - " + TgtLabel[iTgt]);
    }
  }

  H1F getHistogram(int num){
    return hYlds[num];
  }
}
