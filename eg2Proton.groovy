import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.math.*;

import eg2Cuts.clas6beta
import eg2Cuts.clas6EC
import eg2Cuts.eg2Target
import eg2Cuts.clas6Proton

clas6beta myBeta = new clas6beta();  // create the beta object
clas6EC myEC = new clas6EC();  // create the EC object
eg2Target myTarget = new eg2Target();  // create the eg2 target object
clas6Proton myProton = new clas6Proton(); // create the proton object

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;
double LIGHTSPEED = 30.0; // speed of light in cm/ns
double W_DIS = 2.0;
double Q2_DIS = 1.0;
double ELECTRON_MOM = 0.64;
double NPHE_MIN = 28;
double ECIN_MIN = 0.06;
int NUM_ELECTRONS = 1;
int NUM_PROTONS = 1;

int counterFile = 0;
double px, py, pz;
def ElectronList =[];
def ProtonList = [];
def PosChargedList = [];
def OtherList = [];
def ElectronVecList = [];
def ProtonVecList = [];

PhysicsConstants PhyConsts= new PhysicsConstants();

LorentzVector electron = new LorentzVector(0,0,0,0);
Vector3 v3electron = new Vector3(0,0,0);
LorentzVector proton = new LorentzVector(0,0,0,0);
Vector3 v3proton = new Vector3(0,0,0);
LorentzVector partLV = new LorentzVector(0,0,0,0);

double beamEnergy = 5.1;
LorentzVector beam = new LorentzVector(0.0,0.0,beamEnergy,beamEnergy);
LorentzVector protonTarget = new LorentzVector(0.0,0.0,0.0,PhyConsts.massProton());

H1F h1_Q2 = new H1F("h1_Q2","Q^2 (GeV^2)","Counts",100,0.0,5.0);
h1_Q2.setTitle("Experiment: eg2");

H1F h1_Q2_cut = new H1F("h1_Q2_cut","Q^2 (GeV^2)","Counts",100,0.0,5.0);
h1_Q2_cut.setTitle("Experiment: eg2");
h1_Q2_cut.setFillColor(GREEN);

H1F h1_Nu = new H1F("h1_Nu","#nu (GeV)","Counts",100,0.0,5.0);
h1_Nu.setTitle("Experiment: eg2");
h1_Nu.setFillColor(GREEN);

H1F h1_Xb = new H1F("h1_Xb","X_b","Counts",100,0.0,1.5);
h1_Xb.setTitle("Experiment: eg2");
h1_Xb.setFillColor(GREEN);

H1F h1_Yb = new H1F("h1_Yb","Y_b","Counts",100,0.0,1.0);
h1_Yb.setTitle("Experiment: eg2");
h1_Yb.setFillColor(GREEN);

H1F h1_W = new H1F("h1_W","W (GeV)","Counts",160,0.0,3.2);
h1_W.setTitle("Experiment: eg2");

H1F h1_W_cut = new H1F("h1_W_cut","W (GeV)","Counts",160,0.0,3.2);
h1_W_cut.setTitle("Experiment: eg2");
h1_W_cut.setFillColor(GREEN);

H2F h2_Q2_vs_W = new H2F("h1_Q2_vs_W","Experiment: eg2",100,0.0,5.0,160,0.0,3.2);
h2_Q2_vs_W.setTitleX("Q^2 (GeV^2)");
h2_Q2_vs_W.setTitleY("W (GeV)");

H2F h2_Q2_vs_W_cut = new H2F("h1_Q2_vs_W_cut","Experiment: eg2",100,0.0,5.0,160,0.0,3.2);
h2_Q2_vs_W_cut.setTitleX("Q^2 (GeV^2)");
h2_Q2_vs_W_cut.setTitleY("W (GeV)");

H1F h1_ElectronP = new H1F("h1_ElectronP","Momentum(e^-) (GeV)","Counts",100,0.0,5.0);
h1_ElectronP.setTitle("Experiment: eg2");

H1F h1_ElectronP_cut = new H1F("h1_ElectronP_cut","Momentum(e^-) (GeV)","Counts",100,0.0,5.0);
h1_ElectronP_cut.setTitle("Experiment: eg2");
h1_ElectronP_cut.setFillColor(GREEN);

