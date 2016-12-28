package com.bilbosoft.chekers.domain;

public class Position {

	private int line;

	private int column;

	@Override
	public String toString() {
		return "Position [line=" + line + ", column=" + column + "]";
	}

	public Position(int line, int column) {
		this.line = line;
		this.column = column;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + line;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Position))
			return false;
		Position other = (Position) obj;
		if (column != other.column)
			return false;
		if (line != other.line)
			return false;
		return true;
	}

}
