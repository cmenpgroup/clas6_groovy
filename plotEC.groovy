import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.math.*;

import eg2Cuts.clas6EC
import eg2Cuts.clas6FidCuts

clas6EC myEC = new clas6EC();  // create the EC object
clas6FidCuts myFidCuts = new clas6FidCuts(); // create the CLAS6 Fiducial Cuts object

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;
int counterFile = 0;

Vector3 ecXYZ = new Vector3(0.0,0.0,0.0);
Vector3 ecUVW = new Vector3(0.0,0.0,0.0);

int nECx = 450;
double ECxLo = -450.0;
double ECxHi = 450.0;
int nECy = 450;
double ECyLo = -450.0;
double ECyHi = 450.0;
String[] histsECxy = ["h2_EC_XvsY","h2_EC_XvsY_antiFid","h2_EC_XvsY_Fid"];
H2F[] h2_EC_XvsY = new H2F[histsECxy.size()];

histsECxy.eachWithIndex { hname, ih->
  h2_EC_XvsY[ih] = new H2F(hname,"EC x vs y",nECx,ECxLo,ECxHi,nECy,ECyLo,ECyHi);
  h2_EC_XvsY[ih].setTitleX("x (cm)");
  h2_EC_XvsY[ih].setTitleY("y (cm)");
}

int nECstrips = 225;
double ECstripLo = 0.0;
double ECstripHi = 450.0;
H1F h1_EC_U = new H1F("h1_EC_U","U (cm)","Counts",nECstrips,ECstripLo,ECstripHi);
h1_EC_U.setTitle("EC Hit Position");

H1F h1_EC_V = new H1F("h1_EC_V","V (cm)","Counts",nECstrips,ECstripLo,ECstripHi);
h1_EC_V.setTitle("EC Hit Position");

H1F h1_EC_W = new H1F("h1_EC_W","W (cm)","Counts",nECstrips,ECstripLo,ECstripHi);
h1_EC_W.setTitle("EC Hit Position");

H1F h1_EC_U_fid = new H1F("h1_EC_U_fid","U (cm)","Counts",nECstrips,ECstripLo,ECstripHi);
h1_EC_U_fid.setTitle("EC Hit Position");
h1_EC_U_fid.setFillColor(GREEN);

H1F h1_EC_V_fid = new H1F("h1_EC_V_fid","V (cm)","Counts",nECstrips,ECstripLo,ECstripHi);
h1_EC_V_fid.setTitle("EC Hit Position");
h1_EC_V_fid.setFillColor(GREEN);

H1F h1_EC_W_fid = new H1F("h1_EC_W_fid","W (cm)","Counts",nECstrips,ECstripLo,ECstripHi);
h1_EC_W_fid.setTitle("EC Hit Position");
h1_EC_W_fid.setFillColor(GREEN);

def cli = new CliBuilder(usage:'plotEC.groovy [options] infile1 infile2 ...')
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

Event      event  = new Event();
Bank       bank   = new Bank(reader.getSchemaFactory().getSchema("EVENT::particle"));
Bank       ecpb   = new Bank(reader.getSchemaFactory().getSchema("DETECTOR::ecpb"));

// Loop over all events
while(reader.hasNext()){
  if(counterFile % printCounter == 0) println counterFile;
  if(maxEvents!=0 && counterFile >= maxEvents) break;

  reader.nextEvent(event);
  event.read(bank);
  event.read(ecpb);

  int rows = bank.getRows();
  for(int i = 0; i < rows; i++){
    if(bank.getInt("pid",i)==11){
      if(bank.getInt("ecstat",i)>0 && ecpb.getRows()>0){ // check EC
        ECsector = ecpb.getInt("sector",bank.getInt("ecstat",i)-1);
        ecin = ecpb.getFloat("ein",bank.getInt("ecstat",i)-1);
        ecout = ecpb.getFloat("eout",bank.getInt("ecstat",i)-1);
        ectot = ecpb.getFloat("etot",bank.getInt("ecstat",i)-1);
        ecTime = ecpb.getFloat("time",bank.getInt("ecstat",i)-1);
        ecX = ecpb.getFloat("x",bank.getInt("ecstat",i)-1);
        ecY = ecpb.getFloat("y",bank.getInt("ecstat",i)-1);
        ecZ = ecpb.getFloat("z",bank.getInt("ecstat",i)-1);
        ecXYZ.setXYZ(ecX,ecY,ecZ);
        ecUVW = myEC.XYZtoUVW(ecXYZ);
        h2_EC_XvsY[0].fill(ecX,ecY);
        if(myEC.FidCutXYZ(ecXYZ)){
          h2_EC_XvsY[1].fill(ecX,ecY);
        } else{
          h2_EC_XvsY[2].fill(ecX,ecY);
        }
        h1_EC_U.fill(ecUVW.x());
        h1_EC_V.fill(ecUVW.y());
        h1_EC_W.fill(ecUVW.z());
        if(myEC.FidCutU(ecUVW.x())) h1_EC_U_fid.fill(ecUVW.x());
        if(myEC.FidCutV(ecUVW.y())) h1_EC_V_fid.fill(ecUVW.y());
        if(myEC.FidCutW(ecUVW.z())) h1_EC_W_fid.fill(ecUVW.z());
      }
    }
  }
  counterFile++;
}

int c1_title_size = 24;
TCanvas c1 = new TCanvas("c1",1500,500);
c1.divide(3,1);
histsECxy.eachWithIndex { hname, ih->
  c1.cd(ih);
  c1.getPad().setTitleFontSize(c1_title_size);
  c1.getPad().getAxisZ().setLog(true);
  c1.draw(h2_EC_XvsY[ih]);
}

int c2_title_size = 24;
TCanvas c2 = new TCanvas("c2",1500,500);
c2.divide(3,1);
c2.cd(0);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_EC_U);
c2.draw(h1_EC_U_fid,"same");
c2.cd(1);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_EC_V);
c2.draw(h1_EC_V_fid,"same");
c2.cd(2);
c2.getPad().setTitleFontSize(c2_title_size);
c2.draw(h1_EC_W);
c2.draw(h1_EC_W_fid,"same");