H1F h1_cc_nphe = new H1F("h1_cc_nphe","Number of Photoelectrons","Counts",200,0.0,200.0);
h1_cc_nphe.setTitle("Experiment: eg2");

H1F h1_cc_nphe_cut = new H1F("h1_cc_nphe_cut","Number of Photoelectrons","Counts",200,0.0,200.0);
h1_cc_nphe_cut.setTitle("Experiment: eg2");
h1_cc_nphe_cut.setFillColor(GREEN);

H1F h1_cc_nphe_withEC = new H1F("h1_cc_nphe_withEC","Number of Photoelectrons","Counts",200,0.0,200.0);
h1_cc_nphe_withEC.setTitle("Experiment: eg2");

H1F h1_cc_nphe_withEC_cut = new H1F("h1_cc_nphe_withEC_cut","Number of Photoelectrons","Counts",200,0.0,200.0);
h1_cc_nphe_withEC_cut.setTitle("Experiment: eg2");
h1_cc_nphe_withEC_cut.setFillColor(GREEN);

H2F h2_ECin_vs_ECout= new H2F("h1_ECin_vs_ECout","Experiment: eg2",100,0.0,1.0,100,0.0,1.0);
h2_ECin_vs_ECout.setTitleX("ECin (GeV^2)");
h2_ECin_vs_ECout.setTitleY("ECout (GeV)");

H2F h2_ECin_vs_ECout_cut= new H2F("h1_ECin_vs_ECout","Experiment: eg2",100,0.0,1.0,100,0.0,1.0);
h2_ECin_vs_ECout_cut.setTitleX("ECin (GeV^2)");
h2_ECin_vs_ECout_cut.setTitleY("ECout (GeV)");

H2F h2_P_vs_ECtotP = new H2F("h2_P_vs_ECtotP","Experiment: eg2",100,0.0,5.0,100,0.0,0.5);
h2_P_vs_ECtotP.setTitleX("Momentum(e^-) (GeV)");
h2_P_vs_ECtotP.setTitleY("ECtot (GeV)");

H2F h2_P_vs_ECtotP_cut = new H2F("h2_P_vs_ECtotP_cut","Experiment: eg2",100,0.0,5.0,100,0.0,0.5);
h2_P_vs_ECtotP_cut.setTitleX("Momentum(e^-) (GeV)");
h2_P_vs_ECtotP_cut.setTitleY("ECtot (GeV)");

H1F h1_dtECSC = new H1F("h1_dtECSC","t(EC)-t(SC) (ns)","Counts",100,-10.0,10.0);
h1_dtECSC.setTitle("Experiment: eg2");

H1F h1_dtECSC_cut = new H1F("h1_dtECSC_cut","t(EC)-t(SC) (ns)","Counts",100,-10.0,10.0);
h1_dtECSC_cut.setTitle("Experiment: eg2");
h1_dtECSC_cut.setFillColor(GREEN);

H1F h1_NumElectronBank = new H1F("h1_NumElectronBank","# of e^{-} per event","Counts",10,0,10);
h1_NumElectronBank.setTitle("Experiment: eg2");

H1F h1_NumElectronPID = new H1F("h1_NumElectronPID","# of e^{-} per event","Counts",10,0,10);
h1_NumElectronPID.setTitle("Experiment: eg2");
h1_NumElectronPID.setFillColor(GREEN);

H1F h1_NumProtonBank = new H1F("h1_NumProtonBank","# of protons per event","Counts",10,0,10);
h1_NumProtonBank.setTitle("Experiment: eg2");

H1F h1_NumProtonPID = new H1F("h1_NumProtonPID","# of protons per event","Counts",10,0,10);
h1_NumProtonPID.setTitle("Experiment: eg2");
h1_NumProtonPID.setFillColor(GREEN);

H1F h1_ProtonP = new H1F("h1_ProtonP","Momentum(proton) (GeV)","Counts",100,0.0,3.5);
h1_ProtonP.setTitle("Experiment: eg2");
h1_ProtonP.setFillColor(GREEN);

