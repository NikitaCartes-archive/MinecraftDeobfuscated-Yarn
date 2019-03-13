package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.text.TranslatableTextComponent;

public class MeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("me")
				.then(
					ServerCommandManager.argument("action", StringArgumentType.greedyString())
						.executes(
							commandContext -> {
								commandContext.getSource()
									.getMinecraftServer()
									.method_3760()
									.sendToAll(
										new TranslatableTextComponent("chat.type.emote", commandContext.getSource().method_9223(), StringArgumentType.getString(commandContext, "action"))
									);
								return 1;
							}
						)
				)
		);
	}
}
