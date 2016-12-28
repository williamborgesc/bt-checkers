package com.bilbosoft.chekers.domain;

public class Slot {

	private boolean active;

	private boolean isSelectable;

	public boolean isSelectable() {
		return isSelectable;
	}

	public void setSelectable(boolean isSelectable) {
		this.isSelectable = isSelectable;
	}

	private Piece piece;

	private Position position;

	public Slot(Position position) {

		this.position = position;
	}

	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Slot other = (Slot) obj;
		if (active != other.active)
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Slot [active=" + active + ", isSelectable=" + isSelectable
				+ ", piece=" + piece + ", position=" + position + "]";
	}

	public Slot clone(Position position) {

		Slot cloneSlot = new Slot(position);
		Piece piece = getPiece();

		cloneSlot.setActive(isActive());
		cloneSlot.setSelectable(isSelectable());

		if (piece != null) {
			cloneSlot.setPiece(piece.clone(cloneSlot));
		}

		return cloneSlot;
	}
}
