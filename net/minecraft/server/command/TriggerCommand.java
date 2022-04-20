/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TriggerCommand {
    private static final SimpleCommandExceptionType FAILED_UNPRIMED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.trigger.failed.unprimed"));
    private static final SimpleCommandExceptionType FAILED_INVALID_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.trigger.failed.invalid"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("trigger").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("objective", ScoreboardObjectiveArgumentType.scoreboardObjective()).suggests((context, builder) -> TriggerCommand.suggestObjectives((ServerCommandSource)context.getSource(), builder)).executes(context -> TriggerCommand.executeSimple((ServerCommandSource)context.getSource(), TriggerCommand.getScore(((ServerCommandSource)context.getSource()).getPlayer(), ScoreboardObjectiveArgumentType.getObjective(context, "objective"))))).then(CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("value", IntegerArgumentType.integer()).executes(context -> TriggerCommand.executeAdd((ServerCommandSource)context.getSource(), TriggerCommand.getScore(((ServerCommandSource)context.getSource()).getPlayer(), ScoreboardObjectiveArgumentType.getObjective(context, "objective")), IntegerArgumentType.getInteger(context, "value")))))).then(CommandManager.literal("set").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("value", IntegerArgumentType.integer()).executes(context -> TriggerCommand.executeSet((ServerCommandSource)context.getSource(), TriggerCommand.getScore(((ServerCommandSource)context.getSource()).getPlayer(), ScoreboardObjectiveArgumentType.getObjective(context, "objective")), IntegerArgumentType.getInteger(context, "value")))))));
    }

    public static CompletableFuture<Suggestions> suggestObjectives(ServerCommandSource source, SuggestionsBuilder builder) {
        Entity entity = source.getEntity();
        ArrayList<String> list = Lists.newArrayList();
        if (entity != null) {
            ServerScoreboard scoreboard = source.getServer().getScoreboard();
            String string = entity.getEntityName();
            for (ScoreboardObjective scoreboardObjective : scoreboard.getObjectives()) {
                ScoreboardPlayerScore scoreboardPlayerScore;
                if (scoreboardObjective.getCriterion() != ScoreboardCriterion.TRIGGER || !scoreboard.playerHasObjective(string, scoreboardObjective) || (scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective)).isLocked()) continue;
                list.add(scoreboardObjective.getName());
            }
        }
        return CommandSource.suggestMatching(list, builder);
    }

    private static int executeAdd(ServerCommandSource source, ScoreboardPlayerScore score, int value) {
        score.incrementScore(value);
        source.sendFeedback(Text.translatable("commands.trigger.add.success", score.getObjective().toHoverableText(), value), true);
        return score.getScore();
    }

    private static int executeSet(ServerCommandSource source, ScoreboardPlayerScore score, int value) {
        score.setScore(value);
        source.sendFeedback(Text.translatable("commands.trigger.set.success", score.getObjective().toHoverableText(), value), true);
        return value;
    }

    private static int executeSimple(ServerCommandSource source, ScoreboardPlayerScore score) {
        score.incrementScore(1);
        source.sendFeedback(Text.translatable("commands.trigger.simple.success", score.getObjective().toHoverableText()), true);
        return score.getScore();
    }

    private static ScoreboardPlayerScore getScore(ServerPlayerEntity player, ScoreboardObjective objective) throws CommandSyntaxException {
        String string;
        if (objective.getCriterion() != ScoreboardCriterion.TRIGGER) {
            throw FAILED_INVALID_EXCEPTION.create();
        }
        Scoreboard scoreboard = player.getScoreboard();
        if (!scoreboard.playerHasObjective(string = player.getEntityName(), objective)) {
            throw FAILED_UNPRIMED_EXCEPTION.create();
        }
        ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, objective);
        if (scoreboardPlayerScore.isLocked()) {
            throw FAILED_UNPRIMED_EXCEPTION.create();
        }
        scoreboardPlayerScore.setLocked(true);
        return scoreboardPlayerScore;
    }
}

