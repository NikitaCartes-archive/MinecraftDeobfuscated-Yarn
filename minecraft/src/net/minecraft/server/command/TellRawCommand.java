package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.arguments.ComponentArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.network.chat.Components;
import net.minecraft.server.network.ServerPlayerEntity;

public class TellRawCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("tellraw")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("message", ComponentArgumentType.create())
								.executes(
									commandContext -> {
										int i = 0;

										for (ServerPlayerEntity serverPlayerEntity : EntityArgumentType.getPlayers(commandContext, "targets")) {
											serverPlayerEntity.sendMessage(
												Components.resolveAndStyle(commandContext.getSource(), ComponentArgumentType.getComponent(commandContext, "message"), serverPlayerEntity, 0)
											);
											i++;
										}

										return i;
									}
								)
						)
				)
		);
	}
}
