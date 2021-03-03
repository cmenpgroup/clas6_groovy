package eg2Cuts

class clas6EC {

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
}
