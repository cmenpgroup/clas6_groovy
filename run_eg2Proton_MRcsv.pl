#!/usr/bin/perl -w

@MR = ("MR","MR2pT2","MR3zh");
@MRdir = ("MR1D","MR2pT2","MR3zh");
@cuts =("std","sigma_1_0","sigma_1_5","sigma_2_0","sigma_2_5","sigma_3_0");

$coatjavaDir = "$ENV{COATJAVA}/bin";
$coatjavaRunGroovy = "$coatjavaDir/run-groovy";

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
    $cmd = "$coatjavaRunGroovy eg2Proton_$MR[$i]_csv2graph.groovy -c $cuts[$j] -g 0 > $logfile";
    print "$cmd\n\n";
    system($cmd);

    $cmd = "mv -v eg2Proton_$MR[$i]_corr_hists_*_$cuts[$j]_csv.hipo $hipoDir";
    print "$cmd\n\n";
    system($cmd);

    $cmd = "mv -v $logfile $hipoDir";
    print "$cmd\n\n";
    system($cmd);
  }
}
