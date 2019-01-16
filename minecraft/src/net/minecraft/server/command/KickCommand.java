package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class KickCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("kick")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
				.then(
					ServerCommandManager.argument("targets", EntityArgumentType.multiplePlayer())
						.executes(
							commandContext -> method_13411(
									commandContext.getSource(), EntityArgumentType.method_9312(commandContext, "targets"), new TranslatableTextComponent("multiplayer.disconnect.kicked")
								)
						)
						.then(
							ServerCommandManager.argument("reason", MessageArgumentType.create())
								.executes(
									commandContext -> method_13411(
											commandContext.getSource(),
											EntityArgumentType.method_9312(commandContext, "targets"),
											MessageArgumentType.getMessageArgument(commandContext, "reason")
										)
								)
						)
				)
		);
	}

	private static int method_13411(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, TextComponent textComponent) {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.disconnect(textComponent);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.kick.success", serverPlayerEntity.getDisplayName(), textComponent), true);
		}

		return collection.size();
	}
}
