/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
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
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class ScoreboardCommand {
    private static final SimpleCommandExceptionType OBJECTIVES_ADD_DUPLICATE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.scoreboard.objectives.add.duplicate", new Object[0]));
    private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADYEMPTY_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.scoreboard.objectives.display.alreadyEmpty", new Object[0]));
    private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADYSET_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.scoreboard.objectives.display.alreadySet", new Object[0]));
    private static final SimpleCommandExceptionType PLAYERS_ENABLE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.scoreboard.players.enable.failed", new Object[0]));
    private static final SimpleCommandExceptionType PLAYERS_ENABLE_INVALID_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.scoreboard.players.enable.invalid", new Object[0]));
    private static final Dynamic2CommandExceptionType PLAYERS_GET_NULL_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.scoreboard.players.get.null", object, object2));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("scoreboard").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("objectives").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("list").executes(commandContext -> ScoreboardCommand.executeListObjectives((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("objective", StringArgumentType.word()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("criteria", ObjectiveCriteriaArgumentType.create()).executes(commandContext -> ScoreboardCommand.executeAddObjective((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "objective"), ObjectiveCriteriaArgumentType.getCriteria(commandContext, "criteria"), new LiteralText(StringArgumentType.getString(commandContext, "objective"))))).then(CommandManager.argument("displayName", TextArgumentType.create()).executes(commandContext -> ScoreboardCommand.executeAddObjective((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "objective"), ObjectiveCriteriaArgumentType.getCriteria(commandContext, "criteria"), TextArgumentType.getTextArgument(commandContext, "displayName")))))))).then(CommandManager.literal("modify").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("objective", ObjectiveArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("displayname").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("displayName", TextArgumentType.create()).executes(commandContext -> ScoreboardCommand.executeModifyObjective((ServerCommandSource)commandContext.getSource(), ObjectiveArgumentType.getObjective(commandContext, "objective"), TextArgumentType.getTextArgument(commandContext, "displayName")))))).then(ScoreboardCommand.makeRenderTypeArguments())))).then(CommandManager.literal("remove").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("objective", ObjectiveArgumentType.create()).executes(commandContext -> ScoreboardCommand.executeRemoveObjective((ServerCommandSource)commandContext.getSource(), ObjectiveArgumentType.getObjective(commandContext, "objective")))))).then(CommandManager.literal("setdisplay").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("slot", ScoreboardSlotArgumentType.create()).executes(commandContext -> ScoreboardCommand.executeClearDisplay((ServerCommandSource)commandContext.getSource(), ScoreboardSlotArgumentType.getScorebordSlot(commandContext, "slot")))).then(CommandManager.argument("objective", ObjectiveArgumentType.create()).executes(commandContext -> ScoreboardCommand.executeSetDisplay((ServerCommandSource)commandContext.getSource(), ScoreboardSlotArgumentType.getScorebordSlot(commandContext, "slot"), ObjectiveArgumentType.getObjective(commandContext, "objective")))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("players").then((ArgumentBuilder<ServerCommandSource, ?>)((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> ScoreboardCommand.executeListPlayers((ServerCommandSource)commandContext.getSource()))).then(CommandManager.argument("target", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).executes(commandContext -> ScoreboardCommand.executeListScores((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreHolder(commandContext, "target")))))).then(CommandManager.literal("set").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("objective", ObjectiveArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("score", IntegerArgumentType.integer()).executes(commandContext -> ScoreboardCommand.executeSet((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"), ObjectiveArgumentType.getWritableObjective(commandContext, "objective"), IntegerArgumentType.getInteger(commandContext, "score")))))))).then(CommandManager.literal("get").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("target", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("objective", ObjectiveArgumentType.create()).executes(commandContext -> ScoreboardCommand.executeGet((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreHolder(commandContext, "target"), ObjectiveArgumentType.getObjective(commandContext, "objective"))))))).then(CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("objective", ObjectiveArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("score", IntegerArgumentType.integer(0)).executes(commandContext -> ScoreboardCommand.executeAdd((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"), ObjectiveArgumentType.getWritableObjective(commandContext, "objective"), IntegerArgumentType.getInteger(commandContext, "score")))))))).then(CommandManager.literal("remove").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("objective", ObjectiveArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("score", IntegerArgumentType.integer(0)).executes(commandContext -> ScoreboardCommand.executeRemove((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"), ObjectiveArgumentType.getWritableObjective(commandContext, "objective"), IntegerArgumentType.getInteger(commandContext, "score")))))))).then(CommandManager.literal("reset").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).executes(commandContext -> ScoreboardCommand.executeReset((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets")))).then(CommandManager.argument("objective", ObjectiveArgumentType.create()).executes(commandContext -> ScoreboardCommand.executeReset((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"), ObjectiveArgumentType.getObjective(commandContext, "objective"))))))).then(CommandManager.literal("enable").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("objective", ObjectiveArgumentType.create()).suggests((commandContext, suggestionsBuilder) -> ScoreboardCommand.suggestDisabled((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"), suggestionsBuilder)).executes(commandContext -> ScoreboardCommand.executeEnable((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"), ObjectiveArgumentType.getObjective(commandContext, "objective"))))))).then(CommandManager.literal("operation").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targetObjective", ObjectiveArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("operation", OperationArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("sourceObjective", ObjectiveArgumentType.create()).executes(commandContext -> ScoreboardCommand.executeOperation((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"), ObjectiveArgumentType.getWritableObjective(commandContext, "targetObjective"), OperationArgumentType.getOperation(commandContext, "operation"), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "source"), ObjectiveArgumentType.getObjective(commandContext, "sourceObjective")))))))))));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> makeRenderTypeArguments() {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("rendertype");
        for (ScoreboardCriterion.RenderType renderType : ScoreboardCriterion.RenderType.values()) {
            literalArgumentBuilder.then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal(renderType.getName()).executes(commandContext -> ScoreboardCommand.executeModifyRenderType((ServerCommandSource)commandContext.getSource(), ObjectiveArgumentType.getObjective(commandContext, "objective"), renderType)));
        }
        return literalArgumentBuilder;
    }

    private static CompletableFuture<Suggestions> suggestDisabled(ServerCommandSource serverCommandSource, Collection<String> collection, SuggestionsBuilder suggestionsBuilder) {
        ArrayList<String> list = Lists.newArrayList();
        ServerScoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
        for (ScoreboardObjective scoreboardObjective : scoreboard.getObjectives()) {
            if (scoreboardObjective.getCriterion() != ScoreboardCriterion.TRIGGER) continue;
            boolean bl = false;
            for (String string : collection) {
                if (scoreboard.playerHasObjective(string, scoreboardObjective) && !scoreboard.getPlayerScore(string, scoreboardObjective).isLocked()) continue;
                bl = true;
                break;
            }
            if (!bl) continue;
            list.add(scoreboardObjective.getName());
        }
        return CommandSource.suggestMatching(list, suggestionsBuilder);
    }

    private static int executeGet(ServerCommandSource serverCommandSource, String string, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
        ServerScoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
        if (!scoreboard.playerHasObjective(string, scoreboardObjective)) {
            throw PLAYERS_GET_NULL_EXCEPTION.create(scoreboardObjective.getName(), string);
        }
        ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
        serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.get.success", string, scoreboardPlayerScore.getScore(), scoreboardObjective.toHoverableText()), false);
        return scoreboardPlayerScore.getScore();
    }

    private static int executeOperation(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective, OperationArgumentType.Operation operation, Collection<String> collection2, ScoreboardObjective scoreboardObjective2) throws CommandSyntaxException {
        ServerScoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
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
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.operation.success.single", scoreboardObjective.toHoverableText(), collection.iterator().next(), i), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.operation.success.multiple", scoreboardObjective.toHoverableText(), collection.size()), true);
        }
        return i;
    }

    private static int executeEnable(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
        if (scoreboardObjective.getCriterion() != ScoreboardCriterion.TRIGGER) {
            throw PLAYERS_ENABLE_INVALID_EXCEPTION.create();
        }
        ServerScoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
        int i = 0;
        for (String string : collection) {
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
            if (!scoreboardPlayerScore.isLocked()) continue;
            scoreboardPlayerScore.setLocked(false);
            ++i;
        }
        if (i == 0) {
            throw PLAYERS_ENABLE_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.enable.success.single", scoreboardObjective.toHoverableText(), collection.iterator().next()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.enable.success.multiple", scoreboardObjective.toHoverableText(), collection.size()), true);
        }
        return i;
    }

    private static int executeReset(ServerCommandSource serverCommandSource, Collection<String> collection) {
        ServerScoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
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
        ServerScoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
        for (String string : collection) {
            scoreboard.resetPlayerScore(string, scoreboardObjective);
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.reset.specific.single", scoreboardObjective.toHoverableText(), collection.iterator().next()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.reset.specific.multiple", scoreboardObjective.toHoverableText(), collection.size()), true);
        }
        return collection.size();
    }

    private static int executeSet(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective, int i) {
        ServerScoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
        for (String string : collection) {
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
            scoreboardPlayerScore.setScore(i);
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.set.success.single", scoreboardObjective.toHoverableText(), collection.iterator().next(), i), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.set.success.multiple", scoreboardObjective.toHoverableText(), collection.size(), i), true);
        }
        return i * collection.size();
    }

    private static int executeAdd(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective, int i) {
        ServerScoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
        int j = 0;
        for (String string : collection) {
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
            scoreboardPlayerScore.setScore(scoreboardPlayerScore.getScore() + i);
            j += scoreboardPlayerScore.getScore();
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.add.success.single", i, scoreboardObjective.toHoverableText(), collection.iterator().next(), j), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.add.success.multiple", i, scoreboardObjective.toHoverableText(), collection.size()), true);
        }
        return j;
    }

    private static int executeRemove(ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective, int i) {
        ServerScoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
        int j = 0;
        for (String string : collection) {
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
            scoreboardPlayerScore.setScore(scoreboardPlayerScore.getScore() - i);
            j += scoreboardPlayerScore.getScore();
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.remove.success.single", i, scoreboardObjective.toHoverableText(), collection.iterator().next(), j), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.remove.success.multiple", i, scoreboardObjective.toHoverableText(), collection.size()), true);
        }
        return j;
    }

    private static int executeListPlayers(ServerCommandSource serverCommandSource) {
        Collection<String> collection = serverCommandSource.getMinecraftServer().getScoreboard().getKnownPlayers();
        if (collection.isEmpty()) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.list.empty", new Object[0]), false);
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
            for (Map.Entry<ScoreboardObjective, ScoreboardPlayerScore> entry : map.entrySet()) {
                serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.players.list.entity.entry", entry.getKey().toHoverableText(), entry.getValue().getScore()), false);
            }
        }
        return map.size();
    }

    private static int executeClearDisplay(ServerCommandSource serverCommandSource, int i) throws CommandSyntaxException {
        ServerScoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
        if (scoreboard.getObjectiveForSlot(i) == null) {
            throw OBJECTIVES_DISPLAY_ALREADYEMPTY_EXCEPTION.create();
        }
        ((Scoreboard)scoreboard).setObjectiveSlot(i, null);
        serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.objectives.display.cleared", Scoreboard.getDisplaySlotNames()[i]), true);
        return 0;
    }

    private static int executeSetDisplay(ServerCommandSource serverCommandSource, int i, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
        ServerScoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
        if (scoreboard.getObjectiveForSlot(i) == scoreboardObjective) {
            throw OBJECTIVES_DISPLAY_ALREADYSET_EXCEPTION.create();
        }
        ((Scoreboard)scoreboard).setObjectiveSlot(i, scoreboardObjective);
        serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.objectives.display.set", Scoreboard.getDisplaySlotNames()[i], scoreboardObjective.getDisplayName()), true);
        return 0;
    }

    private static int executeModifyObjective(ServerCommandSource serverCommandSource, ScoreboardObjective scoreboardObjective, Text text) {
        if (!scoreboardObjective.getDisplayName().equals(text)) {
            scoreboardObjective.setDisplayName(text);
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.objectives.modify.displayname", scoreboardObjective.getName(), scoreboardObjective.toHoverableText()), true);
        }
        return 0;
    }

    private static int executeModifyRenderType(ServerCommandSource serverCommandSource, ScoreboardObjective scoreboardObjective, ScoreboardCriterion.RenderType renderType) {
        if (scoreboardObjective.getRenderType() != renderType) {
            scoreboardObjective.setRenderType(renderType);
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.objectives.modify.rendertype", scoreboardObjective.toHoverableText()), true);
        }
        return 0;
    }

    private static int executeRemoveObjective(ServerCommandSource serverCommandSource, ScoreboardObjective scoreboardObjective) {
        ServerScoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
        scoreboard.removeObjective(scoreboardObjective);
        serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.objectives.remove.success", scoreboardObjective.toHoverableText()), true);
        return scoreboard.getObjectives().size();
    }

    private static int executeAddObjective(ServerCommandSource serverCommandSource, String string, ScoreboardCriterion scoreboardCriterion, Text text) throws CommandSyntaxException {
        ServerScoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
        if (scoreboard.getNullableObjective(string) != null) {
            throw OBJECTIVES_ADD_DUPLICATE_EXCEPTION.create();
        }
        if (string.length() > 16) {
            throw ObjectiveArgumentType.LONG_NAME_EXCEPTION.create(16);
        }
        scoreboard.addObjective(string, scoreboardCriterion, text, scoreboardCriterion.getCriterionType());
        ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
        serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.objectives.add.success", scoreboardObjective.toHoverableText()), true);
        return scoreboard.getObjectives().size();
    }

    private static int executeListObjectives(ServerCommandSource serverCommandSource) {
        Collection<ScoreboardObjective> collection = serverCommandSource.getMinecraftServer().getScoreboard().getObjectives();
        if (collection.isEmpty()) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.objectives.list.empty", new Object[0]), false);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.scoreboard.objectives.list.success", collection.size(), Texts.join(collection, ScoreboardObjective::toHoverableText)), false);
        }
        return collection.size();
    }
}

