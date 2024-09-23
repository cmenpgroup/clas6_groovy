import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

OptionParser p = new OptionParser("eg2Proton_MR2pT2_acc_graph2txt.groovy");

String userSigmaCut = "std";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (std, sigma_1_0, sigma_1_5, sigma_2_0, sigma_2_5, sigma_3_0)");
p.parse(args);
userSigmaCut = p.getOption("-c").stringValue();

HistInfo myHI = new HistInfo();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<String> sigmaLabel = myHI.getSigmaLabel();
List<String> sigmaUnderscore = myHI.getSigmaUnderscore();

YieldsForMR2pT2 myMR = new YieldsForMR2pT2();
List<String> zhCuts = myMR.getZhCuts();

solidTgt.add("D"); // add the D target to the List
println solidTgt;

String nSig;
if(userSigmaCut=="std"){
  nSig = "2.0";
}else{
  nSig = sigmaLabel[sigmaUnderscore.indexOf(userSigmaCut)];
}
println "Sigma cut: " + nSig + " from " + userSigmaCut;

String fileName = "acc_ratio_hists/MR2pT2/acc_ratio_hists_MR2pT2_allTgts_sig" + nSig + ".hipo";
TDirectory dir = new TDirectory();
dir.readFile(fileName);

String grName;
GraphErrors[] grAcc = new GraphErrors[zhCuts.size()];
GraphErrors[] grRat = new GraphErrors[zhCuts.size()];

zhCuts.eachWithIndex { nZh, iZh->
  solidTgt.eachWithIndex { nTgt, iTgt ->
    grName = "gr_acc" + nTgt + "_" + iZh;
    grAcc[iZh] = dir.getObject(nZh,grName);
    grAcc[iZh].save(grName + "_" + userSigmaCut + ".csv");

    if(nTgt!="D"){
      grName = "gr_rat" + nTgt + "_" + iZh;
      grRat[iZh] = dir.getObject(nZh,grName);
      grRat[iZh].save(grName + "_" + userSigmaCut + ".csv");
    }
  }
}
