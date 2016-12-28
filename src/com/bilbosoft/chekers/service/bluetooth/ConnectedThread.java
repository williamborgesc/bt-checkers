package com.bilbosoft.chekers.service.bluetooth;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.bilbosoft.chekers.domain.Game;
import com.bilbosoft.chekers.domain.Move;
import com.bilbosoft.chekers.domain.Position;
import com.bilbosoft.chekers.exception.GameOverException;
import com.bilbosoft.chekers.exception.InvalidMove;
import com.bilbosoft.chekers.exception.WrongTurn;
import com.bilbosoft.chekers.player.Player;
import com.bilbosoft.chekers.service.BoardService;

public class ConnectedThread extends Thread {

	private final BluetoothSocket mmSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;
	public Handler mHandler;
	public BoardService service;
	public boolean isWaiting = true;
	public boolean received = false;

	public ConnectedThread(BluetoothSocket socket, Handler mHandler) {
		mmSocket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;
		this.mHandler = mHandler;
		service = new BoardService(Game.getActive());

		// Get the input and output streams, using temp objects because
		// member streams are final
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
		}

		mmInStream = tmpIn;
		mmOutStream = tmpOut;
	}

	public void run() {

		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(mmInStream));

		Player playerinTurn = null;

		// Keep listening to the InputStream until an exception occurs
		while (true) {
			try {
				// Read from the InputStream

				if (isWaiting) {

					playerinTurn = Game.getActive().getPlayerInTurn();
					receive(inFromServer.readLine());
					received = true;

					if (playerinTurn.equals(Game.getActive().getPlayerInTurn())) {
						isWaiting = false;
					} else {
						// TODO retirando marcação verde irritante
						service.resetPossibleMoves();
					}

				}

			} catch (IOException e) {
				break;
			}
		}
	}

	/* Call this from the main activity to send data to the remote device */
	public void write(Move move) {

		DataOutputStream outToServer = new DataOutputStream(mmOutStream);

		String request = "Erro";

		try {

			Player playerinTurn = Game.getActive().getPlayerInTurn();

			request = move.getSource().getPosition().getLine() + "," + move.getSource().getPosition().getColumn() + ","
					+ move.getTarget().getPosition().getLine() + "," + move.getTarget().getPosition().getColumn();

			service.move(move);

			if (!playerinTurn.equals(Game.getActive().getPlayerInTurn())) {

				isWaiting = true;
			}

		} catch (InvalidMove e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongTurn e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GameOverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Envia o pacote
		try {
			outToServer.writeBytes(request + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void receive(String message) {

		try {

			System.out.println("Response from Server: " + message);

			if (message.startsWith("erro:")) {

				System.out.println(message);

			} else if (message.startsWith("info:")) {

				System.out.println(message);

			} else {

				String[] params = message.split(",");

				if (params.length == 4) {
					try {
						service.move(new Move(service.getSlotByPosition(new Position(Integer.valueOf(params[0]), Integer.valueOf(params[1]))), service
								.getSlotByPosition(new Position(Integer.valueOf(params[2]), Integer.valueOf(params[3])))));

					} catch (InvalidMove e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WrongTurn e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (GameOverException e) {

						mHandler.post(new Runnable() {

							@Override
							public void run() {

							}
						});
					}
				}
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
		}
	}
}