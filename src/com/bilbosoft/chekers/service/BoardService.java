package com.bilbosoft.chekers.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

import com.bilbosoft.chekers.domain.Game;
import com.bilbosoft.chekers.domain.Move;
import com.bilbosoft.chekers.domain.Piece;
import com.bilbosoft.chekers.domain.Position;
import com.bilbosoft.chekers.domain.Slot;
import com.bilbosoft.chekers.enums.PlayerTeam;
import com.bilbosoft.chekers.exception.GameOverException;
import com.bilbosoft.chekers.exception.InvalidMove;
import com.bilbosoft.chekers.exception.WrongTurn;

public class BoardService {

	private Game game;

	public BoardService(Game game) {
		this.game = game;
	}

	public void deselectPiece() {

		if (game.getSelectedPiece() != null) {

			game.getSelectedPiece().setSelected(false);

		}

		game.setSelectedPiece(null);

	}

	public List<Slot> getAdjacentSlots(Slot selectedSlot) {

		List<Slot> adjacentSlots = new ArrayList<Slot>();

		for (Slot[] line : game.getBoard().getSlots()) {
			for (Slot slot : line) {

				if (isAdjacentMove(new Move(selectedSlot, slot))) {

					adjacentSlots.add(selectedSlot);
				}
			}

		}
		return adjacentSlots;
	}

	public List<Piece> getAllPieces() {

		return getAllPieces(null);
	}

	public List<Piece> getAllPieces(PlayerTeam playerTeam) {

		List<Piece> pieces = new ArrayList<Piece>();
		Slot[][] slots = game.getBoard().getSlots();

		for (Slot[] line : slots) {
			for (Slot slot : line) {

				if (slot.isActive() && slot.getPiece() != null) {

					if (playerTeam == null || playerTeam.equals(slot.getPiece().getPlayerTeam())) {

						pieces.add(slot.getPiece());
					}
				}
			}
		}

		return pieces;
	}

	public Slot getEnemySlotInCapture(Move capture) {

		return getSlotByPosition(getEnemyPositionInCapture(capture));
	}

	public Slot getSlotByPosition(Position position) {

		return game.getBoard().getSlots()[position.getLine()][position.getColumn()];
	}

	public void surrender() throws GameOverException {

		endGame();

		if (game.getPlayerInTurn().equals(PlayerTeam.RED)) {
			throw new GameOverException(PlayerTeam.WHITE);
		} else {
			throw new GameOverException(PlayerTeam.RED);
		}

	}

	public boolean isAValidjump(Move move, boolean isKing, PlayerTeam playerTeam) {

		if ((Math.pow(move.getTarget().getPosition().getColumn() - move.getSource().getPosition().getColumn(), 2) != 4)
				|| (Math.pow(move.getTarget().getPosition().getLine() - move.getSource().getPosition().getLine(), 2) != 4)) {

			return false;
		}

		Slot slot = this.getSlotByPosition(move.getTarget().getPosition());
		Slot enemySlot = getEnemySlotInCapture(move);

		return enemySlot.getPiece() != null && !enemySlot.getPiece().getPlayerTeam().equals(playerTeam) && slot != null && slot.isActive()
				&& slot.getPiece() == null && isDiagonalMove(move) && isPositionInBoard(move.getTarget().getPosition())
				&& !(!isKing && !isFowardMove(move, playerTeam));

	}

	public void move(Move move) throws InvalidMove, WrongTurn, GameOverException {

		if (!move.getSource().getPiece().getPlayerTeam().equals(game.getPlayerInTurn().getPlayerTeam())) {

			throw new WrongTurn();
		}

		if (move.getTarget().getPosition().getLine() >= game.getBoard().getSlots().length
				|| move.getTarget().getPosition().getColumn() >= game.getBoard().getSlots()[0].length) {

			throw new InvalidMove();
		}

		if (isAValidjump(move, move.getSource().getPiece().isKing(), move.getSource().getPiece().getPlayerTeam())) {

			jump(move);

		} else {

			if (isThereAJump(move.getSource().getPiece().getPlayerTeam())
					|| !canMove(move, move.getSource().getPiece().isKing(), move.getSource().getPiece().getPlayerTeam())) {

				throw new InvalidMove();
			}

			proceedMove(move);

			deselectPiece();
			resetPossibleMoves();
			changePlayerInTurn();
		}

	}

