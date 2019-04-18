package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.arguments.ComponentArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextFormatter;

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
												TextFormatter.resolveAndStyle(commandContext.getSource(), ComponentArgumentType.getComponent(commandContext, "message"), serverPlayerEntity)
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
