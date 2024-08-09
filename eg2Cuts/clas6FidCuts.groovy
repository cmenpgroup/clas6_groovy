package eg2Cuts

import org.jlab.clas.physics.*;
//import org.jlab.jnp.physics.*;

class clas6FidCuts {

  double kFidThetaMax = 54;

  // For FidThetaMin calculation for electron
  static def kThetaPar0 = [ 15        , 15        ,  15       , 15        ,  13       ,  13        ];
  static def kThetaPar1 = [ -0.425145 , -1.02217  , -0.7837   , -1.47798  ,   3.47361 ,   3.5714   ];
  static def kThetaPar2 = [ -0.666294 , -0.616567 , -0.673602 , -0.647113 ,  -0.34459 ,  -0.398458 ];
  static def kThetaPar3 = [  5.73077  ,  5.51799  ,  8.05224  ,  7.74737  ,   8.45226 ,   9.54265  ];
  static def kThetaPar4 = [ 10.4976   , 14.0557   , 15.2178   , 16.7291   , -63.4556  , -22.649    ];
  static def kThetaPar5 = [ -1.13254  , -1.16189  , -2.08386  , -1.79939  ,  -3.3791  ,  -1.89746  ];

  // For parameter 0 of the FidPhiMin calculation for electron
  static def kFidPar0Low0 = [  25      ,  25        ,  25       ,  24.6345  ,  23.4731  ,  24.8599  ];
  static def kFidPar0Low1 = [ -12      , -12        , -12       , -12       , -12       , -12       ];
  static def kFidPar0Low2 = [   0.5605 ,   0.714261 ,  0.616788 ,   0.62982 ,   1.84236 ,   1.00513 ];
  static def kFidPar0Low3 = [  4.4     ,  4.4       ,  4.4      ,   4.4     ,   4.4     ,   4.4     ];

  // For parameter 1 of the FidPhiMin calculation for electron
  static def kFidPar1Low0 = [  2.1945   ,  4        ,  3.3352  ,  2.22769   ,  1.63143   ,  3.19807  ];
  static def kFidPar1Low1 = [  1.51417  ,  1.56882  ,  2       ,  2         ,  1.90179   ,  0.173168 ];
  static def kFidPar1Low2 = [ -0.354081 , -2        , -2       , -0.760895  , -0.213751  , -0.1      ];
  static def kFidPar1Low3 = [  0.5      ,  0.5      ,  1.01681 ,  1.31808   ,  0.786844  ,  1.6      ];

  // For parameter 0 of the FidPhiMax calculation for electron
  static def kFidPar0High0 = [ 25       ,  25        ,  25        ,  25        ,  23.7067  ,  25       ];
  static def kFidPar0High1 = [ -8       , -10.3277   , -12        , -11.3361   , -12       , -11.4641  ];
  static def kFidPar0High2 = [  0.479446 ,  0.380908 ,   0.675835 ,   0.636018 ,   2.92146 ,   0.55553 ];
  static def kFidPar0High3 = [  4.8      ,  4.79964  ,   4.4      ,   4.4815   ,   4.4     ,   4.41327 ];

  // For parameter 1 of the FidPhiMax calculation for electron
  static def kFidPar1High0 = [  3.57349 ,  3.02279  ,  2.02102 ,  3.1948   ,  3.0934   ,  2.48828 ];
  static def kFidPar1High1 = [  2       ,  0.966175 ,  2       ,  0.192701 ,  0.821726 ,  2       ];
  static def kFidPar1High2 = [ -2       , -2        , -1.70021 , -1.27578  , -0.233492 , -2       ];
  static def kFidPar1High3 = [  0.5     ,  0.527823 ,  0.68655 ,  1.6      ,  1.6      ,  0.70261 ];

