import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

import eg2Cuts.clas6beta

myBeta = new clas6beta();  // create the beta object

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

double LIGHTSPEED = 30.0; // speed of light in cm/ns
double W_DIS = 2.0;
double Q2_DIS = 1.0;
double ELECTRON_MOM = 0.64;
double NPHE_MIN = 28;
double ECIN_MIN = 0.06;
double PHOTON_MOM = 0.15;
double ELETRON_PHOTON_ANGLE = 12.0;

def Get_EC_SamplingFraction = {
    coeff,sector,targMass->
    double ret = 0.0;

    def EC_SamplingFrac_C = new Float[6][5];
    def EC_SamplingFrac_Fe = new Float[6][5];
    def EC_SamplingFrac_Pb = new Float[6][5];

    EC_SamplingFrac_C[0][0] = 0.226726; EC_SamplingFrac_C[0][1] = 0.0379557; EC_SamplingFrac_C[0][2] = -0.00855326; EC_SamplingFrac_C[0][3] = 7.27022e-09; EC_SamplingFrac_C[0][4] = 0.0370079;
    EC_SamplingFrac_C[1][0] = 0.222333; EC_SamplingFrac_C[1][1] = 0.0581705; EC_SamplingFrac_C[1][2] = -0.0131283; EC_SamplingFrac_C[1][3] = 3.12094e-12; EC_SamplingFrac_C[1][4] = 0.0413565;
    EC_SamplingFrac_C[2][0] = 0.245212; EC_SamplingFrac_C[2][1] = 0.0213835; EC_SamplingFrac_C[2][2] = -0.00277372; EC_SamplingFrac_C[2][3] = 8.27916e-08; EC_SamplingFrac_C[2][4] = 0.0426498;
    EC_SamplingFrac_C[3][0] = 0.238399; EC_SamplingFrac_C[3][1] = 0.0301926; EC_SamplingFrac_C[3][2] = -0.00720393; EC_SamplingFrac_C[3][3] = -3.81029e-09; EC_SamplingFrac_C[3][4] = 0.0309331;
    EC_SamplingFrac_C[4][0] = 0.241834; EC_SamplingFrac_C[4][1] = 0.0442975; EC_SamplingFrac_C[4][2] = -0.0105584; EC_SamplingFrac_C[4][3] = 9.74651e-09; EC_SamplingFrac_C[4][4] = 0.0303602;
    EC_SamplingFrac_C[5][0] = 0.245868; EC_SamplingFrac_C[5][1] = 0.0545128; EC_SamplingFrac_C[5][2] = -0.0149168; EC_SamplingFrac_C[5][3] = 1.43097e-08; EC_SamplingFrac_C[5][4] = 0.0483305;

    EC_SamplingFrac_Fe[0][0] = 2.22E-1; EC_SamplingFrac_Fe[0][1] = 2.23E-2; EC_SamplingFrac_Fe[0][2] = -2.41E-3; EC_SamplingFrac_Fe[0][3] = 9.23E-3; EC_SamplingFrac_Fe[0][4] = 2.98E-2;
    EC_SamplingFrac_Fe[1][0] = 2.34E-1; EC_SamplingFrac_Fe[1][1] = 1.95E-2; EC_SamplingFrac_Fe[1][2] = -2.08E-3; EC_SamplingFrac_Fe[1][3] = 8.66E-3; EC_SamplingFrac_Fe[1][4] = 3.09E-2;
    EC_SamplingFrac_Fe[2][0] = 2.52E-1; EC_SamplingFrac_Fe[2][1] = 2.42E-2; EC_SamplingFrac_Fe[2][2] = -3.39E-3; EC_SamplingFrac_Fe[2][3] = 1.08E-2; EC_SamplingFrac_Fe[2][4] = 2.64E-2;
    EC_SamplingFrac_Fe[3][0] = 2.51E-1; EC_SamplingFrac_Fe[3][1] = 2.08E-2; EC_SamplingFrac_Fe[3][2] = -3.27E-3; EC_SamplingFrac_Fe[3][3] = 7.22E-3; EC_SamplingFrac_Fe[3][4] = 2.98E-2;
    EC_SamplingFrac_Fe[4][0] = 2.72E-1; EC_SamplingFrac_Fe[4][1] = 1.18E-2; EC_SamplingFrac_Fe[4][2] = -1.87E-3; EC_SamplingFrac_Fe[4][3] = 1.84E-2; EC_SamplingFrac_Fe[4][4] = 3.48E-2;
    EC_SamplingFrac_Fe[5][0] = 2.52E-1; EC_SamplingFrac_Fe[5][1] = 2.28E-2; EC_SamplingFrac_Fe[5][2] = -3.11E-3; EC_SamplingFrac_Fe[5][3] = 4.11E-3; EC_SamplingFrac_Fe[5][4] = 3.55E-2;

    EC_SamplingFrac_Pb[0][0] = 2.53E-1; EC_SamplingFrac_Pb[0][1] = 1.38E-2; EC_SamplingFrac_Pb[0][2] = -1.40E-3; EC_SamplingFrac_Pb[0][3] = 7.67E-3; EC_SamplingFrac_Pb[0][4] = 3.54E-2;
    EC_SamplingFrac_Pb[0][0] = 2.53E-1; EC_SamplingFrac_Pb[0][1] = 1.38E-2; EC_SamplingFrac_Pb[0][2] = -1.40E-3; EC_SamplingFrac_Pb[0][3] = 7.67E-3; EC_SamplingFrac_Pb[0][4] = 3.54E-2;
    EC_SamplingFrac_Pb[1][0] = 2.49E-1; EC_SamplingFrac_Pb[1][1] = 1.47E-2; EC_SamplingFrac_Pb[1][2] = -1.49E-3; EC_SamplingFrac_Pb[1][3] = 7.53E-3; EC_SamplingFrac_Pb[1][4] = 3.38E-2;
    EC_SamplingFrac_Pb[2][0] = 2.54E-1; EC_SamplingFrac_Pb[2][1] = 2.26E-2; EC_SamplingFrac_Pb[2][2] = -3.05E-3; EC_SamplingFrac_Pb[2][3] = 8.13E-3; EC_SamplingFrac_Pb[2][4] = 2.77E-2;
    EC_SamplingFrac_Pb[3][0] = 2.55E-1; EC_SamplingFrac_Pb[3][1] = 1.90E-2; EC_SamplingFrac_Pb[3][2] = -3.05E-3; EC_SamplingFrac_Pb[3][3] = 7.20E-3; EC_SamplingFrac_Pb[3][4] = 3.04E-2;
    EC_SamplingFrac_Pb[4][0] = 2.76E-1; EC_SamplingFrac_Pb[4][1] = 1.11E-2; EC_SamplingFrac_Pb[4][2] = -1.76E-3; EC_SamplingFrac_Pb[4][3] = 1.81E-2; EC_SamplingFrac_Pb[4][4] = 3.53E-2;
    EC_SamplingFrac_Pb[5][0] = 2.62E-1; EC_SamplingFrac_Pb[5][1] = 1.92E-2; EC_SamplingFrac_Pb[5][2] = -2.62E-3; EC_SamplingFrac_Pb[5][3] = 1.99E-3; EC_SamplingFrac_Pb[5][4] = 3.76E-2;

    if(sector>=1 && sector<=6){ //check that the sector is between 1 and 6
        if(coeff>=0 && coeff<5){
            switch (targMass){
                case 12: ret = EC_SamplingFrac_C[sector-1][coeff]; break;
                case 56: ret = EC_SamplingFrac_Fe[sector-1][coeff]; break;
                case 208: ret = EC_SamplingFrac_Pb[sector-1][coeff]; break;
                default:
                    System.out.println("Get_EC_SamplingFraction: Target Mass " + targMass + " is unknown.");
                    ret = 0.0;
                    break;
            }
        }
        else{
            System.out.println("Get_EC_SamplingFraction: Coefficient " + coeff + " is out of range.");
        }
    }
    else{
        System.out.println("Get_EC_SamplingFraction: Sector " + sector + " is out of range.");
    }
    return ret;
}

