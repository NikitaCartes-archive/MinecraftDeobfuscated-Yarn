package net.minecraft.server.dedicated;

import net.minecraft.server.command.ServerCommandSource;

public class PendingServerCommand {
	public final String command;
	public final ServerCommandSource source;

	public PendingServerCommand(String command, ServerCommandSource commandSource) {
		this.command = command;
		this.source = commandSource;
	}
}
