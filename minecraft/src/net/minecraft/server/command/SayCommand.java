package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.PlayerManager;

public class SayCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("say")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("message", MessageArgumentType.message())
						.executes(
							context -> {
								MessageArgumentType.SignedMessage signedMessage = MessageArgumentType.getSignedMessage(context, "message");
								ServerCommandSource serverCommandSource = context.getSource();
								PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
								signedMessage.decorate(serverCommandSource)
									.thenAcceptAsync(
										decoratedMessage -> playerManager.broadcast(decoratedMessage, serverCommandSource, MessageType.SAY_COMMAND), serverCommandSource.getServer()
									);
								return 1;
							}
						)
				)
		);
	}
}
