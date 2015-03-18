package com.proper.warehouseupdater;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import com.proper.warehouseupdater.services.UpdaterService;

import java.util.List;

/**
 * Created by Lebel on 25/11/2014.
 */
public class AppContext extends Application {
    protected static Context context = null;
    protected BootCompletedIntentReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Intent pushIntent = new Intent(context, UpdaterService.class);
        receiver = new BootCompletedIntentReceiver();
        //registerReceiver(receiver, Intent.ACTION_BOOT_COMPLETED);
        context.startService(pushIntent);
    }

    public static Context getContext() {
        return context;
    }

    public boolean isProcessRunning(String process)
    {
        ActivityManager activityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++){
            if(procInfos.get(i).processName.equals(process)) {
                return true;
            }
        }
        return false;
    }
}
