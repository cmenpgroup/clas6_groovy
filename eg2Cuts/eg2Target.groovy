package eg2Cuts

import org.jlab.jnp.physics.*;
 
class eg2Target {
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
}
