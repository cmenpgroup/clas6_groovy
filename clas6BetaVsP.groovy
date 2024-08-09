import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.clas.physics.*;
import org.jlab.clas.pdg.PhysicsConstants;
//import org.jlab.jnp.physics.*;
//import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.math.*;
import org.jlab.groot.fitter.DataFitter;

import org.jlab.jnp.utils.options.OptionParser;

import eg2Cuts.clas6beta

long st = System.currentTimeMillis(); // start time

myBeta = new clas6beta();  // create the beta object

GStyle.getAxisAttributesX().setTitleFontSize(32);
GStyle.getAxisAttributesY().setTitleFontSize(32);
GStyle.getAxisAttributesX().setLabelFontSize(24);
GStyle.getAxisAttributesY().setLabelFontSize(24);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

OptionParser p = new OptionParser("clas6BetaVsP.groovy");

p.addOption("-M", "0", "Max. Events");
p.addOption("-c", "20000", "Event progress counter");
p.addOption("-o", "clas6BetaVsP_Hists.hipo", "output file name");

p.parse(args);
int maxEvents = p.getOption("-M").intValue();
int printCounter = p.getOption("-c").intValue();
String outFile = p.getOption("-o").stringValue();

HipoChain reader = new HipoChain();
if(p.getInputList().size()){
  reader.addFiles(p.getInputList());
}else{
    System.out.println("*** No input files on command line. ***");
    p.printUsage();
    System.exit(0);
}
reader.open();

Vector3 v3electron = new Vector3(0,0,0);
LorentzVector electron = new LorentzVector(0,0,0,0);

PhysicsConstants PhyConsts= new PhysicsConstants();

H2F h2_BetaVsP = new H2F("h2_BetaVsP",100,0.0,5,125,0.0,1.25);
h2_BetaVsP.setTitle("Experiment: eg2");
h2_BetaVsP.setTitleX("Momentum (GeV/c)");
h2_BetaVsP.setTitleY("#beta");

H2F h2_BetaVsP_2212 = new H2F("h2_BetaVsP_2212",100,0.0,5,125,0.0,1.25);
h2_BetaVsP_2212.setTitle("Experiment: eg2");
h2_BetaVsP_2212.setTitleX("Momentum (GeV/c)");
h2_BetaVsP_2212.setTitleY("#beta");

String h2_name = "h2_dBetaVsP";
String[] particles_dBeta = ["em","pip","pim","proton"];
H2F[] h2_dBetaVsP = new H1F[particles_dBeta.size()];
String title_dBetaVsP = "Experiment: eg2";
String xLabel_dBetaVsP = "Momentum (GeV/c)";
String[] yLabel_dBetaVsP = ["#Delta #beta (e-)","#Delta #beta (#pi+)","#Delta #beta (#pi-)","#Delta #beta (proton)"];
int[] nBin_dBetaVsP = [200,200,200,200];
double[] xLo_dBetaVsP = [-0.25,-0.25,-0.25,-0.15];
double[] xHi_dBetaVsP = [0.25,0.25,0.25,0.15];

particles_dBeta.eachWithIndex { hname, ih->
  h2_dBetaVsP[ih] = new H2F(h2_name + "_" + particles_dBeta[ih],100,0.0,5,nBin_dBetaVsP[ih],xLo_dBetaVsP[ih],xHi_dBetaVsP[ih]);
  h2_dBetaVsP[ih].setTitle(title_dBetaVsP);
  h2_dBetaVsP[ih].setTitleX(xLabel_dBetaVsP);
  h2_dBetaVsP[ih].setTitleY(yLabel_dBetaVsP[ih]);
}

H1F h1_dBeta_proton = new H1F("h1_dBeta_proton",300,-0.15,0.15);
h1_dBeta_proton.setTitle("Experiment: eg2");
h1_dBeta_proton.setTitleX("#Delta #beta (proton)");
h1_dBeta_proton.setTitleY("Counts");

Event      event  = new Event();
Bank       bank   = new Bank(reader.getSchemaFactory().getSchema("EVENT::particle"));

int counterFile = 0;

