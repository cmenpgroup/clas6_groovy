package eg2Cuts

class clas6beta {
    def Get_BetaFromMass = {
        mom, mass->
        double ret = -99.0;
        double momSq = mom*mom;

        if(momSq>0.0) ret = 1.0/Math.sqrt((mass*mass)/momSq + 1.0);
        return ret;
    }

    def Get_BetaFromLorentzVecMass = {
      Vec4->
      double ret = -99.0;
      double Psq = Vec4.p()*Vec4.p();
      double Msq = Vec4.mass()*Vec4.mass();
      if(Psq>0.0) ret = 1.0/Math.sqrt(Msq/Psq + 1.0);
      return ret;
    }

    def ProtonDBeta_Cut = {
      dbeta ->
      boolean ret = (dbeta >= -0.03 && dbeta < 0.02);
      return ret;
    }

    def Get_Msq = {
      mom, beta->
      double Msq = -99.0;
      double betaSq = beta*beta;
      if(betaSq>0.0) Msq = mom*mom*(1.0-betaSq)/betaSq;
      return Msq;
    }
}
