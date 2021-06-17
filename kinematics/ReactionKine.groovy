package kinematics

import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

class ReactionKine {
  LorentzVector beamElectron = new LorentzVector(0.0,0.0,0.0,0.0);
  LorentzVector target = new LorentzVector(0.0,0.0,0.0,0.0);
  LorentzVector scatteredElectron = new LorentzVector(0.0,0.0,0.0,0.0);
  LorentzVector virtualPhoton = new LorentzVector(0.0,0.0,0.0,0.0);
  LorentzVector hadron = new LorentzVector(0.0,0.0,0.0,0.0);

  PhysicsConstants PhyConsts= new PhysicsConstants();

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
      scatteredElectron = LorentzVector.from(vec4);
  }
  LorentzVector getScatteredElectron(){
      return scatteredElectron;
  }
  void setHadron(LorentzVector vec4){
      hadron = LorentzVector.from(vec4);
  }
  LorentzVector getHadron(){
      return hadron;
  }
  LorentzVector getVirtualPhoton(){
    virtualPhoton = LorentzVector.from(beamElectron);   // calculate Q-squared, first copy incident e- 4-vector
    virtualPhoton.sub(scatteredElectron);  // calculate Q-squared, subtract scattered e- 4-vector
    return virtualPhoton;
  }
  double Q2(){
    LorentzVector virt = this.getVirtualPhoton()
    return -virt.mass2();
  }
  double W(){
    LorentzVector virt = this.getVirtualPhoton()
    return virt.add(target).mass();
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
