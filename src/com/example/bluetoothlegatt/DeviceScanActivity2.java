package com.example.bluetoothlegatt;


import android.app.Activity;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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


import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity2 extends Activity implements OnItemClickListener
{
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private String uniqId;
    private Calendar cl;
    private String now;
    private BluetoothDevice device;
    private BluetoothData btdt;
    private Button scanButton;
    private Button stopButton;
    private ListView blulist;
    private boolean sc;
    private Switch sw;
    //private String npic;
    
    public static ImageLoader iml;
	public static DisplayImageOptions dmp;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 100000000;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_list_1);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        final Intent intent1 = new Intent(DeviceScanActivity2.this, DeviceScanService.class);
        stopService(intent1);
        
        getActionBar().setTitle(R.string.title_devices);
        sc = false;
        mHandler = new Handler();
        uniqId = id(this);
        cl = Calendar.getInstance();
        blulist = (ListView) findViewById(R.id.bluelist2);
//        scanButton = (Button) findViewById(R.id.service_button);
//        stopButton = (Button) findViewById(R.id.stop_button);
        
        sw = (Switch) findViewById(R.id.switch1);
        
        
        //set the switch to ON 
        sw.setChecked(false);
        //attach a listener to check for changes in state
        sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
       
         @Override
         public void onCheckedChanged(CompoundButton buttonView,
           boolean isChecked) 
         {
        	 final Intent intent1 = new Intent(DeviceScanActivity2.this, DeviceScanService.class);
       
          if(isChecked)
          {
           //switchStatus.setText("Switch is currently ON");
        	  intent1.putExtra("deviceId", uniqId);
				Log.d("jjjj", "hello");
				startService(intent1);
        	  Toast.makeText(DeviceScanActivity2.this, "Press On", Toast.LENGTH_LONG).show();
          }
          else
          {
        	  stopService(intent1);
        	  Toast.makeText(DeviceScanActivity2.this, "Press Off", Toast.LENGTH_LONG).show();
          }
       
         }
        });
         
        //check the current state before we display the screen
        if(sw.isChecked())
        {
        	Toast.makeText(DeviceScanActivity2.this, "Press On", Toast.LENGTH_LONG).show();
        }
        else 
        {
        	Toast.makeText(DeviceScanActivity2.this, "Press Off", Toast.LENGTH_LONG).show();
        }
                  
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
        now = df.format(new Date());
        Log.d("fff", "     " + uniqId);
        
