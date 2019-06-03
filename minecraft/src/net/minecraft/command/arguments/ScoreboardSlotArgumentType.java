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
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class ScoreboardSlotArgumentType implements ArgumentType<Integer> {
	private static final Collection<String> EXAMPLES = Arrays.asList("sidebar", "foo.bar");
	public static final DynamicCommandExceptionType INVALID_SLOT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.scoreboardDisplaySlot.invalid", object)
	);

	private ScoreboardSlotArgumentType() {
	}

	public static ScoreboardSlotArgumentType create() {
		return new ScoreboardSlotArgumentType();
	}

	public static int getScorebordSlot(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.<Integer>getArgument(string, Integer.class);
	}

	public Integer method_9466(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		int i = Scoreboard.getDisplaySlotId(string);
		if (i == -1) {
			throw INVALID_SLOT_EXCEPTION.create(string);
		} else {
			return i;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestMatching(Scoreboard.getDisplaySlotNames(), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
