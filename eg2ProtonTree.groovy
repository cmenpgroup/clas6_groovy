import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

import org.jlab.groot.tree.*; // new import for ntuples

double LIGHTSPEED = 30.0; // speed of light in cm/ns
double W_DIS = 2.0;
double Q2_DIS = 1.0;
double ELECTRON_MOM = 0.64;
double NPHE_MIN = 28;
double ECIN_MIN = 0.06;
int NUM_ELECTRONS = 1;
int NUM_PROTONS = 1;

def Get_TargetIndex = {
  Vec3->
    int ret = 0; // init the return variable

    if (Vec3.z() >= -32.0 &&  Vec3.z() < -28.0) {
        ret = 1; // deuterium target
    } else if (Vec3.z() >= -26.0 && Vec3.z() < -23.0) {
        ret = 2; // nuclear target
    } else {
        ret = 0; // no target
    }
    return ret;
}

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

def ProtonDBeta_Cut = {
  dbeta ->
  boolean ret = (dbeta >= -0.03 && dbeta < 0.02);
  return ret;
}

def Get_Msq = {
  mom, beta->
  double Msq = -99.0;
  double betaSq = beta*beta;
  if(betaSq>0.0) Msq = mom*mom*(1.0-betaSq)/betaSq;
  return Msq;
}

def Get_BetaFromMass = {
  Vec4->
  double ret = -99.0;
  double Psq = Vec4.p()*Vec4.p();
  double Msq = Vec4.mass()*Vec4.mass();
  if(Psq>0.0) ret = 1.0/Math.sqrt(Msq/Psq + 1.0);
  return ret;
}

def Get_CorrectedVertex = {
  Vec3, Vec4->

  double phi_deg = Math.toDegrees(Vec4.phi()); // convert to degrees
  int phi_new = phi_deg+30.0; // shift by 30 deg
  if(phi_new<0)phi_new+=360.0; // if negative, shift positive
  int sect =  Math.floor(phi_new/60.0);

  Vector3 RotatedVertPos = Vector3.from(Vec3);
  Vector3 RotatedVertDir = Vector3.from(Vec4.vect());
  Vector3 TargetPos = new Vector3(0.043,-0.33,0.0);

  RotatedVertPos.rotateZ(-Math.toRadians(60.0*sect));
  RotatedVertDir.rotateZ(-Math.toRadians(60.0*sect));
  TargetPos.rotateZ(-Math.toRadians(60.0*sect));

  double ShiftLength = (TargetPos.x() - RotatedVertPos.x())/RotatedVertDir.x();
  RotatedVertDir.setXYZ(ShiftLength*RotatedVertDir.x(),ShiftLength*RotatedVertDir.y(),ShiftLength*RotatedVertDir.z());
  RotatedVertPos.add(RotatedVertDir);

  Vector3 ParticleVertCorr = Vector3.from(RotatedVertPos);
  ParticleVertCorr.sub(TargetPos.x(),TargetPos.y(),0.0);
  return ParticleVertCorr;
}

int counterFile = 0;
float px, py, pz;
def ElectronList =[];
def ProtonList = [];
def OtherList = [];
def ElectronVecList = [];
def ProtonVecList = [];

PhysicsConstants PhyConsts= new PhysicsConstants();

LorentzVector electron = new LorentzVector(0,0,0,0);
Vector3 v3electron = new Vector3(0,0,0);
LorentzVector proton = new LorentzVector(0,0,0,0);
Vector3 v3proton = new Vector3(0,0,0);

double beamEnergy = 5.1;
LorentzVector beam = new LorentzVector(0.0,0.0,beamEnergy,beamEnergy);
LorentzVector protonTarget = new LorentzVector(0.0,0.0,0.0,PhyConsts.massProton());

def cli = new CliBuilder(usage:'eg2ProtonTree.groovy [options] infile1 infile2 ...')
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
Bank       hevt   = new Bank(reader.getSchemaFactory().getSchema("HEADER::info"));
Bank       bank   = new Bank(reader.getSchemaFactory().getSchema("EVENT::particle"));
Bank       ccpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::ccpb"));
Bank       ecpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::ecpb"));
Bank       scpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::scpb"));

// Define a ntuple tree with 5 variables
TreeFile tree = new TreeFile("ntuple.hipo","protonTree","Run:Event:iTgt:eNum:eIndex:ePx:ePy:ePz:pNum:pIndex:pPx:pPy:pPz:q2:nu:W:zh:pT2:xb:yb");
float[]  treeItem = new float[20];

