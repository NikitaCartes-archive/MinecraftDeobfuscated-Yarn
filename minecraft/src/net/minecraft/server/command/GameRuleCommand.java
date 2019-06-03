package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map.Entry;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("gamerule")
			.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));

		for (Entry<String, GameRules.Key> entry : GameRules.getKeys().entrySet()) {
			literalArgumentBuilder.then(
				CommandManager.literal((String)entry.getKey())
					.executes(commandContext -> executeQuery(commandContext.getSource(), (String)entry.getKey()))
					.then(
						((GameRules.Key)entry.getValue())
							.getType()
							.argument("value")
							.executes(commandContext -> executeSet(commandContext.getSource(), (String)entry.getKey(), commandContext))
					)
			);
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static int executeSet(ServerCommandSource serverCommandSource, String string, CommandContext<ServerCommandSource> commandContext) {
		GameRules.Value value = serverCommandSource.getMinecraftServer().getGameRules().get(string);
		value.getType().set(commandContext, "value", value);
		serverCommandSource.method_9226(new TranslatableText("commands.gamerule.set", string, value.getString()), true);
		return value.getInteger();
	}

	private static int executeQuery(ServerCommandSource serverCommandSource, String string) {
		GameRules.Value value = serverCommandSource.getMinecraftServer().getGameRules().get(string);
		serverCommandSource.method_9226(new TranslatableText("commands.gamerule.query", string, value.getString()), false);
		return value.getInteger();
	}
}
