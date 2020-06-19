# DataWedge Component Sample

DataWedge 8.0 introduced a new capability to the DataWedge Intent output plugin, [the ability to specify the **component** of the application which will receive the Intent](https://techdocs.zebra.com/datawedge/latest/guide/output/intent/#intentoutputsetup)

Up until version 8.0, DataWedge only allowed you to specify the action, category and delivery mechanism (Start Activity, Start Service or Send Broadcast)

For StartActivity and StartService, being able to specify the service allows you to be certain which application will receive the Intent.  Before DataWedge 8.0 the system would select a single activity whose Intent filter matched the appropriate action.  So, if you had two activities which had the same intent-filter action, which activity received the scan would be left to chance (similarly, if you had two services whose intent-filter specified the same action then only one service would receive the scan).  For this reason, being able to specify the component makes the delivery more secure and reliable.

When configured to Send Broadcast Intents, the ability to specify the component now allows DataWedge to send **explicit Intents**, this has long been a customer request as **it can help applications receive scans when in the background**.

Android 8.0 introduced [limits on how Broadcast Intents are received](https://developer.android.com/about/versions/oreo/background#broadcasts), which meant applications could no longer receive implicit Intents that had been declared in their manifest and had to be running in the foreground (or a foreground service) to receive implicit Intents.  

DataWedge was previously only capable of sending implicit Intents which made upgrading to Oreo more complicated.  One exception to the Android 8.0 background limits was that it **did not apply to explicit Intents**, therefore applications who have been written to receive their DataWedge intents via broadcast and declared in the application manifest **can upgrade to Oreo without modifying their application, provided they specify a Component for the Intent**.

This application has been written to demonstrate how to receive scans via Android Intents on an Android Oreo device (or higher) running DataWedge 8.0+

![App](https://github.com/darryncampbell/DataWedge-Component-Sample/raw/master/screenshots/app.jpg)


The broadcast receiver is declared in the application manifest as follows:
```xml
<receiver
  android:name=".MyReceiver"
  android:enabled="true"
  android:exported="true">
  <intent-filter>
    <action android:name="com.zebra.datawedge.scan" />
  </intent-filter>
</receiver>
```

And the broadcast receiver looks as follows.  The scanned data is presented as a Toast:

```java
public class MyReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(final Context context, final Intent intent) {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
      @Override
      public void run() {
        String decodedData = 
         intent.getStringExtra("com.symbol.datawedge.data_string");
        String decodedLabelType = 
         intent.getStringExtra("com.symbol.datawedge.label_type");
        Toast.makeText(context.getApplicationContext(), "" +
         decodedData + " [" + decodedLabelType + "]", Toast.LENGTH_SHORT).show();
      }
    });
  }
}
```

**Caveats**:
- If you have previously used the DataWedge API [SetDefaultProfile](https://techdocs.zebra.com/datawedge/8-0/guide/api/setdefaultprofile/) then this application may not appear to work because the "Profile0 (default)" settings are never applied. 
- After boot, you may need to wait a few seconds for the scanning service to fully enable.  During this period, the scanner beam will not emit.
- Do not force close the app which will receive scans in the background.  It is best practice to prevent your end users from accessing the application settings, thereby preventing them from manually closing your app.

## DataWedge setup

This article assumes familiarity with Zebra's DataWedge tool as well as the DataWedge profile mechanism.  For an overview of DataWedge, please refer to the [DataWedge Techdocs page](https://techdocs.zebra.com/datawedge/latest/guide/overview/)

The aim is to have DataWedge send a broadcast intent when the application is in the background, this means we will not know the foreground application.  Since we do not know the foreground application then both the **default** and **Launcher** profiles need to be modified.  Typically the default profile on DataWedge is called **Profile0 (default)**

If you press the 'Modify Default and Launcher Profiles' button the profiles will be automatically configured for you:

- Scanner input plugin enabled
- Intent output plugin enabled
- Intent output action set to 'com.zebra.datawedge.scan'
- Intent output delivery method set to 'Broadcast intent'
- Intent output component set to this app
- Intent output component signature check disabled

![DataWedge 1](https://github.com/darryncampbell/DataWedge-Component-Sample/raw/master/screenshots/dw_1.jpg)
![DataWedge 2](https://github.com/darryncampbell/DataWedge-Component-Sample/raw/master/screenshots/dw_2.jpg)

### Signature check

The [DataWedge documentation](https://techdocs.zebra.com/datawedge/8-0/guide/output/intent/) gives a good explanation of why you might want to enable signature check on a production deployment but for the purposes of this demo it is easiest to leave the check disabled.  For more information on generating these signatures then I wrote a [guide](https://github.com/darryncampbell/MX-SignatureAuthentication-Demo) 

## Running the application

After configuring DataWedge you should be able to scan barcodes regardless of the foreground application, provided that application does not have its own DataWedge profile associated with it.

![Scanning](https://github.com/darryncampbell/DataWedge-Component-Sample/raw/master/screenshots/scan_1.jpg)
![Scanning](https://github.com/darryncampbell/DataWedge-Component-Sample/raw/master/screenshots/scan_2.jpg)
![Scanning](https://github.com/darryncampbell/DataWedge-Component-Sample/raw/master/screenshots/scan_3.jpg)
![Scanning](https://github.com/darryncampbell/DataWedge-Component-Sample/raw/master/screenshots/scan_4.jpg)
![Scanning](https://github.com/darryncampbell/DataWedge-Component-Sample/raw/master/screenshots/scan_5.jpg)

