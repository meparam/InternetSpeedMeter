package com.tofabd.internetspeedmeter;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);

        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


    }

    public static class SettingsFragment extends PreferenceFragment {

        SharedPreferences dataPref;

        @Override
        public void onStart() {
            super.onStart();
            dataPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);


            SwitchPreference NotificationStatus = (SwitchPreference) findPreference("notification_state");

            NotificationStatus.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    boolean isNotificationOn = (boolean) newValue;
                    // Log.e("boolean :",Boolean.toString(isNotificationOn));
                    SharedPreferences.Editor edit = dataPref.edit();
                    edit.putBoolean("notification_state",isNotificationOn);
                    edit.apply();



                    if(isNotificationOn){
                        preference = findPreference("notification_state");
                        preference.setSummary("Notification is Enabled");

                    }else{
                        preference = findPreference("notification_state");
                        preference.setSummary("Notification is Disabled");


                    }

                    return true;
                }
            });

            Preference resetData = findPreference("reset_data");

            resetData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    //Uncomment the below code to Set the message and title from the strings.xml file
                    //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

                    //Setting message manually and performing action on button click
                    builder.setMessage("All data will be removed!")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    SharedPreferences sp_today = getActivity().getSharedPreferences("todaydata", Context.MODE_PRIVATE);
                                    SharedPreferences sp_month = getActivity().getSharedPreferences("monthdata", Context.MODE_PRIVATE);

                                    SharedPreferences.Editor editor = sp_today.edit();
                                    SharedPreferences.Editor edito2 = sp_month.edit();

                                    editor.clear();
                                    edito2.clear();

                                    editor.apply();
                                    edito2.apply();

                                    Toast.makeText(getActivity(), "Data Removed", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Do you want to reset data?");
                    alert.show();

                    return true;

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
