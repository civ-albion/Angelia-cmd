package com.github.albion.rest;

import com.github.albion.rest.command_handling.CommandHandler;
import com.github.albion.rest.listener.ChatListener;
import com.github.albion.rest.listener.PlayerStateListener;
import com.github.maxopoly.angeliacore.connection.ActiveConnectionManager;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
	private static Logger logger = LogManager.getLogger("Main");
	private static CommandHandler cmdHandler;
	private static ActiveConnectionManager connManager;

	public  void main(String[] args) {
		connManager = ActiveConnectionManager.getInstance();
		ServerConnection connection = StartUpCommandParser.parse(args, logger);
		if (connection == null) {
			System.exit(0);
			return;
		}
		connManager.initConnection(connection, false, null);
		connection.getEventHandler().registerListener(new ChatListener(logger));
		connection.getEventHandler().registerListener(new PlayerStateListener(logger));
		cmdHandler = new CommandHandler(logger);
		CommandLineReader reader = new CommandLineReader(logger, connManager, connection.getPlayerName(), cmdHandler);
		reader.start();
	}
}
