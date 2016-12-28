package com.bilbosoft.chekers;

import java.util.UUID;

public interface Constants {

	int INIT_LOCAL_GAME = 1;
	int INIT_BLUETOOTH_GAME_HOST = 2;
	int INIT_BLUETOOTH_GAME_CLIENT = 3;
	int REQUEST_ENABLE_BT = 4;
	int REQUEST_DISCOVERABLE_BT = 5;
	int MESSAGE_READ = 6;
	String SERVICE_NAME = "BLUETOOTH_CHECKERS";
	String SERVICE_UUID = "BLUETOOTH_CHECKERS";
	UUID SERVER_UUID = UUID.fromString("1b5bbc58-44dd-4746-8c1f-7b5a7f661bef");
}
