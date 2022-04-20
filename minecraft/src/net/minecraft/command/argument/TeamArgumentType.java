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
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class TeamArgumentType implements ArgumentType<String> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "123");
	private static final DynamicCommandExceptionType UNKNOWN_TEAM_EXCEPTION = new DynamicCommandExceptionType(name -> Text.translatable("team.notFound", name));

	public static TeamArgumentType team() {
		return new TeamArgumentType();
	}

	public static Team getTeam(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		String string = context.getArgument(name, String.class);
		Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
		Team team = scoreboard.getTeam(string);
		if (team == null) {
			throw UNKNOWN_TEAM_EXCEPTION.create(string);
		} else {
			return team;
		}
	}

	public String parse(StringReader stringReader) throws CommandSyntaxException {
		return stringReader.readUnquotedString();
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return context.getSource() instanceof CommandSource
			? CommandSource.suggestMatching(((CommandSource)context.getSource()).getTeamNames(), builder)
			: Suggestions.empty();
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
