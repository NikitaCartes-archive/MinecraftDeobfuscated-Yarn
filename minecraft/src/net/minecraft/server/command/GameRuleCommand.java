package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map.Entry;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = ServerCommandManager.literal("gamerule")
			.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));

		for (Entry<String, GameRules.Key> entry : GameRules.getKeys().entrySet()) {
			literalArgumentBuilder.then(
				ServerCommandManager.literal((String)entry.getKey())
					.executes(commandContext -> method_13397(commandContext.getSource(), (String)entry.getKey()))
					.then(
						((GameRules.Key)entry.getValue())
							.getType()
							.method_8371("value")
							.executes(commandContext -> method_13394(commandContext.getSource(), (String)entry.getKey(), commandContext))
					)
			);
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static int method_13394(ServerCommandSource serverCommandSource, String string, CommandContext<ServerCommandSource> commandContext) {
		GameRules.Value value = serverCommandSource.getMinecraftServer().getGameRules().get(string);
		value.getType().method_8370(commandContext, "value", value);
		serverCommandSource.method_9226(new TranslatableTextComponent("commands.gamerule.set", string, value.getString()), true);
		return value.getInteger();
	}

	private static int method_13397(ServerCommandSource serverCommandSource, String string) {
		GameRules.Value value = serverCommandSource.getMinecraftServer().getGameRules().get(string);
		serverCommandSource.method_9226(new TranslatableTextComponent("commands.gamerule.query", string, value.getString()), false);
		return value.getInteger();
	}
}
