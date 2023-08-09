package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;

public interface NumberRange<T extends Number> {
	SimpleCommandExceptionType EXCEPTION_EMPTY = new SimpleCommandExceptionType(Text.translatable("argument.range.empty"));
	SimpleCommandExceptionType EXCEPTION_SWAPPED = new SimpleCommandExceptionType(Text.translatable("argument.range.swapped"));

	Optional<T> getMin();

	Optional<T> getMax();

	default boolean isDummy() {
		return this.getMin().isEmpty() && this.getMax().isEmpty();
	}

	default Optional<T> getConstantValue() {
		Optional<T> optional = this.getMin();
		Optional<T> optional2 = this.getMax();
		return optional.equals(optional2) ? optional : Optional.empty();
	}

	static <T extends Number, R extends NumberRange<T>> Codec<R> method_53191(Codec<T> codec, NumberRange.Factory<T, R> factory) {
		Codec<R> codec2 = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.createStrictOptionalFieldCodec(codec, "min").forGetter(NumberRange::getMin),
						Codecs.createStrictOptionalFieldCodec(codec, "max").forGetter(NumberRange::getMax)
					)
					.apply(instance, factory::create)
		);
		return Codec.either(codec2, codec)
			.xmap(either -> either.map(numberRange -> numberRange, number -> factory.create(Optional.of(number), Optional.of(number))), numberRange -> {
				Optional<T> optional = numberRange.getConstantValue();
				return optional.isPresent() ? Either.right((Number)optional.get()) : Either.left(numberRange);
			});
	}

	static <T extends Number, R extends NumberRange<T>> R parse(
		StringReader commandReader,
		NumberRange.CommandFactory<T, R> commandFactory,
		Function<String, T> converter,
		Supplier<DynamicCommandExceptionType> exceptionTypeSupplier,
		Function<T, T> mapper
	) throws CommandSyntaxException {
		if (!commandReader.canRead()) {
			throw EXCEPTION_EMPTY.createWithContext(commandReader);
		} else {
			int i = commandReader.getCursor();

			try {
				Optional<T> optional = fromStringReader(commandReader, converter, exceptionTypeSupplier).map(mapper);
				Optional<T> optional2;
				if (commandReader.canRead(2) && commandReader.peek() == '.' && commandReader.peek(1) == '.') {
					commandReader.skip();
					commandReader.skip();
					optional2 = fromStringReader(commandReader, converter, exceptionTypeSupplier).map(mapper);
					if (optional.isEmpty() && optional2.isEmpty()) {
						throw EXCEPTION_EMPTY.createWithContext(commandReader);
					}
				} else {
					optional2 = optional;
				}

				if (optional.isEmpty() && optional2.isEmpty()) {
					throw EXCEPTION_EMPTY.createWithContext(commandReader);
				} else {
					return commandFactory.create(commandReader, optional, optional2);
				}
			} catch (CommandSyntaxException var8) {
				commandReader.setCursor(i);
				throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), i);
			}
		}
	}

	private static <T extends Number> Optional<T> fromStringReader(
		StringReader reader, Function<String, T> converter, Supplier<DynamicCommandExceptionType> exceptionTypeSupplier
	) throws CommandSyntaxException {
		int i = reader.getCursor();

		while (reader.canRead() && isNextCharValid(reader)) {
			reader.skip();
		}

		String string = reader.getString().substring(i, reader.getCursor());
		if (string.isEmpty()) {
			return Optional.empty();
		} else {
			try {
				return Optional.of((Number)converter.apply(string));
			} catch (NumberFormatException var6) {
				throw ((DynamicCommandExceptionType)exceptionTypeSupplier.get()).createWithContext(reader, string);
			}
		}
	}

	private static boolean isNextCharValid(StringReader reader) {
		char c = reader.peek();
		if ((c < '0' || c > '9') && c != '-') {
			return c != '.' ? false : !reader.canRead(2) || reader.peek(1) != '.';
		} else {
			return true;
		}
	}

	@FunctionalInterface
	public interface CommandFactory<T extends Number, R extends NumberRange<T>> {
		R create(StringReader reader, Optional<T> optional, Optional<T> optional2) throws CommandSyntaxException;
	}

	@FunctionalInterface
	public interface Factory<T extends Number, R extends NumberRange<T>> {
		R create(Optional<T> optional, Optional<T> optional2);
	}

	public static record FloatRange(Optional<Double> getMin, Optional<Double> getMax, Optional<Double> squaredMin, Optional<Double> squaredMax)
		implements NumberRange<Double> {
		public static final NumberRange.FloatRange ANY = new NumberRange.FloatRange(Optional.empty(), Optional.empty());
		public static final Codec<NumberRange.FloatRange> CODEC = NumberRange.method_53191((Codec<T>)Codec.DOUBLE, NumberRange.FloatRange::new);

		private FloatRange(Optional<Double> optional, Optional<Double> optional2) {
			this(optional, optional2, square(optional), square(optional2));
		}

		private static NumberRange.FloatRange create(StringReader reader, Optional<Double> optional, Optional<Double> optional2) throws CommandSyntaxException {
			if (optional.isPresent() && optional2.isPresent() && (Double)optional.get() > (Double)optional2.get()) {
				throw EXCEPTION_SWAPPED.createWithContext(reader);
			} else {
				return new NumberRange.FloatRange(optional, optional2);
			}
		}

		private static Optional<Double> square(Optional<Double> optional) {
			return optional.map(double_ -> double_ * double_);
		}

		public static NumberRange.FloatRange exactly(double value) {
			return new NumberRange.FloatRange(Optional.of(value), Optional.of(value));
		}

		public static NumberRange.FloatRange between(double min, double max) {
			return new NumberRange.FloatRange(Optional.of(min), Optional.of(max));
		}

		public static NumberRange.FloatRange atLeast(double value) {
			return new NumberRange.FloatRange(Optional.of(value), Optional.empty());
		}

		public static NumberRange.FloatRange atMost(double value) {
			return new NumberRange.FloatRange(Optional.empty(), Optional.of(value));
		}

		public boolean test(double value) {
			return this.getMin.isPresent() && this.getMin.get() > value ? false : this.getMax.isEmpty() || !((Double)this.getMax.get() < value);
		}

		public boolean testSqrt(double value) {
			return this.squaredMin.isPresent() && this.squaredMin.get() > value ? false : this.squaredMax.isEmpty() || !((Double)this.squaredMax.get() < value);
		}

		public static NumberRange.FloatRange fromJson(@Nullable JsonElement element) {
			return element != null && !element.isJsonNull() ? Util.getResult(CODEC.parse(JsonOps.INSTANCE, element), JsonParseException::new) : ANY;
		}

		public JsonElement toJson() {
			return (JsonElement)(this.isDummy() ? JsonNull.INSTANCE : Util.getResult(CODEC.encodeStart(JsonOps.INSTANCE, this), IllegalStateException::new));
		}

		public static NumberRange.FloatRange parse(StringReader reader) throws CommandSyntaxException {
			return parse(reader, value -> value);
		}

		public static NumberRange.FloatRange parse(StringReader reader, Function<Double, Double> mapper) throws CommandSyntaxException {
			return NumberRange.parse(
				reader, NumberRange.FloatRange::create, Double::parseDouble, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidDouble, (Function<T, T>)mapper
			);
		}
	}

	public static record IntRange(Optional<Integer> getMin, Optional<Integer> getMax, Optional<Long> minSquared, Optional<Long> maxSquared)
		implements NumberRange<Integer> {
		public static final NumberRange.IntRange ANY = new NumberRange.IntRange(Optional.empty(), Optional.empty());
		public static final Codec<NumberRange.IntRange> CODEC = NumberRange.method_53191((Codec<T>)Codec.INT, NumberRange.IntRange::new);

		private IntRange(Optional<Integer> optional, Optional<Integer> optional2) {
			this(optional, optional2, optional.map(integer -> integer.longValue() * integer.longValue()), squared(optional2));
		}

		private static NumberRange.IntRange parse(StringReader reader, Optional<Integer> optional, Optional<Integer> optional2) throws CommandSyntaxException {
			if (optional.isPresent() && optional2.isPresent() && (Integer)optional.get() > (Integer)optional2.get()) {
				throw EXCEPTION_SWAPPED.createWithContext(reader);
			} else {
				return new NumberRange.IntRange(optional, optional2);
			}
		}

		private static Optional<Long> squared(Optional<Integer> optional) {
			return optional.map(integer -> integer.longValue() * integer.longValue());
		}

		public static NumberRange.IntRange exactly(int value) {
			return new NumberRange.IntRange(Optional.of(value), Optional.of(value));
		}

		public static NumberRange.IntRange between(int min, int max) {
			return new NumberRange.IntRange(Optional.of(min), Optional.of(max));
		}

		public static NumberRange.IntRange atLeast(int value) {
			return new NumberRange.IntRange(Optional.of(value), Optional.empty());
		}

		public static NumberRange.IntRange atMost(int value) {
			return new NumberRange.IntRange(Optional.empty(), Optional.of(value));
		}

		public boolean test(int value) {
			return this.getMin.isPresent() && this.getMin.get() > value ? false : this.getMax.isEmpty() || (Integer)this.getMax.get() >= value;
		}

		public boolean testSqrt(long value) {
			return this.minSquared.isPresent() && this.minSquared.get() > value ? false : this.maxSquared.isEmpty() || (Long)this.maxSquared.get() >= value;
		}

		public static NumberRange.IntRange fromJson(@Nullable JsonElement element) {
			return element != null && !element.isJsonNull() ? Util.getResult(CODEC.parse(JsonOps.INSTANCE, element), JsonParseException::new) : ANY;
		}

		public JsonElement toJson() {
			return (JsonElement)(this.isDummy() ? JsonNull.INSTANCE : Util.getResult(CODEC.encodeStart(JsonOps.INSTANCE, this), IllegalStateException::new));
		}

		public static NumberRange.IntRange parse(StringReader reader) throws CommandSyntaxException {
			return fromStringReader(reader, value -> value);
		}

		public static NumberRange.IntRange fromStringReader(StringReader reader, Function<Integer, Integer> converter) throws CommandSyntaxException {
			return NumberRange.parse(
				reader, NumberRange.IntRange::parse, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, (Function<T, T>)converter
			);
		}
	}
}
