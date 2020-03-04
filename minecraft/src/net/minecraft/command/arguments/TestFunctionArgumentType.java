package net.minecraft.command.arguments;

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
import net.minecraft.test.TestFunctions;
import net.minecraft.text.LiteralText;

public class TestFunctionArgumentType implements ArgumentType<TestFunction> {
	private static final Collection<String> EXAMPLES = Arrays.asList("techtests.piston", "techtests");

	public TestFunction parse(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		Optional<TestFunction> optional = TestFunctions.getTestFunction(string);
		if (optional.isPresent()) {
			return (TestFunction)optional.get();
		} else {
			Message message = new LiteralText("No such test: " + string);
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
	}

	public static TestFunctionArgumentType testFunction() {
		return new TestFunctionArgumentType();
	}

	public static TestFunction getFunction(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, TestFunction.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		Stream<String> stream = TestFunctions.getTestFunctions().stream().map(TestFunction::getStructurePath);
		return CommandSource.suggestMatching(stream, suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
