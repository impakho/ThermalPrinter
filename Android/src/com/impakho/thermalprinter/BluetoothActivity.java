package com.impakho.thermalprinter;

import java.util.ArrayList;
import java.util.List;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothActivity extends Activity {

	private BluetoothAdapter mBluetoothAdapter;
	private DeviceListAdapter mDevListAdapter;
    private boolean mScanning;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "设备不支持蓝牙4.0 BLE", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        
        if (mBluetoothAdapter == null){
            Toast.makeText(this, "设备不支持蓝牙服务", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        if (!mBluetoothAdapter.isEnabled()){
        	Intent bluetoothEnableRequestIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(bluetoothEnableRequestIntent, 1);  
        }
        
        ListView bleList = (ListView) findViewById(R.id.bleList);
        mDevListAdapter = new DeviceListAdapter();
        bleList.setAdapter(mDevListAdapter);
        
        bleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mDevListAdapter.getCount() > 0) {
					BluetoothDevice device1 = mDevListAdapter.getItem(position);
				    if (device1 == null) return;
				    scanLeDevice(false);
				    MainActivity.mDeviceName = device1.getName();
				    MainActivity.mDeviceAddress = device1.getAddress();
				    finish();
				}
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.actionbar_bluetooth, menu);
        if (!mScanning) {
            menu.findItem(R.id.stopBleMenu).setVisible(false);
            menu.findItem(R.id.scanBleMenu).setVisible(true);
            menu.findItem(R.id.refreshBleMenu).setActionView(null);
        } else {
            menu.findItem(R.id.stopBleMenu).setVisible(true);
            menu.findItem(R.id.scanBleMenu).setVisible(false);
            menu.findItem(R.id.refreshBleMenu).setActionView(R.layout.activity_actionbar_progress);
        }
		return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
        	case R.id.scanBleMenu:
        		scanLeDevice(true);
        		mDevListAdapter.clear();
        		mDevListAdapter.notifyDataSetChanged();
        		break;
        	case R.id.stopBleMenu:
        		scanLeDevice(false);
        		break;
        	case R.id.refreshBleMenu:
        		break;
        	default:
        		scanLeDevice(false);
        		finish();
        		break;
        }
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onResume() {
		scanLeDevice(true);
		super.onResume();
	}

    @Override
	public void onPause() {
		scanLeDevice(false);
		super.onPause();
	}
    
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mDevListAdapter.addDevice(device);
					mDevListAdapter.notifyDataSetChanged();
				}
			});
		}
	};
    
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mScanning = true;
            invalidateOptionsMenu();
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }else{
            mScanning = false;
            invalidateOptionsMenu();
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
    
	class ViewHolder {
		TextView tv_devName, tv_devAddress;
	}
    
	class DeviceListAdapter extends BaseAdapter{

		private List<BluetoothDevice> mBleArray;
		private ViewHolder viewHolder;

		public DeviceListAdapter() {
			mBleArray = new ArrayList<BluetoothDevice>();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mBleArray.contains(device)) {
				mBleArray.add(device);
			}
		}
		public void clear(){
			mBleArray.clear();
		}

		@Override
		public int getCount() {
			return mBleArray.size();
		}

		@Override
		public BluetoothDevice getItem(int position) {
			return mBleArray.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(BluetoothActivity.this).inflate(R.layout.listitem_bluetooth_device, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_devName = (TextView) convertView.findViewById(R.id.device_name);
				viewHolder.tv_devAddress = (TextView) convertView.findViewById(R.id.device_address);
				convertView.setTag(viewHolder);
			} else {
				convertView.getTag();
			}

			BluetoothDevice device = mBleArray.get(position);
			String devName = device.getName();
			if (devName != null && devName.length() > 0) {
				viewHolder.tv_devName.setText(devName);
			} else {
				viewHolder.tv_devName.setText("unknow-device");
			}
			viewHolder.tv_devAddress.setText(device.getAddress());

			return convertView;
		}

	}
}
