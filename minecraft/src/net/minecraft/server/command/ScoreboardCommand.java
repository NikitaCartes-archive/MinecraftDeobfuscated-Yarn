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
import net.minecraft.command.arguments.ComponentArgumentType;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.command.arguments.ObjectiveCriteriaArgumentType;
import net.minecraft.command.arguments.OperationArgumentType;
import net.minecraft.command.arguments.ScoreHolderArgumentType;
import net.minecraft.command.arguments.ScoreboardSlotArgumentType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;

public class ScoreboardCommand {
	private static final SimpleCommandExceptionType OBJECTIVES_ADD_DUPLICATE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.scoreboard.objectives.add.duplicate")
	);
	private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADYEMPTY_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.scoreboard.objectives.display.alreadyEmpty")
	);
	private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADYSET_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.scoreboard.objectives.display.alreadySet")
	);
	private static final SimpleCommandExceptionType PLAYERS_ENABLE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.scoreboard.players.enable.failed")
	);
	private static final SimpleCommandExceptionType PLAYERS_ENABLE_INVALID_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.scoreboard.players.enable.invalid")
	);
	private static final Dynamic2CommandExceptionType PLAYERS_GET_NULL_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("commands.scoreboard.players.get.null", object, object2)
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("scoreboard")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.literal("objectives")
						.then(ServerCommandManager.literal("list").executes(commandContext -> method_13597(commandContext.getSource())))
						.then(
							ServerCommandManager.literal("add")
								.then(
									ServerCommandManager.argument("objective", StringArgumentType.word())
										.then(
											ServerCommandManager.argument("criteria", ObjectiveCriteriaArgumentType.create())
												.executes(
													commandContext -> method_13611(
															commandContext.getSource(),
															StringArgumentType.getString(commandContext, "objective"),
															ObjectiveCriteriaArgumentType.getCriteriaArgument(commandContext, "criteria"),
															new StringTextComponent(StringArgumentType.getString(commandContext, "objective"))
														)
												)
												.then(
													ServerCommandManager.argument("displayName", ComponentArgumentType.create())
														.executes(
															commandContext -> method_13611(
																	commandContext.getSource(),
																	StringArgumentType.getString(commandContext, "objective"),
																	ObjectiveCriteriaArgumentType.getCriteriaArgument(commandContext, "criteria"),
																	ComponentArgumentType.method_9280(commandContext, "displayName")
																)
														)
												)
										)
								)
						)
						.then(
							ServerCommandManager.literal("modify")
								.then(
									ServerCommandManager.argument("objective", ObjectiveArgumentType.create())
										.then(
											ServerCommandManager.literal("displayname")
												.then(
													ServerCommandManager.argument("displayName", ComponentArgumentType.create())
														.executes(
															commandContext -> method_13576(
																	commandContext.getSource(),
																	ObjectiveArgumentType.getObjectiveArgument(commandContext, "objective"),
																	ComponentArgumentType.method_9280(commandContext, "displayName")
																)
														)
												)
										)
										.then(method_13606())
								)
						)
						.then(
							ServerCommandManager.literal("remove")
								.then(
									ServerCommandManager.argument("objective", ObjectiveArgumentType.create())
										.executes(commandContext -> method_13602(commandContext.getSource(), ObjectiveArgumentType.getObjectiveArgument(commandContext, "objective")))
								)
						)
						.then(
							ServerCommandManager.literal("setdisplay")
								.then(
									ServerCommandManager.argument("slot", ScoreboardSlotArgumentType.create())
										.executes(commandContext -> method_13592(commandContext.getSource(), ScoreboardSlotArgumentType.getScorebordSlotArgument(commandContext, "slot")))
										.then(
											ServerCommandManager.argument("objective", ObjectiveArgumentType.create())
												.executes(
													commandContext -> method_13596(
															commandContext.getSource(),
															ScoreboardSlotArgumentType.getScorebordSlotArgument(commandContext, "slot"),
															ObjectiveArgumentType.getObjectiveArgument(commandContext, "objective")
														)
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("players")
						.then(
							ServerCommandManager.literal("list")
								.executes(commandContext -> method_13589(commandContext.getSource()))
								.then(
									ServerCommandManager.argument("target", ScoreHolderArgumentType.method_9447())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.executes(commandContext -> method_13614(commandContext.getSource(), ScoreHolderArgumentType.getHolderArgument(commandContext, "target")))
								)
						)
						.then(
							ServerCommandManager.literal("set")
								.then(
									ServerCommandManager.argument("targets", ScoreHolderArgumentType.method_9451())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.then(
											ServerCommandManager.argument("objective", ObjectiveArgumentType.create())
												.then(
													ServerCommandManager.argument("score", IntegerArgumentType.integer())
														.executes(
															commandContext -> method_13604(
																	commandContext.getSource(),
																	ScoreHolderArgumentType.method_9449(commandContext, "targets"),
																	ObjectiveArgumentType.getWritableObjectiveArgument(commandContext, "objective"),
																	IntegerArgumentType.getInteger(commandContext, "score")
																)
														)
												)
										)
								)
						)
						.then(
							ServerCommandManager.literal("get")
								.then(
									ServerCommandManager.argument("target", ScoreHolderArgumentType.method_9447())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.then(
											ServerCommandManager.argument("objective", ObjectiveArgumentType.create())
												.executes(
													commandContext -> method_13607(
															commandContext.getSource(),
															ScoreHolderArgumentType.getHolderArgument(commandContext, "target"),
															ObjectiveArgumentType.getObjectiveArgument(commandContext, "objective")
														)
												)
										)
								)
						)
						.then(
							ServerCommandManager.literal("add")
								.then(
									ServerCommandManager.argument("targets", ScoreHolderArgumentType.method_9451())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.then(
											ServerCommandManager.argument("objective", ObjectiveArgumentType.create())
												.then(
													ServerCommandManager.argument("score", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> method_13578(
																	commandContext.getSource(),
																	ScoreHolderArgumentType.method_9449(commandContext, "targets"),
																	ObjectiveArgumentType.getWritableObjectiveArgument(commandContext, "objective"),
																	IntegerArgumentType.getInteger(commandContext, "score")
																)
														)
												)
										)
								)
						)
						.then(
							ServerCommandManager.literal("remove")
								.then(
									ServerCommandManager.argument("targets", ScoreHolderArgumentType.method_9451())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.then(
											ServerCommandManager.argument("objective", ObjectiveArgumentType.create())
												.then(
													ServerCommandManager.argument("score", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> method_13600(
																	commandContext.getSource(),
																	ScoreHolderArgumentType.method_9449(commandContext, "targets"),
																	ObjectiveArgumentType.getWritableObjectiveArgument(commandContext, "objective"),
																	IntegerArgumentType.getInteger(commandContext, "score")
																)
														)
												)
										)
								)
						)
						.then(
							ServerCommandManager.literal("reset")
								.then(
									ServerCommandManager.argument("targets", ScoreHolderArgumentType.method_9451())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.executes(commandContext -> method_13575(commandContext.getSource(), ScoreHolderArgumentType.method_9449(commandContext, "targets")))
										.then(
											ServerCommandManager.argument("objective", ObjectiveArgumentType.create())
												.executes(
													commandContext -> method_13586(
															commandContext.getSource(),
															ScoreHolderArgumentType.method_9449(commandContext, "targets"),
															ObjectiveArgumentType.getObjectiveArgument(commandContext, "objective")
														)
												)
										)
								)
						)
						.then(
							ServerCommandManager.literal("enable")
								.then(
									ServerCommandManager.argument("targets", ScoreHolderArgumentType.method_9451())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.then(
											ServerCommandManager.argument("objective", ObjectiveArgumentType.create())
												.suggests(
													(commandContext, suggestionsBuilder) -> method_13613(
															commandContext.getSource(), ScoreHolderArgumentType.method_9449(commandContext, "targets"), suggestionsBuilder
														)
												)
												.executes(
													commandContext -> method_13609(
															commandContext.getSource(),
															ScoreHolderArgumentType.method_9449(commandContext, "targets"),
															ObjectiveArgumentType.getObjectiveArgument(commandContext, "objective")
														)
												)
										)
								)
						)
						.then(
							ServerCommandManager.literal("operation")
								.then(
									ServerCommandManager.argument("targets", ScoreHolderArgumentType.method_9451())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.then(
											ServerCommandManager.argument("targetObjective", ObjectiveArgumentType.create())
												.then(
													ServerCommandManager.argument("operation", OperationArgumentType.create())
														.then(
															ServerCommandManager.argument("source", ScoreHolderArgumentType.method_9451())
																.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
																.then(
																	ServerCommandManager.argument("sourceObjective", ObjectiveArgumentType.create())
																		.executes(
																			commandContext -> method_13584(
																					commandContext.getSource(),
																					ScoreHolderArgumentType.method_9449(commandContext, "targets"),
																					ObjectiveArgumentType.getWritableObjectiveArgument(commandContext, "targetObjective"),
																					OperationArgumentType.getOperatorArgument(commandContext, "operation"),
																					ScoreHolderArgumentType.method_9449(commandContext, "source"),
																					ObjectiveArgumentType.getObjectiveArgument(commandContext, "sourceObjective")
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

	private static LiteralArgumentBuilder<ServerCommandSource> method_13606() {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = ServerCommandManager.literal("rendertype");

		for (ScoreboardCriterion.Type type : ScoreboardCriterion.Type.values()) {
			literalArgumentBuilder.then(
				ServerCommandManager.literal(type.getName())
					.executes(commandContext -> method_13581(commandContext.getSource(), ObjectiveArgumentType.getObjectiveArgument(commandContext, "objective"), type))
			);
		}

		return literalArgumentBuilder;
	}

	private static CompletableFuture<Suggestions> method_13613(
		ServerCommandSource serverCommandSource, Collection<String> collection, SuggestionsBuilder suggestionsBuilder
	) {
		List<String> list = Lists.<String>newArrayList();
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();

		for (ScoreboardObjective scoreboardObjective : scoreboard.getObjectives()) {
			if (scoreboardObjective.method_1116() == ScoreboardCriterion.TRIGGER) {
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

	private static int method_13607(ServerCommandSource serverCommandSource, String string, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();
		if (!scoreboard.playerHasObjective(string, scoreboardObjective)) {
			throw PLAYERS_GET_NULL_EXCEPTION.create(scoreboardObjective.getName(), string);
		} else {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.players.get.success", string, scoreboardPlayerScore.getScore(), scoreboardObjective.method_1120()),
				false
			);
			return scoreboardPlayerScore.getScore();
		}
	}

	private static int method_13584(
		ServerCommandSource serverCommandSource,
		Collection<String> collection,
		ScoreboardObjective scoreboardObjective,
		OperationArgumentType.Operator operator,
		Collection<String> collection2,
		ScoreboardObjective scoreboardObjective2
	) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();
		int i = 0;

		for (String string : collection) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);

			for (String string2 : collection2) {
				ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboard.getPlayerScore(string2, scoreboardObjective2);
				operator.apply(scoreboardPlayerScore, scoreboardPlayerScore2);
			}

			i += scoreboardPlayerScore.getScore();
		}

		if (collection.size() == 1) {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.players.operation.success.single", scoreboardObjective.method_1120(), collection.iterator().next(), i),
				true
			);
		} else {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.players.operation.success.multiple", scoreboardObjective.method_1120(), collection.size()), true
			);
		}

		return i;
	}

	private static int method_13609(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
		if (scoreboardObjective.method_1116() != ScoreboardCriterion.TRIGGER) {
			throw PLAYERS_ENABLE_INVALID_EXCEPTION.create();
		} else {
			Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();
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
					serverCommandSource.method_9226(
						new TranslatableTextComponent("commands.scoreboard.players.enable.success.single", scoreboardObjective.method_1120(), collection.iterator().next()), true
					);
				} else {
					serverCommandSource.method_9226(
						new TranslatableTextComponent("commands.scoreboard.players.enable.success.multiple", scoreboardObjective.method_1120(), collection.size()), true
					);
				}

				return i;
			}
		}
	}

	private static int method_13575(ServerCommandSource serverCommandSource, Collection<String> collection) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();

		for (String string : collection) {
			scoreboard.resetPlayerScore(string, null);
		}

		if (collection.size() == 1) {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.scoreboard.players.reset.all.single", collection.iterator().next()), true);
		} else {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.scoreboard.players.reset.all.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_13586(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();

		for (String string : collection) {
			scoreboard.resetPlayerScore(string, scoreboardObjective);
		}

		if (collection.size() == 1) {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.players.reset.specific.single", scoreboardObjective.method_1120(), collection.iterator().next()), true
			);
		} else {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.players.reset.specific.multiple", scoreboardObjective.method_1120(), collection.size()), true
			);
		}

		return collection.size();
	}

	private static int method_13604(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective, int i) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();

		for (String string : collection) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			scoreboardPlayerScore.setScore(i);
		}

		if (collection.size() == 1) {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.players.set.success.single", scoreboardObjective.method_1120(), collection.iterator().next(), i), true
			);
		} else {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.players.set.success.multiple", scoreboardObjective.method_1120(), collection.size(), i), true
			);
		}

		return i * collection.size();
	}

	private static int method_13578(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective, int i) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();
		int j = 0;

		for (String string : collection) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			scoreboardPlayerScore.setScore(scoreboardPlayerScore.getScore() + i);
			j += scoreboardPlayerScore.getScore();
		}

		if (collection.size() == 1) {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.players.add.success.single", i, scoreboardObjective.method_1120(), collection.iterator().next(), j),
				true
			);
		} else {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.players.add.success.multiple", i, scoreboardObjective.method_1120(), collection.size()), true
			);
		}

		return j;
	}

	private static int method_13600(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective, int i) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();
		int j = 0;

		for (String string : collection) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			scoreboardPlayerScore.setScore(scoreboardPlayerScore.getScore() - i);
			j += scoreboardPlayerScore.getScore();
		}

		if (collection.size() == 1) {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.players.remove.success.single", i, scoreboardObjective.method_1120(), collection.iterator().next(), j),
				true
			);
		} else {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.players.remove.success.multiple", i, scoreboardObjective.method_1120(), collection.size()), true
			);
		}

		return j;
	}

	private static int method_13589(ServerCommandSource serverCommandSource) {
		Collection<String> collection = serverCommandSource.getMinecraftServer().method_3845().getKnownPlayers();
		if (collection.isEmpty()) {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.scoreboard.players.list.empty"), false);
		} else {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.players.list.success", collection.size(), TextFormatter.sortedJoin(collection)), false
			);
		}

		return collection.size();
	}

	private static int method_13614(ServerCommandSource serverCommandSource, String string) {
		Map<ScoreboardObjective, ScoreboardPlayerScore> map = serverCommandSource.getMinecraftServer().method_3845().getPlayerObjectives(string);
		if (map.isEmpty()) {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.scoreboard.players.list.entity.empty", string), false);
		} else {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.scoreboard.players.list.entity.success", string, map.size()), false);

			for (Entry<ScoreboardObjective, ScoreboardPlayerScore> entry : map.entrySet()) {
				serverCommandSource.method_9226(
					new TranslatableTextComponent(
						"commands.scoreboard.players.list.entity.entry",
						((ScoreboardObjective)entry.getKey()).method_1120(),
						((ScoreboardPlayerScore)entry.getValue()).getScore()
					),
					false
				);
			}
		}

		return map.size();
	}

	private static int method_13592(ServerCommandSource serverCommandSource, int i) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();
		if (scoreboard.getObjectiveForSlot(i) == null) {
			throw OBJECTIVES_DISPLAY_ALREADYEMPTY_EXCEPTION.create();
		} else {
			scoreboard.setObjectiveSlot(i, null);
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.scoreboard.objectives.display.cleared", Scoreboard.getDisplaySlotNames()[i]), true);
			return 0;
		}
	}

	private static int method_13596(ServerCommandSource serverCommandSource, int i, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();
		if (scoreboard.getObjectiveForSlot(i) == scoreboardObjective) {
			throw OBJECTIVES_DISPLAY_ALREADYSET_EXCEPTION.create();
		} else {
			scoreboard.setObjectiveSlot(i, scoreboardObjective);
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.objectives.display.set", Scoreboard.getDisplaySlotNames()[i], scoreboardObjective.method_1114()), true
			);
			return 0;
		}
	}

	private static int method_13576(ServerCommandSource serverCommandSource, ScoreboardObjective scoreboardObjective, TextComponent textComponent) {
		if (!scoreboardObjective.method_1114().equals(textComponent)) {
			scoreboardObjective.method_1121(textComponent);
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.scoreboard.objectives.modify.displayname", scoreboardObjective.getName(), scoreboardObjective.method_1120()), true
			);
		}

		return 0;
	}

	private static int method_13581(ServerCommandSource serverCommandSource, ScoreboardObjective scoreboardObjective, ScoreboardCriterion.Type type) {
		if (scoreboardObjective.method_1118() != type) {
			scoreboardObjective.method_1115(type);
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.scoreboard.objectives.modify.rendertype", scoreboardObjective.method_1120()), true);
		}

		return 0;
	}

	private static int method_13602(ServerCommandSource serverCommandSource, ScoreboardObjective scoreboardObjective) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();
		scoreboard.removeObjective(scoreboardObjective);
		serverCommandSource.method_9226(new TranslatableTextComponent("commands.scoreboard.objectives.remove.success", scoreboardObjective.method_1120()), true);
		return scoreboard.getObjectives().size();
	}

	private static int method_13611(ServerCommandSource serverCommandSource, String string, ScoreboardCriterion scoreboardCriterion, TextComponent textComponent) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();
		if (scoreboard.method_1170(string) != null) {
			throw OBJECTIVES_ADD_DUPLICATE_EXCEPTION.create();
		} else if (string.length() > 16) {
			throw ObjectiveArgumentType.LONG_NAME_EXCEPTION.create(16);
		} else {
			scoreboard.method_1168(string, scoreboardCriterion, textComponent, scoreboardCriterion.getCriterionType());
			ScoreboardObjective scoreboardObjective = scoreboard.method_1170(string);
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.scoreboard.objectives.add.success", scoreboardObjective.method_1120()), true);
			return scoreboard.getObjectives().size();
		}
	}

	private static int method_13597(ServerCommandSource serverCommandSource) {
		Collection<ScoreboardObjective> collection = serverCommandSource.getMinecraftServer().method_3845().getObjectives();
		if (collection.isEmpty()) {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.scoreboard.objectives.list.empty"), false);
		} else {
			serverCommandSource.method_9226(
				new TranslatableTextComponent(
					"commands.scoreboard.objectives.list.success", collection.size(), TextFormatter.join(collection, ScoreboardObjective::method_1120)
				),
				false
			);
		}

		return collection.size();
	}
}
