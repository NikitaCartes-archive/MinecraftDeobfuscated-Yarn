package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class DefaultGameModeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("defaultgamemode")
			.requires(source -> source.hasPermissionLevel(2));

		for (GameMode gameMode : GameMode.values()) {
			literalArgumentBuilder.then(CommandManager.literal(gameMode.getName()).executes(context -> execute(context.getSource(), gameMode)));
		}

		dispatcher.register(literalArgumentBuilder);
	}

	private static int execute(ServerCommandSource source, GameMode defaultGameMode) {
		int i = 0;
		MinecraftServer minecraftServer = source.getMinecraftServer();
		minecraftServer.setDefaultGameMode(defaultGameMode);
		GameMode gameMode = minecraftServer.getForcedGameMode();
		if (gameMode != null) {
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				if (serverPlayerEntity.changeGameMode(gameMode)) {
					i++;
				}
			}
		}

		source.sendFeedback(new TranslatableText("commands.defaultgamemode.success", defaultGameMode.getTranslatableName()), true);
		return i;
	}
}
