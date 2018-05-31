package com.github.albion.rest.command_handling.commands;

import com.github.albion.rest.command_handling.Command;
import com.github.maxopoly.angeliacore.connection.DisconnectReason;
import com.github.maxopoly.angeliacore.connection.ServerConnection;

public class LogoutCommand extends Command {
	public LogoutCommand() {
		super("logout", 0, 0, "exit", "quit");
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		connection.getLogger().info("Good bye, have a nice day!");
		connection.close(DisconnectReason.Intentional_Disconnect);
	}

	@Override
	public String getUsage() {
		return "logout";
	}

}