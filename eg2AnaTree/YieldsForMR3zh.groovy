package eg2AnaTree

import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*;

import eg2AnaTree.HistInfo;

class YieldsForMR3zh {

  HistInfo myHI = new HistInfo();
  List<String> TgtLabel = myHI.getTgtlabel();
//  double xlo = 0.0;
//  double xhi = 1.25;
//  int nbins = 50;
  String xLabel = "z_h";

  int maxEvents = -1; //default is to analyze all events
  String VarList = "zh";
  String FidCuts = "pFidCut==1&&eFidCut==1&&eFidEC==1";
  String[] Q2Cuts = ["q2>1.0&&q2<1.33","q2>1.33&&q2<1.76","q2>1.76&&q2<4.1"];
  String[] nuCuts = ["nu>2.2&&nu<3.2","nu>3.2&&nu<3.73","nu>3.73&&nu<4.25"];
  int[][] nbins = [[37,28,24],[37,28,24],[34,29,23]];
  double[][] xlo = [[0.3,0.25,0.225],[0.3,0.25,0.225],[0.3,0.25,0.25]];
  double[][] xhi = [[1.225,0.95,0.825],[1.225,0.95,0.825],[1.15,0.975,0.825]];

//  int[][] nbins = [[38,29,25],[38,29,25],[35,30,24]];
//  double[][] xlo = [[0.3,0.25,0.225],[0.3,0.25,0.225],[0.3,0.25,0.25]];
//  double[][] xhi = [[1.25,0.975,0.85],[1.25,0.975,0.85],[1.175,1.0,0.85]];

  TreeHipo tree;
  H1F[][] hYlds = new H1F[Q2Cuts.size()][nuCuts.size()];

  YieldsForMR3zh(){
    System.out.println("Starting YieldsForMR3zh.  The ntuple/tree file has not been loaded.");
  }

  YieldsForMR3zh(String treeFile){
    tree = new TreeHipo(treeFile,"protonTree::tree"); // the writer adds ::tree to the name of the tree
    System.out.println(" ENTRIES = " + tree.getEntries());
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
}