	public void resetPossibleMoves() {

		for (Slot[] line : game.getBoard().getSlots()) {

			for (Slot slot : line) {

				slot.setSelectable(false);
			}
		}
	}

	public void selectPiece(Piece selectedPiece) {

		deselectPiece();

		game.setSelectedPiece(selectedPiece);
		selectedPiece.setSelected(true);

	}

	protected boolean canMove(Move move, boolean isKing, PlayerTeam playerTeam) {

		Slot slot = this.getSlotByPosition(move.getTarget().getPosition());

		return slot != null && slot.isActive() && slot.getPiece() == null && isDiagonalMove(move) && isPositionInBoard(move.getTarget().getPosition())
				&& isAdjacentMove(move) && !(!isKing && !isFowardMove(move, playerTeam));

	}

	protected boolean isThereAJump(PlayerTeam playerTeam) {

		for (Piece piece : this.getAllPieces(playerTeam)) {

			for (Slot[] targetLine : game.getBoard().getSlots()) {

				for (Slot targetSlot : targetLine) {

					if (isAValidjump(new Move(piece.getSlot(), targetSlot), piece.isKing(), piece.getPlayerTeam())) {

						return true;
					}
				}
			}

		}
		return false;

	}

	protected void writeToFile(String string) {

		File file;
		byte[] dados;
		try {

			file = new File(Environment.getExternalStorageDirectory(), "ceheckers_trace.txt");
			FileOutputStream fos;

			dados = string.getBytes();

			fos = new FileOutputStream(file, true);

			fos.write(dados);
			fos.flush();
			fos.close();
		} catch (Exception e) {

		}
	}

	private void changePlayerInTurn() {

		if (game.getPlayerInTurn().equals(game.getPlayer1())) {

			game.setPlayerInTurn(game.getPlayer2());

		} else {

			game.setPlayerInTurn(game.getPlayer1());
		}
	}

	private void checkGameOver() throws GameOverException {

		if (game.getPlayer1().getPiecesInGame() == 0) {

			throw new GameOverException(game.getPlayer1().getPlayerTeam());
		}

		if (game.getPlayer2().getPiecesInGame() == 0) {

			throw new GameOverException(game.getPlayer2().getPlayerTeam());
		}

		MoveAnalyzer analyzer = new MoveAnalyzer(game);

		List<Move> movesWhite = analyzer.getPossibleMoves(PlayerTeam.WHITE);
		List<Move> movesRed = analyzer.getPossibleMoves(PlayerTeam.RED);

		if ((movesWhite == null || movesWhite.isEmpty()) && (movesRed == null || movesRed.isEmpty())) {
			throw new GameOverException();
		}
		if (movesWhite == null || movesWhite.isEmpty()) {
			throw new GameOverException(PlayerTeam.RED);
		}
		if (movesRed == null || movesRed.isEmpty()) {
			throw new GameOverException(PlayerTeam.WHITE);
		}

	}

	private int determinant(int[][] matrix) {

		return (matrix[0][0] * matrix[1][1]) - (matrix[0][1] * matrix[1][0]);

	}

	private Position getEnemyPositionInCapture(Move move) {

		int x1 = move.getSource().getPosition().getColumn();
		int x2 = move.getTarget().getPosition().getColumn();
		int y1 = move.getSource().getPosition().getLine();
		int y2 = move.getTarget().getPosition().getLine();

		float[] fx = getFX(x1, y1, x2, y2);

		Position enemyPosition;

		if (x1 > x2) {

			enemyPosition = new Position(getLine(fx, x1 - 1), x1 - 1);

		} else {

			enemyPosition = new Position(getLine(fx, x1 + 1), x1 + 1);

		}

		return enemyPosition;
	}

