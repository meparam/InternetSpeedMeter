package com.tofabd.internetspeedmeter;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;




public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();
    private Handler vHandler = new Handler();

    Thread dataUpdate;
    private TextView wTotal, mTotal, tTotal;
    static int m = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        wTotal = (TextView) findViewById(R.id.id_wifi);
        mTotal = (TextView) findViewById(R.id.id_mobile);
        tTotal = (TextView) findViewById(R.id.id_total);

        //dataUpdate.setName("started");

        final RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        final DataAdapter dataAdapter = new DataAdapter(createList(30));
        recList.setAdapter(dataAdapter);


        doubleBackToExitPressedOnce = false;


//        sharedPref();
//
        clearExtraData();
        // Log.e("astatus", "hi");


        if (!DataService.service_status) {
            Intent intent = new Intent(this, DataService.class);
            startService(intent);
        }


        Intent intentBC = new Intent();
        intentBC.setAction("com.tofabd.internetmeter");
        sendBroadcast(intentBC);

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


                            //DataAdapter dataAdapter = new DataAdapter(createList(30));
//                            recList.setAdapter(dataAdapter);
                            List<DataInfo> temp = createList(30);
                            DataInfo dInfo = new DataInfo();

                            dInfo.date = "Today";
                            dInfo.wifi = Integer.toString(m * 1);
                            dInfo.mobile = Integer.toString(m * 2);
                            dInfo.total = Integer.toString(m * 3);
                            temp.add(dInfo);

                            dataAdapter.updateData(temp);
                            m++;


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

        //  Log.e("astatus getState main",dataUpdate.getState().toString());
        //  Log.e("astatus main isAlive",Boolean.toString(dataUpdate.isAlive()));



        //startActivity(new Intent(MainActivity.this,SettingsActivity.class));
    }
    public List<DataInfo> createList(int size) {


        List<DataInfo> result = new ArrayList<>();

        String MB = " MB", GB = " GB";

        float count_total = 0, count_wifi = 0, count_mobile = 0;
        float wTemp, mTemp, tTemp;
        DecimalFormat df = new DecimalFormat("#.##");

        String wifi = "0", mobile = "0", total = "0";
        long wifi_data, mobile_data, total_data;

        SharedPreferences sp_month = getSharedPreferences("monthdata", Context.MODE_PRIVATE);
        //   SharedPreferences.Editor editor =editor = sp_month.edit();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");


        for (int i = 1; i <= size; i++) {

            if (i == 1) {


                SharedPreferences sp = getSharedPreferences("todaydata", Context.MODE_PRIVATE);

                // convert to megabyte
                wTemp = (float) (sp.getLong("WIFI_DATA", 0) / 1000000.0);
                mTemp = (float) (sp.getLong("MOBILE_DATA", 0) / 1000000.0);

                tTemp = wTemp + mTemp;
                // tTemp = (double) sp.getLong("TOTAL_DATA", 0) / 1000000.0;

                //count for total data
                count_wifi += wTemp;
                count_mobile += mTemp;
                //count_total += tTemp;


                //     Log.e("today", Float.toString(tTemp));


                //check less than Gigabyte or not
                if (wTemp < 1000) {
                    wifi = df.format(wTemp) + MB; // consider 2 value after decimal point
                } else {
                    wifi = df.format(wTemp / 1000) + GB;
                }

                if (mTemp < 1000) {
                    mobile = df.format(mTemp) + MB; // consider 2 value after decimal point

                } else {
                    mobile = df.format(mTemp / 1000) + GB;
                }

                if (tTemp < 1000) {
                    total = df.format(tTemp) + MB; // consider 2 value after decimal point
                } else {
                    total = df.format(tTemp / 1000) + GB;
                }


                // Calendar ca = Calendar.getInstance();
                // String todayDate = sdf.format(ca.getTime());// get today's date

                DataInfo di = new DataInfo();

                di.date = "Today";

                di.wifi = wifi;
                di.mobile = mobile;
                di.total = total;

                result.add(di);
                continue;
            }


            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DATE, (1 - i)); // day decrease

            String todayDate = sdf.format(ca.getTime());// get  date

            //check date availabe or not
            if (sp_month.contains(todayDate)) {

                String sData = sp_month.getString(todayDate, null); // get saved data
                try {

                    JSONObject jOb = new JSONObject(sData);

                    wifi = jOb.getString("WIFI_DATA");
                    mobile = jOb.getString("MOBILE_DATA");
                    // total = jOb.getString("TOTAL_DATA");


                    wTemp = (float) (Long.parseLong(wifi) / 1000000.0);
                    mTemp = (float) (Long.parseLong(mobile) / 1000000.0);
                    //tTemp = (double) Long.parseLong(total) / 1000000.0;


                    tTemp = wTemp + mTemp;

                    //     Log.e("outside", Integer.toString(i) + " " + wifi + mobile + Double.toString(wTemp));

                    //count for total
                    count_wifi += wTemp;
                    count_mobile += mTemp;
                    //count_total += tTemp;

                    //check less than Gigabyte or not
                    if (wTemp < 1000) {
                        wifi = df.format(wTemp) + MB; // consider 2 value after decimal point
                    } else {
                        wifi = df.format(wTemp / 1000) + GB;
                    }

                    if (mTemp < 1000) {
                        mobile = df.format(mTemp) + MB; // consider 2 value after decimal point

                    } else {
                        mobile = df.format(mTemp / 1000) + GB;
                    }

                    if (tTemp < 1000) {
                        total = df.format(tTemp) + MB; // consider 2 value after decimal point
                    } else {
                        total = df.format(tTemp / 1000) + GB;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {


                wifi = "0" + MB;
                mobile = "0" + MB;
                total = "0" + MB;


            }


            DataInfo di = new DataInfo();

            di.date = todayDate;

            di.wifi = wifi;
            di.mobile = mobile;
            di.total = total;

            result.add(di);

        }

        count_total = count_wifi + count_mobile;
        //check less than Gigabyte or not
        if (count_wifi < 1000) {
            wifi = df.format(count_wifi) + MB; // consider 2 value after decimal point
        } else {
            wifi = df.format(count_wifi / 1000) + GB;
        }

        if (count_mobile < 1000) {
            mobile = df.format(count_mobile) + MB; // consider 2 value after decimal point

        } else {
            mobile = df.format(count_mobile / 1000) + GB;
        }

        if (count_total < 1000) {
            total = df.format(count_total) + MB; // consider 2 value after decimal point
        } else {
            total = df.format(count_total / 1000) + GB;
        }
        wTotal.setText(wifi);
        mTotal.setText(mobile);
        tTotal.setText(total);


        return result;

    }

    void clearExtraData() {

        SharedPreferences sp_month = getSharedPreferences("monthdata", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp_month.edit();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        for (int i = 40; i <= 1000; i++) {


            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DATE, (1 - i));
            String todayDate = sdf.format(ca.getTime());// get  date

            if (sp_month.contains(todayDate)) {
                editor.remove(todayDate);

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

    }

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


            startActivity(new Intent(MainActivity.this,SettingsActivity.class));

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
        if(!dataUpdate.isAlive()){
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

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
