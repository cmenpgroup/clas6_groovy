import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.physics.*;
import org.jlab.jnp.pdg.PhysicsConstants;
import org.jlab.jnp.pdg.PDGDatabase;
import org.jlab.jnp.pdg.PDGParticle;

import eg2AnaTree.*;

import org.jlab.groot.base.GStyle
import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;

GStyle.getAxisAttributesX().setTitleFontSize(18);
GStyle.getAxisAttributesY().setTitleFontSize(18);
GStyle.getAxisAttributesX().setLabelFontSize(18);
GStyle.getAxisAttributesY().setLabelFontSize(18);
GStyle.getAxisAttributesZ().setLabelFontSize(18);

def cli = new CliBuilder(usage:'eg2DISratio_comp.groovy')
cli.h(longOpt:'help', 'Print this message.')

def options = cli.parse(args);
if (!options) return;
if (options.h){ cli.usage(); return; }

HistInfo myHI = new HistInfo();
List<String> solidTgt = myHI.getSolidTgtLabel();

String[] Var = ["q2","nu"];
String[] xLabel = ["Q^2 (GeV^2)","#nu (GeV)"];

int c1_title_size = 22;
TCanvas[] can = new TCanvas[Var.size()];
TDirectory[] dir = new TDirectory[solidTgt.size()];
solidTgt.eachWithIndex { nTgt, iTgt ->
  String fileName = "eg2DIS_AnaTree_" + nTgt + "_Hists.hipo";
  println fileName;
  dir[iTgt] = new TDirectory();
  dir[iTgt].readFile(fileName);
}

GraphErrors[][] gr_rNorm = new GraphErrors[Var.size()][solidTgt.size()];

Var.eachWithIndex { nVar, iVar->
  String cname = "can" + iVar;
  can[iVar] = new TCanvas(cname,600,600);
  can[iVar].cd(0);
  can[iVar].getPad().setTitleFontSize(c1_title_size);

  solidTgt.eachWithIndex { nTgt, iTgt ->
    String gr_eNorm = "gr_rDIS_" + nVar;
    gr_rNorm[iVar][iTgt] = dir[iTgt].getObject("multiplicity/",gr_eNorm);
    gr_rNorm[iVar][iTgt].setTitle("CLAS6 - eg2");
    gr_rNorm[iVar][iTgt].setTitleX(xLabel[iVar]);
    gr_rNorm[iVar][iTgt].setTitleY("N_e(" + nTgt + ")/N_e(D)");
    gr_rNorm[iVar][iTgt].setMarkerColor(iTgt+1);
    gr_rNorm[iVar][iTgt].setLineColor(iTgt+1);
    gr_rNorm[iVar][iTgt].setMarkerSize(5);
    if(iTgt==0){
      can[iVar].draw(gr_rNorm[iVar][iTgt]);
    }else{
      can[iVar].draw(gr_rNorm[iVar][iTgt],"same");
    }
  }
}
