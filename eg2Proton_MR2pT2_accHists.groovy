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

OptionParser p = new OptionParser("eg2Proton_MR2pT2_accHists.groovy");

String outFile = "acc_ratio_hists_MR2pT2.hipo";
p.addOption("-o", outFile, "Output file name");
int iBinning = 1;
p.addOption("-b", Integer.toString(iBinning), "Set the binning of the histograms");
String userSigmaCut = "2.0";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (1.0, 1.5, 2.0, 2.5, 3.0)");
int bGraph = 1;
p.addOption("-g", Integer.toString(bGraph), "Graph monitor histograms. (0=quiet)");

p.parse(args);
outFile = p.getOption("-o").stringValue();
iBinning = p.getOption("-b").intValue();
userSigmaCut = p.getOption("-c").stringValue();
bGraph = p.getOption("-g").intValue();

String pathName;
if(p.getInputList().size()==1){
    pathName = p.getInputList().get(0);
}else{
    System.out.println("*** Wrong number of inputs.  Only one input for the path to the text files. ***")
    p.printUsage();
    System.exit(0);
}

HistInfo myHI = new HistInfo();
List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();

YieldsForMR2pT2 myMR = new YieldsForMR2pT2();
myMR.setBinning(iBinning);
List<String> zhCuts = myMR.getZhCuts();
List<Double> xlo = myMR.getXlo();
List<Double> xhi = myMR.getXhi();
List<Double> nbins = myMR.getNbins();
String xLabel = myMR.getXlabel();

String[] accType = ["gen","rec","acc"];
String[] yLabel = ["Counts","Counts","Acceptance"];

solidTgt.add("D"); // add the D target to the List
println solidTgt;

TDirectory dir = new TDirectory();

H1F[][][] h1_acc = new H1F[zhCuts.size()][accType.size()][solidTgt.size()];
GraphErrors[][] gr_acc = new GraphErrors[zhCuts.size()][solidTgt.size()];
GraphErrors[][] gr_rat = new GraphErrors[zhCuts.size()][solidTgt.size()];

zhCuts.eachWithIndex { nZh, iZh->
  dir.cd();
  dir.mkdir(nZh);
  dir.cd(nZh);
  solidTgt.eachWithIndex {nTgt, iTgt->
    accType.eachWithIndex {nAcc, iAcc->
      String hname = nAcc + nTgt + "_MR2pT2_" + iZh;
      String htitle = "eg2 - " + nTgt + " - " + nAcc;

      if(nAcc!="acc"){
        h1_acc[iZh][iAcc][iTgt] = new H1F(hname,xLabel,yLabel[iAcc],nbins[iZh],xlo[iZh],xhi[iZh]);
        h1_acc[iZh][iAcc][iTgt].setTitle(htitle);
        String path = pathName + "/" + nTgt + "/2d/sig" + userSigmaCut + "/";
        String accFile = path + "pruned" + nTgt + "_MR2pT2_" + iZh + "." + nAcc;
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
          h1_acc[iZh][iAcc][iTgt].fill(x,y);
        }
      }else{
        h1_acc[iZh][iAcc][iTgt] = h1_acc[iZh][1][iTgt].histClone(hname);
        h1_acc[iZh][iAcc][iTgt].divide(h1_acc[iZh][0][iTgt]);
        h1_acc[iZh][iAcc][iTgt].setTitle(htitle);
        h1_acc[iZh][iAcc][iTgt].setTitleX(xLabel);
        h1_acc[iZh][iAcc][iTgt].setTitleY(yLabel[iAcc]);

        gr_acc[iZh][iTgt] = h1_acc[iZh][iAcc][iTgt].getGraph();
        gr_acc[iZh][iTgt].setName("gr_" + nAcc + nTgt + "_" + nZh);
        gr_acc[iZh][iTgt].setTitle(htitle);
        gr_acc[iZh][iTgt].setTitleX(xLabel);
        gr_acc[iZh][iTgt].setTitleY(yLabel[iAcc]);
        dir.addDataSet(gr_acc[iZh][iTgt]); // add graph to the file
      }
      dir.addDataSet(h1_acc[iZh][iAcc][iTgt]); // add histogram to the file
    }
  }
  solidTgt.eachWithIndex {nTgt, iTgt->
    gr_rat[iZh][iTgt] = new GraphErrors();
    gr_rat[iZh][iTgt] = gr_acc[iZh][iTgt].divide(gr_acc[iZh][3]);
    gr_rat[iZh][iTgt].setName("gr_rat" + nTgt + "_" + iZh);
    gr_rat[iZh][iTgt].setTitle("eg2 - " + nTgt + "/D");
    gr_rat[iZh][iTgt].setTitleX(xLabel);
    gr_rat[iZh][iTgt].setTitleY("Ratio");
    dir.addDataSet(gr_rat[iZh][iTgt]); // add ratio graph to the file
  }
}

if(bGraph){
  int canCount = 0;
  int c1_title_size = 22;
  TCanvas[] can = new TCanvas[zhCuts.size()];

  zhCuts.eachWithIndex { nZh, iZh->
    String cname = "can" + iZh;
    can[iZh] = new TCanvas(cname,1200,900);
    can[iZh].divide(4,4);
    canCount = 0;
    solidTgt.eachWithIndex {nTgt, iTgt->
      accType.eachWithIndex {nAcc, iAcc->
        can[iZh].cd(canCount);
        can[iZh].getPad().setTitleFontSize(c1_title_size);
        if(nAcc!="acc"){
          can[iZh].draw(h1_acc[iZh][iAcc][iTgt]);
        }else{
          can[iZh].draw(gr_acc[iZh][iTgt]);
        }
        canCount++;
      }
      can[iZh].cd(canCount);
      can[iZh].getPad().setTitleFontSize(c1_title_size);
      can[iZh].draw(gr_rat[iZh][iTgt]);
      canCount++;            
    }
  }
}

dir.writeFile(outFile);
