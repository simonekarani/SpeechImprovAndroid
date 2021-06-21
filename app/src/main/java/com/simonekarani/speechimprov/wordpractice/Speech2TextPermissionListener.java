package com.simonekarani.speechimprov.wordpractice;

import android.widget.Toast;

import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class Speech2TextPermissionListener implements PermissionListener {

    private final WordPracticeActivity activity;

    public Speech2TextPermissionListener(WordPracticeActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
        Toast.makeText(this.activity, "Permission Granted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {
        Toast.makeText(this.activity, "Permission Denied", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                             PermissionToken token) {
        Toast.makeText(this.activity, "Permission Rationale", Toast.LENGTH_LONG).show();
    }
}