package com.tofabd.internetspeedmeter;


import android.app.ActionBar;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

    List<DataInfo> monthData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


  /*      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (!DataService.service_status) {
            Intent intent = new Intent(this, DataService.class);
            startService(intent);
        }

        Intent intentBC = new Intent();
        intentBC.setAction("com.tofabd.internetmeter");
        sendBroadcast(intentBC);

        Fragment fragment = new MonthFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();


   /*     wTotal = (TextView) findViewById(R.id.id_wifi);
        mTotal = (TextView) findViewById(R.id.id_mobile);
        tTotal = (TextView) findViewById(R.id.id_total);

        //dataUpdate.setName("started");

        final RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        recList.getItemAnimator().setChangeDuration(0);

        monthData = createList(30);
        final DataAdapter dataAdapter = new DataAdapter(monthData);
        recList.setAdapter(dataAdapter);
        totalData();
*/

        doubleBackToExitPressedOnce = false;

/*
        sharedPref();
//
        clearExtraData();
        // Log.e("astatus", "hi");




        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");

        // Log.e("todaytime", df.format(c.getTime()));
        //Log.e("astatus getState",dataUpdate.getState().toString());

        dataUpdate = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!dataUpdate.getName().equals("stopped")) {
                    //             Log.e("astatus", "hi");
                    vHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            monthData.set(0, todayData());
                            dataAdapter.notifyItemChanged(0);
                            Log.e("dhaka", toString().valueOf(total_wifi));

                            totalData();

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

        //  Log.e("astatus getState main",dataUpdate.getState().toString());
        //  Log.e("astatus main isAlive",Boolean.toString(dataUpdate.isAlive()));

        //startActivity(new Intent(MainActivity.this,SettingsActivity.class));
    }

//     @param size of lists
//      @return list of data of last 30 days
//
/*    public List<DataInfo> createList(int size) {

        List<DataInfo> result = new ArrayList<>();
        //total_mobile_wifi = 0;
        total_wifi = 0;
        total_mobile = 0;

        double wTemp, mTemp, tTemp;
        DecimalFormat df = new DecimalFormat("#.##");

        String wifi = "0", mobile = "0", total = "0";
        SharedPreferences sp_month = getSharedPreferences("monthdata", Context.MODE_PRIVATE);
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
        SharedPreferences sp = getSharedPreferences("todaydata", Context.MODE_PRIVATE);
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

        SharedPreferences sp_month = getSharedPreferences("monthdata", Context.MODE_PRIVATE);
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
        editor.commit();

    }

    // populate data
    public void sharedPref() {
        SharedPreferences sp_month = getSharedPreferences("monthdata", Context.MODE_PRIVATE);

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
        editor.commit();

    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

//            setContentView(R.layout.fragment_setting);
//            getFragmentManager().beginTransaction()
//                    .replace(android.R.id.content, new SettingFragment())
//                    .commit();


            startActivity(new Intent(MainActivity.this, SettingsActivity.class));

        } else if (id == R.id.action_exit) {
            NotificationManager nMr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMr.cancel(5000);
            DataService.notification_status = false;

            dataUpdate.setName("stopped"); // to stop thread
            finish();
            //System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }
/*
    @Override
    protected void onPause() {
        super.onPause();
        dataUpdate.setName("stopped");

        // Log.e("astatus","onPause");
        //  Log.e("astatus getState",dataUpdate.getState().toString());
        //finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        DataService.notification_status = true;

        dataUpdate.setName("started");
        // Log.e("astatus","onResume");

        //dataUpdate.start();

        // Log.e("astatus getState",dataUpdate.getState().toString());
        // Log.e("astatus isAlive",Boolean.toString(dataUpdate.isAlive()));
        if (!dataUpdate.isAlive()) {
            //dataUpdate.run();

        }
        //dataUpdate.start();


    }


    @Override
    protected void onRestart() {
        super.onRestart();
        // Log.e("astatus","onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Log.e("astatus","onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Log.e("astatus","onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //  Log.e("astatus","onStop");
    }
*/

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.doubleBackToExitPressedOnce = true;

            Toast.makeText(this, "Press again to quit", Toast.LENGTH_SHORT).show();

            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    //run after 5 sec
                    doubleBackToExitPressedOnce = false;
                }
            }, 5000);
        }


    }

    /*    @Override
        public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;


        if (id == R.id.nav_camera) {
            fragment = new MonthFragment();
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            fragment = new GraphFragment();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
