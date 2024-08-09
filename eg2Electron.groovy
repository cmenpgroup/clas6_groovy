import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.clas.physics.*;
import org.jlab.clas.pdg.PhysicsConstants;
//import org.jlab.jnp.physics.*;
//import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*; // new import for ntuples

import org.jlab.jnp.utils.options.OptionParser;

import eg2Cuts.clas6beta
import eg2Cuts.clas6EC
import eg2Cuts.eg2Target
import eg2Cuts.clas6Proton
import eg2Cuts.clas6FidCuts
import kinematics.ReactionKine

long st = System.currentTimeMillis(); // start time

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

clas6beta myBeta = new clas6beta();  // create the beta object
clas6EC myEC = new clas6EC();  // create the EC object
eg2Target myTarget = new eg2Target();  // create the eg2 target object
clas6Proton myProton = new clas6Proton(); // create the proton object
clas6FidCuts myFidCuts = new clas6FidCuts(); // create the CLAS6 Fiducial Cuts object
ReactionKine myRK = new ReactionKine(); // create object for reaction kinematics

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;
double ELECTRON_MOM = 0.64;
double NPHE_MIN = 28;
double ECIN_MIN = 0.06;
int counterFile = 0;
int counterD2 = 0;
int counterSolid = 0;
int counterOther = 0;
int iQsq, iNu;

PhysicsConstants PhyConsts= new PhysicsConstants();
double LIGHTSPEED = PhyConsts.speedOfLight(); // speed of light in cm/ns
println "Speed of light = " + LIGHTSPEED + " cm/ns";

double beamEnergy = myTarget.Get_Beam_Energy();
println "Beam " + beamEnergy + " GeV";
double W_DIS = myTarget.Get_W_DIS();
double Q2_DIS = myTarget.Get_Q2_DIS();
double YB_DIS = myTarget.Get_YB_DIS();

myRK.setBeam(beamEnergy,PhyConsts.massElectron());
myRK.setTarget(PhyConsts.massProton());

LorentzVector electron = new LorentzVector(0,0,0,0);
Vector3 v3electron = new Vector3(0,0,0);
Vector3 v3electron_corr = new Vector3(0,0,0);

Vector3 ecXYZ = new Vector3(0.0,0.0,0.0);
Vector3 ecUVW = new Vector3(0.0,0.0,0.0);

// histograms for the kinematic variables like Q2, W, nu, Yb
String[] histsKine = ["hQ2","hW","hNu","hYb"];
H1F[] h1_Kine = new H1F[histsKine.size()];
H1F[] h1_KineDIS = new H1F[histsKine.size()];
int[] nBin_Kine = [100,100,100,100];
double[] xLo_Kine = [0.0,1.5,2.0,0.4];
double[] xHi_Kine = [5.0,3.0,4.5,1.0];
String[] xLabel_Kine = ["Q^2 (GeV^2)","W (GeV)","#nu (GeV)","y "];

histsKine.eachWithIndex { hname, ih->
  h1_Kine[ih] = new H1F(hname,xLabel_Kine[ih],"Counts",nBin_Kine[ih],xLo_Kine[ih],xHi_Kine[ih]);
  h1_KineDIS[ih] = new H1F(hname+"_DIS",xLabel_Kine[ih],"Counts",nBin_Kine[ih],xLo_Kine[ih],xHi_Kine[ih]);
}

// histograms for the electron polar angle
H1F hPhi_e = new H1F("hPhi_e","#phi (deg.)","Counts",180,-180.0,180.0);
H1F hPhi_e_fid = new H1F("hPhi_e_fid","#phi (deg.)","Counts",180,-180.0,180.0);

String[] TgtLabel = ["D2","Nuc","Other"];
String[] xLabel = ["Q^2 (GeV^2)","#nu (GeV)"];
String[] Var = ["Qsq","nu"];
int[] nbins = [50,50];
double[] xlo = [Q2_DIS,2.2];
double[] xhi = [4.1,4.2];
H1F[][] h1_nElectron = new H1F[Var.size()][TgtLabel.size()];
H2F[] h2_Q2_Nu_binned = new H2F[TgtLabel.size()];
H1F[] h1_Vz = new H1F[TgtLabel.size()];

