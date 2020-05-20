package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class MeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("me")
				.then(
					CommandManager.argument("action", StringArgumentType.greedyString())
						.executes(
							commandContext -> {
								TranslatableText translatableText = new TranslatableText(
									"chat.type.emote", commandContext.getSource().getDisplayName(), StringArgumentType.getString(commandContext, "action")
								);
								Entity entity = commandContext.getSource().getEntity();
								if (entity != null) {
									commandContext.getSource().getMinecraftServer().getPlayerManager().broadcastChatMessage(translatableText, MessageType.CHAT, entity.getUuid());
								} else {
									commandContext.getSource().getMinecraftServer().getPlayerManager().broadcastChatMessage(translatableText, MessageType.SYSTEM, Util.field_25140);
								}

								return 1;
							}
						)
				)
		);
	}
}
