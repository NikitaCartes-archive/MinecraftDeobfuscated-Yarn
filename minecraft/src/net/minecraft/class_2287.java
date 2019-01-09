package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class class_2287 implements ArgumentType<class_2290> {
	private static final Collection<String> field_10790 = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");

	public static class_2287 method_9776() {
		return new class_2287();
	}

	public class_2290 method_9778(StringReader stringReader) throws CommandSyntaxException {
		class_2291 lv = new class_2291(stringReader, false).method_9789();
		return new class_2290(lv.method_9786(), lv.method_9797());
	}

	public static <S> class_2290 method_9777(CommandContext<S> commandContext, String string) {
		return commandContext.getArgument(string, class_2290.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		class_2291 lv = new class_2291(stringReader, false);

		try {
			lv.method_9789();
		} catch (CommandSyntaxException var6) {
		}

		return lv.method_9793(suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_10790;
	}
}
