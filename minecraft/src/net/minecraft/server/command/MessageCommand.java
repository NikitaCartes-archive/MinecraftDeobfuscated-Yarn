package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;

public class MessageCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(
			CommandManager.literal("msg")
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
							MessageArgumentType.SignedMessage signedMessage = MessageArgumentType.getSignedMessage(context, "message");

							try {
								return execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), signedMessage);
							} catch (Exception var3) {
								signedMessage.sendHeader(context.getSource());
								throw var3;
							}
						}))
				)
		);
		dispatcher.register(CommandManager.literal("tell").redirect(literalCommandNode));
		dispatcher.register(CommandManager.literal("w").redirect(literalCommandNode));
	}

	private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, MessageArgumentType.SignedMessage signedMessage) {
		MessageType.Parameters parameters = MessageType.params(MessageType.MSG_COMMAND_INCOMING, source);
		signedMessage.decorate(source, filteredMessage -> {
			FilteredMessage<SentMessage> filteredMessage2 = SentMessage.of(filteredMessage);

			for (ServerPlayerEntity serverPlayerEntity : targets) {
				MessageType.Parameters parameters2 = MessageType.params(MessageType.MSG_COMMAND_OUTGOING, source).withTargetName(serverPlayerEntity.getDisplayName());
				source.sendChatMessage(filteredMessage2.raw(), parameters2);
				SentMessage sentMessage = filteredMessage2.getFilterableFor(source, serverPlayerEntity);
				if (sentMessage != null) {
					serverPlayerEntity.sendChatMessage(sentMessage, parameters);
				}
			}

			filteredMessage2.raw().afterPacketsSent(source.getServer().getPlayerManager());
		});
		return targets.size();
	}
}
