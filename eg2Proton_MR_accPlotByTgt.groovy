//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.base.GStyle;
//---- imports for OPTIONPARSER library
import org.jlab.jnp.utils.options.OptionParser;

import eg2AnaTree.*;

GStyle.getAxisAttributesX().setTitleFontSize(24);
GStyle.getAxisAttributesY().setTitleFontSize(24);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;
int[] colors = [YELLOW,BLUE,GREEN];

OptionParser p = new OptionParser("eg2Proton_MR_accPlotByTgt.groovy");

String userSigmaCut = "2.0";
p.addOption("-c", userSigmaCut, "Proton ID Cut sigma (1.0, 1.5, 2.0, 2.5, 3.0)");
int allVars = 0;
p.addOption("-z",Integer.toString(allVars), "Variables (0=short list- q2:nu:zh:pT2:zLC:zLC:phiPQ, 1=full list)");

p.parse(args);
userSigmaCut = p.getOption("-c").stringValue();
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
List<String> xLabel = myHI.getXlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<String> sigmaLabel = myHI.getSigmaLabel();

String[] accType = ["gen","rec","acc"];
String[] yLabel = ["Counts","Counts","Acceptance"];
String[] accTitle = ["Generated","Reconstructed","Accepted"];

solidTgt.add(0,"D"); // add the D target to the List
println solidTgt;

H1F[][][] h1_acc = new H1F[Var.size()][accType.size()][solidTgt.size()];
GraphErrors[][][] gr_rat = new GraphErrors[Var.size()][accType.size()][solidTgt.size()];

// create a TDirectory objects and read in the histogram file
TDirectory dir = new TDirectory();
dir.readFile(pathName + "/acc_ratio_hists_MR_allTgts_sig" + userSigmaCut+ ".hipo");

int c1_title_size = 22;
TCanvas[][] can = new TCanvas[Var.size()][accType.size()];

Var.eachWithIndex { nVar, iVar->
  accType.eachWithIndex {nAcc, iAcc->
    String cname = "can_" + nVar + "_" + nAcc;
    can[iVar][iAcc] = new TCanvas(cname,900,900);
    can[iVar][iAcc].divide(2,2);
    solidTgt.eachWithIndex {nTgt, iTgt->
      String hname = nAcc + nTgt + "_" + nVar;
      String htitle = "eg2 - " + nTgt + " - " + accTitle[iAcc];
      can[iVar][iAcc].cd(iTgt);
      h1_acc[iVar][iAcc][iTgt] = dir.getObject(nVar,hname);
      h1_acc[iVar][iAcc][iTgt].setFillColor(colors[iAcc]);
      h1_acc[iVar][iAcc][iTgt].setTitle(htitle);
      if(nVar=="P") can[iVar][iAcc].getPad().getAxisX().setRange(0.0,3.5);
      can[iVar][iAcc].getPad().setTitleFontSize(c1_title_size);
      can[iVar][iAcc].draw(h1_acc[iVar][iAcc][iTgt]);
    }
  }
}
