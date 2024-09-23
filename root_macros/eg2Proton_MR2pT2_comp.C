// eg2Proton_MR2pT2_comp.C
//
// 
// 
// Michael H. Wood, Canisius University
//
//--------------------------------------------------------------
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <string>
#include <vector>

//gROOT->Reset();   // start from scratch

void eg2Proton_MR2pT2_comp(string userSigmaCut = "std", bool zoom = false)
{
    Float_t Lmar = 0.15;
    Float_t Bmar = 0.15;
    Float_t Rmar = 0.125;
    Float_t xoff = 2.0;
    Float_t yoff = 1.0;
    Int_t mcol = 1;
    Float_t msize = 1.0;
    Int_t mstyle = 20;
    
    string pngFile;
    
    string xtitle = "p_{T}^{2} (GeV^{2})";
    string ytitle = "R_{P}";
    
    vector<string> Tgt = {"C","Fe","Pb"};
    vector<string> zhCuts = {
        "0.3 < z_{h} < 0.4","0.4 < z_{h} < 0.5","0.5 < z_{h} < 0.6",
        "0.6 < z_{h} < 0.7","0.7 < z_{h} < 0.8","0.8 < z_{h} < 0.9",
        "0.9 < z_{h} < 1.0","1.0 < z_{h} < 1.1","1.1 < z_{h} < 1.2"};
    vector<double> yMax = {90,90,100,30,30,40,40,15,15};
    vector<double> yZoom = {10,10,10,6,6,6,6,6,6};
    
    gStyle->SetTitleSize(0.07,"t");
    gStyle->SetTitleSize(0.065,"x");
    gStyle->SetTitleSize(0.065,"y");
    gStyle->SetLabelSize(0.055,"x");
    gStyle->SetLabelSize(0.055,"y");
    
    TLegend *legend[zhCuts.size()];
    TGraphErrors *gr[zhCuts.size()][Tgt.size()];
    
    TCanvas *can = new TCanvas("can","can",900,900);
    can->Divide(3,3);
    for(Int_t i=0; i<zhCuts.size(); i++){
        can->cd(i+1);
        gPad->SetLeftMargin(Lmar);
        gPad->SetBottomMargin(Bmar);
        
        legend[i] = new TLegend(0.25,0.6,0.5,0.85);
        
        for(Int_t j=0; j<Tgt.size(); j++){
            string csvFile = "MR2pT2/csvFiles/std/gr_mrProtonCorr_pT2_" + to_string(i) + "_" + Tgt.at(j) + "_" + userSigmaCut + ".csv";
            cout << "Analyzing file " << csvFile << endl;
            gr[i][j] = new TGraphErrors(csvFile.c_str(),"%lg %lg %lg %lg",",");
            
            string grName = "gr" + to_string(i) + to_string(j);
            gr[i][j]->SetName(grName.c_str());
            gr[i][j]->SetTitle(zhCuts.at(i).c_str());
            gr[i][j]->SetMarkerStyle(mstyle+j);
            gr[i][j]->SetMarkerColor(mcol+j);
            gr[i][j]->SetMarkerSize(msize);
            gr[i][j]->SetLineColor(mcol+j);
            gr[i][j]->GetXaxis()->SetTitle(xtitle.c_str());
            gr[i][j]->GetXaxis()->CenterTitle();
            gr[i][j]->GetYaxis()->SetTitle(ytitle.c_str());
            gr[i][j]->GetYaxis()->CenterTitle();
            gr[i][j]->GetYaxis()->SetTitleOffset(yoff);
            gr[i][j]->SetMinimum(-1.0);
            if(zoom){
                gr[i][j]->SetMaximum(yZoom.at(i));
            }else{
                gr[i][j]->SetMaximum(yMax.at(i));
            }
            legend[i]->AddEntry(gr[i][j],Tgt.at(j).c_str());
            if(j==0){
                gr[i][j]->Draw("AP");
            }else{
                gr[i][j]->Draw("Psame");
            }
        }
        legend[i]->Draw();
    }
    if(zoom){
        pngFile = "eg2Proton_MR2pT2_comp_" + userSigmaCut + "_zoom.png";
    }else{
        pngFile = "eg2Proton_MR2pT2_comp_" + userSigmaCut + ".png";
    }
    can->Print(pngFile.c_str());
}
