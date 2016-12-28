package com.bilbosoft.chekers;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bilbosoft.chekers.domain.Game;
import com.bilbosoft.chekers.enums.GameMode;
import com.bilbosoft.chekers.exception.InvalidGameModeException;
import static com.bilbosoft.chekers.Constants.*;

public class MainActivity extends ListActivity {

	private String[] options;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// Criar um array de Strings, que será utilizado em seu ListActivity
		options = new String[] { GameMode.HUMAN_VS_CPU.toString(), GameMode.HUMAN_VS_HUMAN.toString(), GameMode.BLUETOTH_HOST.toString(),
				GameMode.BLUETOTH_JOIN.toString(), GameMode.BOT_VS_BOT.toString() };

		// Criar um ArrayAdapter, que vai fazer aparecer as Strings acima
		// em seu ListView
		this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		try {

			String gameModeSelected = this.getListAdapter().getItem(position).toString();

			GameMode gameMode = GameMode.valueOf(gameModeSelected);
			Game.startNew(gameMode);

			if (GameMode.BLUETOTH_HOST.equals(gameMode)) {

				Intent it = new Intent(this, BluetoothHostActivity.class);

				startActivityForResult(it, INIT_BLUETOOTH_GAME_HOST);

			} else if (GameMode.BLUETOTH_JOIN.equals(gameMode)) {

				Intent it = new Intent(this, BluetoothJoinServerActivity.class);

				startActivityForResult(it, INIT_BLUETOOTH_GAME_CLIENT);

			} else {

				Intent it = new Intent(this, BoardActivity.class);

				startActivityForResult(it, INIT_LOCAL_GAME);
			}

		} catch (InvalidGameModeException e) {
			Toast.makeText(this, "Invalid Choice!", Toast.LENGTH_SHORT).show();
		}
	}

}