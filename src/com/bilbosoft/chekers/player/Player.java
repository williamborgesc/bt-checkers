package com.bilbosoft.chekers.player;

import com.bilbosoft.chekers.domain.Move;
import com.bilbosoft.chekers.enums.PlayerTeam;

public abstract class Player {

	private int piecesInGame;

	private PlayerTeam playerTeam;

	public Player(PlayerTeam playerTeam) {

		this.playerTeam = playerTeam;
	}

	public abstract Move getNextMove();

	public PlayerTeam getPlayerTeam() {
		return playerTeam;
	}

	public void setPlayerTeam(PlayerTeam playerTeam) {
		this.playerTeam = playerTeam;
	}

	public int getPiecesInGame() {
		return piecesInGame;
	}

	public void setPiecesInGame(int piecesInGame) {
		this.piecesInGame = piecesInGame;
	}
}
