package com.bilbosoft.chekers.exception;

public class InvalidMove extends Exception {

	private static final long serialVersionUID = 1675267490512111148L;

	public InvalidMove() {
		super("Invalid move! Try again!");
	}
}
