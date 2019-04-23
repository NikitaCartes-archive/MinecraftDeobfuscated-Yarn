package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class SayCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("say")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("message", MessageArgumentType.create())
						.executes(
							commandContext -> {
								Component component = MessageArgumentType.getMessage(commandContext, "message");
								commandContext.getSource()
									.getMinecraftServer()
									.getPlayerManager()
									.sendToAll(new TranslatableComponent("chat.type.announcement", commandContext.getSource().getDisplayName(), component));
								return 1;
							}
						)
				)
		);
	}
}