	private float[] getFX(int x1, int y1, int x2, int y2) {

		int[][] coeficientMatrix = { { x1, 1 }, { x2, 1 } };
		int[][] resultMatrix = { { y1 }, { y2 } };

		int d = determinant(coeficientMatrix);
		int dx = determinant(swapColumn(coeficientMatrix, resultMatrix, 0));
		int dy = determinant(swapColumn(coeficientMatrix, resultMatrix, 1));

		float x = dx / d;
		float y = dy / d;

		float[] result = { x, y };

		return result;

	}

	private int getLine(float[] fx, int x) {

		return (int) (fx[0] * x + fx[1]);
	}

	private boolean gotKing(Piece piece) {

		if (piece.getPlayerTeam() == PlayerTeam.RED) {

			return piece.getSlot().getPosition().getLine() == 7;

		} else {

			return piece.getSlot().getPosition().getLine() == 0;
		}

	}

	private boolean isAdjacentMove(Move move) {

		int lineDiference = move.getSource().getPosition().getLine() - move.getTarget().getPosition().getLine();
		int columnDiference = move.getSource().getPosition().getColumn() - move.getTarget().getPosition().getColumn();

		return (lineDiference == -1 || lineDiference == 1) && (columnDiference == -1 || columnDiference == 1);

	}

	private boolean isDiagonalMove(Move move) {

		return move.getSource().getPosition().getColumn() != move.getTarget().getPosition().getColumn()
				&& move.getSource().getPosition().getColumn() != move.getTarget().getPosition().getColumn();
	}

	private boolean isFowardMove(Move move, PlayerTeam playerTeam) {

		if (playerTeam.equals(PlayerTeam.RED)) {

			return move.getSource().getPosition().getLine() < move.getTarget().getPosition().getLine();

		} else {

			return move.getSource().getPosition().getLine() > move.getTarget().getPosition().getLine();
		}

	}

	private boolean isPositionInBoard(Position position) {

		return position.getLine() < 8 && position.getLine() >= 0 && position.getColumn() >= 0 && position.getColumn() < 8;
	}

	private boolean isThereANestedJump(Piece selectedPiece) {

		this.resetPossibleMoves();

		new MoveAnalyzer(game).calculatePossibleJumps(selectedPiece, selectedPiece.getSlot());

		for (Slot[] line : game.getBoard().getSlots()) {
			for (Slot slot : line) {
				if (slot.isSelectable()) {
					return true;
				}
			}
		}

		return false;
	}

	private void jump(Move move) throws GameOverException {

		Slot enemySlot = getEnemySlotInCapture(move);

		game.computeCapture(enemySlot.getPiece());

		enemySlot.getPiece().setSlot(null);
		enemySlot.setPiece(null);

		proceedMove(move);

		if (!isThereANestedJump(move.getTarget().getPiece())) {

			deselectPiece();
			resetPossibleMoves();
			changePlayerInTurn();

		}

	}

	private void proceedMove(Move move) throws GameOverException {

		move.getSource().getPiece().setSlot(move.getTarget());

		move.getTarget().setPiece(move.getSource().getPiece());
		move.getSource().setPiece(null);

		if (gotKing(move.getTarget().getPiece())) {
			move.getTarget().getPiece().setKing(true);
		}

		try {

			checkGameOver();

		} catch (GameOverException e) {
			endGame();
			throw e;
		}

//		 String text =
//		 "service.move(service.getSlotByPosition(new Position({0}, {1})).getPiece(), new Position({2}, {3}));\n";
//		 writeToFile(MessageFormat.format(text, slot.getPosition().getLine(),
//		 slot.getPosition().getColumn(),
//		 move.getTarget().getPosition().getLine(),
//		 move.getTarget().getPosition().getColumn()));

	}

	private int[][] swapColumn(int[][] matrix, int[][] resultMatrix, int column) {

		int[][] newMatrix = new int[2][2];

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {

				if (j == column) {

					newMatrix[i][j] = resultMatrix[i][0];

				} else {

					newMatrix[i][j] = matrix[i][j];

				}
			}

		}

		return newMatrix;
	}

	private void endGame() {
		game.setOn(false);
	}
}
