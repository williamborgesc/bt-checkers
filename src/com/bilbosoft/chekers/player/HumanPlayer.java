package com.bilbosoft.chekers.player;

import com.bilbosoft.chekers.domain.Game;
import com.bilbosoft.chekers.domain.Move;
import com.bilbosoft.chekers.domain.Slot;
import com.bilbosoft.chekers.enums.PlayerTeam;

public class HumanPlayer extends Player {

	private Slot target;

	public HumanPlayer(PlayerTeam playerTeam) {
		super(playerTeam);
	}

	@Override
	public Move getNextMove() {

		if (target == null) {

			throw new IllegalStateException();
		}

		return new Move(Game.getActive().getSelectedPiece().getSlot(), target);
	}

	public void setTarget(Slot target) {
		this.target = target;
	}
}
