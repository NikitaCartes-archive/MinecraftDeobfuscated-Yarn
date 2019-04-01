package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class class_2321 {
	private static final Map<class_2960, SuggestionProvider<class_2172>> field_10931 = Maps.<class_2960, SuggestionProvider<class_2172>>newHashMap();
	private static final class_2960 field_10930 = new class_2960("ask_server");
	public static final SuggestionProvider<class_2172> field_10933 = method_10022(
		field_10930, (commandContext, suggestionsBuilder) -> commandContext.getSource().method_9261(commandContext, suggestionsBuilder)
	);
	public static final SuggestionProvider<class_2168> field_10932 = method_10022(
		new class_2960("all_recipes"), (commandContext, suggestionsBuilder) -> class_2172.method_9257(commandContext.getSource().method_9273(), suggestionsBuilder)
	);
	public static final SuggestionProvider<class_2168> field_10934 = method_10022(
		new class_2960("available_sounds"),
		(commandContext, suggestionsBuilder) -> class_2172.method_9270(commandContext.getSource().method_9254(), suggestionsBuilder)
	);
	public static final SuggestionProvider<class_2168> field_10935 = method_10022(
		new class_2960("summonable_entities"),
		(commandContext, suggestionsBuilder) -> class_2172.method_9271(
				class_2378.field_11145.method_10220().filter(class_1299::method_5896),
				suggestionsBuilder,
				class_1299::method_5890,
				arg -> new class_2588(class_156.method_646("entity", class_1299.method_5890(arg)))
			)
	);

	public static <S extends class_2172> SuggestionProvider<S> method_10022(class_2960 arg, SuggestionProvider<class_2172> suggestionProvider) {
		if (field_10931.containsKey(arg)) {
			throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + arg);
		} else {
			field_10931.put(arg, suggestionProvider);
			return new class_2321.class_2322(arg, suggestionProvider);
		}
	}

	public static SuggestionProvider<class_2172> method_10024(class_2960 arg) {
		return (SuggestionProvider<class_2172>)field_10931.getOrDefault(arg, field_10933);
	}

	public static class_2960 method_10027(SuggestionProvider<class_2172> suggestionProvider) {
		return suggestionProvider instanceof class_2321.class_2322 ? ((class_2321.class_2322)suggestionProvider).field_10936 : field_10930;
	}

	public static SuggestionProvider<class_2172> method_10026(SuggestionProvider<class_2172> suggestionProvider) {
		return suggestionProvider instanceof class_2321.class_2322 ? suggestionProvider : field_10933;
	}

	public static class class_2322 implements SuggestionProvider<class_2172> {
		private final SuggestionProvider<class_2172> field_10937;
		private final class_2960 field_10936;

		public class_2322(class_2960 arg, SuggestionProvider<class_2172> suggestionProvider) {
			this.field_10937 = suggestionProvider;
			this.field_10936 = arg;
		}

		@Override
		public CompletableFuture<Suggestions> getSuggestions(CommandContext<class_2172> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
			return this.field_10937.getSuggestions(commandContext, suggestionsBuilder);
		}
	}
}
