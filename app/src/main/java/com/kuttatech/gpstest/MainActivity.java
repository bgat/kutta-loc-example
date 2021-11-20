package com.kuttatech.gpstest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.kuttatech.gpstest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("gpstest");
    }

    private ActivityMainBinding binding;
    private LocationService loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Get permission to access location, if necessary.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        // Launch our location provider. We probably shouldn't do this until we are sure
        // we've been granted permission, but it's expedient to ignore that detail for now.
        loc = new LocationService(MainActivity.this);

        // Another example of a call to a native method, which in this case
        // produces display output. Makes sure everything fits together properly.
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
    }

    // Callback telling us if our request for a permission was successful, or not.
    @Override
    public void onRequestPermissionsResult (int requestCode,
                                                     String[] permissions,
                                                     int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "granted", Toast.LENGTH_SHORT) .show();
        }
        else {
            Toast.makeText(MainActivity.this, "DENIED", Toast.LENGTH_SHORT) .show();
        }
    }

    // Example "native method", see native-lib.cpp for the implementation.
    public native String stringFromJNI();
}