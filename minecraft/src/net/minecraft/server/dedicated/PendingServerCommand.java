package net.minecraft.server.dedicated;

import net.minecraft.server.command.ServerCommandSource;

public class PendingServerCommand {
	public final String command;
	public final ServerCommandSource source;

	public PendingServerCommand(String string, ServerCommandSource serverCommandSource) {
		this.command = string;
		this.source = serverCommandSource;
	}
}
