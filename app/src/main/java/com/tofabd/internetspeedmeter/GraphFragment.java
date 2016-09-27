package com.tofabd.internetspeedmeter;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {
    LineChart mChart;
    private Thread dataUpdate;
    private Handler vHandler = new Handler();
    private ArrayList<Entry> e1, e2;
    protected ArrayList<Float> mDownload, mUpload;
    private TextView dSpeed,uSpeed;

    protected List<Long> downloadList;
    protected List<Long> uploadList;

    DecimalFormat df = new DecimalFormat("#.##");

    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        dSpeed  = (TextView) rootView.findViewById(R.id.text_download);
        uSpeed  = (TextView) rootView.findViewById(R.id.text_upload);

        dSpeed.setText(" ");
        uSpeed.setText(" ");



        mChart = (LineChart) rootView.findViewById(R.id.lineChart);

        setRetainInstance(true);

        mDownload = new ArrayList<>();
        mUpload = new ArrayList<>();


        setGraph();
        liveData();


//        mChart.setData(cd);
//        mChart.setDrawGridBackground(true);
//        mChart.setGridBackgroundColor(Color.rgb(230, 230, 230));
//        //lineChart.getLegend().setEnabled(true);
//

//
//
//
//        lineChart.setVisibleXRangeMaximum(120);
//
//        lineChart.setVisibleYRangeMaximum(300, YAxis.AxisDependency.RIGHT);

        // mChart.setTouchEnabled(false);
        //mChart.setDescription("");
        //lineChart.setDescription("Last 30 Seconds");



       /* dataUpdate = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!dataUpdate.getName().equals("stopped")) {

                    vHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            //monthData.set(0, todayData());
                            //dataAdapter.notifyItemChanged(0);
                            //Log.e("dhaka", toString().valueOf(total_wifi));

                            //totalData();

                            //totalData(); //call main thread
                            //Log.e("monthdata", toString().valueOf(total_wifi));


                                //addEntry();
                               // removeLastEntry();
                            addDataSet();





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
*/

        return rootView;

    }

    public void liveData() {

        dataUpdate = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!dataUpdate.getName().equals("stopped")) {

                    vHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            addDataSet();
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
        dataUpdate.start();

    }


    private void setGraph() {
        float YMax = 1024;
        String mUnit = " KB/s";


        List<Long> downloadList = StoredData.downloadList;
        List<Long> uploadList = StoredData.uploadList;

        //initialize all zero


        e1 = new ArrayList<Entry>();
        e2 = new ArrayList<Entry>();

        float max = 0;
        float t1, t2;

        for (int i = 0; i < downloadList.size(); i++) {
            Log.e("testing", toString().valueOf(downloadList.size()));

            t1 = (float) downloadList.get(i) / 1024;  //convert o Kilobyte
            t2 = (float) uploadList.get(i) / 1024;

            e1.add(new Entry(i, t1));
            e2.add(new Entry(i, t2));

            if (max < t1) {
                max = t1;
            }
            if (max < t2) {
                max = t2;
            }
        }
        if (max < 256) {
            YMax = 512;

        }

        LineDataSet d1 = new LineDataSet(e1, "Download");
        LineDataSet d2 = new LineDataSet(e2, "Upload");

        d1.setLineWidth(2f);
        d1.setCircleRadius(1f);
        d1.setDrawValues(false);

        d1.setColor(Color.rgb(51, 153, 51));
        d1.setCircleColor(Color.rgb(51, 153, 51));
        d1.setCircleColorHole(Color.rgb(51, 153, 51));


        d2.setLineWidth(2f);
        d2.setCircleRadius(1f);
        d2.setDrawValues(false);

        d2.setColor(Color.RED);
        d2.setCircleColor(Color.RED);
        d2.setCircleColorHole(Color.RED);


        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d2);
        sets.add(d1);


        LimitLine ll1 = new LimitLine(max, toString().valueOf(df.format(max)) + mUnit);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        ll1.setTextSize(12f);
        ll1.setTypeface(Typeface.DEFAULT);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelCount(11, true);

        xAxis.setAxisMinValue(0f);
        xAxis.setAxisMaxValue(59f);
        xAxis.setDrawLabels(false);

        xAxis.setTypeface(Typeface.DEFAULT);
        // xAxis.setValueFormatter();


        xAxis.enableGridDashedLine(5f, 5f, 1f);
        //xAxis.setAxisLineColor(Color.RED);

        YAxis leftAxis = mChart.getAxisLeft();

        leftAxis.setLabelCount(9, true);
        leftAxis.setAxisMaxValue(YMax);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        leftAxis.setTextSize(12f);
        leftAxis.enableGridDashedLine(5f, 5f, 1f);

        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.setDrawLimitLinesBehindData(true);


        mChart.getAxisRight().setEnabled(false);


        YAxis rightAxis = mChart.getAxisRight();


        rightAxis.enableGridDashedLine(5f, 5f, 1f);
        rightAxis.setLabelCount(9, true);
        rightAxis.setDrawGridLines(false);

        //rightAxis.setDrawLabels(false);
        rightAxis.setAxisMaxValue(1024f);
        rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(tru


        //  int color = mColors[count % mColors.length];

//            set.setColor(color);
//            set.setCircleColor(color);
//            set.setHighLightColor(color);
//            set.setValueTextSize(10f);
//            set.setValueTextColor(color);

        //data.addDataSet(set);


        LineData cd = new LineData(sets);

        mChart.setData(cd);
        mChart.setDrawGridBackground(true);
        mChart.setGridBackgroundColor(Color.rgb(230, 230, 230));

        mChart.setTouchEnabled(false);
        mChart.setDescription("Last 60 Seconds");



    }

    private void addDataSet() {

        float YMax = 1024;
        float limitData = 0;
        String mUnit = " KB/s";
        LineData data = mChart.getData();
        if (data != null) {

          downloadList = StoredData.downloadList;
             uploadList = StoredData.uploadList;


            e1 = new ArrayList<Entry>();
            e2 = new ArrayList<Entry>();

            setSpeed();


            float max = 0;
            float t1, t2;

            for (int i = 0; i < downloadList.size(); i++) {


                t1 = (float) downloadList.get(i) / 1024;  //convert o Kilobyte
                t2 = (float) uploadList.get(i) / 1024;

                e1.add(new Entry(i, t1));
                e2.add(new Entry(i, t2));

                if (max < t1) {
                    max = t1;
                }
                if (max < t2) {
                    max = t2;
                }
            }

            if (max <= 256) {
                YMax = 512;
                limitData = max;
                mUnit = " KB/s";

            } else if (max <= 1024) {
                YMax = 1024;
                limitData = max;
                mUnit = " KB/s";


            } else if (max <= 4096) {
                YMax = 4096;
                limitData = max / 1024;
                mUnit = " MB/s";

            } else if (max <= 8192) {
                YMax = 8192;
                limitData = max / 1024;
                mUnit = " MB/s";

            } else if (max <= 16384) {
                YMax = 16384;
                limitData = max / 1024;
                mUnit = " MB/s";

            } else {
                YMax = 32768;
                limitData = max / 1024;
                mUnit = " MB/s";

            }


            LineDataSet d1 = new LineDataSet(e1, "Download");
            LineDataSet d2 = new LineDataSet(e2, "Upload");

            d1.setLineWidth(2f);
            d1.setCircleRadius(0.5f);
            // d1.setHighLightColor(Color.rgb(230, 0, 0));
            d1.setDrawValues(false);

            d1.setColor(Color.rgb(51, 153, 51));
            d1.setCircleColor(Color.rgb(51, 153, 51));
            d1.setCircleColorHole(Color.rgb(51, 153, 51));
            d1.setValueTextSize(15f);
            d1.setDrawCircleHole(false);

            d2.setLineWidth(2f);
            d2.setCircleRadius(0.5f);

            d2.setColor(Color.RED);
            d2.setCircleColor(Color.RED);
            d2.setCircleColorHole(Color.RED);
            //        d2.setDrawFilled(true);
            //        d2.setFillColor(Color.rgb(255, 51, 0));
            d2.setHighLightColor(Color.rgb(0, 102, 0));
            d2.setDrawValues(false);
            d2.setDrawCircleHole(false);

            LimitLine ll1 = new LimitLine(max, toString().valueOf(df.format(limitData)) + mUnit);

            ll1.setLineWidth(1f);

            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);

            ll1.setTextSize(12f);
            ll1.setLineColor(Color.rgb(51, 153, 51));
            ll1.setTypeface(Typeface.DEFAULT);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(true);
            xAxis.setDrawAxisLine(true);
            xAxis.setLabelCount(11, true);


            xAxis.setAxisMinValue(0f);
            xAxis.setAxisMaxValue(59f);
            xAxis.setDrawLabels(false);


            xAxis.setTypeface(Typeface.DEFAULT);


            xAxis.enableGridDashedLine(5f, 5f, 1f);
            //xAxis.setAxisLineColor(Color.RED);

            YAxis leftAxis = mChart.getAxisLeft();

            leftAxis.setLabelCount(9, true);
            leftAxis.setAxisMaxValue(YMax);
            leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
            leftAxis.enableGridDashedLine(5f, 5f, 1f);

            leftAxis.removeAllLimitLines();
            leftAxis.addLimitLine(ll1);
            leftAxis.setDrawLimitLinesBehindData(true);


            mChart.getAxisRight().setEnabled(true);


            YAxis rightAxis = mChart.getAxisRight();


            rightAxis.enableGridDashedLine(5f, 5f, 1f);
            rightAxis.setLabelCount(9, true);
            rightAxis.setDrawGridLines(false);

            //rightAxis.setDrawLabels(false);
            rightAxis.setAxisMaxValue(YMax/1024);
            rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(tru


            //  int color = mColors[count % mColors.length];

//            set.setColor(color);
//            set.setCircleColor(color);
//            set.setHighLightColor(color);
//            set.setValueTextSize(10f);
//            set.setValueTextColor(color);

            //data.addDataSet(set);

            data.removeDataSet(0);
            data.removeDataSet(1);
            data.clearValues();

            data.addDataSet(d2);
            data.addDataSet(d1);

            Legend legend = mChart.getLegend();
            legend.setTextSize(15f);
            legend.setTypeface(Typeface.DEFAULT);
           // legend.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "Set1", "Set2", "Set3", "Set4", "Set5" });
            legend.setCustom(new int[]{ Color.rgb(51, 153, 51), Color.rgb(255, 0, 0)},new String[] { "Download  ", "Upload" });

            legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);



         /*   List<String> st = new ArrayList<>();
            st.add("Last  30 Seconds");
            legend.setComputedLabels(st);
*/


            mChart.setData(data);

            data.notifyDataChanged();
            mChart.notifyDataSetChanged();


            mChart.invalidate();

        }
    }

    public void setSpeed(){

        Long download_speed;
        Long  upload_speed;

        String d=" ";
        String u = " ";

        download_speed=  StoredData.downloadSpeed;
        upload_speed= StoredData.uploadSpeed;

        if(download_speed<1024){
            d = download_speed+ " B/s";
        }else if(download_speed<1048576){
           d =  df.format(download_speed/1024)+" KB/s";
        }
        else if(download_speed>=1048576){
            d =  df.format((double)download_speed/1048576)+" MB/s";
        }

        if(upload_speed<1024){
            u = upload_speed+ " B/s";
        }else if(upload_speed<1048576){
            u =  df.format(upload_speed/1024)+" KB/s";
        }
        else if(upload_speed>=1048576){
            u =  df.format((double)upload_speed/1048576)+" MB/s";
        }

        dSpeed.setText(d);
        uSpeed.setText(u);


    }
  /*  private void removeLastEntry(){
        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet sets = data.getDataSetByIndex(0);

            if (sets != null) {

                Entry e = sets.getEntryForXValue(sets.getEntryCount() - 1);

                data.removeEntry(e,0);
                sets.
                // or remove by index
                // mData.removeEntryByXValue(xIndex, dataSetIndex);
                data.notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.invalidate();
            }
        }

    }*/

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

        //dataUpdate.start();

        // Log.e("astatus getState",dataUpdate.getState().toString());
        // Log.e("astatus isAlive",Boolean.toString(dataUpdate.isAlive()));
        if (!dataUpdate.isAlive()) {
            //dataUpdate.run();
            liveData();

        }
        //dataUpdate.start();


    }


}
