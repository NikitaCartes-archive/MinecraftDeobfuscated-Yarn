package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.MessageType;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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
									context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), MessageArgumentType.getSignedMessage(context, "message"))
								)
						)
				)
		);
		dispatcher.register(CommandManager.literal("tell").redirect(literalCommandNode));
		dispatcher.register(CommandManager.literal("w").redirect(literalCommandNode));
	}

	private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, SignedChatMessage signedChatMessage) {
		for (ServerPlayerEntity serverPlayerEntity : targets) {
			source.sendFeedback(
				Text.translatable("commands.message.display.outgoing", serverPlayerEntity.getDisplayName(), signedChatMessage.content())
					.formatted(Formatting.GRAY, Formatting.ITALIC),
				false
			);
			serverPlayerEntity.sendChatMessage(signedChatMessage, source.getChatMessageSender(), MessageType.MSG_COMMAND);
		}

		return targets.size();
	}
}
