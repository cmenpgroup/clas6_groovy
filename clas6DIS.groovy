import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

double W_DIS = 2.0;
double Q2_DIS = 1.0;
int counterFile = 0;

PhysicsConstants PhyConsts= new PhysicsConstants();

double beamEnergy = 5.1;
LorentzVector beam = new LorentzVector(0.0,0.0,beamEnergy,beamEnergy);
LorentzVector protonTarget = new LorentzVector(0.0,0.0,0.0,PhyConsts.massProton());
LorentzVector electron = new LorentzVector(0,0,0,0);

def cli = new CliBuilder(usage:'clas6DIS.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')
cli.M(longOpt:'max',  args:1, argName:'max events' , type: int, 'Filter this number of events')
cli.c(longOpt:'counter', args:1, argName:'count by events', type: int, 'Event progress counter')
cli.o(longOpt:'output', args:1, argName:'Output file', 'Filtered output file')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def printCounter = 20000;
if(options.c) printCounter = options.c;

def maxEvents = 0;
if(options.M) maxEvents = options.M;

String outfile = "skimmed.hipo";
if(options.o) outfile = options.o;

println "Electron DIS cuts"
println "Q^2 >= " + Q2_DIS + " GeV/c^2";
println "W >= " + W_DIS + " GeV";

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

  reader.nextEvent(event);
  event.read(bank);
  event.read(head);

  for(int i=0;i<bank.getRows();i++){ // loop over the particles in the bank
    if(bank.getInt("pid",i)==11){ // check for electron PID
      // create electron 4-vector
      electron.setPxPyPzM(bank.getFloat("px",i), bank.getFloat("py",i), bank.getFloat("pz",i), PhyConsts.massElectron());

      LorentzVector vecQ2 = LorentzVector.from(beam);   // calculate Q-squared, first copy incident e- 4-vector
      vecQ2.sub(electron);  // calculate Q-squared, subtract scattered e- 4-vector
      double posQ2 = -vecQ2.mass2(); // calcuate Q-squared, make into a positive value

      LorentzVector vecW2 = LorentzVector.from(beam); // calculate W, first copy incident e- 4-vector
      vecW2.add(protonTarget).sub(electron); // calculate W, add target proton 4-vector and subtract scattered e- 4-vector

      // save the event if the electron passes the DIS cuts
      if(posQ2>=Q2_DIS && vecW2.mass()>=W_DIS){
        output.addEvent(event);
        break;
      }
    }
  }
  counterFile++;
}
output.close();
