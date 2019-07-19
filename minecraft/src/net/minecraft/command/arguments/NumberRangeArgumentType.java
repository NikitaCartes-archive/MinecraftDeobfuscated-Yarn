package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.PacketByteBuf;

public interface NumberRangeArgumentType<T extends NumberRange<?>> extends ArgumentType<T> {
	static NumberRangeArgumentType.IntRangeArgumentType numberRange() {
		return new NumberRangeArgumentType.IntRangeArgumentType();
	}

	public static class FloatRangeArgumentType implements NumberRangeArgumentType<NumberRange.FloatRange> {
		private static final Collection<String> EXAMPLES = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

		public NumberRange.FloatRange parse(StringReader stringReader) throws CommandSyntaxException {
			return NumberRange.FloatRange.parse(stringReader);
		}

		@Override
		public Collection<String> getExamples() {
			return EXAMPLES;
		}

		public static class Serializer extends NumberRangeArgumentType.NumberSerializer<NumberRangeArgumentType.FloatRangeArgumentType> {
			public NumberRangeArgumentType.FloatRangeArgumentType fromPacket(PacketByteBuf packetByteBuf) {
				return new NumberRangeArgumentType.FloatRangeArgumentType();
			}
		}
	}

	public static class IntRangeArgumentType implements NumberRangeArgumentType<NumberRange.IntRange> {
		private static final Collection<String> EXAMPLES = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

		public static NumberRange.IntRange getRangeArgument(CommandContext<ServerCommandSource> commandContext, String string) {
			return commandContext.getArgument(string, NumberRange.IntRange.class);
		}

		public NumberRange.IntRange parse(StringReader stringReader) throws CommandSyntaxException {
			return NumberRange.IntRange.parse(stringReader);
		}

		@Override
		public Collection<String> getExamples() {
			return EXAMPLES;
		}

		public static class Serializer extends NumberRangeArgumentType.NumberSerializer<NumberRangeArgumentType.IntRangeArgumentType> {
			public NumberRangeArgumentType.IntRangeArgumentType fromPacket(PacketByteBuf packetByteBuf) {
				return new NumberRangeArgumentType.IntRangeArgumentType();
			}
		}
	}

	public abstract static class NumberSerializer<T extends NumberRangeArgumentType<?>> implements ArgumentSerializer<T> {
		public void toPacket(T numberRangeArgumentType, PacketByteBuf packetByteBuf) {
		}

		public void toJson(T numberRangeArgumentType, JsonObject jsonObject) {
		}
	}
}