def EC_SamplingFraction_Cut = {
    mom, ECtotal, sector, targMass ->
    boolean ret = false;

    double a = Get_EC_SamplingFraction(0,sector,targMass);
    double b = Get_EC_SamplingFraction(1,sector,targMass);
    double c = Get_EC_SamplingFraction(2,sector,targMass);
    double d = Get_EC_SamplingFraction(3,sector,targMass);
    double f = Get_EC_SamplingFraction(4,sector,targMass);

    double centroid = a + b*mom + c*mom*mom;
    double sigma = Math.sqrt(d*d + f*f/Math.sqrt(mom));
    double Nsigma = 2.5;

    double diff = Math.abs(ECtotal/mom - centroid);

    ret = (diff < Nsigma*sigma) ? true : false;

    return ret;
}

def dt_ECSC = {
  ECtime, SCtime->

  double dt = ECtime - SCtime;
  double dtCentroid = 0.6;
  double dtWidth = 0.6;
  double dtNsigmas = 3.0;
  double dtLo = dtCentroid - dtNsigmas*dtWidth;
  double dtHi = dtCentroid + dtNsigmas*dtWidth;

  boolean ret = (dt >= dtLo && dt < dtHi) ? true : false;
  return ret;
}

def PhotonTiming_Cut = {
  time ->
  double dtCentroid = 54.0;
  double dtWidth = 5.3;
  double dtNsigmas = 3.0;
  double dtLo = dtCentroid - dtNsigmas*dtWidth;
  double dtHi = dtCentroid + dtNsigmas*dtWidth;
  boolean ret = (time >= dtLo && time < dtHi);
  return ret;
}

def PhotonBeta_Cut = {
  beta ->
  boolean ret = (beta >= 0.95 && beta < 1.05);
  return ret;
}

def Get_Msq = {
  mom, beta->
  double Msq = -99.0;
  double betaSq = beta*beta;
  if(betaSq>0.0) Msq = mom*mom*(1.0-betaSq)/betaSq;
  return Msq;
}

def PhotonSCMsq_Cut = {
  Msq, num->
  def SCMsq_Lo = new Float[2];
  def SCMsq_Hi = new Float[2];

  SCMsq_Lo[0] = -0.041537; // Lower limit on photon 1 TOF M^2
  SCMsq_Lo[1] = -0.044665; // Lower limit on photon 2 TOF M^2
  SCMsq_Hi[0] = 0.031435; // Upper limit on photon 1 TOF M^2
  SCMsq_Hi[1] = 0.035156; // Upper limit on photon 2 TOF M^2

  boolean ret = (Msq >= SCMsq_Lo[num-1] && Msq < SCMsq_Hi[num-1]);
  return ret;
}

