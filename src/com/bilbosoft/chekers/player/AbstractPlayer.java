package com.bilbosoft.chekers.player;

import com.bilbosoft.chekers.domain.Move;
import com.bilbosoft.chekers.enums.PlayerTeam;

public abstract class AbstractPlayer {

	private PlayerTeam playerTeam;

	public AbstractPlayer(PlayerTeam playerTeam) {

		this.playerTeam = playerTeam;
	}

	public abstract Move getNextMove();

	public PlayerTeam getPlayerTeam() {
		return playerTeam;
	}

	public void setPlayerTeam(PlayerTeam playerTeam) {
		this.playerTeam = playerTeam;
	}
}
