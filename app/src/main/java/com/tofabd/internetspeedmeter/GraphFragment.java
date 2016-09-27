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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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
    private ArrayList<Float> mDownload, mUpload;

    DecimalFormat df = new DecimalFormat("#.##");

    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        mChart = (LineChart) rootView.findViewById(R.id.lineChart);

        setRetainInstance(true);

        Legend l = mChart.getLegend();

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


        List<Long> downloadList = DataService.downloadList;
        List<Long> uploadList = DataService.uploadList;


        e1 = new ArrayList<Entry>();
        e2 = new ArrayList<Entry>();

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
        ll1.setTextSize(10f);
        ll1.setTypeface(Typeface.MONOSPACE);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelCount(11, true);


        xAxis.setAxisMinValue(0f);
        xAxis.setAxisMaxValue(60);
        xAxis.setTypeface(Typeface.MONOSPACE);
        // xAxis.setValueFormatter();


        xAxis.enableGridDashedLine(5f, 5f, 1f);
        //xAxis.setAxisLineColor(Color.RED);

        YAxis leftAxis = mChart.getAxisLeft();

        leftAxis.setLabelCount(9, true);
        leftAxis.setAxisMaxValue(YMax);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
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
        mChart.setDescription("");

    }

    private void addDataSet() {
        float YMax = 1024;
        float limitData = 0;
        String mUnit = " KB/s";
        LineData data = mChart.getData();
        if (data != null) {


            List<Long> downloadList = DataService.downloadList;
            List<Long> uploadList = DataService.uploadList;


            e1 = new ArrayList<Entry>();
            e2 = new ArrayList<Entry>();

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
            if (max <=256) {
                YMax = 256;
                limitData = max;
                mUnit = " KB/s";

            }else if(max<=1024){
                YMax = 1024;
                limitData = max;
                mUnit = " KB/s";


            }
            else if(max<=4096){
                YMax = 4096;
                limitData = max/1024;
                mUnit = " MB/s";

            }
            else if(max<=8192){
                YMax = 8192;
                limitData = max/1024;
                mUnit = " MB/s";

            }
            else if(max<=16384){
                YMax = 16384;
                limitData = max/1024;
                mUnit = " MB/s";

            }
            else{
                YMax = 32768;
                limitData = max/1024;
                mUnit = " MB/s";

            }


            LineDataSet d1 = new LineDataSet(e1, "Download");
            LineDataSet d2 = new LineDataSet(e2, "Upload");

            d1.setLineWidth(2f);
            d1.setCircleRadius(1f);
            // d1.setHighLightColor(Color.rgb(230, 0, 0));
            d1.setDrawValues(false);

            d1.setColor(Color.rgb(51, 153, 51));
            d1.setCircleColor(Color.rgb(51, 153, 51));
            d1.setCircleColorHole(Color.rgb(51, 153, 51));
            d1.setValueTextSize(15f);

            d2.setLineWidth(2f);
            d2.setCircleRadius(1f);

            d2.setColor(Color.RED);
            d2.setCircleColor(Color.RED);
            d2.setCircleColorHole(Color.RED);
            //        d2.setDrawFilled(true);
            //        d2.setFillColor(Color.rgb(255, 51, 0));
            d2.setHighLightColor(Color.rgb(0, 102, 0));
            d2.setDrawValues(false);

            LimitLine ll1 = new LimitLine(max, toString().valueOf(df.format(limitData)) + mUnit);
            ll1.setLineWidth(1f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
            ll1.setTextSize(10f);
            ll1.setTypeface(Typeface.DEFAULT_BOLD);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(true);
            xAxis.setDrawAxisLine(true);
            xAxis.setLabelCount(11, true);


            xAxis.setAxisMinValue(0f);
            xAxis.setAxisMaxValue(60);
            xAxis.setTypeface(Typeface.MONOSPACE);
            // xAxis.setValueFormatter();


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

            data.removeDataSet(0);
            data.removeDataSet(1);
            data.clearValues();

            data.addDataSet(d2);
            data.addDataSet(d1);


            mChart.setData(data);

            data.notifyDataChanged();
            mChart.notifyDataSetChanged();


            mChart.invalidate();
        }
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
