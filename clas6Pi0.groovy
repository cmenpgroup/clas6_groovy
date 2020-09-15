import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

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
int NUM_ELECTRONS = 1;
int NUM_PHOTONS = 2;

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

def Get_BetaFromMass = {
  mom, mass->
  double ret = -99.0;
  double momSq = mom*mom;

  if(momSq>0.0) ret = 1.0/Math.sqrt((mass*mass)/momSq + 1.0);
  return ret;
}

int pid;
int counterTotal = 0;
int counterFile;
float px, py, pz;
def ElectronList =[];
def PhotonList = [];
def PiPlusList = [];
def PiMinusList = [];
def OtherList = [];
def ElectronVecList = [];
def PhotonVecList = [];
def PhotonComponents = [];

PhysicsConstants PhyConsts= new PhysicsConstants();
System.out.println("electron = " + PhyConsts.massElectron());
System.out.println("proton = " + PhyConsts.massProton());
System.out.println("pi+/- = " + PhyConsts.massPionCharged());
System.out.println("pi0 = " + PhyConsts.massPionNeutral());

LorentzVector electron = new LorentzVector(0,0,0,0);
LorentzVector photon = new LorentzVector(0,0,0,0);
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

H1F h1_theta_eg = new H1F("h1_theta_eg",240,0.0,120.0);
h1_theta_eg.setTitle("Experiment: eg2");
h1_theta_eg.setTitleX("#theta(e #gamma) (deg.)");
h1_theta_eg.setTitleY("Counts");
//h1_theta_eg.setFillColor(43);

H1F h1_theta_eg_cut = new H1F("h1_theta_eg_cut",240,0.0,120.0);
h1_theta_eg_cut.setTitle("Experiment: eg2");
h1_theta_eg_cut.setTitleX("#theta(e #gamma) (deg.)");
h1_theta_eg_cut.setTitleY("Counts");
h1_theta_eg_cut.setFillColor(43);

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

H1F h1_PhotonP = new H1F("h1_PhotonP",100,0.0,2.5);
h1_PhotonP.setTitle("Experiment: eg2");
h1_PhotonP.setTitleX("Momentum(#gamma) (GeV)");
h1_PhotonP.setTitleY("Counts");
//h1_PhotonP.setFillColor(43);

H1F h1_PhotonP_cut = new H1F("h1_PhotonP_cut",100,0.0,2.5);
h1_PhotonP_cut.setTitle("Experiment: eg2");
h1_PhotonP_cut.setTitleX("Momentum(#gamma) (GeV)");
h1_PhotonP_cut.setTitleY("Counts");
h1_PhotonP_cut.setFillColor(43);

H1F h1_PhotonBeta = new H1F("h1_PhotonBeta",100,0.8,1.2);
h1_PhotonBeta.setTitle("Experiment: eg2");
h1_PhotonBeta.setTitleX("#beta(#gamma)");
h1_PhotonBeta.setTitleY("Counts");
//h1_PhotonBeta.setFillColor(43);

H1F h1_PhotonBeta_cut = new H1F("h1_PhotonBeta_cut",100,0.8,1.2);
h1_PhotonBeta_cut.setTitle("Experiment: eg2");
h1_PhotonBeta_cut.setTitleX("#beta(#gamma)");
h1_PhotonBeta_cut.setTitleY("Counts");
h1_PhotonBeta_cut.setFillColor(43);

H1F h1_PhotonMsq = new H1F("h1_PhotonMsq",100,-0.1,0.1);
h1_PhotonMsq.setTitle("Experiment: eg2");
h1_PhotonMsq.setTitleX("SC M^2(#gamma) (GeV^2)");
h1_PhotonMsq.setTitleY("Counts");
//h1_PhotonMsq.setFillColor(43);

H1F h1_PhotonMsq_cut = new H1F("h1_PhotonMsq_cut",100,-0.1,0.1);
h1_PhotonMsq_cut.setTitle("Experiment: eg2");
h1_PhotonMsq_cut.setTitleX("SC M^2(#gamma) (GeV^2)");
h1_PhotonMsq_cut.setTitleY("Counts");
h1_PhotonMsq_cut.setFillColor(43);

H1F h1_PhotonTiming = new H1F("h1_PhotonTiming",120,-30,90);
h1_PhotonTiming.setTitle("Experiment: eg2");
h1_PhotonTiming.setTitleX("#Delta t(#gamma) (ns)");
h1_PhotonTiming.setTitleY("Counts");
//h1_PhotonTiming.setFillColor(43);

H1F h1_PhotonTiming_cut = new H1F("h1_PhotonTiming_cut",120,-30,90);
h1_PhotonTiming_cut.setTitle("Experiment: eg2");
h1_PhotonTiming_cut.setTitleX("#Delta t(#gamma) (ns)");
h1_PhotonTiming_cut.setTitleY("Counts");
h1_PhotonTiming_cut.setFillColor(43);