int pid;
int counterTotal = 0;
int counterFile;
float px, py, pz;

PhysicsConstants PhyConsts= new PhysicsConstants();
System.out.println("electron = " + PhyConsts.massElectron());
System.out.println("proton = " + PhyConsts.massProton());
System.out.println("pi+/- = " + PhyConsts.massPionCharged());
System.out.println("pi0 = " + PhyConsts.massPionNeutral());

LorentzVector electron = new LorentzVector(0,0,0,0);
LorentzVector photon1 = new LorentzVector(0,0,0,0);
LorentzVector photon2 = new LorentzVector(0,0,0,0);
LorentzVector piplus = new LorentzVector(0,0,0,0);
LorentzVector piminus = new LorentzVector(0,0,0,0);

double beamEnergy = 5.1;
LorentzVector beam = new LorentzVector(0.0,0.0,beamEnergy,beamEnergy);
LorentzVector protonTarget = new LorentzVector(0.0,0.0,0.0,PhyConsts.massProton());

//println args.length;

H1F h1_Mpi0 = new H1F("h1_Mpi0",100,0.0,1.0);
h1_Mpi0.setTitle("Experiment: eg2");
h1_Mpi0.setTitleX("IM(#gamma #gamma) (GeV)");
h1_Mpi0.setTitleY("Counts");
h1_Mpi0.setFillColor(44);

H1F h1_Q2 = new H1F("h1_Q2",100,0.0,5.0);
h1_Q2.setTitle("Experiment: eg2");
h1_Q2.setTitleX("Q^2 (GeV^2)");
h1_Q2.setTitleY("Counts");
//h1_Q2.setFillColor(43);

H1F h1_Q2_cut = new H1F("h1_Q2_cut",100,0.0,5.0);
h1_Q2_cut.setTitle("Experiment: eg2");
h1_Q2_cut.setTitleX("Q^2 (GeV^2)");
h1_Q2_cut.setTitleY("Counts");
h1_Q2_cut.setFillColor(43);

H1F h1_Nu = new H1F("h1_Nu",100,0.0,5.0);
h1_Nu.setTitle("Experiment: eg2");
h1_Nu.setTitleX("#nu (GeV)");
h1_Nu.setTitleY("Counts");
h1_Nu.setFillColor(43);

H1F h1_Xb = new H1F("h1_Xb",100,0.0,1.5);
h1_Xb.setTitle("Experiment: eg2");
h1_Xb.setTitleX("X_b");
h1_Xb.setTitleY("Counts");
h1_Xb.setFillColor(43);

H1F h1_Yb = new H1F("h1_Yb",100,0.0,1.0);
h1_Yb.setTitle("Experiment: eg2");
h1_Yb.setTitleX("Y_b");
h1_Yb.setTitleY("Counts");
h1_Yb.setFillColor(43);

H1F h1_W = new H1F("h1_W",160,0.0,3.2);
h1_W.setTitle("Experiment: eg2");
h1_W.setTitleX("W (GeV)");
h1_W.setTitleY("Counts");
//h1_W.setFillColor(43);

H1F h1_W_cut = new H1F("h1_W_cut",160,0.0,3.2);
h1_W_cut.setTitle("Experiment: eg2");
h1_W_cut.setTitleX("W (GeV)");
h1_W_cut.setTitleY("Counts");
h1_W_cut.setFillColor(43);

H2F h2_Q2_vs_W = new H2F("h1_Q2_vs_W",100,0.0,5.0,160,0.0,3.2);
h2_Q2_vs_W.setTitle("Experiment: eg2");
h2_Q2_vs_W.setTitleX("Q^2 (GeV^2)");
h2_Q2_vs_W.setTitleY("W (GeV)");

H2F h2_Q2_vs_W_cut = new H2F("h1_Q2_vs_W_cut",100,0.0,5.0,160,0.0,3.2);
h2_Q2_vs_W_cut.setTitle("Experiment: eg2");
h2_Q2_vs_W_cut.setTitleX("Q^2 (GeV^2)");
h2_Q2_vs_W_cut.setTitleY("W (GeV)");

H1F h1_theta_eg1 = new H1F("h1_theta_eg1",240,0.0,120.0);
h1_theta_eg1.setTitle("Experiment: eg2");
h1_theta_eg1.setTitleX("#theta(e #gamma1) (deg.)");
h1_theta_eg1.setTitleY("Counts");
//h1_theta_eg1.setFillColor(43);

H1F h1_theta_eg1_cut = new H1F("h1_theta_eg1_cut",240,0.0,120.0);
h1_theta_eg1_cut.setTitle("Experiment: eg2");
h1_theta_eg1_cut.setTitleX("#theta(e #gamma1) (deg.)");
h1_theta_eg1_cut.setTitleY("Counts");
h1_theta_eg1_cut.setFillColor(43);

H1F h1_theta_eg2 = new H1F("h1_theta_eg2",240,0.0,120.0);
h1_theta_eg2.setTitle("Experiment: eg2");
h1_theta_eg2.setTitleX("#theta(e #gamma2) (deg.)");
h1_theta_eg2.setTitleY("Counts");
//h1_theta_eg2.setFillColor(43);

