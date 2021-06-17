import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*; // new import for ntuples

import eg2Cuts.clas6beta
import eg2Cuts.clas6EC
import eg2Cuts.eg2Target
import eg2Cuts.clas6Proton
import eg2Cuts.clas6FidCuts
import kinematics.ReactionKine

clas6beta myBeta = new clas6beta();  // create the beta object
clas6EC myEC = new clas6EC();  // create the EC object
eg2Target myTarget = new eg2Target();  // create the eg2 target object
clas6Proton myProton = new clas6Proton(); // create the proton object
clas6FidCuts myFidCuts = new clas6FidCuts(); // create the CLAS6 Fiducial Cuts object
ReactionKine myRK = new ReactionKine(); // create object for reaction kinematics

long st = System.currentTimeMillis(); // start time

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;
double ELECTRON_MOM = 0.64;
double NPHE_MIN = 28;
double ECIN_MIN = 0.06;
int NUM_ELECTRONS = 1;
int NUM_PROTONS = 1;

double beamEnergy = myTarget.Get_Beam_Energy();
println "Beam " + beamEnergy + " GeV";
double W_DIS = myTarget.Get_W_DIS();
double Q2_DIS = myTarget.Get_Q2_DIS();
double YB_DIS = myTarget.Get_YB_DIS();

int counterFile = 0;
int counterProtonD2 = 0;
int counterProtonSolid = 0;
int counterProtonOther = 0;
def ElectronList =[];
def ProtonList = [];
def OtherList = [];
def ElectronVecList = [];
def ProtonVecList = [];
def PosChargedList = [];

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
H2F h2_dTOF_VS_P_cut_not2212 = new H2F("h2_dTOF_VS_P_cut_not2212","Experiment: eg2 - Protons",P_full_bins,P_full_lo,P_full_hi,160,-16.0,16.0);
h2_dTOF_VS_P_cut_not2212.setTitleX("Momentum (GeV/c)");
h2_dTOF_VS_P_cut_not2212.setTitleY("#DeltaTOF (ns)");

H1F h1_dTOF_not2212 = new H1F("h1_dTOF_not2212",400,-0.5,399.5);
h1_dTOF_not2212.setTitleX("PID");
h1_dTOF_not2212.setTitleY("Counts");

int nECx = 450;
double ECxLo = -450.0;
double ECxHi = 450.0;
int nECy = 450;
double ECyLo = -450.0;
double ECyHi = 450.0;
String[] histsECxy = ["h2_EC_XvsY","h2_EC_XvsY_antiFid","h2_EC_XvsY_Fid"];
H2F[] h2_EC_XvsY = new H2F[histsECxy.size()];

histsECxy.eachWithIndex { hname, ih->
  h2_EC_XvsY[ih] = new H2F(hname,"EC x vs y",nECx,ECxLo,ECxHi,nECy,ECyLo,ECyHi);
  h2_EC_XvsY[ih].setTitleX("x (cm)");
  h2_EC_XvsY[ih].setTitleY("y (cm)");
}

int nECstrips = 225;
double ECstripLo = 0.0;
double ECstripHi = 450.0;
H1F h1_EC_U = new H1F("h1_EC_U","U (cm)","Counts",nECstrips,ECstripLo,ECstripHi);
h1_EC_U.setTitle("EC Hit Position");

H1F h1_EC_V = new H1F("h1_EC_V","V (cm)","Counts",nECstrips,ECstripLo,ECstripHi);
h1_EC_V.setTitle("EC Hit Position");

H1F h1_EC_W = new H1F("h1_EC_W","W (cm)","Counts",nECstrips,ECstripLo,ECstripHi);
h1_EC_W.setTitle("EC Hit Position");

H1F h1_EC_U_fid = new H1F("h1_EC_U_fid","U (cm)","Counts",nECstrips,ECstripLo,ECstripHi);
h1_EC_U_fid.setTitle("EC Hit Position");
h1_EC_U_fid.setFillColor(GREEN);

H1F h1_EC_V_fid = new H1F("h1_EC_V_fid","V (cm)","Counts",nECstrips,ECstripLo,ECstripHi);
h1_EC_V_fid.setTitle("EC Hit Position");
h1_EC_V_fid.setFillColor(GREEN);

H1F h1_EC_W_fid = new H1F("h1_EC_W_fid","W (cm)","Counts",nECstrips,ECstripLo,ECstripHi);
h1_EC_W_fid.setTitle("EC Hit Position");
h1_EC_W_fid.setFillColor(GREEN);

PhysicsConstants PhyConsts= new PhysicsConstants();
double LIGHTSPEED = PhyConsts.speedOfLight(); // speed of light in cm/ns
println "Speed of light = " + LIGHTSPEED + " cm/ns";

