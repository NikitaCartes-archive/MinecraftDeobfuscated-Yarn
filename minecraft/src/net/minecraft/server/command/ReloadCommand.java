package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.text.TranslatableText;

public class ReloadCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("reload").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).executes(commandContext -> {
			commandContext.getSource().sendFeedback(new TranslatableText("commands.reload.success"), true);
			commandContext.getSource().getMinecraftServer().reload();
			return 0;
		}));
	}
}
