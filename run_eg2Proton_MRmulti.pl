#!/usr/bin/perl -w

$iMR = $ARGV[0];
$IDcut = $ARGV[1];

@MR = ("MR","MR2pT2","MR3zh");
@target = ("C","Fe","Pb");
@tgtDir = ("Carbon","Iron","Lead");
@cuts =("std","sigma_1_0","sigma_1_5","sigma_2_0","sigma_2_5","sigma_3_0");

$jawDir = "$ENV{HOME}/jlab/clas12/jaw-2.1/bin";
$RunGroovy = "$jawDir/run-groovy";

for $i (0 .. $#target) {
    # create hipo file with uncorrected multiplicity ratios
    $ntupleDir = "$ENV{HOME}/jlab/clas-data/eg2/Npos/$tgtDir[$i]/ntuples_protonID_sysErr/$cuts[$IDcut]";
    $infile = "$ntupleDir/eg2Proton_jawTree_$MR[$iMR]_$target[$i]_Hists_$cuts[$IDcut].hipo";
    $outfile = "eg2Proton_AnaTree_$MR[$iMR]_$target[$i]_Hists_$cuts[$IDcut].hipo";
    $logfile = $outfile;
    $logfile =~ s/hipo/log/;
    $cmd = "$RunGroovy eg2Proton_$MR[$iMR]_jawHists.groovy -s $target[$i] -o $outfile $infile > $logfile";
    print "$cmd\n\n";
    system($cmd);

    # create hipo file with corrected multiplicity ratios
    $infile = $outfile;
    $outfile = "eg2Proton_$MR[$iMR]_corr_hists_$target[$i]_$cuts[$IDcut].hipo";
    $logfile = $outfile;
    $logfile =~ s/hipo/log/;
    $cmd = "$RunGroovy eg2Proton_$MR[$iMR]_corr.groovy -s $target[$i] -o $outfile $infile > $logfile";
    print "$cmd\n\n";
    system($cmd);
}
