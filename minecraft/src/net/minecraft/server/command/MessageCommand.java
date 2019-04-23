package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.ChatFormat;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.network.ServerPlayerEntity;

public class MessageCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register(
			CommandManager.literal("msg")
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("message", MessageArgumentType.create())
								.executes(
									commandContext -> execute(
											commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), MessageArgumentType.getMessage(commandContext, "message")
										)
								)
						)
				)
		);
		commandDispatcher.register(CommandManager.literal("tell").redirect(literalCommandNode));
		commandDispatcher.register(CommandManager.literal("w").redirect(literalCommandNode));
	}

	private static int execute(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Component component) {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.sendMessage(
				new TranslatableComponent("commands.message.display.incoming", serverCommandSource.getDisplayName(), component.copy())
					.applyFormat(new ChatFormat[]{ChatFormat.field_1080, ChatFormat.field_1056})
			);
			serverCommandSource.sendFeedback(
				new TranslatableComponent("commands.message.display.outgoing", serverPlayerEntity.getDisplayName(), component.copy())
					.applyFormat(new ChatFormat[]{ChatFormat.field_1080, ChatFormat.field_1056}),
				false
			);
		}

		return collection.size();
	}
}
