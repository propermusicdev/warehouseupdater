package com.proper.warehouseupdater;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.proper.warehouseupdater.services.UpdaterService;

/**
 * Created by Lebel on 26/11/2014.
 */
public class ActMain extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(this, "Updater Service has Sted")
        Intent serviceIntent = new Intent(this, UpdaterService.class);
        startService(serviceIntent);
        this.finish();
    }
}