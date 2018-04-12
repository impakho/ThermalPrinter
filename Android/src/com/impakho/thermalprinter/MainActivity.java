package com.impakho.thermalprinter;

import java.io.UnsupportedEncodingException;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static String mDeviceName = "";
	public static String mDeviceAddress = "";
	Intent gattServiceIntent;
	private BluetoothLeService mBluetoothLeService;
	private boolean mConnected = false;
	private boolean mConnectedBit = false;
	private PrinterControl ptc;
	Thread sendThread;
	private String sendBuf;
	private int sendedBufLen;
	private int waitBufLen = 16;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //ActionBar actionBar = getActionBar();
        
        ptc = new PrinterControl();
        
        Button btnTestPrint = (Button) findViewById(R.id.btnTestPrint);
        btnTestPrint.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if (mConnectedBit == true){
					StringBuffer ptsb = new StringBuffer();
					ptsb.append(ptc.begin());
					ptsb.append(ptc.setDefault());
					ptsb.append(ptc.testPage());
					ptsb.append(ptc.setDefault());
					if (sendThread != null){
						sendThread.interrupt();
						sendThread = null;
					}
					sendBuf = ptsb.toString();
					sendedBufLen = 0;
					waitBufLen = 16;
					sendThread = new Thread(sendRunnable);
					sendThread.start();
				}
			}
        });
        
        Button btnPrint = (Button) findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if (mConnectedBit == true){
					StringBuffer ptsb = new StringBuffer();
					ptsb.append(ptc.begin());
					ptsb.append(ptc.setDefault());
					ptsb.append(editHTML());
					ptsb.append(ptc.setDefault());
					if (sendThread != null){
						sendThread.interrupt();
						sendThread = null;
					}
					sendBuf = ptsb.toString();
					sendedBufLen = 0;
					waitBufLen = 16;
					sendThread = new Thread(sendRunnable);
					sendThread.start();
				}
			}
        });
       
    }
	
	public String editHTML(){
		EditText editContent = (EditText) findViewById(R.id.editContent);
		String html = editContent.getText().toString();
		try {
			return ptc.println(new String(html.getBytes("UTF-8"), "GB2312"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ptc.println("");
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.actionbar_main, menu);
		return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
    		case R.id.bluetoothMenu:
    	        if (mBluetoothLeService != null){
    	        	mBluetoothLeService.disconnect();
    	        	mBluetoothLeService = null;
    	        }
    	        if (gattServiceIntent != null){
    	        	unbindService(mServiceConnection);
    	        	gattServiceIntent = null;
    	        }
    			Intent bluetoothIntent = new Intent(this, BluetoothActivity.class);
    			startActivityForResult(bluetoothIntent, 1);
    			break;
    		case R.id.wifiMenu:
    			Intent wifiIntent = new Intent(this, WifiActivity.class);
    			startActivity(wifiIntent);
    			break;
    		default:
    			break;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data){
    	if (reqCode==1){
    		if (mDeviceAddress.length() < 2) return;
    		updateConnectionState("正在连接");
    		if (gattServiceIntent == null){
                gattServiceIntent = new Intent(this, BluetoothLeService.class);
                bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    		}
    	}
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) mBluetoothLeService.connect(mDeviceAddress);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothLeService != null){
        	mBluetoothLeService.disconnect();
        	mBluetoothLeService = null;
        }
        if (gattServiceIntent != null){
        	unbindService(mServiceConnection);
        	gattServiceIntent = null;
        }
    }
    
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
    	
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            	mConnected = true;
            	mConnectedBit = false;
            }else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            	mConnected = false;
            	mConnectedBit = false;
                updateConnectionState("未连接");
            }else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	receiveData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
        
    };
    
    Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            while (sendedBufLen < sendBuf.length()){
            	if (Thread.interrupted()) break;
            	if (mConnectedBit == false) break;
            	if (waitBufLen > 0){
            		int readyBufLen = waitBufLen;
            		int leftBufLen = sendBuf.length() - sendedBufLen;
            		if (readyBufLen > leftBufLen) readyBufLen = leftBufLen;
            		waitBufLen -= readyBufLen;
            		sendData(sendBuf.substring(sendedBufLen, sendedBufLen + readyBufLen));
            		sendedBufLen += readyBufLen;
            	}
            }
        }
    };
    
    private void receiveData(String recvStr){
    	if (recvStr == null || recvStr.length() <= 0) return;
    	if (mConnectedBit == true) {
    		waitBufLen += recvStr.length()/2;
    	}else{
			if (sendThread != null){
				sendThread.interrupt();
				sendThread = null;
			}
    	}
    }
    
    private void sendData(String sendStr){
    	if (mConnectedBit == true){
    		mBluetoothLeService.txxx(str2hex(sendStr));
    	}else{
			if (sendThread != null){
				sendThread.interrupt();
				sendThread = null;
			}
    	}
    }
    
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        if (gattServices.size() > 0 && mBluetoothLeService.get_connected_status(gattServices) >= 4){
	        if (mConnected){
	        	mConnectedBit = true;
				mBluetoothLeService.enable_JDY_ble(true);
				updateConnectionState("已连接");
	        }
	    }
    }
    
    public static String str2hex(String s){
    	String str="";
        for (int i=0;i<s.length();i++) { 
            int ch = (int)s.charAt(i); 
            String s4 = Integer.toHexString(ch); 
            if (s4.length()==1) s4 = "0"+s4;
            str = str + s4; 
        } 
        return str; 
    }
    
    private void updateConnectionState(final String showStr) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	TextView btStatusView = (TextView) MainActivity.this.findViewById(R.id.btStatus);
            	btStatusView.setText("蓝牙状态："+showStr);
            }
        });
    }
    
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) finish();
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        	mBluetoothLeService.disconnect();
            mBluetoothLeService = null;
        }
        
    };
    
}
