package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.PlayerManager;

public class MeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("me").then(CommandManager.argument("action", MessageArgumentType.message()).executes(context -> {
			MessageArgumentType.getSignedMessage(context, "action", message -> {
				ServerCommandSource serverCommandSource = context.getSource();
				PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
				playerManager.broadcast(message, serverCommandSource, MessageType.params(MessageType.EMOTE_COMMAND, serverCommandSource));
			});
			return 1;
		})));
	}
}
