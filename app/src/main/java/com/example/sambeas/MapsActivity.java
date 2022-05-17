package com.example.sambeas;
import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Environment.DIRECTORY_MUSIC;


import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    Button sendSms;

    // creating a variable for media recorder object class.

    private static final int MICROPHONE_PERMISSION_CODE = 200;
    final String TAG = "MainAc";
    List<String> fileList = new ArrayList<>(),fileList2 = new ArrayList<>();
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    RecyclerView recyclerView;

    private final static int RECORD_TIME = 5000;



    // creating a variable for mediaPlayer class


    // string variable is created for storing a file name
    private static String mFileName = null;

    // constant for storing audio permission
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private int requestCode;
    private String[] permissions;
    private int[] grantResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        if (isMicrophonePresent()) {
            getMicrophonePermission();
//            updateRecycler();
        }
        Log.d(TAG, "onCreate: "+getRecordingFilePath());


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//            public void btnStopPressed(View view) {
//                updateRecycler();
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;

                Toast.makeText(getApplicationContext(),"Recording is stopped", Toast.LENGTH_SHORT).show();
            }

        },RECORD_TIME);


        sendSms = findViewById(R.id.senMessageButton);

        getSupportActionBar().setTitle("Google Map Sms");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendingSms();
            }
        });
    }


    public void sendingSms(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+2347010599438", null, "https://www.google.com/maps/dir/?api=1&destination=lat,lng&quot", null, null);
                Toast.makeText(getApplicationContext(), "SMS SENT", Toast.LENGTH_LONG).show();

        Log.d(TAG, "btnRecordPressed: "+getRecordingFilePath());
//        updateRecycler();
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecordingFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

            Toast.makeText(this,"Recording is started", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000); // two minute interval
        mLocationRequest.setFastestInterval(60000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                        Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                    Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //move map camera
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latLng.latitude, latLng.longitude)).zoom(16).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {
                    // if not allow a permission, the application will exit
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    System.exit(0);
                }
            }
        }
        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (permissionToRecord && permissionToStore) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }







//
//    if (isMicrophonePresent()) {
//        getMicrophonePermission();
//        updateRecycler();
//    }
//        Log.d(TAG, "onCreate: "+getRecordingFilePath());
//
//
//    new Handler().postDelayed(new Runnable() {
//        @Override
//        public void run() {
////            public void btnStopPressed(View view) {
//            updateRecycler();
//            mediaRecorder.stop();
//            mediaRecorder.release();
//            mediaRecorder = null;
//
//            Toast.makeText(getApplicationContext(),"Recording is stopped", Toast.LENGTH_SHORT).show();
//        }
//
//    },RECORD_TIME);
//
//}

//    void updateRecycler(){
//        String path = getExternalFilesDir(DIRECTORY_MUSIC).getAbsolutePath();
//        File directory = null;
//
//        try {
//            directory = new File(path);
//        } catch (Exception e) {
//            Log.d(TAG, "btnRecordPressed: " + e);
//        }
//
//        assert directory != null;
//        File[] files = directory.listFiles();
//        assert files != null;
//        for (File file : files) {
//            fileList2.add(file.getName());
//        }
//
//        Adapter  adapter = new Adapter(fileList2);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(adapter);
//    }

//    public void btnRecordPressed(View view) {
//        Log.d(TAG, "btnRecordPressed: "+getRecordingFilePath());
////        updateRecycler();
//        try {
//            mediaRecorder = new MediaRecorder();
//            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            mediaRecorder.setOutputFile(getRecordingFilePath());
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            mediaRecorder.prepare();
//            mediaRecorder.start();
//
//            Toast.makeText(this,"Recording is started", Toast.LENGTH_SHORT).show();
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//    }



//    public void btnStopPressed(View view) {
//        updateRecycler();
//        mediaRecorder.stop();
//        mediaRecorder.release();
//        mediaRecorder = null;
//
//        Toast.makeText(this,"Recording is stopped", Toast.LENGTH_SHORT).show();
//    }

    public void btnPlayPressed(View view) {
//        updateRecycler();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getRecordingFilePath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            Toast.makeText(this,"Recording is playing", Toast.LENGTH_SHORT).show();
        }

        catch (Exception e){
            e.printStackTrace();
        }

    }

    private boolean isMicrophonePresent() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            return true;
        } else {
            return false;
        }
    }

    private void getMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
    }

    private String getRecordingFilePath() {

        List<Integer> ints = new ArrayList<>();

        String child = "testRecordingFile";
//        String output = "0123456789";

        String path = getExternalFilesDir(DIRECTORY_MUSIC).getAbsolutePath();
        File directory = null;

        try {
            directory = new File(path);
        } catch (Exception e) {
            Log.d(TAG, "btnRecordPressed: " + e);
        }

        assert directory != null;
        File[] files = directory.listFiles();
        Log.i(TAG, "getFileListInFolder:files " + Arrays.toString(files));
        assert files != null;
        for (File file : files) {
            fileList.add(file.getName());
            Log.i(TAG, "FileName:" + file.getName());
        }

        for (int i = 0; i < fileList.size(); i++) {
            String input = fileList.get(i);
            if(input.contains(child)){
                String x = input.replace(child,"");
                String y = x.replace(".mp3","");
                ints.add(Integer.parseInt(y));
            }
        }
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File Directory = contextWrapper.getExternalFilesDir(DIRECTORY_MUSIC);
        File file = new File(Directory, child+ (getMax(ints)+1) + ".mp3");
        Log.d(TAG, "getRecordingFilePath: "+file.getPath()+" "+getMax(ints));

        return file.getPath();
    }
    public static Integer getMax(List<Integer> list)
    {
        if (list == null || list.size() == 0) {
            return 0;
        }

        return Collections.max(list);
    }
    
}


