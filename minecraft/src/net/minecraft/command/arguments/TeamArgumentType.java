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
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class TeamArgumentType implements ArgumentType<String> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "123");
	private static final DynamicCommandExceptionType UNKNOWN_TEAM_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("team.notFound", object)
	);

	public static TeamArgumentType team() {
		return new TeamArgumentType();
	}

	public static Team getTeam(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		String string2 = commandContext.getArgument(string, String.class);
		Scoreboard scoreboard = commandContext.getSource().getMinecraftServer().getScoreboard();
		Team team = scoreboard.getTeam(string2);
		if (team == null) {
			throw UNKNOWN_TEAM_EXCEPTION.create(string2);
		} else {
			return team;
		}
	}

	public String method_9483(StringReader stringReader) throws CommandSyntaxException {
		return stringReader.readUnquotedString();
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return commandContext.getSource() instanceof CommandSource
			? CommandSource.suggestMatching(((CommandSource)commandContext.getSource()).getTeamNames(), suggestionsBuilder)
			: Suggestions.empty();
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
