package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("gamerule")
			.requires(source -> source.hasPermissionLevel(2));
		GameRules.forEach(
			new GameRules.RuleConsumer() {
				@Override
				public <T extends GameRules.Rule<T>> void accept(GameRules.RuleKey<T> key, GameRules.RuleType<T> type) {
					literalArgumentBuilder.then(
						CommandManager.literal(key.getName())
							.executes(context -> GameRuleCommand.executeQuery(context.getSource(), key))
							.then(type.argument("value").executes(context -> GameRuleCommand.executeSet(context, key)))
					);
				}
			}
		);
		dispatcher.register(literalArgumentBuilder);
	}

	private static <T extends GameRules.Rule<T>> int executeSet(CommandContext<ServerCommandSource> context, GameRules.RuleKey<T> key) {
		ServerCommandSource serverCommandSource = context.getSource();
		T rule = serverCommandSource.getMinecraftServer().getGameRules().get(key);
		rule.set(context, "value");
		serverCommandSource.sendFeedback(new TranslatableText("commands.gamerule.set", key.getName(), rule.toString()), true);
		return rule.toCommandResult();
	}

	private static <T extends GameRules.Rule<T>> int executeQuery(ServerCommandSource source, GameRules.RuleKey<T> key) {
		T rule = source.getMinecraftServer().getGameRules().get(key);
		source.sendFeedback(new TranslatableText("commands.gamerule.query", key.getName(), rule.toString()), false);
		return rule.toCommandResult();
	}
}
