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

public class class_2194 implements ArgumentType<class_1887> {
	private static final Collection<String> field_9871 = Arrays.asList("unbreaking", "silk_touch");
	public static final DynamicCommandExceptionType field_9872 = new DynamicCommandExceptionType(object -> new class_2588("enchantment.unknown", object));

	public static class_2194 method_9336() {
		return new class_2194();
	}

	public static class_1887 method_9334(CommandContext<class_2168> commandContext, String string) {
		return commandContext.getArgument(string, class_1887.class);
	}

	public class_1887 method_9335(StringReader stringReader) throws CommandSyntaxException {
		class_2960 lv = class_2960.method_12835(stringReader);
		return (class_1887)class_2378.field_11160.method_17966(lv).orElseThrow(() -> field_9872.create(lv));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_2172.method_9270(class_2378.field_11160.method_10235(), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_9871;
	}
}
