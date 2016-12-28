package com.bilbosoft.chekers.domain;

import com.bilbosoft.chekers.enums.GameMode;
import com.bilbosoft.chekers.enums.PlayerTeam;
import com.bilbosoft.chekers.exception.InvalidGameModeException;
import com.bilbosoft.chekers.player.BotPlayer;
import com.bilbosoft.chekers.player.HumanPlayer;
import com.bilbosoft.chekers.player.Player;

public class Game {

	private Board board;

	private Piece selectedPiece;

	private Player playerInTurn;

	private Player player1;

	private Player player2;

	private boolean isOn;

	private static Game instance;

	private GameMode gameMode;

	private Game(Player player1, Player player2) {

		if (player1.getPlayerTeam().equals(player2.getPlayerTeam())) {

			throw new IllegalArgumentException();
		}

		this.player1 = player1;
		this.player2 = player2;
		board = new Board();
		board.setSlots(new Slot[8][8]);
		playerInTurn = player1;
		isOn = true;
		initSlots();
	}

	public static void startNew(GameMode gameMode) throws InvalidGameModeException {

		switch (gameMode) {

		case HUMAN_VS_HUMAN:

			instance = new Game(new HumanPlayer(PlayerTeam.WHITE), new HumanPlayer(PlayerTeam.RED));
			break;

		case HUMAN_VS_CPU:

			instance = new Game(new HumanPlayer(PlayerTeam.WHITE), new BotPlayer(PlayerTeam.RED));

			break;

		case BLUETOTH_HOST:

			instance = new Game(new HumanPlayer(PlayerTeam.WHITE), new HumanPlayer(PlayerTeam.RED));
			break;
		case BLUETOTH_JOIN:

			instance = new Game(new HumanPlayer(PlayerTeam.WHITE), new HumanPlayer(PlayerTeam.RED));
			break;

		case BOT_VS_BOT:

			instance = new Game(new BotPlayer(PlayerTeam.WHITE), new BotPlayer(PlayerTeam.RED));
			break;

		default:
			throw new InvalidGameModeException();
		}

		instance.setGameMode(gameMode);
	}

	public static Game getActive() {

		if (instance == null) {
			throw new IllegalStateException();
		}

		return instance;
	}

	private void initSlots() {

		boolean active = true;

		for (int line = 0; line < 8; line++) {
			for (int column = 0; column < 8; column++) {

				Slot slot = new Slot(new Position(line, column));

				slot.setActive(active);

				if (active) {

					Piece piece = null;

					if (line < 3) {
						piece = new Piece(PlayerTeam.RED, slot);
					}
					if (line > 4) {
						piece = new Piece(PlayerTeam.WHITE, slot);
					}

					slot.setPiece(piece);

				}

				active = !active;
				board.getSlots()[line][column] = slot;
			}

			active = !active;
		}

		player1.setPiecesInGame(12);
		player2.setPiecesInGame(12);
	}

	public void computeCapture(Piece captured) {

		if (captured.getPlayerTeam().equals(player1.getPlayerTeam())) {

			player1.setPiecesInGame(player1.getPiecesInGame() - 1);

		} else {

			player2.setPiecesInGame(player2.getPiecesInGame() - 1);
		}

	}

	@Override
	public Game clone() {

		Game cloneGame = new Game(player1, player2);

		cloneGame.setPlayerInTurn(this.playerInTurn);

		if (selectedPiece != null) {

			cloneGame.setSelectedPiece(selectedPiece.clone(selectedPiece.getSlot().clone(selectedPiece.getSlot().getPosition())));

		}

		cloneGame.setBoard(board.clone());

		return cloneGame;
	}

	public boolean isOn() {
		return isOn;
	}

	public void setOn(boolean gameOn) {
		this.isOn = gameOn;
	}

	public Piece getSelectedPiece() {
		return selectedPiece;
	}

	public void setSelectedPiece(Piece selectedPiece) {
		this.selectedPiece = selectedPiece;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public Player getPlayerInTurn() {
		return playerInTurn;
	}

	public void setPlayerInTurn(Player playerInTurn) {
		this.playerInTurn = playerInTurn;
	}
}
