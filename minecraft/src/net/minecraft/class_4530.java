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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.test.TestFunction;
import net.minecraft.text.LiteralText;

public class class_4530 implements ArgumentType<TestFunction> {
	private static final Collection<String> field_20589 = Arrays.asList("techtests.piston", "techtests");

	public TestFunction method_22302(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		Optional<TestFunction> optional = class_4519.method_22199(string);
		if (optional.isPresent()) {
			return (TestFunction)optional.get();
		} else {
			Message message = new LiteralText("No such test: " + string);
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
	}

	public static TestFunction method_22303(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, TestFunction.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		Stream<String> stream = class_4519.method_22191().stream().map(TestFunction::method_22296);
		return CommandSource.suggestMatching(stream, suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_20589;
	}
}
