# DataWedge Component Sample

DataWedge 8.0 introduced a new capability to the DataWedge Intent output plugin, [the ability to specify the **component** of the application which will receive the Intent](https://techdocs.zebra.com/datawedge/latest/guide/output/intent/#intentoutputsetup)

Up until version 8.0, DataWedge only allowed you to specify the action, category and delivery mechanism (Start Activity, Start Service or Send Broadcast)

For StartActivity and StartService, being able to specify the service allows you to be certain which application will receive the Intent.  Before DataWedge 8.0 the system would select a single activity whose Intent filter matched the appropriate action.  So, if you had two activities which had the same intent-filter action, which activity received the scan would be left to chance (similarly, if you had two services whose intent-filter specified the same action then only one service would receive the scan).  For this reason, being able to specify the component makes the delivery more secure and reliable.

When configured to Send Broadcast Intents, the ability to specify the component now allows DataWedge to send **explicit Intents**, this has long been a customer request as **it can help applications receive scans when in the background**.

Android 8.0 introduced [limits on how Broadcast Intents are received](https://developer.android.com/about/versions/oreo/background#broadcasts), essentially, applications could no longer receive implicit Intents that had been declared in their manifest and had to be running in the foreground (or a foreground service) to receive an implicit Intents.  DataWedge was previously only capable of sending implicit Intents which made upgrading to Oreo more complicated.  One exception to the Android 8.0 background limits was that it **did not apply to explicit Intents**, therefore applications who have been written to receive their DataWedge intents via broadcast and declared in the application manifest *can upgrade to Oreo without modifying their application, provided they specify a Component for the Intent*.

This application has been written to demonstrate how to receive scans via Android Intents on an Android Oreo device (or higher) running DataWedge 8.0+

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

As part of application setup, if the app detects DataWedge 8.0+ is running, "Profile0 (default)" will be modified to send scans to this application via Intents.

**Caveats**:
- If you do not want "Profile0 (default)" modified then modify the source code before running this app
- If you have previously used the DataWedge API [SetDefaultProfile](https://techdocs.zebra.com/datawedge/8-0/guide/api/setdefaultprofile/) then this application may not appear to work because the "Profile0 (default)" settings are never applied. 

## DataWedge setup

todo images here


Current reception via StartActivity:

```xml
<activity
  android:name=".MainActivity"
  android:label="@string/app_name_long"
  android:theme="@style/AppTheme.NoActionBar">
  <intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.LAUNCHER" />
  </intent-filter>
  <intent-filter>
    <action android:name="com.zebra.dwapiexerciser.ACTION" />
    <category android:name="android.intent.category.DEFAULT" />
  </intent-filter>
</activity>
```


Current reception via StartService:

```xml
<service
  android:name=".ListeningService"
  android:enabled="true"
  android:exported="true" >
  <intent-filter>
    <action android:name="com.zebra.dwapiexerciser.ACTION" />
    <category android:name="android.intent.category.DEFAULT" />
  </intent-filter>
</service>
```

Current reception via Broadcast (dynamic receiver)

```java
IntentFilter filter = new IntentFilter();
filter.addAction("com.symbol.datawedge.api.RESULT_ACTION");
filter.addAction("com.symbol.datawedge.api.NOTIFICATION_ACTION");  
filter.addAction("com.zebra.dwapiexerciser.ACTION");
filter.addCategory(Intent.CATEGORY_DEFAULT);
registerReceiver(myBroadcastReceiver, filter);
```







Wait for scanner service to be ready after boot
Do not force close app (prevent users from accessing settings)

Signature??