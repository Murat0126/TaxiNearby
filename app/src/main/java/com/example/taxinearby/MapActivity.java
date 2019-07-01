package com.example.taxinearby;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.runtime.image.ImageProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import android.app.AlertDialog;
import models.Company;

public class MapActivity extends Activity  implements MapObjectTapListener, LocationListener {

    private final String MAPKIT_API_KEY = "********************************";
    private final Point DRAGGABLE_PLACEMARK_CENTER = new Point(42.8497, 74.5971691);
    private MapView mapView;
    private MapObject mapObjects;
    private ArrayList<Company> companies;
    private UserLocationLayer userLocationLayer;

    private String phone;
    private String sms;
    private String type;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_map);
        super.onCreate(savedInstanceState);
        mapView = (MapView) findViewById(R.id.mapview);
        mapObjects = mapView.getMap().getMapObjects();


        mapObjects.addTapListener(this);

        getLocation();

    }


    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        OpenfreecabsDetails openfreecabsDetails = new OpenfreecabsDetails(this);
        NetworkUtils networkUtils = new NetworkUtils();
        networkUtils.setLocation(latitude, longitude);
        mapView.getMap().move(new CameraPosition(new Point(latitude, longitude), 16, 0, 0));openfreecabsDetails.setFreecabsNetWork(networkUtils);
        openfreecabsDetails.execute();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MapActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    public  void insertPoint(ArrayList<Company> arrayList) {
        companies = arrayList;
        for(Company company: arrayList) {
            createMapObjects(company);
        }
    }
    public void setIcon(Bitmap icon){
        ImageProvider.fromBitmap(icon);
    }

    private void createMapObjects(Company company) {

        ArrayList<Point> points = company.drivers;
        Object userDate = null;
        userDate = company;

        IconStyle iconStyle = new IconStyle().setAnchor(new PointF(1.0f, 1.0f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(1.0f);

        for(Point point : points){
            PlacemarkMapObject placemarkMapObject = mapView.getMap().getMapObjects().addPlacemark (point, getIcon(company.name), iconStyle);
            placemarkMapObject.setUserData(userDate);
        }
    }

    ImageProvider getIcon(String name){
        switch (name){
            case "NambaTaxi":
                return ImageProvider.fromResource(this, R.drawable.namba);
            case "SmsTaxi":
                return ImageProvider.fromResource(this, R.drawable.logo_sms);
            default:
                return ImageProvider.fromResource(this, R.drawable.question);
        }
    }

    @Override
    public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {

            Object user;
            user =  mapObject.getUserData();
            Company company = (Company) user;
            getContacts(company);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_frame, null);
            TextView smsTextView = view.findViewById(R.id.sms_number);
            TextView phoneTextView = view.findViewById(R.id.phone_number);
            TextView nameTextView = view.findViewById(R.id.name);
            ImageView icon = view.findViewById(R.id.icon);
            Button callButton = view.findViewById(R.id.call_button);
            Button cancelButton = view.findViewById(R.id.cancel_button);
            Button sendSmsButton = view.findViewById(R.id.sendsms_button);


            icon.setImageResource(getIconId(company.name));
            getContacts(company);
            smsTextView.setText(sms);
            phoneTextView.setText(phone);
            nameTextView.setText(company.name);
            builder.setView(view);
            final AlertDialog alert = builder.create();
            alert.show();

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(phone)) {
                    String dial = "tel:" + phone;
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                }else {
                    Toast.makeText(MapActivity.this, "Enter a phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sendSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(sms)) {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + sms));
                    smsIntent.putExtra("sms_body", "");
                    startActivity(smsIntent);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });

        return false;

    }

    public void getContacts(Company company){
        type = company.contacts.get(0).contactNumber[0];

        switch (type){
            case "sms":
                sms = company.contacts.get(0).contactNumber[1];
            case "phone":
                phone = company.contacts.get(1).contactNumber[1];
            default:
                break;
        }
    }

    int getIconId(String name){
        switch (name){
            case "NambaTaxi":
                return (R.drawable.namba);
            case "SmsTaxi":
                return  (R.drawable.logo_sms);
            default:
                return (R.drawable.question);
        }
    }
























    Point roundOff(Point point)
    {

        double latitude = point.getLatitude();
        double longitude = point.getLongitude();
        BigDecimal  bigDecimal = new BigDecimal(latitude);
        BigDecimal latit = bigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP);

         bigDecimal = new BigDecimal(longitude);
        BigDecimal longit = bigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP);

        double lat = latit.doubleValue();
        double lon = longit.doubleValue();

        Point driver = new Point(lon, lat);

        return  driver;

    }

}