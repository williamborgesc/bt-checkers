package com.bilbosoft.chekers.exception;

public class WrongTurn extends Exception {

	private static final long serialVersionUID = -7464893064537511049L;

	public WrongTurn() {
		super("Is not your turn!");
	}
}
