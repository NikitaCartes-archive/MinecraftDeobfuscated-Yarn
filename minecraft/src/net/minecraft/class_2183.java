package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;

public class class_2183 implements ArgumentType<class_2183.class_2184> {
	private static final Collection<String> field_9847 = Arrays.asList("eyes", "feet");
	private static final DynamicCommandExceptionType field_9846 = new DynamicCommandExceptionType(object -> new class_2588("argument.anchor.invalid", object));

	public static class_2183.class_2184 method_9294(CommandContext<class_2168> commandContext, String string) {
		return commandContext.getArgument(string, class_2183.class_2184.class);
	}

	public static class_2183 method_9295() {
		return new class_2183();
	}

	public class_2183.class_2184 method_9292(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		String string = stringReader.readUnquotedString();
		class_2183.class_2184 lv = class_2183.class_2184.method_9296(string);
		if (lv == null) {
			stringReader.setCursor(i);
			throw field_9846.createWithContext(stringReader, string);
		} else {
			return lv;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_2172.method_9265(class_2183.class_2184.field_9852.keySet(), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_9847;
	}

	public static enum class_2184 {
		field_9853("feet", (arg, arg2) -> arg),
		field_9851("eyes", (arg, arg2) -> new class_243(arg.field_1352, arg.field_1351 + (double)arg2.method_5751(), arg.field_1350));

		private static final Map<String, class_2183.class_2184> field_9852 = class_156.method_654(Maps.<String, class_2183.class_2184>newHashMap(), hashMap -> {
			for (class_2183.class_2184 lv : values()) {
				hashMap.put(lv.field_9849, lv);
			}
		});
		private final String field_9849;
		private final BiFunction<class_243, class_1297, class_243> field_9848;

		private class_2184(String string2, BiFunction<class_243, class_1297, class_243> biFunction) {
			this.field_9849 = string2;
			this.field_9848 = biFunction;
		}

		@Nullable
		public static class_2183.class_2184 method_9296(String string) {
			return (class_2183.class_2184)field_9852.get(string);
		}

		public class_243 method_9302(class_1297 arg) {
			return (class_243)this.field_9848.apply(new class_243(arg.field_5987, arg.field_6010, arg.field_6035), arg);
		}

		public class_243 method_9299(class_2168 arg) {
			class_1297 lv = arg.method_9228();
			return lv == null ? arg.method_9222() : (class_243)this.field_9848.apply(arg.method_9222(), lv);
		}
	}
}
