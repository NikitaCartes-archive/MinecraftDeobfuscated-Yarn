package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.TextArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Texts;

public class TellRawCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("tellraw")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("message", TextArgumentType.create())
								.executes(
									commandContext -> {
										int i = 0;

										for (ServerPlayerEntity serverPlayerEntity : EntityArgumentType.getPlayers(commandContext, "targets")) {
											serverPlayerEntity.sendMessage(
												Texts.parse(commandContext.getSource(), TextArgumentType.getTextArgument(commandContext, "message"), serverPlayerEntity, 0)
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
