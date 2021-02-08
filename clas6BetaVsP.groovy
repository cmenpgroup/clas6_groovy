import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.math.*;
import org.jlab.groot.fitter.DataFitter;

import eg2Cuts.clas6beta

myBeta = new clas6beta();  // create the beta object

GStyle.getAxisAttributesX().setTitleFontSize(32);
GStyle.getAxisAttributesY().setTitleFontSize(32);
GStyle.getAxisAttributesX().setLabelFontSize(24);
GStyle.getAxisAttributesY().setLabelFontSize(24);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

def cli = new CliBuilder(usage:'clas6BetaVsP.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
cli.c(longOpt:'counter', args:1, argName:'count by events', type: int, 'Event progress counter')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def printCounter = 20000;
if(options.c) printCounter = options.c;

def maxEvents = 0;
if(options.M) maxEvents = options.M;

def extraArguments = options.arguments()
if (extraArguments.isEmpty()){
  println "No input file!";
  cli.usage();
  return;
}

HipoChain reader = new HipoChain();

extraArguments.each { infile ->
  reader.addFile(infile);
}
reader.open();

Vector3 v3electron = new Vector3(0,0,0);
LorentzVector electron = new LorentzVector(0,0,0,0);

PhysicsConstants PhyConsts= new PhysicsConstants();

H2F h2_BetaVsP = new H2F("h2_BetaVsP",100,0.0,5,125,0.0,1.25);
h2_BetaVsP.setTitle("Experiment: eg2");
h2_BetaVsP.setTitleX("Momentum (GeV/c)");
h2_BetaVsP.setTitleY("#beta");

H2F h2_dBetaVsP_em = new H2F("h2_dBetaVsP_em",100,0.0,5,200,-0.25,0.25);
h2_dBetaVsP_em.setTitle("Experiment: eg2");
h2_dBetaVsP_em.setTitleX("Momentum (GeV/c)");
h2_dBetaVsP_em.setTitleY("#Delta #beta (e-)");

H2F h2_dBetaVsP_pip = new H2F("h2_dBetaVsP_pip",100,0.0,5,200,-0.25,0.25);
h2_dBetaVsP_pip.setTitle("Experiment: eg2");
h2_dBetaVsP_pip.setTitleX("Momentum (GeV/c)");
h2_dBetaVsP_pip.setTitleY("#Delta #beta (#pi+)");

H2F h2_dBetaVsP_pim = new H2F("h2_dBetaVsP_pim",100,0.0,5,200,-0.25,0.25);
h2_dBetaVsP_pim.setTitle("Experiment: eg2");
h2_dBetaVsP_pim.setTitleX("Momentum (GeV/c)");
h2_dBetaVsP_pim.setTitleY("#Delta #beta (#pi-)");

H1F h1_dBeta_proton = new H1F("h2_dBeta_proton",300,-0.15,0.15);
h1_dBeta_proton.setTitle("Experiment: eg2");
h1_dBeta_proton.setTitleX("#Delta #beta (proton)");
h1_dBeta_proton.setTitleY("Counts");

H2F h2_dBetaVsP_proton = new H2F("h2_dBetaVsP_proton",100,0.0,5,200,-0.15,0.15);
h2_dBetaVsP_proton.setTitle("Experiment: eg2");
h2_dBetaVsP_proton.setTitleX("Momentum (GeV/c)");
h2_dBetaVsP_proton.setTitleY("#Delta #beta (proton)");

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
    def p = Math.sqrt(px*px + py*py + pz*pz)
    def beta = bank.getFloat("beta",i);
    h2_BetaVsP.fill(p,beta);

    int pid = bank.getInt("pid",i);
    switch(pid){
      case 11:
        betaFromMass = myBeta.Get_BetaFromMass(p,PhyConsts.massElectron());
        if(betaFromMass>-99.0) h2_dBetaVsP_em.fill(p,beta-betaFromMass);
        break;
      case 211:
        betaFromMass = myBeta.Get_BetaFromMass(p,PhyConsts.massPionCharged());
        if(betaFromMass>-99.0) h2_dBetaVsP_pip.fill(p,beta-betaFromMass);
        break;
      case -211:
        betaFromMass = myBeta.Get_BetaFromMass(p,PhyConsts.massPionCharged());
        if(betaFromMass>-99.0) h2_dBetaVsP_pim.fill(p,beta-betaFromMass);
        break;
      case 2212:
        betaFromMass = myBeta.Get_BetaFromMass(p,PhyConsts.massProton());
        if(betaFromMass>-99.0){
          h1_dBeta_proton.fill(beta-betaFromMass);
          h2_dBetaVsP_proton.fill(p,beta-betaFromMass);
        }
        break;
      default: break;
    }
  }
  counterFile++;
}
System.out.println("processed (total) = " + counterFile);

TCanvas c1 = new TCanvas("c1",600,600);
c1.getPad().setTitleFontSize(32);
c1.getPad().getAxisZ().setLog(true);
c1.draw(h2_BetaVsP);

TCanvas c2 = new TCanvas("c2",900,900);
c2.divide(2,2);
c2.cd(0);
c2.getPad().setTitleFontSize(32);
c2.getPad().setAxisTitleFontSize(24);
c2.getPad().setAxisLabelFontSize(18);
c2.draw(h2_dBetaVsP_em);
c2.cd(1);
c2.getPad().setTitleFontSize(32);
c2.getPad().setAxisTitleFontSize(24);
c2.getPad().setAxisLabelFontSize(18);
c2.draw(h2_dBetaVsP_pip);
c2.cd(2);
c2.getPad().setTitleFontSize(32);
c2.getPad().setAxisTitleFontSize(24);
c2.getPad().setAxisLabelFontSize(18);
c2.draw(h2_dBetaVsP_pim);
c2.cd(3);
c2.getPad().setTitleFontSize(32);
c2.getPad().setAxisTitleFontSize(24);
c2.getPad().setAxisLabelFontSize(18);
c2.draw(h2_dBetaVsP_proton);

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
for(int j=0; j<f1.getNPars(); j++) System.out.println(" par = " + f1.parameter(j).value() + " error = " + f1.parameter(j).error());;
