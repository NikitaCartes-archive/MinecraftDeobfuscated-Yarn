package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public class GameModeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("gamemode")
			.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));

		for (GameMode gameMode : GameMode.values()) {
			if (gameMode != GameMode.field_9218) {
				literalArgumentBuilder.then(
					CommandManager.literal(gameMode.getName())
						.executes(commandContext -> execute(commandContext, Collections.singleton(commandContext.getSource().getPlayer()), gameMode))
						.then(
							CommandManager.argument("target", EntityArgumentType.players())
								.executes(commandContext -> execute(commandContext, EntityArgumentType.getPlayers(commandContext, "target"), gameMode))
						)
				);
			}
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static void setGameMode(ServerCommandSource serverCommandSource, ServerPlayerEntity serverPlayerEntity, GameMode gameMode) {
		Text text = new TranslatableText("gameMode." + gameMode.getName());
		if (serverCommandSource.getEntity() == serverPlayerEntity) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.gamemode.success.self", text), true);
		} else {
			if (serverCommandSource.getWorld().getGameRules().getBoolean(GameRules.field_19400)) {
				serverPlayerEntity.sendMessage(new TranslatableText("gameMode.changed", text));
			}

			serverCommandSource.sendFeedback(new TranslatableText("commands.gamemode.success.other", serverPlayerEntity.getDisplayName(), text), true);
		}
	}

	private static int execute(CommandContext<ServerCommandSource> commandContext, Collection<ServerPlayerEntity> collection, GameMode gameMode) {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			if (serverPlayerEntity.interactionManager.getGameMode() != gameMode) {
				serverPlayerEntity.method_7336(gameMode);
				setGameMode(commandContext.getSource(), serverPlayerEntity, gameMode);
				i++;
			}
		}

		return i;
	}
}
