import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.clas.physics.*;
import org.jlab.clas.pdg.PhysicsConstants;
//import org.jlab.jnp.physics.*;
//import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.math.*;

import org.jlab.jnp.utils.options.OptionParser;

import eg2Cuts.clas6EC
import eg2Cuts.clas6FidCuts

long st = System.currentTimeMillis(); // start time

PhysicsConstants PhyConsts= new PhysicsConstants();
clas6EC myEC = new clas6EC();  // create the EC object
clas6FidCuts myFidCuts = new clas6FidCuts(); // create the CLAS6 Fiducial Cuts object

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;
int counterFile = 0;

double NPHE_MIN = 28;
double ELECTRON_MOM = myEC.Get_ELECTRON_MOM();
double ECIN_MIN = myEC.Get_ECIN_MIN();
double EC_SAMPLEFRAC = myEC.Get_EC_SAMPLEFRAC();

LorentzVector electron = new LorentzVector(0,0,0,0);
Vector3 ecXYZ = new Vector3(0.0,0.0,0.0);
Vector3 ecUVW = new Vector3(0.0,0.0,0.0);

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
double ECstripLo = 0.5;
double ECstripHi = 450.5;
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

H2F h2_ECin_vs_ECout= new H2F("h2_ECin_vs_ECout","Experiment: eg2",100,0.0,0.4,100,0.0,0.15);
h2_ECin_vs_ECout.setTitleX("EC_i_n (GeV)");
h2_ECin_vs_ECout.setTitleY("EC_o_u_t (GeV)");

H2F h2_ECin_vs_ECout_cut= new H2F("h2_ECin_vs_ECout_cut","Experiment: eg2",100,0.0,0.4,100,0.0,0.15);
h2_ECin_vs_ECout_cut.setTitleX("EC_i_n (GeV)");
h2_ECin_vs_ECout_cut.setTitleY("EC_o_u_t (GeV)");

H2F h2_ECinP_vs_ECoutP= new H2F("h2_ECinP_vs_ECoutP","Experiment: eg2",100,0.0,1.2,100,0.0,0.8);
h2_ECinP_vs_ECoutP.setTitleX("EC_i_n/0.27/P");
h2_ECinP_vs_ECoutP.setTitleY("EC_o_u_t/0.27/P");

H2F h2_ECinP_vs_ECoutP_cut = new H2F("h2_ECinP_vs_ECoutP_cut","Experiment: eg2",100,0.0,1.2,100,0.0,0.8);
h2_ECinP_vs_ECoutP_cut.setTitleX("EC_i_n/0.27/P (GeV)");
h2_ECinP_vs_ECoutP_cut.setTitleY("EC_o_u_t/0.27/P (GeV)");

H2F h2_P_vs_ECtotP = new H2F("h2_P_vs_ECtotP","Experiment: eg2",100,0.0,3.0,100,0.0,0.5);
h2_P_vs_ECtotP.setTitleX("Momentum (GeV/c)");
h2_P_vs_ECtotP.setTitleY("EC_t_o_t/P (GeV)");

H2F h2_P_vs_ECtotP_cut = new H2F("h2_P_vs_ECtotP_cut","Experiment: eg2",100,0.0,3.0,100,0.0,0.5);
h2_P_vs_ECtotP_cut.setTitleX("Momentum (GeV/c)");
h2_P_vs_ECtotP_cut.setTitleY("EC_t_o_t/P (GeV)");

H1F h1_cc_nphe = new H1F("h1_cc_nphe","Number of Photoelectrons","Counts",200,0.0,200.0);
h1_cc_nphe.setTitle("Experiment: eg2");

H1F h1_cc_nphe_cut = new H1F("h1_cc_nphe_cut","Number of Photoelectrons","Counts",200,0.0,200.0);
h1_cc_nphe_cut.setTitle("Experiment: eg2");

H1F h1_cc_nphe_withEC = new H1F("h1_cc_nphe_withEC","Number of Photoelectrons","Counts",200,0.0,200.0);
h1_cc_nphe_withEC.setTitle("Experiment: eg2");

H1F h1_cc_nphe_withEC_cut = new H1F("h1_cc_nphe_withEC_cut","Number of Photoelectrons","Counts",200,0.0,200.0);
h1_cc_nphe_withEC_cut.setTitle("Experiment: eg2");

OptionParser p = new OptionParser("plotEC.groovy");

p.addOption("-M", "0", "Max. Events");
p.addOption("-c", "20000", "Event progress counter");
p.addOption("-o", "plotEC_Hists.hipo", "output file name");