H1F h1_NumElectronBank = new H1F("h1_NumElectronBank",10,0,10);
h1_NumElectronBank.setTitle("Experiment: eg2");
h1_NumElectronBank.setTitleX("Number e^{-} per event");
h1_NumElectronBank.setTitleY("Counts");
//h1_NumElectronBank.setFillColor(43);

H1F h1_NumElectronPID = new H1F("h1_NumElectronPID",10,0,10);
h1_NumElectronPID.setTitle("Experiment: eg2");
h1_NumElectronPID.setTitleX("Number e^{-} per event");
h1_NumElectronPID.setTitleY("Counts");
h1_NumElectronPID.setFillColor(43);

H1F h1_NumPhotonBank = new H1F("h1_NumPhotonBank",10,0,10);
h1_NumPhotonBank.setTitle("Experiment: eg2");
h1_NumPhotonBank.setTitleX("Number #gamma per event");
h1_NumPhotonBank.setTitleY("Counts");
//h1_NumPhotonBank.setFillColor(43);

H1F h1_NumPhotonPID = new H1F("h1_NumPhotonPID",10,0,10);
h1_NumPhotonPID.setTitle("Experiment: eg2");
h1_NumPhotonPID.setTitleX("Number #gamma per event");
h1_NumPhotonPID.setTitleY("Counts");
h1_NumPhotonPID.setFillColor(43);

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
    ElectronList.clear();
    PhotonList.clear();
    PiPlusList.clear();
    PiMinusList.clear();
    OtherList.clear();
    ElectronVecList.clear();
    PhotonVecList.clear();
    PhotonComponents.clear();

    if(counterFile%25000 == 0) println counterFile;
    reader.nextEvent(event);
    event.read(bank);
    event.read(ccpb);
    event.read(ecpb);
    event.read(scpb);

