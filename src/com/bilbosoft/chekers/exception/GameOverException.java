package com.bilbosoft.chekers.exception;

import com.bilbosoft.chekers.enums.PlayerTeam;

public class GameOverException extends Exception {

	private static final long serialVersionUID = -7464893064537511049L;

	public GameOverException(PlayerTeam playerTeam) {
		super("Game over! Winner: " + playerTeam);
	}

	public GameOverException() {
		super("Game over! Draw Game!");
	}
}
