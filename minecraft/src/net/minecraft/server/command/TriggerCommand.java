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
import net.minecraft.text.TranslatableTextComponent;

public class TriggerCommand {
	private static final SimpleCommandExceptionType FAILED_UMPRIMED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.trigger.failed.unprimed")
	);
	private static final SimpleCommandExceptionType FAILED_INVALID_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.trigger.failed.invalid")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("trigger")
				.then(
					ServerCommandManager.argument("objective", ObjectiveArgumentType.create())
						.suggests((commandContext, suggestionsBuilder) -> method_13819(commandContext.getSource(), suggestionsBuilder))
						.executes(
							commandContext -> method_13818(
									commandContext.getSource(),
									method_13821(commandContext.getSource().getPlayer(), ObjectiveArgumentType.getObjectiveArgument(commandContext, "objective"))
								)
						)
						.then(
							ServerCommandManager.literal("add")
								.then(
									ServerCommandManager.argument("value", IntegerArgumentType.integer())
										.executes(
											commandContext -> method_13817(
													commandContext.getSource(),
													method_13821(commandContext.getSource().getPlayer(), ObjectiveArgumentType.getObjectiveArgument(commandContext, "objective")),
													IntegerArgumentType.getInteger(commandContext, "value")
												)
										)
								)
						)
						.then(
							ServerCommandManager.literal("set")
								.then(
									ServerCommandManager.argument("value", IntegerArgumentType.integer())
										.executes(
											commandContext -> method_13820(
													commandContext.getSource(),
													method_13821(commandContext.getSource().getPlayer(), ObjectiveArgumentType.getObjectiveArgument(commandContext, "objective")),
													IntegerArgumentType.getInteger(commandContext, "value")
												)
										)
								)
						)
				)
		);
	}

	public static CompletableFuture<Suggestions> method_13819(ServerCommandSource serverCommandSource, SuggestionsBuilder suggestionsBuilder) {
		Entity entity = serverCommandSource.getEntity();
		List<String> list = Lists.<String>newArrayList();
		if (entity != null) {
			Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
			String string = entity.getEntityName();

			for (ScoreboardObjective scoreboardObjective : scoreboard.getObjectives()) {
				if (scoreboardObjective.getCriterion() == ScoreboardCriterion.TRIGGER && scoreboard.playerHasObjective(string, scoreboardObjective)) {
					ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
					if (!scoreboardPlayerScore.isLocked()) {
						list.add(scoreboardObjective.getName());
					}
				}
			}
		}

		return CommandSource.suggestMatching(list, suggestionsBuilder);
	}

	private static int method_13817(ServerCommandSource serverCommandSource, ScoreboardPlayerScore scoreboardPlayerScore, int i) {
		scoreboardPlayerScore.incrementScore(i);
		serverCommandSource.sendFeedback(
			new TranslatableTextComponent("commands.trigger.add.success", scoreboardPlayerScore.getObjective().getTextComponent(), i), true
		);
		return scoreboardPlayerScore.getScore();
	}

	private static int method_13820(ServerCommandSource serverCommandSource, ScoreboardPlayerScore scoreboardPlayerScore, int i) {
		scoreboardPlayerScore.setScore(i);
		serverCommandSource.sendFeedback(
			new TranslatableTextComponent("commands.trigger.set.success", scoreboardPlayerScore.getObjective().getTextComponent(), i), true
		);
		return i;
	}

	private static int method_13818(ServerCommandSource serverCommandSource, ScoreboardPlayerScore scoreboardPlayerScore) {
		scoreboardPlayerScore.incrementScore(1);
		serverCommandSource.sendFeedback(
			new TranslatableTextComponent("commands.trigger.simple.success", scoreboardPlayerScore.getObjective().getTextComponent()), true
		);
		return scoreboardPlayerScore.getScore();
	}

	private static ScoreboardPlayerScore method_13821(ServerPlayerEntity serverPlayerEntity, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
		if (scoreboardObjective.getCriterion() != ScoreboardCriterion.TRIGGER) {
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
