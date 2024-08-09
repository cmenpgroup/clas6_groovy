import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*; // new import for ntuples

def cli = new CliBuilder(usage:'eg2Proton_AnaTree.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
cli.o(longOpt:'output', args:1, argName:'Histogram output file', type: String, 'Output file name')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def maxEvents = -1;
if(options.M) maxEvents = options.M;

def outFile = "eg2Proton_AnaTree_Hists.hipo";
if(options.o) outFile = options.o;

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

//HipoReader reader = new HipoReader();
//reader.open(fileName);
TreeHipo tree = new TreeHipo(fileName,"protonTree::tree"); // the writer adds ::tree to the name of the tree
//tree.setReader(reader);
int entries = tree.getEntries();
System.out.println(" ENTRIES = " + entries);
List vec = tree.getDataVectors("q2:W:nu:yb","",maxEvents);

// Create the histograms
H1F hQ2 = new H1F().create("hQ2",100,vec.get(0),0.0,5.0);
hQ2.setTitleX("Q^2 (GeV^2)");
hQ2.setTitleY("Counts");
H1F hW = new H1F().create("hW",100,vec.get(1),0.0,3.0);
hW.setTitleX("W (GeV)");
hW.setTitleY("Counts");
H1F hNu = new H1F().create("hNu",100,vec.get(2),2.0,4.5);
hNu.setTitleX("#nu (GeV)");
hNu.setTitleY("Counts");
H1F hYb = new H1F().create("hYb",100,vec.get(3),0.4,1.0);
hYb.setTitleX("Y_b");
hYb.setTitleY("Counts");

def dirname = "/electron";
TDirectory dir = new TDirectory();
dir.mkdir(dirname);
dir.cd(dirname);

// create the canvas for the display
int c1a_title_size = 24;
TCanvas c1 = new TCanvas("c1",800,800);
c1.divide(2,2);
c1.cd(0);
c1.draw(hQ2);
dir.addDataSet(hQ2);
c1.cd(1);
c1.draw(hW);
dir.addDataSet(hW);
c1.cd(2);
c1.draw(hNu);
dir.addDataSet(hNu);
c1.cd(3);
c1.draw(hYb);
dir.addDataSet(hYb);

dir.writeFile(outFile); // write the histograms to the file

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen
