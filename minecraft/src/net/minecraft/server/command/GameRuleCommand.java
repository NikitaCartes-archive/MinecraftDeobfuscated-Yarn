package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("gamerule")
			.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));
		GameRules.forEach(
			new GameRules.RuleConsumer() {
				@Override
				public <T extends GameRules.Rule<T>> void accept(GameRules.RuleKey<T> ruleKey, GameRules.RuleType<T> ruleType) {
					literalArgumentBuilder.then(
						CommandManager.literal(ruleKey.getName())
							.executes(commandContext -> GameRuleCommand.executeQuery(commandContext.getSource(), ruleKey))
							.then(ruleType.argument("value").executes(commandContext -> GameRuleCommand.executeSet(commandContext, ruleKey)))
					);
				}
			}
		);
		commandDispatcher.register(literalArgumentBuilder);
	}

	private static <T extends GameRules.Rule<T>> int executeSet(CommandContext<ServerCommandSource> commandContext, GameRules.RuleKey<T> ruleKey) {
		ServerCommandSource serverCommandSource = commandContext.getSource();
		T rule = serverCommandSource.getMinecraftServer().getGameRules().get(ruleKey);
		rule.set(commandContext, "value");
		serverCommandSource.sendFeedback(new TranslatableText("commands.gamerule.set", ruleKey.getName(), rule.toString()), true);
		return rule.toCommandResult();
	}

	private static <T extends GameRules.Rule<T>> int executeQuery(ServerCommandSource serverCommandSource, GameRules.RuleKey<T> ruleKey) {
		T rule = serverCommandSource.getMinecraftServer().getGameRules().get(ruleKey);
		serverCommandSource.sendFeedback(new TranslatableText("commands.gamerule.query", ruleKey.getName(), rule.toString()), false);
		return rule.toCommandResult();
	}
}
