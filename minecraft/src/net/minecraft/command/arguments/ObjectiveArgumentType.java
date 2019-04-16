package net.minecraft.command.arguments;

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
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;

public class ObjectiveArgumentType implements ArgumentType<String> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "*", "012");
	private static final DynamicCommandExceptionType UNKNOWN_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("arguments.objective.notFound", object)
	);
	private static final DynamicCommandExceptionType READONLY_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("arguments.objective.readonly", object)
	);
	public static final DynamicCommandExceptionType LONG_NAME_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.scoreboard.objectives.add.longName", object)
	);

	public static ObjectiveArgumentType create() {
		return new ObjectiveArgumentType();
	}

	public static ScoreboardObjective getObjective(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		String string2 = commandContext.getArgument(string, String.class);
		Scoreboard scoreboard = commandContext.getSource().getMinecraftServer().getScoreboard();
		ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string2);
		if (scoreboardObjective == null) {
			throw UNKNOWN_OBJECTIVE_EXCEPTION.create(string2);
		} else {
			return scoreboardObjective;
		}
	}

	public static ScoreboardObjective getWritableObjective(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		ScoreboardObjective scoreboardObjective = getObjective(commandContext, string);
		if (scoreboardObjective.getCriterion().isReadOnly()) {
			throw READONLY_OBJECTIVE_EXCEPTION.create(scoreboardObjective.getName());
		} else {
			return scoreboardObjective;
		}
	}

	public String method_9396(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		if (string.length() > 16) {
			throw LONG_NAME_EXCEPTION.create(16);
		} else {
			return string;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (commandContext.getSource() instanceof ServerCommandSource) {
			return CommandSource.suggestMatching(
				((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getScoreboard().getObjectiveNames(), suggestionsBuilder
			);
		} else if (commandContext.getSource() instanceof CommandSource) {
			CommandSource commandSource = (CommandSource)commandContext.getSource();
			return commandSource.getCompletions(commandContext, suggestionsBuilder);
		} else {
			return Suggestions.empty();
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
