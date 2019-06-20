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

public class class_2277 implements ArgumentType<class_2267> {
	private static final Collection<String> field_10754 = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
	public static final SimpleCommandExceptionType field_10755 = new SimpleCommandExceptionType(new class_2588("argument.pos3d.incomplete"));
	public static final SimpleCommandExceptionType field_10757 = new SimpleCommandExceptionType(new class_2588("argument.pos.mixed"));
	private final boolean field_10756;

	public class_2277(boolean bl) {
		this.field_10756 = bl;
	}

	public static class_2277 method_9737() {
		return new class_2277(true);
	}

	public static class_2277 method_9735(boolean bl) {
		return new class_2277(bl);
	}

	public static class_243 method_9736(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<class_2267>getArgument(string, class_2267.class).method_9708(commandContext.getSource());
	}

	public static class_2267 method_9734(CommandContext<class_2168> commandContext, String string) {
		return commandContext.getArgument(string, class_2267.class);
	}

	public class_2267 method_9738(StringReader stringReader) throws CommandSyntaxException {
		return (class_2267)(stringReader.canRead() && stringReader.peek() == '^'
			? class_2268.method_9711(stringReader)
			: class_2280.method_9750(stringReader, this.field_10756));
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
				collection = ((class_2172)commandContext.getSource()).method_17772();
			}

			return class_2172.method_9260(string, collection, suggestionsBuilder, class_2170.method_9238(this::method_9738));
		}
	}

	@Override
	public Collection<String> getExamples() {
		return field_10754;
	}
}
