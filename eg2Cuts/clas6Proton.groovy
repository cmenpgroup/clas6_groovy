package eg2Cuts

import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.math.*;
import org.jlab.groot.fitter.*;

public class clas6Proton {

    public enum ProtonIDCuts{
      STD,SIGMA_1_0,SIGMA_1_5,SIGMA_2_0,SIGMA_2_5,SIGMA_3_0
    }

    static def Plimits = [0.2,0.8,3.0];
//  Parameters from Or Hen
    static def lowP_lower = [-26.8257,153.155,-408.979,673.476,-790.433,708.255,-474.843,217.295,-58.6269,6.90981];
    static def lowP_upper = [120.251,-1168.19,5237.42,-13638.1,22325.3,-23649.6,16177.8,-6894.16,1663.4,-173.474];
    static def highP_lower = [-1.1009,0.719988,-0.280016,0.0319352];
    static def highP_upper = [1.59223,-1.49056,0.676338,-0.104644];

//  Parameters from M. H. Wood
    static def lowP_lower_1_0 = [-2.0422168814772323,-1.3451519051810799,17.275575589566643,0.08921753724871984,-32.60817972322157,-35.74903149915523,14.210327788187966,89.72311881808517,93.2668747469746,-159.73860066775387];
    static def lowP_upper_1_0 = [66.2532345797808,-505.942088070417,1309.1538010759375,-691.0023017304252,-1788.8984265769855,752.3070842301471,3293.431620067332,220.13000969155246,-6612.341506353419,4031.5361438062773];
    static def highP_lower_1_0 = [-0.2877400561814147,-0.007137783981180271,0.05626850509053691,-0.012478365290976725];
    static def highP_upper_1_0 = [0.730054680605518,-0.5402533763004982,0.26565041708788273,-0.047845521266182314];

    static def lowP_lower_1_5 = [-8.793693313742441,27.9872528820737,-11.41692102662695,-33.602521925114864,-11.361923947192414,29.0901718722599,46.85810389015145,19.44681264194296,-34.519499842995785,-37.62330694038743];
    static def lowP_upper_1_5 = [81.72957606403867,-617.0394502923606,1585.910933709864,-829.5621676088213,-2165.691406950516,906.4048590375108,3986.3024861402178,262.09782262290406,-8020.135100918154,4903.560011683063];
    static def highP_lower_1_5 = [-0.5421887403306627,0.126141114026095,0.003923027123678202,-0.0036365763012905263];
    static def highP_upper_1_5 = [0.9845033651359688,-0.6735322750776053,0.3179958955384099,-0.05668731035069989];

    static def lowP_lower_2_0 = [-17.461106132711382,71.98010011448311,-67.5761283450894,-82.09852091451413,48.87413651911435,157.6733693627079,82.4416402949596,-176.09196607243032,-329.1123849872472,322.1022773673661];
    static def lowP_upper_2_0 = [96.01125684043431,-711.9099561793429,1797.332730309307,-914.2201009331419,-2375.6913102852636,916.9982461809541,4247.808830130243,417.79584583689086,-8350.944295280562,4953.8251993101885];
    static def highP_lower_2_0 = [-1.668671469088495,1.574924347604879,-0.7509176048369096,0.12042077891734768];
    static def highP_upper_2_0 = [1.9284408141841745,-1.8045065086380618,0.9002473000957958,-0.15212003172104444];

    static def lowP_lower_2_5 = [-42.0882167946718,271.80643200848726,-628.3522722010671,279.0631243403004,848.9531446346007,-326.5696548333036,-1557.2918536278744,-73.59549287244882,3243.5262944649994,-2073.4936696181685];
    static def lowP_upper_2_5 = [112.68242782055468,-839.2361271786397,2139.4322236187477,-1106.6866174602687,-2919.2943868112397,1214.6128313904549,5372.081937503868,346.023100820702,-10835.79970636825,6647.663535168332];
    static def highP_lower_2_5 = [-1.0510861083553447,0.3926989094222026,-0.10076792838083395,0.014047001586274338];
    static def highP_upper_2_5 = [1.4934007326333694,-0.9400900693281455,0.42268685026866104,-0.07437088807583085];

    static def lowP_lower_3_0 = [-57.56379443743068,382.8967707945226,-905.0886083896855,417.6093836872813,1225.7159481076603,-480.6491913788573,-2250.105252532629,-115.5650348263859,4651.2003358116,-2945.441131094498];
    static def lowP_upper_3_0 = [128.1590164202921,-950.3358994145747,2416.197150446759,-1245.2533162574239,-3296.096097107342,1368.7175107975802,6064.9740672346115,387.98725907405344,-12243.635904491863,7519.716049868261];
    static def highP_lower_3_0 = [-1.3055347924459686,0.525977807081282,-0.1531134059895049,0.02288879048040078];
    static def highP_upper_3_0 = [1.7478494162337979,-1.0733689663581678,0.47503232770150855,-0.08321267697517896];

    static Map IDfcn_proton = [loP_bot_std:lowP_lower,loP_top_std:lowP_upper,hiP_bot_std:highP_lower,hiP_top_std:highP_upper,
                               loP_bot_1_0:lowP_lower_1_0,loP_top_1_0:lowP_upper_1_0,hiP_bot_1_0:highP_lower_1_0,hiP_top_1_0:highP_upper_1_0,
                               loP_bot_1_5:lowP_lower_1_5,loP_top_1_5:lowP_upper_1_5,hiP_bot_1_5:highP_lower_1_5,hiP_top_1_5:highP_upper_1_5,
                               loP_bot_2_0:lowP_lower_2_0,loP_top_2_0:lowP_upper_2_0,hiP_bot_2_0:highP_lower_2_0,hiP_top_2_0:highP_upper_2_0,
                               loP_bot_2_5:lowP_lower_2_5,loP_top_2_5:lowP_upper_2_5,hiP_bot_2_5:highP_lower_2_5,hiP_top_2_5:highP_upper_2_5,
                               loP_bot_3_0:lowP_lower_3_0,loP_top_3_0:lowP_upper_3_0,hiP_bot_3_0:highP_lower_3_0,hiP_top_3_0:highP_upper_3_0];

    ProtonIDCuts mycuts;

    public void SetCuts(ProtonIDCuts cuts) {this.mycuts = cuts;}

    public String GetCutName(){
        println "clas6Proton::GetCutName " + this.mycuts;
        String ret;
        this.mycuts = ProtonIDCuts.STD;
        println "clas6Proton::GetCutName " + this.mycuts;        
        switch(this.mycuts){
          case STD:
            ret = "loP_bot_std";
            break;
          case SIGMA_1_0:
            ret = "loP_bot_1_0";
            break;
          case SIGMA_1_5:
            ret = "loP_bot_1_5";
            break;
          case SIGMA_2_0:
            ret = "loP_bot_2_0";
            break;
          case SIGMA_2_5:
            ret = "loP_bot_2_5";
            break;
          case SIGMA_3_0:
            ret = "loP_bot_3_0";
            break;
          default:
            ret = "loP_bot_std";
            break;
        }
        return ret;
    }

    static boolean  Get_ProtonTOF_Cut(double mom, double corrTOF) {
        double low = 0.0;
        double hi = 0.0;
        boolean ret = false;

        switch(mom){
          // test low momentum cut
          case {(mom>=this.Plimits[0]) && (mom<this.Plimits[1])}:
            this.IDfcn_proton["loP_bot_std"].eachWithIndex { val, index ->
                low = low + val*Math.pow(mom,index);
            }
            this.IDfcn_proton["loP_top_std"].eachWithIndex { val, index ->
                hi = hi + val*Math.pow(mom,index);
            }
            ret = ((corrTOF>=low) && (corrTOF<=hi));
//            ret = ((corrTOF>=this.Get_CorrectedVertex_Cut(mom,"loP_bot_std")) && (corrTOF<=this.Get_CorrectedVertex_Cut(mom,"loP_top_std")));
            break;

          // test high momentum cut
          case {(mom>=this.Plimits[1]) && (mom<=this.Plimits[2])}:
            this.IDfcn_proton["hiP_bot_std"].eachWithIndex { val, index ->
                low = low + val*Math.pow(mom,index);
            }
            this.IDfcn_proton["hiP_top_std"].eachWithIndex { val, index ->
                hi = hi + val*Math.pow(mom,index);
            }
            ret = ((corrTOF>=low) && (corrTOF<=hi));
//            ret = ((corrTOF>=this.&Get_CorrectedVertex_Cut(mom,"hiP_bot_std")) && (corrTOF<=this.&Get_CorrectedVertex_Cut(mom,"hiP_top_std")));
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

    //
    // Function of the proton ID momentum cuts
    //       keyName - name for the parameter set ("std","1_0","1_5","2_0",etc)
    //       MomRange - set the momentum range (true = low, false = high)
    //       CutBotTop - select the bottom or top cut (values = "bot", "top")
    //
    static F1D fcnMomentumCuts(String CutLabel, String MomRange, String CutBotTop){
      String fcn;
      String Prange;
      double loLimit, hiLimit;

      if(MomRange.matches("lo")){
        // 9th order polynomial function for low momentum range
        fcn = "[a]+[b]*x+[c]*x*x+[d]*x*x*x+[e]*x*x*x*x+[f]*x*x*x*x*x+[g]*x*x*x*x*x*x+[h]*x*x*x*x*x*x*x+[i]*x*x*x*x*x*x*x*x+[j]*x*x*x*x*x*x*x*x*x";
        loLimit = this.Plimits[0];
        hiLimit = this.Plimits[1];
      }else if(MomRange.matches("lo")){
        // 3rd order polynomial function for high momentum range
        fcn = "[a]+[b]*x+[c]*x*x+[d]*x*x*x";
        loLimit = this.Plimits[1];
        hiLimit = this.Plimits[2];
      }else{
        println "clas6Proton::fcnMomentumCuts - no match with MomRange " + MomRange;
      }

      String keyName = MomRange + "P_" + CutBotTop + "_" + CutLabel;

      F1D tempFcn = new F1D("tempFcn",fcn,loLimit,hiLimit);
      double[] tempParam = (double[])this.Get_ProtonCutPars(keyName);
      tempFcn.setParameters(tempParam);
      return tempFcn;
    }
}
