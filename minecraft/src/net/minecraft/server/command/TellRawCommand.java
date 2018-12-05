package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.arguments.ComponentArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextFormatter;

public class TellRawCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("tellraw")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("targets", EntityArgumentType.method_9308())
						.then(
							ServerCommandManager.argument("message", ComponentArgumentType.create())
								.executes(
									commandContext -> {
										int i = 0;

										for (ServerPlayerEntity serverPlayerEntity : EntityArgumentType.method_9312(commandContext, "targets")) {
											serverPlayerEntity.appendCommandFeedback(
												TextFormatter.method_10881(commandContext.getSource(), ComponentArgumentType.getComponentArgument(commandContext, "message"), serverPlayerEntity)
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
