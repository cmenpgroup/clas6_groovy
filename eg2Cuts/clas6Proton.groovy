package eg2Cuts

class clas6Proton {
    static def Plimits = [0.2,0.8,3.0];
//  Parameters from Or Hen
    static def lowP_lower = [-26.8257,153.155,-408.979,673.476,-790.433,708.255,-474.843,217.295,-58.6269,6.90981];
    static def lowP_upper = [120.251,-1168.19,5237.42,-13638.1,22325.3,-23649.6,16177.8,-6894.16,1663.4,-173.474];
    static def highP_lower = [-1.1009,0.719988,-0.280016,0.0319352];
    static def highP_upper = [1.59223,-1.49056,0.676338,-0.104644];

//  Parameters from M. H. Wood
//    static def lowP_lower = [-17.461106132711382,71.98010011448311,-67.5761283450894,-82.09852091451413,48.87413651911435,157.6733693627079,82.4416402949596,-176.09196607243032,-329.1123849872472,322.1022773673661];
//    static def lowP_upper = [96.01125684043431,-711.9099561793429,1797.332730309307,-914.2201009331419,-2375.6913102852636,916.9982461809541,4247.808830130243,417.79584583689086,-8350.944295280562,4953.8251993101885];
//    static def highP_lower = [-1.668671469088495,1.574924347604879,-0.7509176048369096,0.12042077891734768];
//    static def highP_upper = [1.9284408141841745,-1.8045065086380618,0.9002473000957958,-0.15212003172104444];

    static Map IDfcn_proton = [lowMomLower:lowP_lower,lowMomUpper:lowP_upper,highMomLower:highP_lower,highMomUpper:highP_upper];

    static boolean  Get_ProtonTOF_Cut(double mom, double corrTOF) {
        double low = 0.0;
        double hi = 0.0;
        boolean ret = false;

        switch(mom){
          // test low momentum cut
          case {(mom>=this.Plimits[0]) && (mom<this.Plimits[1])}:
            this.IDfcn_proton["lowMomLower"].eachWithIndex { val, index ->
                low = low + val*Math.pow(mom,index);
            }
            this.IDfcn_proton["lowMomUpper"].eachWithIndex { val, index ->
                hi = hi + val*Math.pow(mom,index);
            }
            ret = ((corrTOF>=low) && (corrTOF<=hi));
//            ret = ((corrTOF>=this.Get_CorrectedVertex_Cut(mom,"lowMomLower")) && (corrTOF<=this.Get_CorrectedVertex_Cut(mom,"lowMomUpper")));
            break;

          // test high momentum cut
          case {(mom>=this.Plimits[1]) && (mom<=this.Plimits[2])}:
            this.IDfcn_proton["highMomLower"].eachWithIndex { val, index ->
                low = low + val*Math.pow(mom,index);
            }
            this.IDfcn_proton["highMomUpper"].eachWithIndex { val, index ->
                hi = hi + val*Math.pow(mom,index);
            }
            ret = ((corrTOF>=low) && (corrTOF<=hi));
//            ret = ((corrTOF>=this.&Get_CorrectedVertex_Cut(mom,"highMomLower")) && (corrTOF<=this.&Get_CorrectedVertex_Cut(mom,"highMomUpper")));
            break;

          // set the cut to false if not in the momentum range
          default:
            ret = false;
            break;
        }
        return ret;
    }

    static double Get_CorrectedVertexTime_Cut(double mom, String keyName) {
      double ret = -99.0;

      this.IDfcn_proton[keyName].eachWithIndex { val, index ->
        ret = ret + val*Math.pow(mom,index);
      }
      return ret;
    }

    static List Get_ProtonCutPars(String keyName) {
        return this.IDfcn_proton[keyName];
    }

    static boolean LowMomentumCut(double mom){
      boolean ret = false;
      if(mom >= this.Plimits[0]) ret = true;
      return ret;
    }
}
