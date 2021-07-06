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
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class ScoreboardObjectiveArgumentType implements ArgumentType<String> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "*", "012");
	private static final DynamicCommandExceptionType UNKNOWN_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType(
		name -> new TranslatableText("arguments.objective.notFound", name)
	);
	private static final DynamicCommandExceptionType READONLY_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType(
		name -> new TranslatableText("arguments.objective.readonly", name)
	);
	public static final DynamicCommandExceptionType LONG_NAME_EXCEPTION = new DynamicCommandExceptionType(
		maxLength -> new TranslatableText("commands.scoreboard.objectives.add.longName", maxLength)
	);

	public static ScoreboardObjectiveArgumentType scoreboardObjective() {
		return new ScoreboardObjectiveArgumentType();
	}

	public static ScoreboardObjective getObjective(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		String string = context.getArgument(name, String.class);
		Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
		ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
		if (scoreboardObjective == null) {
			throw UNKNOWN_OBJECTIVE_EXCEPTION.create(string);
		} else {
			return scoreboardObjective;
		}
	}

	public static ScoreboardObjective getWritableObjective(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		ScoreboardObjective scoreboardObjective = getObjective(context, name);
		if (scoreboardObjective.getCriterion().isReadOnly()) {
			throw READONLY_OBJECTIVE_EXCEPTION.create(scoreboardObjective.getName());
		} else {
			return scoreboardObjective;
		}
	}

	public String parse(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		if (string.length() > 16) {
			throw LONG_NAME_EXCEPTION.create(16);
		} else {
			return string;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		if (context.getSource() instanceof ServerCommandSource) {
			return CommandSource.suggestMatching(((ServerCommandSource)context.getSource()).getServer().getScoreboard().getObjectiveNames(), builder);
		} else if (context.getSource() instanceof CommandSource) {
			CommandSource commandSource = (CommandSource)context.getSource();
			return commandSource.getCompletions(context, builder);
		} else {
			return Suggestions.empty();
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
