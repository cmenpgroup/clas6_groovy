#!/usr/bin/perl -w

@MR = ("MR","MR2pT2","MR3zh");
@MRdir = ("MR1D","MR2pT2","MR3zh");
@target = ("C","Fe","Pb");
#@tgtDir = ("Carbon","Iron","Lead");
@cuts =("std","sigma_1_0","sigma_1_5","sigma_2_0","sigma_2_5","sigma_3_0");

$jawDir = "$ENV{HOME}/jlab/clas12/jaw-2.1/bin";
$jawRunGroovy = "$jawDir/run-groovy";
#$coatjavaDir = "$ENV{COATJAVA}/bin";
#$coatjavaRunGroovy = "$coatjavaDir/run-groovy";

# create the text files from the graphs files
for $i (0 .. $#MR) {
  $csvDir = "$MRdir[$i]/csvFiles";
  if ( -e $csvDir && -d $csvDir ) {
    print "Directory exists: $csvDir\n";
  }
  else{
    $cmd = "mkdir $csvDir";
    print "$cmd\n\n";
    system($cmd);
  }
  for $j (0 .. $#cuts) {
    $cutDir = "$MRdir[$i]/csvFiles/$cuts[$j]";
    if ( -e $cutDir && -d $cutDir ) {
      print "Directory exists: $cutDir\n";
    }
    else{
      $cmd = "mkdir $cutDir";
      print "$cmd\n";
      system($cmd);
    }
    for $k (0 .. $#target) {
      $logfile = "eg2Proton_$MR[$i]_graph2txt_$target[$k]_$cuts[$j].log";
      $cmd = "$jawRunGroovy eg2Proton_$MR[$i]_graph2txt.groovy -s $target[$k] -c $cuts[$j] > $logfile";
#      print "$cmd\n\n";
#      system($cmd);

      $cmd = "mv -v gr*_$target[$k]_$cuts[$j].csv $MR[$i]/csvFiles/.";
#      print "$cmd\n\n";
#      system($cmd);
    }
  }
}

# create the graph files from the csv text files
for $i (0 .. $#MR) {
  $hipoDir = "$MRdir[$i]/coatjavaHipo";
  if ( -e $hipoDir && -d $hipoDir ) {
    print "Directory exists: $hipoDir\n";
  }
  else{
    $cmd = "mkdir $hipoDir";
    print "$cmd\n";
    system($cmd);
  }
  for $j (0 .. $#cuts) {
    $logfile = "eg2Proton_$MR[$i]_csv2graph_$cuts[$j].log";
    $cmd = "$jawRunGroovy eg2Proton_$MR[$i]_csv2graph.groovy -c $cuts[$j] -g 0 > $logfile";
#    print "$cmd\n\n";
#    system($cmd);

    $cmd = "mv -v eg2Proton_$MR[$i]_corr_hists_*_$cuts[$j]_csv.hipo $hipoDir.";
#  print "$cmd\n\n";
  #      system($cmd);
  }
}
