/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ScoreboardObjectiveArgumentType
implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "*", "012");
    private static final DynamicCommandExceptionType UNKNOWN_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType(name -> Text.translatable("arguments.objective.notFound", name));
    private static final DynamicCommandExceptionType READONLY_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType(name -> Text.translatable("arguments.objective.readonly", name));

    public static ScoreboardObjectiveArgumentType scoreboardObjective() {
        return new ScoreboardObjectiveArgumentType();
    }

    public static ScoreboardObjective getObjective(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        String string = context.getArgument(name, String.class);
        ServerScoreboard scoreboard = context.getSource().getServer().getScoreboard();
        ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
        if (scoreboardObjective == null) {
            throw UNKNOWN_OBJECTIVE_EXCEPTION.create(string);
        }
        return scoreboardObjective;
    }

    public static ScoreboardObjective getWritableObjective(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        ScoreboardObjective scoreboardObjective = ScoreboardObjectiveArgumentType.getObjective(context, name);
        if (scoreboardObjective.getCriterion().isReadOnly()) {
            throw READONLY_OBJECTIVE_EXCEPTION.create(scoreboardObjective.getName());
        }
        return scoreboardObjective;
    }

    @Override
    public String parse(StringReader stringReader) throws CommandSyntaxException {
        return stringReader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        S object = context.getSource();
        if (object instanceof ServerCommandSource) {
            ServerCommandSource serverCommandSource = (ServerCommandSource)object;
            return CommandSource.suggestMatching(serverCommandSource.getServer().getScoreboard().getObjectiveNames(), builder);
        }
        if (object instanceof CommandSource) {
            CommandSource commandSource = (CommandSource)object;
            return commandSource.getCompletions(context);
        }
        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }
}

