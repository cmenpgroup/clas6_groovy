import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

OptionParser p = new OptionParser("eg2Proton_MR_graph2txt.groovy");

String userTgt = "C";
p.addOption("-s", userTgt, "Solid Target (C, Fe, Pb)");
String userSigmaCut = "std";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (std, sigma_1_0, sigma_1_5, sigma_2_0, sigma_2_5, sigma_3_0)");
p.parse(args);
userTgt = p.getOption("-s").stringValue();
userSigmaCut = p.getOption("-c").stringValue();

HistInfo myHI = new HistInfo();
List<String> Var = myHI.getVariables();

String fileName = "MR1D/jawHipo/eg2Proton_MR_corr_hists_" + userTgt + "_" + userSigmaCut + ".hipo";
TDirectory dir = new TDirectory();
dir.readFile(fileName);

GraphErrors[] gr_mrProton = new GraphErrors[Var.size()];
GraphErrors[] gr_mrProtonCorr = new GraphErrors[Var.size()];
GraphErrors[] grAcc = new GraphErrors[Var.size()];

Var.eachWithIndex { nVar, iVar->
  String grProton = "gr_mrProton_" + nVar;
  gr_mrProton[iVar] = dir.getObject(nVar,grProton);
  gr_mrProton[iVar].save(grProton + "_" + userTgt + "_" + userSigmaCut + ".csv");

  String grcorr = "gr_mrProtonCorr_" + nVar;
  gr_mrProtonCorr[iVar] = dir.getObject(nVar,grcorr);
  gr_mrProtonCorr[iVar].save(grcorr + "_" + userTgt + "_" + userSigmaCut + ".csv");

  String grAccRatio = "gr_rat" + userTgt +"_" + nVar;
  grAcc[iVar] = dir.getObject(nVar,grAccRatio);
  grAcc[iVar].save(grAccRatio + "_" + userTgt + "_" + userSigmaCut + ".csv");
}
