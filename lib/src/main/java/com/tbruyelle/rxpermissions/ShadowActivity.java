package com.tbruyelle.rxpermissions;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

@TargetApi(Build.VERSION_CODES.M)
public class ShadowActivity extends EnsureSameProcessActivity {
    private boolean[] shouldShowRequestPermissionRationale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String[] permissions = intent.getStringArrayExtra("permissions");
        if (permissions == null) {
            finish();
            return;
        }
        try {
            requestPermissions(permissions, 42);
        } catch (NoSuchMethodError error) {
            int[] grantResults = new int[permissions.length];
            shouldShowRequestPermissionRationale = new boolean[permissions.length];

            for (int i = 0; i < permissions.length; i++) {
                grantResults[i] = PackageManager.PERMISSION_GRANTED;
                shouldShowRequestPermissionRationale[i] = false;
            }

            RxPermissions.getInstance(this)
                    .onRequestPermissionsResult(42, permissions, grantResults, shouldShowRequestPermissionRationale);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        shouldShowRequestPermissionRationale = new boolean[permissions.length];

        for (int i = 0; i < permissions.length; i++) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i]);
        }

        RxPermissions.getInstance(this).onRequestPermissionsResult(requestCode, permissions, grantResults, shouldShowRequestPermissionRationale);
        finish();
    }
}
