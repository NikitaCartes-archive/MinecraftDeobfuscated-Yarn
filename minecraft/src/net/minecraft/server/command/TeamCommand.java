package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.ColorArgumentType;
import net.minecraft.command.arguments.ComponentArgumentType;
import net.minecraft.command.arguments.ScoreHolderArgumentType;
import net.minecraft.command.arguments.TeamArgumentType;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;

public class TeamCommand {
	private static final SimpleCommandExceptionType ADD_DUPLICATE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.team.add.duplicate")
	);
	private static final DynamicCommandExceptionType ADD_LONGNAME_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.team.add.longName", object)
	);
	private static final SimpleCommandExceptionType EMPTY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.team.empty.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_NAME_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.team.option.name.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_COLOR_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.team.option.color.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_FRIENDLYFIRE_ALREADYENABLED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.team.option.friendlyfire.alreadyEnabled")
	);
	private static final SimpleCommandExceptionType OPTION_FRIENDLYFIRE_ALREADYDISABLED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.team.option.friendlyfire.alreadyDisabled")
	);
	private static final SimpleCommandExceptionType OPTION_SEEFRIENDLYINVISIBLES_ALREADYENABLED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.team.option.seeFriendlyInvisibles.alreadyEnabled")
	);
	private static final SimpleCommandExceptionType SEEFRIENDLYINVISIBLES_ALREADYDSISABLED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.team.option.seeFriendlyInvisibles.alreadyDisabled")
	);
	private static final SimpleCommandExceptionType OPTION_NAMETAGEVISIBILITY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.team.option.nametagVisibility.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_DEATHMESSAGEVISIBILITY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.team.option.deathMessageVisibility.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_COLLISIONRULE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.team.option.collisionRule.unchanged")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("team")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.literal("list")
						.executes(commandContext -> method_13728(commandContext.getSource()))
						.then(
							ServerCommandManager.argument("team", TeamArgumentType.create())
								.executes(commandContext -> method_13748(commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team")))
						)
				)
				.then(
					ServerCommandManager.literal("add")
						.then(
							ServerCommandManager.argument("team", StringArgumentType.word())
								.executes(commandContext -> method_13757(commandContext.getSource(), StringArgumentType.getString(commandContext, "team")))
								.then(
									ServerCommandManager.argument("displayName", ComponentArgumentType.create())
										.executes(
											commandContext -> method_13715(
													commandContext.getSource(),
													StringArgumentType.getString(commandContext, "team"),
													ComponentArgumentType.getComponentArgument(commandContext, "displayName")
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("remove")
						.then(
							ServerCommandManager.argument("team", TeamArgumentType.create())
								.executes(commandContext -> method_13747(commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team")))
						)
				)
				.then(
					ServerCommandManager.literal("empty")
						.then(
							ServerCommandManager.argument("team", TeamArgumentType.create())
								.executes(commandContext -> method_13723(commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team")))
						)
				)
				.then(
					ServerCommandManager.literal("join")
						.then(
							ServerCommandManager.argument("team", TeamArgumentType.create())
								.executes(
									commandContext -> method_13720(
											commandContext.getSource(),
											TeamArgumentType.getTeamArgument(commandContext, "team"),
											Collections.singleton(commandContext.getSource().getEntityOrThrow().getEntityName())
										)
								)
								.then(
									ServerCommandManager.argument("members", ScoreHolderArgumentType.method_9451())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.executes(
											commandContext -> method_13720(
													commandContext.getSource(),
													TeamArgumentType.getTeamArgument(commandContext, "team"),
													ScoreHolderArgumentType.method_9449(commandContext, "members")
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("leave")
						.then(
							ServerCommandManager.argument("members", ScoreHolderArgumentType.method_9451())
								.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
								.executes(commandContext -> method_13714(commandContext.getSource(), ScoreHolderArgumentType.method_9449(commandContext, "members")))
						)
				)
				.then(
					ServerCommandManager.literal("modify")
						.then(
							ServerCommandManager.argument("team", TeamArgumentType.create())
								.then(
									ServerCommandManager.literal("displayName")
										.then(
											ServerCommandManager.argument("displayName", ComponentArgumentType.create())
												.executes(
													commandContext -> method_13711(
															commandContext.getSource(),
															TeamArgumentType.getTeamArgument(commandContext, "team"),
															ComponentArgumentType.getComponentArgument(commandContext, "displayName")
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("color")
										.then(
											ServerCommandManager.argument("value", ColorArgumentType.create())
												.executes(
													commandContext -> method_13745(
															commandContext.getSource(),
															TeamArgumentType.getTeamArgument(commandContext, "team"),
															ColorArgumentType.getColorArgument(commandContext, "value")
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("friendlyFire")
										.then(
											ServerCommandManager.argument("allowed", BoolArgumentType.bool())
												.executes(
													commandContext -> method_13754(
															commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team"), BoolArgumentType.getBool(commandContext, "allowed")
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("seeFriendlyInvisibles")
										.then(
											ServerCommandManager.argument("allowed", BoolArgumentType.bool())
												.executes(
													commandContext -> method_13751(
															commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team"), BoolArgumentType.getBool(commandContext, "allowed")
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("nametagVisibility")
										.then(
											ServerCommandManager.literal("never")
												.executes(
													commandContext -> method_13732(
															commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team"), AbstractScoreboardTeam.VisibilityRule.NEVER
														)
												)
										)
										.then(
											ServerCommandManager.literal("hideForOtherTeams")
												.executes(
													commandContext -> method_13732(
															commandContext.getSource(),
															TeamArgumentType.getTeamArgument(commandContext, "team"),
															AbstractScoreboardTeam.VisibilityRule.HIDDEN_FOR_OTHER_TEAMS
														)
												)
										)
										.then(
											ServerCommandManager.literal("hideForOwnTeam")
												.executes(
													commandContext -> method_13732(
															commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team"), AbstractScoreboardTeam.VisibilityRule.HIDDEN_FOR_TEAM
														)
												)
										)
										.then(
											ServerCommandManager.literal("always")
												.executes(
													commandContext -> method_13732(
															commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team"), AbstractScoreboardTeam.VisibilityRule.ALWAYS
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("deathMessageVisibility")
										.then(
											ServerCommandManager.literal("never")
												.executes(
													commandContext -> method_13735(
															commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team"), AbstractScoreboardTeam.VisibilityRule.NEVER
														)
												)
										)
										.then(
											ServerCommandManager.literal("hideForOtherTeams")
												.executes(
													commandContext -> method_13735(
															commandContext.getSource(),
															TeamArgumentType.getTeamArgument(commandContext, "team"),
															AbstractScoreboardTeam.VisibilityRule.HIDDEN_FOR_OTHER_TEAMS
														)
												)
										)
										.then(
											ServerCommandManager.literal("hideForOwnTeam")
												.executes(
													commandContext -> method_13735(
															commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team"), AbstractScoreboardTeam.VisibilityRule.HIDDEN_FOR_TEAM
														)
												)
										)
										.then(
											ServerCommandManager.literal("always")
												.executes(
													commandContext -> method_13735(
															commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team"), AbstractScoreboardTeam.VisibilityRule.ALWAYS
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("collisionRule")
										.then(
											ServerCommandManager.literal("never")
												.executes(
													commandContext -> method_13713(
															commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team"), AbstractScoreboardTeam.CollisionRule.field_1435
														)
												)
										)
										.then(
											ServerCommandManager.literal("pushOwnTeam")
												.executes(
													commandContext -> method_13713(
															commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team"), AbstractScoreboardTeam.CollisionRule.field_1440
														)
												)
										)
										.then(
											ServerCommandManager.literal("pushOtherTeams")
												.executes(
													commandContext -> method_13713(
															commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team"), AbstractScoreboardTeam.CollisionRule.field_1434
														)
												)
										)
										.then(
											ServerCommandManager.literal("always")
												.executes(
													commandContext -> method_13713(
															commandContext.getSource(), TeamArgumentType.getTeamArgument(commandContext, "team"), AbstractScoreboardTeam.CollisionRule.field_1437
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("prefix")
										.then(
											ServerCommandManager.argument("prefix", ComponentArgumentType.create())
												.executes(
													commandContext -> method_13743(
															commandContext.getSource(),
															TeamArgumentType.getTeamArgument(commandContext, "team"),
															ComponentArgumentType.getComponentArgument(commandContext, "prefix")
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("suffix")
										.then(
											ServerCommandManager.argument("suffix", ComponentArgumentType.create())
												.executes(
													commandContext -> method_13756(
															commandContext.getSource(),
															TeamArgumentType.getTeamArgument(commandContext, "team"),
															ComponentArgumentType.getComponentArgument(commandContext, "suffix")
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_13714(ServerCommandSource serverCommandSource, Collection<String> collection) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();

		for (String string : collection) {
			scoreboard.clearPlayerTeam(string);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.leave.success.single", collection.iterator().next()), true);
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.leave.success.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_13720(ServerCommandSource serverCommandSource, ScoreboardTeam scoreboardTeam, Collection<String> collection) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();

		for (String string : collection) {
			scoreboard.addPlayerToTeam(string, scoreboardTeam);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.join.success.single", collection.iterator().next(), scoreboardTeam.method_1148()), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.join.success.multiple", collection.size(), scoreboardTeam.method_1148()), true);
		}

		return collection.size();
	}

	private static int method_13732(ServerCommandSource serverCommandSource, ScoreboardTeam scoreboardTeam, AbstractScoreboardTeam.VisibilityRule visibilityRule) throws CommandSyntaxException {
		if (scoreboardTeam.getNameTagVisibilityRule() == visibilityRule) {
			throw OPTION_NAMETAGEVISIBILITY_UNCHANGED_EXCEPTION.create();
		} else {
			scoreboardTeam.setNameTagVisibilityRule(visibilityRule);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.option.nametagVisibility.success", scoreboardTeam.method_1148(), visibilityRule.method_1214()), true
			);
			return 0;
		}
	}

	private static int method_13735(ServerCommandSource serverCommandSource, ScoreboardTeam scoreboardTeam, AbstractScoreboardTeam.VisibilityRule visibilityRule) throws CommandSyntaxException {
		if (scoreboardTeam.getDeathMessageVisibilityRule() == visibilityRule) {
			throw OPTION_DEATHMESSAGEVISIBILITY_UNCHANGED_EXCEPTION.create();
		} else {
			scoreboardTeam.setDeathMessageVisibilityRule(visibilityRule);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.option.deathMessageVisibility.success", scoreboardTeam.method_1148(), visibilityRule.method_1214()), true
			);
			return 0;
		}
	}

	private static int method_13713(ServerCommandSource serverCommandSource, ScoreboardTeam scoreboardTeam, AbstractScoreboardTeam.CollisionRule collisionRule) throws CommandSyntaxException {
		if (scoreboardTeam.getCollisionRule() == collisionRule) {
			throw OPTION_COLLISIONRULE_UNCHANGED_EXCEPTION.create();
		} else {
			scoreboardTeam.setCollisionRule(collisionRule);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.option.collisionRule.success", scoreboardTeam.method_1148(), collisionRule.method_1209()), true
			);
			return 0;
		}
	}

	private static int method_13751(ServerCommandSource serverCommandSource, ScoreboardTeam scoreboardTeam, boolean bl) throws CommandSyntaxException {
		if (scoreboardTeam.shouldShowFriendlyInvisibles() == bl) {
			if (bl) {
				throw OPTION_SEEFRIENDLYINVISIBLES_ALREADYENABLED_EXCEPTION.create();
			} else {
				throw SEEFRIENDLYINVISIBLES_ALREADYDSISABLED_EXCEPTION.create();
			}
		} else {
			scoreboardTeam.setShowFriendlyInvisibles(bl);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.option.seeFriendlyInvisibles." + (bl ? "enabled" : "disabled"), scoreboardTeam.method_1148()), true
			);
			return 0;
		}
	}

	private static int method_13754(ServerCommandSource serverCommandSource, ScoreboardTeam scoreboardTeam, boolean bl) throws CommandSyntaxException {
		if (scoreboardTeam.isFriendlyFireAllowed() == bl) {
			if (bl) {
				throw OPTION_FRIENDLYFIRE_ALREADYENABLED_EXCEPTION.create();
			} else {
				throw OPTION_FRIENDLYFIRE_ALREADYDISABLED_EXCEPTION.create();
			}
		} else {
			scoreboardTeam.setFriendlyFireAllowed(bl);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.option.friendlyfire." + (bl ? "enabled" : "disabled"), scoreboardTeam.method_1148()), true
			);
			return 0;
		}
	}

	private static int method_13711(ServerCommandSource serverCommandSource, ScoreboardTeam scoreboardTeam, TextComponent textComponent) throws CommandSyntaxException {
		if (scoreboardTeam.getDisplayName().equals(textComponent)) {
			throw OPTION_NAME_UNCHANGED_EXCEPTION.create();
		} else {
			scoreboardTeam.method_1137(textComponent);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.option.name.success", scoreboardTeam.method_1148()), true);
			return 0;
		}
	}

	private static int method_13745(ServerCommandSource serverCommandSource, ScoreboardTeam scoreboardTeam, TextFormat textFormat) throws CommandSyntaxException {
		if (scoreboardTeam.getColor() == textFormat) {
			throw OPTION_COLOR_UNCHANGED_EXCEPTION.create();
		} else {
			scoreboardTeam.setColor(textFormat);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.option.color.success", scoreboardTeam.method_1148(), textFormat.getFormatName()), true
			);
			return 0;
		}
	}

	private static int method_13723(ServerCommandSource serverCommandSource, ScoreboardTeam scoreboardTeam) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();
		Collection<String> collection = Lists.<String>newArrayList(scoreboardTeam.getPlayerList());
		if (collection.isEmpty()) {
			throw EMPTY_UNCHANGED_EXCEPTION.create();
		} else {
			for (String string : collection) {
				scoreboard.removePlayerFromTeam(string, scoreboardTeam);
			}

			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.empty.success", collection.size(), scoreboardTeam.method_1148()), true);
			return collection.size();
		}
	}

	private static int method_13747(ServerCommandSource serverCommandSource, ScoreboardTeam scoreboardTeam) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();
		scoreboard.removeTeam(scoreboardTeam);
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.remove.success", scoreboardTeam.method_1148()), true);
		return scoreboard.getTeams().size();
	}

	private static int method_13757(ServerCommandSource serverCommandSource, String string) throws CommandSyntaxException {
		return method_13715(serverCommandSource, string, new StringTextComponent(string));
	}

	private static int method_13715(ServerCommandSource serverCommandSource, String string, TextComponent textComponent) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().method_3845();
		if (scoreboard.getTeam(string) != null) {
			throw ADD_DUPLICATE_EXCEPTION.create();
		} else if (string.length() > 16) {
			throw ADD_LONGNAME_EXCEPTION.create(16);
		} else {
			ScoreboardTeam scoreboardTeam = scoreboard.addTeam(string);
			scoreboardTeam.method_1137(textComponent);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.add.success", scoreboardTeam.method_1148()), true);
			return scoreboard.getTeams().size();
		}
	}

	private static int method_13748(ServerCommandSource serverCommandSource, ScoreboardTeam scoreboardTeam) {
		Collection<String> collection = scoreboardTeam.getPlayerList();
		if (collection.isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.list.members.empty", scoreboardTeam.method_1148()), false);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.list.members.success", scoreboardTeam.method_1148(), collection.size(), TextFormatter.sortedJoin(collection)),
				false
			);
		}

		return collection.size();
	}

	private static int method_13728(ServerCommandSource serverCommandSource) {
		Collection<ScoreboardTeam> collection = serverCommandSource.getMinecraftServer().method_3845().getTeams();
		if (collection.isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.list.teams.empty"), false);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.list.teams.success", collection.size(), TextFormatter.join(collection, ScoreboardTeam::method_1148)), false
			);
		}

		return collection.size();
	}

	private static int method_13743(ServerCommandSource serverCommandSource, ScoreboardTeam scoreboardTeam, TextComponent textComponent) {
		scoreboardTeam.method_1138(textComponent);
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.option.prefix.success", textComponent), false);
		return 1;
	}

	private static int method_13756(ServerCommandSource serverCommandSource, ScoreboardTeam scoreboardTeam, TextComponent textComponent) {
		scoreboardTeam.method_1139(textComponent);
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.option.suffix.success", textComponent), false);
		return 1;
	}
}
