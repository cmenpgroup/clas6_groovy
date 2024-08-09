package eg2Cuts

//---- imports for PHYSICS library
import org.jlab.clas.physics.*;

class eg2Target {

  static double EBEAM = 5.014;
  static double Q2_DIS = 1.0;
  static double W_DIS = 2.0;
  static double YB_DIS = 0.85;
  static int MAX_SECTORS = 6;

  int Get_MAX_SECTORS(){
    return this.MAX_SECTORS;
  }

  double Get_Beam_Energy(){
    return this.EBEAM;
  }

  double Get_Q2_DIS(){
    return this.Q2_DIS;
  }

  double Get_W_DIS(){
    return this.W_DIS;
  }

  double Get_YB_DIS(){
    return this.YB_DIS;
  }

  def Get_Sector = {
    phi_rad->
    double phi_deg = Math.toDegrees(phi_rad); // convert to degrees
    int phi_new = phi_deg+30.0; // shift by 30 deg
    if(phi_new<0)phi_new+=360.0; // if negative, shift positive
    int sect =  Math.floor(phi_new/60.0);
    return sect;
  }

  def Get_TargetIndex = {
    Vec3->
      int ret = 2; // init the return variable to not D2 or solid targets

      if (Vec3.z() >= -32.0 &&  Vec3.z() < -28.0) {
        ret = 0; // deuterium target
      } else if (Vec3.z() >= -26.0 && Vec3.z() < -23.0) {
        ret = 1; // nuclear target
      } else {
        ret = 2; // no target
      }
      return ret;
  }

  def Get_CorrectedVertex = {
    Vec3, Vec4->

    double phi_deg = Math.toDegrees(Vec4.phi()); // convert to degrees
    int phi_new = phi_deg+30.0; // shift by 30 deg
    if(phi_new<0)phi_new+=360.0; // if negative, shift positive
    int sect =  Math.floor(phi_new/60.0);

    Vector3 RotatedVertPos = new Vector3();
    RotatedVertPos.copy(Vec3);
    Vector3 RotatedVertDir = new Vector3();
    RotatedVertDir.copy(Vec4.vect());
    Vector3 TargetPos = new Vector3(0.043,-0.33,0.0);

    RotatedVertPos.rotateZ(-Math.toRadians(60.0*sect));
    RotatedVertDir.rotateZ(-Math.toRadians(60.0*sect));
    TargetPos.rotateZ(-Math.toRadians(60.0*sect));

    double ShiftLength = (TargetPos.x() - RotatedVertPos.x())/RotatedVertDir.x();
    RotatedVertDir.setXYZ(ShiftLength*RotatedVertDir.x(),ShiftLength*RotatedVertDir.y(),ShiftLength*RotatedVertDir.z());
    RotatedVertPos.add(RotatedVertDir);

    Vector3 ParticleVertCorr = new Vector3();
    ParticleVertCorr.copy(RotatedVertPos);
    Vector3 ParticleVertOffset = new Vector3(TargetPos.x(),TargetPos.y(),0.0);
    ParticleVertCorr.sub(ParticleVertOffset);
    return ParticleVertCorr;
  }
}
