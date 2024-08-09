//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.base.GStyle;
import org.jlab.jnp.utils.options.OptionParser;

import org.jlab.jnp.graphics.attr.AttributeType;
import org.jlab.jnp.groot.graphics.LegendNode2D.LegendStyle;
import org.jlab.jnp.groot.settings.GRootColorPalette;

GStyle.getAxisAttributesX().setTitleFontSize(24);
GStyle.getAxisAttributesY().setTitleFontSize(24);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;

// create a TDirectory object and read in the histogram file
TDirectory dir2 = new TDirectory();

OptionParser p = new OptionParser("eg2Electron_print.groovy");
p.parse(args);

if(p.getInputList().size()==1){
  dir2.readFile(p.getInputList().get(0));
}else{
  System.out.println("*** No input files on command line. ***");
  p.printUsage();
  System.exit(0);
}

// declare a histogram object and set it equal to the histogram in the file
String[] histsKine = ["hQ2","hW","hNu","hYb"];
String[] xLabel_Kine = ["Q^2 (GeV^2)","W (GeV)","#nu (GeV)","y "];
H1F[] h1_KineDIS = new H1F[histsKine.size()];
int c1_title_size = 24;
TCanvas[] c1 = new TCanvas[histsKine.size()];

histsKine.eachWithIndex { hname, ih->
  def cName = "can_" + hname;
  c1[ih] = new TCanvas(cName,625,600);
  c1[ih].cd(0);
  h1_KineDIS[ih] = (H1F)dir2.getObject("kinematics/",hname+"_DIS");
  c1[ih].getPad().setTitleFontSize(c1_title_size);
  h1_KineDIS[ih].setTitle("Experiment: eg2");
  h1_KineDIS[ih].setTitleX(xLabel_Kine[ih]);
  c1[ih].draw(h1_KineDIS[ih]);
  c1[ih].save("eg2Electron_" + hname + "_DIS.png");
}

String[] histsFid = ["hPhi_e","hPhi_e_fid"];
H1F[] h1_Fid = new H1F[histsFid.size()];
int c2_title_size = 24;
TCanvas c2 = new TCanvas("c2",625,600);
c2.cd(0);
histsFid.eachWithIndex { hname, ih->
  h1_Fid[ih] = (H1F)dir2.getObject("kinematics/",hname);
  c2.getPad().setTitleFontSize(c2_title_size);
  h1_Fid[ih].setTitle("Experiment: eg2");
  if(ih==0){
    c2.draw(h1_Fid[ih]);
  }else{
    c2.draw(h1_Fid[ih],"same");
  }
}
c2.save("eg2Electron_hPhi_e_fid.png");
