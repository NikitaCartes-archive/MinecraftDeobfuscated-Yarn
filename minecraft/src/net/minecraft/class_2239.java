package net.minecraft;

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

public class class_2239 implements ArgumentType<Integer> {
	private static final Collection<String> field_9953 = Arrays.asList("sidebar", "foo.bar");
	public static final DynamicCommandExceptionType field_9954 = new DynamicCommandExceptionType(
		object -> new class_2588("argument.scoreboardDisplaySlot.invalid", object)
	);

	private class_2239() {
	}

	public static class_2239 method_9468() {
		return new class_2239();
	}

	public static int method_9465(CommandContext<class_2168> commandContext, String string) {
		return commandContext.<Integer>getArgument(string, Integer.class);
	}

	public Integer method_9466(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		int i = class_269.method_1192(string);
		if (i == -1) {
			throw field_9954.create(string);
		} else {
			return i;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_2172.method_9253(class_269.method_1186(), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_9953;
	}
}
