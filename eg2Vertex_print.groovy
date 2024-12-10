//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.base.GStyle;
import org.jlab.jnp.utils.options.OptionParser;

import eg2Cuts.eg2Target;

eg2Target myTarget = new eg2Target();  // create the eg2 target object

GStyle.getAxisAttributesX().setTitleFontSize(24);
GStyle.getAxisAttributesY().setTitleFontSize(24);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;

int MAX_SECTORS = myTarget.Get_MAX_SECTORS();
String[] part = ["electron","proton","positive"];
String[] h1DPart =["h1_Vz","h1_Vz_corr"];
String[] h2DPart =["h2_Vz_phi","h2_Vz_phi_corr"];
String[] histSec = ["h1_Vz_sec","h1_Vz_sec_corr"];
H1F[] h1D = new H1F[h1DPart.size()];
H2F[] h2D = new H2F[h1DPart.size()];
H1F[][] h1DSec = new H1F[histSec.size()][MAX_SECTORS];

// create a TDirectory object and read in the histogram file
TDirectory dir2 = new TDirectory();

OptionParser p = new OptionParser("egVertex_print.groovy");
p.addRequired("-p","Particle Type: 0=electron, 1=proton, 2=positive");
p.parse(args);
int pType = p.getOption("-p").intValue();

if(p.getInputList().size()==1){
  dir2.readFile(p.getInputList().get(0));
}else{
  System.out.println("*** No input files on command line. ***");
  p.printUsage();
  System.exit(0);
}

if(pType<0 || pType>part.size()){
  System.out.println("Incorrect particle type "+pType+". Try again!");
  p.printUsage();
  System.exit(0);
}

// Declare the canvas object with name and dimensions
int can_title_size = 24;
TCanvas[] can1D = new TCanvas[h1DPart.size()];
h1DPart.eachWithIndex { hname, ih->
  can1D[ih] = new TCanvas("can_"+hname,600,600);
  can1D[ih].getPad().setTitleFontSize(can_title_size);
  h1D[ih] = (H1F)dir2.getObject(part[pType]+"/",hname+"_"+part[pType]);
  h1D[ih].setLineColor(1);
  can1D[ih].draw(h1D[ih]);
  can1D[ih].save("eg2Vertex_"+hname+"_"+part[pType]+".png");
}
TCanvas[] can2D = new TCanvas[h2DPart.size()];
h2DPart.eachWithIndex { hname, ih->
  can2D[ih] = new TCanvas("can_"+hname,600,600);
  can2D[ih].getPad().setTitleFontSize(can_title_size);
//  can2D[ih].getPad().getAxisZ().setLog(true);
  h2D[ih] = (H2F)dir2.getObject(part[pType]+"/",hname+"_"+part[pType]);
  can2D[ih].draw(h2D[ih]);
  can2D[ih].save("eg2Vertex_"+hname+"_"+part[pType]+".png");
}
TCanvas[] canSec = new TCanvas[histSec.size()];
histSec.eachWithIndex { hname, ih->
  canSec[ih] = new TCanvas("can_"+hname,600,600);
  canSec[ih].getPad().setTitleFontSize(can_title_size);
  for(int i=0; i<MAX_SECTORS; i++){
    h1DSec[ih][i] = (H1F)dir2.getObject(part[pType]+"/",hname+"_"+part[pType]+"_"+i);
    if(i==0){
      canSec[ih].draw(h1DSec[ih][i]);
    }else{
      canSec[ih].draw(h1DSec[ih][i],"same");
    }
  }
  canSec[ih].save("eg2Vertex_"+hname+"_"+part[pType]+".png");
}
