#!/usr/bin/perl -w

@cuts =("1.0","1.5","2.0","2.5","3.0");
#@MRlabel = ("MR","MR2pT2","MR3zh");
@MRlabel = ("MR");

$binDir = "$ENV{COATJAVA}/bin";
$RunGroovy = "$binDir/run-groovy";

$accDir = "$ENV{HOME}/jlab/clas6/DMS/eg2/proton/Simulations/eg2_proton_sim/plot_acc";

for $i (0 .. $#MRlabel) {
  for $j (0 .. $#cuts) {
    $outfile = "acc_ratio_hists_$MRlabel[$i]_allTgts_sig$cuts[$j].hipo";
    $logfile = $outfile;
    $logfile =~ s/hipo/log/;
    $cmd = "$RunGroovy eg2Proton_$MRlabel[$i]_accHists.groovy -o $outfile -c $cuts[$j] -g 0 $accDir >& $logfile";
    print "$cmd\n\n";
    system($cmd);
  }
}
