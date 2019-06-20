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

public class class_2177 implements ArgumentType<class_124> {
	private static final Collection<String> field_9839 = Arrays.asList("red", "green");
	public static final DynamicCommandExceptionType field_9840 = new DynamicCommandExceptionType(object -> new class_2588("argument.color.invalid", object));

	private class_2177() {
	}

	public static class_2177 method_9276() {
		return new class_2177();
	}

	public static class_124 method_9277(CommandContext<class_2168> commandContext, String string) {
		return commandContext.getArgument(string, class_124.class);
	}

	public class_124 method_9279(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		class_124 lv = class_124.method_533(string);
		if (lv != null && !lv.method_542()) {
			return lv;
		} else {
			throw field_9840.create(string);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_2172.method_9265(class_124.method_540(true, false), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_9839;
	}
}
