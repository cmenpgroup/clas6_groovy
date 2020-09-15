import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;

def ElectronList =[];
def PhotonList = [];
def PiPlusList = [];
def PiMinusList = [];
def KPlusList = [];
def KMinusList = [];
def ProtonList = [];
def NeutronList = [];
def OtherList = [];

int counterFile = 0;

def cli = new CliBuilder(usage:'clas6Filtering.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
cli.c(longOpt:'counter', args:1, argName:'count by events', type: int, 'Event progress counter')
cli.o(longOpt:'output', args:1, argName:'Output file', 'Filtered output file')
cli._(longOpt:'Nproton', args:1, argName:'# of protons', type: int, 'Select events with >= Nproton')
cli._(longOpt:'Nneutron', args:1, argName:'# of neutrons', type: int, 'Select events with >= Nneutron')
cli._(longOpt:'Nelectron', args:1, argName:'# of electrons', type: int, 'Select events with >= Nelectron')
cli._(longOpt:'Nphoton', args:1, argName:'# of photons', type: int, 'Select events with >= Nphoton')
cli._(longOpt:'Npiplus', args:1, argName:'# of pi+', type: int, 'Select events with >= Npiplus')
cli._(longOpt:'Npiminus', args:1, argName:'# of pi-', type: int, 'Select events with >= Npiminus')
cli._(longOpt:'Nkplus', args:1, argName:'# of K+', type: int, 'Select events with >= Nkplus')
cli._(longOpt:'Nkminus', args:1, argName:'# of K-', type: int, 'Select events with >= Nkminus')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def printCounter = 20000;
if(options.c) printCounter = options.c;

def maxEvents = 0;
if(options.M) maxEvents = options.M;

String outfile = "skimmed.hipo";
if(options.o) outfile = options.o;

def Np = 0; if(options.Nproton) Np = options.Nproton;
def Nn = 0; if(options.Nneutron) Nn = options.Nneutron;
def Nem = 0; if(options.Nelectron) Nem = options.Nelectron;
def Nphot = 0; if(options.Nphoton) Nphot = options.Nphoton;
def Npip = 0; if(options.Npiplus) Npip = options.Npiplus;
def Npim = 0; if(options.Npiminus) Npim = options.Npiminus;
def Nkp = 0; if(options.Nkplus) Nkp = options.Nkplus;
def Nkm = 0; if(options.Nkminus) Nkm = options.Nkminus;

println "e-: " + Nem;
println "p: " + Np;
println "n: " + Nn;
println "photon: " + Nphot;
println "pi+: " + Npip;
println "pi-: " + Npim;
println "K+: " + Nkp;
println "K-: " + Nkm;

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

HipoWriter output = new HipoWriter(reader.getSchemaFactory());
output.open(outfile);

Event event = new Event();
Bank bank = new Bank(reader.getSchemaFactory().getSchema("EVENT::particle"));

while(reader.hasNext()) {
  if(counterFile % printCounter == 0) println counterFile;
  if(maxEvents!=0 && counterFile >= maxEvents) break;

  ElectronList.clear();
  PhotonList.clear();
  PiPlusList.clear();
  PiMinusList.clear();
  KPlusList.clear();
  KMinusList.clear();
  ProtonList.clear();
  NeutronList.clear();
  OtherList.clear();

  boolean iNem = false;
  boolean iNphot = false;
  boolean iNpip = false;
  boolean iNpim = false;
  boolean iNkp = false;
  boolean iNkm = false;
  boolean iNp = false;
  boolean iNn = false;

  reader.nextEvent(event);
  event.read(bank);
  for(int i=0;i<bank.getRows();i++){
    int pid = bank.getInt("pid",i);
    switch(pid){
      case 11: if(Nem) ElectronList.add(i); break;
      case 22: if(Nphot) PhotonList.add(i); break;
      case 211: if(Npip) PiPlusList.add(i); break;
      case -211: if(Npim) PiMinusList.add(i); break;
      case 321: if(Nkp) KPlusList.add(i); break;
      case -321: if(Nkm) KMinusList.add(i); break;
      case 2212: if(Np) ProtonList.add(i); break;
      case 2112: if(Nn) NeutronList.add(i); break;
      default: OtherList.add(i); break;
    }
  }

  if(ElectronList.size()>=Nem) iNem = true;
  if(PhotonList.size()>=Nphot) iNphot = true;
  if(PiPlusList.size()>=Npip) iNpip = true;
  if(PiMinusList.size()>=Npim) iNpim = true;
  if(KPlusList.size()>=Nkp) iNkp = true;
  if(KMinusList.size()>=Nkm) iNkm = true;
  if(ProtonList.size()>=Np) iNp = true;
  if(NeutronList.size()>=Nn) iNn = true;

	if(iNem && iNphot && iNpip && iNpim && iNkp && iNkm && iNp && iNn){
    output.addEvent(event);
  }
  counterFile++;
}
output.close();