p.parse(args);
int maxEvents = p.getOption("-M").intValue();
int printCounter = p.getOption("-c").intValue();
String outFile = p.getOption("-o").stringValue();

HipoChain reader = new HipoChain();
if(p.getInputList().size()){
  reader.addFiles(p.getInputList());
}else{
    System.out.println("*** No input files on command line. ***");
    p.printUsage();
    System.exit(0);
}
reader.open();

Event event  = new Event();
Bank  bank   = new Bank(reader.getSchemaFactory().getSchema("EVENT::particle"));
Bank  ecpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::ecpb"));
Bank  ccpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::ccpb"));

// Loop over all events
while(reader.hasNext()){
  if(counterFile % printCounter == 0) println counterFile;
  if(maxEvents!=0 && counterFile >= maxEvents) break;

  reader.nextEvent(event);
  event.read(bank);
  event.read(ecpb);
  event.read(ccpb);

  int rows = bank.getRows();
  for(int i = 0; i < rows; i++){
    if(bank.getInt("charge",i)==-1){
      boolean cutElectronMom = false;
      boolean cutCCnphe = false;
      boolean cutECin = false;
      boolean cutECoverP = false;
      boolean cutCCstat = false;

      if(bank.getInt("ccstat",i)>0 && ccpb.getRows()>0){ // check CC
        cutCCstat = true;
        cc_nphe = ccpb.getFloat("nphe",bank.getInt("ccstat",i)-1);
        cutCCnphe = (cc_nphe>=NPHE_MIN); // CC number of photoelectrons cut

        h1_cc_nphe.fill(cc_nphe);
        if(cutCCnphe){ // check CC nphe cut
          h1_cc_nphe_cut.fill(cc_nphe);
        }
      }

      if(bank.getInt("ecstat",i)>0 && ecpb.getRows()>0){ // check EC
        // create electron 4-vector
        electron.setPxPyPzM(bank.getFloat("px",i), bank.getFloat("py",i), bank.getFloat("pz",i), PhyConsts.massElectron());

        ECsector = ecpb.getInt("sector",bank.getInt("ecstat",i)-1);
        ecin = ecpb.getFloat("ein",bank.getInt("ecstat",i)-1);
        ecout = ecpb.getFloat("eout",bank.getInt("ecstat",i)-1);
        ectot = ecpb.getFloat("etot",bank.getInt("ecstat",i)-1);
        ecTime = ecpb.getFloat("time",bank.getInt("ecstat",i)-1);
        ecX = ecpb.getFloat("x",bank.getInt("ecstat",i)-1);
        ecY = ecpb.getFloat("y",bank.getInt("ecstat",i)-1);
        ecZ = ecpb.getFloat("z",bank.getInt("ecstat",i)-1);
        ecXYZ.setXYZ(ecX,ecY,ecZ);
        ecUVW = myEC.XYZtoUVW(ecXYZ);
        h2_EC_XvsY[0].fill(ecX,ecY);
        if(myEC.FidCutXYZ(ecXYZ)){
          h2_EC_XvsY[1].fill(ecX,ecY);
        } else{
          h2_EC_XvsY[2].fill(ecX,ecY);
        }
        h1_EC_U.fill(ecUVW.x());
        h1_EC_V.fill(ecUVW.y());
        h1_EC_W.fill(ecUVW.z());
        if(myEC.FidCutU(ecUVW.x())) h1_EC_U_fid.fill(ecUVW.x());
        if(myEC.FidCutV(ecUVW.y())) h1_EC_V_fid.fill(ecUVW.y());
        if(myEC.FidCutW(ecUVW.z())) h1_EC_W_fid.fill(ecUVW.z());

        if(electron.p()>0.0 && ecin>0 && ecout>0){
          cutElectronMom = (electron.p()>=ELECTRON_MOM); // electron momentum cut
          cutECoverP = myEC.EC_SamplingFraction_Cut(electron.p(),ectot,ECsector,12);
          cutECin = (ecin >= ECIN_MIN); // EC inner energy cut

          h2_P_vs_ECtotP.fill(electron.p(),ectot/electron.p());
          h2_ECin_vs_ECout.fill(ecin,ecout); // check EC inner energy cut
          h2_ECinP_vs_ECoutP.fill(ecin/electron.p()/EC_SAMPLEFRAC,ecout/electron.p()/EC_SAMPLEFRAC);

          if(cutECin && cutECoverP && cutElectronMom){
            h2_P_vs_ECtotP_cut.fill(electron.p(),ectot/electron.p());
            h2_ECin_vs_ECout_cut.fill(ecin,ecout);
            h2_ECinP_vs_ECoutP_cut.fill(ecin/electron.p()/EC_SAMPLEFRAC,ecout/electron.p()/EC_SAMPLEFRAC);

            if(cutCCstat){
              h1_cc_nphe_withEC.fill(cc_nphe);
              if(cutCCnphe){ // check CC nphe cut
                h1_cc_nphe_withEC_cut.fill(cc_nphe);
              }
            }
          }
        }
      }
    }
  }
  counterFile++;
}

