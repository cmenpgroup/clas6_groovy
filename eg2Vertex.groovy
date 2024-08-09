import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
//import org.jlab.jnp.physics.*;
//import org.jlab.jnp.pdg.PhysicsConstants;
import org.jlab.clas.physics.*;
import org.jlab.clas.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*; // new import for ntuples

import org.jlab.jnp.utils.options.OptionParser;

import eg2Cuts.eg2Target

def FindSector = {
  phi ->
  if(phi > -30 && phi < 30) {
    sect = 1;
  } else if(phi > -90 && phi < -30) {
    sect = 2;
  } else if(phi > -150 && phi < -90) {
    sect = 3;
  } else if(phi > 150 || phi < -150) {
    sect = 4;
  } else if(phi > 90 && phi < 150) {
    sect = 5;
  } else if(phi > 30 && phi < 90) {
    sect = 6;
  }
  return sect;
}

long st = System.currentTimeMillis(); // start time

GStyle.getAxisAttributesX().setTitleFontSize(24);
GStyle.getAxisAttributesY().setTitleFontSize(24);
GStyle.getAxisAttributesX().setLabelFontSize(24);
GStyle.getAxisAttributesY().setLabelFontSize(24);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

eg2Target myTarget = new eg2Target();  // create the eg2 target object

OptionParser p = new OptionParser("eg2Vertex.groovy");

p.addOption("-M", "0", "Max. Events");
p.addOption("-c", "20000", "Event progress counter");
p.addOption("-o", "eg2Vertex_Hists.hipo", "output file name");

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

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;
int sector;
int counterTotal = 0;
int counterFile;

PhysicsConstants PhyConsts= new PhysicsConstants();
double LIGHTSPEED = PhyConsts.speedOfLight(); // speed of light in cm/ns
println "Speed of light = " + LIGHTSPEED + " cm/ns";

int num = -1; // particle index
int MAX_SECTORS = myTarget.Get_MAX_SECTORS();
String[] part = ["electron","proton","positive"];
double[] partMass = [PhyConsts.massElectron(),PhyConsts.massProton(),0.0];
String hTitle = "Experiment: eg2";
H1F[] h1_Vz = new H1F[part.size()];
H2F[] h2_Vz_phi = new H2F[part.size()];
H1F[] h1_Vz_corr = new H1F[part.size()];
H2F[] h2_Vz_phi_corr = new H2F[part.size()];
H1F[][] h1_Vz_sec = new H1F[part.size()][MAX_SECTORS];
H1F[][] h1_Vz_sec_corr = new H1F[part.size()][MAX_SECTORS];
int nBins_Vz = 500;
double Vz_Lo = -33.0;
double Vz_Hi = -20.0;
int nBins_Phi = 360;
double Phi_Lo = -180.0;
double Phi_Hi = 180.0;

Vector3[] vec3 = new Vector3[part.size()];
Vector3[] vec3_corr = new Vector3[part.size()];
LorentzVector[] vec4 = new LorentzVector[part.size()];

part.eachWithIndex { partName, ih->
  vec3[ih] = new Vector3(0.0,0.0,0.0);
  vec3_corr[ih] = new Vector3(0.0,0.0,0.0);
  vec4[ih] = new LorentzVector(0.0,0.0,0.0,0.0);

  h1_Vz[ih] = new H1F("h1_Vz_"+partName,"Vertex z (cm)","Counts",nBins_Vz,Vz_Lo,Vz_Hi);
  h1_Vz[ih].setTitle(hTitle);
  h1_Vz[ih].setFillColor(44);

  h1_Vz_corr[ih] = new H1F("h1_Vz_corr_"+partName,"Vertex z (cm)","Counts",nBins_Vz,Vz_Lo,Vz_Hi);
  h1_Vz_corr[ih].setTitle(hTitle);
  h1_Vz_corr[ih].setFillColor(BLUE);

  h2_Vz_phi[ih] = new H2F("h2_Vz_phi_"+partName,nBins_Vz,Vz_Lo,Vz_Hi,nBins_Phi,Phi_Lo,Phi_Hi);
  h2_Vz_phi[ih].setTitle("Experiment: eg2");
  h2_Vz_phi[ih].setTitleX("Vertex z (cm)");
  h2_Vz_phi[ih].setTitleY("#phi (deg.)");

  h2_Vz_phi_corr[ih] = new H2F("h2_Vz_phi_corr_"+partName,nBins_Vz,Vz_Lo,Vz_Hi,nBins_Phi,Phi_Lo,Phi_Hi);
  h2_Vz_phi_corr[ih].setTitle("Experiment: eg2");
  h2_Vz_phi_corr[ih].setTitleX("Vertex z (cm)");
  h2_Vz_phi_corr[ih].setTitleY("#phi (deg.)");

  for(int i=0; i<MAX_SECTORS; i++){
    h1_Vz_sec[ih][i] = new H1F("h1_Vz_sec_"+partName+"_"+i,"Vertex z (cm)","Counts",nBins_Vz,Vz_Lo,Vz_Hi);
    h1_Vz_sec[ih][i].setTitle(hTitle);

    h1_Vz_sec_corr[ih][i] = new H1F("h1_Vz_sec_corr_"+partName+"_"+i,"Vertex z (cm)","Counts",nBins_Vz,Vz_Lo,Vz_Hi);
    h1_Vz_sec_corr[ih][i].setTitle(hTitle);
  }
}

