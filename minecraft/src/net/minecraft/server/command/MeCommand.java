package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.MessageType;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

public class MeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("me").then(CommandManager.argument("action", MessageArgumentType.message()).executes(context -> {
			SignedChatMessage signedChatMessage = MessageArgumentType.getSignedMessage(context, "action");
			ServerCommandSource serverCommandSource = context.getSource();
			if (serverCommandSource.isExecutedByPlayer()) {
				ServerPlayerEntity serverPlayerEntity = serverCommandSource.getPlayer();
				serverPlayerEntity.getTextStream().filterText(signedChatMessage.content().getString()).thenAcceptAsync(message -> {
					PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
					playerManager.broadcast(signedChatMessage, message, serverPlayerEntity, MessageType.EMOTE_COMMAND);
				}, serverCommandSource.getServer());
			} else {
				serverCommandSource.getServer().getPlayerManager().broadcast(signedChatMessage, serverCommandSource.getChatMessageSender(), MessageType.EMOTE_COMMAND);
			}

			return 1;
		})));
	}
}
