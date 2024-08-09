package eg2Cuts

import org.jlab.clas.physics.*;
//import org.jlab.jnp.physics.*;

class clas6EC {

  static double ECIN_MIN = 0.06;
  static double EC_SAMPLEFRAC = 0.27;
  static double ELECTRON_MOM = 0.64;

  double Get_ECIN_MIN(){
    return this.ECIN_MIN;
  }

  double Get_EC_SAMPLEFRAC(){
    return this.EC_SAMPLEFRAC;
  }

  double Get_ELECTRON_MOM(){
    return this.ELECTRON_MOM;
  }

  def dt_ECSC = {
    ECtime, SCtime->

    double dt = ECtime - SCtime;
    double dtCentroid = 0.6;
    double dtWidth = 0.6;
    double dtNsigmas = 3.0;
    double dtLo = dtCentroid - dtNsigmas*dtWidth;
    double dtHi = dtCentroid + dtNsigmas*dtWidth;

    boolean ret = (dt >= dtLo && dt < dtHi) ? true : false;
    return ret;
  }

  // calculate the EC sampling fraction using eg2 numbers
  def Get_EC_SamplingFraction = {
      coeff,sector,targMass->
      double ret = 0.0;

      def EC_SamplingFrac_C = new Float[6][5];
      def EC_SamplingFrac_Fe = new Float[6][5];
      def EC_SamplingFrac_Pb = new Float[6][5];

      EC_SamplingFrac_C[0][0] = 0.226726; EC_SamplingFrac_C[0][1] = 0.0379557; EC_SamplingFrac_C[0][2] = -0.00855326; EC_SamplingFrac_C[0][3] = 7.27022e-09; EC_SamplingFrac_C[0][4] = 0.0370079;
      EC_SamplingFrac_C[1][0] = 0.222333; EC_SamplingFrac_C[1][1] = 0.0581705; EC_SamplingFrac_C[1][2] = -0.0131283; EC_SamplingFrac_C[1][3] = 3.12094e-12; EC_SamplingFrac_C[1][4] = 0.0413565;
      EC_SamplingFrac_C[2][0] = 0.245212; EC_SamplingFrac_C[2][1] = 0.0213835; EC_SamplingFrac_C[2][2] = -0.00277372; EC_SamplingFrac_C[2][3] = 8.27916e-08; EC_SamplingFrac_C[2][4] = 0.0426498;
      EC_SamplingFrac_C[3][0] = 0.238399; EC_SamplingFrac_C[3][1] = 0.0301926; EC_SamplingFrac_C[3][2] = -0.00720393; EC_SamplingFrac_C[3][3] = -3.81029e-09; EC_SamplingFrac_C[3][4] = 0.0309331;
      EC_SamplingFrac_C[4][0] = 0.241834; EC_SamplingFrac_C[4][1] = 0.0442975; EC_SamplingFrac_C[4][2] = -0.0105584; EC_SamplingFrac_C[4][3] = 9.74651e-09; EC_SamplingFrac_C[4][4] = 0.0303602;
      EC_SamplingFrac_C[5][0] = 0.245868; EC_SamplingFrac_C[5][1] = 0.0545128; EC_SamplingFrac_C[5][2] = -0.0149168; EC_SamplingFrac_C[5][3] = 1.43097e-08; EC_SamplingFrac_C[5][4] = 0.0483305;

      EC_SamplingFrac_Fe[0][0] = 2.22E-1; EC_SamplingFrac_Fe[0][1] = 2.23E-2; EC_SamplingFrac_Fe[0][2] = -2.41E-3; EC_SamplingFrac_Fe[0][3] = 9.23E-3; EC_SamplingFrac_Fe[0][4] = 2.98E-2;
      EC_SamplingFrac_Fe[1][0] = 2.34E-1; EC_SamplingFrac_Fe[1][1] = 1.95E-2; EC_SamplingFrac_Fe[1][2] = -2.08E-3; EC_SamplingFrac_Fe[1][3] = 8.66E-3; EC_SamplingFrac_Fe[1][4] = 3.09E-2;
      EC_SamplingFrac_Fe[2][0] = 2.52E-1; EC_SamplingFrac_Fe[2][1] = 2.42E-2; EC_SamplingFrac_Fe[2][2] = -3.39E-3; EC_SamplingFrac_Fe[2][3] = 1.08E-2; EC_SamplingFrac_Fe[2][4] = 2.64E-2;
      EC_SamplingFrac_Fe[3][0] = 2.51E-1; EC_SamplingFrac_Fe[3][1] = 2.08E-2; EC_SamplingFrac_Fe[3][2] = -3.27E-3; EC_SamplingFrac_Fe[3][3] = 7.22E-3; EC_SamplingFrac_Fe[3][4] = 2.98E-2;
      EC_SamplingFrac_Fe[4][0] = 2.72E-1; EC_SamplingFrac_Fe[4][1] = 1.18E-2; EC_SamplingFrac_Fe[4][2] = -1.87E-3; EC_SamplingFrac_Fe[4][3] = 1.84E-2; EC_SamplingFrac_Fe[4][4] = 3.48E-2;
      EC_SamplingFrac_Fe[5][0] = 2.52E-1; EC_SamplingFrac_Fe[5][1] = 2.28E-2; EC_SamplingFrac_Fe[5][2] = -3.11E-3; EC_SamplingFrac_Fe[5][3] = 4.11E-3; EC_SamplingFrac_Fe[5][4] = 3.55E-2;

      EC_SamplingFrac_Pb[0][0] = 2.53E-1; EC_SamplingFrac_Pb[0][1] = 1.38E-2; EC_SamplingFrac_Pb[0][2] = -1.40E-3; EC_SamplingFrac_Pb[0][3] = 7.67E-3; EC_SamplingFrac_Pb[0][4] = 3.54E-2;
      EC_SamplingFrac_Pb[0][0] = 2.53E-1; EC_SamplingFrac_Pb[0][1] = 1.38E-2; EC_SamplingFrac_Pb[0][2] = -1.40E-3; EC_SamplingFrac_Pb[0][3] = 7.67E-3; EC_SamplingFrac_Pb[0][4] = 3.54E-2;
      EC_SamplingFrac_Pb[1][0] = 2.49E-1; EC_SamplingFrac_Pb[1][1] = 1.47E-2; EC_SamplingFrac_Pb[1][2] = -1.49E-3; EC_SamplingFrac_Pb[1][3] = 7.53E-3; EC_SamplingFrac_Pb[1][4] = 3.38E-2;
      EC_SamplingFrac_Pb[2][0] = 2.54E-1; EC_SamplingFrac_Pb[2][1] = 2.26E-2; EC_SamplingFrac_Pb[2][2] = -3.05E-3; EC_SamplingFrac_Pb[2][3] = 8.13E-3; EC_SamplingFrac_Pb[2][4] = 2.77E-2;
      EC_SamplingFrac_Pb[3][0] = 2.55E-1; EC_SamplingFrac_Pb[3][1] = 1.90E-2; EC_SamplingFrac_Pb[3][2] = -3.05E-3; EC_SamplingFrac_Pb[3][3] = 7.20E-3; EC_SamplingFrac_Pb[3][4] = 3.04E-2;
      EC_SamplingFrac_Pb[4][0] = 2.76E-1; EC_SamplingFrac_Pb[4][1] = 1.11E-2; EC_SamplingFrac_Pb[4][2] = -1.76E-3; EC_SamplingFrac_Pb[4][3] = 1.81E-2; EC_SamplingFrac_Pb[4][4] = 3.53E-2;
      EC_SamplingFrac_Pb[5][0] = 2.62E-1; EC_SamplingFrac_Pb[5][1] = 1.92E-2; EC_SamplingFrac_Pb[5][2] = -2.62E-3; EC_SamplingFrac_Pb[5][3] = 1.99E-3; EC_SamplingFrac_Pb[5][4] = 3.76E-2;

      if(sector>=1 && sector<=6){ //check that the sector is between 1 and 6
          if(coeff>=0 && coeff<5){
              switch (targMass){
                  case 12: ret = EC_SamplingFrac_C[sector-1][coeff]; break;
                  case 56: ret = EC_SamplingFrac_Fe[sector-1][coeff]; break;
                  case 208: ret = EC_SamplingFrac_Pb[sector-1][coeff]; break;
                  default:
                      System.out.println("Get_EC_SamplingFraction: Target Mass " + targMass + " is unknown.");
                      ret = 0.0;
                      break;
              }
          }
          else{
              System.out.println("Get_EC_SamplingFraction: Coefficient " + coeff + " is out of range.");
          }
      }
      else{
          System.out.println("Get_EC_SamplingFraction: Sector " + sector + " is out of range.");
      }
      return ret;
  }

