package com.proper.warehouseupdater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import com.proper.warehouseupdater.services.UpdaterService;

import java.io.File;

/**
 * Created by Lebel on 26/11/2014.
 */
public class ActMain extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(this, "Updater Service has Started")
        if (getIntent().getAction().equalsIgnoreCase("Notify")) {
            SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_credentials), MODE_PRIVATE);
            boolean notified = prefs.getBoolean("notified", false);
            if (notified) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("notified", false);
                editor.commit();
            }
            updatePrograms(getIntent().getStringExtra("PATH"));
            this.finish();
        }else{
            Intent serviceIntent = new Intent(this, UpdaterService.class);
            startService(serviceIntent);
            this.finish();
        }
    }

    private void updatePrograms(String path) {
        Intent promptInstall = new Intent(Intent.ACTION_VIEW);
        promptInstall.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(promptInstall);
    }
}