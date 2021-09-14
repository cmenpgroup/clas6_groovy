import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;
import org.jlab.jnp.pdg.PDGDatabase;
import org.jlab.jnp.pdg.PDGParticle;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

double x, y, err;
String[] str;

int[][] nbins = [[37,28,24],[37,28,24],[34,29,23]];
double[][] xlo = [[0.3,0.25,0.225],[0.3,0.25,0.225],[0.3,0.25,0.25]];
double[][] xhi = [[1.225,0.95,0.825],[1.225,0.95,0.825],[1.15,0.975,0.825]];

H1F[][] h1_acc = new H1F[3][3];

for(int i=0; i<3;i++){
  for(int j=0; j<3;j++){
    String hname = "acc_ratio_C_" + i + j;
    h1_acc[i][j] = new H1F(hname,"z_h","Ratio",nbins[i][j],xlo[i][j],xhi[i][j]);
    h1_acc[i][j].setTitle("eg2 - Acceptance Ratio");

    int iQ2 = i+1;
    int iNu = j+1;
    String accFile = "/Users/wood5/jlab/clas12/eg2_proton_acceptance/MR3zh/acc_ratio_hists_C_" + iQ2 + iNu +".txt";
    println "Analyzing " + accFile;
    new File(accFile).eachLine { line ->
      str = line.split('\t');
      str.eachWithIndex{ val, ival->
        switch(ival){
          case 0: x = Double.parseDouble(val); break;
          case 1: y = Double.parseDouble(val); break;
          case 2: err = Double.parseDouble(val); break;
          default: break;
        }
      }
      if(y>0){
        h1_acc[i][j].fill(x,1.0/y);
      }else{
        h1_acc[i][j].fill(x,0.0);
      }
    }
  }
}

def dirname = ["/Carbon"];
TDirectory dir = new TDirectory();
dirname.each { val->
  dir.mkdir(val);
}

int iCount = 0;
int c1_title_size = 22;
TCanvas c1 = new TCanvas("c1",900,900);
c1.divide(3,3);

dir.cd(dirname[0]);
for(int i=0; i<3;i++){
  for(int j=0; j<3;j++){
    c1.cd(iCount);
    c1.getPad().setTitleFontSize(c1_title_size);
    c1.draw(h1_acc[i][j]);
    dir.addDataSet(h1_acc[i][j]); // add to the histogram file
    iCount++;
  }
}

String histFile = "acc_ratio_hists_C.hipo";
dir.writeFile(histFile);
