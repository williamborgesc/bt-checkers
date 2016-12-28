package com.bilbosoft.chekers;

import static com.bilbosoft.chekers.Constants.REQUEST_ENABLE_BT;
import static com.bilbosoft.chekers.Constants.SERVER_UUID;

import java.io.IOException;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.widget.ListView;

import com.bilbosoft.chekers.adapter.BluetoothBoardAdapter;
import com.bilbosoft.chekers.service.bluetooth.ConnectedThread;

public class BluetoothClientActivity extends Activity {

	private ProgressDialog progressDialog;
	private BluetoothAdapter mBluetoothAdapter;
	public Handler mHandler;
	private BluetoothBoardAdapter boardAdapter;
	public ConnectedThread connectedThread;
	public ConnectThread connectThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);

		mHandler = new Handler();

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			throw new RuntimeException("Device does not support Bluetooth");
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {

			connectToServer();
		}
	}

	private void connectToServer() {

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

		boardAdapter = new BluetoothBoardAdapter(this);

		ListView gridview = (ListView) findViewById(R.id.board);
		gridview.setAdapter(boardAdapter);

		BluetoothDevice device = null;

		String selectedDeviceName = this.getIntent().getExtras().getString("deviceName");

		for (BluetoothDevice bluetoothDevice : pairedDevices) {

			if (bluetoothDevice.getName().equals(selectedDeviceName)) {

				device = bluetoothDevice;

				break;
			}
		}

		ConnectThread connectThread = new ConnectThread(device);
		connectThread.start();
		progressDialog = ProgressDialog.show(BluetoothClientActivity.this, getString(R.string.connecting), device.getName());
		progressDialog.setCancelable(true);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_ENABLE_BT) {

			if (resultCode != Activity.RESULT_CANCELED) {

				connectToServer();

			} else {

				finish();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				tmp = device.createRfcommSocketToServiceRecord(SERVER_UUID);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			mBluetoothAdapter.cancelDiscovery();

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();
			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				try {
					mmSocket.close();
				} catch (IOException closeException) {
				}
				return;
			}

			// Do work to manage the connection (in a separate thread)
			manageConnectedSocket(mmSocket);
			progressDialog.dismiss();
		}

		private void manageConnectedSocket(BluetoothSocket socket) {

			connectedThread = new ConnectedThread(socket, mHandler);

			boardAdapter.setConnectedThread(connectedThread);

			connectedThread.start();

			Looper.prepare();

			UpdateView updateView = new UpdateView(connectedThread, mHandler, boardAdapter, BluetoothClientActivity.this);

			Void v = null;

			updateView.execute(v);

		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (connectedThread != null) {
			connectedThread.cancel();
		}

		if (connectThread != null) {
			connectThread.cancel();
		}
	}

}
