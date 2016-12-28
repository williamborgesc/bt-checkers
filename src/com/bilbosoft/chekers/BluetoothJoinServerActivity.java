package com.bilbosoft.chekers;

import static com.bilbosoft.chekers.Constants.INIT_LOCAL_GAME;
import static com.bilbosoft.chekers.Constants.REQUEST_ENABLE_BT;

import java.util.Set;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothJoinServerActivity extends ListActivity {

	private BluetoothAdapter mBluetoothAdapter;
	public Handler mHandler;
	private Set<BluetoothDevice> pairedDevices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			throw new RuntimeException("Device does not support Bluetooth");
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

		} else {

			listDevices();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_ENABLE_BT) {

			if (resultCode != Activity.RESULT_CANCELED) {

				listDevices();

			} else {

				finish();
			}
		}

	}

	private void listDevices() {

		pairedDevices = mBluetoothAdapter.getBondedDevices();

		if (pairedDevices.size() == 0) {

			Toast.makeText(this, "No paired device", Toast.LENGTH_LONG).show();
			finish();
		}

		String[] deviceNames = new String[pairedDevices.size()];

		int i = 0;
		for (BluetoothDevice device : pairedDevices) {

			deviceNames[i++] = device.getName();

		}

		this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, (String[]) deviceNames));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		String deviceName = this.getListAdapter().getItem(position).toString();

		Intent it = new Intent(this, BluetoothClientActivity.class);

		it.putExtra("deviceName", deviceName);

		startActivityForResult(it, INIT_LOCAL_GAME);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
