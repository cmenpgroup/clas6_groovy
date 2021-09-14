import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

import eg2AnaTree.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

int GREEN = 33;
int BLUE = 34;
int YELLOW = 35;

def cli = new CliBuilder(usage:'eg2Proton_AnaHists.groovy [options] infile1 infile2 ...')
cli.h(longOpt:'help', 'Print this message.')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

def extraArguments = options.arguments()
if (extraArguments.isEmpty()){
  println "No input file!";
  cli.usage();
  return;
}

String fileName;
extraArguments.each { infile ->
  fileName = infile;
}

HistInfo myHI = new HistInfo();
List<String> Var = myHI.getVariables();
List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();

TDirectory dir = new TDirectory();
dir.readFile(fileName);

String[] DirLabel = ["LD2/","Solid/","multiplicity/"];
H1F[][] h1_nProton = new H1F[Var.size()][TgtLabel.size()];
GraphErrors[] gr_mrProton = new GraphErrors[Var.size()];
TCanvas[] can = new TCanvas[Var.size()];
int c_title_size = 24;

Var.eachWithIndex{nVar, iVar->
  String canName = "c" + iVar;
  can[iVar] = new TCanvas(canName,1200,600);
  can[iVar].divide(3,1);
  DirLabel.eachWithIndex{nDir, iDir->
    can[iVar].cd(iDir);
    can[iVar].getPad().setTitleFontSize(c_title_size);    
    if(iDir<2){
      hname = "hYlds_" + TgtLabel[iDir] + "_" + nVar;
      h1_nProton[iVar][iDir]= dir.getObject(nDir,hname);
      can[iVar].draw(h1_nProton[iVar][iDir]);
    }else{
      hname = "gr_mrProton_" + nVar;
      gr_mrProton[iVar] = dir.getObject(nDir,hname);
      can[iVar].draw(gr_mrProton[iVar]);
    }
  }
}
