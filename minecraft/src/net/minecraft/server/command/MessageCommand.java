package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;

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

	private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, MessageArgumentType.SignedMessage signedMessage) {
		if (targets.isEmpty()) {
			return 0;
		} else {
			MessageSender messageSender = source.getChatMessageSender();
			signedMessage.decorate(source).thenAcceptAsync(decoratedMessage -> {
				for (ServerPlayerEntity serverPlayerEntity : targets) {
					MessageSender messageSender2 = messageSender.withTargetName(serverPlayerEntity.getDisplayName());
					source.sendChatMessage(messageSender2, (SignedMessage)decoratedMessage.raw(), MessageType.MSG_COMMAND_OUTGOING);
					SignedMessage signedMessagex = (SignedMessage)decoratedMessage.getFilterableFor(source, serverPlayerEntity);
					if (signedMessagex != null) {
						serverPlayerEntity.sendChatMessage(signedMessagex, messageSender, MessageType.MSG_COMMAND_INCOMING);
					}
				}
			}, source.getServer());
			return targets.size();
		}
	}
}
