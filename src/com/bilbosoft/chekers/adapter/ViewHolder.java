package com.bilbosoft.chekers.adapter;

class ViewHolder {
	public CellHolder ivColumn0;
	public CellHolder ivColumn1;
	public CellHolder ivColumn2;
	public CellHolder ivColumn3;
	public CellHolder ivColumn4;
	public CellHolder ivColumn5;
	public CellHolder ivColumn6;
	public CellHolder ivColumn7;

	public ViewHolder(int line) {
		ivColumn0 = new CellHolder(line, 0);
		ivColumn1 = new CellHolder(line, 1);
		ivColumn2 = new CellHolder(line, 2);
		ivColumn3 = new CellHolder(line, 3);
		ivColumn4 = new CellHolder(line, 4);
		ivColumn5 = new CellHolder(line, 5);
		ivColumn6 = new CellHolder(line, 6);
		ivColumn7 = new CellHolder(line, 7);
	}
}