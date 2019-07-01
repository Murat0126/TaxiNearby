package com.example.taxinearby;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import models.Company;

public class OpenfreecabsDetails extends AsyncTask<URL, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private MapActivity mapActivity;
    ConverterForOpenFreeCabs converterForOpenFreeCabs;
    private NetworkUtils networkUtils;
    private ArrayList<ServicesTaxiModel> servicesTaxiArrayList;

    OpenfreecabsDetails(MapActivity mapActivity){
        this.mapActivity = mapActivity;
    }

    public void setFreecabsNetWork(NetworkUtils netWork) {
        this.networkUtils = netWork;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(URL... urls) {

        String searchResults = null;

        try{
            searchResults = networkUtils.getResponseFromHttpUrl();
        }catch (IOException e){
            e.printStackTrace();
        }


        return searchResults;
    }
    ArrayList<Company> companies;
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(s != null && !s.equals("")){
            companies = ConverterForOpenFreeCabs.convertToServicesList(s);
            mapActivity.insertPoint(companies);

        }
    }

}
