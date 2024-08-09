//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.base.GStyle;
import org.jlab.jnp.utils.options.OptionParser;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;

// create a TDirectory object and read in the histogram file
TDirectory dir2 = new TDirectory();

OptionParser p = new OptionParser("plotEC_print.groovy");
p.addOption("-o", "plotEC_Hists.png", "output file name");
p.parse(args);
String outFile = p.getOption("-o").stringValue();

if(p.getInputList().size()==1){
  dir2.readFile(p.getInputList().get(0));
}else{
  System.out.println("*** No input files on command line. ***");
  p.printUsage();
  System.exit(0);
}

// declare a histogram object and set it equal to the histogram in the file
String[] histsECxy = ["h2_EC_XvsY","h2_EC_XvsY_antiFid","h2_EC_XvsY_Fid"];
H2F[] h2_XY = new H2F[histsECxy.size()];

// Declare the canvas object with name and dimensions
int c1_title_size = 24;
TCanvas c1 = new TCanvas("c1",1500,500);
c1.divide(3,1);

histsECxy.eachWithIndex { hname, ih->
  h2_XY[ih] = (H2F)dir2.getObject("XY/",hname);
  c1.cd(ih);

  // set the sizes of the fonts of the labels
  c1.getPad().setAxisTitleFontSize(24);
  c1.getPad().setAxisLabelFontSize(18);
  c1.getPad().setStatBoxFontSize(18);

  c1.getPad().setTitleFontSize(c1_title_size);
  c1.getPad().getAxisZ().setLog(true);
  c1.draw(h2_XY[ih]);
}
c1.save("plotEC_XY.png");

String[] histsECuvw = ["U","V","W"];
H1F[] h1_UVW = new H1F[histsECuvw.size()];
H1F[] h1_UVW_fid = new H1F[histsECuvw.size()];
int c2_title_size = 24;
TCanvas[] c2 = new TCanvas[histsECuvw.size()];

histsECuvw.eachWithIndex { ECplane, ih->
  def cName = "can_" + ECplane;
  c2[ih] = new TCanvas(cName,600,600);
  c2[ih].cd(0);
  h1_UVW[ih] = (H1F)dir2.getObject("Fid/","h1_EC_" + ECplane);
  h1_UVW_fid[ih] = (H1F)dir2.getObject("Fid/","h1_EC_" + ECplane + "_fid");
  c2[ih].getPad().setTitleFontSize(c2_title_size);
  c2[ih].draw(h1_UVW[ih]);
  c2[ih].draw(h1_UVW_fid[ih],"same");
  c2[ih].save("plotEC_" + ECplane + ".png");
}

String[] histsEC = ["P_vs_ECtotP","ECin_vs_ECout","ECinP_vs_ECoutP","P_vs_ECtotP_cut","ECin_vs_ECout_cut","ECinP_vs_ECoutP_cut"];
H2F[] h2_EC = new H2F[histsEC.size()];
TCanvas[] c3 = new TCanvas[histsEC.size()];
int c3_title_size = 24;

histsEC.eachWithIndex { ECname, ih->
  def cName = "can_" + ECname;
  c3[ih] = new TCanvas(cName,600,600);
  c3[ih].cd(0);
  c3[ih].getPad().setTitleFontSize(c3_title_size);
  h2_EC[ih] = (H2F)dir2.getObject("EC/","h2_" + ECname);
  c3[ih].draw(h2_EC[ih]);
  c3[ih].save("plotEC_" + ECname + ".png");
}

String[] histsCC = ["cc_nphe","cc_nphe_withEC"];
H1F[] h1_CC = new H1F[histsCC.size()];
H1F[] h1_CC_cut = new H1F[histsCC.size()];
int c4_title_size = 24;
TCanvas[] c4 = new TCanvas[histsCC.size()];

histsCC.eachWithIndex { CCname, ih->
  def cName = "can_" + CCname;
  c4[ih] = new TCanvas(cName,600,600);
  c4[ih].cd(0);
  h1_CC[ih] = (H1F)dir2.getObject("CC/","h1_" + CCname);
  h1_CC_cut[ih] = (H1F)dir2.getObject("CC/","h1_" + CCname + "_cut");
  c4[ih].getPad().setTitleFontSize(c2_title_size);
  c4[ih].draw(h1_CC[ih]);
  c4[ih].draw(h1_CC_cut[ih],"same");
  c4[ih].save("plotEC_" + CCname + ".png");
}