H2F h2_dBetaVsP_proton = new H2F("h2_dBetaVsP_proton","Experiment: eg2",100,0.0,3.5,200,-0.1,0.1);
h2_dBetaVsP_proton.setTitleX("Momentum (GeV/c)");
h2_dBetaVsP_proton.setTitleY("#Delta #beta (proton)");

H2F h2_dBetaVsP_proton_cut = new H2F("h2_dBetaVsP_proton_cut","Experiment: eg2",100,0.0,3.5,200,-0.1,0.1);
h2_dBetaVsP_proton_cut.setTitleX("Momentum (GeV/c)");
h2_dBetaVsP_proton_cut.setTitleY("#Delta #beta (proton)");

H2F h2_Vz_phi = new H2F("h2_Vz_phi","Experiment: eg2 - Electrons",100,-33,-20.0,360,-180.0,180.0);
h2_Vz_phi.setTitleX("Vertex z (cm)");
h2_Vz_phi.setTitleY("#phi (deg.)");

H2F h2_Vz_phi_corr = new H2F("h2_Vz_phi_corr","Experiment: eg2 - Electrons",100,-33,-20.0,360,-180.0,180.0);
h2_Vz_phi_corr.setTitleX("Vertex z (cm)");
h2_Vz_phi_corr.setTitleY("#phi (deg.)");

H2F h2_Vz_phi_prot = new H2F("h2_Vz_phi_prot","Experiment: eg2 - Protons",100,-33,-20.0,360,-180.0,180.0);
h2_Vz_phi_prot.setTitleX("Vertex z (cm)");
h2_Vz_phi_prot.setTitleY("#phi (deg.)");

H2F h2_Vz_phi_prot_corr = new H2F("h2_Vz_phi_prot_corr","Experiment: eg2 - Protons",100,-33,-20.0,360,-180.0,180.0);
h2_Vz_phi_prot_corr.setTitleX("Vertex z (cm)");
h2_Vz_phi_prot_corr.setTitleY("#phi (deg.)");

double P_full_lo = 0.0;
double P_full_hi = 3.0;
double P_bin_width = 0.03;
int P_full_bins = (P_full_hi - P_full_lo)/P_bin_width;
H2F h2_dTOF_VS_P = new H2F("h2_dTOF_VS_P","Experiment: eg2 - Protons",P_full_bins,P_full_lo,P_full_hi,160,-16.0,16.0);
h2_dTOF_VS_P.setTitleX("Momentum (GeV/c)");
h2_dTOF_VS_P.setTitleY("#DeltaTOF (ns)");

H2F h2_dTOF_VS_P_cut = new H2F("h2_dTOF_VS_P_cut","Experiment: eg2 - Protons",P_full_bins,P_full_lo,P_full_hi,160,-16.0,16.0);
h2_dTOF_VS_P_cut.setTitleX("Momentum (GeV/c)");
h2_dTOF_VS_P_cut.setTitleY("#DeltaTOF (ns)");

String[] TgtLabel = ["D2","Nuc","Other"];
String[] xLabel = ["Q^2 (GeV^2)","#nu (GeV)","zh","pT^2 (GeV^2)","zLC"];
String[] Var = ["Qsq","nu","zh","pT2","zLC"];
int[] nbins = [50,50,30,30,30];
double[] xlo = [Q2_DIS,2.0,0.0,0.0,0.0];
double[] xhi = [5.0,4.5,1.0,2.0,1.0];
H1F[][] h1_nProton = new H1F[Var.size()][TgtLabel.size()];

TgtLabel.eachWithIndex {nTgt, iTgt->
  Var.eachWithIndex { nVar, iVar->
    String hname = "h1_nProton_" + nTgt + "_" + nVar;
    h1_nProton[iVar][iTgt] = new H1F(hname,xLabel[iVar],"Counts",nbins[iVar],xlo[iVar],xhi[iVar]);
    h1_nProton[iVar][iTgt].setTitle("eg2 - " + nTgt);
    h1_nProton[iVar][iTgt].setFillColor(YELLOW - iTgt);
  }
}

