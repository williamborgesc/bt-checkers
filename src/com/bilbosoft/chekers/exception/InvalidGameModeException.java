package com.bilbosoft.chekers.exception;

public class InvalidGameModeException extends Exception {

	private static final long serialVersionUID = -4758747490365586126L;

	public InvalidGameModeException() {
		super("Invalid game mode");
	}
}
