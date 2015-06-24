package com.example.bluetoothlegatt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class DeviceScanService extends Service 
{
	private static final String TAG = "MyService";
	MediaPlayer player;
	private String devid;
    private Handler mHandler;
    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    private static final long SCAN_PERIOD = 100000000;
		
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
	
	
	@Override
	public void onCreate() 
	{
		
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		player = MediaPlayer.create(this, R.raw.cmb);
		player.setLooping(true); // Set looping
		//devId = getIntent().getStringExtra("deviceId", devid);
		 mHandler = new Handler();
	        // Use this check to determine whether BLE is supported on the device.  Then you can
	        // selectively disable BLE-related features.
	        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) 
	        {
	            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
	            this.stopSelf();
	        }

	        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
	        // BluetoothAdapter through BluetoothManager.
	        final BluetoothManager bluetoothManager =
	                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
	        mBluetoothAdapter = bluetoothManager.getAdapter();

	        // Checks if Bluetooth is supported on the device.
	        if (mBluetoothAdapter == null) {
	            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
	            this.stopSelf();
	            return;
	        }
		 
		 //scanLeDevice(true);		
	}
	


	@Override
	public void onDestroy() 
	{
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		//player.stop();
		scanLeDevice(false);
	}
	
	@Override
	public void onStart(Intent intent, int startid) 
	{
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		//player.start();
		scanLeDevice(true);
	}
	
    private void Notify(String notificationTitle, String notificationMessage)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")
        
        Notification notification = new Notification(R.drawable.wymall_icon,"WYMALL !!", System.currentTimeMillis());
        Intent notificationIntent = new Intent(this,DeviceScanActivity2.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);
        
        notification.setLatestEventInfo(DeviceScanService.this, notificationTitle,notificationMessage, pendingIntent);
        notificationManager.notify(9999, notification);
     }
    
    //--------------------------- ID ---------------------------------//
  	private static String uniqueID = null;
  	private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
  	
  	public synchronized static String id(Context context) 
      {
          if (uniqueID == null) {
              SharedPreferences sharedPrefs = context.getSharedPreferences(
                      PREF_UNIQUE_ID, Context.MODE_PRIVATE);
              uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
              if (uniqueID == null) {
                  uniqueID = UUID.randomUUID().toString();
                  Editor editor = sharedPrefs.edit();
                  editor.putString(PREF_UNIQUE_ID, uniqueID);
                  editor.commit();
              }
          }
          return uniqueID;

      }
  	
    public void scanLeDevice(final boolean enable) 
    {
        if (enable) 
        {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() 
            {
                @Override
                public void run() 
                {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    //invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            
            
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        //invalidateOptionsMenu();
    }
    
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() 
    {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) 
        {
        	Notify("There's promotion available","Click to view promotion");

        }
    };

}
