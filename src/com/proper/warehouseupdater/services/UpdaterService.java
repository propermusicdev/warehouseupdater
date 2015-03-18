package com.proper.warehouseupdater.services;

/**
 * Created by Lebel on 11/11/2014.
 */
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.*;
import android.util.Log;
import android.widget.Toast;
import com.proper.data.diagnostics.LogEntry;
import com.proper.logger.LogHelper;
//import com.proper.messagequeue.HttpMessageResolver;
import com.proper.security.UserLoginResponse;
import com.proper.utils.DeviceUtils;
//import com.proper.utils.FileUtils;
import com.proper.utils.UpdaterFileSorter;
import com.proper.warehouseupdater.R;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lebel on 11/11/2014.
 */
public class UpdaterService extends Service {
    private static final String TAG = UpdaterService.class.getSimpleName();
    private static final long interval = 10800000;  //  every 3 hrs     //7200000; // every 2 hours
    private static final long initialDelay = 3000;//3600000; // 1 hr
    private static final int NOTIFY_UPDATE_FOUND = 1;
    private static final int NOTIFY_UPDATE_NOTFOUND = 2;
    private IBinder binder = null;
    private Context appContext;
    private HandlerThread mWorkerHandlerThread;
    private Handler handler;
    private WifiManager mainWifi;
    private Configurator configurator = null;
    protected int screenSize;
    protected String deviceID = "";
    protected String deviceIMEI = "";
    protected String PATH_TO_APK = "";
    protected java.util.Date utilDate = java.util.Calendar.getInstance().getTime();
    protected java.sql.Timestamp today = null;
    protected DeviceUtils device = null;
    //protected UserAuthenticator authenticator = null;
    protected UserLoginResponse currentUser = null;
    protected LogHelper logger = new LogHelper();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        device = new DeviceUtils(this);
        logger = new LogHelper();
        deviceID = device.getDeviceID();
        deviceIMEI = device.getIMEI();


        //handle our message
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case NOTIFY_UPDATE_NOTFOUND:
                        Toast.makeText(appContext, "Update not available \nYou have the current version", Toast.LENGTH_LONG).show();
                        break;
                    case NOTIFY_UPDATE_FOUND:
                        //TODO - Send notification, Toast and prompt to the target device compelling them = to update
                        promptUpdate();
                        break;
                }
            }
        };
        Toast.makeText(appContext, "Updater Service - Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (!configurator.isRunning) {
//            configurator.setRunning(true);
//        }
        Log.d(TAG, "onStartCommand");
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleWithFixedDelay(new doWorkInBackground(), initialDelay, interval, TimeUnit.MILLISECONDS);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (configurator.isRunning) {
//            configurator.setRunning(false);
//        }
        //unregisterReceiver(wifiReceiver);
        mWorkerHandlerThread.quit();
        mWorkerHandlerThread = null;
        handler = null;
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
        int found = 0;
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for(int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue ;
            }
            if (p.versionName != null & p.packageName.equalsIgnoreCase("com.proper.warehousetools")) {
                PInfo newInfo = new PInfo();
                newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
                newInfo.pname = p.packageName;
                newInfo.versionName = p.versionName;
                newInfo.versionCode = p.versionCode;
                newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
                res.add(newInfo);
                found ++;
            }
            if (found > 0) break;
        }
        return res;
    }

    private void promptUpdate() {
        Vibrator vib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        vib.vibrate(2000);
        String mMsg = "There is a new version of Warehouse Tools";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mMsg)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  TODO - apply some updates, install etc..

                        Intent promptInstall = new Intent(Intent.ACTION_VIEW);
                        promptInstall.setDataAndType(Uri.fromFile(new File(PATH_TO_APK)), "application/vnd.android.package-archive");
                        promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(promptInstall);
                    }
                });
        builder.show();
    }

    public class MyPropertyChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getPropertyName().equalsIgnoreCase("isRunning")) {
                boolean newVal = (Boolean) event.getNewValue();
                if (newVal) {
                    Toast.makeText(UpdaterService.this, "WifiReceiver is initialised", Toast.LENGTH_SHORT).show();
                }
            }
