#!/usr/bin/perl -w

$filelist = $ARGV[0];
$itgt = $ARGV[1];
$IDcut = $ARGV[2];

@target = ("C","Fe","Pb");
@tgtDir = ("Carbon","Iron","Lead");
@cuts =("std","sigma_1_0","sigma_1_5","sigma_2_0","sigma_2_5","sigma_3_0");

$jawDir = "$ENV{HOME}/jlab/clas12/jaw-2.1/bin";
$RunGroovy = "$jawDir/run-groovy";

$outDir = "$ENV{HOME}/jlab/clas-data/eg2/Npos/@tgtDir[$itgt]/ntuples_protonID_sysErr/@cuts[$IDcut]";

open(LIST, "$filelist") || die "\n$0: Could not open $filelist.\n";
while (defined($line=<LIST>)) {
    chomp($line);
    $filename = $line;
    $filename =~ s#.*/##;
    $outfile = $filename;
    $outfile =~ s/run/ntuple/;
    $logfile = $outfile;
    $logfile =~ s/hipo/log/;
    $cmd = "$RunGroovy eg2ProtonTree.groovy -s $target[$itgt] -P $IDcut -o $outfile $line > $logfile";
    print "$cmd\n";
    system($cmd);
    $cmd = "mv -v $outfile $outDir/.";
    print "$cmd\n";
    system($cmd);
    $cmd = "mv -v $logfile $outDir/.";
    system($cmd);
}
close(LIST);

