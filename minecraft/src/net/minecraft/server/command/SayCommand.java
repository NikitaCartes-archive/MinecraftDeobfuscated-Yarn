package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class SayCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("say")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("message", MessageArgumentType.create())
						.executes(
							commandContext -> {
								TextComponent textComponent = MessageArgumentType.getMessage(commandContext, "message");
								commandContext.getSource()
									.getMinecraftServer()
									.getPlayerManager()
									.sendToAll(new TranslatableTextComponent("chat.type.announcement", commandContext.getSource().getDisplayName(), textComponent));
								return 1;
							}
						)
				)
		);
	}
}
