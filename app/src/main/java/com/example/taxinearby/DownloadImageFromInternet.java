package com.example.taxinearby;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.taxinearby.MapActivity;

import java.io.InputStream;

import models.Company;

import static com.yandex.runtime.Runtime.getApplicationContext;

public class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {

    private Company company;
    @SuppressLint("StaticFieldLeak")
    private MapActivity mapActivity;

    public DownloadImageFromInternet(MapActivity mapActivity) {
        this.mapActivity = mapActivity;
        Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
    }
    protected Bitmap doInBackground(String... urls) {
        String imageURL = urls[0];
        Bitmap bimage = null;
        try {
            InputStream in = new java.net.URL(imageURL).openStream();
            bimage = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            Log.e("Error Message", e.getMessage());
            e.printStackTrace();
        }
        return bimage;
    }

    protected void onPostExecute(Bitmap result) {
        mapActivity.setIcon(result);
    }
}

