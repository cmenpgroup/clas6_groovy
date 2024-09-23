package eg2AnaTree

import eg2Cuts.eg2Target;

class HistInfo {

  eg2Target myTarget = new eg2Target();  // create the eg2 target object
  double Q2_DIS = myTarget.Get_Q2_DIS();

  String VarList = "q2:nu:zh:pT2:zLC:zLC:phiPQ";

  String[] solidTgt = ["C","Fe","Pb"];
  String[] TgtLabel = ["D2","Nuc"];
  String[] xLabel = ["Q^2 (GeV^2)","#nu (GeV)","z_h","pT^2 (GeV^2)","z ","z ","#phi_P_Q (deg.)"];
  String[] Var = ["q2","nu","zh","pT2","zLC_lo","zLC_hi","phiPQ"];
  String[] VarClasTool = ["Q2","Nu","Zh","Pt2","Zlc_lo","Zlc_hi","PhiPQ"];
  int[] nbins = [48,49,40,30,16,16,60];
  double[] xlo = [Q2_DIS,2.24,0.25,0.0,0.14,0.3,-180.0];
  double[] xhi = [3.976,4.2,1.25,2.0,0.3,1.02,180.0];

//  int[] nbins = [48,50,41,30,17,16,60];
//  double[] xlo = [Q2_DIS,2.2,0.225,0.0,0.13,0.3,-180.0];
//  double[] xhi = [3.976,4.2,1.25,2.0,0.3,1.02,180.0];

  // more variables to study the kinematics
  String[] Var_kine = ["P","W","Yb","PhiLab"];
  String[] xLabel_kine = ["P (GeV)","W (GeV)", "y ", "#phi_L_a_b (deg.)"];
  int[] nbins_kine = [50,100,100,360];
  double[] xlo_kine = [0.5,1.5,0.0,-180.0];
  double[] xhi_kine = [3.0,3.5,1.0,180.0];

  String[] sigmaLabel = ["1.0","1.5","2.0","2.5","3.0"]; // sigma for proton ID cut
  String[] sigmaUnderscore = ["sigma_1_0","sigma_1_5","sigma_2_0","sigma_2_5","sigma_3_0"]; // sigma for proton ID cut

  List<String> getSigmaLabel(){
    return sigmaLabel;
  }
  List<String> getSigmaUnderscore(){
    return sigmaUnderscore;
  }
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
  void createFullList(){
    Var += this.getVariables_kine();
    VarClasTool += this.getVariables_kine();
    xLabel += this.getXlabel_kine();
//    nbins += this.getNbins_kine();
//    xlo += this.getXlo_kine();
//    xhi += this.getXhi_kine();
  }
  List<String> getVariables_kine(){
    return Var_kine;
  }
  List<Integer> getNbins_kine(){
    return nbins_kine;
  }
  List<String> getXlabel_kine(){
    return xLabel_kine;
  }
  List<Double> getXlo_kine(){
    return xlo_kine;
  }
  List<Double> getXhi_kine(){
    return xhi_kine;
  }
}
