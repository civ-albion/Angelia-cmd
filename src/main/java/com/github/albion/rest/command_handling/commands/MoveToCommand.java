package com.github.albion.rest.command_handling.commands;

import com.github.maxopoly.angeliacore.model.location.Location;

import com.github.albion.rest.command_handling.Command;
import com.github.maxopoly.angeliacore.actions.actions.MoveTo;
import com.github.maxopoly.angeliacore.connection.ServerConnection;

public class MoveToCommand extends Command {

	public MoveToCommand() {
		super("move", 3, 3, "moveto", "goto");
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		int x, y, z;
		try {
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			z = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			connection.getLogger().warn("One of the args supplied was not a valid integer");
			return;
		}
		connection.getActionQueue().queue(
				new MoveTo(connection, new Location(x, y, z).getBlockCenterXZ(), MoveTo.SPRINTING_SPEED));
		connection.getLogger().info("Queued movement to " + x + " " + y + " " + z);
	}

	@Override
	public String getUsage() {
		return "moveto <x> <y> <z>";
	}

}
