package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.TeamArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

public class TeamCommand {
	private static final SimpleCommandExceptionType ADD_DUPLICATE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.team.add.duplicate"));
	private static final SimpleCommandExceptionType EMPTY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.team.empty.unchanged"));
	private static final SimpleCommandExceptionType OPTION_NAME_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.team.option.name.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_COLOR_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.team.option.color.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_FRIENDLY_FIRE_ALREADY_ENABLED_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.team.option.friendlyfire.alreadyEnabled")
	);
	private static final SimpleCommandExceptionType OPTION_FRIENDLY_FIRE_ALREADY_DISABLED_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.team.option.friendlyfire.alreadyDisabled")
	);
	private static final SimpleCommandExceptionType OPTION_SEE_FRIENDLY_INVISIBLES_ALREADY_ENABLED_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.team.option.seeFriendlyInvisibles.alreadyEnabled")
	);
	private static final SimpleCommandExceptionType OPTION_SEE_FRIENDLY_INVISIBLES_ALREADY_DISABLED_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.team.option.seeFriendlyInvisibles.alreadyDisabled")
	);
	private static final SimpleCommandExceptionType OPTION_NAMETAG_VISIBILITY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.team.option.nametagVisibility.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_DEATH_MESSAGE_VISIBILITY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.team.option.deathMessageVisibility.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_COLLISION_RULE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.team.option.collisionRule.unchanged")
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("team")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.literal("list")
						.executes(context -> executeListTeams(context.getSource()))
						.then(
							CommandManager.argument("team", TeamArgumentType.team())
								.executes(context -> executeListMembers(context.getSource(), TeamArgumentType.getTeam(context, "team")))
						)
				)
				.then(
					CommandManager.literal("add")
						.then(
							CommandManager.argument("team", StringArgumentType.word())
								.executes(context -> executeAdd(context.getSource(), StringArgumentType.getString(context, "team")))
								.then(
									CommandManager.argument("displayName", TextArgumentType.text())
										.executes(
											context -> executeAdd(context.getSource(), StringArgumentType.getString(context, "team"), TextArgumentType.getTextArgument(context, "displayName"))
										)
								)
						)
				)
				.then(
					CommandManager.literal("remove")
						.then(
							CommandManager.argument("team", TeamArgumentType.team())
								.executes(context -> executeRemove(context.getSource(), TeamArgumentType.getTeam(context, "team")))
						)
				)
				.then(
					CommandManager.literal("empty")
						.then(
							CommandManager.argument("team", TeamArgumentType.team())
								.executes(context -> executeEmpty(context.getSource(), TeamArgumentType.getTeam(context, "team")))
						)
				)
				.then(
					CommandManager.literal("join")
						.then(
							CommandManager.argument("team", TeamArgumentType.team())
								.executes(
									context -> executeJoin(
											context.getSource(), TeamArgumentType.getTeam(context, "team"), Collections.singleton(context.getSource().getEntityOrThrow().getEntityName())
										)
								)
								.then(
									CommandManager.argument("members", ScoreHolderArgumentType.scoreHolders())
										.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
										.executes(
											context -> executeJoin(
													context.getSource(), TeamArgumentType.getTeam(context, "team"), ScoreHolderArgumentType.getScoreboardScoreHolders(context, "members")
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
								.executes(context -> executeLeave(context.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(context, "members")))
						)
				)
				.then(
					CommandManager.literal("modify")
						.then(
							CommandManager.argument("team", TeamArgumentType.team())
								.then(
									CommandManager.literal("displayName")
										.then(
											CommandManager.argument("displayName", TextArgumentType.text())
												.executes(
													context -> executeModifyDisplayName(
															context.getSource(), TeamArgumentType.getTeam(context, "team"), TextArgumentType.getTextArgument(context, "displayName")
														)
												)
										)
								)
								.then(
									CommandManager.literal("color")
										.then(
											CommandManager.argument("value", ColorArgumentType.color())
												.executes(
													context -> executeModifyColor(context.getSource(), TeamArgumentType.getTeam(context, "team"), ColorArgumentType.getColor(context, "value"))
												)
										)
								)
								.then(
									CommandManager.literal("friendlyFire")
										.then(
											CommandManager.argument("allowed", BoolArgumentType.bool())
												.executes(
													context -> executeModifyFriendlyFire(context.getSource(), TeamArgumentType.getTeam(context, "team"), BoolArgumentType.getBool(context, "allowed"))
												)
										)
								)
								.then(
									CommandManager.literal("seeFriendlyInvisibles")
										.then(
											CommandManager.argument("allowed", BoolArgumentType.bool())
												.executes(
													context -> executeModifySeeFriendlyInvisibles(
															context.getSource(), TeamArgumentType.getTeam(context, "team"), BoolArgumentType.getBool(context, "allowed")
														)
												)
										)
								)
								.then(
									CommandManager.literal("nametagVisibility")
										.then(
											CommandManager.literal("never")
												.executes(
													context -> executeModifyNametagVisibility(context.getSource(), TeamArgumentType.getTeam(context, "team"), AbstractTeam.VisibilityRule.NEVER)
												)
										)
										.then(
											CommandManager.literal("hideForOtherTeams")
												.executes(
													context -> executeModifyNametagVisibility(
															context.getSource(), TeamArgumentType.getTeam(context, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS
														)
												)
										)
										.then(
											CommandManager.literal("hideForOwnTeam")
												.executes(
													context -> executeModifyNametagVisibility(
															context.getSource(), TeamArgumentType.getTeam(context, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM
														)
												)
										)
										.then(
											CommandManager.literal("always")
												.executes(
													context -> executeModifyNametagVisibility(context.getSource(), TeamArgumentType.getTeam(context, "team"), AbstractTeam.VisibilityRule.ALWAYS)
												)
										)
								)
								.then(
									CommandManager.literal("deathMessageVisibility")
										.then(
											CommandManager.literal("never")
												.executes(
													context -> executeModifyDeathMessageVisibility(context.getSource(), TeamArgumentType.getTeam(context, "team"), AbstractTeam.VisibilityRule.NEVER)
												)
										)
										.then(
											CommandManager.literal("hideForOtherTeams")
												.executes(
													context -> executeModifyDeathMessageVisibility(
															context.getSource(), TeamArgumentType.getTeam(context, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS
														)
												)
										)
										.then(
											CommandManager.literal("hideForOwnTeam")
												.executes(
													context -> executeModifyDeathMessageVisibility(
															context.getSource(), TeamArgumentType.getTeam(context, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM
														)
												)
										)
										.then(
											CommandManager.literal("always")
												.executes(
													context -> executeModifyDeathMessageVisibility(context.getSource(), TeamArgumentType.getTeam(context, "team"), AbstractTeam.VisibilityRule.ALWAYS)
												)
										)
								)
								.then(
									CommandManager.literal("collisionRule")
										.then(
											CommandManager.literal("never")
												.executes(context -> executeModifyCollisionRule(context.getSource(), TeamArgumentType.getTeam(context, "team"), AbstractTeam.CollisionRule.NEVER))
										)
										.then(
											CommandManager.literal("pushOwnTeam")
												.executes(
													context -> executeModifyCollisionRule(context.getSource(), TeamArgumentType.getTeam(context, "team"), AbstractTeam.CollisionRule.PUSH_OWN_TEAM)
												)
										)
										.then(
											CommandManager.literal("pushOtherTeams")
												.executes(
													context -> executeModifyCollisionRule(context.getSource(), TeamArgumentType.getTeam(context, "team"), AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS)
												)
										)
										.then(
											CommandManager.literal("always")
												.executes(context -> executeModifyCollisionRule(context.getSource(), TeamArgumentType.getTeam(context, "team"), AbstractTeam.CollisionRule.ALWAYS))
										)
								)
								.then(
									CommandManager.literal("prefix")
										.then(
											CommandManager.argument("prefix", TextArgumentType.text())
												.executes(
													context -> executeModifyPrefix(context.getSource(), TeamArgumentType.getTeam(context, "team"), TextArgumentType.getTextArgument(context, "prefix"))
												)
										)
								)
								.then(
									CommandManager.literal("suffix")
										.then(
											CommandManager.argument("suffix", TextArgumentType.text())
												.executes(
													context -> executeModifySuffix(context.getSource(), TeamArgumentType.getTeam(context, "team"), TextArgumentType.getTextArgument(context, "suffix"))
												)
										)
								)
						)
				)
		);
	}

	private static int executeLeave(ServerCommandSource source, Collection<String> members) {
		Scoreboard scoreboard = source.getServer().getScoreboard();

		for (String string : members) {
			scoreboard.clearPlayerTeam(string);
		}

		if (members.size() == 1) {
			source.sendFeedback(() -> Text.translatable("commands.team.leave.success.single", members.iterator().next()), true);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.team.leave.success.multiple", members.size()), true);
		}

		return members.size();
	}

	private static int executeJoin(ServerCommandSource source, Team team, Collection<String> members) {
		Scoreboard scoreboard = source.getServer().getScoreboard();

		for (String string : members) {
			scoreboard.addPlayerToTeam(string, team);
		}

		if (members.size() == 1) {
			source.sendFeedback(() -> Text.translatable("commands.team.join.success.single", members.iterator().next(), team.getFormattedName()), true);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.team.join.success.multiple", members.size(), team.getFormattedName()), true);
		}

		return members.size();
	}

	private static int executeModifyNametagVisibility(ServerCommandSource source, Team team, AbstractTeam.VisibilityRule visibility) throws CommandSyntaxException {
		if (team.getNameTagVisibilityRule() == visibility) {
			throw OPTION_NAMETAG_VISIBILITY_UNCHANGED_EXCEPTION.create();
		} else {
			team.setNameTagVisibilityRule(visibility);
			source.sendFeedback(() -> Text.translatable("commands.team.option.nametagVisibility.success", team.getFormattedName(), visibility.getDisplayName()), true);
			return 0;
		}
	}

	private static int executeModifyDeathMessageVisibility(ServerCommandSource source, Team team, AbstractTeam.VisibilityRule visibility) throws CommandSyntaxException {
		if (team.getDeathMessageVisibilityRule() == visibility) {
			throw OPTION_DEATH_MESSAGE_VISIBILITY_UNCHANGED_EXCEPTION.create();
		} else {
			team.setDeathMessageVisibilityRule(visibility);
			source.sendFeedback(
				() -> Text.translatable("commands.team.option.deathMessageVisibility.success", team.getFormattedName(), visibility.getDisplayName()), true
			);
			return 0;
		}
	}

	private static int executeModifyCollisionRule(ServerCommandSource source, Team team, AbstractTeam.CollisionRule collisionRule) throws CommandSyntaxException {
		if (team.getCollisionRule() == collisionRule) {
			throw OPTION_COLLISION_RULE_UNCHANGED_EXCEPTION.create();
		} else {
			team.setCollisionRule(collisionRule);
			source.sendFeedback(() -> Text.translatable("commands.team.option.collisionRule.success", team.getFormattedName(), collisionRule.getDisplayName()), true);
			return 0;
		}
	}

	private static int executeModifySeeFriendlyInvisibles(ServerCommandSource source, Team team, boolean allowed) throws CommandSyntaxException {
		if (team.shouldShowFriendlyInvisibles() == allowed) {
			if (allowed) {
				throw OPTION_SEE_FRIENDLY_INVISIBLES_ALREADY_ENABLED_EXCEPTION.create();
			} else {
				throw OPTION_SEE_FRIENDLY_INVISIBLES_ALREADY_DISABLED_EXCEPTION.create();
			}
		} else {
			team.setShowFriendlyInvisibles(allowed);
			source.sendFeedback(
				() -> Text.translatable("commands.team.option.seeFriendlyInvisibles." + (allowed ? "enabled" : "disabled"), team.getFormattedName()), true
			);
			return 0;
		}
	}

	private static int executeModifyFriendlyFire(ServerCommandSource source, Team team, boolean allowed) throws CommandSyntaxException {
		if (team.isFriendlyFireAllowed() == allowed) {
			if (allowed) {
				throw OPTION_FRIENDLY_FIRE_ALREADY_ENABLED_EXCEPTION.create();
			} else {
				throw OPTION_FRIENDLY_FIRE_ALREADY_DISABLED_EXCEPTION.create();
			}
		} else {
			team.setFriendlyFireAllowed(allowed);
			source.sendFeedback(() -> Text.translatable("commands.team.option.friendlyfire." + (allowed ? "enabled" : "disabled"), team.getFormattedName()), true);
			return 0;
		}
	}

	private static int executeModifyDisplayName(ServerCommandSource source, Team team, Text displayName) throws CommandSyntaxException {
		if (team.getDisplayName().equals(displayName)) {
			throw OPTION_NAME_UNCHANGED_EXCEPTION.create();
		} else {
			team.setDisplayName(displayName);
			source.sendFeedback(() -> Text.translatable("commands.team.option.name.success", team.getFormattedName()), true);
			return 0;
		}
	}

	private static int executeModifyColor(ServerCommandSource source, Team team, Formatting color) throws CommandSyntaxException {
		if (team.getColor() == color) {
			throw OPTION_COLOR_UNCHANGED_EXCEPTION.create();
		} else {
			team.setColor(color);
			source.sendFeedback(() -> Text.translatable("commands.team.option.color.success", team.getFormattedName(), color.getName()), true);
			return 0;
		}
	}

	private static int executeEmpty(ServerCommandSource source, Team team) throws CommandSyntaxException {
		Scoreboard scoreboard = source.getServer().getScoreboard();
		Collection<String> collection = Lists.<String>newArrayList(team.getPlayerList());
		if (collection.isEmpty()) {
			throw EMPTY_UNCHANGED_EXCEPTION.create();
		} else {
			for (String string : collection) {
				scoreboard.removePlayerFromTeam(string, team);
			}

			source.sendFeedback(() -> Text.translatable("commands.team.empty.success", collection.size(), team.getFormattedName()), true);
			return collection.size();
		}
	}

	private static int executeRemove(ServerCommandSource source, Team team) {
		Scoreboard scoreboard = source.getServer().getScoreboard();
		scoreboard.removeTeam(team);
		source.sendFeedback(() -> Text.translatable("commands.team.remove.success", team.getFormattedName()), true);
		return scoreboard.getTeams().size();
	}

	private static int executeAdd(ServerCommandSource source, String team) throws CommandSyntaxException {
		return executeAdd(source, team, Text.literal(team));
	}

	private static int executeAdd(ServerCommandSource source, String team, Text displayName) throws CommandSyntaxException {
		Scoreboard scoreboard = source.getServer().getScoreboard();
		if (scoreboard.getTeam(team) != null) {
			throw ADD_DUPLICATE_EXCEPTION.create();
		} else {
			Team team2 = scoreboard.addTeam(team);
			team2.setDisplayName(displayName);
			source.sendFeedback(() -> Text.translatable("commands.team.add.success", team2.getFormattedName()), true);
			return scoreboard.getTeams().size();
		}
	}

	private static int executeListMembers(ServerCommandSource source, Team team) {
		Collection<String> collection = team.getPlayerList();
		if (collection.isEmpty()) {
			source.sendFeedback(() -> Text.translatable("commands.team.list.members.empty", team.getFormattedName()), false);
		} else {
			source.sendFeedback(
				() -> Text.translatable("commands.team.list.members.success", team.getFormattedName(), collection.size(), Texts.joinOrdered(collection)), false
			);
		}

		return collection.size();
	}

	private static int executeListTeams(ServerCommandSource source) {
		Collection<Team> collection = source.getServer().getScoreboard().getTeams();
		if (collection.isEmpty()) {
			source.sendFeedback(() -> Text.translatable("commands.team.list.teams.empty"), false);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.team.list.teams.success", collection.size(), Texts.join(collection, Team::getFormattedName)), false);
		}

		return collection.size();
	}

	private static int executeModifyPrefix(ServerCommandSource source, Team team, Text prefix) {
		team.setPrefix(prefix);
		source.sendFeedback(() -> Text.translatable("commands.team.option.prefix.success", prefix), false);
		return 1;
	}

	private static int executeModifySuffix(ServerCommandSource source, Team team, Text suffix) {
		team.setSuffix(suffix);
		source.sendFeedback(() -> Text.translatable("commands.team.option.suffix.success", suffix), false);
		return 1;
	}
}
