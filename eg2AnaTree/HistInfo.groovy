package eg2AnaTree

import eg2Cuts.eg2Target;

class HistInfo {

  eg2Target myTarget = new eg2Target();  // create the eg2 target object
  double Q2_DIS = myTarget.Get_Q2_DIS();

  String VarList = "q2:nu:zh:pT2:zLC";

  String[] solidTgt = ["C","Fe","Pb"];
  String[] TgtLabel = ["D2","Nuc"];
  String[] xLabel = ["Q^2 (GeV^2)","#nu (GeV)","z_h","pT^2 (GeV^2)","z_L_C"];
  String[] Var = ["q2","nu","zh","pT2","zLC"];
  int[] nbins = [50,50,25,30,30];
  double[] xlo = [Q2_DIS,2.25,0.2,0.0,0.1];
  double[] xhi = [4.0,4.25,1.0,2.0,1.0];

  List<String> getSolidTgtLabel(){
    return solidTgt;
  }
  List<String> getTgtlabel(){
    return TgtLabel;
  }
  List<String> getVariables(){
    return Var;
  }
  List<String> getXlabel(){
    return xLabel;
  }
  List<Double> getXlo(){
    return xlo;
  }
  List<Double> getXhi(){
    return xhi;
  }
  List<Integer> getNbins(){
    return nbins;
  }
}
