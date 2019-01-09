package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class class_2233 implements ArgumentType<class_2233.class_2234> {
	public static final SuggestionProvider<class_2168> field_9951 = (commandContext, suggestionsBuilder) -> {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		class_2303 lv = new class_2303(stringReader);

		try {
			lv.method_9882();
		} catch (CommandSyntaxException var5) {
		}

		return lv.method_9908(suggestionsBuilder, suggestionsBuilderx -> class_2172.method_9265(commandContext.getSource().method_9262(), suggestionsBuilderx));
	};
	private static final Collection<String> field_9948 = Arrays.asList("Player", "0123", "*", "@e");
	private static final SimpleCommandExceptionType field_9950 = new SimpleCommandExceptionType(new class_2588("argument.scoreHolder.empty"));
	private final boolean field_9949;

	public class_2233(boolean bl) {
		this.field_9949 = bl;
	}

	public static String method_9452(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return (String)method_9458(commandContext, string).iterator().next();
	}

	public static Collection<String> method_9458(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return method_9450(commandContext, string, Collections::emptyList);
	}

	public static Collection<String> method_9449(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return method_9450(commandContext, string, commandContext.getSource().method_9211().method_3845()::method_1178);
	}

	public static Collection<String> method_9450(CommandContext<class_2168> commandContext, String string, Supplier<Collection<String>> supplier) throws CommandSyntaxException {
		Collection<String> collection = commandContext.<class_2233.class_2234>getArgument(string, class_2233.class_2234.class)
			.getNames(commandContext.getSource(), supplier);
		if (collection.isEmpty()) {
			throw class_2186.field_9863.create();
		} else {
			return collection;
		}
	}

	public static class_2233 method_9447() {
		return new class_2233(false);
	}

	public static class_2233 method_9451() {
		return new class_2233(true);
	}

	public class_2233.class_2234 method_9453(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '@') {
			class_2303 lv = new class_2303(stringReader);
			class_2300 lv2 = lv.method_9882();
			if (!this.field_9949 && lv2.method_9815() > 1) {
				throw class_2186.field_9860.create();
			} else {
				return new class_2233.class_2235(lv2);
			}
		} else {
			int i = stringReader.getCursor();

			while (stringReader.canRead() && stringReader.peek() != ' ') {
				stringReader.skip();
			}

			String string = stringReader.getString().substring(i, stringReader.getCursor());
			if (string.equals("*")) {
				return (arg, supplier) -> {
					Collection<String> collectionx = (Collection<String>)supplier.get();
					if (collectionx.isEmpty()) {
						throw field_9950.create();
					} else {
						return collectionx;
					}
				};
			} else {
				Collection<String> collection = Collections.singleton(string);
				return (arg, supplier) -> collection;
			}
		}
	}

	@Override
	public Collection<String> getExamples() {
		return field_9948;
	}

	@FunctionalInterface
	public interface class_2234 {
		Collection<String> getNames(class_2168 arg, Supplier<Collection<String>> supplier) throws CommandSyntaxException;
	}

	public static class class_2235 implements class_2233.class_2234 {
		private final class_2300 field_9952;

		public class_2235(class_2300 arg) {
			this.field_9952 = arg;
		}

		@Override
		public Collection<String> getNames(class_2168 arg, Supplier<Collection<String>> supplier) throws CommandSyntaxException {
			List<? extends class_1297> list = this.field_9952.method_9816(arg);
			if (list.isEmpty()) {
				throw class_2186.field_9863.create();
			} else {
				List<String> list2 = Lists.<String>newArrayList();

				for (class_1297 lv : list) {
					list2.add(lv.method_5820());
				}

				return list2;
			}
		}
	}

	public static class class_2236 implements class_2314<class_2233> {
		public void method_9461(class_2233 arg, class_2540 arg2) {
			byte b = 0;
			if (arg.field_9949) {
				b = (byte)(b | 1);
			}

			arg2.writeByte(b);
		}

		public class_2233 method_9460(class_2540 arg) {
			byte b = arg.readByte();
			boolean bl = (b & 1) != 0;
			return new class_2233(bl);
		}

		public void method_9459(class_2233 arg, JsonObject jsonObject) {
			jsonObject.addProperty("amount", arg.field_9949 ? "multiple" : "single");
		}
	}
}