def cli = new CliBuilder(usage:'eg2Proton.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
cli.c(longOpt:'counter', args:1, argName:'count by events', type: int, 'Event progress counter')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def printCounter = 20000;
if(options.c) printCounter = options.c;

def maxEvents = 0;
if(options.M) maxEvents = options.M;

def extraArguments = options.arguments()
if (extraArguments.isEmpty()){
  println "No input file!";
  cli.usage();
  return;
}

HipoChain reader = new HipoChain();

extraArguments.each { infile ->
  reader.addFile(infile);
}
reader.open();

Event      event  = new Event();
Bank       bank   = new Bank(reader.getSchemaFactory().getSchema("EVENT::particle"));
Bank       ccpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::ccpb"));
Bank       ecpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::ecpb"));
Bank       scpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::scpb"));

// Loop over all events
while(reader.hasNext()){
  if(counterFile % printCounter == 0) println counterFile;
  if(maxEvents!=0 && counterFile >= maxEvents) break;

  ElectronList.clear();
  ProtonList.clear();
  OtherList.clear();
  ElectronVecList.clear();
  ProtonVecList.clear();
  PosChargedList.clear();

  reader.nextEvent(event);
  event.read(bank);
  event.read(ccpb);
  event.read(ecpb);
  event.read(scpb);

  int rows = bank.getRows();
  for(int i = 0; i < rows; i++){
    switch(i){
      case {bank.getInt("pid",i)==11}: ElectronList.add(i); break;
//      case {bank.getInt("pid",i)==2212}: ProtonList.add(i); break;
      case {bank.getInt("charge",i)>0}: PosChargedList.add(i); break;
      default: OtherList.add(i); break;
    }
  }

  // electron ID
  h1_NumElectronBank.fill(ElectronList.size());
  if(ElectronList.size()>=NUM_ELECTRONS){  // check that there are electrons in event
    ElectronList.each { val ->
      // initialize electron cuts
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

      // read in the momentum components
      px = bank.getFloat("px",val);
      py = bank.getFloat("py",val);
      pz = bank.getFloat("pz",val);
      electron.setPxPyPzM(px, py, pz, PhyConsts.massElectron()); // create electron 4-vector
      v3electron.setXYZ(bank.getFloat("vx",val), bank.getFloat("vy",val), bank.getFloat("vz",val));

      double phi_deg = Math.toDegrees(electron.phi()); // convert to degrees
      h2_Vz_phi.fill(v3electron.z(),phi_deg);
      Vector3 v3electron_corr = myTarget.Get_CorrectedVertex(v3electron,electron); // corrected vertex
      h2_Vz_phi_corr.fill(v3electron_corr.z(),phi_deg);

      if(bank.getInt("ccstat",val)>0 && ccpb.getRows()>0){ // check CC
        cutCCstat = true;
        cc_nphe = ccpb.getFloat("nphe",bank.getInt("ccstat",val)-1);
      }
      if(bank.getInt("ecstat",val)>0 && ecpb.getRows()>0){ // check EC
        cutECstat = true;
        ECsector = ecpb.getInt("sector",bank.getInt("ecstat",val)-1);
        ecin = ecpb.getFloat("ein",bank.getInt("ecstat",val)-1);
        ecout = ecpb.getFloat("eout",bank.getInt("ecstat",val)-1);
        ectot = ecpb.getFloat("etot",bank.getInt("ecstat",val)-1);
        ecTime = ecpb.getFloat("time",bank.getInt("ecstat",val)-1);
      }
      if(bank.getInt("scstat",val)>0 && scpb.getRows()>0){ // check SC
        cutSCstat = true;
        scTime = scpb.getFloat("time",bank.getInt("scstat",val)-1);
        scPath = scpb.getFloat("path",bank.getInt("scstat",val)-1);
        tofElectron = scTime - (scPath/LIGHTSPEED);
      }

      if(cutCCstat && cutECstat && cutSCstat){ // proceed if EC && CC && SC
        h1_ElectronP.fill(electron.p());
        if(electron.p()>=ELECTRON_MOM){  // electron momentum cut
          cutElectronMom = true;
          h1_ElectronP_cut.fill(electron.p());
        }
        LorentzVector vecQ2 = LorentzVector.from(beam);   // calculate Q-squared, first copy incident e- 4-vector
        vecQ2.sub(electron);  // calculate Q-squared, subtract scattered e- 4-vector
        posQ2 = -vecQ2.mass2(); // calcuate Q-squared, make into a positive value
        h1_Q2.fill(posQ2);

        if(posQ2>=Q2_DIS){  // check Q-squared cut
          cutQ2 = true;
          h1_Q2_cut.fill(posQ2);
        }

        LorentzVector vecW2 = LorentzVector.from(beam); // calculate W, first copy incident e- 4-vector
        vecW2.add(protonTarget).sub(electron); // calculate W, add target proton 4-vector and subtract scattered e- 4-vector
        h1_W.fill(vecW2.mass());
        if(vecW2.mass()>=W_DIS){ // check W cut
          cutW = true;
          h1_W_cut.fill(vecW2.mass());
        }

        h2_Q2_vs_W.fill(posQ2,vecW2.mass());
        if(cutQ2 && cutW) h2_Q2_vs_W_cut.fill(posQ2,vecW2.mass());

        nu = beamEnergy - electron.e(); // calculate nu
        h1_Nu.fill(nu);

        Xb = posQ2/(2*nu*PhyConsts.massProton()); // calcuate x-byorken
        h1_Xb.fill(Xb);
        Yb = nu/beamEnergy; // calculate normalized nu
        h1_Yb.fill(Yb);

        h1_cc_nphe.fill(cc_nphe);
        if(cc_nphe>=NPHE_MIN){ // check CC nphe cut
          cutCCnphe = true;
          h1_cc_nphe_cut.fill(cc_nphe);
        }

        h2_ECin_vs_ECout.fill(ecin,ecout); // check EC inner energy cut
        if(ecin >= ECIN_MIN){
          cutECin = true;
          h2_ECin_vs_ECout_cut.fill(ecin,ecout);
        }

        if(electron.p()>0.0){ // check EC total energy vs momentum cut
          h2_P_vs_ECtotP.fill(electron.p(),ectot/electron.p());
          cutECoverP = myEC.EC_SamplingFraction_Cut(electron.p(),ectot,ECsector,12);
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

        // check cut on EC - SC timing
        h1_dtECSC.fill(ecTime-scTime);
        cutdtECSC = myEC.dt_ECSC(ecTime,scTime);
        if(cutdtECSC) h1_dtECSC_cut.fill(ecTime-scTime);

        // check all electron ID cuts
        if(cutQ2 && cutW  && cutElectronMom && cutECoverP && cutCCnphe && cutdtECSC && cutECin){
          ElectronVecList << [px,py,pz,electron.e(),myTarget.Get_TargetIndex(v3electron_corr),posQ2,nu,tofElectron];
        }
      }
    }
  }
  h1_NumElectronPID.fill(ElectronVecList.size());

  // start proton ID with TOF cut
  if(PosChargedList.size()>0 && ElectronVecList.size()>0){
    PosChargedList.each { val ->
      partLV.setPxPyPzM(bank.getFloat("px",val),  bank.getFloat("py",val), bank.getFloat("pz",val), PhyConsts.massProton());

      if(bank.getInt("scstat",val)>0 && scpb.getRows()>0 && myProton.LowMomentumCut(partLV.p())){
        scTimeProton = scpb.getFloat("time",bank.getInt("scstat",val)-1);
        scPathProton = scpb.getFloat("path",bank.getInt("scstat",val)-1);
        tofProton = scTimeProton - (scPathProton/LIGHTSPEED)*Math.sqrt(Math.pow(PhyConsts.massProton()/partLV.p(),2)+1);

        for(int eIndex=0; eIndex<ElectronVecList.size(); eIndex++){
          def emList = ElectronVecList.get(eIndex);
          h2_dTOF_VS_P.fill(partLV.p(),tofProton-emList[7]);
          if(myProton.Get_ProtonTOF_Cut(partLV.p(),tofProton-emList[7])){
            h2_dTOF_VS_P_cut.fill(partLV.p(),tofProton-emList[7]);
            ProtonList.add(val);
            break;
          }
        }
      }
    }
  }

  // proton id cuts
  h1_NumProtonBank.fill(ProtonList.size());
  if(ProtonList.size()>=NUM_PROTONS){
    ProtonList.each { val ->
      beta = bank.getFloat("beta",val);
      proton.setPxPyPzM(bank.getFloat("px",val), bank.getFloat("py",val), bank.getFloat("pz",val), PhyConsts.massProton());
      v3proton.setXYZ(bank.getFloat("vx",val), bank.getFloat("vy",val), bank.getFloat("vz",val));

      if(bank.getInt("scstat",val)>0 && scpb.getRows()>0){
        scTimeProton = scpb.getFloat("time",bank.getInt("scstat",val)-1);
        scPathProton = scpb.getFloat("path",bank.getInt("scstat",val)-1);
        tofProton = scTimeProton - (scPathProton/LIGHTSPEED)*Math.sqrt(Math.pow(PhyConsts.massProton()/proton.p(),2)+1);
      }

      h2_Vz_phi_prot.fill(v3proton.z(),Math.toDegrees(proton.phi()));
      Vector3 v3proton_corr = myTarget.Get_CorrectedVertex(v3proton,proton);
      h2_Vz_phi_prot_corr.fill(v3proton_corr.z(),Math.toDegrees(proton.phi()));

      beta_proton = myBeta.Get_BetaFromLorentzVecMass(proton);
      h2_dBetaVsP_proton.fill(proton.p(),beta - beta_proton);

      if(myBeta.ProtonDBeta_Cut(beta - beta_proton)){
        h1_ProtonP.fill(proton.p());
        h2_dBetaVsP_proton_cut.fill(proton.p(),beta - beta_proton);
      }
      // store the proton info that passed the ID cuts
      ProtonVecList << [proton.px(),proton.py(),proton.pz(),proton.e(),myTarget.Get_TargetIndex(v3proton_corr),tofProton];
    }
  }
  h1_NumProtonPID.fill(ProtonVecList.size());

  if(ElectronVecList.size()>=NUM_ELECTRONS && ProtonVecList.size()>=NUM_PROTONS){
    ElectronVecList.each { emList ->
      LorentzVector emVec = new LorentzVector(emList[0],emList[1],emList[2],emList[3]);
      int emTgt =emList[4];
      float emQsq = emList[5];
      float emNu = emList[6];
      ProtonVecList.each { pList ->
        LorentzVector protonVec = new LorentzVector(pList[0],pList[1],pList[2],pList[3]);
        int pTgt = pList[4];
        if(pTgt==emTgt){
          h1_nProton[0][pTgt].fill(emQsq);
          h1_nProton[1][pTgt].fill(emNu);
          h1_nProton[2][pTgt].fill(protonVec.e()/emNu); // zh - fractional quark energy
          h1_nProton[3][pTgt].fill(protonVec.pt()*protonVec.pt());
          h1_nProton[4][pTgt].fill((protonVec.e() + protonVec.pz())/(PhyConsts.massProton() + 2*emNu)); // zLC - lightcone fractional quark energy
        }
      }
    }
  }
  counterFile++;
}
System.out.println("processed (total) = " + counterFile);

//TCanvas c1 = new TCanvas("c1",600,600);
//c1.cd(0);
//c1.draw(h1_Mpi0);

int c2_title_size = 22;
TCanvas c2 = new TCanvas("c2",1400,900);
c2.divide(4,3);
c2.cd(0);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_Q2);
c2.draw(h1_Q2_cut,"same");
c2.cd(1);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_W);
c2.draw(h1_W_cut,"same");
c2.cd(2);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h2_Q2_vs_W);
c2.cd(3);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h2_Q2_vs_W_cut);
c2.cd(4);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_Nu);
c2.cd(5);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_Xb);
c2.cd(6);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_Yb);
c2.cd(7);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_dtECSC);
c2.draw(h1_dtECSC_cut,"same");
c2.cd(8);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_ElectronP);
c2.draw(h1_ElectronP_cut,"same");
c2.cd(9);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_cc_nphe);
c2.draw(h1_cc_nphe_cut,"same");
c2.cd(10);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_cc_nphe_withEC);
c2.draw(h1_cc_nphe_withEC_cut,"same");
//c2.save("ElectronID_proton.png");