  // cut on the EC sampling fraction
  def EC_SamplingFraction_Cut = {
      mom, ECtotal, sector, targMass ->
      boolean ret = false;

      double a = Get_EC_SamplingFraction(0,sector,targMass);
      double b = Get_EC_SamplingFraction(1,sector,targMass);
      double c = Get_EC_SamplingFraction(2,sector,targMass);
      double d = Get_EC_SamplingFraction(3,sector,targMass);
      double f = Get_EC_SamplingFraction(4,sector,targMass);

      double centroid = a + b*mom + c*mom*mom;
      double sigma = Math.sqrt(d*d + f*f/Math.sqrt(mom));
      double Nsigma = 2.5;

      double diff = Math.abs(ECtotal/mom - centroid);

      ret = (diff < Nsigma*sigma) ? true : false;

      return ret;
  }

  Vector3 XYZtoUVW(Vector3 xyz){
    double u=0.0;
    double v=0.0;
    double w=0.0;
    double xi=0.0;
    double yi=0.0;
    double zi=0.0;
    double ec_phi = 0.0;
    double phi = 0.0;

    // Parameters
    double ec_theta = 0.4363323;
    double ylow = -182.974;
    double yhi = 189.956;
    double tgrho = 1.95325;
    double sinrho = 0.8901256;
    double cosrho = 0.455715;

    phi = Math.toDegrees(Math.atan2(xyz.y(),xyz.x()));
    if(phi<0.0){phi = phi + 360.0;}
    phi = phi+30.0;
    if(phi>360.0){phi = phi-360.0;}

    ec_phi = ((int)(phi/60.0))*1.0471975;

    Vector3 RotColumnY = new Vector3(Math.cos(ec_theta)*Math.cos(ec_phi),Math.cos(ec_theta)*Math.sin(ec_phi),-Math.sin(ec_theta));
    Vector3 RotColumnX = new Vector3(-Math.sin(ec_phi),Math.cos(ec_phi),0.0);
    Vector3 RotColumnZ = new Vector3(Math.sin(ec_theta)*Math.cos(ec_phi),Math.sin(ec_theta)*Math.sin(ec_phi),Math.cos(ec_theta));

    yi = xyz.dot(RotColumnY);
    xi = xyz.dot(RotColumnX);
    zi = xyz.dot(RotColumnZ) - 510.32;

    u = (yi-ylow)/sinrho;
    v = (yhi-ylow)/tgrho - xi + (yhi-yi)/tgrho;
    w = ((yhi-ylow)/tgrho + xi + (yhi-yi)/tgrho)/2.0/cosrho;

    Vector3 uvw = new Vector3(u,v,w);

    return uvw;
  }

  boolean FidCutU(double u){
    double ecU_lo = 40.0;
    double ecU_hi = 410.0;
    boolean ret = (u>ecU_lo && u<ecU_hi) ? true : false;
    return ret;
  }
  boolean FidCutV(double v){
    double ecV_lo = 0.0;
    double ecV_hi = 370.0;
    boolean ret = (v>=ecV_lo && v<ecV_hi) ? true : false;
    return ret;
  }
  boolean FidCutW(double w){
    double ecW_lo = 0.0;
    double ecW_hi = 405.0;
    boolean ret = (w>=ecW_lo && w<ecW_hi) ? true : false;
    return ret;
  }
  boolean FidCutUVW(Vector3 uvw){
    return (this.FidCutU(uvw.x()) && this.FidCutV(uvw.y()) && this.FidCutW(uvw.z()));
  }
  boolean FidCutXYZ(Vector3 xyz){
    return this.FidCutUVW(this.XYZtoUVW(xyz));
  }
}
