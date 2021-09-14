import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;
import org.jlab.jnp.pdg.PDGDatabase;
import org.jlab.jnp.pdg.PDGParticle;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

double x, y, err;
String[] str;

def cli = new CliBuilder(usage:'eg2Proton_AnaTree.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.o(longOpt:'output', args:1, argName:'Histogram output file', type: String, 'Output file name')
cli.s(longOpt:'solid', args:1, argName:'Solid Target', type: String, 'Solid Target (C, Fe, Pb)')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def outFile = "acc_ratio_hists.hipo";
if(options.o) outFile = options.o;

String userTgt = "C";
if(options.s) userTgt = options.s;

def extraArguments = options.arguments()
if (extraArguments.isEmpty()){
  println "No input path!";
  cli.usage();
  return;
}

String pathName;
extraArguments.each { input ->
  pathName = input;
}

HistInfo myHI = new HistInfo();
List<String> Var = myHI.getVariables();
List<String> xLabel = myHI.getXlabel();
List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();
List<Double> xlo = myHI.getXlo();
List<Double> xhi = myHI.getXhi();
List<Integer> nbins = myHI.getNbins();

int indexTgt = solidTgt.indexOf(userTgt);
if(indexTgt==-1){
  println "Target " + userTgt + " is unavailable! Please choose one of the following:";
  println solidTgt;
  return;
}

TDirectory dir = new TDirectory();
int c1_title_size = 22;
TCanvas[] can = new TCanvas[Var.size()];

H1F[] h1_acc = new H1F[Var.size()];

Var.eachWithIndex { nVar, iVar->
  if(nVar!="phiPQ"){
    dir.mkdir(nVar);
    String hname = "acc_ratio_" + solidTgt[indexTgt] + "_" + nVar;
    h1_acc[iVar] = new H1F(hname,xLabel[iVar],"Ratio",nbins[iVar],xlo[iVar],xhi[iVar]);
    h1_acc[iVar].setTitle("eg2 - Acceptance ratio");

    String path = pathName + "/MR" + nVar + "/";
    String accFile = path + "acc_ratio_hists_" + nVar + "_" + solidTgt[indexTgt] + ".txt";
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
      if(y>0){
        h1_acc[iVar].fill(x,1.0/y);
      }else{
        h1_acc[iVar].fill(x,0.0);
      }
    }
    String cname = "can" + iVar;
    can[iVar] = new TCanvas(cname,600,600);
    can[iVar].cd(0);
    can[iVar].getPad().setTitleFontSize(c1_title_size);
    can[iVar].draw(h1_acc[iVar]);
    dir.addDataSet(h1_acc[iVar]); // add to the histogram file
  }
}

dir.writeFile(outFile);
