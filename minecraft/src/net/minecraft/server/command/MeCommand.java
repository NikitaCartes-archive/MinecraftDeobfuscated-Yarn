package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.text.TranslatableText;

public class MeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("me")
				.then(
					CommandManager.argument("action", StringArgumentType.greedyString())
						.executes(
							commandContext -> {
								commandContext.getSource()
									.getMinecraftServer()
									.getPlayerManager()
									.sendToAll(
										new TranslatableText("chat.type.emote", commandContext.getSource().getDisplayName(), StringArgumentType.getString(commandContext, "action"))
									);
								return 1;
							}
						)
				)
		);
	}
}