int c3_title_size = 24;
TCanvas c3 = new TCanvas("c3",800,800);
c3.divide(2,2);
c3.cd(0);
c3.getPad().getAxisZ().setLog(true);
c3.getPad().setTitleFontSize(c3_title_size);
c3.draw(h2_ECin_vs_ECout);
c3.cd(1);
c3.getPad().getAxisZ().setLog(true);
c3.getPad().setTitleFontSize(c3_title_size);
c3.draw(h2_ECin_vs_ECout_cut);
c3.cd(2);
c3.getPad().setTitleFontSize(c3_title_size);
c3.draw(h2_P_vs_ECtotP);
c3.cd(3);
c3.getPad().setTitleFontSize(c3_title_size);
c3.draw(h2_P_vs_ECtotP_cut);
//c3.save("EC_proton.png");

int c4_title_size = 24;
TCanvas c4 = new TCanvas("c4",800,500);
c4.divide(2,1);
c4.cd(0);
c4.getPad().setTitleFontSize(c4_title_size);
c4.getPad().getAxisY().setLog(true);
c4.draw(h1_NumElectronBank);
c4.draw(h1_NumElectronPID,"same");
c4.cd(1);
c4.getPad().setTitleFontSize(c4_title_size);
c4.getPad().getAxisY().setLog(true);
c4.draw(h1_NumProtonBank);
c4.draw(h1_NumProtonPID,"same");
//c4.save("Num_proton.png");

