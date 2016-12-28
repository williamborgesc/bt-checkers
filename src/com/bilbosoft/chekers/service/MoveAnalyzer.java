package com.bilbosoft.chekers.service;

import java.util.ArrayList;
import java.util.List;

import com.bilbosoft.chekers.domain.Game;
import com.bilbosoft.chekers.domain.Move;
import com.bilbosoft.chekers.domain.Piece;
import com.bilbosoft.chekers.domain.Slot;
import com.bilbosoft.chekers.enums.PlayerTeam;

public class MoveAnalyzer {

	private BoardService boardService;
	private Game game;

	public MoveAnalyzer(Game game) {

		this.game = game;
		boardService = new BoardService(game);
	}

	public void calculatePossibleMoves(Piece selected) {

		if (!game.isOn()) {
			return;
		}

		Slot[][] slots = game.getBoard().getSlots();
		boolean isThereAJump = boardService.isThereAJump(selected.getPlayerTeam());
		boardService.resetPossibleMoves();

		if (isThereAJump) {

			calculatePossibleJumps(selected, selected.getSlot());

		} else {
			for (Slot[] line : slots) {
				for (Slot slot : line) {

					if (boardService.canMove(new Move(selected.getSlot(), slot), selected.isKing(), selected.getPlayerTeam())) {
						slot.setSelectable(true);
					}
				}
			}

		}

	}

	public List<Move> getPossibleJumps(PlayerTeam playerTeam) {

		boolean thereIsAJump = boardService.isThereAJump(playerTeam);
		Slot[][] slots = game.getBoard().getSlots();
		List<Piece> pieces = boardService.getAllPieces(playerTeam);
		List<Move> possibleJumps = null;

		if (thereIsAJump) {

			possibleJumps = new ArrayList<Move>();

			for (Piece piece : pieces) {
				for (Slot[] line : slots) {
					for (Slot slot : line) {

						if (boardService.isAValidjump(new Move(piece.getSlot(), slot), piece.isKing(), playerTeam)) {
							possibleJumps.add(new Move(piece.getSlot(), slot));
						}
					}
				}
			}
		}
		return possibleJumps;
	}

	public List<Move> getPossibleMoves(PlayerTeam playerTeam) {

		Slot[][] slots = game.getBoard().getSlots();
		List<Piece> pieces = boardService.getAllPieces(playerTeam);
		List<Move> possibleMoves = null;

		possibleMoves = new ArrayList<Move>();

		for (Piece piece : pieces) {
			for (Slot[] line : slots) {
				for (Slot slot : line) {

					Move move = new Move(piece.getSlot(), slot);
					if (boardService.canMove(move, piece.isKing(), playerTeam)) {
						possibleMoves.add(move);
					}
				}
			}
		}
		return possibleMoves;
	}

	public void calculatePossibleJumps(Piece selectedPiece, Slot currentSlot) {

		for (Slot[] line : game.getBoard().getSlots()) {
			for (Slot slot : line) {
				if (!slot.isSelectable() && boardService.isAValidjump(new Move(currentSlot, slot), selectedPiece.isKing(), selectedPiece.getPlayerTeam())) {
					slot.setSelectable(true);
					calculatePossibleJumps(selectedPiece, slot);
				}
			}
		}
	}

	public Slot getCloserEnemySlot(Slot selectedSlot) {

		return getCloserEnemySlot(selectedSlot, null, selectedSlot.getPiece().getPlayerTeam());
	}

	private Slot getCloserEnemySlot(Slot selectedSlot, List<Slot> checked, PlayerTeam playerTeam) {

		if (checked == null) {
			checked = new ArrayList<Slot>();
		}

		for (Slot slot : boardService.getAdjacentSlots(selectedSlot)) {

			if (checked.contains(selectedSlot)) {
				continue;

			} else if (slot.getPiece() != null && selectedSlot.getPiece() == null && !slot.getPiece().getPlayerTeam().equals(playerTeam)) {

				selectedSlot = slot;
				break;

			} else {

				checked.add(selectedSlot);
				return getCloserEnemySlot(slot, checked, playerTeam);
			}

		}
		return selectedSlot;

	}

	public double calculateDistance(Slot slot1, Slot slot2) {

		int a = slot1.getPosition().getLine() - slot2.getPosition().getLine();
		int b = slot1.getPosition().getColumn() - slot2.getPosition().getColumn();

		return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
	}

}