LorentzVector electron = new LorentzVector(0,0,0,0);
Vector3 v3electron = new Vector3(0,0,0);
LorentzVector proton = new LorentzVector(0,0,0,0);
Vector3 v3proton = new Vector3(0,0,0);
LorentzVector partLV = new LorentzVector(0,0,0,0);

Vector3 ecXYZ = new Vector3(0.0,0.0,0.0);
Vector3 ecUVW = new Vector3(0.0,0.0,0.0);

myRK.setBeam(beamEnergy,PhyConsts.massElectron());
myRK.setTarget(PhyConsts.massProton());

def cli = new CliBuilder(usage:'eg2ProtonTree.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
cli.c(longOpt:'counter', args:1, argName:'count by events', type: int, 'Event progress counter')
cli.o(longOpt:'output', args:1, argName:'Ntuple output file', type: String, 'Output file name')
cli.g(longOpt:'graph', 'Graph monitoring histograms')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def printCounter = 20000;
if(options.c) printCounter = options.c;

def maxEvents = 0;
if(options.M) maxEvents = options.M;

def outFile = "eg2ProtonNtuple.hipo";
if(options.o) outFile = options.o;

boolean bGraph = false;
if(options.g) bGraph = true;

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
TreeFileWriter tree = new TreeFileWriter(outFile,"protonTree","Run:Event:iTgt:eNum:eIndex:ePx:ePy:ePz:eTheta:ePhi:eVx:eVy:eVz:pNum:pIndex:pPx:pPy:pPz:pTheta:pPhi:pVx:pVy:pVz:q2:nu:W:zh:zLC:pT2:xb:yb:phiPQ:pFidCut:eFidCut:eFidEC");
float[]  treeItem = new float[35];

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
  event.read(hevt);
  event.read(bank);
  event.read(ccpb);
  event.read(ecpb);
  event.read(scpb);

  def runNum = hevt.getInt("nrun",0);
  def evtNum = hevt.getInt("nevt",0);

  int rows = bank.getRows();
  for(int i = 0; i < rows; i++){
    switch(i){
      case {bank.getInt("pid",i)==11}: ElectronList.add(i); break;
//      case {bank.getInt("pid",i)==2212}: PosChargedList.add(i); break;
      case {bank.getInt("charge",i)>0}: PosChargedList.add(i); break;
      default: OtherList.add(i); break;
    }
  }

  if(ElectronList.size()>=NUM_ELECTRONS){
    ElectronList.each { val ->
      boolean cutElectronMom = false;
      boolean cutQ2 = false;
      boolean cutW = false;
      boolean cutYb = false;
      boolean cutCCnphe = false;
      boolean cutCCstat = false;
      boolean cutECstat = false;
      boolean cutSCstat = false;
      boolean cutECin = false;
      boolean cutECoverP = false;
      boolean cutdtECSC = false;
      boolean cutFidCut = false;
      boolean cutECfid = false;

      electron.setPxPyPzM(bank.getFloat("px",val), bank.getFloat("py",val), bank.getFloat("pz",val), PhyConsts.massElectron());
      v3electron.setXYZ(bank.getFloat("vx",val), bank.getFloat("vy",val), bank.getFloat("vz",val));

      double phi_deg = Math.toDegrees(electron.phi()); // convert to degrees
      Vector3 v3electron_corr = myTarget.Get_CorrectedVertex(v3electron,electron);

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
        ecX = ecpb.getFloat("x",bank.getInt("ecstat",val)-1);
        ecY = ecpb.getFloat("y",bank.getInt("ecstat",val)-1);
        ecZ = ecpb.getFloat("z",bank.getInt("ecstat",val)-1);
        ecXYZ.setXYZ(ecX,ecY,ecZ);
        h2_EC_XvsY[0].fill(ecX,ecY);
        if(myEC.FidCutXYZ(ecXYZ)){
          cutECfid = true;
          h2_EC_XvsY[1].fill(ecX,ecY);
        } else{
          h2_EC_XvsY[2].fill(ecX,ecY);
        }
        ecUVW = myEC.XYZtoUVW(ecXYZ);
        h1_EC_U.fill(ecUVW.x());
        h1_EC_V.fill(ecUVW.y());
        h1_EC_W.fill(ecUVW.z());
        if(myEC.FidCutU(ecUVW.x())) h1_EC_U_fid.fill(ecUVW.x());
        if(myEC.FidCutV(ecUVW.y())) h1_EC_V_fid.fill(ecUVW.y());
        if(myEC.FidCutW(ecUVW.z())) h1_EC_W_fid.fill(ecUVW.z());
      }
      if(bank.getInt("scstat",val)>0 && scpb.getRows()>0){ // check SC
        cutSCstat = true;
        scTime = scpb.getFloat("time",bank.getInt("scstat",val)-1);
        scPath = scpb.getFloat("path",bank.getInt("scstat",val)-1);
        tofElectron = scTime - (scPath/LIGHTSPEED);
      }

      if(cutCCstat && cutECstat && cutSCstat){
        if(electron.p()>=ELECTRON_MOM) cutElectronMom = true;
        myRK.setScatteredElectron(electron);

        if(myRK.Q2()>=Q2_DIS) cutQ2 = true;
        if(myRK.W()>=W_DIS) cutW = true;
        if(myRK.Yb()<=YB_DIS) cutYb = true;

        if(cc_nphe>=NPHE_MIN) cutCCnphe = true;
        if(ecin >= ECIN_MIN) cutECin = true;

        if(electron.p()>0.0){
          cutECoverP = myEC.EC_SamplingFraction_Cut(electron.p(),ectot,ECsector,12);
          if(ECsector<1 || ECsector>6){
            println counterFile;
            bank.show();
            ccpb.show();
            ecpb.show();
            println ecpb.getRows();
          }
        }

        cutdtECSC = myEC.dt_ECSC(ecTime,scTime); // cut on time difference between EC and SC
        cutFidCut = myFidCuts.clas6FidCheckCut(electron,"electron"); // electron fiducial cuts

        if(cutQ2 && cutW  && cutYb && cutElectronMom && cutECoverP && cutCCnphe && cutdtECSC && cutECin){
          ElectronVecList << [electron.px(),electron.py(),electron.pz(),electron.e(),v3electron_corr.x(),v3electron_corr.y(),v3electron_corr.z(),tofElectron,cutFidCut,cutECfid];
        }
      }
    }
  }

  // start proton ID with TOF cut
  if(PosChargedList.size()>0 && ElectronVecList.size()>0){
    PosChargedList.each { val ->
      partLV.setPxPyPzM(bank.getFloat("px",val), bank.getFloat("py",val), bank.getFloat("pz",val), PhyConsts.massProton());

      if(bank.getInt("scstat",val)>0 && scpb.getRows()>0 && myProton.LowMomentumCut(partLV.p())){
        scTimeProton = scpb.getFloat("time",bank.getInt("scstat",val)-1);
        scPathProton = scpb.getFloat("path",bank.getInt("scstat",val)-1);
        tofProton = scTimeProton - (scPathProton/LIGHTSPEED)*Math.sqrt(Math.pow(PhyConsts.massProton()/partLV.p(),2)+1);

        for(int eIndex=0; eIndex<ElectronVecList.size(); eIndex++){
          def emList = ElectronVecList.get(eIndex);
          h2_dTOF_VS_P.fill(partLV.p(),tofProton-emList[7]); // fill histogram before TOF cut
          if(myProton.Get_ProtonTOF_Cut(partLV.p(),tofProton-emList[7])){
            h2_dTOF_VS_P_cut.fill(partLV.p(),tofProton-emList[7]); // fill histogram after TOF cut
            if(bank.getInt("pid",val)!=2212){
              h2_dTOF_VS_P_cut_not2212.fill(partLV.p(),tofProton-emList[7]); // pid not equal 2212
              h1_dTOF_not2212.fill(bank.getInt("pid",val));
//              System.out.println("pid = " + bank.getInt("pid",val));
            }
            ProtonList.add(val); // add particle index for proton that pass the TOF cut
            break;
          }
        }
      }
    }
  }

  // sort protons
  if(ProtonList.size()>=NUM_PROTONS){
    ProtonList.each { val ->
      proton.setPxPyPzM(bank.getFloat("px",val), bank.getFloat("py",val), bank.getFloat("pz",val), PhyConsts.massProton());
      v3proton.setXYZ(bank.getFloat("vx",val), bank.getFloat("vy",val), bank.getFloat("vz",val));
      Vector3 v3proton_corr = myTarget.Get_CorrectedVertex(v3proton,proton);

      boolean protonFidCut = myFidCuts.clas6FidCheckCut(proton,"piplus");
      ProtonVecList << [proton.px(),proton.py(),proton.pz(),proton.e(),v3proton_corr.x(),v3proton_corr.y(),v3proton_corr.z(),protonFidCut];

      if(protonFidCut){ // proton fiducial cuts
        switch(myTarget.Get_TargetIndex(v3proton_corr)){
          case 0: counterProtonD2++; break;
          case 1: counterProtonSolid++; break;
          default: counterProtonOther++; break;
        }
      }
    }
  }

  if(ElectronVecList.size()>=NUM_ELECTRONS && ProtonVecList.size()>=NUM_PROTONS){
    ElectronVecList.eachWithIndex { emList, emInd ->
      LorentzVector emVec = new LorentzVector(emList[0],emList[1],emList[2],emList[3]);
      Vector3 emVert = new Vector3(emList[4],emList[5],emList[6])
      int emTgt = myTarget.Get_TargetIndex(emVert);     // electron target label
      myRK.setScatteredElectron(emVec);
      int emFidCut = 0;         // initialized fiducial cut flag
      if(emList[8]) emFidCut = 1;  // set fiducial cut flag if true
      int emECFidCut = 0;         // initialized fiducial cut flag
      if(emList[9]) emECFidCut = 1;  // set fiducial cut flag if true
      ProtonVecList.eachWithIndex { pList, pInd ->
        LorentzVector protonVec = new LorentzVector(pList[0],pList[1],pList[2],pList[3]);
        Vector3 protonVert = new Vector3(pList[4],pList[5],pList[6]);
        myRK.setHadron(protonVec);
        int pTgt = myTarget.Get_TargetIndex(protonVert);       // proton target label
        int pFidCut = 0;           // initialized fiducial cut flag
        if(pList[7]) pFidCut = 1;  // set fiducial cut flag if true
        if(pTgt==emTgt){
          treeItem[0] = runNum;
          treeItem[1] = evtNum;
          treeItem[2] = pTgt;
          treeItem[3] = ElectronVecList.size();
          treeItem[4] = emInd;
          treeItem[5] = emVec.px();
          treeItem[6] = emVec.py();
          treeItem[7] = emVec.pz();
          treeItem[8] = Math.toDegrees(emVec.theta());
          treeItem[9] = Math.toDegrees(emVec.phi());
          treeItem[10] = emVert.x();
          treeItem[11] = emVert.y();
          treeItem[12] = emVert.z();
          treeItem[13] = ProtonVecList.size();
          treeItem[14] = pInd;
          treeItem[15] = protonVec.px();
          treeItem[16] = protonVec.py();
          treeItem[17] = protonVec.pz();
          treeItem[18] = Math.toDegrees(protonVec.theta());
          treeItem[19] = Math.toDegrees(protonVec.phi());
          treeItem[20] = protonVert.x();
          treeItem[21] = protonVert.y();
          treeItem[22] = protonVert.z();
          treeItem[23] = myRK.Q2();
          treeItem[24] = myRK.nu();
          treeItem[25] = myRK.W();
          treeItem[26] = myRK.zh();
          treeItem[27] = myRK.zLC();
          treeItem[28] = myRK.pT2();
          treeItem[29] = myRK.Xb();
          treeItem[30] = myRK.Yb();
          treeItem[31] = Math.toDegrees(myRK.PhiPQ());
          treeItem[32] = pFidCut;
          treeItem[33] = emFidCut;
          treeItem[34] = emECFidCut;
          tree.addRow(treeItem);  // add the tree data
        }
      }
    }
  }
  counterFile++;
}
System.out.println("processed (total) = " + counterFile);
System.out.println("protons (D2) = " + counterProtonD2);
System.out.println("protons (Solid) = " + counterProtonSolid);
System.out.println("protons (Other) = " + counterProtonOther);

