import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

def FindSector = {
  phi ->
  if(phi > -30 && phi < 30) {
    sect = 1;
  } else if(phi > -90 && phi < -30) {
    sect = 2;
  } else if(phi > -150 && phi < -90) {
    sect = 3;
  } else if(phi > 150 || phi < -150) {
    sect = 4;
  } else if(phi > 90 && phi < 150) {
    sect = 5;
  } else if(phi > 30 && phi < 90) {
    sect = 6;
  }
  return sect;
}

GStyle.getAxisAttributesX().setTitleFontSize(32);
GStyle.getAxisAttributesY().setTitleFontSize(32);
GStyle.getAxisAttributesX().setLabelFontSize(24);
GStyle.getAxisAttributesY().setLabelFontSize(24);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

int sector;
int counterTotal = 0;
int counterFile;

Vector3 v3electron = new Vector3(0,0,0);
LorentzVector electron = new LorentzVector(0,0,0,0);

PhysicsConstants PhyConsts= new PhysicsConstants();

//println args.length;

H1F h1_Vz = new H1F("h1_Vz",100,-33,-20.0);
h1_Vz.setTitle("Experiment: eg2");
h1_Vz.setTitleX("Vertex z (cm)");
h1_Vz.setTitleY("Counts");
h1_Vz.setFillColor(44);

H2F h2_Vz_phi = new H2F("h2_Vz_phi",100,-33,-20.0,360,-180.0,180.0);
h2_Vz_phi.setTitle("Experiment: eg2");
h2_Vz_phi.setTitleX("Vertex z (cm)");
h2_Vz_phi.setTitleY("#phi (deg.)");

H1F h1_Vz_corr = new H1F("h1_Vz_corr",100,-33,-20.0);
h1_Vz_corr.setTitle("Experiment: eg2");
h1_Vz_corr.setTitleX("Vertex z (cm)");
h1_Vz_corr.setTitleY("Counts");
h1_Vz_corr.setFillColor(44);

H2F h2_Vz_phi_corr = new H2F("h2_Vz_phi_corr",100,-33,-20.0,360,-180.0,180.0);
h2_Vz_phi_corr.setTitle("Experiment: eg2");
h2_Vz_phi_corr.setTitleX("Vertex z (cm)");
h2_Vz_phi_corr.setTitleY("#phi (deg.)");

for(int j=0;j<args.length;j++){
  HipoReader reader = new HipoReader();
  reader.open(args[j]);

  Event      event  = new Event();
  Bank       bank   = new Bank(reader.getSchemaFactory().getSchema("EVENT::particle"));
  // Loop over all events
  counterFile = 0;
  while(reader.hasNext()==true){
    if(counterFile%25000 == 0) println counterFile;
    reader.nextEvent(event);
    event.read(bank);

    int rows = bank.getRows();
    for(int i = 0; i < rows; i++){
      if(bank.getInt("pid",i) == 11){
        electron.setPxPyPzM(bank.getFloat("px",i), bank.getFloat("py",i), bank.getFloat("pz",i), PhyConsts.massElectron());
        v3electron.setXYZ(bank.getFloat("vx",i), bank.getFloat("vy",i), bank.getFloat("vz",i));
        h1_Vz.fill(v3electron.z());

        double phi_deg = Math.toDegrees(electron.phi()); // convert to degrees
        h2_Vz_phi.fill(v3electron.z(),phi_deg);

        sector = FindSector(phi_deg);

        int phi_new = phi_deg+30.0; // shift by 30 deg
        if(phi_new<0)phi_new+=360.0; // if negative, shift positive
        int sect =  Math.floor(phi_new/60.0);

        Vector3 RotatedVertPos = Vector3.from(v3electron);
        Vector3 RotatedVertDir = Vector3.from(electron.vect());
        Vector3 TargetPos = new Vector3(0.043,-0.33,0.0);

        RotatedVertPos.rotateZ(-Math.toRadians(60.0*sect));
        RotatedVertDir.rotateZ(-Math.toRadians(60.0*sect));
        TargetPos.rotateZ(-Math.toRadians(60.0*sect));

        double ShiftLength = (TargetPos.x() - RotatedVertPos.x())/RotatedVertDir.x();
        RotatedVertDir.setXYZ(ShiftLength*RotatedVertDir.x(),ShiftLength*RotatedVertDir.y(),ShiftLength*RotatedVertDir.z());
        RotatedVertPos.add(RotatedVertDir);

        Vector3 ParticleVertCorr = Vector3.from(RotatedVertPos);
        ParticleVertCorr.sub(TargetPos.x(),TargetPos.y(),0.0);

        h1_Vz_corr.fill(ParticleVertCorr.z());
        h2_Vz_phi_corr.fill(ParticleVertCorr.z(),phi_deg);
//      bank.show();
      }
    }
    counterFile++;
  }
  reader.close();
  System.out.println("processed " + counterFile + " in " + args[j]);
  counterTotal += counterFile;
}
System.out.println("processed (total) = " + counterTotal);

TCanvas c1 = new TCanvas("c1",900,900);
c1.divide(2,2);
c1.cd(0);
c1.draw(h1_Vz);
c1.cd(1);
c1.draw(h2_Vz_phi);
c1.cd(2);
c1.draw(h1_Vz_corr);
c1.cd(3);
c1.draw(h2_Vz_phi_corr);
