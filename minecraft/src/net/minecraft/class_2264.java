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

public class class_2264 implements ArgumentType<class_2267> {
	private static final Collection<String> field_10705 = Arrays.asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
	public static final SimpleCommandExceptionType field_10706 = new SimpleCommandExceptionType(new class_2588("argument.pos2d.incomplete"));

	public static class_2264 method_9701() {
		return new class_2264();
	}

	public static class_2265 method_9702(CommandContext<class_2168> commandContext, String string) {
		class_2338 lv = commandContext.<class_2267>getArgument(string, class_2267.class).method_9704(commandContext.getSource());
		return new class_2265(lv.method_10263(), lv.method_10260());
	}

	public class_2267 method_9703(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		if (!stringReader.canRead()) {
			throw field_10706.createWithContext(stringReader);
		} else {
			class_2278 lv = class_2278.method_9739(stringReader);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				class_2278 lv2 = class_2278.method_9739(stringReader);
				return new class_2280(lv, new class_2278(true, 0.0), lv2);
			} else {
				stringReader.setCursor(i);
				throw field_10706.createWithContext(stringReader);
			}
		}
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

			return class_2172.method_9252(string, collection, suggestionsBuilder, class_2170.method_9238(this::method_9703));
		}
	}

	@Override
	public Collection<String> getExamples() {
		return field_10705;
	}
}