H1F h1_theta_eg2_cut = new H1F("h1_theta_eg2_cut",240,0.0,120.0);
h1_theta_eg2_cut.setTitle("Experiment: eg2");
h1_theta_eg2_cut.setTitleX("#theta(e #gamma2) (deg.)");
h1_theta_eg2_cut.setTitleY("Counts");
h1_theta_eg2_cut.setFillColor(43);

H1F h1_ElectronP = new H1F("h1_ElectronP",100,0.0,5.0);
h1_ElectronP.setTitle("Experiment: eg2");
h1_ElectronP.setTitleX("Momentum(e^-) (GeV)");
h1_ElectronP.setTitleY("Counts");
//h1_ElectronP.setFillColor(43);

H1F h1_ElectronP_cut = new H1F("h1_ElectronP_cut",100,0.0,5.0);
h1_ElectronP_cut.setTitle("Experiment: eg2");
h1_ElectronP_cut.setTitleX("Momentum(e^-) (GeV)");
h1_ElectronP_cut.setTitleY("Counts");
h1_ElectronP_cut.setFillColor(43);

H1F h1_cc_nphe = new H1F("h1_cc_nphe",200,0.0,200.0);
h1_cc_nphe.setTitle("Experiment: eg2");
h1_cc_nphe.setTitleX("Number of Photoelectrons");
h1_cc_nphe.setTitleY("Counts");
//h1_cc_nphe.setFillColor(43);

H1F h1_cc_nphe_cut = new H1F("h1_cc_nphe_cut",200,0.0,200.0);
h1_cc_nphe_cut.setTitle("Experiment: eg2");
h1_cc_nphe_cut.setTitleX("Number of Photoelectrons");
h1_cc_nphe_cut.setTitleY("Counts");
h1_cc_nphe_cut.setFillColor(43);

H1F h1_cc_nphe_withEC = new H1F("h1_cc_nphe_withEC",200,0.0,200.0);
h1_cc_nphe_withEC.setTitle("Experiment: eg2");
h1_cc_nphe_withEC.setTitleX("Number of Photoelectrons");
h1_cc_nphe_withEC.setTitleY("Counts");
//h1_cc_nphe_withEC.setFillColor(43);

H1F h1_cc_nphe_withEC_cut = new H1F("h1_cc_nphe_withEC_cut",200,0.0,200.0);
h1_cc_nphe_withEC_cut.setTitle("Experiment: eg2");
h1_cc_nphe_withEC_cut.setTitleX("Number of Photoelectrons");
h1_cc_nphe_withEC_cut.setTitleY("Counts");
h1_cc_nphe_withEC_cut.setFillColor(43);

H2F h2_ECin_vs_ECout= new H2F("h1_ECin_vs_ECout",100,0.0,1.0,100,0.0,1.0);
h2_ECin_vs_ECout.setTitle("Experiment: eg2");
h2_ECin_vs_ECout.setTitleX("ECin (GeV^2)");
h2_ECin_vs_ECout.setTitleY("ECout (GeV)");

H2F h2_ECin_vs_ECout_cut= new H2F("h1_ECin_vs_ECout",100,0.0,1.0,100,0.0,1.0);
h2_ECin_vs_ECout_cut.setTitle("Experiment: eg2");
h2_ECin_vs_ECout_cut.setTitleX("ECin (GeV^2)");
h2_ECin_vs_ECout_cut.setTitleY("ECout (GeV)");

H2F h2_P_vs_ECtotP = new H2F("h2_P_vs_ECtotP",100,0.0,5.0,100,0.0,0.5);
h2_P_vs_ECtotP.setTitle("Experiment: eg2");
h2_P_vs_ECtotP.setTitleX("Momentum(e^-) (GeV)");
h2_P_vs_ECtotP.setTitleY("ECtot (GeV)");

H2F h2_P_vs_ECtotP_cut = new H2F("h2_P_vs_ECtotP_cut",100,0.0,5.0,100,0.0,0.5);
h2_P_vs_ECtotP_cut.setTitle("Experiment: eg2");
h2_P_vs_ECtotP_cut.setTitleX("Momentum(e^-) (GeV)");
h2_P_vs_ECtotP_cut.setTitleY("ECtot (GeV)");

H1F h1_dtECSC = new H1F("h1_dtECSC",100,-10.0,10.0);
h1_dtECSC.setTitle("Experiment: eg2");
h1_dtECSC.setTitleX("t(EC)-t(SC) (ns)");
h1_dtECSC.setTitleY("Counts");
//h1_dtECSC.setFillColor(43);

H1F h1_dtECSC_cut = new H1F("h1_dtECSC_cut",100,-10.0,10.0);
h1_dtECSC_cut.setTitle("Experiment: eg2");
h1_dtECSC_cut.setTitleX("t(EC)-t(SC) (ns)");
h1_dtECSC_cut.setTitleY("Counts");
h1_dtECSC_cut.setFillColor(43);

H1F h1_PhotonP1 = new H1F("h1_PhotonP1",100,0.0,2.5);
h1_PhotonP1.setTitle("Experiment: eg2");
h1_PhotonP1.setTitleX("Momentum(#gamma) (GeV)");
h1_PhotonP1.setTitleY("Counts");
//h1_PhotonP1.setFillColor(43);

