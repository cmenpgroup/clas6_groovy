package eg2AnaTree

import eg2Cuts.eg2Target;

class HistInfo {

  eg2Target myTarget = new eg2Target();  // create the eg2 target object
  double Q2_DIS = myTarget.Get_Q2_DIS();

  String VarList = "q2:nu:zh:pT2:zLC:zLC:phiPQ";

  String[] solidTgt = ["C","Fe","Pb"];
  String[] TgtLabel = ["D2","Nuc"];
  String[] xLabel = ["Q^2 (GeV^2)","#nu (GeV)","z_h","pT^2 (GeV^2)","z_L_C","z_L_C","#phi_P_Q (deg.)"];
  String[] Var = ["q2","nu","zh","pT2","zLC_lo","zLC_hi","phiPQ"];
  String[] VarClasTool = ["Q2","Nu","Zh","Pt2","Zlc_lo","Zlc_hi","PhiPQ"];
  int[] nbins = [48,50,41,30,17,16,60];
  double[] xlo = [Q2_DIS,2.2,0.225,0.0,0.13,0.3,-180.0];
  double[] xhi = [3.976,4.2,1.25,2.0,0.3,1.02,180.0];

  List<String> getSolidTgtLabel(){
    return solidTgt;
  }
  List<String> getTgtlabel(){
    return TgtLabel;
  }
  List<String> getVariables(){
    return Var;
  }
  List<String> getVariablesClasTool(){
    return VarClasTool;
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
