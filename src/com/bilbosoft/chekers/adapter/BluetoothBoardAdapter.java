package com.bilbosoft.chekers.adapter;

import android.content.Context;

import com.bilbosoft.chekers.domain.Position;
import com.bilbosoft.chekers.domain.Slot;
import com.bilbosoft.chekers.enums.GameMode;
import com.bilbosoft.chekers.player.HumanPlayer;
import com.bilbosoft.chekers.service.BoardService;
import com.bilbosoft.chekers.service.bluetooth.ConnectedThread;

public class BluetoothBoardAdapter extends BoardAdapter {

	private BoardService service;
	private ConnectedThread connectedThread;

	public BluetoothBoardAdapter(Context context) {
		super(context);

		service = new BoardService(game);
	}

	@Override
	protected void moveSelectedPiece(ViewHolder holder, CellHolder cellHolder) {

		Slot target = service.getSlotByPosition(new Position(cellHolder.line, cellHolder.column));

		if (game.getPlayerInTurn() instanceof HumanPlayer) {

			((HumanPlayer) game.getPlayerInTurn()).setTarget(target);
		}

		connectedThread.write(game.getPlayerInTurn().getNextMove());

		redrawView();

	}

	public void setConnectedThread(ConnectedThread connectedThread) {
		this.connectedThread = connectedThread;
	}

	@Override
	protected void onSelect(ViewHolder holder, CellHolder cellHolder) {
		if ((isGameMode(GameMode.BLUETOTH_HOST) && game.getPlayerInTurn().equals(game.getPlayer1()))
				|| (isGameMode(GameMode.BLUETOTH_JOIN) && game.getPlayerInTurn().equals(game.getPlayer2()))) {
			super.onSelect(holder, cellHolder);
		}

	}

	private boolean isGameMode(GameMode gameMode) {
		return game.getGameMode().equals(gameMode);
	}
}
