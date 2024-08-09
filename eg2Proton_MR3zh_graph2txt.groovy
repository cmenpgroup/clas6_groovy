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
p.parse(args);

HistInfo myHI = new HistInfo();
List<String> solidTgt = myHI.getSolidTgtLabel();

YieldsForMR3zh myMR = new YieldsForMR3zh();
List<String> Q2Cuts = myMR.getQ2Cuts();
List<String> nuCuts = myMR.getNuCuts();

TDirectory[] dir = new TDirectory[solidTgt.size()];
solidTgt.eachWithIndex { nTgt, iTgt ->
  String fileName = "eg2Proton_MR3zh_corr_hists_" + nTgt + ".hipo";
  println fileName;
  dir[iTgt] = new TDirectory();
  dir[iTgt].readFile(fileName);
}

GraphErrors[][][] gr_mrProtonCorr = new GraphErrors[Q2Cuts.size()][nuCuts.size()][solidTgt.size()];
GraphErrors[][][] grAcc = new GraphErrors[Q2Cuts.size()][nuCuts.size()][solidTgt.size()];

nuCuts.eachWithIndex { nNu, iNu->
  Q2Cuts.eachWithIndex { nQ2, iQ2->
    String grcorr = "gr_mrProtonCorr_" + iQ2 + iNu;
    String grAccRatio = "gr_acc_" + iQ2 + iNu;

    solidTgt.eachWithIndex { nTgt, iTgt ->
      gr_mrProtonCorr[iQ2][iNu][iTgt] = dir[iTgt].getObject("MR3zh/",grcorr);
      gr_mrProtonCorr[iQ2][iNu][iTgt].save(grcorr + "_" + nTgt + ".txt");

      grAcc[iQ2][iNu][iTgt] = dir[iTgt].getObject("MR3zh/",grAccRatio);
      grAcc[iQ2][iNu][iTgt].save(grAccRatio + "_" + nTgt + ".txt");
    }
  }
}
