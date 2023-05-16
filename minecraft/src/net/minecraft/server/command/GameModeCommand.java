package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public class GameModeCommand {
	public static final int REQUIRED_PERMISSION_LEVEL = 2;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("gamemode")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("gamemode", GameModeArgumentType.gameMode())
						.executes(
							commandContext -> execute(
									commandContext, Collections.singleton(commandContext.getSource().getPlayerOrThrow()), GameModeArgumentType.getGameMode(commandContext, "gamemode")
								)
						)
						.then(
							CommandManager.argument("target", EntityArgumentType.players())
								.executes(
									commandContext -> execute(
											commandContext, EntityArgumentType.getPlayers(commandContext, "target"), GameModeArgumentType.getGameMode(commandContext, "gamemode")
										)
								)
						)
				)
		);
	}

	private static void sendFeedback(ServerCommandSource source, ServerPlayerEntity player, GameMode gameMode) {
		Text text = Text.translatable("gameMode." + gameMode.getName());
		if (source.getEntity() == player) {
			source.sendFeedback(() -> Text.translatable("commands.gamemode.success.self", text), true);
		} else {
			if (source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
				player.sendMessage(Text.translatable("gameMode.changed", text));
			}

			source.sendFeedback(() -> Text.translatable("commands.gamemode.success.other", player.getDisplayName(), text), true);
		}
	}

	private static int execute(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, GameMode gameMode) {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			if (serverPlayerEntity.changeGameMode(gameMode)) {
				sendFeedback(context.getSource(), serverPlayerEntity, gameMode);
				i++;
			}
		}

		return i;
	}
}