double[] Q2bins = [Q2_DIS,1.33,1.76,4.10];
double[] Nubins = [2.20,3.20,3.73,4.25];

TgtLabel.eachWithIndex {nTgt, iTgt->
  Var.eachWithIndex { nVar, iVar->
    String hname = "h1_nElectron_" + nTgt + "_" + nVar;
    h1_nElectron[iVar][iTgt] = new H1F(hname,xLabel[iVar],"Counts",nbins[iVar],xlo[iVar],xhi[iVar]);
    h1_nElectron[iVar][iTgt].setTitle("eg2 - " + nTgt);
    h1_nElectron[iVar][iTgt].setFillColor(YELLOW - iTgt);
  }

  hname = "h2_Q2_Nu_binned_" + nTgt;
  h2_Q2_Nu_binned[iTgt] = new H2F(hname,"Experiment: eg2 - Electrons",Q2bins.size()-1,0.5,Q2bins.size()-0.5,Nubins.size()-1,0.5,Nubins.size()-0.5);
  h2_Q2_Nu_binned[iTgt].setTitleX("Q^2 (GeV/c)^2");
  h2_Q2_Nu_binned[iTgt].setTitleY("#nu (GeV)");

  hname = "h1_Vz_" + nTgt;
  h1_Vz[iTgt] = new H1F(hname,"Experiment: eg2 - Electrons",100,-33,-20.0);
  h1_Vz[iTgt].setTitleX("Vertex z (cm)");
  h1_Vz[iTgt].setTitleY("Counts");
}

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

OptionParser p = new OptionParser("eg2Electron.groovy");

p.addOption("-M", "0", "Max. Events");
p.addOption("-c", "20000", "Event progress counter");
p.addOption("-o", "eg2Electron_Hists.hipo", "output file name");
p.addOption("-s", "C", "Solid Target (C, Fe, Pb)");

p.parse(args);
int maxEvents = p.getOption("-M").intValue();
int printCounter = p.getOption("-c").intValue();
String outFile = p.getOption("-o").stringValue();
String solidTgt = p.getOption("-s").stringValue();

HipoChain reader = new HipoChain();
if(p.getInputList().size()){
  reader.addFiles(p.getInputList());
  //p.getInputList().each { infile ->
  //  System.out.println("Analyzing " + infile);
  //  reader.addFile(infile);
  //}
}else{
    System.out.println("*** No input files on command line. ***");
    p.printUsage();
    System.exit(0);
}
reader.open();

println "Electron DIS cuts"
println "Q^2 >= " + Q2_DIS + " GeV/c^2";
println "W >= " + W_DIS + " GeV";
println "y <= " + YB_DIS;

Event event = new Event();
Bank bank = new Bank(reader.getSchemaFactory().getSchema("EVENT::particle"));
Bank head = new Bank(reader.getSchemaFactory().getSchema("HEADER::info"));
Bank ccpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::ccpb"));
Bank ecpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::ecpb"));
Bank scpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::scpb"));

