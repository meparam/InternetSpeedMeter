package com.tofabd.internetspeedmeter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class DataService extends Service {


    static NotificationManager notificationManager;

    protected static boolean service_status = false;
    protected static boolean notification_status = true;

    Context context;


    public static final String TODAY_DATA = "todaydata";
    public static final String MONTH_DATA = "monthdata";

    public DataService() {
    }

    Thread dataThread;


    int nid = 5000;
    static int k = 0;


    final class MyThreadClass implements Runnable {

        int service_id;

        MyThreadClass(int service_id) {
            this.service_id = service_id;

        }

        @Override
        public void run() {
            int i = 0;
            synchronized (this) {
                while (dataThread.getName() == "showNotification") {
                    //  Log.e("insidebroadcast", Integer.toString(service_id) + " " + Integer.toString(i));
                    getData();
                    try {
                        wait(1000);
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                //stopSelf(service_id);
            }

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        SharedPreferences sp_day = getSharedPreferences(TODAY_DATA, Context.MODE_PRIVATE);

        //check today_date empty or not
        //if not then create pref key by date
        if (!sp_day.contains("today_date")) {

            SharedPreferences.Editor editor_day = sp_day.edit();

            Calendar ca = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            String tDate = sdf.format(ca.getTime());// get today's date

            editor_day.putString("today_date", tDate);

            editor_day.commit();


        }

        if (!service_status) {
            service_status = true;
            dataThread = new Thread(new MyThreadClass(startId));
            dataThread.setName("showNotification");
            dataThread.start();

        }


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service_status = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void getData() {
        long mobileData, totalData, wifiData, saved_mobileData, saved_wifiData, saved_totalData, receiveData;

        String saved_date, tDate;
        List<Long> allData;

        String network_status = NetworkUtil.getConnectivityStatusString(getApplicationContext());

        //if (!network_status.equals("no_connection")) {
        //receiveData = RetrieveData.findData();
        allData = RetrieveData.findData();

        receiveData = allData.get(0) + allData.get(1);


        if (notification_status) {
            showNotification(receiveData);
        }

        wifiData = 0;
        mobileData = 0;
        totalData = 0;

        if (network_status.equals("wifi_enabled")) {
            totalData = receiveData;
            wifiData = receiveData;


        } else if (network_status.equals("mobile_enabled")) {
            totalData = receiveData;
            mobileData = receiveData;
        }


        // Log.e("dhaka", Long.toString(totalData) + " " + Long.toString(wifiData) + " " + Long.toString(mobileData));


        // mobileData = mobileData;

        Calendar ca = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        tDate = sdf.format(ca.getTime());// get today's date


        SharedPreferences sp_day = getSharedPreferences(TODAY_DATA, Context.MODE_PRIVATE);

        // get containg date by "today_date" key
        saved_date = sp_day.getString("today_date", "empty");

        //Log.e("today",saved_date);


        //check today's date
        if (saved_date.equals(tDate)) {


            //get today's saved data
            //saved_totalData = sp_day.getLong("TOTAL_DATA", 0);
            saved_mobileData = sp_day.getLong("MOBILE_DATA", 0);
            saved_wifiData = sp_day.getLong("WIFI_DATA", 0);


            SharedPreferences.Editor day_editor = sp_day.edit();
            // editor.putString("today", tDate);
            //update data
            //day_editor.putLong("TOTAL_DATA", totalData + saved_totalData);
            day_editor.putLong("MOBILE_DATA", mobileData + saved_mobileData);
            day_editor.putLong("WIFI_DATA", wifiData + saved_wifiData);

            day_editor.commit();

            //Log.e("today", Long.toString(saved_totalData + totalData));

        } else {


            try {

                JSONObject jsonObject = new JSONObject();

                //.put("today_data",sharedpreferences.getString("today_date",null));

                //save data as a json object
                jsonObject.put("WIFI_DATA", sp_day.getLong("WIFI_DATA", 0));
                jsonObject.put("MOBILE_DATA", sp_day.getLong("MOBILE_DATA", 0));
                //jsonObject.put("TOTAL_DATA", sp_day.getLong("TOTAL_DATA", 0));

                SharedPreferences sp_month = getSharedPreferences(MONTH_DATA, Context.MODE_PRIVATE);
                SharedPreferences.Editor month_editor = sp_month.edit();

                // previous day's data save to monthdata preference
                month_editor.putString(saved_date, jsonObject.toString());
                month_editor.commit();

                SharedPreferences.Editor day_editor = sp_day.edit();

                //update new date by tDate
                day_editor.clear();
                day_editor.putString("today_date", tDate);
                day_editor.commit();


            } catch (Exception e) {
                e.printStackTrace();

            }


        }


        // }


    }

    public void showNotification(long receiveData) {


        List<String> connStatus = NetworkUtil.getConnectivityInfo(getApplicationContext());

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences dataPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Boolean notification_state = dataPref.getBoolean("notification_state", true);


        String wifi_mobile_details = getWifiMobileData();


        //used to find icon
        String s = "0";

        if (receiveData < 1024) {
            int show_data = (int) (receiveData / 1024) * 10;  //0.1KB/s  to 0.9KB/s
            s = "b" + show_data;

        } else if (receiveData >= 1024 && receiveData < 1048576) {// range 1KB to 999KB
            int show_data = (int) receiveData / 1024;   //convert byte to KB to make seial
            s = "k" + show_data; //make icon serial

            Log.e("dhaka2k",s);

        } else if (receiveData >= 1048576 && receiveData < 10485760) {//range 1MB to 9.9MB

            int show_data = (int) (receiveData / 104857.6);   // it means (int)((receiveData / 1048576)*10)

            s = "m" + show_data;
            Log.e("dhaka2",s);
        } else if (receiveData >= 10485760 && receiveData <= 20971520) {
            int show_data = (int) receiveData / 1048576;
            s = "mm" + show_data;
        } else if (receiveData > 20971520) {
            s = "mmm" + "20";
        }


//        //make speed by icon
//        if (show_data <= 300) {
//            s = "a" + show_data;
//        } else {
//            //  s = "a".concat(Integer.toString(221));
//            s = "a1000";
//        }

        //int dataid = getResources().getIdentifier(s, "drawable", getPackageName());
        int data_icon = getResources().getIdentifier(s, "drawable", getPackageName());
        String network_name = "";

        if (connStatus.get(0).equals("wifi_enabled")) {
            network_name = connStatus.get(1) + " " + connStatus.get(2);

        } else if (connStatus.get(0).equals("mobile_enabled")) {
            network_name = connStatus.get(1);
        } else {
            network_name = "";
        }
        DecimalFormat df = new DecimalFormat("#.##");

        String speed = "";
        if (receiveData < 1024) {
            speed = "Speed " + (int) receiveData + " B/s" + " " + network_name;

        } else if (receiveData < 1048576) {

            speed = "Speed " + (int) receiveData / 1024 + " KB/s" + " " + network_name;

        } else {
            speed = "Speed " + df.format(receiveData / 1048576) + " MB/s" + " " + network_name;

        }

        //Log.e("astatus","notification status");
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        Notification notification = new Notification.Builder(this)
                .setContentTitle(speed)
                .setContentText(wifi_mobile_details)
                .setSmallIcon(data_icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setOngoing(true)
                .setWhen(0)
                .build();


        // Log.e("hello notification", Integer.toString(k));
        // Log.e("estatus ",Long.toString(receiveData));

//        try {

//
        if (notification_state) {
            notificationManager.notify(nid, notification);
        } else {

            notificationManager.cancel(nid);
        }
//        }catch(InterruptedException e){
//            e.printStackTrace();
//            //Log.e("exception ",Integer.toString(data_icon));
//
//
//        }


        // k++;
        // }

    }


    public String getWifiMobileData() {

        SharedPreferences sp_day = getSharedPreferences("todaydata", Context.MODE_PRIVATE);

        // long saved_totalData = sharedpreferences.getLong("TOTAL_DATA", 0);
        long saved_mobileData = sp_day.getLong("MOBILE_DATA", 0);
        long saved_wifiData = sp_day.getLong("WIFI_DATA", 0);

        DecimalFormat df = new DecimalFormat("#.##");

        double wifi_data = (double) saved_wifiData / 1048576.0;
        double mobile_data = (double) saved_mobileData / 1048576.0;

        String wifi_today, mobile_today;

        //check Megabyte or Gigabyte
        if (wifi_data < 1024) {
            wifi_today = "Wifi: " + df.format(wifi_data) + "MB  ";
        } else {
            wifi_today = "Wifi: " + df.format(wifi_data / 1024) + "GB  ";  //convert to Gigabyte

        }

        if (mobile_data < 1024) {
            mobile_today = " Mobile: " + df.format(mobile_data) + "MB";
        } else {
            mobile_today = " Mobile: " + df.format(mobile_data / 1024) + "GB";

        }

        String wifi_mobile = wifi_today + mobile_today;


        return wifi_mobile;
    }

}
