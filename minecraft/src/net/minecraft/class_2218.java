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
import java.util.concurrent.CompletableFuture;

public class class_2218 implements ArgumentType<class_2218.class_2219> {
	private static final Collection<String> field_9929 = Arrays.asList("=", ">", "<");
	private static final SimpleCommandExceptionType field_9931 = new SimpleCommandExceptionType(new class_2588("arguments.operation.invalid"));
	private static final SimpleCommandExceptionType field_9930 = new SimpleCommandExceptionType(new class_2588("arguments.operation.div0"));

	public static class_2218 method_9404() {
		return new class_2218();
	}

	public static class_2218.class_2219 method_9409(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.getArgument(string, class_2218.class_2219.class);
	}

	public class_2218.class_2219 method_9412(StringReader stringReader) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw field_9931.create();
		} else {
			int i = stringReader.getCursor();

			while (stringReader.canRead() && stringReader.peek() != ' ') {
				stringReader.skip();
			}

			return method_9413(stringReader.getString().substring(i, stringReader.getCursor()));
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_2172.method_9253(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_9929;
	}

	private static class_2218.class_2219 method_9413(String string) throws CommandSyntaxException {
		return (class_2218.class_2219)(string.equals("><") ? (arg, arg2) -> {
			int i = arg.method_1126();
			arg.method_1128(arg2.method_1126());
			arg2.method_1128(i);
		} : method_9407(string));
	}

	private static class_2218.class_2220 method_9407(String string) throws CommandSyntaxException {
		switch (string) {
			case "=":
				return (i, j) -> j;
			case "+=":
				return (i, j) -> i + j;
			case "-=":
				return (i, j) -> i - j;
			case "*=":
				return (i, j) -> i * j;
			case "/=":
				return (i, j) -> {
					if (j == 0) {
						throw field_9930.create();
					} else {
						return class_3532.method_15346(i, j);
					}
				};
			case "%=":
				return (i, j) -> {
					if (j == 0) {
						throw field_9930.create();
					} else {
						return class_3532.method_15387(i, j);
					}
				};
			case "<":
				return Math::min;
			case ">":
				return Math::max;
			default:
				throw field_9931.create();
		}
	}

	@FunctionalInterface
	public interface class_2219 {
		void apply(class_267 arg, class_267 arg2) throws CommandSyntaxException;
	}

	@FunctionalInterface
	interface class_2220 extends class_2218.class_2219 {
		int apply(int i, int j) throws CommandSyntaxException;

		@Override
		default void apply(class_267 arg, class_267 arg2) throws CommandSyntaxException {
			arg.method_1128(this.apply(arg.method_1126(), arg2.method_1126()));
		}
	}
}
