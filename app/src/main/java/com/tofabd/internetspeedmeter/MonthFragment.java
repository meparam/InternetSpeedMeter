package com.tofabd.internetspeedmeter;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MonthFragment extends Fragment {

    final static String MEGABYTE = " MB", GIGABYTE = " GB";

    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();
    private Handler vHandler = new Handler();

    Thread dataUpdate;
    private TextView wTotal, mTotal, tTotal;
    static int m = 1;


    private double total_wifi;
    private double total_mobile;

    private double today_wifi = 0;
    private double today_mobile = 0;

    private DataAdapter dataAdapter;


    List<DataInfo> monthData;


    public MonthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_month, container, false);


        wTotal = (TextView) rootView.findViewById(R.id.id_wifi);
        mTotal = (TextView) rootView.findViewById(R.id.id_mobile);
        tTotal = (TextView) rootView.findViewById(R.id.id_total);

        final RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        recList.getItemAnimator().setChangeDuration(0);

        monthData = createList(30);
        dataAdapter = new DataAdapter(monthData);
        recList.setAdapter(dataAdapter);
        totalData();


//        doubleBackToExitPressedOnce = false;


        sharedPref();
//
        clearExtraData();
         Log.e("astatus", "hi");


//        if (!DataService.service_status) {
//            Intent intent = new Intent(getActivity(), DataService.class);
//            startService(intent);
//        }
//
//
//        Intent intentBC = new Intent();
//        intentBC.setAction("com.tofabd.internetmeter");
//        sendBroadcast(intentBC);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        liveData();

        // Log.e("todaytime", df.format(c.getTime()));
        //Log.e("astatus getState",dataUpdate.getState().toString());

       /* dataUpdate = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!dataUpdate.getName().equals("stopped")) {

                    vHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            monthData.set(0, todayData());
                            dataAdapter.notifyItemChanged(0);
                            //Log.e("dhaka", toString().valueOf(total_wifi));

                            //totalData();

                            totalData(); //call main thread
                            Log.e("monthdata", toString().valueOf(total_wifi));


                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //  progressStatus--;
                }

            }
        });

        dataUpdate.setName("started");

        //   Log.e("astatus getState main",dataUpdate.getState().toString());
        //    Log.e("astatus main isAlive",Boolean.toString(dataUpdate.isAlive()));
        dataUpdate.start();*/


