package com.bilbosoft.chekers.domain;

public class Move {

	private Slot target;

	private Slot source;

	private int score;

	public Move(Slot source, Slot target) {
		this.target = target;
		this.source = source;
	}

	public Move() {
	}

	public Slot getTarget() {
		return target;
	}

	public void setTarget(Slot target) {
		this.target = target;
	}

	public Slot getSource() {
		return source;
	}

	public void setSource(Slot source) {
		this.source = source;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Move))
			return false;
		Move other = (Move) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Move [source=(" + source.getPosition().getLine() + "," + source.getPosition().getColumn() + ") , target= (" + target.getPosition().getLine()
				+ "," + target.getPosition().getColumn() + ")]";
	}

	public void scoreUp(int points) {
		this.score += points;

	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
