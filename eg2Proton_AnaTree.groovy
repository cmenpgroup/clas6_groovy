import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.clas.physics.*;
import org.jlab.clas.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.tree.*; // new import for ntuples

import org.jlab.jnp.utils.options.OptionParser;

GStyle.getAxisAttributesX().setTitleFontSize(24);
GStyle.getAxisAttributesY().setTitleFontSize(24);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

OptionParser p = new OptionParser("eg2Proton_AnaTree.groovy");

p.addOption("-M", "-1", "Max. Events");
p.addOption("-o", "eg2Proton_AnaTree_Hists.hipo", "output file name");

p.parse(args);
int maxEvents = p.getOption("-M").intValue();
String outFile = p.getOption("-o").stringValue();

String inFile = "ntuple_C_Npos.hipo"; // name of the input file with the data tree
if(p.getInputList().size()==1){
    inFile = p.getInputList().get(0);
}else{
    System.out.println("*** Wrong number of input files.  Only one input file required. ***");
    p.printUsage();
    System.exit(0);
}

long st = System.currentTimeMillis(); // start time

String hTitle = "Experiment: eg2";

TreeHipo tree = new TreeHipo(inFile,"protonTree::tree"); // the writer adds ::tree to the name of the tree
int entries = tree.getEntries();   // get the number of events in the file
System.out.println(" ENTRIES = " + entries);  // print the number of events in the file to the screen

// to analyze the entire file, set max Events less than zero
if(maxEvents < 0){
  maxEvents = entries;
}

// Select the target in the cuts
// iTgt = 0 (deuterium)
// iTgt = 1 (solid - C, Fe, or Pb)
List vec_fid = tree.getDataVectors("q2:W:nu:yb","pFidCut==1&&eFidCut==1&&iTgt==0",maxEvents);
List vec = tree.getDataVectors("q2:W:nu:yb","iTgt==0",maxEvents);
List vec_vert = tree.getDataVectors("eVx:eVy:eVz:ePhi:pVx:pVy:pVz:pPhi","eVy<1.4&&eVy>-1.4&&pVy<1.4&&pVy>-1.4",maxEvents);

// Create the histograms
H1F hQ2 = new H1F().create("hQ2",100,(DataVector)vec.get(0),0.0,5.0);
hQ2.setTitle(hTitle);
hQ2.setTitleX("Q^2 (GeV^2)");
hQ2.setTitleY("Counts");
H1F hW = new H1F().create("hW",100,(DataVector)vec.get(1),1.5,3.0);
hW.setTitle(hTitle);
hW.setTitleX("W (GeV)");
hW.setTitleY("Counts");
H1F hNu = new H1F().create("hNu",100,(DataVector)vec.get(2),2.0,4.5);
hNu.setTitle(hTitle);
hNu.setTitleX("#nu (GeV)");
hNu.setTitleY("Counts");
H1F hYb = new H1F().create("hYb",100,(DataVector)vec.get(3),0.4,1.0);
hYb.setTitle(hTitle);
hYb.setTitleX("Y_b");
hYb.setTitleY("Counts");

DataVector eVz = new DataVector();
eVz.copy(vec_vert.get(2));
H1F hVz_electron = new H1F().create("hVz_electron",400,eVz,-33.0,-22.0);
hVz_electron.setTitle(hTitle);
hVz_electron.setTitleX("Vertex z (cm)");
hVz_electron.setTitleY("Counts");

DataVector pVz = new DataVector();
pVz.copy(vec_vert.get(6));
H1F hVz_proton = new H1F().create("hVz_proton",400,pVz,-33.0,-22.0);
hVz_proton.setTitle(hTitle);
hVz_proton.setTitleX("Vertex z (cm)");
hVz_proton.setTitleY("Counts");

DataVector diffVz = new DataVector();
DataVector pVz_minus = new DataVector();
diffVz.copy(eVz);
pVz_minus.copy(pVz);
pVz_minus.mult(-1.0);
diffVz.addDataVector(pVz_minus);
H1F hdVz = new H1F().create("hdVz",400,diffVz,-5.0,5.0);
hdVz.setTitle(hTitle);
hdVz.setTitleX("#Deltaz (cm)");
hdVz.setTitleY("Counts");

TDirectory dir = new TDirectory();
String[] dirLabel = ["/DIS","/Vertex"];
dir.mkdir(dirLabel[0]);
dir.cd(dirLabel[0]);

// create the canvas for the display
int c1_title_size = 24;
TCanvas c1 = new TCanvas("c1",825,825);
c1.divide(2,2);
c1.cd(0);
c1.getPad().setTitleFontSize(c1_title_size);
c1.draw(hQ2);
dir.addDataSet(hQ2);
c1.cd(1);
c1.getPad().setTitleFontSize(c1_title_size);
c1.draw(hW);
dir.addDataSet(hW);
c1.cd(2);
c1.getPad().setTitleFontSize(c1_title_size);
c1.draw(hNu);
dir.addDataSet(hNu);
c1.cd(3);
c1.getPad().setTitleFontSize(c1_title_size);
c1.draw(hYb);
dir.addDataSet(hYb);

dir.mkdir(dirLabel[1]);
dir.cd(dirLabel[1]);
// create the canvas for the display
int c2_title_size = 24;
TCanvas c2 = new TCanvas("c2",1200,600);
c2.divide(2,1);
c2.cd(0);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(hVz_electron);
dir.addDataSet(hVz_electron);
c2.cd(1);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(hVz_proton);
dir.addDataSet(hVz_proton);

int c3_title_size = 24;
TCanvas c3 = new TCanvas("c3",600,600);
c3.cd(0);
c3.getPad().setTitleFontSize(c3_title_size);
c3.draw(hdVz);
dir.addDataSet(hdVz);

dir.writeFile(outFile); // write the histograms to the file
//c1.save(outFile+".png");

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen
