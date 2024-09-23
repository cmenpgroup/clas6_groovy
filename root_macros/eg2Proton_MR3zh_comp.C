// eg2Proton_MR3zh_comp.C
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

void eg2Proton_MR3zh_comp(string userSigmaCut = "std")
{
    Float_t Lmar = 0.15;
    Float_t Bmar = 0.15;
    Float_t Rmar = 0.125;
    Float_t xoff = 2.0;
    Float_t yoff = 1.0;
    Int_t mcol = 1;
    Float_t msize = 1.0;
    Int_t mstyle = 20;
    Int_t iCount = 0;
    
    string pngFile;
    
    string xtitle = "z_{h}";
    string ytitle = "R_{P}";
    string title;
    
    vector<string> Tgt = {"C","Fe","Pb"};
    vector<string> Q2Cuts = {
        "1.0 < Q^{2} < 1.33 GeV^{2}","1.33 < Q^{2} < 1.76 GeV^{2}","1.76 < Q^{2} < 4.1 GeV^{2}"};
    vector<string> nuCuts = {
        "2.2 < #nu < 3.2 GeV","3.2 < #nu < 3.73 GeV","3.73 < #nu < 4.25 GeV"};
    vector<double> yMax = {12,14,16,10,14,16,5,6,9};
    vector<double> yMaxTgt = {4,8,12,4.5,9,14,5.5,11,16};
    
    gStyle->SetTitleSize(0.1,"t");
    gStyle->SetTitleSize(0.075,"x");
    gStyle->SetTitleSize(0.075,"y");
    gStyle->SetLabelSize(0.06,"x");
    gStyle->SetLabelSize(0.06,"y");
    
    TLegend *legend[Q2Cuts.size()*nuCuts.size()];
    TGraphErrors *gr[Q2Cuts.size()][nuCuts.size()][Tgt.size()];
    
    TCanvas *can1 = new TCanvas("can1","can1",900,900);
    can1->Divide(3,3);
    for(Int_t j=0; j<nuCuts.size(); j++){
        for(Int_t i=0; i<Q2Cuts.size(); i++){
            can1->cd(iCount+1);
            gPad->SetLeftMargin(Lmar);
            gPad->SetBottomMargin(Bmar);
        
            legend[iCount] = new TLegend(0.5,0.6,0.8,0.85);
            title = Q2Cuts.at(i) + " , " + nuCuts.at(j);
            
            for(Int_t k=0; k<Tgt.size(); k++){
                string csvFile = "MR3zh/csvFiles/std/gr_mrProtonCorr_" + to_string(i) + to_string(j) + "_" + Tgt.at(k) + "_" + userSigmaCut + ".csv";
                cout << "Analyzing file " << csvFile << endl;
                gr[i][j][k] = new TGraphErrors(csvFile.c_str(),"%lg %lg %lg %lg",",");
                
                string grName = "gr" + to_string(i) + to_string(j) + to_string(k);
                gr[i][j][k]->SetName(grName.c_str());
                gr[i][j][k]->SetTitle(title.c_str());
                gr[i][j][k]->SetMarkerStyle(mstyle+k);
                gr[i][j][k]->SetMarkerColor(mcol+k);
                gr[i][j][k]->SetMarkerSize(msize);
                gr[i][j][k]->SetLineColor(mcol+k);
                gr[i][j][k]->GetXaxis()->SetTitle(xtitle.c_str());
                gr[i][j][k]->GetXaxis()->CenterTitle();
                gr[i][j][k]->GetYaxis()->SetTitle(ytitle.c_str());
                gr[i][j][k]->GetYaxis()->CenterTitle();
                gr[i][j][k]->GetYaxis()->SetTitleOffset(yoff);
                gr[i][j][k]->SetMinimum(-0.1);
                gr[i][j][k]->SetMaximum(yMax.at(iCount));

                legend[iCount]->AddEntry(gr[i][j][k],Tgt.at(k).c_str());
                if(k==0){
                    gr[i][j][k]->Draw("AP");
                }else{
                    gr[i][j][k]->Draw("Psame");
                }
            }
            legend[iCount]->Draw();
            iCount++;
        }
    }
    can1->Update();
    pngFile = "eg2Proton_MR3zh_compNu_" + userSigmaCut + ".png";
    can1->Print(pngFile.c_str());
    
    iCount = 0; // reset the canvas pad counter
    TLegend *legend2[Q2Cuts.size()*Tgt.size()];
    TCanvas *can2 = new TCanvas("can2","can2",900,900);
    can2->Divide(3,3);
    for(Int_t i=0; i<Q2Cuts.size(); i++){
        for(Int_t k=0; k<Tgt.size(); k++){
            can2->cd(iCount+1);
            gPad->SetLeftMargin(Lmar);
            gPad->SetBottomMargin(Bmar);
        
            legend2[iCount] = new TLegend(0.325,0.6,0.875,0.875);
            title = Tgt.at(k) + ": " + Q2Cuts.at(i);

            for(Int_t j=0; j<nuCuts.size(); j++){
                gr[i][j][k]->SetTitle(title.c_str());
                gr[i][j][k]->SetMarkerStyle(mstyle+j);
                gr[i][j][k]->SetMarkerColor(mcol+j);
                gr[i][j][k]->SetMarkerSize(msize);
                gr[i][j][k]->SetLineColor(mcol+j);
                gr[i][j][k]->GetXaxis()->SetTitle(xtitle.c_str());
                gr[i][j][k]->GetXaxis()->CenterTitle();
                gr[i][j][k]->GetYaxis()->SetTitle(ytitle.c_str());
                gr[i][j][k]->GetYaxis()->CenterTitle();
                gr[i][j][k]->GetYaxis()->SetTitleOffset(yoff);
                gr[i][j][k]->SetMinimum(-0.1);
                gr[i][j][k]->SetMaximum(yMaxTgt.at(iCount));

                legend2[iCount]->AddEntry(gr[i][j][k],nuCuts.at(j).c_str());
                if(j==0){
                    gr[i][j][k]->Draw("AP");
                }else{
                    gr[i][j][k]->Draw("Psame");
                }
            }
            legend2[iCount]->Draw();
            iCount++;
        }
    }
    pngFile = "eg2Proton_MR3zh_compTgt_" + userSigmaCut + ".png";
    can2->Print(pngFile.c_str());
}
