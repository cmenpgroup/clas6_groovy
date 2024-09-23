// eg2Proton_MR_IDsysErr.C
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

void eg2Proton_MR_IDsysErr(vector<string> IDcuts = {"std","sigma_1_0"})
{
    Float_t Lmar = 0.15;
    Float_t Bmar = 0.15;
    Float_t Rmar = 0.125;
    Float_t xoff = 2.0;
    Float_t yoff = 1.0;
    vector<int> colorList = {1,2,3,4,7,8};
    Float_t msize = 1.25;
    vector<int> styleList = {20,21,22,24,25,26};
    Int_t iCount = 0;
    Int_t cutIndex = 0;
    
    vector<string> cutList = {"std","sigma_1_0","sigma_1_5","sigma_2_0","sigma_2_5","sigma_3_0"};
    vector<string> cutValues = {"2.0","1.0","1.5","2.0","2.5","3.0"};
    
    vector<string>::iterator itr = find(cutList.begin(), cutList.end(), IDcuts.at(1));
    if (itr != cutList.cend()) {
        cutIndex = distance(cutList.begin(), itr);
        cout << "Element present at index " << cutIndex << endl;
    }else{
        cout << "No element " << IDcuts.at(1) << " found in list of ID cuts" << endl;
        exit(0);
    }
    
    string legName;
    string pngFile;
    double x0, x1, y0, y1, y0_err, y1_err, ratio, ratio_err;
    
    vector<string> nVar = {"q2","nu","zh","pT2","zLC_lo","zLC_hi","phiPQ"};
    vector<string> xtitle = {"Q^{2} (GeV^{2})","#nu (GeV)","z_{h}","p_{T}^{2} (GeV^{2})","z ","z ","#phi_{PQ} (deg.)"};
    string ytitle = "R_{P}";
    string ytitleRat = "R_{P}(cut)/R_{P}(std)";
    
    vector<string> Tgt = {"C","Fe","Pb"};
    vector<double> legX1 = {0.25,0.4,0.7,0.25,0.7,0.5,0.7};
    vector<double> legX2 = {0.4,0.55,0.85,0.4,0.85,0.65,0.85};
    vector<double> legY1 = {0.175,0.375,0.675,0.65,0.65,0.675,0.65};
    vector<double> legY2 = {0.375,0.575,0.875,0.85,0.85,0.875,0.85};

    vector<double> legX1Rat = {0.25,0.7,0.25,0.25,0.7,0.25,0.7};
    vector<double> legX2Rat = {0.4,0.85,0.4,0.4,0.85,0.4,0.85};
    vector<double> legY1Rat = {0.65,0.65,0.675,0.25,0.25,0.675,0.675};
    vector<double> legY2Rat = {0.85,0.85,0.875,0.4,0.4,0.875,0.875};
    
    gStyle->SetTitleSize(0.05,"t");
    gStyle->SetTitleSize(0.05,"x");
    gStyle->SetTitleSize(0.05,"y");
    gStyle->SetLabelSize(0.035,"x");
    gStyle->SetLabelSize(0.035,"y");
    
    TLegend *legend[nVar.size()];
    TLegend *legendRat[nVar.size()];
    TGraphErrors *gr[nVar.size()][IDcuts.size()][Tgt.size()];
    TGraphErrors *grRat[nVar.size()][Tgt.size()];
    TMultiGraph *mgr[nVar.size()];
    TMultiGraph *mgrRat[nVar.size()];
    
    TCanvas *can[nVar.size()];
    
    for(Int_t i=0; i<nVar.size(); i++){
        string cname = "can" + to_string(i);
        string ctitle = "can" + to_string(i);
        can[i] = new TCanvas(cname.c_str(),ctitle.c_str(),1200,600);
        can[i]->Divide(2,1);
        can[i]->cd(1);
        gPad->SetLeftMargin(Lmar);
        gPad->SetBottomMargin(Bmar);
        
        legend[i] = new TLegend(legX1.at(i),legY1.at(i),legX2.at(i),legY2.at(i));
        mgr[i] = new TMultiGraph();
        
        iCount = 0;
        for(Int_t j=0; j<IDcuts.size(); j++){
            for(Int_t k=0; k<Tgt.size(); k++){
                string csvFile = "MR1D/csvFiles/" + IDcuts.at(j) + "/gr_mrProtonCorr_" + nVar.at(i) + "_" + Tgt.at(k) + "_" + IDcuts.at(j) + ".csv";
                cout << "Analyzing file " << csvFile << endl;
                gr[i][j][k] = new TGraphErrors(csvFile.c_str(),"%lg %lg %lg %lg",",");
                
                string grName = "gr" + to_string(i) + to_string(j) + to_string(k);
                gr[i][j][k]->SetName(grName.c_str());
                gr[i][j][k]->SetMarkerStyle(styleList.at(iCount));
                gr[i][j][k]->SetMarkerColor(colorList.at(iCount));
                gr[i][j][k]->SetMarkerSize(msize);
                gr[i][j][k]->SetLineColor(colorList.at(iCount));
                gr[i][j][k]->SetLineWidth(2);
                mgr[i]->Add(gr[i][j][k]);
                
                if(j==0){
                    legName = Tgt.at(k);
                }else{
                    legName = Tgt.at(k) + ", " + cutValues.at(cutIndex) + "#sigma";
                }
                legend[i]->AddEntry(gr[i][j][k],legName.c_str());
                iCount++;
            }
        }
        mgr[i]->SetTitle("eg2 Experiment");
        mgr[i]->GetXaxis()->SetTitle(xtitle.at(i).c_str());
        mgr[i]->GetXaxis()->CenterTitle();
        mgr[i]->GetYaxis()->SetTitle(ytitle.c_str());
        mgr[i]->GetYaxis()->CenterTitle();
        mgr[i]->GetYaxis()->SetTitleOffset(yoff);
        mgr[i]->Draw("AP");
        legend[i]->Draw();

        can[i]->cd(2);
        gPad->SetLeftMargin(Lmar);
        gPad->SetBottomMargin(Bmar);
        legendRat[i] = new TLegend(legX1Rat.at(i),legY1Rat.at(i),legX2Rat.at(i),legY2Rat.at(i));
        mgrRat[i] = new TMultiGraph();
        for(Int_t kk=0; kk<Tgt.size(); kk++){
            grRat[i][kk] = new TGraphErrors();
            for(Int_t jj=0; jj<gr[i][1][kk]->GetN(); jj++){
                x0 = gr[i][0][kk]->GetPointX(jj);
                x1 = gr[i][1][kk]->GetPointX(jj);
                if(x0==x1){
                    y0 = gr[i][0][kk]->GetPointY(jj);
                    y1 = gr[i][1][kk]->GetPointY(jj);
                    y0_err = gr[i][0][kk]->GetErrorY(jj);
                    y1_err = gr[i][1][kk]->GetErrorY(jj);
                    if(y0!=0.0){
                        ratio = y1/y0;
                        ratio_err = ratio*sqrt(pow(y0_err/y0,2) + pow(y1_err/y1,2));
                    }else{
                        ratio = 0.0;
                        ratio_err = 0.0;
                    }
                    grRat[i][kk]->AddPoint(x1,ratio);
                    grRat[i][kk]->SetPointError(jj,0.0,ratio_err);
                }
            }
            grRat[i][kk]->SetMarkerStyle(styleList.at(kk));
            grRat[i][kk]->SetMarkerColor(colorList.at(kk));
            grRat[i][kk]->SetMarkerSize(msize);
            grRat[i][kk]->SetLineColor(colorList.at(kk));
            grRat[i][kk]->SetLineWidth(2);
            mgrRat[i]->Add(grRat[i][kk]);
            legendRat[i]->AddEntry(grRat[i][kk],Tgt.at(kk).c_str());
        }
        mgrRat[i]->SetTitle("eg2 Experiment");
        mgrRat[i]->GetXaxis()->SetTitle(xtitle.at(i).c_str());
        mgrRat[i]->GetXaxis()->CenterTitle();
        mgrRat[i]->GetYaxis()->SetTitle(ytitleRat.c_str());
        mgrRat[i]->GetYaxis()->CenterTitle();
        mgrRat[i]->GetYaxis()->SetTitleOffset(yoff);
        mgrRat[i]->Draw("AP");
        
        legendRat[i]->Draw();
        
        pngFile = "eg2Proton_MR_IDsysErr_" + nVar.at(i) + "_sig" + cutValues.at(cutIndex) + ".png";
        can[i]->Print(pngFile.c_str());
    }
}
