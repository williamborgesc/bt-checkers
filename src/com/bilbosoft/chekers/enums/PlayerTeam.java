package com.bilbosoft.chekers.enums;

public enum PlayerTeam {

	RED, WHITE;

	public PlayerTeam getEnemyPlayerTeam() {

		if (this.equals(RED)) {

			return WHITE;

		} else {

			return RED;
		}

	}

}
