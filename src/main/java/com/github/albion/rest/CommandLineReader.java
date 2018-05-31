package com.github.albion.rest;

import com.github.albion.rest.command_handling.CommandHandler;
import com.github.maxopoly.angeliacore.connection.ActiveConnectionManager;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.connection.play.packets.out.ChatPacket;
import java.io.Console;
import java.io.IOException;
import org.apache.logging.log4j.Logger;

public class CommandLineReader {

	private Mode mode;

	enum Mode {
		CHAT, COMMAND;
	}

	private Logger logger;
	private ActiveConnectionManager connectionMan;
	private String playerName;
	private CommandHandler cmdHandler;

	public CommandLineReader(Logger logger, ActiveConnectionManager connectionMan, String playerName,
			CommandHandler cmdHandler) {
		this.logger = logger;
		this.connectionMan = connectionMan;
		this.playerName = playerName;
		this.mode = Mode.COMMAND;
		getConnection().getLogger().info("--- Set console to command mode");
		this.cmdHandler = cmdHandler;
	}

	public void start() {
		Console c = System.console();
		if (c == null) {
			logger.error("No open console was found, assuming we are running as daemon and continue anyway");
			return;
		}
		while (true) {
			String msg = c.readLine("");
			if (msg.equals("++")) {
				if (mode == Mode.CHAT) {
					mode = Mode.COMMAND;
					logger.info("--- Switched to command mode");
				} else if (mode == Mode.COMMAND) {
					mode = Mode.CHAT;
					logger.info("--- Switched to chat mode");
				}
				continue;
			}
			if (mode == Mode.CHAT) {
				sendChatMsg(msg);
				continue;
			}
			if (mode == Mode.COMMAND) {
				cmdHandler.handle(msg, getConnection());
			}
		}
	}

	private void sendChatMsg(String msg) {
		ChatPacket packet;
		try {
			packet = new ChatPacket(msg);
		} catch (IOException e) {
			logger.error("Failed to create msg packet with msg " + msg, e);
			return;
		}
		try {
			getConnection().sendPacket(packet);
		} catch (IOException e) {
			logger.error("Failed to send message, server might have disconnected?");
		}
	}

	/**
	 * Gets the players current connection. This needs to be dynamic due to reconnecting
	 * 
	 * @return Active connection
	 */
	public ServerConnection getConnection() {
		return connectionMan.getConnection(playerName);
	}

}
