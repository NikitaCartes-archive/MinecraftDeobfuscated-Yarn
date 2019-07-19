package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

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
		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.sendMessage(
				new TranslatableText("commands.message.display.incoming", source.getDisplayName(), message.deepCopy())
					.formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC})
			);
			source.sendFeedback(
				new TranslatableText("commands.message.display.outgoing", serverPlayerEntity.getDisplayName(), message.deepCopy())
					.formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC}),
				false
			);
		}

		return targets.size();
	}
}
