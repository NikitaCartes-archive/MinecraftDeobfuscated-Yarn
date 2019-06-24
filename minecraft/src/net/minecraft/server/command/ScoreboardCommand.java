package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.command.arguments.ObjectiveCriteriaArgumentType;
import net.minecraft.command.arguments.OperationArgumentType;
import net.minecraft.command.arguments.ScoreHolderArgumentType;
import net.minecraft.command.arguments.ScoreboardSlotArgumentType;
import net.minecraft.command.arguments.TextArgumentType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class ScoreboardCommand {
	private static final SimpleCommandExceptionType OBJECTIVES_ADD_DUPLICATE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.scoreboard.objectives.add.duplicate")
	);
	private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADYEMPTY_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.scoreboard.objectives.display.alreadyEmpty")
	);
	private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADYSET_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.scoreboard.objectives.display.alreadySet")
	);
	private static final SimpleCommandExceptionType PLAYERS_ENABLE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.scoreboard.players.enable.failed")
	);
	private static final SimpleCommandExceptionType PLAYERS_ENABLE_INVALID_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.scoreboard.players.enable.invalid")
	);
	private static final Dynamic2CommandExceptionType PLAYERS_GET_NULL_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.scoreboard.players.get.null", object, object2)
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("scoreboard")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("objectives")
						.then(CommandManager.literal("list").executes(commandContext -> executeListObjectives(commandContext.getSource())))
						.then(
							CommandManager.literal("add")
								.then(
									CommandManager.argument("objective", StringArgumentType.word())
										.then(
											CommandManager.argument("criteria", ObjectiveCriteriaArgumentType.objectiveCriteria())
												.executes(
													commandContext -> executeAddObjective(
															commandContext.getSource(),
															StringArgumentType.getString(commandContext, "objective"),
															ObjectiveCriteriaArgumentType.getCriteria(commandContext, "criteria"),
															new LiteralText(StringArgumentType.getString(commandContext, "objective"))
														)
												)
												.then(
													CommandManager.argument("displayName", TextArgumentType.text())
														.executes(
															commandContext -> executeAddObjective(
																	commandContext.getSource(),
																	StringArgumentType.getString(commandContext, "objective"),
																	ObjectiveCriteriaArgumentType.getCriteria(commandContext, "criteria"),
																	TextArgumentType.getTextArgument(commandContext, "displayName")
																)
														)
												)
										)
								)
						)
						.then(
							CommandManager.literal("modify")
								.then(
									CommandManager.argument("objective", ObjectiveArgumentType.objective())
										.then(
											CommandManager.literal("displayname")
												.then(
													CommandManager.argument("displayName", TextArgumentType.text())
														.executes(
															commandContext -> executeModifyObjective(
																	commandContext.getSource(),
																	ObjectiveArgumentType.getObjective(commandContext, "objective"),
																	TextArgumentType.getTextArgument(commandContext, "displayName")
																)
														)
												)
										)
										.then(makeRenderTypeArguments())
								)
						)
						.then(
							CommandManager.literal("remove")
								.then(
									CommandManager.argument("objective", ObjectiveArgumentType.objective())
										.executes(commandContext -> executeRemoveObjective(commandContext.getSource(), ObjectiveArgumentType.getObjective(commandContext, "objective")))
								)
						)
						.then(
							CommandManager.literal("setdisplay")
								.then(
									CommandManager.argument("slot", ScoreboardSlotArgumentType.scoreboardSlot())
										.executes(commandContext -> executeClearDisplay(commandContext.getSource(), ScoreboardSlotArgumentType.getScorebordSlot(commandContext, "slot")))
										.then(
											CommandManager.argument("objective", ObjectiveArgumentType.objective())
												.executes(
													commandContext -> executeSetDisplay(
															commandContext.getSource(),
															ScoreboardSlotArgumentType.getScorebordSlot(commandContext, "slot"),
															ObjectiveArgumentType.getObjective(commandContext, "objective")
														)
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("players")
						.then(
							CommandManager.literal("list")
								.executes(commandContext -> executeListPlayers(commandContext.getSource()))
								.then(
									CommandManager.argument("target", ScoreHolderArgumentType.scoreHolder())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.executes(commandContext -> executeListScores(commandContext.getSource(), ScoreHolderArgumentType.getScoreHolder(commandContext, "target")))
								)
						)
						.then(
							CommandManager.literal("set")
								.then(
									CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.then(
											CommandManager.argument("objective", ObjectiveArgumentType.objective())
												.then(
													CommandManager.argument("score", IntegerArgumentType.integer())
														.executes(
															commandContext -> executeSet(
																	commandContext.getSource(),
																	ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"),
																	ObjectiveArgumentType.getWritableObjective(commandContext, "objective"),
																	IntegerArgumentType.getInteger(commandContext, "score")
																)
														)
												)
										)
								)
						)
						.then(
							CommandManager.literal("get")
								.then(
									CommandManager.argument("target", ScoreHolderArgumentType.scoreHolder())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.then(
											CommandManager.argument("objective", ObjectiveArgumentType.objective())
												.executes(
													commandContext -> executeGet(
															commandContext.getSource(),
															ScoreHolderArgumentType.getScoreHolder(commandContext, "target"),
															ObjectiveArgumentType.getObjective(commandContext, "objective")
														)
												)
										)
								)
						)
						.then(
							CommandManager.literal("add")
								.then(
									CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.then(
											CommandManager.argument("objective", ObjectiveArgumentType.objective())
												.then(
													CommandManager.argument("score", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> executeAdd(
																	commandContext.getSource(),
																	ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"),
																	ObjectiveArgumentType.getWritableObjective(commandContext, "objective"),
																	IntegerArgumentType.getInteger(commandContext, "score")
																)
														)
												)
										)
								)
						)
						.then(
							CommandManager.literal("remove")
								.then(
									CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.then(
											CommandManager.argument("objective", ObjectiveArgumentType.objective())
												.then(
													CommandManager.argument("score", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> executeRemove(
																	commandContext.getSource(),
																	ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"),
																	ObjectiveArgumentType.getWritableObjective(commandContext, "objective"),
																	IntegerArgumentType.getInteger(commandContext, "score")
																)
														)
												)
										)
								)
						)
						.then(
							CommandManager.literal("reset")
								.then(
									CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.executes(commandContext -> executeReset(commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets")))
										.then(
											CommandManager.argument("objective", ObjectiveArgumentType.objective())
												.executes(
													commandContext -> executeReset(
															commandContext.getSource(),
															ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"),
															ObjectiveArgumentType.getObjective(commandContext, "objective")
														)
												)
										)
								)
						)
						.then(
							CommandManager.literal("enable")
								.then(
									CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.then(
											CommandManager.argument("objective", ObjectiveArgumentType.objective())
												.suggests(
													(commandContext, suggestionsBuilder) -> suggestDisabled(
															commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"), suggestionsBuilder
														)
												)
												.executes(
													commandContext -> executeEnable(
															commandContext.getSource(),
															ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"),
															ObjectiveArgumentType.getObjective(commandContext, "objective")
														)
												)
										)
								)
						)
						.then(
							CommandManager.literal("operation")
								.then(
									CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.then(
											CommandManager.argument("targetObjective", ObjectiveArgumentType.objective())
												.then(
													CommandManager.argument("operation", OperationArgumentType.operation())
														.then(
															CommandManager.argument("source", ScoreHolderArgumentType.scoreHolders())
																.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
																.then(
																	CommandManager.argument("sourceObjective", ObjectiveArgumentType.objective())
																		.executes(
																			commandContext -> executeOperation(
																					commandContext.getSource(),
																					ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"),
																					ObjectiveArgumentType.getWritableObjective(commandContext, "targetObjective"),
																					OperationArgumentType.getOperation(commandContext, "operation"),
																					ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "source"),
																					ObjectiveArgumentType.getObjective(commandContext, "sourceObjective")
																				)
																		)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static LiteralArgumentBuilder<ServerCommandSource> makeRenderTypeArguments() {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("rendertype");

		for (ScoreboardCriterion.RenderType renderType : ScoreboardCriterion.RenderType.values()) {
			literalArgumentBuilder.then(
				CommandManager.literal(renderType.getName())
					.executes(
						commandContext -> executeModifyRenderType(commandContext.getSource(), ObjectiveArgumentType.getObjective(commandContext, "objective"), renderType)
					)
			);
		}

		return literalArgumentBuilder;
	}

	private static CompletableFuture<Suggestions> suggestDisabled(
		ServerCommandSource serverCommandSource, Collection<String> collection, SuggestionsBuilder suggestionsBuilder
	) {
		List<String> list = Lists.<String>newArrayList();
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();

		for (ScoreboardObjective scoreboardObjective : scoreboard.getObjectives()) {
			if (scoreboardObjective.getCriterion() == ScoreboardCriterion.TRIGGER) {
				boolean bl = false;

				for (String string : collection) {
					if (!scoreboard.playerHasObjective(string, scoreboardObjective) || scoreboard.getPlayerScore(string, scoreboardObjective).isLocked()) {
						bl = true;
						break;
					}
				}

				if (bl) {
					list.add(scoreboardObjective.getName());
				}
			}
		}

		return CommandSource.suggestMatching(list, suggestionsBuilder);
	}

	private static int executeGet(ServerCommandSource serverCommandSource, String string, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		if (!scoreboard.playerHasObjective(string, scoreboardObjective)) {
			throw PLAYERS_GET_NULL_EXCEPTION.create(scoreboardObjective.getName(), string);
		} else {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.players.get.success", string, scoreboardPlayerScore.getScore(), scoreboardObjective.toHoverableText()), false
			);
			return scoreboardPlayerScore.getScore();
		}
	}

	private static int executeOperation(
		ServerCommandSource serverCommandSource,
		Collection<String> collection,
		ScoreboardObjective scoreboardObjective,
		OperationArgumentType.Operation operation,
		Collection<String> collection2,
		ScoreboardObjective scoreboardObjective2
	) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		int i = 0;

		for (String string : collection) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);

			for (String string2 : collection2) {
				ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboard.getPlayerScore(string2, scoreboardObjective2);
				operation.apply(scoreboardPlayerScore, scoreboardPlayerScore2);
			}

			i += scoreboardPlayerScore.getScore();
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.players.operation.success.single", scoreboardObjective.toHoverableText(), collection.iterator().next(), i), true
			);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.players.operation.success.multiple", scoreboardObjective.toHoverableText(), collection.size()), true
			);
		}

		return i;
	}

	private static int executeEnable(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
		if (scoreboardObjective.getCriterion() != ScoreboardCriterion.TRIGGER) {
			throw PLAYERS_ENABLE_INVALID_EXCEPTION.create();
		} else {
			Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
			int i = 0;

			for (String string : collection) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
				if (scoreboardPlayerScore.isLocked()) {
					scoreboardPlayerScore.setLocked(false);
					i++;
				}
			}

			if (i == 0) {
				throw PLAYERS_ENABLE_FAILED_EXCEPTION.create();
			} else {
				if (collection.size() == 1) {
					serverCommandSource.sendFeedback(
						new TranslatableText("commands.scoreboard.players.enable.success.single", scoreboardObjective.toHoverableText(), collection.iterator().next()), true
					);
				} else {
					serverCommandSource.sendFeedback(
						new TranslatableText("commands.scoreboard.players.enable.success.multiple", scoreboardObjective.toHoverableText(), collection.size()), true
					);
				}

				return i;
			}
		}
	}

	private static int executeReset(ServerCommandSource serverCommandSource, Collection<String> collection) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();

		for (String string : collection) {
			scoreboard.resetPlayerScore(string, null);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.reset.all.single", collection.iterator().next()), true);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.reset.all.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int executeReset(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();

		for (String string : collection) {
			scoreboard.resetPlayerScore(string, scoreboardObjective);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.players.reset.specific.single", scoreboardObjective.toHoverableText(), collection.iterator().next()), true
			);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.players.reset.specific.multiple", scoreboardObjective.toHoverableText(), collection.size()), true
			);
		}

		return collection.size();
	}

	private static int executeSet(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective, int i) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();

		for (String string : collection) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			scoreboardPlayerScore.setScore(i);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.players.set.success.single", scoreboardObjective.toHoverableText(), collection.iterator().next(), i), true
			);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.players.set.success.multiple", scoreboardObjective.toHoverableText(), collection.size(), i), true
			);
		}

		return i * collection.size();
	}

	private static int executeAdd(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective, int i) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		int j = 0;

		for (String string : collection) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			scoreboardPlayerScore.setScore(scoreboardPlayerScore.getScore() + i);
			j += scoreboardPlayerScore.getScore();
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.players.add.success.single", i, scoreboardObjective.toHoverableText(), collection.iterator().next(), j), true
			);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.players.add.success.multiple", i, scoreboardObjective.toHoverableText(), collection.size()), true
			);
		}

		return j;
	}

	private static int executeRemove(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective, int i) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		int j = 0;

		for (String string : collection) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			scoreboardPlayerScore.setScore(scoreboardPlayerScore.getScore() - i);
			j += scoreboardPlayerScore.getScore();
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.players.remove.success.single", i, scoreboardObjective.toHoverableText(), collection.iterator().next(), j), true
			);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.players.remove.success.multiple", i, scoreboardObjective.toHoverableText(), collection.size()), true
			);
		}

		return j;
	}

	private static int executeListPlayers(ServerCommandSource serverCommandSource) {
		Collection<String> collection = serverCommandSource.getMinecraftServer().getScoreboard().getKnownPlayers();
		if (collection.isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.list.empty"), false);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.list.success", collection.size(), Texts.joinOrdered(collection)), false);
		}

		return collection.size();
	}

	private static int executeListScores(ServerCommandSource serverCommandSource, String string) {
		Map<ScoreboardObjective, ScoreboardPlayerScore> map = serverCommandSource.getMinecraftServer().getScoreboard().getPlayerObjectives(string);
		if (map.isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.list.entity.empty", string), false);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.list.entity.success", string, map.size()), false);

			for (Entry<ScoreboardObjective, ScoreboardPlayerScore> entry : map.entrySet()) {
				serverCommandSource.sendFeedback(
					new TranslatableText(
						"commands.scoreboard.players.list.entity.entry",
						((ScoreboardObjective)entry.getKey()).toHoverableText(),
						((ScoreboardPlayerScore)entry.getValue()).getScore()
					),
					false
				);
			}
		}

		return map.size();
	}

	private static int executeClearDisplay(ServerCommandSource serverCommandSource, int i) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		if (scoreboard.getObjectiveForSlot(i) == null) {
			throw OBJECTIVES_DISPLAY_ALREADYEMPTY_EXCEPTION.create();
		} else {
			scoreboard.setObjectiveSlot(i, null);
			serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.objectives.display.cleared", Scoreboard.getDisplaySlotNames()[i]), true);
			return 0;
		}
	}

	private static int executeSetDisplay(ServerCommandSource serverCommandSource, int i, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		if (scoreboard.getObjectiveForSlot(i) == scoreboardObjective) {
			throw OBJECTIVES_DISPLAY_ALREADYSET_EXCEPTION.create();
		} else {
			scoreboard.setObjectiveSlot(i, scoreboardObjective);
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.objectives.display.set", Scoreboard.getDisplaySlotNames()[i], scoreboardObjective.getDisplayName()), true
			);
			return 0;
		}
	}

	private static int executeModifyObjective(ServerCommandSource serverCommandSource, ScoreboardObjective scoreboardObjective, Text text) {
		if (!scoreboardObjective.getDisplayName().equals(text)) {
			scoreboardObjective.setDisplayName(text);
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.objectives.modify.displayname", scoreboardObjective.getName(), scoreboardObjective.toHoverableText()), true
			);
		}

		return 0;
	}

	private static int executeModifyRenderType(
		ServerCommandSource serverCommandSource, ScoreboardObjective scoreboardObjective, ScoreboardCriterion.RenderType renderType
	) {
		if (scoreboardObjective.getRenderType() != renderType) {
			scoreboardObjective.setRenderType(renderType);
			serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.objectives.modify.rendertype", scoreboardObjective.toHoverableText()), true);
		}

		return 0;
	}

	private static int executeRemoveObjective(ServerCommandSource serverCommandSource, ScoreboardObjective scoreboardObjective) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		scoreboard.removeObjective(scoreboardObjective);
		serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.objectives.remove.success", scoreboardObjective.toHoverableText()), true);
		return scoreboard.getObjectives().size();
	}

	private static int executeAddObjective(ServerCommandSource serverCommandSource, String string, ScoreboardCriterion scoreboardCriterion, Text text) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		if (scoreboard.getNullableObjective(string) != null) {
			throw OBJECTIVES_ADD_DUPLICATE_EXCEPTION.create();
		} else if (string.length() > 16) {
			throw ObjectiveArgumentType.LONG_NAME_EXCEPTION.create(16);
		} else {
			scoreboard.addObjective(string, scoreboardCriterion, text, scoreboardCriterion.getCriterionType());
			ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
			serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.objectives.add.success", scoreboardObjective.toHoverableText()), true);
			return scoreboard.getObjectives().size();
		}
	}

	private static int executeListObjectives(ServerCommandSource serverCommandSource) {
		Collection<ScoreboardObjective> collection = serverCommandSource.getMinecraftServer().getScoreboard().getObjectives();
		if (collection.isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.objectives.list.empty"), false);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.scoreboard.objectives.list.success", collection.size(), Texts.join(collection, ScoreboardObjective::toHoverableText)), false
			);
		}

		return collection.size();
	}
}
