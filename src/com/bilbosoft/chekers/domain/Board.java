package com.bilbosoft.chekers.domain;

public class Board {

	private Slot[][] slots;

	public Slot[][] getSlots() {
		return slots;
	}

	public void setSlots(Slot[][] slots) {
		this.slots = slots;
	}

	@Override
	public Board clone() {

		Board cloneBoard = new Board();

		Slot[][] cloneSlots = new Slot[8][8];

		for (int line = 0; line < slots.length; line++) {

			for (int column = 0; column < slots[line].length; column++) {

				Slot slot = slots[line][column];
				cloneSlots[line][column] = slot.clone(new Position(line, column));
			}
		}

		cloneBoard.setSlots(cloneSlots);

		return cloneBoard;
	}

}
