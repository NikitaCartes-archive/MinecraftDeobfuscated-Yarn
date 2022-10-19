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
				.then(CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
					MessageArgumentType.getSignedMessage(context, "message", message -> {
						ServerCommandSource serverCommandSource = context.getSource();
						PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
						playerManager.broadcast(message, serverCommandSource, MessageType.params(MessageType.SAY_COMMAND, serverCommandSource));
					});
					return 1;
				}))
		);
	}
}
