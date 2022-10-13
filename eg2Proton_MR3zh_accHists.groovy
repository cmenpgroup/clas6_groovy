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

OptionParser p = new OptionParser("eg2Proton_MR3zh_accHists.groovy");

String outFile = "acc_ratio_hists_MR3zh.hipo";
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

YieldsForMR3zh myMR = new YieldsForMR3zh();
List<String> Q2Cuts = myMR.getQ2Cuts();
List<String> nuCuts = myMR.getNuCuts();

myMR.setBinning(iBinning);
List<Double> xlo = myMR.getXlo();
List<Double> xhi = myMR.getXhi();
List<Double> nbins = myMR.getNbins();
String xLabel = myMR.getXlabel();

String[] accType = ["gen","rec","acc"];
String[] yLabel = ["Counts","Counts","Acceptance"];

solidTgt.add("D"); // add the D target to the List
println solidTgt;

TDirectory dir = new TDirectory();

H1F[][][] h1_acc = new H1F[Q2Cuts.size()*nuCuts.size()][accType.size()][solidTgt.size()];
GraphErrors[][] gr_acc = new GraphErrors[Q2Cuts.size()*nuCuts.size()][solidTgt.size()];
GraphErrors[][] gr_rat = new GraphErrors[Q2Cuts.size()*nuCuts.size()][solidTgt.size()];

int hNum;

Q2Cuts.eachWithIndex { nQ2, iQ2->
  nuCuts.eachWithIndex { nNu, iNu->
    hNum = iQ2*Q2Cuts.size() + iNu;
    println "hNum " + hNum + " " + iQ2 + " " + iNu;
    String dirname = "Qsq" + iQ2 + "_nu" + iNu;
    dir.cd();
    dir.mkdir(dirname);
    dir.cd(dirname);
    canCount = 0;
    solidTgt.eachWithIndex {nTgt, iTgt->
      accType.eachWithIndex {nAcc, iAcc->
        String hname = nAcc + nTgt + "_MR3zh_" +  iQ2 + iNu;
        String htitle = "eg2 - " + nTgt + " - " + nAcc;

        if(nAcc!="acc"){
          h1_acc[hNum][iAcc][iTgt] = new H1F(hname,xLabel,yLabel[iAcc],nbins[iQ2][iNu],xlo[iQ2][iNu],xhi[iQ2][iNu]);
          h1_acc[hNum][iAcc][iTgt].setTitle(htitle);
          String path = pathName + "/" + nTgt + "/3d/sig" + userSigmaCut + "/";
          String accFile = path + "pruned" + nTgt + "_MR3zh_" +  iQ2 + iNu + "." + nAcc;
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
            h1_acc[hNum][iAcc][iTgt].fill(x,y);
          }
        }else{
          h1_acc[hNum][iAcc][iTgt] = h1_acc[hNum][1][iTgt].histClone(hname);
          h1_acc[hNum][iAcc][iTgt].divide(h1_acc[hNum][0][iTgt]);
          h1_acc[hNum][iAcc][iTgt].setTitle(htitle);
          h1_acc[hNum][iAcc][iTgt].setTitleX(xLabel);
          h1_acc[hNum][iAcc][iTgt].setTitleY(yLabel[iAcc]);

          gr_acc[hNum][iTgt] = h1_acc[hNum][iAcc][iTgt].getGraph();
          gr_acc[hNum][iTgt].setName("gr_" + nAcc + nTgt + "_" +  iQ2 + iNu);
          gr_acc[hNum][iTgt].setTitle(htitle);
          gr_acc[hNum][iTgt].setTitleX(xLabel);
          gr_acc[hNum][iTgt].setTitleY(yLabel[iAcc]);
          dir.addDataSet(gr_acc[hNum][iTgt]); // add graph to the file
        }
        dir.addDataSet(h1_acc[hNum][iAcc][iTgt]); // add histogram to the file
      }
    }
    solidTgt.eachWithIndex {nTgt, iTgt->
      gr_rat[hNum][iTgt] = new GraphErrors();
      gr_rat[hNum][iTgt] = gr_acc[hNum][iTgt].divide(gr_acc[hNum][3]);
      gr_rat[hNum][iTgt].setName("gr_rat" + nTgt + "_" +  iQ2 + iNu);
      gr_rat[hNum][iTgt].setTitle("eg2 - " + nTgt + "/D");
      gr_rat[hNum][iTgt].setTitleX(xLabel);
      gr_rat[hNum][iTgt].setTitleY("Ratio");
      dir.addDataSet(gr_rat[hNum][iTgt]); // add ratio graph to the file
    }
  }
}

if(bGraph){
  int canCount = 0;
  int c1_title_size = 22;
  TCanvas[][] can = new TCanvas[Q2Cuts.size()][nuCuts.size()];

  Q2Cuts.eachWithIndex { nQ2, iQ2->
    nuCuts.eachWithIndex { nNu, iNu->
      hNum = iQ2*Q2Cuts.size() + iNu;
      String cname = "c" + iQ2 + iNu;
      can[iQ2][iNu] = new TCanvas(cname,1200,900);
      can[iQ2][iNu].divide(4,4);
      canCount = 0;
      solidTgt.eachWithIndex {nTgt, iTgt->
        accType.eachWithIndex {nAcc, iAcc->
          can[iQ2][iNu].cd(canCount);
          can[iQ2][iNu].getPad().setTitleFontSize(c1_title_size);
          if(nAcc!="acc"){
            can[iQ2][iNu].draw(h1_acc[hNum][iAcc][iTgt]);
          }else{
            can[iQ2][iNu].draw(gr_acc[hNum][iTgt]);
          }
          canCount++;
        }
        can[iQ2][iNu].cd(canCount);
        can[iQ2][iNu].getPad().setTitleFontSize(c1_title_size);
        can[iQ2][iNu].draw(gr_rat[hNum][iTgt]);
        canCount++;
      }
    }
  }
}

dir.writeFile(outFile);
