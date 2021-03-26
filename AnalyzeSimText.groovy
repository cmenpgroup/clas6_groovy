import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;
import org.jlab.jnp.pdg.PDGDatabase;
import org.jlab.jnp.pdg.PDGParticle;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

PhysicsConstants PhyConsts= new PhysicsConstants();
PDGDatabase myPDGdb = new PDGDatabase();

def Get_ParticleNumber = { partList, pid ->
  int pIndex = 0;

  for(List pList in partList) if(pList[2]==pid) pIndex++;
  return pIndex;
}

def Get_LorentzVector = { partList, pid, num ->
  LorentzVector Vec = new LorentzVector(0,0,0,0);
  int pIndex = 0;

  PDGParticle partPDG = myPDGdb.getParticleById(pid);

  for(List pList in partList){
    if(pList[2]==pid){
      Vec.setPxPyPzM(pList[4],pList[5],pList[6],partPDG.mass());
      pIndex++;
      if(pIndex==num) break;
    }
  }
  return Vec;
}

def Get_EventNum = {partList ->
  for(List pList in partList) return pList[0];
}

def Get_SimType = {partList ->
  for(List pList in partList) return pList[1];
}

int simEvent = 0;
int counterFile = 0;
def printCounter = 10000;

int eventNum, simType, pid;
double beta, px, py, pz, vx, vy, vz;
String[] str;
def ParticleList = [];

int NUM_PARTICLES = 4; // minimum number of particles in event
int NUM_PHOTONS = 2; // number of photons analyzed per event
int nPhotons = 0; // photon counter per event

LorentzVector electron = new LorentzVector(0,0,0,0);
LorentzVector pi0 = new LorentzVector(0,0,0,0);

int REC_EVENT = 0;
int GEN_EVENT = 1;
String[] SimLabel = ["Rec","Gen"];
H1F[] h1_Mpi0 = new H1F[SimLabel.size()];
SimLabel.eachWithIndex { nSim, iSim->
  String hname = "h1_Mpi0_" + nSim;
  h1_Mpi0[iSim] = new H1F(hname,"IM(#gamma #gamma) (GeV)","Counts",100,0.0,1.0);
  h1_Mpi0[iSim].setTitle("eg2 - " + nSim);
  h1_Mpi0[iSim].setFillColor(44);
}

new File("/Users/wood5/clas/clas-data/eg2/sim_borquez/D/00/recsisD_08.txt").eachLine { line ->
  if(line ==~ /\d+\t\d+/){ // select the line with the event number and simulation type

    // analyze the event if the ParticleList is full
    if(ParticleList.size()>=NUM_PARTICLES){
      if(Get_ParticleNumber(ParticleList,22)>=NUM_PHOTONS){
        LorentzVector photon1 = Get_LorentzVector(ParticleList,22,1);
        LorentzVector photon2 = Get_LorentzVector(ParticleList,22,2);
        pi0 = LorentzVector.from(photon1);
        pi0.add(photon2);
        simEvent = Get_SimType(ParticleList);
        if(simEvent==REC_EVENT || simEvent==GEN_EVENT) h1_Mpi0[simEvent].fill(pi0.mass())
      }
    }
    ParticleList.clear(); // clear the event information

    // start gathering the next event
    str = line.split('\t');
    str.eachWithIndex{ val, ival->
      switch(ival){
        case 0: eventNum = val.toInteger(); break;
        case 1: simType = val.toInteger(); break;
        default: break;
      }
    }
    if(counterFile % printCounter == 0) println counterFile;
    counterFile++;  // counter the number of events
  }else{ // gather the particle informaiton per event
    str = line.split('\s+');
    str.eachWithIndex{ val, ival->
      switch(ival){
        case 1: pid = val.toInteger();
        case 2: beta = Double.parseDouble(val); break;
        case 3: px = Double.parseDouble(val); break;
        case 4: py = Double.parseDouble(val); break;
        case 5: pz = Double.parseDouble(val); break;
        case 6: vx = Double.parseDouble(val); break;
        case 7: vy = Double.parseDouble(val); break;
        case 8: vz = Double.parseDouble(val); break;
        default: break;
      }
    }
    ParticleList << [eventNum,simType,pid,beta,px,py,pz,vx,vy,vz];
  }
}
def dirname = ["/Reconstructed","/Generated"];
TDirectory dir = new TDirectory();
dirname.each { val->
  dir.mkdir(val);
}

int c1_title_size = 22;
TCanvas c1 = new TCanvas("c1",1200,500);
c1.divide(2,1);
SimLabel.eachWithIndex { nSim, iSim->
  c1.cd(iSim);
  c1.getPad().setTitleFontSize(c1_title_size);
  c1.draw(h1_Mpi0[iSim]);
  dir.cd(dirname[iSim]);
  dir.addDataSet(h1_Mpi0[iSim]); // add to the histogram file
}

String histFile = "AnalyzeSimText_hists.hipo";
dir.writeFile(histFile);
