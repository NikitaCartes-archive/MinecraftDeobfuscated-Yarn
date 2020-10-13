package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.class_5513;
import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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
								String string = StringArgumentType.getString(commandContext, "action");
								Entity entity = commandContext.getSource().getEntity();
								MinecraftServer minecraftServer = commandContext.getSource().getMinecraftServer();
								if (entity != null) {
									if (entity instanceof ServerPlayerEntity) {
										class_5513 lv = ((ServerPlayerEntity)entity).method_31273();
										if (lv != null) {
											lv.method_31288(string)
												.thenAcceptAsync(
													optional -> optional.ifPresent(
															stringx -> minecraftServer.getPlayerManager().broadcastChatMessage(method_31373(commandContext, stringx), MessageType.CHAT, entity.getUuid())
														),
													minecraftServer
												);
											return 1;
										}
									}

									minecraftServer.getPlayerManager().broadcastChatMessage(method_31373(commandContext, string), MessageType.CHAT, entity.getUuid());
								} else {
									minecraftServer.getPlayerManager().broadcastChatMessage(method_31373(commandContext, string), MessageType.SYSTEM, Util.NIL_UUID);
								}

								return 1;
							}
						)
				)
		);
	}

	private static Text method_31373(CommandContext<ServerCommandSource> commandContext, String string) {
		return new TranslatableText("chat.type.emote", commandContext.getSource().getDisplayName(), string);
	}
}
