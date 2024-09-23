import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

OptionParser p = new OptionParser("eg2Proton_MR3zh_acc_graph2txt.groovy");

String userSigmaCut = "std";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (std, sigma_1_0, sigma_1_5, sigma_2_0, sigma_2_5, sigma_3_0)");
p.parse(args);
userSigmaCut = p.getOption("-c").stringValue();

HistInfo myHI = new HistInfo();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<String> sigmaLabel = myHI.getSigmaLabel();
List<String> sigmaUnderscore = myHI.getSigmaUnderscore();

YieldsForMR3zh myMR = new YieldsForMR3zh();
List<String> Q2Cuts = myMR.getQ2Cuts();
List<String> nuCuts = myMR.getNuCuts();

solidTgt.add("D"); // add the D target to the List
println solidTgt;

String nSig;
if(userSigmaCut=="std"){
  nSig = "2.0";
}else{
  nSig = sigmaLabel[sigmaUnderscore.indexOf(userSigmaCut)];
}
println "Sigma cut: " + nSig + " from " + userSigmaCut;

String fileName = "acc_ratio_hists/MR3zh/acc_ratio_hists_MR3zh_allTgts_sig" + nSig + ".hipo";
TDirectory dir = new TDirectory();
dir.readFile(fileName);

String grName;
GraphErrors[][] grAcc = new GraphErrors[Q2Cuts.size()][nuCuts.size()];
GraphErrors[][] grRat = new GraphErrors[Q2Cuts.size()][nuCuts.size()];

Q2Cuts.eachWithIndex { nQ2, iQ2->
  nuCuts.eachWithIndex { nNu, iNu->
    solidTgt.eachWithIndex { nTgt, iTgt ->
      String dirname = "Qsq" + iQ2 + "_nu" + iNu;
      grName = "gr_acc" + nTgt + "_" + iQ2 + iNu;
      grAcc[iQ2][iNu] = dir.getObject(dirname,grName);
      grAcc[iQ2][iNu].save(grName + "_" + userSigmaCut + ".csv");

      if(nTgt!="D"){
        grName = "gr_rat" + nTgt + "_" + iQ2 + iNu;
        grRat[iQ2][iNu] = dir.getObject(dirname,grName);
        grRat[iQ2][iNu].save(grName + "_" + userSigmaCut + ".csv");
      }
    }
  }
}