H1F h1_PhotonP1_cut = new H1F("h1_PhotonP1_cut",100,0.0,2.5);
h1_PhotonP1_cut.setTitle("Experiment: eg2");
h1_PhotonP1_cut.setTitleX("Momentum(#gamma) (GeV)");
h1_PhotonP1_cut.setTitleY("Counts");
h1_PhotonP1_cut.setFillColor(43);

H1F h1_PhotonP2 = new H1F("h1_PhotonP2",100,0.0,2.5);
h1_PhotonP2.setTitle("Experiment: eg2");
h1_PhotonP2.setTitleX("Momentum(#gamma) (GeV)");
h1_PhotonP2.setTitleY("Counts");
//h1_PhotonP2.setFillColor(43);

H1F h1_PhotonP2_cut = new H1F("h1_PhotonP2_cut",100,0.0,2.5);
h1_PhotonP2_cut.setTitle("Experiment: eg2");
h1_PhotonP2_cut.setTitleX("Momentum(#gamma) (GeV)");
h1_PhotonP2_cut.setTitleY("Counts");
h1_PhotonP2_cut.setFillColor(43);

H1F h1_PhotonBeta1 = new H1F("h1_PhotonBeta1",100,0.8,1.2);
h1_PhotonBeta1.setTitle("Experiment: eg2");
h1_PhotonBeta1.setTitleX("#beta(#gamma)");
h1_PhotonBeta1.setTitleY("Counts");
//h1_PhotonBeta1.setFillColor(43);

H1F h1_PhotonBeta1_cut = new H1F("h1_PhotonBeta1_cut",100,0.8,1.2);
h1_PhotonBeta1_cut.setTitle("Experiment: eg2");
h1_PhotonBeta1_cut.setTitleX("#beta(#gamma)");
h1_PhotonBeta1_cut.setTitleY("Counts");
h1_PhotonBeta1_cut.setFillColor(43);

H1F h1_PhotonBeta2 = new H1F("h1_PhotonBeta2",100,0.8,1.2);
h1_PhotonBeta2.setTitle("Experiment: eg2");
h1_PhotonBeta2.setTitleX("#beta(#gamma)");
h1_PhotonBeta2.setTitleY("Counts");
//h1_PhotonBeta2.setFillColor(43);

H1F h1_PhotonBeta2_cut = new H1F("h1_PhotonBeta2_cut",100,0.8,1.2);
h1_PhotonBeta2_cut.setTitle("Experiment: eg2");
h1_PhotonBeta2_cut.setTitleX("#beta(#gamma)");
h1_PhotonBeta2_cut.setTitleY("Counts");
h1_PhotonBeta2_cut.setFillColor(43);

H1F h1_PhotonMsq1 = new H1F("h1_PhotonMsq1",100,-0.1,0.1);
h1_PhotonMsq1.setTitle("Experiment: eg2");
h1_PhotonMsq1.setTitleX("SC M^2(#gamma) (GeV^2)");
h1_PhotonMsq1.setTitleY("Counts");
//h1_PhotonMsq1.setFillColor(43);

H1F h1_PhotonMsq1_cut = new H1F("h1_PhotonMsq1_cut",100,-0.1,0.1);
h1_PhotonMsq1_cut.setTitle("Experiment: eg2");
h1_PhotonMsq1_cut.setTitleX("SC M^2(#gamma) (GeV^2)");
h1_PhotonMsq1_cut.setTitleY("Counts");
h1_PhotonMsq1_cut.setFillColor(43);

H1F h1_PhotonMsq2 = new H1F("h1_PhotonMsq2",100,-0.1,0.1);
h1_PhotonMsq2.setTitle("Experiment: eg2");
h1_PhotonMsq2.setTitleX("SC M^2(#gamma) (GeV^2)");
h1_PhotonMsq2.setTitleY("Counts");
//h1_PhotonMsq2.setFillColor(43);

H1F h1_PhotonMsq2_cut = new H1F("h1_PhotonMsq2_cut",100,-0.1,0.1);
h1_PhotonMsq2_cut.setTitle("Experiment: eg2");
h1_PhotonMsq2_cut.setTitleX("SC M^2(#gamma) (GeV^2)");
h1_PhotonMsq2_cut.setTitleY("Counts");
h1_PhotonMsq2_cut.setFillColor(43);

H1F h1_PhotonTiming1 = new H1F("h1_PhotonTiming1",120,-30,90);
h1_PhotonTiming1.setTitle("Experiment: eg2");
h1_PhotonTiming1.setTitleX("#Delta t(#gamma) (ns)");
h1_PhotonTiming1.setTitleY("Counts");
//h1_PhotonTiming1.setFillColor(43);

H1F h1_PhotonTiming1_cut = new H1F("h1_PhotonTiming1_cut",120,-30,90);
h1_PhotonTiming1_cut.setTitle("Experiment: eg2");
h1_PhotonTiming1_cut.setTitleX("#Delta t(#gamma) (ns)");
h1_PhotonTiming1_cut.setTitleY("Counts");
h1_PhotonTiming1_cut.setFillColor(43);

