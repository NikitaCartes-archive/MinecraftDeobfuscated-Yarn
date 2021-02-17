package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
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
										ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
										serverPlayerEntity.getTextStream()
											.filterText(string)
											.thenAcceptAsync(
												arg -> {
													String stringx = arg.method_33803();
													Text text = stringx.isEmpty() ? null : method_31373(commandContext, stringx);
													Text text2 = method_31373(commandContext, arg.method_33801());
													minecraftServer.getPlayerManager()
														.method_33810(
															text2, serverPlayerEntity2 -> serverPlayerEntity.method_33795(serverPlayerEntity2) ? text : text2, MessageType.CHAT, entity.getUuid()
														);
												},
												minecraftServer
											);
										return 1;
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
