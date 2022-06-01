package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.PlayerManager;

public class MeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("me")
				.then(
					CommandManager.argument("action", MessageArgumentType.message())
						.executes(
							context -> {
								MessageArgumentType.SignedMessage signedMessage = MessageArgumentType.getSignedMessage(context, "action");
								ServerCommandSource serverCommandSource = context.getSource();
								PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
								signedMessage.decorate(serverCommandSource)
									.thenAcceptAsync(
										decoratedMessage -> playerManager.broadcast(decoratedMessage, serverCommandSource, MessageType.EMOTE_COMMAND), serverCommandSource.getServer()
									);
								return 1;
							}
						)
				)
		);
	}
}