while(reader.hasNext()){   // Loop over all events
  if(counterFile % printCounter == 0) println counterFile;
  if(maxEvents!=0 && counterFile >= maxEvents) break;

  reader.nextEvent(event);
  event.read(bank);

  int rows = bank.getRows();
  for(int i = 0; i < rows; i++){
    def px = bank.getFloat("px",i);
    def py = bank.getFloat("py",i);
    def pz = bank.getFloat("pz",i);
    def mom = Math.sqrt(px*px + py*py + pz*pz)
    def beta = bank.getFloat("beta",i);
    if(beta>0 && mom>0.1) h2_BetaVsP.fill(mom,beta);

    int pid = bank.getInt("pid",i);
    switch(pid){
      case 11:
        betaFromMass = myBeta.Get_BetaFromMass(mom,PhyConsts.massElectron());
        if(betaFromMass>-99.0) h2_dBetaVsP[0].fill(mom,beta-betaFromMass);
        break;
      case 211:
        betaFromMass = myBeta.Get_BetaFromMass(mom,PhyConsts.massPionCharged());
        if(betaFromMass>-99.0) h2_dBetaVsP[1].fill(mom,beta-betaFromMass);
        break;
      case -211:
        betaFromMass = myBeta.Get_BetaFromMass(mom,PhyConsts.massPionCharged());
        if(betaFromMass>-99.0) h2_dBetaVsP[2].fill(mom,beta-betaFromMass);
        break;
      case 2212:
        betaFromMass = myBeta.Get_BetaFromMass(mom,PhyConsts.massProton());
        if(betaFromMass>-99.0){
          h1_dBeta_proton.fill(beta-betaFromMass);
          h2_dBetaVsP[3].fill(mom,beta-betaFromMass);
          if(beta>0 && mom>0.1) h2_BetaVsP_2212.fill(mom,beta);
        }
        break;
      default: break;
    }
  }
  counterFile++;
}
System.out.println("processed (total) = " + counterFile);

TDirectory dir = new TDirectory();
String[] dirLabel = ["/BetaVsP","/dBetaVsP"];
dir.mkdir(dirLabel[0]);
dir.cd(dirLabel[0]);
TCanvas c1 = new TCanvas("c1",600,600);
c1.getPad().setTitleFontSize(32);
c1.getPad().getAxisZ().setLog(true);
c1.draw(h2_BetaVsP);
dir.addDataSet(h2_BetaVsP);

TCanvas c1_2212 = new TCanvas("c1_2212",600,600);
c1_2212.getPad().setTitleFontSize(32);
c1_2212.getPad().getAxisZ().setLog(true);
c1_2212.draw(h2_BetaVsP_2212);
dir.addDataSet(h2_BetaVsP_2212);

dir.mkdir(dirLabel[1]);
dir.cd(dirLabel[1]);
TCanvas c2 = new TCanvas("c2",900,900);
c2.divide(2,2);
particles_dBeta.eachWithIndex { hname, ih->
  c2.cd(ih);
  c2.getPad().setTitleFontSize(32);
  c2.getPad().setAxisTitleFontSize(24);
  c2.getPad().setAxisLabelFontSize(18);
  c2.draw(h2_dBetaVsP[ih]);
  dir.addDataSet(h2_dBetaVsP[ih]);
}

TCanvas c3 = new TCanvas("c3",600,600);
c3.getPad().setTitleFontSize(32);
F1D f1 = new F1D("f1","[amp]*gaus(x,[mean],[sigma])", -0.0135, 0.010);
f1.setParameter(0, 100.0);
f1.setParameter(1, 0.0);
f1.setParameter(2, 0.01);
c3.draw(h1_dBeta_proton);
DataFitter.fit(f1, h1_dBeta_proton, "Q"); //No options uses error for sigma
f1.setLineColor(32);
f1.setLineWidth(5);
f1.setLineStyle(1);
f1.setOptStat(1111);
c3.draw(f1,"same");
dir.addDataSet(h1_dBeta_proton);
for(int j=0; j<f1.getNPars(); j++) System.out.println(" par = " + f1.parameter(j).value() + " error = " + f1.parameter(j).error());;

dir.writeFile(outFile); // write the histograms to the file

long et = System.currentTimeMillis(); // end time
long time = et-st; // time to run the script
System.out.println(" time = " + (time/1000.0)); // print run time to the screen