  // For FidThetaMinPiPlus calculation for pi+
  static def kThetaPar0PiPlus = [  7.00823   ,  5.5        ,  7.06596   ,  6.32763   ,  5.5       ,  5.5      ];
  static def kThetaPar1PiPlus = [  0.207249  ,  0.1        ,  0.127764  ,  0.1       ,  0.211012  ,  0.281549 ];
  static def kThetaPar2PiPlus = [  0.169287  ,  0.506354   , -0.0663754 ,  0.221727  ,  0.640963  ,  0.358452 ];
  static def kThetaPar3PiPlus = [  0.1       ,  0.1        ,  0.100003  ,  0.1       ,  0.1       ,  0.1      ];
  static def kThetaPar4PiPlus = [  0.1       ,  3.30779    ,  4.499     ,  5.30981   ,  3.20347   ,  0.776161 ];
  static def kThetaPar5PiPlus = [ -0.1       , -0.651811   , -3.1793    , -3.3461    , -1.10808   , -0.462045 ];

  // For parameter 0 of the FidPhiMinPiPlus calculation for pi+
  static def kFidPar0Low0PiPlus = [  25.0     ,  25.0     ,  25.0    ,  25.0      ,  25.0      ,  25.0      ];
  static def kFidPar0Low1PiPlus = [ -12.0     , -12.0     , -12.0    , -12.0      , -12.0      , -12.0      ];
  static def kFidPar0Low2PiPlus = [   1.64476 ,   1.51915 ,   1.1095 ,   0.977829 ,   0.955366 ,   0.969146 ];
  static def kFidPar0Low3PiPlus = [   4.4     ,   4.4     ,   4.4    ,   4.4      ,   4.4      ,   4.4      ];

  // For parameter 1 of the FidPhiMinPiPlus calculation for pi+
  static def kFidPar1Low0PiPlus = [  4.0       ,  4.0  ,  2.78427 ,  3.58539 ,  3.32277   ,  4.0     ];
  static def kFidPar1Low1PiPlus = [  2.0       ,  2.0  ,  2.0     ,  1.38233 ,  0.0410601 ,  2.0     ];
  static def kFidPar1Low2PiPlus = [ -0.978469  , -2.0  , -1.73543 , -2.0     , -0.953828  , -2.0     ];
  static def kFidPar1Low3PiPlus = [  0.5       ,  0.5  ,  0.5     ,  0.5     ,  0.5       ,  1.08576 ];

  // For parameter 0 of the FidPhiMaxPiPlus calculation for pi+
  static def kFidPar0High0PiPlus = [  25.0     , 24.8096  , 24.8758  ,  25.0      , 25.0      , 25.0     ];
  static def kFidPar0High1PiPlus = [ -11.9735  , -8.0     , -8.0     , -12.0      , -8.52574  , -8.0     ];
  static def kFidPar0High2PiPlus = [  0.803484 ,  0.85143 ,  1.01249 ,   0.910994 ,  0.682825 ,  0.88846 ];
  static def kFidPar0High3PiPlus = [  4.40024  ,  4.8     ,  4.8     ,   4.4      ,  4.79866  ,  4.8     ];

  // For parameter 1 of the FidPhiMaxPiPlus calculation for pi+
  static def kFidPar1High0PiPlus = [  2.53606  ,  2.65468  ,  3.17084 ,  2.47156 ,  2.42349  ,  2.64394 ];
  static def kFidPar1High1PiPlus = [  0.442034 ,  0.201149 ,  1.27519 ,  1.76076 ,  1.25399  ,  0.15892 ];
  static def kFidPar1High2PiPlus = [ -2.0      , -0.179631 , -2.0     , -1.89436 , -2.0      , -2.0     ];
  static def kFidPar1High3PiPlus = [  1.02806  ,  1.6      ,  0.5     ,  1.03961 ,  0.815707 ,  1.31013 ];

