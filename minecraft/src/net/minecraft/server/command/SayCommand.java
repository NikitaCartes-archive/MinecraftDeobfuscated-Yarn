package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class SayCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("say")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("message", MessageArgumentType.create())
						.executes(
							commandContext -> {
								TextComponent textComponent = MessageArgumentType.getMessageArgument(commandContext, "message");
								commandContext.getSource()
									.getMinecraftServer()
									.getConfigurationManager()
									.sendToAll(new TranslatableTextComponent("chat.type.announcement", commandContext.getSource().getDisplayName(), textComponent));
								return 1;
							}
						)
				)
		);
	}
}