tree.close(); // close the tree file

if(bGraph){
  int c1_title_size = 24;
  TCanvas c1 = new TCanvas("c1",1000,1000);
  c1.divide(2,2);
  c1.cd(0);
  c1.getPad().setTitleFontSize(c1_title_size);
  c1.getPad().getAxisZ().setLog(true);
  c1.draw(h2_dTOF_VS_P);
  c1.cd(1);
  c1.getPad().setTitleFontSize(c1_title_size);
  c1.getPad().getAxisZ().setLog(true);
  c1.draw(h2_dTOF_VS_P_cut);
  c1.cd(2);
  c1.getPad().setTitleFontSize(c1_title_size);
  c1.getPad().getAxisZ().setLog(true);
  c1.draw(h2_dTOF_VS_P_cut_not2212);
  c1.cd(3);
  c1.getPad().setTitleFontSize(c1_title_size);
  c1.draw(h1_dTOF_not2212);

  int c2_title_size = 24;
  TCanvas c2 = new TCanvas("c2",1400,450);
  c2.divide(3,1);
  c2.cd(0);
  c2.getPad().setTitleFontSize(c2_title_size);
  c2.draw(h1_EC_U);
  c2.draw(h1_EC_U_fid,"same");
  c2.cd(1);
  c2.getPad().setTitleFontSize(c2_title_size);
  c2.draw(h1_EC_V);
  c2.draw(h1_EC_V_fid,"same");
  c2.cd(2);
  c2.getPad().setTitleFontSize(c2_title_size);
  c2.draw(h1_EC_W);
  c2.draw(h1_EC_W_fid,"same");

  int c3_title_size = 24;
  TCanvas c3 = new TCanvas("c3",1400,450);
  c3.divide(3,1);
  histsECxy.eachWithIndex { hname, ih->
    c3.cd(ih);
    c3.getPad().setTitleFontSize(c1_title_size);
    c3.getPad().getAxisZ().setLog(true);
    c3.draw(h2_EC_XvsY[ih]);
  }
}

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen
