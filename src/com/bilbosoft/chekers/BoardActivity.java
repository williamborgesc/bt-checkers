package com.bilbosoft.chekers;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import com.bilbosoft.chekers.adapter.BoardAdapter;

public class BoardActivity extends Activity {

	private BoardAdapter boardAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);

		boardAdapter = new BoardAdapter(this);

		ListView gridview = (ListView) findViewById(R.id.board);
		gridview.setAdapter(boardAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