while(reader.hasNext()) {
  if(counterFile % printCounter == 0) println counterFile;
  if(maxEvents!=0 && counterFile >= maxEvents) break;

  reader.nextEvent(event);
  event.read(bank);
  event.read(head);
  event.read(ccpb);
  event.read(ecpb);
  event.read(scpb);

  def runNum = head.getInt("nrun",0);
  def evtNum = head.getInt("nevt",0);
  int emCtr = 0; // electron counter

  for(int i=0;i<bank.getRows();i++){ // loop over the particles in the bank
    if(bank.getInt("pid",i)==11){ // check for electron PID

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

      // create electron 4-vector
      electron.setPxPyPzM(bank.getFloat("px",i), bank.getFloat("py",i), bank.getFloat("pz",i), PhyConsts.massElectron());
      v3electron.setXYZ(bank.getFloat("vx",i), bank.getFloat("vy",i), bank.getFloat("vz",i));
      v3electron_corr = myTarget.Get_CorrectedVertex(v3electron,electron); // corrected vertex

      if(bank.getInt("ccstat",i)>0 && ccpb.getRows()>0){ // check CC
        cutCCstat = true;
        cc_nphe = ccpb.getFloat("nphe",bank.getInt("ccstat",i)-1);
      }
      if(bank.getInt("ecstat",i)>0 && ecpb.getRows()>0){ // check EC
        cutECstat = true;
        ECsector = ecpb.getInt("sector",bank.getInt("ecstat",i)-1);
        ecin = ecpb.getFloat("ein",bank.getInt("ecstat",i)-1);
        ecout = ecpb.getFloat("eout",bank.getInt("ecstat",i)-1);
        ectot = ecpb.getFloat("etot",bank.getInt("ecstat",i)-1);
        ecTime = ecpb.getFloat("time",bank.getInt("ecstat",i)-1);
        ecX = ecpb.getFloat("x",bank.getInt("ecstat",i)-1);
        ecY = ecpb.getFloat("y",bank.getInt("ecstat",i)-1);
        ecZ = ecpb.getFloat("z",bank.getInt("ecstat",i)-1);
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
      if(bank.getInt("scstat",i)>0 && scpb.getRows()>0){ // check SC
        cutSCstat = true;
        scTime = scpb.getFloat("time",bank.getInt("scstat",i)-1);
        scPath = scpb.getFloat("path",bank.getInt("scstat",i)-1);
      }
      if(cutCCstat && cutECstat && cutSCstat){ // proceed if EC && CC && SC
        cutElectronMom = (electron.p()>=ELECTRON_MOM); // electron momentum cut

        myRK.setScatteredElectron(electron);

        double posQ2 = myRK.Q2();
        double nu = myRK.nu();
        if(myRK.Q2()>=Q2_DIS) cutQ2 = true;
        if(myRK.W()>=W_DIS) cutW = true;
        if(myRK.Yb()<=YB_DIS) cutYb = true;

        cutCCnphe = (cc_nphe>=NPHE_MIN); // CC number of photoelectrons cut
        cutECin = (ecin >= ECIN_MIN); // EC inner energy cut
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
        cutdtECSC = myEC.dt_ECSC(ecTime,scTime); // cut on timing difference between EC and SC
        cutFidCut = myFidCuts.clas6FidCheckCut(electron,"electron"); // electron fiducial cuts

        // save the event if the particle pases the electron ID cuts
        if(cutElectronMom && cutECoverP && cutCCnphe && cutdtECSC && cutECin){
          h1_Kine[0].fill(myRK.Q2());
          h1_Kine[1].fill(myRK.W());
          h1_Kine[2].fill(myRK.nu());
          h1_Kine[3].fill(myRK.Yb());

          if(cutQ2) h1_KineDIS[0].fill(myRK.Q2());
          if(cutW) h1_KineDIS[1].fill(myRK.W());
          if(cutYb){
            h1_KineDIS[2].fill(myRK.nu());
            h1_KineDIS[3].fill(myRK.Yb());
          }

          if(cutQ2 && cutW  && cutYb){
            int emTgt = myTarget.Get_TargetIndex(v3electron_corr);

            h1_nElectron[0][emTgt].fill(posQ2);
            h1_nElectron[1][emTgt].fill(nu);
            h1_Vz[emTgt].fill(v3electron_corr.z());

            switch(emTgt){
              case 0: counterD2++; break;
              case 1: counterSolid++; break;
              default: counterOther++; break;
            }

            int indexQ2 = -99;
            int indexNu = -99;
            if((posQ2>=Q2bins[0] && posQ2<Q2bins[Q2bins.size()-1]) && (nu>=Nubins[0] && nu<Nubins[Nubins.size()-1])){
              for(iQsq = 0; iQsq < Q2bins.size()-1; iQsq++){
                if(posQ2>=Q2bins[iQsq] && posQ2<Q2bins[iQsq+1]){
                  indexQ2 = iQsq;
                  break;
                }
              }

              for(iNu = 0; iNu < Nubins.size()-1; iNu++){
                if(nu>=Nubins[iNu] && nu<Nubins[iNu+1]){
                  indexNu = iNu;
                  break;
                }
              }
              h2_Q2_Nu_binned[emTgt].fill(indexQ2+1,indexNu+1);
            }

            hPhi_e.fill(Math.toDegrees(electron.phi()));
            if(cutFidCut) hPhi_e_fid.fill(Math.toDegrees(electron.phi()));
            emCtr++; // electron counter per event
          }
        }
      }
    }
  }
  counterFile++;
}

System.out.println("processed (total) = " + counterFile);
System.out.println("electrons (D2) = " + counterD2);
System.out.println("electrons (Solid) = " + counterSolid);
System.out.println("electrons (Other) = " + counterOther);
System.out.println("Ne(D2)/Ne(Solid) = " + counterSolid/counterD2);

TDirectory dir = new TDirectory();
String[] dirLabel = ["/vertex","/Q2_nu","/EC","/electron","/kinematics"];
dir.mkdir(dirLabel[0]);
dir.cd(dirLabel[0]);
TCanvas c1 = new TCanvas("c1",600,600);
int c1_title_size = 24;
c1.cd(0);
TgtLabel.eachWithIndex {nTgt, iTgt->
  c1.getPad().setTitleFontSize(c1_title_size);
  if(nTgt!="Other"){
    h1_Vz[iTgt].setFillColor(YELLOW);
  }else{
    h1_Vz[iTgt].setFillColor(BLUE);
  }
  if(iTgt==0){
    c1.draw(h1_Vz[iTgt]);
  }else{
    c1.draw(h1_Vz[iTgt],"same");
  }
  dir.addDataSet(h1_Vz[iTgt]);
}

dir.mkdir(dirLabel[1]);
dir.cd(dirLabel[1]);
TCanvas c2 = new TCanvas("c2",1200,500);
int c2_title_size = 24;
c2.divide(3,1);
c2.cd(0);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h2_Q2_Nu_binned[0]);
dir.addDataSet(h2_Q2_Nu_binned[0]);
c2.cd(1);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h2_Q2_Nu_binned[1]);
dir.addDataSet(h2_Q2_Nu_binned[1]);
c2.cd(2);
c2.getPad().setTitleFontSize(c2_title_size);
H2F h2_Q2_Nu_binned_ratio = H2F.divide(h2_Q2_Nu_binned[1],h2_Q2_Nu_binned[0]);
h2_Q2_Nu_binned_ratio.setTitle("eg2 - " + solidTgt + "/D2");
h2_Q2_Nu_binned_ratio.setTitleX("Q^2 (GeV/c)^2");
h2_Q2_Nu_binned_ratio.setTitleY("#nu (GeV)");
c2.draw(h2_Q2_Nu_binned_ratio);
dir.addDataSet(h2_Q2_Nu_binned_ratio);