H1F h1_PhotonTiming2 = new H1F("h1_PhotonTiming2",120,-30,90);
h1_PhotonTiming2.setTitle("Experiment: eg2");
h1_PhotonTiming2.setTitleX("#Delta t(#gamma) (ns)");
h1_PhotonTiming2.setTitleY("Counts");
//h1_PhotonTiming2.setFillColor(43);

H1F h1_PhotonTiming2_cut = new H1F("h1_PhotonTiming2_cut",120,-30,90);
h1_PhotonTiming2_cut.setTitle("Experiment: eg2");
h1_PhotonTiming2_cut.setTitleX("#Delta t(#gamma) (ns)");
h1_PhotonTiming2_cut.setTitleY("Counts");
h1_PhotonTiming2_cut.setFillColor(43);

for(int j=0;j<args.length;j++){
  HipoReader reader = new HipoReader();
  reader.open(args[j]);

  Event      event  = new Event();
  Bank       bank   = new Bank(reader.getSchemaFactory().getSchema("EVENT::particle"));
  Bank       ccpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::ccpb"));
  Bank       ecpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::ecpb"));
  Bank       scpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::scpb"));
  // Loop over all events
  counterFile = 0;
  while(reader.hasNext()==true){
    if(counterFile%25000 == 0) println counterFile;
    reader.nextEvent(event);
    event.read(bank);
    event.read(ccpb);
    event.read(ecpb);
    event.read(scpb);

// A00 - 164866, 302772
// A02 - 94422
// A05 - 140285, 190902
    int numElectron = 0;
    int numPiPlus = 0;
    int numPiMinus = 0;
    int numPhoton = 0;
    int numOther = 0;

    boolean nElectron = false;
    boolean cutElectronMom = false;
    boolean cutQ2 = false;
    boolean cutW = false;
    boolean cutAngleElecPhoton1 = false;
    boolean cutAngleElecPhoton2 = false;
    boolean cutCCnphe = false;
    boolean cutCCstat = false;
    boolean cutECstat = false;
    boolean cutSCstat = false;
    boolean cutECin = false;
    boolean cutECoverP = false;
    boolean cutdtECSC = false;
    boolean idElectron = false;
    boolean nPhoton = false;
    boolean cutECstat_photon1 = false;
    boolean cutECstat_photon2 = false;
    boolean cutPhotonMom1 = false;
    boolean cutPhotonTiming1 = false;
    boolean cutPhotonBeta1 = false;
    boolean cutPhotonMsq1 = false;
    boolean cutPhotonMom2 = false;
    boolean cutPhotonTiming2 = false;
    boolean cutPhotonBeta2 = false;
    boolean cutPhotonMsq2 = false;
    boolean idPhoton1 = false;
    boolean idPhoton2 = false;

    int rows = bank.getRows();
    for(int i = 0; i < rows; i++){
      pid = bank.getInt("pid",i);
      px = bank.getFloat("px",i);
      py = bank.getFloat("py",i);
      pz = bank.getFloat("pz",i);
//      beta = bank.getFloat("beta",i);
      switch(pid){
        case 11:
          numElectron++;
          if(numElectron == 1){
            electron.setPxPyPzM(px, py, pz, PhyConsts.massElectron());
            if(bank.getInt("ccstat",i)>0 && ccpb.getRows()>0){
              cutCCstat = true;
              cc_nphe = ccpb.getFloat("nphe",bank.getInt("ccstat",i)-1);
//            ccpb.show();
            }
            if(bank.getInt("ecstat",i)>0 && ecpb.getRows()>0){
              cutECstat = true;
              ECsector = ecpb.getInt("sector",bank.getInt("ecstat",i)-1);
              ecin = ecpb.getFloat("ein",bank.getInt("ecstat",i)-1);
              ecout = ecpb.getFloat("eout",bank.getInt("ecstat",i)-1);
              ectot = ecpb.getFloat("etot",bank.getInt("ecstat",i)-1);
              ecTime = ecpb.getFloat("time",bank.getInt("ecstat",i)-1);
//              ecpb.show();
            }
            if(bank.getInt("scstat",i)>0 && scpb.getRows()>0){
              cutSCstat = true;
              scTime = scpb.getFloat("time",bank.getInt("scstat",i)-1);
//            scpb.show();
            }
          }
          break;
        case 22:
          numPhoton++;
          if(numPhoton == 1){
            photon1.setPxPyPzM(px, py, pz, 0.0);
            beta_photon1 = myBeta.Get_BetaFromMass(photon1.p(),0.0);
            if(bank.getInt("ecstat",i)>0 && ecpb.getRows()>0){
              cutECstat_photon1 = true;
              ecTime_photon1 = ecpb.getFloat("time",bank.getInt("ecstat",i)-1);
              ecPath_photon1 = ecpb.getFloat("path",bank.getInt("ecstat",i)-1);
//              ecpb.show();
              timing_photon1 = ecTime_photon1 - ecPath_photon1/LIGHTSPEED;
            }
          }
          if(numPhoton == 2){
            photon2.setPxPyPzM(px, py, pz, 0.0);
            beta_photon2 = myBeta.Get_BetaFromMass(photon2.p(),0.0);
            if(bank.getInt("ecstat",i)>0 && ecpb.getRows()>0){
              cutECstat_photon2 = true;
              ecTime_photon2 = ecpb.getFloat("time",bank.getInt("ecstat",i)-1);
              ecPath_photon2 = ecpb.getFloat("path",bank.getInt("ecstat",i)-1);
//              ecpb.show();
              timing_photon2 = ecTime_photon2 - ecPath_photon2/LIGHTSPEED;
            }
          }
          break;
        case 211:
          numPiPlus++;
          if(numPiPlus == 1) piplus.setPxPyPzM(px, py, pz, PhyConsts.massPionCharged());
          break;
        case -211:
          numPiMinus++;
          if(numPiMinus == 1) piminus.setPxPyPzM(px, py, pz, PhyConsts.massPionCharged());
          break;
        default:
            numOther++;
      }
    }

    if(numElectron>0) nElectron = true;
    if(numPhoton>1) nPhoton = true;

    if(nElectron && cutCCstat && cutECstat && cutSCstat){
      h1_ElectronP.fill(electron.p());
      if(electron.p()>=ELECTRON_MOM){
        cutElectronMom = true;
        h1_ElectronP_cut.fill(electron.p());
      }
      LorentzVector vecQ2 = LorentzVector.from(beam);
      // creates a copy of lorentz vector from electron
      LorentzVector  vecE = LorentzVector.from(electron);
      vecQ2.sub(vecE);
      posQ2 = -vecQ2.mass2();
      h1_Q2.fill(posQ2);

      if(posQ2>=Q2_DIS){
        cutQ2 = true;
        h1_Q2_cut.fill(posQ2);
      }

      LorentzVector vecW2 = LorentzVector.from(beam);
      vecW2.add(protonTarget).sub(electron);
      h1_W.fill(vecW2.mass());
      if(vecW2.mass()>=W_DIS){
        cutW = true;
        h1_W_cut.fill(vecW2.mass());
      }

      h2_Q2_vs_W.fill(posQ2,vecW2.mass());
      if(cutQ2 && cutW) h2_Q2_vs_W_cut.fill(posQ2,vecW2.mass());

      nu = beamEnergy - electron.e();
      h1_Nu.fill(nu);

      Xb = posQ2/(2*nu*PhyConsts.massProton());
      h1_Xb.fill(Xb);
      Yb = nu/beamEnergy;
      h1_Yb.fill(Yb);

      h1_cc_nphe.fill(cc_nphe);
      if(cc_nphe>=NPHE_MIN){
        cutCCnphe = true;
        h1_cc_nphe_cut.fill(cc_nphe);
      }

      h2_ECin_vs_ECout.fill(ecin,ecout);
      if(ecin >= ECIN_MIN){
        cutECin = true;
        h2_ECin_vs_ECout_cut.fill(ecin,ecout);
      }

      if(electron.p()>0.0){
        h2_P_vs_ECtotP.fill(electron.p(),ectot/electron.p());
        cutECoverP = EC_SamplingFraction_Cut(electron.p(),ectot,ECsector,12);
        if(ECsector<1 || ECsector>6){
          println counterFile;
          bank.show();
          ccpb.show();
          ecpb.show();
          println ecpb.getRows();
        }
        if(cutECoverP) h2_P_vs_ECtotP_cut.fill(electron.p(),ectot/electron.p());
      }

      if(cutElectronMom && cutECoverP){
        h1_cc_nphe_withEC.fill(cc_nphe);
        if(cutCCnphe) h1_cc_nphe_withEC_cut.fill(cc_nphe);
      }

      h1_dtECSC.fill(ecTime-scTime);
      cutdtECSC = dt_ECSC(ecTime,scTime);
      if(cutdtECSC) h1_dtECSC_cut.fill(ecTime-scTime);

      idElectron = (cutQ2 && cutW  && cutElectronMom && cutECoverP && cutCCnphe && cutdtECSC && cutECin);
    }

    // photon id cut
    if(nPhoton && cutECstat_photon1 && cutECstat_photon2){
      h1_PhotonP1.fill(photon1.p());
      if(photon1.p()>=PHOTON_MOM){
        cutPhotonMom1 = true;
        h1_PhotonP1_cut.fill(photon1.p());
      }
      h1_PhotonP2.fill(photon2.p());
      if(photon2.p()>=PHOTON_MOM){
        cutPhotonMom2 = true;
        h1_PhotonP2_cut.fill(photon2.p());
      }
      h1_PhotonTiming1.fill(timing_photon1);
      cutPhotonTiming1 = PhotonTiming_Cut(timing_photon1);
      if(cutPhotonTiming1){
        h1_PhotonTiming1_cut.fill(timing_photon1);
      }
      h1_PhotonTiming2.fill(timing_photon2);
      cutPhotonTiming2 = PhotonTiming_Cut(timing_photon2);
      if(cutPhotonTiming2){
        h1_PhotonTiming2_cut.fill(timing_photon2);
      }
      h1_PhotonBeta1.fill(beta_photon1);
      cutPhotonBeta1 = PhotonBeta_Cut(beta_photon1);
      if(cutPhotonBeta1){
        h1_PhotonBeta1_cut.fill(beta_photon1);
      }
      h1_PhotonBeta2.fill(beta_photon2);
      cutPhotonBeta2 = PhotonBeta_Cut(beta_photon2);
      if(cutPhotonBeta2){
        h1_PhotonBeta2_cut.fill(beta_photon2);
      }
      Msq_photon1 = Get_Msq(photon1.p(),beta_photon1);
      h1_PhotonMsq1.fill(Msq_photon1);
      cutPhotonMsq1 = PhotonSCMsq_Cut(Msq_photon1,1);
      if(cutPhotonMsq1){
        h1_PhotonMsq1_cut.fill(Msq_photon1);
      }
      Msq_photon2 = Get_Msq(photon2.p(),beta_photon2);
      h1_PhotonMsq2.fill(Msq_photon2);
      cutPhotonMsq2 = PhotonSCMsq_Cut(Msq_photon2,2);
      if(cutPhotonMsq2){
        h1_PhotonMsq2_cut.fill(Msq_photon2);
      }
      idPhoton1 = (cutPhotonMom1 && cutPhotonBeta1 && cutPhotonTiming1 && cutPhotonMsq1);
      idPhoton2 = (cutPhotonMom2 && cutPhotonBeta2 && cutPhotonTiming2 && cutPhotonMsq2);
    }

//    println idElectron;

    if(idElectron && idPhoton1 && idPhoton2){
      theta_ElectronPhoton1 = Math.toDegrees(electron.angle(photon1));
      h1_theta_eg1.fill(theta_ElectronPhoton1);
      if(theta_ElectronPhoton1 > ELETRON_PHOTON_ANGLE) {
        cutAngleElecPhoton1 = true;
        h1_theta_eg1_cut.fill(theta_ElectronPhoton1);
      }
      theta_ElectronPhoton2 = Math.toDegrees(electron.angle(photon2));
      h1_theta_eg2.fill(theta_ElectronPhoton2);
      if(theta_ElectronPhoton2 > ELETRON_PHOTON_ANGLE) {
        cutAngleElecPhoton2 = true;
        h1_theta_eg2_cut.fill(theta_ElectronPhoton2);
      }

      if(cutAngleElecPhoton1 && cutAngleElecPhoton2){
        LorentzVector  pi0 = LorentzVector.from(photon1);
        pi0.add(photon2);
        h1_Mpi0.fill(pi0.mass());
      }
    }

    counterFile++;
  }
  reader.close();
  System.out.println("processed " + counterFile + " in " + args[j]);
  counterTotal += counterFile;
}
System.out.println("processed (total) = " + counterTotal);

