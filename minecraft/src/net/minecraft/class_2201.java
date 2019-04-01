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

public class class_2201 implements ArgumentType<class_1291> {
	private static final Collection<String> field_9895 = Arrays.asList("spooky", "effect");
	public static final DynamicCommandExceptionType field_9896 = new DynamicCommandExceptionType(object -> new class_2588("effect.effectNotFound", object));

	public static class_2201 method_9350() {
		return new class_2201();
	}

	public static class_1291 method_9347(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.getArgument(string, class_1291.class);
	}

	public class_1291 method_9348(StringReader stringReader) throws CommandSyntaxException {
		class_2960 lv = class_2960.method_12835(stringReader);
		return (class_1291)class_2378.field_11159.method_17966(lv).orElseThrow(() -> field_9896.create(lv));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_2172.method_9270(class_2378.field_11159.method_10235(), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_9895;
	}
}