TDirectory dir = new TDirectory();
String[] dirLabel = ["/XY","/Fid","/EC","/CC"];
dir.mkdir(dirLabel[0]);
dir.cd(dirLabel[0]);
int c1_title_size = 24;
TCanvas c1 = new TCanvas("c1",1500,500);
c1.divide(3,1);
histsECxy.eachWithIndex { hname, ih->
  c1.cd(ih);
  c1.getPad().setTitleFontSize(c1_title_size);
  c1.getPad().getAxisZ().setLog(true);
  c1.draw(h2_EC_XvsY[ih]);
  dir.addDataSet(h2_EC_XvsY[ih]);
}

dir.mkdir(dirLabel[1]);
dir.cd(dirLabel[1]);
int c2_title_size = 24;
TCanvas c2 = new TCanvas("c2",1500,500);
c2.divide(3,1);
c2.cd(0);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_EC_U);
c2.draw(h1_EC_U_fid,"same");
dir.addDataSet(h1_EC_U);
dir.addDataSet(h1_EC_U_fid);
c2.cd(1);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_EC_V);
c2.draw(h1_EC_V_fid,"same");
dir.addDataSet(h1_EC_V);
dir.addDataSet(h1_EC_V_fid);
c2.cd(2);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_EC_W);
c2.draw(h1_EC_W_fid,"same");
dir.addDataSet(h1_EC_W);
dir.addDataSet(h1_EC_W_fid);

dir.mkdir(dirLabel[2]);
dir.cd(dirLabel[2]);
int c3_title_size = 24;
TCanvas c3 = new TCanvas("c3",1200,800);
c3.divide(3,2);
c3.cd(0);
c3.getPad().setTitleFontSize(c2_title_size);
c3.draw(h2_P_vs_ECtotP);
dir.addDataSet(h2_P_vs_ECtotP);
c3.cd(1);
c3.getPad().setTitleFontSize(c2_title_size);
//c3.getPad().getAxisZ().setLog(true);
c3.draw(h2_ECin_vs_ECout);
dir.addDataSet(h2_ECin_vs_ECout);
c3.cd(2);
c3.getPad().setTitleFontSize(c2_title_size);
//c3.getPad().getAxisZ().setLog(true);
c3.draw(h2_ECinP_vs_ECoutP);
dir.addDataSet(h2_ECinP_vs_ECoutP);
c3.cd(3);
c3.getPad().setTitleFontSize(c2_title_size);
c3.draw(h2_P_vs_ECtotP_cut);
dir.addDataSet(h2_P_vs_ECtotP_cut);
c3.cd(4);
c3.getPad().setTitleFontSize(c2_title_size);
c3.draw(h2_ECin_vs_ECout_cut);
dir.addDataSet(h2_ECin_vs_ECout_cut);
c3.cd(5);
c3.getPad().setTitleFontSize(c2_title_size);
c3.draw(h2_ECinP_vs_ECoutP_cut);
dir.addDataSet(h2_ECinP_vs_ECoutP_cut);

dir.mkdir(dirLabel[3]);
dir.cd(dirLabel[3]);
int c4_title_size = 24;
TCanvas c4 = new TCanvas("c4",1200,650);
c4.divide(2,1);
c4.cd(0);
c4.getPad().setTitleFontSize(c4_title_size);
c4.draw(h1_cc_nphe);
h1_cc_nphe_cut.setFillColor(GREEN);
c4.draw(h1_cc_nphe_cut,"same");
dir.addDataSet(h1_cc_nphe);
dir.addDataSet(h1_cc_nphe_cut);
c4.cd(1);
c4.getPad().setTitleFontSize(c4_title_size);
c4.draw(h1_cc_nphe_withEC);
h1_cc_nphe_withEC_cut.setFillColor(GREEN);
c4.draw(h1_cc_nphe_withEC_cut,"same");
dir.addDataSet(h1_cc_nphe_withEC);
dir.addDataSet(h1_cc_nphe_withEC_cut);

dir.writeFile(outFile); // write the histograms to the file

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen
