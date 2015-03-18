package com.proper.warehouseupdater;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.proper.warehouseupdater.services.UpdaterService;

import java.util.List;

/**
 * Created by Lebel on 25/11/2014.
 */
public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String myApp = "com.proper.warehousetools";
        //AppContext appContext;
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if (isProcessRunning(context, myApp)) {
                Intent pushIntent = new Intent(context, UpdaterService.class);
                context.startService(pushIntent);
            }
        }
    }

    public boolean isProcessRunning(Context context, String process)
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++){
            if(procInfos.get(i).processName.equals(process)) {
                return true;
            }
        }
        return false;
    }
}
