package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class class_2284 implements ArgumentType<class_2284.class_2285> {
	private static final Collection<String> field_10783 = Arrays.asList("foo", "foo:bar", "#foo");
	private static final DynamicCommandExceptionType field_10782 = new DynamicCommandExceptionType(
		object -> new class_2588("arguments.function.tag.unknown", object)
	);
	private static final DynamicCommandExceptionType field_10784 = new DynamicCommandExceptionType(object -> new class_2588("arguments.function.unknown", object));

	public static class_2284 method_9760() {
		return new class_2284();
	}

	public class_2284.class_2285 method_9764(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '#') {
			stringReader.skip();
			final class_2960 lv = class_2960.method_12835(stringReader);
			return new class_2284.class_2285() {
				@Override
				public Collection<class_2158> method_9771(CommandContext<class_2168> commandContext) throws CommandSyntaxException {
					class_3494<class_2158> lv = class_2284.method_9767(commandContext, lv);
					return lv.method_15138();
				}

				@Override
				public Either<class_2158, class_3494<class_2158>> method_9770(CommandContext<class_2168> commandContext) throws CommandSyntaxException {
					return Either.right(class_2284.method_9767(commandContext, lv));
				}
			};
		} else {
			final class_2960 lv = class_2960.method_12835(stringReader);
			return new class_2284.class_2285() {
				@Override
				public Collection<class_2158> method_9771(CommandContext<class_2168> commandContext) throws CommandSyntaxException {
					return Collections.singleton(class_2284.method_9761(commandContext, lv));
				}

				@Override
				public Either<class_2158, class_3494<class_2158>> method_9770(CommandContext<class_2168> commandContext) throws CommandSyntaxException {
					return Either.left(class_2284.method_9761(commandContext, lv));
				}
			};
		}
	}

	private static class_2158 method_9761(CommandContext<class_2168> commandContext, class_2960 arg) throws CommandSyntaxException {
		class_2158 lv = commandContext.getSource().method_9211().method_3740().method_12905(arg);
		if (lv == null) {
			throw field_10784.create(arg.toString());
		} else {
			return lv;
		}
	}

	private static class_3494<class_2158> method_9767(CommandContext<class_2168> commandContext, class_2960 arg) throws CommandSyntaxException {
		class_3494<class_2158> lv = commandContext.getSource().method_9211().method_3740().method_12901().method_15193(arg);
		if (lv == null) {
			throw field_10782.create(arg.toString());
		} else {
			return lv;
		}
	}

	public static Collection<class_2158> method_9769(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<class_2284.class_2285>getArgument(string, class_2284.class_2285.class).method_9771(commandContext);
	}

	public static Either<class_2158, class_3494<class_2158>> method_9768(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<class_2284.class_2285>getArgument(string, class_2284.class_2285.class).method_9770(commandContext);
	}

	@Override
	public Collection<String> getExamples() {
		return field_10783;
	}

	public interface class_2285 {
		Collection<class_2158> method_9771(CommandContext<class_2168> commandContext) throws CommandSyntaxException;

		Either<class_2158, class_3494<class_2158>> method_9770(CommandContext<class_2168> commandContext) throws CommandSyntaxException;
	}
}
