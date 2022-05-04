package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.MessageType;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

public class SayCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("say")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("message", MessageArgumentType.message())
						.executes(
							context -> {
								SignedChatMessage signedChatMessage = MessageArgumentType.getSignedMessage(context, "message");
								ServerCommandSource serverCommandSource = context.getSource();
								PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
								if (serverCommandSource.isExecutedByPlayer()) {
									ServerPlayerEntity serverPlayerEntity = serverCommandSource.getPlayer();
									serverPlayerEntity.getTextStream()
										.filterText(signedChatMessage.content().getString())
										.thenAcceptAsync(
											message -> playerManager.broadcast(signedChatMessage, message, serverPlayerEntity, MessageType.SAY_COMMAND), serverCommandSource.getServer()
										);
								} else {
									playerManager.broadcast(signedChatMessage, serverCommandSource.getChatMessageSender(), MessageType.SAY_COMMAND);
								}

								return 1;
							}
						)
				)
		);
	}
}
