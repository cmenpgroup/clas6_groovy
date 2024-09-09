import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

OptionParser p = new OptionParser("eg2Proton_MR3zh_graph2txt.groovy");
String userTgt = "C";
p.addOption("-s", userTgt, "Solid Target (C, Fe, Pb)");
String userSigmaCut = "std";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (std, sigma_1_0, sigma_1_5, sigma_2_0, sigma_2_5, sigma_3_0)");
p.parse(args);
userTgt = p.getOption("-s").stringValue();
userSigmaCut = p.getOption("-c").stringValue();

HistInfo myHI = new HistInfo();

YieldsForMR3zh myMR = new YieldsForMR3zh();
List<String> Q2Cuts = myMR.getQ2Cuts();
List<String> nuCuts = myMR.getNuCuts();

String fileName = "MR3zh/jawHipo/eg2Proton_MR3zh_corr_hists_" + userTgt + "_" + userSigmaCut + ".hipo";
println fileName;
TDirectory dir = new TDirectory();
dir.readFile(fileName);

GraphErrors[][] gr_mrProton = new GraphErrors[Q2Cuts.size()][nuCuts.size()];
GraphErrors[][] gr_mrProtonCorr = new GraphErrors[Q2Cuts.size()][nuCuts.size()];
GraphErrors[][] grAcc = new GraphErrors[Q2Cuts.size()][nuCuts.size()];

nuCuts.eachWithIndex { nNu, iNu->
  Q2Cuts.eachWithIndex { nQ2, iQ2->
    String grcorr = "gr_mrProtonCorr_" + iQ2 + iNu;
    gr_mrProtonCorr[iQ2][iNu] = dir.getObject("MR3zh/",grcorr);
    gr_mrProtonCorr[iQ2][iNu].save(grcorr + "_" + userTgt + "_" + userSigmaCut + ".csv");

    String grUnCorr = "gr_mrProton_zh_" + iQ2 + iNu;
    gr_mrProtonCorr[iQ2][iNu] = dir.getObject("MR3zh/",grUnCorr);
    gr_mrProtonCorr[iQ2][iNu].save(grUnCorr + "_" + userTgt + "_" + userSigmaCut + ".csv");

    String grAccRatio = "gr_rat" + userTgt + "_" + iQ2 + iNu;
    grAcc[iQ2][iNu] = dir.getObject("MR3zh/",grAccRatio);
    grAcc[iQ2][iNu].save(grAccRatio + "_" + userTgt + "_" + userSigmaCut + ".csv");
  }
}
