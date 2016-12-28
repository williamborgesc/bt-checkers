package com.bilbosoft.chekers.service;

import com.bilbosoft.chekers.domain.Game;
import com.bilbosoft.chekers.enums.PlayerTeam;

public class BoardDrower {

	public void draw(Game game) {

		System.out.println("___________________");
		for (int linha = 0; linha < game.getBoard().getSlots().length; linha++) {
			for (int coluna = 0; coluna < game.getBoard().getSlots()[linha].length; coluna++) {

				boolean havePeace = game.getBoard().getSlots()[linha][coluna].getPiece() != null;
				boolean isSelectable = game.getBoard().getSlots()[linha][coluna].isActive();

				if (isSelectable) {

					if (havePeace) {

						if (game.getBoard().getSlots()[linha][coluna].getPiece().getPlayerTeam().equals(PlayerTeam.WHITE)) {

							System.out.print("W");

						} else {

							System.out.print("B");
						}

					} else {

						System.out.print("0");

					}
				} else {

					System.out.print("  ");

				}

				System.out.print("|");
			}

			System.out.println();
		}
	}
}