int c5_title_size = 24;
TCanvas c5 = new TCanvas("c5",1000,500);
c5.divide(3,1);
c5.cd(0);
c5.getPad().setTitleFontSize(c5_title_size);
c5.draw(h1_ProtonP);
c5.cd(1);
c5.getPad().setTitleFontSize(c5_title_size);
c5.draw(h2_dBetaVsP_proton);
c5.cd(2);
c5.getPad().setTitleFontSize(c5_title_size);
c5.draw(h2_dBetaVsP_proton_cut);
//c5.save("dBeta_proton.png");

int c6_title_size = 24;
TCanvas c6 = new TCanvas("c6",800,800);
c6.divide(2,2);
c6.cd(0);
c6.getPad().getAxisZ().setLog(true);
c6.getPad().setTitleFontSize(c6_title_size);
c6.draw(h2_Vz_phi);
c6.cd(1);
c6.getPad().getAxisZ().setLog(true);
c6.getPad().setTitleFontSize(c6_title_size);
c6.draw(h2_Vz_phi_corr);
c6.cd(2);
c6.getPad().getAxisZ().setLog(true);
c6.getPad().setTitleFontSize(c6_title_size);
c6.draw(h2_Vz_phi_prot);
c6.cd(3);
c6.getPad().getAxisZ().setLog(true);
c6.getPad().setTitleFontSize(c6_title_size);
c6.draw(h2_Vz_phi_prot_corr);
//c6.save("Vert_proton_eg2.png");

