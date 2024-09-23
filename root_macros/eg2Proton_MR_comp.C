// eg2Proton_MR_comp.C
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

void eg2Proton_MR_comp(string userSigmaCut = "std")
{
    Float_t Lmar = 0.15;
    Float_t Bmar = 0.15;
    Float_t Rmar = 0.125;
    Float_t xoff = 2.0;
    Float_t yoff = 1.0;
    Int_t mcol = 1;
    Float_t msize = 1.25;
    Int_t mstyle = 20;
    
    string pngFile;
    
    vector<string> nVar = {"q2","nu","zh","pT2","zLC_lo","zLC_hi","phiPQ"};
    vector<string> xtitle = {"Q^{2} (GeV^{2})","#nu (GeV)","z_{h}","p_{T}^{2} (GeV^{2})","z ","z ","#phi_{PQ} (deg.)"};
    string ytitle = "R_{P}";
    
    vector<string> Tgt = {"C","Fe","Pb"};
    vector<double> yMinAcc = {0.9,0.94,0.8,0.0,0.8,0.9,0.94};
    vector<double> yMaxAcc = {2.5,1.15,1.2,1.2,1.05,1.18,1.05};
    vector<double> yMax = {4,4,6,50,10,1.5,10};
    vector<double> legX1 = {0.25,0.25,0.7,0.25,0.7,0.5,0.7};
    vector<double> legX2 = {0.4,0.4,0.85,0.4,0.85,0.65,0.85};
    vector<double> legY1 = {0.2,0.2,0.675,0.65,0.65,0.675,0.65};
    vector<double> legY2 = {0.4,0.4,0.875,0.85,0.85,0.875,0.85};

    vector<double> legX1Acc = {0.25,0.7,0.25,0.25,0.7,0.25,0.7};
    vector<double> legX2Acc = {0.4,0.85,0.4,0.4,0.85,0.4,0.85};
    vector<double> legY1Acc = {0.65,0.65,0.675,0.25,0.25,0.675,0.675};
    vector<double> legY2Acc = {0.85,0.85,0.875,0.4,0.4,0.875,0.875};
    
    gStyle->SetTitleSize(0.05,"t");
    gStyle->SetTitleSize(0.05,"x");
    gStyle->SetTitleSize(0.05,"y");
    gStyle->SetLabelSize(0.035,"x");
    gStyle->SetLabelSize(0.035,"y");
    
    TLegend *legend[nVar.size()];
    TGraphErrors *gr[nVar.size()][Tgt.size()];
    TCanvas *can[nVar.size()];
    
    for(Int_t i=0; i<nVar.size(); i++){
        string cname = "can" + to_string(i);
        string ctitle = "can" + to_string(i);
        can[i] = new TCanvas(cname.c_str(),ctitle.c_str(),600,600);
        gPad->SetLeftMargin(Lmar);
        gPad->SetBottomMargin(Bmar);
        
        legend[i] = new TLegend(legX1.at(i),legY1.at(i),legX2.at(i),legY2.at(i));
        
        for(Int_t j=0; j<Tgt.size(); j++){
            string csvFile = "MR1D/csvFiles/" + userSigmaCut + "/gr_mrProtonCorr_" + nVar.at(i) + "_" + Tgt.at(j) + "_" + userSigmaCut + ".csv";
            cout << "Analyzing file " << csvFile << endl;
            gr[i][j] = new TGraphErrors(csvFile.c_str(),"%lg %lg %lg %lg",",");
            
            string grName = "gr" + to_string(i) + to_string(j);
            gr[i][j]->SetName(grName.c_str());
            gr[i][j]->SetTitle("eg2 Experiment");
            gr[i][j]->SetMarkerStyle(mstyle+j);
            gr[i][j]->SetMarkerColor(mcol+j);
            gr[i][j]->SetMarkerSize(msize);
            gr[i][j]->SetLineColor(mcol+j);
            gr[i][j]->GetXaxis()->SetTitle(xtitle.at(i).c_str());
            gr[i][j]->GetXaxis()->CenterTitle();
            gr[i][j]->GetYaxis()->SetTitle(ytitle.c_str());
            gr[i][j]->GetYaxis()->CenterTitle();
            gr[i][j]->GetYaxis()->SetTitleOffset(yoff);
            gr[i][j]->SetMinimum(0.0);
            gr[i][j]->SetMaximum(yMax.at(i));
            
            legend[i]->AddEntry(gr[i][j],Tgt.at(j).c_str());
            if(j==0){
                gr[i][j]->Draw("AP");
            }else{
                gr[i][j]->Draw("Psame");
            }
        }
        legend[i]->Draw();
        pngFile = "eg2Proton_MR_comp_" + nVar.at(i) + "_" + userSigmaCut + ".png";
        can[i]->Print(pngFile.c_str());
    }
    
    TLegend *legendAcc[nVar.size()];
    TGraphErrors *grAcc[nVar.size()][Tgt.size()];
    TCanvas *canAcc[nVar.size()];
    
    for(Int_t i=0; i<nVar.size(); i++){
        string cname = "canAcc" + to_string(i);
        string ctitle = "canAcc" + to_string(i);
        canAcc[i] = new TCanvas(cname.c_str(),ctitle.c_str(),600,600);
        gPad->SetLeftMargin(Lmar);
        gPad->SetBottomMargin(Bmar);
        
        legendAcc[i] = new TLegend(legX1Acc.at(i),legY1Acc.at(i),legX2Acc.at(i),legY2Acc.at(i));
        
        for(Int_t j=0; j<Tgt.size(); j++){
            string csvFile = "MR1D/csvFiles/" + userSigmaCut + "/gr_rat" + Tgt.at(j) + "_" + nVar.at(i) + "_" + Tgt.at(j) + "_" + userSigmaCut + ".csv";
            cout << "Analyzing file " << csvFile << endl;
            grAcc[i][j] = new TGraphErrors(csvFile.c_str(),"%lg %lg %lg %lg",",");
            
            string grName = "grAcc" + to_string(i) + to_string(j);
            grAcc[i][j]->SetName(grName.c_str());
            grAcc[i][j]->SetTitle("eg2 Experiment");
            grAcc[i][j]->SetMarkerStyle(mstyle+j);
            grAcc[i][j]->SetMarkerColor(mcol+j);
            grAcc[i][j]->SetMarkerSize(msize);
            grAcc[i][j]->SetLineColor(mcol+j);
            grAcc[i][j]->GetXaxis()->SetTitle(xtitle.at(i).c_str());
            grAcc[i][j]->GetXaxis()->CenterTitle();
            grAcc[i][j]->GetYaxis()->SetTitle("Acceptance Ratio");
            grAcc[i][j]->GetYaxis()->CenterTitle();
            grAcc[i][j]->GetYaxis()->SetTitleOffset(yoff);
            grAcc[i][j]->SetMinimum(yMinAcc.at(i));
            grAcc[i][j]->SetMaximum(yMaxAcc.at(i));

            legendAcc[i]->AddEntry(grAcc[i][j],Tgt.at(j).c_str());
            if(j==0){
                grAcc[i][j]->Draw("AP");
            }else{
                grAcc[i][j]->Draw("Psame");
            }
        }
        legendAcc[i]->Draw();
        pngFile = "eg2Proton_MR_comp_" + nVar.at(i) + "_" + userSigmaCut + "_AccRat.png";
        canAcc[i]->Print(pngFile.c_str());
    }

}