  static Map mThetaPar0 = [electron:kThetaPar0,piplus:kThetaPar0PiPlus];
  static Map mThetaPar1 = [electron:kThetaPar1,piplus:kThetaPar1PiPlus];
  static Map mThetaPar2 = [electron:kThetaPar2,piplus:kThetaPar2PiPlus];
  static Map mThetaPar3 = [electron:kThetaPar3,piplus:kThetaPar3PiPlus];
  static Map mThetaPar4 = [electron:kThetaPar4,piplus:kThetaPar4PiPlus];
  static Map mThetaPar5 = [electron:kThetaPar5,piplus:kThetaPar5PiPlus];

  static Map mPhiPar0Min0 = [electron:kFidPar0Low0,piplus:kFidPar0Low0PiPlus];
  static Map mPhiPar0Min1 = [electron:kFidPar0Low1,piplus:kFidPar0Low1PiPlus];
  static Map mPhiPar0Min2 = [electron:kFidPar0Low2,piplus:kFidPar0Low2PiPlus];
  static Map mPhiPar0Min3 = [electron:kFidPar0Low3,piplus:kFidPar0Low3PiPlus];

  static Map mPhiPar1Min0 = [electron:kFidPar1Low0,piplus:kFidPar1Low0PiPlus];
  static Map mPhiPar1Min1 = [electron:kFidPar1Low1,piplus:kFidPar1Low1PiPlus];
  static Map mPhiPar1Min2 = [electron:kFidPar1Low2,piplus:kFidPar1Low2PiPlus];
  static Map mPhiPar1Min3 = [electron:kFidPar1Low3,piplus:kFidPar1Low3PiPlus];

  static Map mPhiPar0Max0 = [electron:kFidPar0High0,piplus:kFidPar0High0PiPlus];
  static Map mPhiPar0Max1 = [electron:kFidPar0High1,piplus:kFidPar0High1PiPlus];
  static Map mPhiPar0Max2 = [electron:kFidPar0High2,piplus:kFidPar0High2PiPlus];
  static Map mPhiPar0Max3 = [electron:kFidPar0High3,piplus:kFidPar0High3PiPlus];

  static Map mPhiPar1Max0 = [electron:kFidPar1High0,piplus:kFidPar1High0PiPlus];
  static Map mPhiPar1Max1 = [electron:kFidPar1High1,piplus:kFidPar1High1PiPlus];
  static Map mPhiPar1Max2 = [electron:kFidPar1High2,piplus:kFidPar1High2PiPlus];
  static Map mPhiPar1Max3 = [electron:kFidPar1High3,piplus:kFidPar1High3PiPlus];

  static boolean clas6FidCheckCut(LorentzVector Vec4, String keyName) {
    boolean ret = false; // init the return variable

    double ThetaDeg = this.FidTheta(Vec4);
    double PhiDeg = this.FidPhi(Vec4);
    if (ThetaDeg>this.FidThetaMin(Vec4,keyName) && PhiDeg>this.FidPhiMin(Vec4,keyName) && PhiDeg<this.FidPhiMax(Vec4,keyName)) ret = true;
    return ret;
  }

  static int FidSector(LorentzVector Vec4)
  {
    int sector;
    double phiMod = (this.FidPhi(Vec4) + 90.0) / 60.0;
    if (this.FidPhi(Vec4) != 330) {
      sector = (int)phiMod - 1;
      return sector;
    } else {
      return 5;
    }
  }

  static double FidTheta(LorentzVector Vec4){
    double fid_theta_val = Math.toDegrees(Vec4.theta());
    return fid_theta_val;
  }

  static double FidPhi(LorentzVector Vec4) {
    double fid_phi_val = Math.toDegrees(Vec4.phi());
    if (fid_phi_val < -30){
      fid_phi_val += 360;
      return fid_phi_val;
    }
    if (fid_phi_val > 330){
      fid_phi_val -= 360;
      return fid_phi_val;
    }
    return fid_phi_val;
  }

  static double FidThetaMin(LorentzVector Vec4, String keyName) {
    int sector = this.FidSector(Vec4);

    List par0 = this.mThetaPar0[keyName];
    List par1 = this.mThetaPar1[keyName];
    List par2 = this.mThetaPar2[keyName];
    List par3 = this.mThetaPar3[keyName];
    List par4 = this.mThetaPar4[keyName];
    List par5 = this.mThetaPar5[keyName];

    double Mom = Vec4.p();
    double theta_min_val = par0[sector] + par1[sector] / Math.pow(Mom,2) + par2[sector] * Mom + par3[sector] / Mom + par4[sector] * Math.exp(par5[sector] * Mom);
    return theta_min_val;
  }

  static double FidPhiMin(LorentzVector Vec4, String keyName){
    int sector = this.FidSector(Vec4);
    double fid_phi_min_val = 60.0 * sector;

      if (this.FidTheta(Vec4) <= this.FidThetaMin(Vec4,keyName)) {
        return fid_phi_min_val;
      } else {
        fid_phi_min_val -= this.FidFunc(Vec4,keyName,0,0) * (1 - 1 / (1 + (this.FidTheta(Vec4) - this.FidThetaMin(Vec4,keyName)) / this.FidFunc(Vec4,keyName,0,1)));
        return fid_phi_min_val;
      }
  }

  static double FidPhiMax(LorentzVector Vec4, String keyName){
    int sector = this.FidSector(Vec4);
    double fid_phi_max_val = 60.0 * sector;

    if (this.FidTheta(Vec4) <= this.FidThetaMin(Vec4,keyName)){
      return fid_phi_max_val;
    } else {
      fid_phi_max_val += this.FidFunc(Vec4,keyName,1,0) * (1 - 1 / (1 + (this.FidTheta(Vec4) - this.FidThetaMin(Vec4,keyName)) / this.FidFunc(Vec4,keyName,1,1)));
      return fid_phi_max_val;
    }
  }

  static double FidFunc(LorentzVector Vec4, String keyName, int side, int param)
  {
    int sector = this.FidSector(Vec4);
    double fid_func_val = 0.0; // dummy value to avoid that uninitialized warning
    double Mom = Vec4.p();

    List par0min0 = this.mPhiPar0Min0[keyName];
    List par0min1 = this.mPhiPar0Min1[keyName];
    List par0min2 = this.mPhiPar0Min2[keyName];
    List par0min3 = this.mPhiPar0Min3[keyName];

    List par1min0 = this.mPhiPar1Min0[keyName];
    List par1min1 = this.mPhiPar1Min1[keyName];
    List par1min2 = this.mPhiPar1Min2[keyName];
    List par1min3 = this.mPhiPar1Min3[keyName];

    List par0max0 = this.mPhiPar0Max0[keyName];
    List par0max1 = this.mPhiPar0Max1[keyName];
    List par0max2 = this.mPhiPar0Max2[keyName];
    List par0max3 = this.mPhiPar0Max3[keyName];

    List par1max0 = this.mPhiPar1Max0[keyName];
    List par1max1 = this.mPhiPar1Max1[keyName];
    List par1max2 = this.mPhiPar1Max2[keyName];
    List par1max3 = this.mPhiPar1Max3[keyName];

    if (side == 0 && param==0){
      fid_func_val = par0min0[sector] + par0min1[sector] * Math.exp(par0min2[sector] * (Mom - par0min3[sector]));
    } else if (side == 1 && param==0){
      fid_func_val = par0max0[sector] + par0max1[sector] * Math.exp(par0max2[sector] * (Mom - par0max3[sector]));
    } else if (side == 0 && param==1) {
      fid_func_val=par1min0[sector] + par1min1[sector] * Mom * Math.exp(par1min2[sector] * Math.pow((Mom - par1min3[sector]),2));
    } else if (side == 1 && param==1){
      fid_func_val = par1max0[sector] + par1max1[sector] * Mom * Math.exp(par1max2[sector] * Math.pow((Mom - par1max3[sector]),2));
    } else{
      System.out.println("eg2Cuts::clas6FidCuts::FidFunc - incorrect side = " + side + ", param = " + param);
    }
    return fid_func_val;
  }
}
