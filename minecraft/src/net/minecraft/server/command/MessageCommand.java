package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;

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

	private static int execute(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, TextComponent textComponent) {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.sendMessage(
				new TranslatableTextComponent("commands.message.display.incoming", serverCommandSource.getDisplayName(), textComponent.copy())
					.applyFormat(new TextFormat[]{TextFormat.field_1080, TextFormat.field_1056})
			);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.message.display.outgoing", serverPlayerEntity.getDisplayName(), textComponent.copy())
					.applyFormat(new TextFormat[]{TextFormat.field_1080, TextFormat.field_1056}),
				false
			);
		}

		return collection.size();
	}
}
