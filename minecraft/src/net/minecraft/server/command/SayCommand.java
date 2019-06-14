package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class SayCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("say")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("message", MessageArgumentType.create())
						.executes(
							commandContext -> {
								Text text = MessageArgumentType.getMessage(commandContext, "message");
								commandContext.getSource()
									.getMinecraftServer()
									.getPlayerManager()
									.sendToAll(new TranslatableText("chat.type.announcement", commandContext.getSource().getDisplayName(), text));
								return 1;
							}
						)
				)
		);
	}
}
