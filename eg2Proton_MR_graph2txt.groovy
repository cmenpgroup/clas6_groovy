import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
//---- imports for PDG library
import org.jlab.clas.pdg.PhysicsConstants;
import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.pdg.PDGParticle;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

//import org.jlab.jnp.pdg.PhysicsConstants;
//import org.jlab.jnp.pdg.PDGDatabase;
//import org.jlab.jnp.pdg.PDGParticle;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

OptionParser p = new OptionParser("eg2Proton_MR_graph2txt.groovy");
p.parse(args);

HistInfo myHI = new HistInfo();
List<String> Var = myHI.getVariables();
List<String> solidTgt = myHI.getSolidTgtLabel();

int c1_title_size = 22;
TCanvas[] can = new TCanvas[Var.size()];
TDirectory[] dir = new TDirectory[solidTgt.size()];
solidTgt.eachWithIndex { nTgt, iTgt ->
  String fileName = "MR1D/eg2Proton_MR_corr_hists_" + nTgt + "_std.hipo";
  println fileName;
  dir[iTgt] = new TDirectory();
  dir[iTgt].readFile(fileName);
}

GraphErrors[][] gr_mrProton = new GraphErrors[Var.size()][solidTgt.size()];
GraphErrors[][] gr_mrProtonCorr = new GraphErrors[Var.size()][solidTgt.size()];
GraphErrors[][] grAcc = new GraphErrors[Var.size()][solidTgt.size()];

Var.eachWithIndex { nVar, iVar->
  String grProton = "gr_mrProton_" + nVar;
  String grcorr = "gr_mrProtonCorr_" + nVar;
  String grAccRatio = "gr_acc_" + nVar;

  solidTgt.eachWithIndex { nTgt, iTgt ->
    gr_mrProton[iVar][iTgt] = dir[iTgt].getObject(nVar,grProton);
    gr_mrProton[iVar][iTgt].save(grProton + "_" + nTgt + ".csv");

    gr_mrProtonCorr[iVar][iTgt] = dir[iTgt].getObject(nVar,grcorr);
    gr_mrProtonCorr[iVar][iTgt].save(grcorr + "_" + nTgt + ".csv");

    grAcc[iVar][iTgt] = dir[iTgt].getObject(nVar,grAccRatio);
    grAcc[iVar][iTgt].save(grAccRatio + "_" + nTgt + ".csv");
  }
}
