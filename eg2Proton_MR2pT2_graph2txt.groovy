import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

OptionParser p = new OptionParser("eg2Proton_MR2pT2_graph2txt.groovy");
String userTgt = "C";
p.addOption("-s", userTgt, "Solid Target (C, Fe, Pb)");
String userSigmaCut = "std";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (std, sigma_1_0, sigma_1_5, sigma_2_0, sigma_2_5, sigma_3_0)");
p.parse(args);
userTgt = p.getOption("-s").stringValue();
userSigmaCut = p.getOption("-c").stringValue();

HistInfo myHI = new HistInfo();

YieldsForMR2pT2 myMR = new YieldsForMR2pT2();
List<String> zhCuts = myMR.getZhCuts();

String fileName = "MR2pT2/jawHipo/eg2Proton_MR2pT2_corr_hists_" + userTgt + "_" + userSigmaCut + ".hipo";
println fileName;
TDirectory dir = new TDirectory();
dir.readFile(fileName);

GraphErrors[] gr_mrProtonCorr = new GraphErrors[zhCuts.size()];
GraphErrors[] gr_mrProton = new GraphErrors[zhCuts.size()];
GraphErrors[] grAcc = new GraphErrors[zhCuts.size()];

zhCuts.eachWithIndex { nZh, iZh->
  String grcorr = "gr_mrProtonCorr_pT2_" + iZh;
  gr_mrProtonCorr[iZh] = dir.getObject("MR2pT2/",grcorr);
  gr_mrProtonCorr[iZh].save(grcorr + "_" + userTgt + "_" + userSigmaCut + ".csv");

  String grUnCorr = "gr_mrProton_pT2_" + iZh;
  gr_mrProton[iZh] = dir.getObject("MR2pT2/",grUnCorr);
  gr_mrProton[iZh].save(grUnCorr + "_" + userTgt + "_" + userSigmaCut + ".csv");

  String grAccRatio = "gr_rat" + userTgt + "_" + iZh;
  grAcc[iZh] = dir.getObject("MR2pT2/",grAccRatio);
  grAcc[iZh].save(grAccRatio + "_pT2_" + userTgt + "_" + userSigmaCut + ".csv");
}
