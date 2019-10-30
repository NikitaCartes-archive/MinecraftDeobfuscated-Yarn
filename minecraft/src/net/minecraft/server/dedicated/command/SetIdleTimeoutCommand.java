package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class SetIdleTimeoutCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("setidletimeout")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
				.then(
					CommandManager.argument("minutes", IntegerArgumentType.integer(0))
						.executes(commandContext -> execute(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "minutes")))
				)
		);
	}

	private static int execute(ServerCommandSource source, int minutes) {
		source.getMinecraftServer().setPlayerIdleTimeout(minutes);
		source.sendFeedback(new TranslatableText("commands.setidletimeout.success", minutes), true);
		return minutes;
	}
}
