//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.base.GStyle;
import org.jlab.jnp.utils.options.OptionParser;

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

OptionParser p = new OptionParser("clas6BetaVsP_print.groovy");
p.addOption("-o", "clas6BetaVsP_Hists.png", "output file name");
p.parse(args);
String outFile = p.getOption("-o").stringValue();

if(p.getInputList().size()==1){
  dir2.readFile(p.getInputList().get(0));
}else{
  System.out.println("*** No input files on command line. ***");
  p.printUsage();
  System.exit(0);
}

// Declare the canvas object with name and dimensions
int c1_title_size = 24;
TCanvas c1 = new TCanvas("c1",600,600);
c1.getPad().setTitleFontSize(32);
c1.getPad().getAxisZ().setLog(true);
H2F h2_BetaVsP = (H2F)dir2.getObject("BetaVsP/","h2_BetaVsP");
c1.draw(h2_BetaVsP);
c1.save("clas6BetaVsP.png");