TCanvas c1 = new TCanvas("c1",600,600);
c1.cd(0);
c1.draw(h1_Mpi0);

TCanvas c2 = new TCanvas("c2",1200,800);
c2.divide(4,3);
c2.cd(0);
c2.draw(h1_Q2);
c2.draw(h1_Q2_cut,"same");
c2.cd(1);
c2.draw(h1_W);
c2.draw(h1_W_cut,"same");
c2.cd(2);
c2.draw(h2_Q2_vs_W);
c2.cd(3);
c2.draw(h2_Q2_vs_W_cut);
c2.cd(4);
c2.draw(h1_Nu);
c2.cd(5);
c2.draw(h1_Xb);
c2.cd(6);
c2.draw(h1_Yb);
c2.cd(7);
c2.draw(h1_theta_eg1);
c2.draw(h1_theta_eg1_cut,"same");
c2.cd(8);
c2.draw(h1_theta_eg2);
c2.draw(h1_theta_eg2_cut,"same");
c2.cd(9);
c2.draw(h1_ElectronP);
c2.draw(h1_ElectronP_cut,"same");
c2.cd(10);
c2.draw(h1_cc_nphe);
c2.draw(h1_cc_nphe_cut,"same");
c2.cd(11);
c2.draw(h1_cc_nphe_withEC);
c2.draw(h1_cc_nphe_withEC_cut,"same");

