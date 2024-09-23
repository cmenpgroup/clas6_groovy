// eg2Proton_MR3zh_IDsysErr.C
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

void eg2Proton_MR3zh_IDsysErr(vector<string> IDcuts = {"std","sigma_1_0"})
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
    Int_t iPad = 0;
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
    
    string title;
    string xtitle = "z_{h}";
    string ytitle = "R_{P}";
    string ytitleRat = "R_{P}(cut)/R_{P}(std)";
    
    vector<string> Tgt = {"C","Fe","Pb"};
    vector<string> Q2Cuts = {
        "1.0 < Q^{2} < 1.33 GeV^{2}","1.33 < Q^{2} < 1.76 GeV^{2}","1.76 < Q^{2} < 4.1 GeV^{2}"};
    vector<string> nuCuts = {
        "2.2 < #nu < 3.2 GeV","3.2 < #nu < 3.73 GeV","3.73 < #nu < 4.25 GeV"};
    
    gStyle->SetTitleSize(0.1,"t");
    gStyle->SetTitleSize(0.075,"x");
    gStyle->SetTitleSize(0.075,"y");
    gStyle->SetLabelSize(0.06,"x");
    gStyle->SetLabelSize(0.06,"y");
    
    TLegend *legend[Q2Cuts.size()*nuCuts.size()];
    TLegend *legendRat[Q2Cuts.size()*nuCuts.size()];
    TGraphErrors *gr[Q2Cuts.size()][nuCuts.size()][IDcuts.size()][Tgt.size()];
    TGraphErrors *grRat[Q2Cuts.size()][nuCuts.size()][Tgt.size()];
    TMultiGraph *mgr[Q2Cuts.size()*nuCuts.size()];
    TMultiGraph *mgrRat[Q2Cuts.size()*nuCuts.size()];
    
    TCanvas *can = new TCanvas("can","can",900,900);
    can->Divide(Q2Cuts.size(),nuCuts.size());
    TCanvas *canRat = new TCanvas("canRat","canRat",900,900);
    canRat->Divide(Q2Cuts.size(),nuCuts.size());
    
    for(Int_t j=0; j<nuCuts.size(); j++){
        for(Int_t i=0; i<Q2Cuts.size(); i++){
            can->cd(iPad+1);
            gPad->SetLeftMargin(Lmar);
            gPad->SetBottomMargin(Bmar);
            
            legend[iPad] = new TLegend(0.6,0.4,0.85,0.85);
            mgr[iPad] = new TMultiGraph();
            
            iCount = 0;
            for(Int_t k=0; k<IDcuts.size(); k++){
                for(Int_t n=0; n<Tgt.size(); n++){
                    string csvFile = "MR3zh/csvFiles/" + IDcuts.at(k) + "/gr_mrProtonCorr_" + to_string(i) + to_string(j) + "_" + Tgt.at(n) + "_" + IDcuts.at(k) + ".csv";
                    cout << "Analyzing file " << csvFile << endl;
                    gr[i][j][k][n] = new TGraphErrors(csvFile.c_str(),"%lg %lg %lg %lg",",");
                    
                    string grName = "gr" + to_string(i) + to_string(j) + to_string(k) + to_string(n);
                    gr[i][j][k][n]->SetName(grName.c_str());
                    gr[i][j][k][n]->SetMarkerStyle(styleList.at(iCount));
                    gr[i][j][k][n]->SetMarkerColor(colorList.at(iCount));
                    gr[i][j][k][n]->SetMarkerSize(msize);
                    gr[i][j][k][n]->SetLineColor(colorList.at(iCount));
                    gr[i][j][k][n]->SetLineWidth(2);
                    mgr[iPad]->Add(gr[i][j][k][n]);
                    
                    if(k==0){
                        legName = Tgt.at(n);
                    }else{
                        legName = Tgt.at(n) + ", " + cutValues.at(cutIndex) + "#sigma";
                    }
                    legend[iPad]->AddEntry(gr[i][j][k][n],legName.c_str());
                    iCount++;
                }
            }
            title = Q2Cuts.at(i) + " , " + nuCuts.at(j);
            mgr[iPad]->SetTitle(title.c_str());
            mgr[iPad]->GetXaxis()->SetTitle(xtitle.c_str());
            mgr[iPad]->GetXaxis()->CenterTitle();
            mgr[iPad]->GetYaxis()->SetTitle(ytitle.c_str());
            mgr[iPad]->GetYaxis()->CenterTitle();
            mgr[iPad]->GetYaxis()->SetTitleOffset(yoff);
            mgr[iPad]->Draw("AP");
            legend[iPad]->Draw();
            
            canRat->cd(iPad+1);
            gPad->SetLeftMargin(Lmar);
            gPad->SetBottomMargin(Bmar);
            if(iPad==2 && cutIndex==2){
                legendRat[iPad] = new TLegend(0.25,0.25,0.5,0.5);
            }else{
                legendRat[iPad] = new TLegend(0.25,0.625,0.5,0.875);
            }
            mgrRat[iPad] = new TMultiGraph();
            for(Int_t kk=0; kk<Tgt.size(); kk++){
                grRat[i][j][kk] = new TGraphErrors();
                for(Int_t jj=0; jj<gr[i][j][1][kk]->GetN(); jj++){
                    x0 = gr[i][j][0][kk]->GetPointX(jj);
                    x1 = gr[i][j][1][kk]->GetPointX(jj);
                    if(x0==x1){
                        y0 = gr[i][j][0][kk]->GetPointY(jj);
                        y1 = gr[i][j][1][kk]->GetPointY(jj);
                        y0_err = gr[i][j][0][kk]->GetErrorY(jj);
                        y1_err = gr[i][j][1][kk]->GetErrorY(jj);
                        if(y0!=0.0){
                            ratio = y1/y0;
                            ratio_err = ratio*sqrt(pow(y0_err/y0,2) + pow(y1_err/y1,2));
                        }else{
                            ratio = 0.0;
                            ratio_err = 0.0;
                        }
                        grRat[i][j][kk]->AddPoint(x1,ratio);
                        grRat[i][j][kk]->SetPointError(jj,0.0,ratio_err);
                    }
                }
                grRat[i][j][kk]->SetMarkerStyle(styleList.at(kk));
                grRat[i][j][kk]->SetMarkerColor(colorList.at(kk));
                grRat[i][j][kk]->SetMarkerSize(msize);
                grRat[i][j][kk]->SetLineColor(colorList.at(kk));
                grRat[i][j][kk]->SetLineWidth(2);
                mgrRat[iPad]->Add(grRat[i][j][kk]);
                legendRat[iPad]->AddEntry(grRat[i][j][kk],Tgt.at(kk).c_str());
            }
            mgrRat[iPad]->SetTitle(title.c_str());
            mgrRat[iPad]->GetXaxis()->SetTitle(xtitle.c_str());
            mgrRat[iPad]->GetXaxis()->CenterTitle();
            mgrRat[iPad]->GetYaxis()->SetTitle(ytitleRat.c_str());
            mgrRat[iPad]->GetYaxis()->CenterTitle();
            mgrRat[iPad]->GetYaxis()->SetTitleOffset(yoff);
            mgrRat[iPad]->Draw("AP");
            
            legendRat[iPad]->Draw();
            iPad++;
        }
    }
    can->Update();
    pngFile = "eg2Proton_MR3zh_IDsysErr_comp_sig" + cutValues.at(cutIndex) + ".png";
    can->Print(pngFile.c_str());
    
    pngFile = "eg2Proton_MR3zh_IDsysErr_ratio_sig" + cutValues.at(cutIndex) + ".png";
    canRat->Print(pngFile.c_str());
}
