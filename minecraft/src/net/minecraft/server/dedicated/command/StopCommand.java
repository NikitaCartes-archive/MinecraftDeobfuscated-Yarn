package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;

public class StopCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("stop").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4)).executes(commandContext -> {
				commandContext.getSource().method_9226(new TranslatableTextComponent("commands.stop.stopping"), true);
				commandContext.getSource().getMinecraftServer().stop(false);
				return 1;
			})
		);
	}
}
