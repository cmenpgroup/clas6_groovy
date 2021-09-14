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

def cli = new CliBuilder(usage:'eg2Proton_MR3zh_DumpHists.groovy [options] infile1 infile2 ...')
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
List<String> TgtLabel = myHI.getTgtlabel();
List<String> solidTgt = myHI.getSolidTgtLabel();
//YieldsForMR3zh myMR = new YieldsForMR3zh();
//List<String> Q2Cuts = myMR.getQ2Cuts();
//List<String> nuCuts = myMR.getNuCuts();
String[] Q2Cuts = ["q2>1.0&&q2<1.33","q2>1.33&&q2<1.76","q2>1.76&&q2<4.1"];
String[] nuCuts = ["nu>2.2&&nu<3.2","nu>3.2&&nu<3.73","nu>3.73&&nu<4.25"];

TDirectory dir = new TDirectory();
dir.readFile(fileName);

String[] DirLabel = ["LD2/","Solid/","multiplicity/"];
H1F[][][] h1_nProton = new H1F[Q2Cuts.size()][nuCuts.size()][TgtLabel.size()];
GraphErrors[][] gr_mrProton = new GraphErrors[Q2Cuts.size()][nuCuts.size()];

Q2Cuts.eachWithIndex { nQ2, iQ2->
  nuCuts.eachWithIndex { nNu, iNu->
    DirLabel.eachWithIndex{nDir, iDir->
      if(iDir<2){
        hname = "hYlds_" + TgtLabel[iDir] + "_zh_" + iQ2 + iNu;
        h1_nProton[iQ2][iNu][iDir]= dir.getObject(nDir,hname);
        System.out.println(hname + " " + h1_nProton[iQ2][iNu][iDir].getData().size() + " bins");
        for(int i=0; i<h1_nProton[iQ2][iNu][iDir].getData().size();i++){
          if(h1_nProton[iQ2][iNu][iDir].getDataY(i)>0.0){
            System.out.printf("%d %6g %5e\n ",i+1,h1_nProton[iQ2][iNu][iDir].getDataX(i),h1_nProton[iQ2][iNu][iDir].getDataY(i));
          }
        }
      }else{
        hname = "gr_mrProton_zh_" + iQ2 + iNu;
        gr_mrProton[iQ2][iNu] = dir.getObject(nDir,hname);
        DataVector vecX = gr_mrProton[iQ2][iNu].getVectorX();
        DataVector vecY = gr_mrProton[iQ2][iNu].getVectorY();
        System.out.println(hname + " " + gr_mrProton[iQ2][iNu].getDataSize(0) + " bins");
        for(int i=0; i<vecX.getSize();i++){
          if(vecY.getValue(i)>0.0){
            System.out.printf("%d %6g %3g %5.3g\n ",i+1,vecX.getValue(i),vecY.getValue(i),vecX.getBinWidth(i));
          }
        }
      }
    }
  }
}