// Loop over all events
while(reader.hasNext()){
  if(counterFile % printCounter == 0) println counterFile;
  if(maxEvents!=0 && counterFile >= maxEvents) break;

  ElectronList.clear();
  ProtonList.clear();
  OtherList.clear();
  ElectronVecList.clear();
  ProtonVecList.clear();

  reader.nextEvent(event);
  event.read(hevt);
  event.read(bank);
  event.read(ccpb);
  event.read(ecpb);
  event.read(scpb);

  def runNum = hevt.getInt("nrun",0);
  def evtNum = hevt.getInt("nevt",0);

  int rows = bank.getRows();
  for(int i = 0; i < rows; i++){
    switch(bank.getInt("pid",i)){
      case 11: ElectronList.add(i); break;
      case 2212: ProtonList.add(i); break;
      default: OtherList.add(i); break;
    }
  }

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
      v3electron.setXYZ(bank.getFloat("vx",val), bank.getFloat("vy",val), bank.getFloat("vz",val));

      double phi_deg = Math.toDegrees(electron.phi()); // convert to degrees
      Vector3 v3electron_corr = Get_CorrectedVertex(v3electron,electron);

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
        if(electron.p()>=ELECTRON_MOM) cutElectronMom = true;
        LorentzVector vecQ2 = LorentzVector.from(beam);
        // creates a copy of lorentz vector from electron
        LorentzVector  vecE = LorentzVector.from(electron);
        vecQ2.sub(vecE);
        posQ2 = -vecQ2.mass2();

        if(posQ2>=Q2_DIS) cutQ2 = true;

        LorentzVector vecW2 = LorentzVector.from(beam);
        vecW2.add(protonTarget).sub(electron);
        if(vecW2.mass()>=W_DIS) cutW = true;

        nu = beamEnergy - electron.e();

        Xb = posQ2/(2*nu*PhyConsts.massProton());
        Yb = nu/beamEnergy;

        if(cc_nphe>=NPHE_MIN) cutCCnphe = true;
        if(ecin >= ECIN_MIN) cutECin = true;

        if(electron.p()>0.0){
          cutECoverP = EC_SamplingFraction_Cut(electron.p(),ectot,ECsector,12);
          if(ECsector<1 || ECsector>6){
            println counterFile;
            bank.show();
            ccpb.show();
            ecpb.show();
            println ecpb.getRows();
          }
        }

        cutdtECSC = dt_ECSC(ecTime,scTime);

        if(cutQ2 && cutW  && cutElectronMom && cutECoverP && cutCCnphe && cutdtECSC && cutECin){
          ElectronVecList << [px,py,pz,electron.e(),Get_TargetIndex(v3electron_corr),posQ2,nu,vecW2.mass(),Xb,Yb];
        }
      }
    }
  }

  // proton id cuts
  if(ProtonList.size()>=NUM_PROTONS){
    ProtonList.each { val ->
      beta = bank.getFloat("beta",val);
      px = bank.getFloat("px",val);
      py = bank.getFloat("py",val);
      pz = bank.getFloat("pz",val);
      proton.setPxPyPzM(px, py, pz, PhyConsts.massProton());
      v3proton.setXYZ(bank.getFloat("vx",val), bank.getFloat("vy",val), bank.getFloat("vz",val));
      Vector3 v3proton_corr = Get_CorrectedVertex(v3proton,proton);

      beta_proton = Get_BetaFromMass(proton);
      if(ProtonDBeta_Cut(beta - beta_proton)){
        ProtonVecList << [px,py,pz,proton.e(),Get_TargetIndex(v3proton_corr)];
      }
    }
  }

  if(ElectronVecList.size()>=NUM_ELECTRONS && ProtonVecList.size()>=NUM_PROTONS){
    ElectronVecList.eachWithIndex { emList, emInd ->
      LorentzVector emVec = new LorentzVector(emList[0],emList[1],emList[2],emList[3]);
      int emTgt =emList[4];
      float emQsq = emList[5];
      float emNu = emList[6];
      float emW = emList[7];
      float emXb = emList[8];
      float emYb = emList[9];
      ProtonVecList.eachWithIndex { pList, pInd ->
        LorentzVector protonVec = new LorentzVector(pList[0],pList[1],pList[2],pList[3]);
        int pTgt = pList[4];
        if(pTgt==emTgt){
          treeItem[0] = runNum;
          treeItem[1] = evtNum;
          treeItem[2] = pTgt;
          treeItem[3] = ElectronVecList.size();
          treeItem[4] = emInd;
          treeItem[5] = emVec.px();
          treeItem[6] = emVec.py();
          treeItem[7] = emVec.pz();
          treeItem[8] = ProtonVecList.size();
          treeItem[9] = pInd;
          treeItem[10] = protonVec.px();
          treeItem[11] = protonVec.py();
          treeItem[12] = protonVec.pz();
          treeItem[13] = emQsq;
          treeItem[14] = emNu;
          treeItem[15] = emW;
          treeItem[16] = protonVec.e()/emNu;
          treeItem[17] = protonVec.pt()*protonVec.pt();
          treeItem[18] = emXb;
          treeItem[19] = emYb;
          tree.addRow(treeItem);  // add the tree data
        }
      }
    }
  }
  counterFile++;
}
System.out.println("processed (total) = " + counterFile);

tree.close(); // close the tree file
