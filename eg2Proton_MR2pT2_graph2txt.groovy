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
p.parse(args);

HistInfo myHI = new HistInfo();
List<String> solidTgt = myHI.getSolidTgtLabel();

YieldsForMR2pT2 myMR = new YieldsForMR2pT2();
List<String> zhCuts = myMR.getZhCuts();

TDirectory[] dir = new TDirectory[solidTgt.size()];
solidTgt.eachWithIndex { nTgt, iTgt ->
  String fileName = "MR2D/eg2Proton_MR2pT2_corr_hists_" + nTgt + "_std.hipo";
  println fileName;
  dir[iTgt] = new TDirectory();
  dir[iTgt].readFile(fileName);
}

GraphErrors[][] gr_mrProtonCorr = new GraphErrors[zhCuts.size()][solidTgt.size()];
GraphErrors[][] grAcc = new GraphErrors[zhCuts.size()][solidTgt.size()];

zhCuts.eachWithIndex { nZh, iZh->
  String grcorr = "gr_mrProtonCorr_pT2_" + iZh;
  String grAccRatio = "gr_acc_" + iZh;
  solidTgt.eachWithIndex { nTgt, iTgt ->
    gr_mrProtonCorr[iZh][iTgt] = dir[iTgt].getObject("MR2pT2/",grcorr);
    gr_mrProtonCorr[iZh][iTgt].save(grcorr + "_" + nTgt + ".txt");

    grAcc[iZh][iTgt] = dir[iTgt].getObject("MR2pT2/",grAccRatio);
    grAcc[iZh][iTgt].save(grAccRatio + "_pT2_" + nTgt + ".txt");
  }
}
