package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.JsonHelper;

public abstract class NumberRange<T extends Number> {
	public static final SimpleCommandExceptionType EXCEPTION_EMPTY = new SimpleCommandExceptionType(new TranslatableText("argument.range.empty"));
	public static final SimpleCommandExceptionType EXCEPTION_SWAPPED = new SimpleCommandExceptionType(new TranslatableText("argument.range.swapped"));
	protected final T min;
	protected final T max;

	protected NumberRange(@Nullable T min, @Nullable T max) {
		this.min = min;
		this.max = max;
	}

	@Nullable
	public T getMin() {
		return this.min;
	}

	@Nullable
	public T getMax() {
		return this.max;
	}

	public boolean isDummy() {
		return this.min == null && this.max == null;
	}

	public JsonElement toJson() {
		if (this.isDummy()) {
			return JsonNull.INSTANCE;
		} else if (this.min != null && this.min.equals(this.max)) {
			return new JsonPrimitive(this.min);
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.min != null) {
				jsonObject.addProperty("min", this.min);
			}

			if (this.max != null) {
				jsonObject.addProperty("max", this.max);
			}

			return jsonObject;
		}
	}

	protected static <T extends Number, R extends NumberRange<T>> R fromJson(
		@Nullable JsonElement json, R fallback, BiFunction<JsonElement, String, T> asNumber, NumberRange.Factory<T, R> factory
	) {
		if (json == null || json.isJsonNull()) {
			return fallback;
		} else if (JsonHelper.isNumber(json)) {
			T number = (T)asNumber.apply(json, "value");
			return factory.create(number, number);
		} else {
			JsonObject jsonObject = JsonHelper.asObject(json, "value");
			T number2 = (T)(jsonObject.has("min") ? asNumber.apply(jsonObject.get("min"), "min") : null);
			T number3 = (T)(jsonObject.has("max") ? asNumber.apply(jsonObject.get("max"), "max") : null);
			return factory.create(number2, number3);
		}
	}

	protected static <T extends Number, R extends NumberRange<T>> R parse(
		StringReader commandReader,
		NumberRange.class_2098<T, R> arg,
		Function<String, T> converter,
		Supplier<DynamicCommandExceptionType> exceptionTypeSupplier,
		Function<T, T> mapper
	) throws CommandSyntaxException {
		if (!commandReader.canRead()) {
			throw EXCEPTION_EMPTY.createWithContext(commandReader);
		} else {
			int i = commandReader.getCursor();

			try {
				T number = (T)map(fromStringReader(commandReader, converter, exceptionTypeSupplier), mapper);
				T number2;
				if (commandReader.canRead(2) && commandReader.peek() == '.' && commandReader.peek(1) == '.') {
					commandReader.skip();
					commandReader.skip();
					number2 = (T)map(fromStringReader(commandReader, converter, exceptionTypeSupplier), mapper);
					if (number == null && number2 == null) {
						throw EXCEPTION_EMPTY.createWithContext(commandReader);
					}
				} else {
					number2 = number;
				}

				if (number == null && number2 == null) {
					throw EXCEPTION_EMPTY.createWithContext(commandReader);
				} else {
					return arg.create(commandReader, number, number2);
				}
			} catch (CommandSyntaxException var8) {
				commandReader.setCursor(i);
				throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), i);
			}
		}
	}

	@Nullable
	private static <T extends Number> T fromStringReader(
		StringReader reader, Function<String, T> converter, Supplier<DynamicCommandExceptionType> exceptionTypeSupplier
	) throws CommandSyntaxException {
		int i = reader.getCursor();

		while (reader.canRead() && isNextCharValid(reader)) {
			reader.skip();
		}

		String string = reader.getString().substring(i, reader.getCursor());
		if (string.isEmpty()) {
			return null;
		} else {
			try {
				return (T)converter.apply(string);
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

	@Nullable
	private static <T> T map(@Nullable T object, Function<T, T> function) {
		return (T)(object == null ? null : function.apply(object));
	}

	@FunctionalInterface
	public interface Factory<T extends Number, R extends NumberRange<T>> {
		R create(@Nullable T number, @Nullable T number2);
	}

	public static class FloatRange extends NumberRange<Float> {
		public static final NumberRange.FloatRange ANY = new NumberRange.FloatRange(null, null);
		private final Double minSquared;
		private final Double maxSquared;

		private static NumberRange.FloatRange create(StringReader reader, @Nullable Float min, @Nullable Float max) throws CommandSyntaxException {
			if (min != null && max != null && min > max) {
				throw EXCEPTION_SWAPPED.createWithContext(reader);
			} else {
				return new NumberRange.FloatRange(min, max);
			}
		}

		@Nullable
		private static Double squared(@Nullable Float value) {
			return value == null ? null : value.doubleValue() * value.doubleValue();
		}

		private FloatRange(@Nullable Float max, @Nullable Float float_) {
			super(max, float_);
			this.minSquared = squared(max);
			this.maxSquared = squared(float_);
		}

		public static NumberRange.FloatRange atLeast(float value) {
			return new NumberRange.FloatRange(value, null);
		}

		public boolean matches(float f) {
			return this.min != null && this.min > f ? false : this.max == null || !(this.max < f);
		}

		public boolean matchesSquared(double d) {
			return this.minSquared != null && this.minSquared > d ? false : this.maxSquared == null || !(this.maxSquared < d);
		}

		public static NumberRange.FloatRange fromJson(@Nullable JsonElement element) {
			return fromJson(element, ANY, JsonHelper::asFloat, NumberRange.FloatRange::new);
		}

		public static NumberRange.FloatRange parse(StringReader reader) throws CommandSyntaxException {
			return parse(reader, float_ -> float_);
		}

		public static NumberRange.FloatRange parse(StringReader reader, Function<Float, Float> function) throws CommandSyntaxException {
			return parse(reader, NumberRange.FloatRange::create, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, function);
		}
	}

	public static class IntRange extends NumberRange<Integer> {
		public static final NumberRange.IntRange ANY = new NumberRange.IntRange(null, null);
		private final Long minSquared;
		private final Long maxSquared;

		private static NumberRange.IntRange method_9055(StringReader stringReader, @Nullable Integer min, @Nullable Integer max) throws CommandSyntaxException {
			if (min != null && max != null && min > max) {
				throw EXCEPTION_SWAPPED.createWithContext(stringReader);
			} else {
				return new NumberRange.IntRange(min, max);
			}
		}

		@Nullable
		private static Long squared(@Nullable Integer value) {
			return value == null ? null : value.longValue() * value.longValue();
		}

		private IntRange(@Nullable Integer max, @Nullable Integer integer) {
			super(max, integer);
			this.minSquared = squared(max);
			this.maxSquared = squared(integer);
		}

		public static NumberRange.IntRange exactly(int value) {
			return new NumberRange.IntRange(value, value);
		}

		public static NumberRange.IntRange atLeast(int value) {
			return new NumberRange.IntRange(value, null);
		}

		public boolean test(int i) {
			return this.min != null && this.min > i ? false : this.max == null || this.max >= i;
		}

		public static NumberRange.IntRange fromJson(@Nullable JsonElement element) {
			return fromJson(element, ANY, JsonHelper::asInt, NumberRange.IntRange::new);
		}

		public static NumberRange.IntRange parse(StringReader stringReader) throws CommandSyntaxException {
			return fromStringReader(stringReader, integer -> integer);
		}

		public static NumberRange.IntRange fromStringReader(StringReader stringReader, Function<Integer, Integer> function) throws CommandSyntaxException {
			return parse(stringReader, NumberRange.IntRange::method_9055, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, function);
		}
	}

	@FunctionalInterface
	public interface class_2098<T extends Number, R extends NumberRange<T>> {
		R create(StringReader stringReader, @Nullable T number, @Nullable T number2) throws CommandSyntaxException;
	}
}