TCanvas c7 = new TCanvas("c7",1200,800);
int canCount = 0;
int c7_title_size = 24;
c7.divide(Var.size(),3);
TgtLabel.eachWithIndex {nTgt, iTgt->
  if(nTgt!="Other"){
    Var.eachWithIndex { nVar, iVar->
      c7.cd(canCount);
      c7.getPad().setTitleFontSize(c7_title_size);
      c7.draw(h1_nProton[iVar][iTgt]);
      canCount++;
    }
  }
}

H1F[] h1_mrProton = new H1F[Var.size()];
GraphErrors[] gr_mrProton = new GraphErrors[Var.size()];
Var.eachWithIndex{nVar, iVar->
  c7.cd(canCount+iVar);
  c7.getPad().setTitleFontSize(c7_title_size);
  h1_mrProton[iVar] = H1F.divide(h1_nProton[iVar][1],h1_nProton[iVar][0]);
  h1_mrProton[iVar].setName("h1_mrProton_" + nVar);
  h1_mrProton[iVar].setFillColor(GREEN);
  gr_mrProton[iVar] = h1_mrProton[iVar].getGraph();
  gr_mrProton[iVar].setTitle("eg2 - C/D2");
  gr_mrProton[iVar].setTitleX(xLabel[iVar]);
  gr_mrProton[iVar].setTitleY("R^p");
  gr_mrProton[iVar].setMarkerColor(3);
  gr_mrProton[iVar].setLineColor(3);
  gr_mrProton[iVar].setMarkerSize(3);
  c7.draw(gr_mrProton[iVar]);
}
//c7.save("MRproton.png");

