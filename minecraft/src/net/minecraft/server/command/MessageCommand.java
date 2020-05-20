package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class MessageCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(
			CommandManager.literal("msg")
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("message", MessageArgumentType.message())
								.executes(
									commandContext -> execute(
											commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), MessageArgumentType.getMessage(commandContext, "message")
										)
								)
						)
				)
		);
		dispatcher.register(CommandManager.literal("tell").redirect(literalCommandNode));
		dispatcher.register(CommandManager.literal("w").redirect(literalCommandNode));
	}

	private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text message) {
		UUID uUID = source.getEntity() == null ? Util.field_25140 : source.getEntity().getUuid();

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.sendSystemMessage(
				new TranslatableText("commands.message.display.incoming", source.getDisplayName(), message).formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC}),
				uUID
			);
			source.sendFeedback(
				new TranslatableText("commands.message.display.outgoing", serverPlayerEntity.getDisplayName(), message)
					.formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC}),
				false
			);
		}

		return targets.size();
	}
}
