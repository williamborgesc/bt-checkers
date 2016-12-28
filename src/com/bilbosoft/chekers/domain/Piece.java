package com.bilbosoft.chekers.domain;

import com.bilbosoft.chekers.enums.PlayerTeam;

public class Piece {

	private static int pieceCount;

	private int id;

	private PlayerTeam playerTeam;

	private Slot slot;

	private boolean king;

	private boolean isSelected;

	public Piece(PlayerTeam playerTeam, Slot slot) {

		this.slot = slot;
		this.playerTeam = playerTeam;
		id = pieceCount++;
	}

	public int getId() {
		return id;
	}

	public PlayerTeam getPlayerTeam() {
		return playerTeam;
	}

	public boolean isKing() {
		return king;
	}

	public void setKing(boolean king) {
		this.king = king;
	}

	public void setPlayerTeam(PlayerTeam playerTeam) {
		this.playerTeam = playerTeam;
	}

	public Slot getSlot() {
		return slot;
	}

	public void setSlot(Slot slot) {
		this.slot = slot;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public String toString() {
		return "Piece [id=" + id + ", player=" + playerTeam + ", king=" + king
				+ ", isSelected=" + isSelected + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Piece other = (Piece) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public Piece clone(Slot slot) {

		Piece clonePiece = new Piece(getPlayerTeam(), slot);
		clonePiece.setKing(isKing());
		clonePiece.setSelected(isSelected());

		return clonePiece;
	}

}