TCanvas[] cMR = new TCanvas[Var.size()];
int cMR_title_size = 24;
Var.eachWithIndex { nVar, iVar->
  canCount = 0;
  def cName = "can_" + nVar;
  cMR[iVar] = new TCanvas(cName,1000,500);
  cMR[iVar].divide(3,1);
  TgtLabel.eachWithIndex {nTgt, iTgt->
    cMR[iVar].cd(canCount);
    cMR[iVar].getPad().setTitleFontSize(cMR_title_size);
    if(nTgt!="Other") {cMR[iVar].draw(h1_nProton[iVar][iTgt]); canCount++;}
  }
  cMR[iVar].cd(canCount);
  cMR[iVar].draw(gr_mrProton[iVar]);
  def cFile = "MRproton_" + nVar + ".png";
  cMR[iVar].save(cFile);
}

int c8_title_size = 24;
TCanvas c8 = new TCanvas("c8",800,500);
c8.divide(2,1);
c8.cd(0);
c8.getPad().setTitleFontSize(c8_title_size);
c8.getPad().getAxisZ().setLog(true);
c8.draw(h2_dTOF_VS_P);

F1D f1l = new F1D("f1l","[a]+[b]*x+[c]*x*x+[d]*x*x*x", 0.8, 3.0);
double[] highPl = (double[])myProton.Get_ProtonCutPars("highMomLower");
f1l.setParameters(highPl);
f1l.setLineWidth(5);
f1l.setLineStyle(1);
f1l.setOptStat(0);
c8.draw(f1l,"same");

F1D f1u = new F1D("f1u","[a]+[b]*x+[c]*x*x+[d]*x*x*x", 0.8, 3.0);
double[] highPu = (double[])myProton.Get_ProtonCutPars("highMomUpper");
f1u.setParameters(highPu);
f1u.setLineWidth(5);
f1u.setLineStyle(1);
f1u.setOptStat(0);
c8.draw(f1u,"same");

String fcn9 = "[a]+[b]*x+[c]*x*x+[d]*x*x*x+[e]*x*x*x*x+[f]*x*x*x*x*x+[g]*x*x*x*x*x*x+[h]*x*x*x*x*x*x*x+[i]*x*x*x*x*x*x*x*x+[j]*x*x*x*x*x*x*x*x*x"
F1D f2l = new F1D("f2l",fcn9, 0.2, 0.8);
double[] lowPl = (double[])myProton.Get_ProtonCutPars("highMomLower");
//f2l.setParameters(lowPl);
f2l.setLineColor(34);
f2l.setLineWidth(5);
f2l.setLineStyle(1);
f2l.setOptStat(0);
c8.draw(f2l,"same");

F1D f2u = new F1D("f2u",fcn9, 0.2, 0.8);
double[] lowPu = (double[])myProton.Get_ProtonCutPars("highMomUpper");
f2u.setParameters(lowPu);
//f2u.setLineColor(34);
f2u.setLineWidth(5);
f2u.setLineStyle(1);
f2u.setOptStat(0);
c8.draw(f2u,"same");

c8.cd(1);
c8.getPad().setTitleFontSize(c8_title_size);
c8.getPad().getAxisZ().setLog(true);
c8.draw(h2_dTOF_VS_P_cut);
