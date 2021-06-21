#!/usr/bin/perl -w

$filelist = $ARGV[0];
$solid = $ARGV[1];

$jawDir = "$ENV{HOME}/jlab/clas12/jaw-2.1/bin";
$RunGroovy = "$jawDir/run-groovy";

open(LIST, "$filelist") || die "\n$0: Could not open $filelist.\n";
while (defined($line=<LIST>)) {
    chomp($line);
    $filename = $line;
    $filename =~ s#.*/##;
    $outfile = $filename;
    $outfile =~ s/run/ntuple/;
    $logfile = $outfile;
    $logfile =~ s/hipo/log/;
    $cmd = "$RunGroovy eg2DISratio.groovy -s $solid -o $outfile $line > $logfile";
    print "$cmd\n";
    system($cmd);
}
close(LIST);
