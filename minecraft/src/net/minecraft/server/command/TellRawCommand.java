package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Texts;

public class TellRawCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("tellraw")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("message", TextArgumentType.text())
								.executes(
									context -> {
										int i = 0;

										for (ServerPlayerEntity serverPlayerEntity : EntityArgumentType.getPlayers(context, "targets")) {
											serverPlayerEntity.sendMessageToClient(
												Texts.parse(context.getSource(), TextArgumentType.getTextArgument(context, "message"), serverPlayerEntity, 0), false
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
