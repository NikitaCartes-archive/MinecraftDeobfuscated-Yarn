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
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
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
			CommandManager.literal("team")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("list")
						.executes(commandContext -> executeListTeams(commandContext.getSource()))
						.then(
							CommandManager.argument("team", TeamArgumentType.create())
								.executes(commandContext -> executeListMembers(commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team")))
						)
				)
				.then(
					CommandManager.literal("add")
						.then(
							CommandManager.argument("team", StringArgumentType.word())
								.executes(commandContext -> executeAdd(commandContext.getSource(), StringArgumentType.getString(commandContext, "team")))
								.then(
									CommandManager.argument("displayName", ComponentArgumentType.create())
										.executes(
											commandContext -> executeAdd(
													commandContext.getSource(),
													StringArgumentType.getString(commandContext, "team"),
													ComponentArgumentType.getComponent(commandContext, "displayName")
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("remove")
						.then(
							CommandManager.argument("team", TeamArgumentType.create())
								.executes(commandContext -> executeRemove(commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team")))
						)
				)
				.then(
					CommandManager.literal("empty")
						.then(
							CommandManager.argument("team", TeamArgumentType.create())
								.executes(commandContext -> executeEmpty(commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team")))
						)
				)
				.then(
					CommandManager.literal("join")
						.then(
							CommandManager.argument("team", TeamArgumentType.create())
								.executes(
									commandContext -> executeJoin(
											commandContext.getSource(),
											TeamArgumentType.getTeam(commandContext, "team"),
											Collections.singleton(commandContext.getSource().getEntityOrThrow().getEntityName())
										)
								)
								.then(
									CommandManager.argument("members", ScoreHolderArgumentType.scoreHolders())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.executes(
											commandContext -> executeJoin(
													commandContext.getSource(),
													TeamArgumentType.getTeam(commandContext, "team"),
													ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "members")
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("leave")
						.then(
							CommandManager.argument("members", ScoreHolderArgumentType.scoreHolders())
								.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
								.executes(commandContext -> executeLeave(commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "members")))
						)
				)
				.then(
					CommandManager.literal("modify")
						.then(
							CommandManager.argument("team", TeamArgumentType.create())
								.then(
									CommandManager.literal("displayName")
										.then(
											CommandManager.argument("displayName", ComponentArgumentType.create())
												.executes(
													commandContext -> executeModifyDisplayName(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), ComponentArgumentType.getComponent(commandContext, "displayName")
														)
												)
										)
								)
								.then(
									CommandManager.literal("color")
										.then(
											CommandManager.argument("value", ColorArgumentType.create())
												.executes(
													commandContext -> executeModifyColor(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), ColorArgumentType.getColor(commandContext, "value")
														)
												)
										)
								)
								.then(
									CommandManager.literal("friendlyFire")
										.then(
											CommandManager.argument("allowed", BoolArgumentType.bool())
												.executes(
													commandContext -> executeModifyFriendlyFire(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), BoolArgumentType.getBool(commandContext, "allowed")
														)
												)
										)
								)
								.then(
									CommandManager.literal("seeFriendlyInvisibles")
										.then(
											CommandManager.argument("allowed", BoolArgumentType.bool())
												.executes(
													commandContext -> executeModifySeeFriendlyInvisibles(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), BoolArgumentType.getBool(commandContext, "allowed")
														)
												)
										)
								)
								.then(
									CommandManager.literal("nametagVisibility")
										.then(
											CommandManager.literal("never")
												.executes(
													commandContext -> executeModifyNametagVisibility(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.NEVER
														)
												)
										)
										.then(
											CommandManager.literal("hideForOtherTeams")
												.executes(
													commandContext -> executeModifyNametagVisibility(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.HIDDEN_FOR_OTHER_TEAMS
														)
												)
										)
										.then(
											CommandManager.literal("hideForOwnTeam")
												.executes(
													commandContext -> executeModifyNametagVisibility(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.HIDDEN_FOR_TEAM
														)
												)
										)
										.then(
											CommandManager.literal("always")
												.executes(
													commandContext -> executeModifyNametagVisibility(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.ALWAYS
														)
												)
										)
								)
								.then(
									CommandManager.literal("deathMessageVisibility")
										.then(
											CommandManager.literal("never")
												.executes(
													commandContext -> executeModifyDeathMessageVisibility(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.NEVER
														)
												)
										)
										.then(
											CommandManager.literal("hideForOtherTeams")
												.executes(
													commandContext -> executeModifyDeathMessageVisibility(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.HIDDEN_FOR_OTHER_TEAMS
														)
												)
										)
										.then(
											CommandManager.literal("hideForOwnTeam")
												.executes(
													commandContext -> executeModifyDeathMessageVisibility(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.HIDDEN_FOR_TEAM
														)
												)
										)
										.then(
											CommandManager.literal("always")
												.executes(
													commandContext -> executeModifyDeathMessageVisibility(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.ALWAYS
														)
												)
										)
								)
								.then(
									CommandManager.literal("collisionRule")
										.then(
											CommandManager.literal("never")
												.executes(
													commandContext -> executeModifyCollisionRule(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.CollisionRule.field_1435
														)
												)
										)
										.then(
											CommandManager.literal("pushOwnTeam")
												.executes(
													commandContext -> executeModifyCollisionRule(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.CollisionRule.field_1440
														)
												)
										)
										.then(
											CommandManager.literal("pushOtherTeams")
												.executes(
													commandContext -> executeModifyCollisionRule(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.CollisionRule.field_1434
														)
												)
										)
										.then(
											CommandManager.literal("always")
												.executes(
													commandContext -> executeModifyCollisionRule(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.CollisionRule.ALWAYS
														)
												)
										)
								)
								.then(
									CommandManager.literal("prefix")
										.then(
											CommandManager.argument("prefix", ComponentArgumentType.create())
												.executes(
													commandContext -> executeModifyPrefix(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), ComponentArgumentType.getComponent(commandContext, "prefix")
														)
												)
										)
								)
								.then(
									CommandManager.literal("suffix")
										.then(
											CommandManager.argument("suffix", ComponentArgumentType.create())
												.executes(
													commandContext -> executeModifySuffix(
															commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), ComponentArgumentType.getComponent(commandContext, "suffix")
														)
												)
										)
								)
						)
				)
		);
	}

	private static int executeLeave(ServerCommandSource serverCommandSource, Collection<String> collection) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();

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

	private static int executeJoin(ServerCommandSource serverCommandSource, Team team, Collection<String> collection) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();

		for (String string : collection) {
			scoreboard.addPlayerToTeam(string, team);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.join.success.single", collection.iterator().next(), team.getFormattedName()), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.join.success.multiple", collection.size(), team.getFormattedName()), true);
		}

		return collection.size();
	}

	private static int executeModifyNametagVisibility(ServerCommandSource serverCommandSource, Team team, AbstractTeam.VisibilityRule visibilityRule) throws CommandSyntaxException {
		if (team.getNameTagVisibilityRule() == visibilityRule) {
			throw OPTION_NAMETAGEVISIBILITY_UNCHANGED_EXCEPTION.create();
		} else {
			team.setNameTagVisibilityRule(visibilityRule);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.option.nametagVisibility.success", team.getFormattedName(), visibilityRule.getTranslationKey()), true
			);
			return 0;
		}
	}

	private static int executeModifyDeathMessageVisibility(ServerCommandSource serverCommandSource, Team team, AbstractTeam.VisibilityRule visibilityRule) throws CommandSyntaxException {
		if (team.getDeathMessageVisibilityRule() == visibilityRule) {
			throw OPTION_DEATHMESSAGEVISIBILITY_UNCHANGED_EXCEPTION.create();
		} else {
			team.setDeathMessageVisibilityRule(visibilityRule);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.option.deathMessageVisibility.success", team.getFormattedName(), visibilityRule.getTranslationKey()), true
			);
			return 0;
		}
	}

	private static int executeModifyCollisionRule(ServerCommandSource serverCommandSource, Team team, AbstractTeam.CollisionRule collisionRule) throws CommandSyntaxException {
		if (team.getCollisionRule() == collisionRule) {
			throw OPTION_COLLISIONRULE_UNCHANGED_EXCEPTION.create();
		} else {
			team.setCollisionRule(collisionRule);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.option.collisionRule.success", team.getFormattedName(), collisionRule.getTranslationKey()), true
			);
			return 0;
		}
	}

	private static int executeModifySeeFriendlyInvisibles(ServerCommandSource serverCommandSource, Team team, boolean bl) throws CommandSyntaxException {
		if (team.shouldShowFriendlyInvisibles() == bl) {
			if (bl) {
				throw OPTION_SEEFRIENDLYINVISIBLES_ALREADYENABLED_EXCEPTION.create();
			} else {
				throw SEEFRIENDLYINVISIBLES_ALREADYDSISABLED_EXCEPTION.create();
			}
		} else {
			team.setShowFriendlyInvisibles(bl);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.option.seeFriendlyInvisibles." + (bl ? "enabled" : "disabled"), team.getFormattedName()), true
			);
			return 0;
		}
	}

	private static int executeModifyFriendlyFire(ServerCommandSource serverCommandSource, Team team, boolean bl) throws CommandSyntaxException {
		if (team.isFriendlyFireAllowed() == bl) {
			if (bl) {
				throw OPTION_FRIENDLYFIRE_ALREADYENABLED_EXCEPTION.create();
			} else {
				throw OPTION_FRIENDLYFIRE_ALREADYDISABLED_EXCEPTION.create();
			}
		} else {
			team.setFriendlyFireAllowed(bl);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.option.friendlyfire." + (bl ? "enabled" : "disabled"), team.getFormattedName()), true
			);
			return 0;
		}
	}

	private static int executeModifyDisplayName(ServerCommandSource serverCommandSource, Team team, TextComponent textComponent) throws CommandSyntaxException {
		if (team.getDisplayName().equals(textComponent)) {
			throw OPTION_NAME_UNCHANGED_EXCEPTION.create();
		} else {
			team.setDisplayName(textComponent);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.option.name.success", team.getFormattedName()), true);
			return 0;
		}
	}

	private static int executeModifyColor(ServerCommandSource serverCommandSource, Team team, TextFormat textFormat) throws CommandSyntaxException {
		if (team.getColor() == textFormat) {
			throw OPTION_COLOR_UNCHANGED_EXCEPTION.create();
		} else {
			team.setColor(textFormat);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.option.color.success", team.getFormattedName(), textFormat.getName()), true);
			return 0;
		}
	}

	private static int executeEmpty(ServerCommandSource serverCommandSource, Team team) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		Collection<String> collection = Lists.<String>newArrayList(team.getPlayerList());
		if (collection.isEmpty()) {
			throw EMPTY_UNCHANGED_EXCEPTION.create();
		} else {
			for (String string : collection) {
				scoreboard.removePlayerFromTeam(string, team);
			}

			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.empty.success", collection.size(), team.getFormattedName()), true);
			return collection.size();
		}
	}

	private static int executeRemove(ServerCommandSource serverCommandSource, Team team) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		scoreboard.removeTeam(team);
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.remove.success", team.getFormattedName()), true);
		return scoreboard.getTeams().size();
	}

	private static int executeAdd(ServerCommandSource serverCommandSource, String string) throws CommandSyntaxException {
		return executeAdd(serverCommandSource, string, new StringTextComponent(string));
	}

	private static int executeAdd(ServerCommandSource serverCommandSource, String string, TextComponent textComponent) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		if (scoreboard.getTeam(string) != null) {
			throw ADD_DUPLICATE_EXCEPTION.create();
		} else if (string.length() > 16) {
			throw ADD_LONGNAME_EXCEPTION.create(16);
		} else {
			Team team = scoreboard.addTeam(string);
			team.setDisplayName(textComponent);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.add.success", team.getFormattedName()), true);
			return scoreboard.getTeams().size();
		}
	}

	private static int executeListMembers(ServerCommandSource serverCommandSource, Team team) {
		Collection<String> collection = team.getPlayerList();
		if (collection.isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.list.members.empty", team.getFormattedName()), false);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.list.members.success", team.getFormattedName(), collection.size(), TextFormatter.sortedJoin(collection)),
				false
			);
		}

		return collection.size();
	}

	private static int executeListTeams(ServerCommandSource serverCommandSource) {
		Collection<Team> collection = serverCommandSource.getMinecraftServer().getScoreboard().getTeams();
		if (collection.isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.list.teams.empty"), false);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.team.list.teams.success", collection.size(), TextFormatter.join(collection, Team::getFormattedName)), false
			);
		}

		return collection.size();
	}

	private static int executeModifyPrefix(ServerCommandSource serverCommandSource, Team team, TextComponent textComponent) {
		team.setPrefix(textComponent);
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.option.prefix.success", textComponent), false);
		return 1;
	}

	private static int executeModifySuffix(ServerCommandSource serverCommandSource, Team team, TextComponent textComponent) {
		team.setSuffix(textComponent);
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.team.option.suffix.success", textComponent), false);
		return 1;
	}
}
