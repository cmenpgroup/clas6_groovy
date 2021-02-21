import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;

def ElectronList =[];
def PosChargedList =[];
def NegChargedList =[];
def NeutralList =[];
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
cli._(longOpt:'Npos', args:1, argName:'# of positive particles', type: int, 'Select events with >= Npos')
cli._(longOpt:'Nneg', args:1, argName:'# of negative particles', type: int, 'Select events with >= Nneg')
cli._(longOpt:'Nzero', args:1, argName:'# of neutral particles', type: int, 'Select events with >= Nzero')
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

def Npos = 0; if(options.Npos) Npos = options.Npos;
def Nneg = 0; if(options.Nneg) Nneg = options.Nneg;
def Nzero = 0; if(options.Nzero) Nzero = options.Nzero;
def Np = 0; if(options.Nproton) Np = options.Nproton;
def Nn = 0; if(options.Nneutron) Nn = options.Nneutron;
def Nem = 0; if(options.Nelectron) Nem = options.Nelectron;
def Nphot = 0; if(options.Nphoton) Nphot = options.Nphoton;
def Npip = 0; if(options.Npiplus) Npip = options.Npiplus;
def Npim = 0; if(options.Npiminus) Npim = options.Npiminus;
def Nkp = 0; if(options.Nkplus) Nkp = options.Nkplus;
def Nkm = 0; if(options.Nkminus) Nkm = options.Nkminus;

println "e-: " + Nem;
println "N+: " + Npos;
println "N-: " + Nneg;
println "N0: " + Nzero;
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
Bank head = new Bank(reader.getSchemaFactory().getSchema("HEADER::info"));

while(reader.hasNext()) {
  if(counterFile % printCounter == 0) println counterFile;
  if(maxEvents!=0 && counterFile >= maxEvents) break;

  PosChargedList.clear();
  NegChargedList.clear();
  NeutralList.clear();
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
  boolean iNpos = false;
  boolean iNneg = false;
  boolean iNzero = false;
  boolean iNphot = false;
  boolean iNpip = false;
  boolean iNpim = false;
  boolean iNkp = false;
  boolean iNkm = false;
  boolean iNp = false;
  boolean iNn = false;

  reader.nextEvent(event);
  event.read(bank);
  event.read(head);

  for(int i=0;i<bank.getRows();i++){
    if(bank.getInt("charge",i)>0 && Npos) PosChargedList.add(i);
    if(bank.getInt("charge",i)<0 && Nneg) NegChargedList.add(i);
    if(bank.getInt("charge",i)==0 && Nzero) NeutralList.add(i);
    if(bank.getInt("pid",i)==11 && Nem) ElectronList.add(i);
    if(bank.getInt("pid",i)==22 && Nphot) PhotonList.add(i);
    if(bank.getInt("pid",i)==211 && Npip) PiPlusList.add(i);
    if(bank.getInt("pid",i)==-211 && Npim) PiMinusList.add(i);
    if(bank.getInt("pid",i)==321 && Nkp) KPlusList.add(i);
    if(bank.getInt("pid",i)==-321 && Nkm) KMinusList.add(i);
    if(bank.getInt("pid",i)==2212 && Np) ProtonList.add(i);
    if(bank.getInt("pid",i)==2112 && Nn) NeutronList.add(i);
  }

  // check the filter criteria
  // if a particle is not selected, the default value is zero and the flag is set from FALSE to TRUE.
  if(ElectronList.size()>=Nem) iNem = true;
  if(PosChargedList.size()>=Npos) iNpos = true;
  if(NegChargedList.size()>=Nneg) iNneg = true;
  if(NeutralList.size()>=Nzero) iNzero = true;
  if(PhotonList.size()>=Nphot) iNphot = true;
  if(PiPlusList.size()>=Npip) iNpip = true;
  if(PiMinusList.size()>=Npim) iNpim = true;
  if(KPlusList.size()>=Nkp) iNkp = true;
  if(KMinusList.size()>=Nkm) iNkm = true;
  if(ProtonList.size()>=Np) iNp = true;
  if(NeutronList.size()>=Nn) iNn = true;

  // condition is AND since particles not selected are zero which sets their flags to TRUE
	if(iNem && iNphot && iNpip && iNpim && iNkp && iNkm && iNp && iNn && iNpos && iNneg && iNzero){
    output.addEvent(event);
  }
  counterFile++;
}
output.close();