for(iQsq = 0; iQsq < Q2bins.size()-1; iQsq++){
  System.out.println("*** Q^2 " + Q2bins[iQsq] + " : " + Q2bins[iQsq+1] + " ***");
  for(iNu = 0; iNu < Nubins.size()-1; iNu++){
    System.out.println("<<< nu " + Nubins[iNu] + " : " + Nubins[iNu+1] + " >>>");
    System.out.println(h2_Q2_Nu_binned[1].getBinContent(iQsq,iNu) + " / " + h2_Q2_Nu_binned[0].getBinContent(iQsq,iNu) + " = " + h2_Q2_Nu_binned_ratio.getBinContent(iQsq,iNu));
  }
}

dir.mkdir(dirLabel[2]);
dir.cd(dirLabel[2]);
int c3_title_size = 24;
TCanvas c3 = new TCanvas("c3",1400,450);
c3.divide(3,1);
c3.cd(0);
c3.getPad().setTitleFontSize(c3_title_size);
c3.draw(h1_EC_U);
c3.draw(h1_EC_U_fid,"same");
dir.addDataSet(h1_EC_U);
dir.addDataSet(h1_EC_U_fid);
c3.cd(1);
c3.getPad().setTitleFontSize(c3_title_size);
c3.draw(h1_EC_V);
c3.draw(h1_EC_V_fid,"same");
dir.addDataSet(h1_EC_V);
dir.addDataSet(h1_EC_V_fid);
c3.cd(2);
c3.getPad().setTitleFontSize(c3_title_size);
c3.draw(h1_EC_W);
c3.draw(h1_EC_W_fid,"same");
dir.addDataSet(h1_EC_W);
dir.addDataSet(h1_EC_W_fid);

