import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

OptionParser p = new OptionParser("eg2Proton_MR_acc_graph2txt.groovy");

String userSigmaCut = "std";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (std, sigma_1_0, sigma_1_5, sigma_2_0, sigma_2_5, sigma_3_0)");
p.parse(args);
userSigmaCut = p.getOption("-c").stringValue();

HistInfo myHI = new HistInfo();
List<String> Var = myHI.getVariables();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<String> sigmaLabel = myHI.getSigmaLabel();
List<String> sigmaUnderscore = myHI.getSigmaUnderscore();

solidTgt.add("D"); // add the D target to the List
println solidTgt;

String nSig;
if(userSigmaCut=="std"){
  nSig = "2.0";
}else{
  nSig = sigmaLabel[sigmaUnderscore.indexOf(userSigmaCut)];
}
println "Sigma cut: " + nSig + " from " + userSigmaCut;

String fileName = "acc_ratio_hists/MR/acc_ratio_hists_MR_allTgts_sig" + nSig + ".hipo";
TDirectory dir = new TDirectory();
dir.readFile(fileName);

String grName;
GraphErrors[] grAcc = new GraphErrors[Var.size()];
GraphErrors[] grRat = new GraphErrors[Var.size()];

Var.eachWithIndex { nVar, iVar->
  solidTgt.eachWithIndex { nTgt, iTgt ->
    grName = "gr_acc" + nTgt + "_" + nVar;
    grAcc[iVar] = dir.getObject(nVar,grName);
    grAcc[iVar].save(grName + "_" + userSigmaCut + ".csv");

    if(nTgt!="D"){
      grName = "gr_rat" + nTgt + "_" + nVar;
      grRat[iVar] = dir.getObject(nVar,grName);
      grRat[iVar].save(grName + "_" + userSigmaCut + ".csv");
    }
  }
}
