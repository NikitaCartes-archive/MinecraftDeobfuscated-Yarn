package net.minecraft;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class class_2181 implements ArgumentType<class_2874> {
	private static final Collection<String> field_9844 = (Collection<String>)Stream.of(class_2874.field_13072, class_2874.field_13076)
		.map(arg -> class_2874.method_12485(arg).toString())
		.collect(Collectors.toList());
	public static final DynamicCommandExceptionType field_9845 = new DynamicCommandExceptionType(object -> new class_2588("argument.dimension.invalid", object));

	public <S> class_2874 method_9287(StringReader stringReader) throws CommandSyntaxException {
		class_2960 lv = class_2960.method_12835(stringReader);
		class_2874 lv2 = class_2874.method_12483(lv);
		if (lv2 == null) {
			throw field_9845.create(lv);
		} else {
			return lv2;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_2172.method_9257(Streams.stream(class_2874.method_12482()).map(class_2874::method_12485), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_9844;
	}

	public static class_2181 method_9288() {
		return new class_2181();
	}

	public static class_2874 method_9289(CommandContext<class_2168> commandContext, String string) {
		return commandContext.getArgument(string, class_2874.class);
	}
}
