package net.minecraft.predicate;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.text.Text;

public interface NumberRange<T extends Number> {
	SimpleCommandExceptionType EXCEPTION_EMPTY = new SimpleCommandExceptionType(Text.translatable("argument.range.empty"));
	SimpleCommandExceptionType EXCEPTION_SWAPPED = new SimpleCommandExceptionType(Text.translatable("argument.range.swapped"));

	Optional<T> min();

	Optional<T> max();

	default boolean isDummy() {
		return this.min().isEmpty() && this.max().isEmpty();
	}

	default Optional<T> getConstantValue() {
		Optional<T> optional = this.min();
		Optional<T> optional2 = this.max();
		return optional.equals(optional2) ? optional : Optional.empty();
	}

	static <T extends Number, R extends NumberRange<T>> Codec<R> createCodec(Codec<T> valueCodec, NumberRange.Factory<T, R> rangeFactory) {
		Codec<R> codec = RecordCodecBuilder.create(
			instance -> instance.group(valueCodec.optionalFieldOf("min").forGetter(NumberRange::min), valueCodec.optionalFieldOf("max").forGetter(NumberRange::max))
					.apply(instance, rangeFactory::create)
		);
		return Codec.either(codec, valueCodec)
			.xmap(either -> either.map(range -> range, value -> rangeFactory.create(Optional.of(value), Optional.of(value))), range -> {
				Optional<T> optional = range.getConstantValue();
				return optional.isPresent() ? Either.right((Number)optional.get()) : Either.left(range);
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
		R create(StringReader reader, Optional<T> min, Optional<T> max) throws CommandSyntaxException;
	}

	public static record DoubleRange(Optional<Double> min, Optional<Double> max, Optional<Double> squaredMin, Optional<Double> squaredMax)
		implements NumberRange<Double> {
		public static final NumberRange.DoubleRange ANY = new NumberRange.DoubleRange(Optional.empty(), Optional.empty());
		public static final Codec<NumberRange.DoubleRange> CODEC = NumberRange.createCodec((Codec<T>)Codec.DOUBLE, NumberRange.DoubleRange::new);

		private DoubleRange(Optional<Double> min, Optional<Double> max) {
			this(min, max, square(min), square(max));
		}

		private static NumberRange.DoubleRange create(StringReader reader, Optional<Double> min, Optional<Double> max) throws CommandSyntaxException {
			if (min.isPresent() && max.isPresent() && (Double)min.get() > (Double)max.get()) {
				throw EXCEPTION_SWAPPED.createWithContext(reader);
			} else {
				return new NumberRange.DoubleRange(min, max);
			}
		}

		private static Optional<Double> square(Optional<Double> value) {
			return value.map(d -> d * d);
		}

		public static NumberRange.DoubleRange exactly(double value) {
			return new NumberRange.DoubleRange(Optional.of(value), Optional.of(value));
		}

		public static NumberRange.DoubleRange between(double min, double max) {
			return new NumberRange.DoubleRange(Optional.of(min), Optional.of(max));
		}

		public static NumberRange.DoubleRange atLeast(double value) {
			return new NumberRange.DoubleRange(Optional.of(value), Optional.empty());
		}

		public static NumberRange.DoubleRange atMost(double value) {
			return new NumberRange.DoubleRange(Optional.empty(), Optional.of(value));
		}

		public boolean test(double value) {
			return this.min.isPresent() && this.min.get() > value ? false : this.max.isEmpty() || !((Double)this.max.get() < value);
		}

		public boolean testSqrt(double value) {
			return this.squaredMin.isPresent() && this.squaredMin.get() > value ? false : this.squaredMax.isEmpty() || !((Double)this.squaredMax.get() < value);
		}

		public static NumberRange.DoubleRange parse(StringReader reader) throws CommandSyntaxException {
			return parse(reader, value -> value);
		}

		public static NumberRange.DoubleRange parse(StringReader reader, Function<Double, Double> mapper) throws CommandSyntaxException {
			return NumberRange.parse(
				reader, NumberRange.DoubleRange::create, Double::parseDouble, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidDouble, (Function<T, T>)mapper
			);
		}
	}

	@FunctionalInterface
	public interface Factory<T extends Number, R extends NumberRange<T>> {
		R create(Optional<T> min, Optional<T> max);
	}

	public static record IntRange(Optional<Integer> min, Optional<Integer> max, Optional<Long> minSquared, Optional<Long> maxSquared)
		implements NumberRange<Integer> {
		public static final NumberRange.IntRange ANY = new NumberRange.IntRange(Optional.empty(), Optional.empty());
		public static final Codec<NumberRange.IntRange> CODEC = NumberRange.createCodec((Codec<T>)Codec.INT, NumberRange.IntRange::new);

		private IntRange(Optional<Integer> min, Optional<Integer> max) {
			this(min, max, min.map(i -> i.longValue() * i.longValue()), square(max));
		}

		private static NumberRange.IntRange parse(StringReader reader, Optional<Integer> min, Optional<Integer> max) throws CommandSyntaxException {
			if (min.isPresent() && max.isPresent() && (Integer)min.get() > (Integer)max.get()) {
				throw EXCEPTION_SWAPPED.createWithContext(reader);
			} else {
				return new NumberRange.IntRange(min, max);
			}
		}

		private static Optional<Long> square(Optional<Integer> value) {
			return value.map(i -> i.longValue() * i.longValue());
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
			return this.min.isPresent() && this.min.get() > value ? false : this.max.isEmpty() || (Integer)this.max.get() >= value;
		}

		public boolean testSqrt(long value) {
			return this.minSquared.isPresent() && this.minSquared.get() > value ? false : this.maxSquared.isEmpty() || (Long)this.maxSquared.get() >= value;
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
