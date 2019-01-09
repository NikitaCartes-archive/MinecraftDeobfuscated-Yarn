package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class class_2216 implements ArgumentType<class_274> {
	private static final Collection<String> field_9926 = Arrays.asList("foo", "foo.bar.baz", "minecraft:foo");
	public static final DynamicCommandExceptionType field_9927 = new DynamicCommandExceptionType(object -> new class_2588("argument.criteria.invalid", object));

	private class_2216() {
	}

	public static class_2216 method_9399() {
		return new class_2216();
	}

	public static class_274 method_9402(CommandContext<class_2168> commandContext, String string) {
		return commandContext.getArgument(string, class_274.class);
	}

	public class_274 method_9403(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && stringReader.peek() != ' ') {
			stringReader.skip();
		}

		String string = stringReader.getString().substring(i, stringReader.getCursor());
		class_274 lv = class_274.method_1224(string);
		if (lv == null) {
			stringReader.setCursor(i);
			throw field_9927.create(string);
		} else {
			return lv;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		List<String> list = Lists.<String>newArrayList(class_274.field_1455.keySet());

		for (class_3448<?> lv : class_2378.field_11152) {
			for (Object object : lv.method_14959()) {
				String string = this.method_9400(lv, object);
				list.add(string);
			}
		}

		return class_2172.method_9265(list, suggestionsBuilder);
	}

	public <T> String method_9400(class_3448<T> arg, Object object) {
		return class_3445.method_14950(arg, (T)object);
	}

	@Override
	public Collection<String> getExamples() {
		return field_9926;
	}
}
