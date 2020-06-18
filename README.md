# DataWedge-Component-Sample
Sample app to show how specifying the component in DataWedge Intent output can improve how your application receives data



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
filter.addAction(getResources().getString("com.zebra.dwapiexerciser.ACTION");
filter.addCategory(Intent.CATEGORY_DEFAULT);
registerReceiver(myBroadcastReceiver, filter);
```