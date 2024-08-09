package kinematics

import org.jlab.clas.physics.*;
import org.jlab.clas.pdg.PhysicsConstants;
//import org.jlab.jnp.physics.*;
//import org.jlab.jnp.pdg.PhysicsConstants;

class ReactionKine {
  LorentzVector beamElectron = new LorentzVector(0.0,0.0,0.0,0.0);
  LorentzVector target = new LorentzVector(0.0,0.0,0.0,0.0);
  LorentzVector scatteredElectron = new LorentzVector(0.0,0.0,0.0,0.0);
  LorentzVector virtualPhoton = new LorentzVector(0.0,0.0,0.0,0.0);
  LorentzVector hadron = new LorentzVector(0.0,0.0,0.0,0.0);
  LorentzVector targetLiquid = new LorentzVector(0.0,0.0,0.0,0.0);
  LorentzVector targetSolid = new LorentzVector(0.0,0.0,0.0,0.0);

  PhysicsConstants PhyConsts= new PhysicsConstants();

  int solidTgtIndex = -1;
  def solidTgt = ["C","Fe","Pb"];
  double[] solidTgtMass = [11.1880,52.0196,193.0068];  // nuclear mass in GeV/c^2
  double amu2GeV = 0.9315; // 1 amu = 0.931494 GeV/c^2
  double deuteronMass = 1.875612762; // deuteron mass in GeV/c^2

  void setSolidTarget(String userTgt){
      solidTgtIndex = solidTgt.indexOf(userTgt);
      if(solidTgtIndex==-1){
        println "Target " + userTgt + " is unavailable! Please choose one of the following:";
        println solidTgt;
        exit;
      }
      targetLiquid.setPxPyPzM(0.0,0.0,0.0,deuteronMass);
      targetSolid.setPxPyPzM(0.0,0.0,0.0,solidTgtMass[solidTgtIndex]);
  }
  void setBeam(double beamE, double beamM){
      beamElectron.setPxPyPzM(0.0,0.0,beamE,beamM);
  }
  LorentzVector getBeam(){
      return beamElectron;
  }
  void setTarget(double targetM){
      target.setPxPyPzM(0.0,0.0,0.0,targetM);
  }
  LorentzVector getTarget(){
      return target;
  }
  void setScatteredElectron(LorentzVector vec4){
      scatteredElectron.copy(vec4);
  }
  LorentzVector getScatteredElectron(){
      return scatteredElectron;
  }
  void setHadron(LorentzVector vec4){
      hadron.copy(vec4);
  }
  LorentzVector getHadron(){
      return hadron;
  }
  LorentzVector getVirtualPhoton(){
    virtualPhoton.copy(beamElectron);   // calculate Q-squared, first copy incident e- 4-vector
    virtualPhoton.sub(scatteredElectron);  // calculate Q-squared, subtract scattered e- 4-vector
    return virtualPhoton;
  }
  double Q2(){
    LorentzVector virt = this.getVirtualPhoton()
    return -virt.mass2();
  }
  double W(){
    LorentzVector virt = this.getVirtualPhoton()
    virt.add(target)
    return virt.mass();
  }
  double Mx(){ // missing mass: beam + target - scatterElectron - hadron
    LorentzVector virt = this.getVirtualPhoton();
    virt.add(target).sub(hadron)
    return virt.mass();
  }
  double Mx2(){ // missing mass: beam + target - scatterElectron - hadron
    LorentzVector virt = this.getVirtualPhoton();
    return virt.add(target).sub(hadron).mass2();
  }
  double MxNuclei(int isSolid){ // missing mass: beam + nucleus - scatterElectron - hadron
    LorentzVector virt = this.getVirtualPhoton();
    switch(isSolid){
      case 0:
        virt.add(targetLiquid);
        break;
      case 1:
        virt.add(targetSolid);
        break;
      case 2:
        virt.add(target);
        break;
      default:
        println "ReactionKine.groovy: MxNuclei(): Unknown target region" + isSolid;
        exit;
        break;
    }
    return virt.sub(hadron).mass();
  }
  double nu(){
    return beamElectron.e()-scatteredElectron.e();
  }
  double Xb(){
    double ret = -99.0;
    if(this.nu()>0.0) ret = this.Q2()/(2.0*this.nu()*PhyConsts.massProton());
    return ret;
  }
  double Yb(){
    double ret = -99.0;
    if(beamElectron.e()>0.0) ret = this.nu()/beamElectron.e();
    return ret;
  }
  double ThetaPQ(){
    LorentzVector virt = this.getVirtualPhoton();
    return virt.angle(hadron);
  }
  double cosThetaPQ(){
    return Math.cos(this.ThetaPQ());
  }
  double sinThetaPQ(){
    return Math.sin(this.ThetaPQ());
  }
  double pT2(){
    double momHadron = hadron.vect().mag();
    double sinPQ = this.sinThetaPQ();
    return momHadron*momHadron*sinPQ*sinPQ;
  }
  double pL2(){
    double momHadron = hadron.vect().mag();
    double cosPQ = this.cosThetaPQ();
    return momHadron*momHadron*cosPQ*cosPQ;
  }
  double PhiPQ(){
    LorentzVector tempHadron = this.getHadron();
    LorentzVector virt = this.getVirtualPhoton();
    double phi_z = Math.PI - virt.phi();
    virt.rotateZ(phi_z);
    tempHadron.rotateZ(phi_z);
    Vector3 Vhelp = new Vector3(0.0,0.0,1.0);
    double phi_y = virt.vect().angle(Vhelp);
    virt.rotateY(phi_y);
    tempHadron.rotateY(phi_y);
    return tempHadron.phi();
  }
  double zh(){
    return hadron.e()/this.nu();
  }
  double zLC(){
    return (hadron.e() + hadron.pz())/(PhyConsts.massProton() + 2*this.nu());
  }
}
