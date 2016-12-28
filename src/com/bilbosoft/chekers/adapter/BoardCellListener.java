package com.bilbosoft.chekers.adapter;

import android.content.Context;
import android.view.View;

class BoardCellListener implements View.OnClickListener {

	CellHolder cellHolder;
	ViewHolder viewHolder;
	Context context;
	BoardAdapter adapter;

	public BoardCellListener(BoardAdapter adapter, CellHolder cellHolder, Context context, ViewHolder viewHolder) {
		this.cellHolder = cellHolder;
		this.context = context;
		this.viewHolder = viewHolder;
		this.adapter = adapter;
	}

	@Override
	public void onClick(View view) {

		adapter.onSelect(viewHolder, cellHolder);

	}
}