//        scanButton.setOnClickListener(new View.OnClickListener() 
//		{			
//			@Override
//			public void onClick(View v) 
//			{
//				final Intent intent1 = new Intent(DeviceScanActivity2.this, DeviceScanService.class);
//				if(sc == false)
//				{
//					Toast.makeText(DeviceScanActivity2.this, "press", Toast.LENGTH_LONG).show();
//					intent1.putExtra("deviceId", uniqId);
//					Log.d("jjjj", "hello");
//					startService(intent1);
//					sc = true;
//				}
//				else
//				{
//					Toast.makeText(DeviceScanActivity2.this, "stop", Toast.LENGTH_LONG).show();
//					stopService(intent1);
//					sc  = false;					
//				}
//				
//				//Toast.makeText(DeviceScanActivity2.this, "press", Toast.LENGTH_LONG).show();
////				if(sc == false)
////				{
////					Log.d("jjjj", "hello");
////				}
//			}
//		});
//        
//        stopButton.setOnClickListener(new View.OnClickListener() 
//		{			
//			@Override
//			public void onClick(View v) 
//			{
//				final Intent intent1 = new Intent(DeviceScanActivity2.this, DeviceScanService.class);
//				
//					Toast.makeText(DeviceScanActivity2.this, "stop", Toast.LENGTH_LONG).show();
//					stopService(intent1);
//					sc  = false;					
//				
//				//Toast.makeText(DeviceScanActivity2.this, "press", Toast.LENGTH_LONG).show();
////				if(sc == false)
////				{
////					Log.d("jjjj", "hello");
////				}
//			}
//		});
        
        
    	// UNIVERSAL IMAGE LOADER SETUP
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc().cacheInMemory()
				.imageScaleType(ImageScaleType.EXACTLY)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new WeakMemoryCache())
				.discCacheSize(100 * 1024 * 1024).build();
		
		ImageLoader.getInstance().init(config);
		// END - UNIVERSAL IMAGE LOADER SETUP
		
		
		iml = ImageLoader.getInstance();
		dmp = new DisplayImageOptions.Builder().cacheInMemory()
				.cacheOnDisc().resetViewBeforeLoading()
				.showImageForEmptyUri(R.drawable.ic_launcher).build();		

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) 
        {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        scanLeDevice(true);
        //hh
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() 
    {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        blulist.setAdapter(mLeDeviceListAdapter);
        blulist.setOnItemClickListener(this);
        //setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }


    public void scanLeDevice(final boolean enable) 
    {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() 
            {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            
            
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter 
    {
        private ArrayList<BluetoothDevice> mLeDevices;
        private ArrayList<BluetoothData> mLeShoppro;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() 
        {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mLeShoppro = new ArrayList<BluetoothData>();
            mInflator = DeviceScanActivity2.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) 
        {
            if(!mLeDevices.contains(device)) 
            {
                mLeDevices.add(device);
                //mLeShoppro.add(object);
            }
        }
        
        public void addShopdat(BluetoothData shop)
        {
        	 if(!mLeShoppro.contains(shop)) 
             {
                 mLeShoppro.add(shop);
                 //mLeShoppro.add(object);
             }
        }

        public BluetoothDevice getDevice(int position) 
        {
            return mLeDevices.get(position);
        }
        
        public BluetoothData getBluetooth(int position)
        {
        	return mLeShoppro.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) 
        {
            final ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) 
            {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.shop_name);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.product_name);
                viewHolder.promoDate = (TextView) view.findViewById(R.id.date);
                viewHolder.promoPic = (ImageView) view.findViewById(R.id.product_pic);
                viewHolder.promoExpDate = (TextView) view.findViewById(R.id.expDate);
                		
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            device = mLeDevices.get(i);
            btdt = mLeShoppro.get(i);
            
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(btdt.getShname());
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            
            viewHolder.deviceAddress.setText(btdt.getCatname());
            viewHolder.promoDate.setText("Start Date : " + btdt.getStdate());
            viewHolder.promoExpDate.setText("Expiration Date : " + btdt.getExpdate());
            iml.displayImage(btdt.getPicurl(), viewHolder.promoPic , dmp);
            
            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) 
        {
            runOnUiThread(new Runnable() 
            {
            	
                @Override
                public void run() 
                {
                    mLeDeviceListAdapter.addDevice(device);
                    
                    BluetoothData bt = new BluetoothData();
                    
                    HttpClient httpclient = new DefaultHttpClient();
                    
                    // HttpPost httppost = new HttpPost("http://192.168.61.1/ajaxuploadimg/connected.php");
                     HttpPost httppost = new HttpPost("http://192.168.39.50/beacon2/connected.php");
                     
                     
                     // Add your data
                     List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > ();
                     
                     nameValuePairs.add(new BasicNameValuePair("bt_mac", device.getAddress()));
                     nameValuePairs.add(new BasicNameValuePair("device_mac", uniqId));
                     

                     try {
                         httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                         Log.d("myapp1", "works till here. 2");
                         try {
                             HttpResponse response = httpclient.execute(httppost);
                             
                             String js = (String) EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                             Log.d("myapp1", "    llllll   lllll " + js);
                             
                             try 
                             {
     							JSONObject jj = new JSONObject(js);
     							String picurl = jj.getString("Image");
     							picurl = picurl.replace("photo\"", "http://192.168.39.50/beacon2/photo");
     							picurl = "http://192.168.39.50/beacon2/" + picurl;
     							bt.setShname(jj.getString("Shopname"));
     							bt.setCatname(jj.getString("Category"));
     							bt.setStdate(jj.getString("Firstdate"));
     							bt.setExpdate(jj.getString("Lastdate"));
     							bt.setPicurl(picurl);

     							Log.d("GGGFGDRGH", "fdgergerg   " + picurl);
     						} catch (JSONException e) 
     						{
     							// TODO Auto-generated catch block
     							e.printStackTrace();
     						}
                             
                             //iml.displayImage(npic, promo, dmp);
                             
                             
                             //Log.d("myapp", "response " + (String) EntityUtils.toString(response.getEntity(), HTTP.UTF_8));
                         } catch (ClientProtocolException e) {
                             e.printStackTrace();
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     } catch (UnsupportedEncodingException e) 
                     {
                         e.printStackTrace();
                     }                    
                    mLeDeviceListAdapter.addShopdat(bt);
                    //Notify("There's promotion available","Click to view promotion");
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    
    // View Holder class
    static class ViewHolder 
    {
        TextView deviceName;
        TextView deviceAddress;
        TextView promoDate;
        TextView promoExpDate;
        ImageView promoPic;
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
  	
    private void Notify(String notificationTitle, String notificationMessage)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")
        
        Notification notification = new Notification(R.drawable.wymall_icon,"WYMALL !!", System.currentTimeMillis());
        Intent notificationIntent = new Intent(this,DeviceScanActivity2.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);
        
        notification.setLatestEventInfo(DeviceScanActivity2.this, notificationTitle,notificationMessage, pendingIntent);
        notificationManager.notify(9999, notification);
     }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) 
	{
		// TODO Auto-generated method stub
		
		 final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
	        final BluetoothData bluda = mLeDeviceListAdapter.getBluetooth(position);
	        if (device == null) return;
	        final Intent intent = new Intent(this, DeviceControlActivity.class);
	        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, bluda.getShname());
	        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
	        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_PROMOTION, bluda.getPicurl());
	        if (mScanning) {
	            mBluetoothAdapter.stopLeScan(mLeScanCallback);
	            mScanning = false;
	        }
	        startActivity(intent);
		
	}
  	
  	
  	
  	
  	
}