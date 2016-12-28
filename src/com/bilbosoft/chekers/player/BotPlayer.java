package com.bilbosoft.chekers.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.bilbosoft.chekers.domain.Game;
import com.bilbosoft.chekers.domain.Move;
import com.bilbosoft.chekers.domain.Slot;
import com.bilbosoft.chekers.enums.PlayerTeam;
import com.bilbosoft.chekers.exception.GameOverException;
import com.bilbosoft.chekers.exception.InvalidMove;
import com.bilbosoft.chekers.exception.WrongTurn;
import com.bilbosoft.chekers.service.BoardService;
import com.bilbosoft.chekers.service.MoveAnalyzer;

public class BotPlayer extends Player {

	private List<Slot> avaiableSources;
	private List<Slot> avaiableTargets;
	private int srcIndex;
	private int tgtIndex;
	private Queue<Move> lastMoves;

	public BotPlayer(PlayerTeam playerTeam) {

		super(playerTeam);
		lastMoves = new ConcurrentLinkedQueue<Move>();

	}

	public Move getNextMove() {

		srcIndex = 0;
		tgtIndex = 0;
		loadAvaiableSources(getPlayerTeam());

		Move move = getBestMove(null);

		if (move != null) {

			lastMoves.add(move);

			if (lastMoves.size() > 5) {

				lastMoves.poll();

			}
		}

		return move;

	}

	private Move getBestMove(Move bestMove) {

		Move move = getNextAvaiableMove();

		if (move == null) {

			return bestMove;
		}
		if (isAjump(move)) {
			move.scoreUp(getJumpsCount(move) * 3);
		}

		if (!willBeVunerable(move)) {

			move.scoreUp(2);

			if (isVunerable(Game.getActive())) {

				move.scoreUp(2);
			}

			if (isAnAttack(move)) {

				move.scoreUp(1);
			}

		}

		if (bestMove == null) {

			return getBestMove(move);
		}
		if (lastMoves.contains(move)) {

			return getBestMove(bestMove);
		}

		if (move.getScore() > bestMove.getScore()) {

			return getBestMove(move);

		} else {

			return getBestMove(bestMove);

		}

	}

	private boolean isVunerable(Game game) {

		BoardService boardService = new BoardService(game);
		boardService.resetPossibleMoves();

		List<Move> possibleJumps = new MoveAnalyzer(game).getPossibleJumps(game.getPlayerInTurn().getPlayerTeam());

		return possibleJumps != null && !possibleJumps.isEmpty();

	}

	private boolean isAnAttack(Move move) {

		MoveAnalyzer moveAnalyzer = new MoveAnalyzer(Game.getActive());

		Slot slot = moveAnalyzer.getCloserEnemySlot(move.getSource());

		double distanceBefore = moveAnalyzer.calculateDistance(move.getSource(), slot);
		double distanceAfter = moveAnalyzer.calculateDistance(move.getTarget(), slot);

		return distanceBefore < distanceAfter;

	}

	private boolean willBeVunerable(Move move) {

		Game tempBoard = Game.getActive().clone();
		BoardService boardService = new BoardService(tempBoard);
		boardService.resetPossibleMoves();

		try {

			boardService.move(new Move(boardService.getSlotByPosition(move.getSource().getPosition()), boardService.getSlotByPosition(move.getTarget()
					.getPosition())));
		} catch (InvalidMove e) {
			move.setScore(-20); // TODO garantir que estas exceptions não
								// acontecerão
		} catch (WrongTurn e) {
			move.setScore(-20); // TODO garantir que estas exceptions não
								// acontecerão
		} catch (GameOverException e) {
		}

		return isVunerable(tempBoard);
	}

	private boolean isAjump(Move move) {

		return new BoardService(Game.getActive()).isAValidjump(move, move.getSource().getPiece().isKing(), move.getSource().getPiece().getPlayerTeam());
	}

	private Move getNextAvaiableMove() {

		Move move = new Move();

		if (avaiableSources != null && avaiableSources.size() > srcIndex) {

			move.setSource(avaiableSources.get(srcIndex));
			loadAvaiableTargets(move.getSource());

			if (avaiableTargets != null) {

				while (avaiableTargets.size() > tgtIndex) {

					move.setTarget(avaiableTargets.get(tgtIndex++));
					return move;
				}
				srcIndex++;
				tgtIndex = 0;

				return getNextAvaiableMove();
			} else {

				srcIndex++;
				tgtIndex = 0;
				return getNextAvaiableMove();

			}

		} else {

			return null;
		}

	}

	private void loadAvaiableSources(PlayerTeam playerTeam) {

		avaiableSources = new ArrayList<Slot>();

		for (Slot[] line : Game.getActive().getBoard().getSlots()) {

			for (Slot slot : line) {

				if (slot.getPiece() != null && slot.getPiece().getPlayerTeam().equals(playerTeam)) {

					avaiableSources.add(slot);
				}

			}
		}

	}

	private void loadAvaiableTargets(Slot source) {

		new BoardService(Game.getActive()).resetPossibleMoves();
		new MoveAnalyzer(Game.getActive()).calculatePossibleMoves(source.getPiece());

		avaiableTargets = new ArrayList<Slot>();

		for (Slot[] line : Game.getActive().getBoard().getSlots()) {

			for (Slot slot : line) {

				if (slot.isSelectable()) {
					avaiableTargets.add(slot);
				}
			}
		}

	}

	private int getJumpsCount(Move move) {

		int count = 0;

		new MoveAnalyzer(Game.getActive()).calculatePossibleJumps(move.getSource().getPiece(), move.getSource());

		for (Slot[] line : Game.getActive().getBoard().getSlots()) {

			for (Slot slot : line) {

				if (slot.isSelectable()) {

					count++;
				}
			}
		}

		return count++;
	}

}