TCanvas c3 = new TCanvas("c3",800,800);
c3.divide(2,2);
c3.cd(0);
c3.draw(h2_ECin_vs_ECout);
c3.cd(1);
c3.draw(h2_ECin_vs_ECout_cut);
c3.cd(2);
c3.draw(h2_P_vs_ECtotP);
c3.cd(3);
c3.draw(h2_P_vs_ECtotP_cut);

TCanvas c4 = new TCanvas("c4",600,600);
c4.divide(2,1);
c4.cd(0);
c4.draw(h1_dtECSC);
c4.draw(h1_dtECSC_cut,"same");

TCanvas c5 = new TCanvas("c5",1200,800);
c5.divide(4,2);
c5.cd(0);
c5.draw(h1_PhotonP1);
c5.draw(h1_PhotonP1_cut,"same");
c5.cd(1);
c5.draw(h1_PhotonBeta1);
c5.draw(h1_PhotonBeta1_cut,"same");
c5.cd(2);
c5.draw(h1_PhotonTiming1);
c5.draw(h1_PhotonTiming1_cut,"same");
c5.cd(3);
c5.draw(h1_PhotonMsq1);
c5.draw(h1_PhotonMsq1_cut,"same");
c5.divide(4,2);
c5.cd(4);
c5.draw(h1_PhotonP2);
c5.draw(h1_PhotonP2_cut,"same");
c5.cd(5);
c5.draw(h1_PhotonBeta2);
c5.draw(h1_PhotonBeta2_cut,"same");
c5.cd(6);
c5.draw(h1_PhotonTiming2);
c5.draw(h1_PhotonTiming2_cut,"same");
c5.cd(7);
c5.draw(h1_PhotonMsq2);
c5.draw(h1_PhotonMsq2_cut,"same");