//            if (event.getPropertyName().equalsIgnoreCase("endpointElect")) {
//                ScanResult oldWifi = (ScanResult) event.getOldValue();
//                ScanResult newWifi = (ScanResult) event.getNewValue();
//
//                if (event.getOldValue() != null) {
//                    if (!oldWifi.BSSID.equalsIgnoreCase(newWifi.BSSID)) {
//                        Toast.makeText(BinMoveService.this, String.format("Switching to a stronger WIFI\nNow Connected to: %s\nOn channel: %s",
//                                getEndPointLocation(BinMoveService.this, newWifi.BSSID), getWifiChannel(newWifi.frequency)), Toast.LENGTH_SHORT).show();
//                        UpdateNotifier updater = new UpdateNotifier(event);
//                        updater.run();
//                    }
//                } else {
//                    ScanResult wifiValue = (ScanResult) event.getNewValue();
//                    Toast.makeText(BinMoveService.this, String.format("Switching to a stronger WIFI\nNow Connected to: %s\nOn channel: %s",
//                            getEndPointLocation(BinMoveService.this, wifiValue.BSSID), getWifiChannel(wifiValue.frequency)), Toast.LENGTH_SHORT).show();
//                    UpdateNotifier updater = new UpdateNotifier(event);
//                    updater.run();
//                }
//            }
        }
    }

    class doWorkInBackground implements Runnable {
        protected org.apache.commons.net.ftp.FTPClient ftp = null;
        private Message msg = null;

        @Override
        public void run() {

            AbstractMap.SimpleEntry<Boolean, String> ret = null;
            Resources res = appContext.getResources();
            final UpdaterFileSorter sorter = new UpdaterFileSorter();
            boolean success = false;

            try {
//                if (!mainWifi.isWifiEnabled()) {
//                    mainWifi.setWifiEnabled(true);
//                }
                //Give it some time to work
                Thread.sleep(3000);
                String host = res.getString(R.string.FTP_HOST_EXTERNAL);
                String user = res.getString(R.string.FTP_DEFAULTUSER);
                String pass = res.getString(R.string.FTP_PASSWORD);
                //org.apache.commons.net.ftp.FTPClient ftp = new org.apache.commons.net.ftp.FTPClient();
                if (ftp == null) {
                    ftp = new org.apache.commons.net.ftp.FTPClient();
                    ftp.connect(host);
                    ftp.login(user, pass);
                    ftp.setFileType(FTP.BINARY_FILE_TYPE);
                    ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE); //new
                    ftp.enterLocalPassiveMode();
                }
                String updatesDir = "/WarehouseUpdates/";
                //change directory
                ftp.changeWorkingDirectory(updatesDir);
                //get files
                FTPFile[] files = ftp.listFiles(updatesDir);
                List<FTPFile> fileList = new ArrayList<FTPFile>();
                if (files.length > 1) {
                    for (int i = 0; i < files.length; i ++) {
                        fileList.add(files[i]);
                    }
                    //Sort our list based on signal strength in ascending order
                    Collections.sort(fileList, sorter);
                }
                //get the most recent file
                FTPFile latestUpdateFile = fileList.get(fileList.size() - 1);

                //TODO - Determine if currently installed version is up-to-date

                List<PInfo> installedProgz = getInstalledApps(false);
                //Download File and store it in the download directory
                //File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File downloadDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/warehouseupdates");
                if (!downloadDir.exists()) {
                    downloadDir.mkdir();
                }
                String newFileName = FilenameUtils.concat(downloadDir.getAbsolutePath(), latestUpdateFile.getName());
                File recentFile = new File(newFileName);
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(recentFile));
                //success = ftp.retrieveFile(updatesDir + latestUpdateFile.getName(), outputStream);
                success = ftp.retrieveFile(latestUpdateFile.getName(), outputStream);
                outputStream.close();
                //ftp.logout();
                //ftp.disconnect();

                msg = new Message();
                if (success) {
                    ret = new AbstractMap.SimpleEntry<Boolean, String>(success, newFileName);
                    //String APKFilePath = "mnt/sdcard/myapkfile.apk"; //For example...
                    PATH_TO_APK = newFileName;
                    PackageManager pm = getPackageManager();
                    PackageInfo    pi = pm.getPackageArchiveInfo(PATH_TO_APK, 0);

                    // the secret are these two lines....
                    pi.applicationInfo.sourceDir = downloadDir.getAbsolutePath();
                    pi.applicationInfo.publicSourceDir = downloadDir.getAbsolutePath();

                    Drawable APKicon = pi.applicationInfo.loadIcon(pm);
                    String   AppName = (String)pi.applicationInfo.loadLabel(pm);
                    String AppVersionName = pi.versionName;
                    int AppVersionCode = pi.versionCode;
                    //TODO - Determine if version is higher
                    if (installedProgz.get(0).versionCode < AppVersionCode) {
                        msg.what = NOTIFY_UPDATE_FOUND;
                    } else {
                        msg.what = NOTIFY_UPDATE_NOTFOUND;
                    }
                } else {
                    ret = new AbstractMap.SimpleEntry<Boolean, String>(success, "");
                    msg.what = NOTIFY_UPDATE_NOTFOUND;
                }
                msg.obj = ret;
                handler.sendMessage(msg);   //  Notify updater scheduled check status

            } catch(Exception ex) {
                ex.printStackTrace();
                today = new java.sql.Timestamp(utilDate.getTime());
                LogEntry log = new LogEntry(1L, TAG, "doWorkInBackground - FTP download", deviceIMEI, ex.getClass().getSimpleName(), ex.getMessage(), today);
                //logger.log(log);
            } finally {
                try {
                    if (ftp.isConnected()) {
                        ftp.logout();
                        ftp.disconnect();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class UpdateNotifier implements Runnable {
        private PropertyChangeEvent event;

        UpdateNotifier(PropertyChangeEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
//            ScanResult newWifi = (ScanResult) event.getNewValue();
//            boolean success = connectToAStrongerWIfi(newWifi);
//            if (success) {
//                Toast.makeText(BinMoveService.this, String.format("[[--> Success <--]]\nSwitching to a stronger WIFI\nConnecting to: %s\nOn channel: %s",
//                        getEndPointLocation(BinMoveService.this, newWifi.BSSID), getWifiChannel(newWifi.frequency)), Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(BinMoveService.this, String.format("[[--> Error <--]]\nUnable to connect to: %s\nOn channel: %s",
//                        getEndPointLocation(BinMoveService.this, newWifi.BSSID), getWifiChannel(newWifi.frequency)), Toast.LENGTH_LONG).show();
//            }
        }
    }

    class Configurator {
        private PropertyChangeSupport pcs1;
        private boolean isRunning = false;
        //private ScanResult endpointElect;

        Configurator(Context context) {
            this.pcs1 = new PropertyChangeSupport(context);
        }

        public boolean isRunning() {
            return isRunning;
        }

        public void setRunning(boolean isRunning) {
            this.pcs1.firePropertyChange("isRunning", this.isRunning, isRunning);
            this.isRunning = isRunning;
        }

//        public ScanResult getEndpointElect() {
//            return endpointElect;
//        }
//
//        public void setEndpointElect(ScanResult endpointElect) {
//            this.pcs1.firePropertyChange("endpointElect", this.endpointElect, endpointElect);
//            this.endpointElect = endpointElect;
//        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            this.pcs1.addPropertyChangeListener(listener);
        }
    }

    class PInfo {
        private String appname = "";
        private String pname = "";
        private String versionName = "";
        private int versionCode = 0;
        private Drawable icon;
        private void prettyPrint() {
            //Log.v(appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
            Log.v("TAG", appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
        }
    }
}

