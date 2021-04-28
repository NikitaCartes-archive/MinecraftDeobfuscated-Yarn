package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.command.ServerCommandSource;

public interface NumberRangeArgumentType<T extends NumberRange<?>> extends ArgumentType<T> {
	static NumberRangeArgumentType.IntRangeArgumentType intRange() {
		return new NumberRangeArgumentType.IntRangeArgumentType();
	}

	static NumberRangeArgumentType.FloatRangeArgumentType floatRange() {
		return new NumberRangeArgumentType.FloatRangeArgumentType();
	}

	public static class FloatRangeArgumentType implements NumberRangeArgumentType<NumberRange.FloatRange> {
		private static final Collection<String> EXAMPLES = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

		public static NumberRange.FloatRange getRangeArgument(CommandContext<ServerCommandSource> context, String name) {
			return context.getArgument(name, NumberRange.FloatRange.class);
		}

		public NumberRange.FloatRange parse(StringReader stringReader) throws CommandSyntaxException {
			return NumberRange.FloatRange.parse(stringReader);
		}

		@Override
		public Collection<String> getExamples() {
			return EXAMPLES;
		}
	}

	public static class IntRangeArgumentType implements NumberRangeArgumentType<NumberRange.IntRange> {
		private static final Collection<String> EXAMPLES = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

		public static NumberRange.IntRange getRangeArgument(CommandContext<ServerCommandSource> context, String name) {
			return context.getArgument(name, NumberRange.IntRange.class);
		}

		public NumberRange.IntRange parse(StringReader stringReader) throws CommandSyntaxException {
			return NumberRange.IntRange.parse(stringReader);
		}

		@Override
		public Collection<String> getExamples() {
			return EXAMPLES;
		}
	}
}
