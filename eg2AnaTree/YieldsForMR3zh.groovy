package eg2AnaTree

import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*;

import eg2AnaTree.HistInfo;

class YieldsForMR3zh {

  HistInfo myHI = new HistInfo();
  List<String> TgtLabel = myHI.getTgtlabel();
  String xLabel = "z_h";

  int maxEvents = -1; //default is to analyze all events
  String VarList = "zh";
  String FidCuts = "pFidCut==1&&eFidCut==1&&eFidEC==1";
  String[] Q2Cuts = ["q2>1.0&&q2<1.33","q2>1.33&&q2<1.76","q2>1.76&&q2<4.1"];
  String[] nuCuts = ["nu>2.2&&nu<3.2","nu>3.2&&nu<3.73","nu>3.73&&nu<4.25"];

  int[][] nbins_eg2 = [[36,27,23],[36,27,23],[33,28,23]];
  double[][] xlo_eg2 = [[0.325,0.275,0.25],[0.325,0.275,0.25],[0.325,0.275,0.25]];
  double[][] xhi_eg2 = [[1.225,0.95,0.825],[1.225,0.95,0.825],[1.15,0.975,0.825]];

  int[][] nbins_full = [[50,50,50],[50,50,50],[50,50,50]];
  double[][] xlo_full = [[0.0,0.0,0.0],[0.0,0.0,0.0],[0.0,0.0,0.0]];
  double[][] xhi_full = [[1.25,1.25,1.25],[1.25,1.25,1.25],[1.25,1.25,1.25]];

//  int[][] nbins_eg2 = [[37,28,24],[37,28,24],[34,29,23]];
//  double[][] xlo_eg2 = [[0.3,0.25,0.225],[0.3,0.25,0.225],[0.3,0.25,0.25]];
//  double[][] xhi_eg2 = [[1.225,0.95,0.825],[1.225,0.95,0.825],[1.15,0.975,0.825]];

//  int[][] nbins = [[38,29,25],[38,29,25],[35,30,24]];
//  double[][] xlo = [[0.3,0.25,0.225],[0.3,0.25,0.225],[0.3,0.25,0.25]];
//  double[][] xhi = [[1.25,0.975,0.85],[1.25,0.975,0.85],[1.175,1.0,0.85]];

  int[][] nbins;
  double[][] xlo;
  double[][] xhi;

  TreeHipo tree;
  H1F[][] hYlds = new H1F[Q2Cuts.size()][nuCuts.size()];

  YieldsForMR3zh(){
    System.out.println("Starting YieldsForMR3zh.  The ntuple/tree file has not been loaded.");
    setBinning(1);
  }

  YieldsForMR3zh(String treeFile){
    tree = new TreeHipo(treeFile,"protonTree::tree"); // the writer adds ::tree to the name of the tree
    System.out.println(" ENTRIES = " + tree.getEntries());
    setBinning(1);
  }

  void setMaxEvents(int nMax){
    maxEvents = nMax;
  }

  List<String> getQ2Cuts(){
    return Q2Cuts;
  }

  List<String> getNuCuts(){
    return nuCuts;
  }

  void createHistograms(int iTgt){
    Q2Cuts.eachWithIndex { nQ2, iQ2->
      nuCuts.eachWithIndex { nNu, iNu->
        tree.reset();
        String Cuts = FidCuts + "&&" + nQ2 + "&&" + nNu + "&&iTgt==" + iTgt;
        List vec = tree.getDataVectors(VarList,Cuts,maxEvents);
        System.out.println("Cuts " + Cuts);
        System.out.println("Vector Size (" + TgtLabel[iTgt] + ")= " + vec.get(0).getSize());
        System.out.println("Bins " + nbins[iQ2][iNu] + " from " + xlo[iQ2][iNu] + " to " + xhi[iQ2][iNu]);

        String hname = "hYlds_" + TgtLabel[iTgt] + "_" + VarList + "_" + iQ2 + iNu;
        hYlds[iQ2][iNu] = new H1F().create(hname,nbins[iQ2][iNu],vec.get(0),xlo[iQ2][iNu],xhi[iQ2][iNu]);
        hYlds[iQ2][iNu].setTitleX(xLabel);
        hYlds[iQ2][iNu].setTitleY("Counts");
        hYlds[iQ2][iNu].setTitle("eg2 - " + TgtLabel[iTgt]);
        vec = null;
      }
    }
  }

  H1F getHistogram(int numQ2, int numNu){
    return hYlds[numQ2][numNu];
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
