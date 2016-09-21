package com.tofabd.internetspeedmeter;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment {

//
//    public SettingFragment() {
//        // Required empty public constructor
//    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_setting, container, false);
//
//
//
//        return view;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.fragment_setting);



        addPreferencesFromResource(R.xml.preferences);
    }


}
