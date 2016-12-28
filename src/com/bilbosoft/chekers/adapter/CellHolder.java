package com.bilbosoft.chekers.adapter;

import android.widget.ImageView;

class CellHolder {

		public CellHolder(int line, int column) {
			this.line = line;
			this.column = column;

		}

		public ImageView imageView;
		public int line;
		public int column;

	}