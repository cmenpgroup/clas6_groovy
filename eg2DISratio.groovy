import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
//import org.jlab.clas.physics.*;
//import org.jlab.clas.pdg.PhysicsConstants;
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

clas6beta myBeta = new clas6beta();  // create the beta object
clas6EC myEC = new clas6EC();  // create the EC object
eg2Target myTarget = new eg2Target();  // create the eg2 target object
clas6Proton myProton = new clas6Proton(); // create the proton object
clas6FidCuts myFidCuts = new clas6FidCuts(); // create the CLAS6 Fiducial Cuts object

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

LorentzVector beam = new LorentzVector(0.0,0.0,beamEnergy,beamEnergy);
LorentzVector protonTarget = new LorentzVector(0.0,0.0,0.0,PhyConsts.massProton());
LorentzVector electron = new LorentzVector(0,0,0,0);
Vector3 v3electron = new Vector3(0,0,0);
Vector3 v3electron_corr = new Vector3(0,0,0);

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

def cli = new CliBuilder(usage:'eg2DISratio.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
cli.c(longOpt:'counter', args:1, argName:'count by events', type: int, 'Event progress counter')
cli.o(longOpt:'output', args:1, argName:'Ntuple output file', type: String, 'Output file name')
cli.s(longOpt:'solid', args:1, argName:'Solid Target', type: String, 'Solid Target (C, Fe, Pb)')
cli.g(longOpt:'graph', 'Graph monitoring histograms')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def printCounter = 20000;
if(options.c) printCounter = options.c;

def maxEvents = 0;
if(options.M) maxEvents = options.M;

String solidTgt = "C";
if(options.s) solidTgt = options.s;

String outFile = "eg2DISntuple.hipo";
if(options.o) outFile = options.o;

boolean bGraph = false;
if(options.g) bGraph = true;

println "Electron DIS cuts"
println "Q^2 >= " + Q2_DIS + " GeV/c^2";
println "W >= " + W_DIS + " GeV";
println "Yb <= " + YB_DIS;

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

Event event = new Event();
Bank bank = new Bank(reader.getSchemaFactory().getSchema("EVENT::particle"));
Bank head = new Bank(reader.getSchemaFactory().getSchema("HEADER::info"));
Bank ccpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::ccpb"));
Bank ecpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::ecpb"));
Bank scpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::scpb"));

// Define a ntuple tree with 16 variables
TreeFileWriter tree = new TreeFileWriter(outFile,"DISTree","Run:Event:iTgt:eNum:ePx:ePy:ePz:eVx:eVy:eVz:q2:nu:W:xb:yb:eFidCut");
float[]  treeItem = new float[16];

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
      boolean cutCCnphe = false;
      boolean cutCCstat = false;
      boolean cutECstat = false;
      boolean cutSCstat = false;
      boolean cutECin = false;
      boolean cutECoverP = false;
      boolean cutdtECSC = false;
      boolean cutFidCut = false;

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
      }
      if(bank.getInt("scstat",i)>0 && scpb.getRows()>0){ // check SC
        cutSCstat = true;
        scTime = scpb.getFloat("time",bank.getInt("scstat",i)-1);
        scPath = scpb.getFloat("path",bank.getInt("scstat",i)-1);
      }
      if(cutCCstat && cutECstat && cutSCstat){ // proceed if EC && CC && SC
        cutElectronMom = (electron.p()>=ELECTRON_MOM); // electron momentum cut

        LorentzVector vecQ2 = LorentzVector.from(beam);   // calculate Q-squared, first copy incident e- 4-vector
        vecQ2.sub(electron);  // calculate Q-squared, subtract scattered e- 4-vector
        posQ2 = -vecQ2.mass2(); // calcuate Q-squared, make into a positive value
        cutQ2 = (posQ2>=Q2_DIS);

        LorentzVector vecW2 = LorentzVector.from(beam); // calculate W, first copy incident e- 4-vector
        vecW2.add(protonTarget).sub(electron); // calculate W, add target proton 4-vector and subtract scattered e- 4-vector
        cutW = (vecW2.mass()>=W_DIS);

        nu = beamEnergy - electron.e(); // calculate nu
        Xb = posQ2/(2*nu*PhyConsts.massProton()); // calcuate x-byorken
        Yb = nu/beamEnergy; // calculate normalized nu
        cutYb = (Yb<=YB_DIS); // Y cut
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
        if(cutQ2 && cutW  && cutYb && cutElectronMom && cutECoverP && cutCCnphe && cutdtECSC && cutECin){
          int emFidCut = 0;         // initialized fiducial cut flag
          if(cutFidCut) emFidCut = 1;  // set fiducial cut flag if true
          int emTgt = myTarget.Get_TargetIndex(v3electron_corr);
          treeItem[0] = runNum;
          treeItem[1] = evtNum;
          treeItem[2] = emTgt;
          treeItem[3] = emCtr;
          treeItem[4] = electron.px();
          treeItem[5] = electron.py();
          treeItem[6] = electron.pz();
          treeItem[7] = v3electron_corr.x();
          treeItem[8] = v3electron_corr.y();
          treeItem[9] = v3electron_corr.z();
          treeItem[10] = posQ2;
          treeItem[11] = nu;
          treeItem[12] = vecW2.mass();
          treeItem[13] = Xb;
          treeItem[14] = Yb;
          treeItem[15] = emFidCut;
          tree.addRow(treeItem);  // add the tree data

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

          emCtr++; // electron counter per event
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

tree.close(); // close the tree file

if(bGraph){
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
  }

  TCanvas c2 = new TCanvas("c2",1200,500);
  int c2_title_size = 24;
  c2.divide(3,1);
  c2.cd(0);
  c2.getPad().setTitleFontSize(c2_title_size);
  c2.draw(h2_Q2_Nu_binned[0]);
  c2.cd(1);
  c2.getPad().setTitleFontSize(c2_title_size);
  c2.draw(h2_Q2_Nu_binned[1]);
  c2.cd(2);
  c2.getPad().setTitleFontSize(c2_title_size);
  H2F h2_Q2_Nu_binned_ratio = H2F.divide(h2_Q2_Nu_binned[1],h2_Q2_Nu_binned[0]);
  h2_Q2_Nu_binned_ratio.setTitle("eg2 - " + solidTgt + "/D2");
  h2_Q2_Nu_binned_ratio.setTitleX("Q^2 (GeV/c)^2");
  h2_Q2_Nu_binned_ratio.setTitleY("#nu (GeV)");
  c2.draw(h2_Q2_Nu_binned_ratio);

  for(iQsq = 0; iQsq < Q2bins.size()-1; iQsq++){
    System.out.println("*** Q^2 " + Q2bins[iQsq] + " : " + Q2bins[iQsq+1] + " ***");
    for(iNu = 0; iNu < Nubins.size()-1; iNu++){
      System.out.println("<<< nu " + Nubins[iNu] + " : " + Nubins[iNu+1] + " >>>");
      System.out.println(h2_Q2_Nu_binned[1].getBinContent(iQsq,iNu) + " / " + h2_Q2_Nu_binned[0].getBinContent(iQsq,iNu) + " = " + h2_Q2_Nu_binned_ratio.getBinContent(iQsq,iNu));
    }
  }

  TCanvas c7 = new TCanvas("c7",1200,800);
  int canCount = 0;
  int c7_title_size = 24;
  c7.divide(Var.size(),3);
  TgtLabel.eachWithIndex {nTgt, iTgt->
    if(nTgt!="Other"){
      Var.eachWithIndex { nVar, iVar->
        c7.cd(canCount);
        c7.getPad().setTitleFontSize(c7_title_size);
        c7.draw(h1_nElectron[iVar][iTgt]);
        canCount++;
      }
    }
  }

  H1F[] h1_mrElectron = new H1F[Var.size()];
  GraphErrors[] gr_mrElectron = new GraphErrors[Var.size()];
  Var.eachWithIndex{nVar, iVar->
    c7.cd(canCount+iVar);
    c7.getPad().setTitleFontSize(c7_title_size);
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
    c7.draw(gr_mrElectron[iVar]);
  }
  //c7.save("mrElectron.png");

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
}
