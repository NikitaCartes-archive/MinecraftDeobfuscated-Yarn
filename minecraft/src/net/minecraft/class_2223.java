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

public class class_2223 implements ArgumentType<class_2394> {
	private static final Collection<String> field_9935 = Arrays.asList("foo", "foo:bar", "particle with options");
	public static final DynamicCommandExceptionType field_9936 = new DynamicCommandExceptionType(object -> new class_2588("particle.notFound", object));

	public static class_2223 method_9417() {
		return new class_2223();
	}

	public static class_2394 method_9421(CommandContext<class_2168> commandContext, String string) {
		return commandContext.getArgument(string, class_2394.class);
	}

	public class_2394 method_9416(StringReader stringReader) throws CommandSyntaxException {
		return method_9418(stringReader);
	}

	@Override
	public Collection<String> getExamples() {
		return field_9935;
	}

	public static class_2394 method_9418(StringReader stringReader) throws CommandSyntaxException {
		class_2960 lv = class_2960.method_12835(stringReader);
		class_2396<?> lv2 = (class_2396<?>)class_2378.field_11141.method_17966(lv).orElseThrow(() -> field_9936.create(lv));
		return method_9420(stringReader, (class_2396<class_2394>)lv2);
	}

	private static <T extends class_2394> T method_9420(StringReader stringReader, class_2396<T> arg) throws CommandSyntaxException {
		return arg.method_10298().method_10296(arg, stringReader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_2172.method_9270(class_2378.field_11141.method_10235(), suggestionsBuilder);
	}
}
