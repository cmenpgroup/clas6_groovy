import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.base.GStyle
//---- imports for PHYSICS library
import org.jlab.clas.physics.*;
//---- imports for PDG library
import org.jlab.clas.pdg.PhysicsConstants;
import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.pdg.PDGParticle;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

double x, y, err;
String[] str;

OptionParser p = new OptionParser("eg2Proton_MRaccHists.groovy");

String outFile = "acc_ratio_hists_MR_allTgt.hipo";
p.addOption("-o", outFile, "Output file name");
String userTgt = "C";
p.addOption("-s", "All Targets", "Solid Target (C, Fe, Pb)");
String userSigmaCut = "2.0";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (1.0, 1.5, 2.0, 2.5, 3.0)");
int bGraph = 1;
p.addOption("-g",Integer.toString(bGraph), "Graph monitor histograms. (0=quiet)");
int allVars = 0;
p.addOption("-z",Integer.toString(allVars), "Variables (0=short list- q2:nu:zh:pT2:zLC:zLC:phiPQ, 1=full list)");

p.parse(args);
outFile = p.getOption("-o").stringValue();
userTgt = p.getOption("-s").stringValue();
userSigmaCut = p.getOption("-c").stringValue();
bGraph = p.getOption("-g").intValue();
allVars = p.getOption("-z").intValue();

String pathName;
if(p.getInputList().size()==1){
    pathName = p.getInputList().get(0);
}else{
    System.out.println("*** Wrong number of inputs.  Only one input for the path to the text files. ***")
    p.printUsage();
    System.exit(0);
}

HistInfo myHI = new HistInfo();
if(allVars) myHI.createFullList();
List<String> Var = myHI.getVariables();
List<String> VarClasTool = myHI.getVariablesClasTool();
List<String> xLabel = myHI.getXlabel();
List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<Double> xlo = myHI.getXlo();
List<Double> xhi = myHI.getXhi();
List<Integer> nbins = myHI.getNbins();
if(allVars){
  nbins.addAll(myHI.getNbins_kine());
  xlo.addAll(myHI.getXlo_kine());
  xhi.addAll(myHI.getXhi_kine());
}
println Var;
println nbins;
String[] accType = ["gen","rec","acc"];
String[] yLabel = ["Counts","Counts","Acceptance"];

solidTgt.add("D"); // add the D target to the List
println solidTgt;

TDirectory dir = new TDirectory();

H1F[][][] h1_acc = new H1F[Var.size()][accType.size()][solidTgt.size()];
GraphErrors[][] gr_acc = new GraphErrors[Var.size()][solidTgt.size()];
GraphErrors[][] gr_rat = new GraphErrors[Var.size()][solidTgt.size()];

Var.eachWithIndex { nVar, iVar->
  dir.cd();
  dir.mkdir(nVar);
  dir.cd(nVar);
  solidTgt.eachWithIndex {nTgt, iTgt->
    accType.eachWithIndex {nAcc, iAcc->
      String hname = nAcc + nTgt + "_" + nVar;
      String htitle = "eg2 - " + nTgt + " - " + nAcc;

      if(nAcc!="acc"){
        h1_acc[iVar][iAcc][iTgt] = new H1F(hname,xLabel[iVar],yLabel[iAcc],nbins[iVar],xlo[iVar],xhi[iVar]);
        h1_acc[iVar][iAcc][iTgt].setTitle(htitle);
        String path = pathName + "/" + nTgt + "/1d/sig" + userSigmaCut + "/";
        String accFile = path + "pruned" + nTgt + "_" + VarClasTool[iVar] + "." + nAcc;
        println "Analyzing " + accFile;
        new File(accFile).eachLine { line ->
          str = line.split('\t');
          str.eachWithIndex{ val, ival->
            switch(ival){
              case 0: x = Double.parseDouble(val); break;
              case 1: y = Double.parseDouble(val); break;
              case 2: err = Double.parseDouble(val); break;
              default: break;
            }
          }
          if(nVar=="PhiLab" && x>180.0) x -= 360.0;
          h1_acc[iVar][iAcc][iTgt].fill(x,y);
        }
      }else{
        h1_acc[iVar][iAcc][iTgt] = h1_acc[iVar][1][iTgt].histClone(hname);
        h1_acc[iVar][iAcc][iTgt].divide(h1_acc[iVar][0][iTgt]);
        h1_acc[iVar][iAcc][iTgt].setTitle(htitle);
        h1_acc[iVar][iAcc][iTgt].setTitleX(xLabel[iVar]);
        h1_acc[iVar][iAcc][iTgt].setTitleY(yLabel[iAcc]);

        gr_acc[iVar][iTgt] = h1_acc[iVar][iAcc][iTgt].getGraph();
        gr_acc[iVar][iTgt].setName("gr_" + nAcc + nTgt + "_" + nVar);
        gr_acc[iVar][iTgt].setTitle(htitle);
        gr_acc[iVar][iTgt].setTitleX(xLabel[iVar]);
        gr_acc[iVar][iTgt].setTitleY(yLabel[iAcc]);
        dir.addDataSet(gr_acc[iVar][iTgt]); // add graph to the file
      }
      dir.addDataSet(h1_acc[iVar][iAcc][iTgt]); // add histogram to the file
    }
  }
  solidTgt.eachWithIndex {nTgt, iTgt->
        gr_rat[iVar][iTgt] = new GraphErrors();
        gr_rat[iVar][iTgt] = gr_acc[iVar][iTgt].divide(gr_acc[iVar][3]);
        gr_rat[iVar][iTgt].setName("gr_rat" + nTgt + "_" + nVar);
        gr_rat[iVar][iTgt].setTitle("eg2 - " + nTgt + "/D");
        gr_rat[iVar][iTgt].setTitleX(xLabel[iVar]);
        gr_rat[iVar][iTgt].setTitleY("Ratio");
        dir.addDataSet(gr_rat[iVar][iTgt]); // add ratio graph to the file
  }
}

if(bGraph){
  int canCount = 0;
  int c1_title_size = 22;
  TCanvas[] can = new TCanvas[Var.size()];

  Var.eachWithIndex { nVar, iVar->
    canCount = 0;
    String cname = "can" + iVar;
    can[iVar] = new TCanvas(cname,1200,900);
    can[iVar].divide(4,4);
    solidTgt.eachWithIndex {nTgt, iTgt->
      accType.eachWithIndex {nAcc, iAcc->
        can[iVar].cd(canCount);
        can[iVar].getPad().setTitleFontSize(c1_title_size);
        if(nAcc!="acc"){
          can[iVar].draw(h1_acc[iVar][iAcc][iTgt]);
        }else{
          can[iVar].draw(gr_acc[iVar][iTgt]);
        }
        canCount++;
      }
      can[iVar].cd(canCount);
      can[iVar].getPad().setTitleFontSize(c1_title_size);
      can[iVar].draw(gr_rat[iVar][iTgt]);
      canCount++;
    }
  }
}

dir.writeFile(outFile);
