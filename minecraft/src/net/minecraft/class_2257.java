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

public class class_2257 implements ArgumentType<class_2247> {
	private static final Collection<String> field_10679 = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");

	public static class_2257 method_9653() {
		return new class_2257();
	}

	public class_2247 method_9654(StringReader stringReader) throws CommandSyntaxException {
		class_2259 lv = new class_2259(stringReader, false).method_9678(true);
		return new class_2247(lv.method_9669(), lv.method_9692().keySet(), lv.method_9694());
	}

	public static class_2247 method_9655(CommandContext<class_2168> commandContext, String string) {
		return commandContext.getArgument(string, class_2247.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		class_2259 lv = new class_2259(stringReader, false);

		try {
			lv.method_9678(true);
		} catch (CommandSyntaxException var6) {
		}

		return lv.method_9666(suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_10679;
	}
}
