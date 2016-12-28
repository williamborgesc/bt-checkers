package com.bilbosoft.chekers;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;

import com.bilbosoft.chekers.adapter.BoardAdapter;
import com.bilbosoft.chekers.service.bluetooth.ConnectedThread;

public class UpdateView extends AsyncTask<Void, Void, Void> {

	private ConnectedThread connectedThread;
	private Handler mHandler;
	protected BoardAdapter boardAdapter;

	public UpdateView(ConnectedThread connectedThread, Handler mHandler, BoardAdapter boardAdapter, Activity context) {
		this.connectedThread = connectedThread;
		this.mHandler = mHandler;
		this.boardAdapter = boardAdapter;
		this.context = context;
	}

	private Activity context;

	@Override
	protected Void doInBackground(Void... params) {

		Looper.prepare();

		Void v = null;
		while (true) {

			if (connectedThread.received) {
				publishProgress(v);

				mHandler.post(new Runnable() {

					@Override
					public void run() {

						ListView gridview = (ListView) context.findViewById(R.id.board);
						gridview.setAdapter(boardAdapter);

					}
				});

				connectedThread.received = false;
			}

		}
	}

}