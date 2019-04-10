package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;

public class StopCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("stop").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4)).executes(commandContext -> {
				commandContext.getSource().sendFeedback(new TranslatableTextComponent("commands.stop.stopping"), true);
				commandContext.getSource().getMinecraftServer().stop(false);
				return 1;
			})
		);
	}
}
