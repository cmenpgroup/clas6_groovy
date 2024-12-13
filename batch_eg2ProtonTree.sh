#!/bin/bash

tgtDir=("Carbon" "Iron" "Lead")
cuts=("std" "sigma_1_0" "sigma_1_5" "sigma_2_0" "sigma_2_5" "sigma_3_0")

for((i=0; i<${#tgtDir[@]}; i++))
do
  /Users/wood5/jlab/clas12/clas6_groovy/run_eg2ProtonTree.pl eg2_filtered_Npos_${tgtDir[$i]}.txt $i $1 >& eg2_ProtonTree_${tgtDir[$i]}_${cuts[$1]}.log
done
