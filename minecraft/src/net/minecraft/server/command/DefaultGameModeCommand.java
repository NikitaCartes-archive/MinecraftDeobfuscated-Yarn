package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

public class DefaultGameModeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("defaultgamemode")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("gamemode", GameModeArgumentType.gameMode())
						.executes(commandContext -> execute(commandContext.getSource(), GameModeArgumentType.getGameMode(commandContext, "gamemode")))
				)
		);
	}

	private static int execute(ServerCommandSource source, GameMode defaultGameMode) {
		int i = 0;
		MinecraftServer minecraftServer = source.getServer();
		minecraftServer.setDefaultGameMode(defaultGameMode);
		GameMode gameMode = minecraftServer.getForcedGameMode();
		if (gameMode != null) {
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				if (serverPlayerEntity.changeGameMode(gameMode)) {
					i++;
				}
			}
		}

		source.sendFeedback(() -> Text.translatable("commands.defaultgamemode.success", defaultGameMode.getTranslatableName()), true);
		return i;
	}
}
