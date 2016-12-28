package com.bilbosoft.chekers;

import static com.bilbosoft.chekers.Constants.REQUEST_DISCOVERABLE_BT;
import static com.bilbosoft.chekers.Constants.SERVER_UUID;
import static com.bilbosoft.chekers.Constants.SERVICE_NAME;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.widget.ListView;

import com.bilbosoft.chekers.adapter.BluetoothBoardAdapter;
import com.bilbosoft.chekers.service.bluetooth.ConnectedThread;

public class BluetoothHostActivity extends Activity {

	private ProgressDialog progressDialog;
	private BluetoothAdapter mBluetoothAdapter;
	public Handler mHandler;
	BluetoothBoardAdapter boardAdapter;
	private ConnectedThread connectedThread;
	private AcceptThread acceptThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);

		mHandler = new Handler();

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			throw new RuntimeException("Device does not support Bluetooth");
		}
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);

		boardAdapter = new BluetoothBoardAdapter(this);

		ListView gridview = (ListView) findViewById(R.id.board);
		gridview.setAdapter(boardAdapter);

	}

	public void initListen() {

		acceptThread = new AcceptThread();

		acceptThread.start();
		progressDialog = ProgressDialog.show(BluetoothHostActivity.this, getString(R.string.waiting_client), getString(R.string.waiting_client));
		progressDialog.setCancelable(true);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_DISCOVERABLE_BT) {

			if (resultCode != Activity.RESULT_CANCELED) {

				initListen();

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

	private class AcceptThread extends Thread {

		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			// Use a temporary object that is later assigned to mmServerSocket,
			// because mmServerSocket is final
			BluetoothServerSocket tmp = null;
			try {
				// MY_UUID is the app's UUID string, also used by the client
				// code
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, SERVER_UUID);

			} catch (IOException e) {
			}
			mmServerSocket = tmp;
		}

		public void run() {
			BluetoothSocket socket = null;
			// Keep listening until exception occurs or a socket is returned
			while (true) {
				try {
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					break;
				}
				// If a connection was accepted
				if (socket != null) {
					// Do work to manage the connection (in a separate thread)
					manageConnectedSocket(socket);
					progressDialog.dismiss();
					break;
				}
			}
		}

		private void manageConnectedSocket(BluetoothSocket socket) {

			connectedThread = new ConnectedThread(socket, mHandler);

			boardAdapter.setConnectedThread(connectedThread);

			connectedThread.start();

			Looper.prepare();

			UpdateView updateView = new UpdateView(connectedThread, mHandler, boardAdapter, BluetoothHostActivity.this);

			Void v = null;

			updateView.execute(v);

		}

		public void cancel() {
			try {
				mmServerSocket.close();
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
		if (acceptThread != null) {
			acceptThread.cancel();
		}
	}

}
