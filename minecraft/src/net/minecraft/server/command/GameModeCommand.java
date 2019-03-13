package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameMode;

public class GameModeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = ServerCommandManager.literal("gamemode")
			.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));

		for (GameMode gameMode : GameMode.values()) {
			if (gameMode != GameMode.INVALID) {
				literalArgumentBuilder.then(
					ServerCommandManager.literal(gameMode.getName())
						.executes(commandContext -> method_13387(commandContext, Collections.singleton(commandContext.getSource().method_9207()), gameMode))
						.then(
							ServerCommandManager.argument("target", EntityArgumentType.multiplePlayer())
								.executes(commandContext -> method_13387(commandContext, EntityArgumentType.method_9312(commandContext, "target"), gameMode))
						)
				);
			}
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static void method_13390(ServerCommandSource serverCommandSource, ServerPlayerEntity serverPlayerEntity, GameMode gameMode) {
		TextComponent textComponent = new TranslatableTextComponent("gameMode." + gameMode.getName());
		if (serverCommandSource.getEntity() == serverPlayerEntity) {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.gamemode.success.self", textComponent), true);
		} else {
			if (serverCommandSource.method_9225().getGameRules().getBoolean("sendCommandFeedback")) {
				serverPlayerEntity.method_9203(new TranslatableTextComponent("gameMode.changed", textComponent));
			}

			serverCommandSource.method_9226(new TranslatableTextComponent("commands.gamemode.success.other", serverPlayerEntity.method_5476(), textComponent), true);
		}
	}

	private static int method_13387(CommandContext<ServerCommandSource> commandContext, Collection<ServerPlayerEntity> collection, GameMode gameMode) {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			if (serverPlayerEntity.field_13974.getGameMode() != gameMode) {
				serverPlayerEntity.method_7336(gameMode);
				method_13390(commandContext.getSource(), serverPlayerEntity, gameMode);
				i++;
			}
		}

		return i;
	}
}
