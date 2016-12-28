package com.bilbosoft.chekers.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bilbosoft.chekers.R;
import com.bilbosoft.chekers.R.id;
import com.bilbosoft.chekers.R.layout;
import com.bilbosoft.chekers.domain.Game;
import com.bilbosoft.chekers.domain.Move;
import com.bilbosoft.chekers.domain.Piece;
import com.bilbosoft.chekers.domain.Position;
import com.bilbosoft.chekers.domain.Slot;
import com.bilbosoft.chekers.exception.GameOverException;
import com.bilbosoft.chekers.exception.InvalidMove;
import com.bilbosoft.chekers.exception.WrongTurn;
import com.bilbosoft.chekers.player.BotPlayer;
import com.bilbosoft.chekers.player.HumanPlayer;
import com.bilbosoft.chekers.service.BoardService;
import com.bilbosoft.chekers.service.MoveAnalyzer;

public class BoardAdapter extends BaseAdapter {
	protected Game game;
	private LayoutInflater inflater;
	protected Context context;
	private BoardService service;
	private MoveAnalyzer analyzer;
	private boolean isSelectMode;
	private ImageHandler imageHandler;

	public BoardAdapter(Context context) {
		this.game = Game.getActive();
		this.context = context;
		this.service = new BoardService(game);
		this.imageHandler = new ImageHandler();
		this.analyzer = new MoveAnalyzer(game);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return game.getBoard().getSlots().length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		// O ViewHolder irá guardar a instâncias dos objetos do estado_row
		ViewHolder holder;

		Slot[] line = game.getBoard().getSlots()[position];

		if (convertView == null) { // if it's not recycled, initialize some
			// attributes
			convertView = inflater.inflate(layout.board_cell, null);

			// Cria o Viewholder e guarda a instância dos objetos
			holder = new ViewHolder(position);

			initView(convertView, holder, line);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		return convertView;
	}

	private void initView(View convertView, ViewHolder holder, Slot[] line) {

		holder.ivColumn0.imageView = (ImageView) convertView.findViewById(id.column0);
		holder.ivColumn1.imageView = (ImageView) convertView.findViewById(id.column1);
		holder.ivColumn2.imageView = (ImageView) convertView.findViewById(id.column2);
		holder.ivColumn3.imageView = (ImageView) convertView.findViewById(id.column3);
		holder.ivColumn4.imageView = (ImageView) convertView.findViewById(id.column4);
		holder.ivColumn5.imageView = (ImageView) convertView.findViewById(id.column5);
		holder.ivColumn6.imageView = (ImageView) convertView.findViewById(id.column6);
		holder.ivColumn7.imageView = (ImageView) convertView.findViewById(id.column7);

		boundOnclick(holder);
		imageHandler.loadImages(holder, line);
	}

	private void boundOnclick(ViewHolder holder) {
		holder.ivColumn0.imageView.setOnClickListener(getOnclickListener(holder, holder.ivColumn0));
		holder.ivColumn1.imageView.setOnClickListener(getOnclickListener(holder, holder.ivColumn1));
		holder.ivColumn2.imageView.setOnClickListener(getOnclickListener(holder, holder.ivColumn2));
		holder.ivColumn3.imageView.setOnClickListener(getOnclickListener(holder, holder.ivColumn3));
		holder.ivColumn4.imageView.setOnClickListener(getOnclickListener(holder, holder.ivColumn4));
		holder.ivColumn5.imageView.setOnClickListener(getOnclickListener(holder, holder.ivColumn5));
		holder.ivColumn6.imageView.setOnClickListener(getOnclickListener(holder, holder.ivColumn6));
		holder.ivColumn7.imageView.setOnClickListener(getOnclickListener(holder, holder.ivColumn7));
	}

	private BoardCellListener getOnclickListener(ViewHolder holder, CellHolder cellHolder) {

		Piece piece = service.getSlotByPosition(new Position(cellHolder.line, cellHolder.column)).getPiece();

		if (piece != null || isSelectMode) {

			return new BoardCellListener(this, cellHolder, context, holder);

		}

		return null;

	}

	protected void onSelect(ViewHolder holder, CellHolder cellHolder) {

		Slot selectedSlot = service.getSlotByPosition(new Position(cellHolder.line, cellHolder.column));
		Piece selectedPiece = selectedSlot.getPiece();

		if (isSelectMode) {

			if (selectedPiece == null) {
				if (!selectedSlot.isSelectable()) {
					return;
				}

				moveSelectedPiece(holder, cellHolder);

				if (game.getSelectedPiece() == null) {
					isSelectMode = false;
				}

			} else {

				if (!selectedPiece.getPlayerTeam().equals(game.getPlayerInTurn().getPlayerTeam())) {

					return;
				}

				selectPiece(holder, cellHolder, selectedPiece);

				Toast.makeText(context, "line: " + cellHolder.line + ", column: " + cellHolder.column, Toast.LENGTH_SHORT).show();

			}

		} else {

			if (selectedPiece == null || !selectedPiece.getPlayerTeam().equals(game.getPlayerInTurn().getPlayerTeam())) {

				return;
			}

			isSelectMode = true;
			selectPiece(holder, cellHolder, selectedPiece);
		}

		Void v = null;

		final Handler handler = new Handler();

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				while (game.isOn() && game.getPlayerInTurn() instanceof BotPlayer) {

					Move move = game.getPlayerInTurn().getNextMove();

					service.selectPiece(move.getSource().getPiece());
					analyzer.calculatePossibleMoves(move.getSource().getPiece());

					try {
						service.move(move);

						handler.post(new Runnable() {

							@Override
							public void run() {
								redrawView();

							}
						});
					} catch (InvalidMove e) {
					} catch (WrongTurn e) {
					} catch (GameOverException e) {
						Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}
				return null;
			}
		}.execute(v);

		redrawView();

	}

	private void selectPiece(ViewHolder holder, CellHolder cellHolder, Piece piece) {

		service.selectPiece(piece);
		analyzer.calculatePossibleMoves(piece);

		redrawView();
	}

	protected void moveSelectedPiece(ViewHolder holder, CellHolder cellHolder) {

		Slot target = service.getSlotByPosition(new Position(cellHolder.line, cellHolder.column));

		try {

			if (game.getPlayerInTurn() instanceof HumanPlayer) {

				((HumanPlayer) game.getPlayerInTurn()).setTarget(target);
			}

			service.move(game.getPlayerInTurn().getNextMove());

			redrawView();

		} catch (InvalidMove e) {
			// Just ignore
		} catch (WrongTurn e) {
			Toast.makeText(context, "Não é a sua vez", Toast.LENGTH_SHORT).show();
		} catch (GameOverException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

	}

	protected void redrawView() {
		Activity activity = (Activity) context;
		ListView gridview = (ListView) activity.findViewById(R.id.board);
		gridview.setAdapter(this);

	}

}