Event      event  = new Event();
Bank       bank   = new Bank(reader.getSchemaFactory().getSchema("EVENT::particle"));

while(reader.hasNext()) {
  if(counterFile % printCounter == 0) println counterFile;
  if(maxEvents!=0 && counterFile >= maxEvents) break;

  reader.nextEvent(event);
  event.read(bank);

  int rows = bank.getRows();
  for(int i = 0; i < rows; i++){
    switch(i){
      case {bank.getInt("pid",i)==11}: num = 0; break;
      case {bank.getInt("pid",i)==2212}: num = 1; break;
      case {bank.getInt("charge",i)>0}: num = 2; break;
      default: num = -1; break;
    }

    if(num >= 0 && num < part.size()){
      vec4[num].setPxPyPzM(bank.getFloat("px",i), bank.getFloat("py",i), bank.getFloat("pz",i), partMass[num]);
      vec3[num].setXYZ(bank.getFloat("vx",i), bank.getFloat("vy",i), bank.getFloat("vz",i));
      vec3_corr[num] = myTarget.Get_CorrectedVertex(vec3[num],vec4[num]); // corrected vertex

      h1_Vz[num].fill(vec3[num].z());
      double phi_deg = Math.toDegrees(vec4[num].phi()); // convert to degrees
      h2_Vz_phi[num].fill(vec3[num].z(),phi_deg);

      sector = FindSector(phi_deg);
//      System.out.println("Sector " + sector + " " + myTarget.Get_Sector(vec4[num].phi()) + " " + phi_deg);
      h1_Vz_sec[num][sector-1].fill(vec3[num].z());

      if(Math.abs(vec3_corr[num].y())<1.4){
        h1_Vz_corr[num].fill(vec3_corr[num].z());
        h2_Vz_phi_corr[num].fill(vec3_corr[num].z(),phi_deg);
        h1_Vz_sec_corr[num][sector-1].fill(vec3_corr[num].z());
      }
    }
  }
  counterFile++;
}
System.out.println("processed (total) = " + counterTotal);

String dirLabel;
TDirectory dir = new TDirectory();
TCanvas[] can = new TCanvas[part.size()];
int can_title_size = 24;
part.eachWithIndex { partName, ih->
  dirLabel = "/"+partName;
  dir.mkdir(dirLabel);
  dir.cd(dirLabel);
  can[ih] = new TCanvas("can_"+partName,1500,900);
  can[ih].divide(3,2);
  can[ih].cd(0);
  can[ih].getPad().setTitleFontSize(can_title_size);
  can[ih].draw(h1_Vz[ih]);
  dir.addDataSet(h1_Vz[ih]);
  can[ih].cd(1);
  can[ih].getPad().setTitleFontSize(can_title_size);
  can[ih].draw(h2_Vz_phi[ih]);
  dir.addDataSet(h2_Vz_phi[ih]);
  can[ih].cd(2);
  can[ih].getPad().setTitleFontSize(can_title_size);
  for(int i=0; i<MAX_SECTORS; i++){
    h1_Vz_sec[ih][i].setLineColor(i);
    if(i==0){
      can[ih].draw(h1_Vz_sec[ih][i]);
    }else{
      can[ih].draw(h1_Vz_sec[ih][i],"same");
    }
    dir.addDataSet(h1_Vz_sec[ih][i]);
  }
  can[ih].cd(3);
  can[ih].getPad().setTitleFontSize(can_title_size);
  can[ih].draw(h1_Vz_corr[ih]);
  dir.addDataSet(h1_Vz_corr[ih]);
  can[ih].cd(4);
  can[ih].getPad().setTitleFontSize(can_title_size);
  can[ih].draw(h2_Vz_phi_corr[ih]);
  dir.addDataSet(h2_Vz_phi_corr[ih]);
  can[ih].cd(5);
  can[ih].getPad().setTitleFontSize(can_title_size);
  for(int i=0; i<MAX_SECTORS; i++){
    h1_Vz_sec_corr[ih][i].setLineColor(i+1);
    if(i==0){
      can[ih].draw(h1_Vz_sec_corr[ih][i]);
    }else{
      can[ih].draw(h1_Vz_sec_corr[ih][i],"same");
    }
    dir.addDataSet(h1_Vz_sec_corr[ih][i]);
  }
}
dir.writeFile(outFile); // write the histograms to the file

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen
