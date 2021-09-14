package eg2AnaTree

import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*;

import eg2AnaTree.HistInfo;

class YieldsForDIS {

  HistInfo myHI = new HistInfo();
  List<String> Var = myHI.getVariables();
  List<String> TgtLabel = myHI.getTgtlabel();
  List<String> xLabel = myHI.getXlabel();
  List<Double> xlo = myHI.getXlo();
  List<Double> xhi = myHI.getXhi();
  List<Integer> nbins = myHI.getNbins();

  int maxEvents = -1; //default is to analyze all events
  String VarList = "q2:nu";
  String[] Cuts = ["eFidCut==1&&eFidEC==1&&iTgt==0","eFidCut==1&&eFidEC==1&&iTgt==1"];

  double[] normDIS = [1.11978 ,  1.0609 ,  2.19708]; // DIS e- normalization factors (Acc Corr, Rad Corr and Coulomb)

  TreeHipo tree;
  H1F[] hRatDIS = new H1F[Var.size()];

  YieldsForDIS(String treeFile){
    tree = new TreeHipo(treeFile,"DISTree::tree"); // the writer adds ::tree to the name of the tree
    System.out.println(" ENTRIES = " + tree.getEntries());
  }

  void setMaxEvents(int nMax){
    maxEvents = nMax;
  }

  void createHistogramRatios(int iSolidTgt){
    Var.eachWithIndex { nVar, iVar->
      String hname = "hRatDIS_" + nVar;
      if(nVar=="q2"){
        H1F hYldsQ2_LD2 = createYieldsQ2(0);
        H1F hYldsQ2_Solid = createYieldsQ2(1);
        hRatDIS[iVar] = H1F.divide(hYldsQ2_Solid,hYldsQ2_LD2);
        hRatDIS[iVar].setName(hname);
      }else if(nVar=="nu"){
        H1F hYldsNu_LD2 = createYieldsNu(0);
        H1F hYldsNu_Solid = createYieldsNu(1);
        hRatDIS[iVar] = H1F.divide(hYldsNu_Solid,hYldsNu_LD2);
        hRatDIS[iVar].setName(hname);
      }else{
        hRatDIS[iVar] = new H1F(hname,xLabel[iVar],"Counts",nbins[iVar],xlo[iVar],xhi[iVar]);
        for(int bin = 0; bin < hRatDIS[iVar].getXaxis().getNBins(); bin++){
          hRatDIS[iVar].incrementBinContent(bin, normDIS[iSolidTgt]);
        }
      }
      hRatDIS[iVar].setTitleX(xLabel[iVar]);
      hRatDIS[iVar].setTitleY("Ratio");
      hRatDIS[iVar].setTitle("eg2 - DIS ratio");
    }
  }

  H1F createYieldsQ2(int iTgt){
    tree.reset();
    List vec = tree.getDataVectors("q2",Cuts[iTgt],maxEvents);
    H1F hist = new H1F().create("hist",nbins[0],vec.get(0),xlo[0],xhi[0]);
    vec = null;
    return hist;
  }

  H1F createYieldsNu(int iTgt){
    tree.reset();
    List vec = tree.getDataVectors("nu",Cuts[iTgt],maxEvents);
    H1F hist = new H1F().create("hist",nbins[1],vec.get(0),xlo[1],xhi[1]);
    vec = null;
    return hist;
  }

  H1F getHistogramRatios(int num){
    return hRatDIS[num];
  }
}
