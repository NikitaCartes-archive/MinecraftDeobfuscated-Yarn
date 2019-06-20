package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class class_2262 implements ArgumentType<class_2267> {
	private static final Collection<String> field_10702 = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
	public static final SimpleCommandExceptionType field_10703 = new SimpleCommandExceptionType(new class_2588("argument.pos.unloaded"));
	public static final SimpleCommandExceptionType field_10704 = new SimpleCommandExceptionType(new class_2588("argument.pos.outofworld"));

	public static class_2262 method_9698() {
		return new class_2262();
	}

	public static class_2338 method_9696(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		class_2338 lv = commandContext.<class_2267>getArgument(string, class_2267.class).method_9704(commandContext.getSource());
		if (!commandContext.getSource().method_9225().method_8591(lv)) {
			throw field_10703.create();
		} else {
			commandContext.getSource().method_9225();
			if (!class_3218.method_8558(lv)) {
				throw field_10704.create();
			} else {
				return lv;
			}
		}
	}

	public static class_2338 method_9697(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<class_2267>getArgument(string, class_2267.class).method_9704(commandContext.getSource());
	}

	public class_2267 method_9699(StringReader stringReader) throws CommandSyntaxException {
		return (class_2267)(stringReader.canRead() && stringReader.peek() == '^' ? class_2268.method_9711(stringReader) : class_2280.method_9749(stringReader));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (!(commandContext.getSource() instanceof class_2172)) {
			return Suggestions.empty();
		} else {
			String string = suggestionsBuilder.getRemaining();
			Collection<class_2172.class_2173> collection;
			if (!string.isEmpty() && string.charAt(0) == '^') {
				collection = Collections.singleton(class_2172.class_2173.field_9834);
			} else {
				collection = ((class_2172)commandContext.getSource()).method_17771();
			}

			return class_2172.method_9260(string, collection, suggestionsBuilder, class_2170.method_9238(this::method_9699));
		}
	}

	@Override
	public Collection<String> getExamples() {
		return field_10702;
	}
}
