import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.groot.tree.*;
//import org.jlab.groot.data.*;
//import org.jlab.groot.ui.*;
//import org.jlab.groot.tree.*; // new import for ntuples
//import org.jlab.groot.base.GStyle;

TreeFileWriter tree = new TreeFileWriter("ntuple.hipo","T","a:b:c");
float[] data = new double[3];

for(int i = 0; i < 100; i++){
  data[0] = 1.5;
  data[1] = 2.5*i;
  data[2] = 3.5*Math.pow(i,2);
  tree.addRow(data);
}
tree.close();
