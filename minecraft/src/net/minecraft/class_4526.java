package net.minecraft;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class class_4526 implements ArgumentType<String> {
	private static final Collection<String> field_20580 = Arrays.asList("techtests", "mobtests");

	public String method_22261(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		if (class_4519.method_22196(string)) {
			return string;
		} else {
			Message message = new LiteralText("No such test class: " + string);
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
	}

	public static String method_22262(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, String.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestMatching(class_4519.method_22195().stream(), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_20580;
	}
}
