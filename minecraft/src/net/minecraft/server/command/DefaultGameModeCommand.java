package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameMode;

public class DefaultGameModeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = ServerCommandManager.literal("defaultgamemode")
			.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));

		for (GameMode gameMode : GameMode.values()) {
			if (gameMode != GameMode.INVALID) {
				literalArgumentBuilder.then(ServerCommandManager.literal(gameMode.getName()).executes(commandContext -> method_13167(commandContext.getSource(), gameMode)));
			}
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static int method_13167(ServerCommandSource serverCommandSource, GameMode gameMode) {
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

		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.defaultgamemode.success", gameMode.getTextComponent()), true);
		return i;
	}
}
