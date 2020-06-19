package com.darryncampbell.datawedge_component_sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView message = findViewById(R.id.message);
        message.setText("This application will register a broadcast receiver in the application manifest.\n\n" +
                        "On Oreo+, configure DataWedge to send an explicit broadcast by specifying a component for the Intent (requires DW 8.0+).\n\n" +
                "See the ReadMe for more information");

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.symbol.datawedge.api.RESULT_ACTION");
        filter.addCategory(Intent.CATEGORY_DEFAULT);    //  NOTE: this IS REQUIRED for DW6.2 and up!
        //  Whilst we're here also register to receive broadcasts via DataWedge scanning
        registerReceiver(myBroadcastReceiver, filter);
        Button btnCreateProfile = findViewById(R.id.btnCreateProfiles);
        btnCreateProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                configureProfileIntentOutput("Profile0 (default)");
                configureProfileIntentOutput("Launcher");
            }
        });
        getDataWedgeVersion();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    private void getDataWedgeVersion() {
        //Requires DataWedge 6.4
        sendDataWedgeIntentWithExtra("com.symbol.datawedge.api.ACTION",
                "com.symbol.datawedge.api.GET_VERSION_INFO", "");
    }

    private void configureProfileIntentOutput(String profileName)
    {
        Bundle profileConfig = new Bundle();
        profileConfig.putString("PROFILE_NAME", profileName);
        profileConfig.putString("PROFILE_ENABLED", "true");
        profileConfig.putString("CONFIG_MODE", "UPDATE");

        Bundle barcodeConfig = new Bundle();
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE");
        barcodeConfig.putString("RESET_CONFIG", "true");
        Bundle barcodeProps = new Bundle();
        barcodeProps.putString("scanner_input_enabled", "true");
        barcodeConfig.putBundle("PARAM_LIST", barcodeProps);
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig);

        sendDataWedgeIntentWithExtra("com.symbol.datawedge.api.ACTION", "com.symbol.datawedge.api.SET_CONFIG", profileConfig);

        profileConfig.remove("PLUGIN_CONFIG");

        Bundle intentConfig = new Bundle();
        intentConfig.putString("PLUGIN_NAME", "INTENT");
        intentConfig.putString("RESET_CONFIG", "true");
        Bundle intentProps = new Bundle();
        intentProps.putString("intent_output_enabled", "true");
        //  Must match the action declared in the manifest (in this sample)
        intentProps.putString("intent_action", "com.zebra.datawedge.scan");
        intentProps.putString("intent_delivery", "2");

        ArrayList<Bundle> bundleComponentInfo = new ArrayList<>();
        Bundle component0 = new Bundle();
        component0.putString("PACKAGE_NAME","com.darryncampbell.datawedge_component_sample");
        bundleComponentInfo.add(component0);
        intentProps.putParcelableArrayList("intent_component_info", bundleComponentInfo);

        intentConfig.putBundle("PARAM_LIST", intentProps);
        profileConfig.putBundle("PLUGIN_CONFIG", intentConfig);

        sendDataWedgeIntentWithExtra("com.symbol.datawedge.api.ACTION", "com.symbol.datawedge.api.SET_CONFIG", profileConfig);
    }

    private void sendDataWedgeIntentWithExtra(String action, String extraKey, String extraValue)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extraValue);
        this.sendBroadcast(dwIntent);
    }

    private void sendDataWedgeIntentWithExtra(String action, String extraKey, Bundle extras)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extras);
        this.sendBroadcast(dwIntent);
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("com.symbol.datawedge.api.RESULT_ACTION"))
        {
            if (intent.hasExtra("com.symbol.datawedge.api.RESULT_GET_VERSION_INFO"))
            {
                Bundle versionInformation = intent.getBundleExtra("com.symbol.datawedge.api.RESULT_GET_VERSION_INFO");
                String DWVersion = versionInformation.getString("DATAWEDGE");
                TextView txtDataWedgeVersion = findViewById(R.id.datawedgeVersion);
                if (DWVersion.compareTo("8.0.0") >= 1)
                {
                    Button btnCreateProfiles = findViewById(R.id.btnCreateProfiles);
                    btnCreateProfiles.setVisibility(View.VISIBLE);
                    txtDataWedgeVersion.setText("DataWedge Version: " + DWVersion + "\nPress button to configure DataWedge");
                }
                else
                {
                    txtDataWedgeVersion.setText("DataWedge Version: " + DWVersion + "\nUNABLE to set Intent Component");
                }

            }
        }
        }
    };

}