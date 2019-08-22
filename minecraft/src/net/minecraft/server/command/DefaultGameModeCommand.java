package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class DefaultGameModeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("defaultgamemode")
			.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));

		for (GameMode gameMode : GameMode.values()) {
			if (gameMode != GameMode.NOT_SET) {
				literalArgumentBuilder.then(CommandManager.literal(gameMode.getName()).executes(commandContext -> execute(commandContext.getSource(), gameMode)));
			}
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static int execute(ServerCommandSource serverCommandSource, GameMode gameMode) {
		int i = 0;
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		minecraftServer.setDefaultGameMode(gameMode);
		if (minecraftServer.shouldForceGameMode()) {
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				if (serverPlayerEntity.interactionManager.getGameMode() != gameMode) {
					serverPlayerEntity.setGameMode(gameMode);
					i++;
				}
			}
		}

		serverCommandSource.sendFeedback(new TranslatableText("commands.defaultgamemode.success", gameMode.getTranslatableName()), true);
		return i;
	}
}
