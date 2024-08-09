import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*; // new import for ntuples

import eg2Cuts.eg2Target;
eg2Target myTarget = new eg2Target();  // create the eg2 target object

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;

double beamEnergy = myTarget.Get_Beam_Energy();
println "Beam " + beamEnergy + " GeV";
double W_DIS = myTarget.Get_W_DIS();
double Q2_DIS = myTarget.Get_Q2_DIS();
double YB_DIS = myTarget.Get_YB_DIS();

def cli = new CliBuilder(usage:'eg2Proton_AnaTree.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
cli.o(longOpt:'output', args:1, argName:'Histogram output file', type: String, 'Output file name')
cli.s(longOpt:'solid', args:1, argName:'Solid Target', type: String, 'Solid Target (C, Fe, Pb)')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def maxEvents = -1;
if(options.M) maxEvents = options.M;

def outFile = "eg2Proton_AnaTree_MR_Hists.hipo";
if(options.o) outFile = options.o;

String solidTgt = "C";
if(options.s) solidTgt = options.s;

def extraArguments = options.arguments()
if (extraArguments.isEmpty()){
  println "No input file!";
  cli.usage();
  return;
}

String fileName;
extraArguments.each { infile ->
  fileName = infile;
}

long st = System.currentTimeMillis(); // start time

TDirectory dir = new TDirectory();

//HipoReader reader = new HipoReader();
//reader.open(fileName);
TreeHipo tree = new TreeHipo(fileName,"protonTree::tree"); // the writer adds ::tree to the name of the tree
//tree.setReader(reader);
int entries = tree.getEntries();
System.out.println(" ENTRIES = " + entries);

List vecD2 = tree.getDataVectors("q2:nu:zh:pT2:zLC","pFidCut==1&&eFidCut==1&&iTgt==0",maxEvents);
tree.reset();
List vecSolid = tree.getDataVectors("q2:nu:zh:pT2:zLC","pFidCut==1&&eFidCut==1&&iTgt==1",maxEvents);
System.out.println(" LD2 = " + vecD2.get(0).getSize());
System.out.println(" Solid = " + vecSolid.get(0).getSize());

String[] DirLabel = ["/LD2","/Solid"];
String[] TgtLabel = ["D2","Nuc"];
String[] xLabel = ["Q^2 (GeV^2)","#nu (GeV)","z_h","pT^2 (GeV^2)","z "];
String[] Var = ["Qsq","nu","zh","pT2","zLC"];
int[] nbins = [50,50,30,30,30];
double[] xlo = [Q2_DIS,2.2,0.0,0.0,0.0];
double[] xhi = [4.1,4.2,1.0,2.0,1.0];
H1F[][] h1_nProton = new H1F[Var.size()][TgtLabel.size()];

TgtLabel.eachWithIndex {nTgt, iTgt->
  dir.mkdir(DirLabel[iTgt]);
  dir.cd(DirLabel[iTgt]);
  Var.eachWithIndex { nVar, iVar->
    String hname = "h1_nProton_" + nTgt + "_" + nVar;
    if(iTgt==0) h1_nProton[iVar][iTgt] = new H1F().create(hname,nbins[iVar],vecD2.get(iVar),xlo[iVar],xhi[iVar]);
    if(iTgt==1) h1_nProton[iVar][iTgt] = new H1F().create(hname,nbins[iVar],vecSolid.get(iVar),xlo[iVar],xhi[iVar]);
    h1_nProton[iVar][iTgt].setTitleX(xLabel[iVar]);
    h1_nProton[iVar][iTgt].setTitleY("Counts");
    h1_nProton[iVar][iTgt].setTitle("eg2 - " + nTgt);
    h1_nProton[iVar][iTgt].setFillColor(YELLOW - iTgt);
    dir.addDataSet(h1_nProton[iVar][iTgt]);
  }
}

String dirMR = '/multiplicity';
dir.mkdir(dirMR);
dir.cd(dirMR);

TCanvas[] can = new TCanvas[Var.size()];
int c_title_size = 24;

H1F[] h1_mrProton = new H1F[Var.size()];
GraphErrors[] gr_mrProton = new GraphErrors[Var.size()];
Var.eachWithIndex{nVar, iVar->
  String canName = "c" + iVar;
  can[iVar] = new TCanvas(canName,600,600);
  can[iVar].cd(0);
  can[iVar].getPad().setTitleFontSize(c_title_size);
  h1_mrProton[iVar] = H1F.divide(h1_nProton[iVar][1],h1_nProton[iVar][0]);
  h1_mrProton[iVar].setName("h1_mrProton_" + nVar);
  h1_mrProton[iVar].setFillColor(GREEN);
  gr_mrProton[iVar] = h1_mrProton[iVar].getGraph();
  gr_mrProton[iVar].setName("gr_mrProton_" + nVar);
  gr_mrProton[iVar].setTitle("eg2 - " + solidTgt + "/D2");
  gr_mrProton[iVar].setTitleX(xLabel[iVar]);
  gr_mrProton[iVar].setTitleY("R^p");
  gr_mrProton[iVar].setMarkerColor(3);
  gr_mrProton[iVar].setLineColor(3);
  gr_mrProton[iVar].setMarkerSize(3);
  can[iVar].draw(gr_mrProton[iVar],"line");
  dir.addDataSet(gr_mrProton[iVar]); // add to the histogram file
}

dir.writeFile(outFile); // write the histograms to the file

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen
