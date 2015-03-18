package com.proper.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * Created by Lebel on 06/08/2014.
 */
public class DeviceUtils {
    private Context context = null;
    protected TelephonyManager manager = null;

    public DeviceUtils(Context context) {
        this.context = context;
        this.manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public String getDeviceID() {
        return Build.MANUFACTURER;
    }

    public String getIMEI(){
        String generated = "";
        generated = manager.getDeviceId();
        if (generated == null || generated.contains("amsung")) {
            generated = String.format("Samsung %s, %s", Build.MODEL, Build.SERIAL);
        }
        return generated;
    }
}