/*

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                monthData.set(0, todayData());
                dataAdapter.notifyItemChanged(0);
                Log.e("dhaka", toString().valueOf(total_wifi));

                totalData();
                Log.e("dhaka5","testing");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

*/


        //  Log.e("astatus getState main",dataUpdate.getState().toString());
        //  Log.e("astatus main isAlive",Boolean.toString(dataUpdate.isAlive()));

        //startActivity(new Intent(MainActivity.this,SettingsActivity.class));


        return rootView;


    }


    public void liveData(){
        dataUpdate = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!dataUpdate.getName().equals("stopped")) {

                    vHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            monthData.set(0, todayData());
                            dataAdapter.notifyItemChanged(0);
                            //Log.e("dhaka", toString().valueOf(total_wifi));

                            //totalData();

                            totalData(); //call main thread
                            Log.e("monthdata", toString().valueOf(total_wifi));


                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //  progressStatus--;
                }

            }
        });

        dataUpdate.setName("started");

        //   Log.e("astatus getState main",dataUpdate.getState().toString());
        //    Log.e("astatus main isAlive",Boolean.toString(dataUpdate.isAlive()));
        dataUpdate.start();

    }

    /**
     * @param size of lists
     * @return list of data of last 30 days
     */
    public List<DataInfo> createList(int size) {

        List<DataInfo> result = new ArrayList<>();
        //total_mobile_wifi = 0;
        total_wifi = 0;
        total_mobile = 0;

        double wTemp, mTemp, tTemp;
        DecimalFormat df = new DecimalFormat("#.##");

        String wifi = "0", mobile = "0", total = "0";
        SharedPreferences sp_month = getActivity().getSharedPreferences("monthdata", Context.MODE_PRIVATE);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

        for (int i = 1; i <= size; i++) {
            if (i == 1) {
                result.add(todayData());
                continue;
            }

            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DATE, (1 - i)); // day decrease to get previous day

            String mDate = sdf.format(ca.getTime());// get  date
            List<String> allData = new ArrayList<>();

            //check date availabe or not
            if (sp_month.contains(mDate)) {

                String sData = sp_month.getString(mDate, null); // get saved data
                try {

                    JSONObject jOb = new JSONObject(sData);
                    wifi = jOb.getString("WIFI_DATA");
                    mobile = jOb.getString("MOBILE_DATA");
                    // total = jOb.getString("TOTAL_DATA");
                    wTemp = (Long.parseLong(wifi) / 1048576.0);
                    mTemp = (Long.parseLong(mobile) / 1048576.0);
                    //tTemp = (double) Long.parseLong(total) / 1000000.0;

                    tTemp = wTemp + mTemp;
                    allData = dataFormate(wTemp, mTemp, tTemp);
                    //     Log.e("outside", Integer.toString(i) + " " + wifi + mobile + Double.toString(wTemp));
                    //count for total
                    total_wifi += wTemp;
                    total_mobile += mTemp;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                allData = dataFormate(0, 0, 0);
            }

            DataInfo dataInfo = new DataInfo();
            dataInfo.date = mDate;
            dataInfo.wifi = allData.get(0);
            dataInfo.mobile = allData.get(1);
            dataInfo.total = allData.get(2);

            result.add(dataInfo);
        }

        return result;
    }

    public DataInfo todayData() {

        List<DataInfo> listToday = new ArrayList<>();

        double wTemp, mTemp, tTemp;
        SharedPreferences sp = getActivity().getSharedPreferences("todaydata", Context.MODE_PRIVATE);
        // convert to megabyte
        wTemp = sp.getLong("WIFI_DATA", 0) / 1048576.0;
        mTemp = sp.getLong("MOBILE_DATA", 0) / 1048576.0;
        tTemp = wTemp + mTemp;

        List<String> allData = dataFormate(wTemp, mTemp, tTemp);

        total_wifi = total_wifi + (wTemp - today_wifi);
        total_mobile = total_mobile + (mTemp - today_mobile);

        today_wifi = wTemp;
        today_mobile = mTemp;
        //total_mobile_wifi = total_wifi + total_mobile;
        // Calendar ca = Calendar.getInstance();
        // String mDate = sdf.format(ca.getTime());// get today's date

        DataInfo dataInfo = new DataInfo();

        dataInfo.date = "Today";
        dataInfo.wifi = allData.get(0);
        dataInfo.mobile = allData.get(1);
        dataInfo.total = allData.get(2);

        listToday.add(dataInfo);

        Log.e("dhaka", dataInfo.wifi + dataInfo.mobile + dataInfo.total);

        return dataInfo;

    }

    public void totalData() {

        List<String> total = dataFormate(total_wifi, total_mobile, total_wifi + total_mobile);

        wTotal.setText(total.get(0));
        mTotal.setText(total.get(1));
        tTotal.setText(total.get(2));

    }

    public List<String> dataFormate(double wifi, double mobile, double total) {

        List<String> allData = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.##");
        //check less than Gigabyte or not
        if (wifi < 1024) {
            allData.add(df.format(wifi) + MEGABYTE); // consider 2 value after decimal point
        } else {
            allData.add(df.format(wifi / 1024) + GIGABYTE);
        }

        if (mobile < 1024) {
            allData.add(df.format(mobile) + MEGABYTE); // consider 2 value after decimal point
        } else {
            allData.add(df.format(mobile / 1024) + GIGABYTE);
        }

        if (total < 1024) {
            allData.add(df.format(total) + MEGABYTE); // consider 2 value after decimal point
        } else {
            allData.add(df.format(total / 1024) + GIGABYTE);
        }

        return allData;

    }

    void clearExtraData() {

        SharedPreferences sp_month = getActivity().getSharedPreferences("monthdata", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp_month.edit();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

        for (int i = 40; i <= 1000; i++) {
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DATE, (1 - i));
            String mDate = sdf.format(ca.getTime());// get  date

            if (sp_month.contains(mDate)) {
                editor.remove(mDate);

            }
        }
        editor.apply();

    }

    // populate data
    public void sharedPref() {
        SharedPreferences sp_month = getActivity().getSharedPreferences("monthdata", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp_month.edit();
        editor.clear();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

        for (int i = 1; i <= 30; i++) {
            if (i % 2 == 1)
                continue;

            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DATE, (1 - i));

            try {
                String tDate = sdf.format(ca.getTime());// get today's date
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("WIFI_DATA", i * 10000906);
                jsonObject.put("MOBILE_DATA", i * 40005002);
                //jsonObject.put("TOTAL_DATA", i * 50050006);

                editor.putString(tDate, jsonObject.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        editor.apply();

    }

    @Override
    public void onPause() {
        super.onPause();
        dataUpdate.setName("stopped");

        Log.e("astatus", "onPause");
        //  Log.e("astatus getState",dataUpdate.getState().toString());
        //finish();
    }


    @Override
    public void onResume() {
        super.onResume();

        DataService.notification_status = true;

        dataUpdate.setName("started");
        Log.e("astatus", "onResume");
        Log.e("astatus", dataUpdate.getState().toString());

        //dataUpdate.start();

        // Log.e("astatus getState",dataUpdate.getState().toString());
        // Log.e("astatus isAlive",Boolean.toString(dataUpdate.isAlive()));
        if (!dataUpdate.isAlive()) {
            //dataUpdate.run();
            liveData();

        }
        //dataUpdate.start();


    }
/*    @Override
    public void onRestart() {
        super.onRestart();
         Log.e("astatus","onRestart");
    }*/


    @Override
    public void onDestroy() {
        super.onDestroy();
         Log.e("astatus","onDestroy");
    }

    @Override
    public void onStart() {
        super.onStart();
         Log.e("astatus","onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
          Log.e("astatus","onStop");
    }


}
