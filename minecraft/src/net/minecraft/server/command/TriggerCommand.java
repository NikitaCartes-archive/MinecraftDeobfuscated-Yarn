package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class TriggerCommand {
	private static final SimpleCommandExceptionType FAILED_UMPRIMED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.trigger.failed.unprimed")
	);
	private static final SimpleCommandExceptionType FAILED_INVALID_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.trigger.failed.invalid")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("trigger")
				.then(
					CommandManager.argument("objective", ObjectiveArgumentType.create())
						.suggests((commandContext, suggestionsBuilder) -> suggestObjectives(commandContext.getSource(), suggestionsBuilder))
						.executes(
							commandContext -> executeSimple(
									commandContext.getSource(), getScore(commandContext.getSource().getPlayer(), ObjectiveArgumentType.getObjective(commandContext, "objective"))
								)
						)
						.then(
							CommandManager.literal("add")
								.then(
									CommandManager.argument("value", IntegerArgumentType.integer())
										.executes(
											commandContext -> executeAdd(
													commandContext.getSource(),
													getScore(commandContext.getSource().getPlayer(), ObjectiveArgumentType.getObjective(commandContext, "objective")),
													IntegerArgumentType.getInteger(commandContext, "value")
												)
										)
								)
						)
						.then(
							CommandManager.literal("set")
								.then(
									CommandManager.argument("value", IntegerArgumentType.integer())
										.executes(
											commandContext -> executeSet(
													commandContext.getSource(),
													getScore(commandContext.getSource().getPlayer(), ObjectiveArgumentType.getObjective(commandContext, "objective")),
													IntegerArgumentType.getInteger(commandContext, "value")
												)
										)
								)
						)
				)
		);
	}

	public static CompletableFuture<Suggestions> suggestObjectives(ServerCommandSource serverCommandSource, SuggestionsBuilder suggestionsBuilder) {
		Entity entity = serverCommandSource.getEntity();
		List<String> list = Lists.<String>newArrayList();
		if (entity != null) {
			Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
			String string = entity.getEntityName();

			for (ScoreboardObjective scoreboardObjective : scoreboard.getObjectives()) {
				if (scoreboardObjective.getCriterion() == ScoreboardCriterion.field_1462 && scoreboard.playerHasObjective(string, scoreboardObjective)) {
					ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
					if (!scoreboardPlayerScore.isLocked()) {
						list.add(scoreboardObjective.getName());
					}
				}
			}
		}

		return CommandSource.suggestMatching(list, suggestionsBuilder);
	}

	private static int executeAdd(ServerCommandSource serverCommandSource, ScoreboardPlayerScore scoreboardPlayerScore, int i) {
		scoreboardPlayerScore.incrementScore(i);
		serverCommandSource.method_9226(new TranslatableText("commands.trigger.add.success", scoreboardPlayerScore.getObjective().method_1120(), i), true);
		return scoreboardPlayerScore.getScore();
	}

	private static int executeSet(ServerCommandSource serverCommandSource, ScoreboardPlayerScore scoreboardPlayerScore, int i) {
		scoreboardPlayerScore.setScore(i);
		serverCommandSource.method_9226(new TranslatableText("commands.trigger.set.success", scoreboardPlayerScore.getObjective().method_1120(), i), true);
		return i;
	}

	private static int executeSimple(ServerCommandSource serverCommandSource, ScoreboardPlayerScore scoreboardPlayerScore) {
		scoreboardPlayerScore.incrementScore(1);
		serverCommandSource.method_9226(new TranslatableText("commands.trigger.simple.success", scoreboardPlayerScore.getObjective().method_1120()), true);
		return scoreboardPlayerScore.getScore();
	}

	private static ScoreboardPlayerScore getScore(ServerPlayerEntity serverPlayerEntity, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
		if (scoreboardObjective.getCriterion() != ScoreboardCriterion.field_1462) {
			throw FAILED_INVALID_EXCEPTION.create();
		} else {
			Scoreboard scoreboard = serverPlayerEntity.getScoreboard();
			String string = serverPlayerEntity.getEntityName();
			if (!scoreboard.playerHasObjective(string, scoreboardObjective)) {
				throw FAILED_UMPRIMED_EXCEPTION.create();
			} else {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
				if (scoreboardPlayerScore.isLocked()) {
					throw FAILED_UMPRIMED_EXCEPTION.create();
				} else {
					scoreboardPlayerScore.setLocked(true);
					return scoreboardPlayerScore;
				}
			}
		}
	}
}