int c4_title_size = 24;
TCanvas c4 = new TCanvas("c4",1400,450);
c4.divide(3,1);
histsECxy.eachWithIndex { hname, ih->
  c4.cd(ih);
  c4.getPad().setTitleFontSize(c4_title_size);
  c4.getPad().getAxisZ().setLog(true);
  c4.draw(h2_EC_XvsY[ih]);
  dir.addDataSet(h2_EC_XvsY[ih]);
}

dir.mkdir(dirLabel[3]);
dir.cd(dirLabel[3]);
TCanvas c5 = new TCanvas("c5",1200,800);
int canCount = 0;
int c5_title_size = 24;
c5.divide(Var.size(),3);
TgtLabel.eachWithIndex {nTgt, iTgt->
  if(nTgt!="Other"){
    Var.eachWithIndex { nVar, iVar->
      c5.cd(canCount);
      c5.getPad().setTitleFontSize(c5_title_size);
      c5.draw(h1_nElectron[iVar][iTgt]);
      dir.addDataSet(h1_nElectron[iVar][iTgt]);
      canCount++;
    }
  }
}

H1F[] h1_mrElectron = new H1F[Var.size()];
GraphErrors[] gr_mrElectron = new GraphErrors[Var.size()];
Var.eachWithIndex{nVar, iVar->
  c5.cd(canCount+iVar);
  c5.getPad().setTitleFontSize(c5_title_size);
  h1_mrElectron[iVar] = H1F.divide(h1_nElectron[iVar][1],h1_nElectron[iVar][0]);
  h1_mrElectron[iVar].setName("h1_mrElectron_" + nVar);
  h1_mrElectron[iVar].setFillColor(GREEN);
  gr_mrElectron[iVar] = h1_mrElectron[iVar].getGraph();
  gr_mrElectron[iVar].setName("gr_mrElectron_" + nVar);
  gr_mrElectron[iVar].setTitle("eg2 - " + solidTgt + "/D2");
  gr_mrElectron[iVar].setTitleX(xLabel[iVar]);
  gr_mrElectron[iVar].setTitleY("R^p");
  gr_mrElectron[iVar].setMarkerColor(3);
  gr_mrElectron[iVar].setLineColor(3);
  gr_mrElectron[iVar].setMarkerSize(3);
  c5.draw(gr_mrElectron[iVar]);
  dir.addDataSet(gr_mrElectron[iVar]);
}
//c5.save("mrElectron.png");

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
    if(nTgt!="Other") {cMR[iVar].draw(h1_nElectron[iVar][iTgt]); canCount++;}
  }
  cMR[iVar].cd(canCount);
  cMR[iVar].draw(gr_mrElectron[iVar]);
  def cFile = "mrElectron_" + nVar + ".png";
  cMR[iVar].save(cFile);
}

dir.mkdir(dirLabel[4]);
dir.cd(dirLabel[4]);
TCanvas c6 = new TCanvas("c6",800,800);
int c6_title_size = 24;
c6.divide(2,2);

histsKine.eachWithIndex {hname, ih->
  c6.cd(ih);
  c6.getPad().setTitleFontSize(c6_title_size);
  c6.draw(h1_Kine[ih]);
  dir.addDataSet(h1_Kine[ih]);
  h1_KineDIS[ih].setFillColor(GREEN);
  c6.draw(h1_KineDIS[ih],"same");
  dir.addDataSet(h1_KineDIS[ih]);
}

TCanvas c7 = new TCanvas("c7",600,600);
int c7_title_size = 24;
c7.cd(0);
c7.getPad().setTitleFontSize(c7_title_size);
c7.draw(hPhi_e);
dir.addDataSet(hPhi_e);
hPhi_e_fid.setFillColor(GREEN);
c7.draw(hPhi_e_fid,"same");
dir.addDataSet(hPhi_e_fid);

dir.writeFile(outFile); // write the histograms to the file

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen
