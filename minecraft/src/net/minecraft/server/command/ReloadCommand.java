package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.text.TranslatableTextComponent;

public class ReloadCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("reload").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3)).executes(commandContext -> {
				commandContext.getSource().method_9226(new TranslatableTextComponent("commands.reload.success"), true);
				commandContext.getSource().getMinecraftServer().reload();
				return 0;
			})
		);
	}
}
