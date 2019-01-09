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

public class class_2274 implements ArgumentType<class_2267> {
	private static final Collection<String> field_10742 = Arrays.asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
	public static final SimpleCommandExceptionType field_10743 = new SimpleCommandExceptionType(new class_2588("argument.pos2d.incomplete"));
	private final boolean field_10744;

	public class_2274(boolean bl) {
		this.field_10744 = bl;
	}

	public static class_2274 method_9723() {
		return new class_2274(true);
	}

	public static class_241 method_9724(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		class_243 lv = commandContext.<class_2267>getArgument(string, class_2267.class).method_9708(commandContext.getSource());
		return new class_241((float)lv.field_1352, (float)lv.field_1350);
	}

	public class_2267 method_9725(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		if (!stringReader.canRead()) {
			throw field_10743.createWithContext(stringReader);
		} else {
			class_2278 lv = class_2278.method_9743(stringReader, this.field_10744);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				class_2278 lv2 = class_2278.method_9743(stringReader, this.field_10744);
				return new class_2280(lv, new class_2278(true, 0.0), lv2);
			} else {
				stringReader.setCursor(i);
				throw field_10743.createWithContext(stringReader);
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
				collection = ((class_2172)commandContext.getSource()).method_9274(true);
			}

			return class_2172.method_9252(string, collection, suggestionsBuilder, class_2170.method_9238(this::method_9725));
		}
	}

	@Override
	public Collection<String> getExamples() {
		return field_10742;
	}
}
