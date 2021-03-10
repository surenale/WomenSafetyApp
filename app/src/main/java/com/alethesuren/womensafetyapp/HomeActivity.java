package com.alethesuren.womensafetyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.alethesuren.womensafetyapp.EditActivity.CONTACT_BOOK_PREFERENCE;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final int phone_request_permission = 123;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private double latitude, longitude;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_home);
        initializeGoogleApiClient();
        askForPhonePermission();

        ImageView contactBook = findViewById(R.id.contact);
        contactBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ContactBookActivity.class);
                startActivity(i);
            }

        });

        //fetch the data from the sharedpreferences when the reporting button is pressed for 2 second

        Button reportingBtn = findViewById(R.id.reportingBtn);
        reportingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = getSharedPreferences(EditActivity.CONTACT_BOOK_PREFERENCE, Context.MODE_PRIVATE);

                String keys = sharedpreferences.getString("keys", "no-value-found");
                if (keys.equals("no-value-found") || keys.equals("")) {
                    Toast.makeText(HomeActivity.this, "Contact is not found please save contact", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String[] kNums = keys.split(",");
                    for (String kNum : kNums) {
                        String data = sharedpreferences.getString(kNum, "no-value-found");
                        if (data.equals("") || data.equals("no-value-found")) {
                            continue;
                        }

                        String[] dataArray = data.split(",");
                        String smFlag = dataArray[3];

                        if (smFlag.equals("true")) {
                            try {
                                SmsManager smgr = SmsManager.getDefault();
                                ArrayList<String> parts = smgr.divideMessage("I'm in trouble please help me !! https://www.google.com/maps/place/@" + latitude + "," + longitude + ",17z/data=!3m1!4b1!4m5!3m4!1s0x39eb19c18b827885:0x4207ecf181978d2e!8m2!3d27.6770118!4d85.3327667" + "\n Reporting date: " + getDate());
                                smgr.sendMultipartTextMessage(kNum, null, parts, null, null);
                                Toast.makeText(getBaseContext(), "Press Detected", Toast.LENGTH_SHORT).show();
                                Toast.makeText(HomeActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(HomeActivity.this, "SMS Failed to Send, Please try again" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + kNums[0]));
                    startActivity(callIntent);

                }

            }
        });
    }

    private String getDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        return formatter.format(date);
    }

    private void initializeGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(phone_request_permission)
    private void askForPhonePermission() {
        String[] perms = {Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS};
        if (EasyPermissions.hasPermissions(this, perms)) {
//         nothing to do
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.request_phone_call),
                    phone_request_permission, perms);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
            Log.d("location:::", "lat:" + latitude + " long:" + longitude);
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("TAG", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("TAG", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}