// A00 - 164866, 302772
// A02 - 94422
// A05 - 140285, 190902

    int rows = bank.getRows();
    for(int i = 0; i < rows; i++){
      pid = bank.getInt("pid",i);
//      beta = bank.getFloat("beta",i);
      switch(pid){
        case 11: ElectronList.add(i); break;
        case 22: PhotonList.add(i); break;
        case 211: PiPlusList.add(i); break;
        case -211: PiMinusList.add(i); break;
        default: OtherList.add(i); break;
      }
    }

    h1_NumElectronBank.fill(ElectronList.size());
    if(ElectronList.size()>=NUM_ELECTRONS){
      ElectronList.each { val ->
        boolean cutElectronMom = false;
        boolean cutQ2 = false;
        boolean cutW = false;
        boolean cutCCnphe = false;
        boolean cutCCstat = false;
        boolean cutECstat = false;
        boolean cutSCstat = false;
        boolean cutECin = false;
        boolean cutECoverP = false;
        boolean cutdtECSC = false;

        px = bank.getFloat("px",val);
        py = bank.getFloat("py",val);
        pz = bank.getFloat("pz",val);
        electron.setPxPyPzM(px, py, pz, PhyConsts.massElectron());
        if(bank.getInt("ccstat",val)>0 && ccpb.getRows()>0){
          cutCCstat = true;
          cc_nphe = ccpb.getFloat("nphe",bank.getInt("ccstat",val)-1);
        }
        if(bank.getInt("ecstat",val)>0 && ecpb.getRows()>0){
          cutECstat = true;
          ECsector = ecpb.getInt("sector",bank.getInt("ecstat",val)-1);
          ecin = ecpb.getFloat("ein",bank.getInt("ecstat",val)-1);
          ecout = ecpb.getFloat("eout",bank.getInt("ecstat",val)-1);
          ectot = ecpb.getFloat("etot",bank.getInt("ecstat",val)-1);
          ecTime = ecpb.getFloat("time",bank.getInt("ecstat",val)-1);
        }
        if(bank.getInt("scstat",val)>0 && scpb.getRows()>0){
          cutSCstat = true;
          scTime = scpb.getFloat("time",bank.getInt("scstat",val)-1);
        }

        if(cutCCstat && cutECstat && cutSCstat){
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

          if(cutQ2 && cutW  && cutElectronMom && cutECoverP && cutCCnphe && cutdtECSC && cutECin){
            ElectronVecList << [px,py,pz,electron.e()];
          }
        }
      }
    }
    h1_NumElectronPID.fill(ElectronVecList.size());

    // photon id cuts
    h1_NumPhotonBank.fill(PhotonList.size());
    if(PhotonList.size()>=NUM_PHOTONS){
      PhotonList.each { val ->
        boolean cutECstat_photon = false;
        boolean cutPhotonMom = false;
        boolean cutPhotonTiming = false;
        boolean cutPhotonBeta = false;
        boolean cutPhotonMsq = false;

        px = bank.getFloat("px",val);
        py = bank.getFloat("py",val);
        pz = bank.getFloat("pz",val);
        photon.setPxPyPzM(px, py, pz, 0.0);
        beta_photon = Get_BetaFromMass(photon.p(),0.0);
        if(bank.getInt("ecstat",val)>0 && ecpb.getRows()>0){
          cutECstat_photon = true;
          ecTime_photon = ecpb.getFloat("time",bank.getInt("ecstat",val)-1);
          ecPath_photon = ecpb.getFloat("path",bank.getInt("ecstat",val)-1);
          timing_photon = ecTime_photon - ecPath_photon/LIGHTSPEED;
        }

        if(cutECstat_photon){
          h1_PhotonP.fill(photon.p());
          if(photon.p()>=PHOTON_MOM){
            cutPhotonMom = true;
            h1_PhotonP_cut.fill(photon.p());
          }
          h1_PhotonTiming.fill(timing_photon);
          cutPhotonTiming = PhotonTiming_Cut(timing_photon);
          if(cutPhotonTiming){
            h1_PhotonTiming_cut.fill(timing_photon);
          }
          h1_PhotonBeta.fill(beta_photon);
          cutPhotonBeta = PhotonBeta_Cut(beta_photon);
          if(cutPhotonBeta){
            h1_PhotonBeta_cut.fill(beta_photon);
          }
          Msq_photon = Get_Msq(photon.p(),beta_photon);
          h1_PhotonMsq.fill(Msq_photon);
          cutPhotonMsq = PhotonSCMsq_Cut(Msq_photon,1);
          if(cutPhotonMsq){
            h1_PhotonMsq_cut.fill(Msq_photon);
          }

          if(cutPhotonMom && cutPhotonBeta && cutPhotonTiming && cutPhotonMsq){
            PhotonVecList << [px,py,pz,photon.e()];
          }
        }
      }
    }
    h1_NumPhotonPID.fill(PhotonVecList.size());

    def PhotonVecCopy = PhotonVecList.clone();

    if(ElectronVecList.size()>=NUM_ELECTRONS && PhotonVecList.size()>=NUM_PHOTONS){
      ElectronVecList.each { emList ->
        LorentzVector emVec = new LorentzVector(emList[0],emList[1],emList[2],emList[3]);
        PhotonVecList.eachWithIndex { list1, ii ->
          LorentzVector gamVec1 = new LorentzVector(list1[0],list1[1],list1[2],list1[3]);
          theta_ElectronPhoton = Math.toDegrees(emVec.angle(gamVec1));
          h1_theta_eg.fill(theta_ElectronPhoton);
          if(theta_ElectronPhoton >= ELETRON_PHOTON_ANGLE) {
            h1_theta_eg_cut.fill(theta_ElectronPhoton);
            PhotonVecCopy.eachWithIndex { list2, jj ->
              if(jj>ii){
                LorentzVector gamVec2 = new LorentzVector(list2[0],list2[1],list2[2],list2[3]);
                if(Math.toDegrees(emVec.angle(gamVec2)) >= ELETRON_PHOTON_ANGLE) {
                  LorentzVector  pi0 = LorentzVector.from(gamVec1);
                  pi0.add(gamVec2);
                  h1_Mpi0.fill(pi0.mass());
                }
              }
            }
          }
        }
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
c2.draw(h1_theta_eg);
c2.draw(h1_theta_eg_cut,"same");
c2.cd(8);
c2.draw(h1_dtECSC);
c2.draw(h1_dtECSC_cut,"same");
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

TCanvas c4 = new TCanvas("c4",800,500);
c4.divide(2,1);
c4.cd(0);
c4.draw(h1_NumElectronBank);
c4.draw(h1_NumElectronPID,"same");
c4.cd(1);
c4.draw(h1_NumPhotonBank);
c4.draw(h1_NumPhotonPID,"same");

TCanvas c5 = new TCanvas("c5",800,800);
c5.divide(2,2);
c5.cd(0);
c5.draw(h1_PhotonP);
c5.draw(h1_PhotonP_cut,"same");
c5.cd(1);
c5.draw(h1_PhotonBeta);
c5.draw(h1_PhotonBeta_cut,"same");
c5.cd(2);
c5.draw(h1_PhotonTiming);
c5.draw(h1_PhotonTiming_cut,"same");
c5.cd(3);
c5.draw(h1_PhotonMsq);
c5.draw(h1_PhotonMsq_cut,"same");
