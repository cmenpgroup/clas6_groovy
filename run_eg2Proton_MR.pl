#!/usr/bin/perl -w

$IDcut = $ARGV[0];

@target = ("C","Fe","Pb");
@tgtDir = ("Carbon","Iron","Lead");
@cuts =("std","sigma_1_0","sigma_1_5","sigma_2_0","sigma_2_5","sigma_3_0");

$jawDir = "$ENV{HOME}/jlab/clas12/jaw-2.1/bin";
$RunGroovy = "$jawDir/run-groovy";

$accPath = "/Users/wood5/jlab/clas12/eg2_proton_acceptance";

#for ($i = 0; $i < @target; $i = $i + 1) {
for $i (0 .. $#target) {
    # create hipo file with uncorrected multiplicity ratios
    $ntupleDir = "$ENV{HOME}/jlab/clas-data/eg2/Npos/$tgtDir[$i]/ntuples_protonID_sysErr/$cuts[$IDcut]";
    $infile = "$ntupleDir/eg2Proton_jawTree_MR_$target[$i]_Hists_$cuts[$IDcut].hipo";
    $outfile = "eg2Proton_AnaTree_MR_$target[$i]_Hists_$cuts[$IDcut].hipo";
    $logfile = $outfile;
    $logfile =~ s/hipo/log/;
    $cmd = "$RunGroovy eg2Proton_MR_jawHists.groovy -s $target[$i] -o $outfile $infile > $logfile";
    print "$cmd\n\n";
    system($cmd);

    # create hipo file with corrected multiplicity ratios
    $infile = $outfile;
    $outfile = "eg2Proton_MRcorr_hists_$target[$i]_$cuts[$IDcut].hipo";
    $logfile = $outfile;
    $logfile =~ s/hipo/log/;
    $cmd = "$RunGroovy eg2Proton_MRcorr.groovy -s $target[$i] -p $accPath -o $outfile $infile > $logfile";
    print "$cmd\n\n";
    system($cmd);
}
