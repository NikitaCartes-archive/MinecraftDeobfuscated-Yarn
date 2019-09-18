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

	protected NumberRange(@Nullable T number, @Nullable T number2) {
		this.min = number;
		this.max = number2;
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
		@Nullable JsonElement jsonElement, R numberRange, BiFunction<JsonElement, String, T> biFunction, NumberRange.Factory<T, R> factory
	) {
		if (jsonElement == null || jsonElement.isJsonNull()) {
			return numberRange;
		} else if (JsonHelper.isNumber(jsonElement)) {
			T number = (T)biFunction.apply(jsonElement, "value");
			return factory.create(number, number);
		} else {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
			T number2 = (T)(jsonObject.has("min") ? biFunction.apply(jsonObject.get("min"), "min") : null);
			T number3 = (T)(jsonObject.has("max") ? biFunction.apply(jsonObject.get("max"), "max") : null);
			return factory.create(number2, number3);
		}
	}

	protected static <T extends Number, R extends NumberRange<T>> R parse(
		StringReader stringReader,
		NumberRange.CommandFactory<T, R> commandFactory,
		Function<String, T> function,
		Supplier<DynamicCommandExceptionType> supplier,
		Function<T, T> function2
	) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw EXCEPTION_EMPTY.createWithContext(stringReader);
		} else {
			int i = stringReader.getCursor();

			try {
				T number = (T)map(fromStringReader(stringReader, function, supplier), function2);
				T number2;
				if (stringReader.canRead(2) && stringReader.peek() == '.' && stringReader.peek(1) == '.') {
					stringReader.skip();
					stringReader.skip();
					number2 = (T)map(fromStringReader(stringReader, function, supplier), function2);
					if (number == null && number2 == null) {
						throw EXCEPTION_EMPTY.createWithContext(stringReader);
					}
				} else {
					number2 = number;
				}

				if (number == null && number2 == null) {
					throw EXCEPTION_EMPTY.createWithContext(stringReader);
				} else {
					return commandFactory.create(stringReader, number, number2);
				}
			} catch (CommandSyntaxException var8) {
				stringReader.setCursor(i);
				throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), i);
			}
		}
	}

	@Nullable
	private static <T extends Number> T fromStringReader(StringReader stringReader, Function<String, T> function, Supplier<DynamicCommandExceptionType> supplier) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && isNextCharValid(stringReader)) {
			stringReader.skip();
		}

		String string = stringReader.getString().substring(i, stringReader.getCursor());
		if (string.isEmpty()) {
			return null;
		} else {
			try {
				return (T)function.apply(string);
			} catch (NumberFormatException var6) {
				throw ((DynamicCommandExceptionType)supplier.get()).createWithContext(stringReader, string);
			}
		}
	}

	private static boolean isNextCharValid(StringReader stringReader) {
		char c = stringReader.peek();
		if ((c < '0' || c > '9') && c != '-') {
			return c != '.' ? false : !stringReader.canRead(2) || stringReader.peek(1) != '.';
		} else {
			return true;
		}
	}

	@Nullable
	private static <T> T map(@Nullable T object, Function<T, T> function) {
		return (T)(object == null ? null : function.apply(object));
	}

	@FunctionalInterface
	public interface CommandFactory<T extends Number, R extends NumberRange<T>> {
		R create(StringReader stringReader, @Nullable T number, @Nullable T number2) throws CommandSyntaxException;
	}

	@FunctionalInterface
	public interface Factory<T extends Number, R extends NumberRange<T>> {
		R create(@Nullable T number, @Nullable T number2);
	}

	public static class FloatRange extends NumberRange<Float> {
		public static final NumberRange.FloatRange ANY = new NumberRange.FloatRange(null, null);
		private final Double squaredMin;
		private final Double squaredMax;

		private static NumberRange.FloatRange create(StringReader stringReader, @Nullable Float float_, @Nullable Float float2) throws CommandSyntaxException {
			if (float_ != null && float2 != null && float_ > float2) {
				throw EXCEPTION_SWAPPED.createWithContext(stringReader);
			} else {
				return new NumberRange.FloatRange(float_, float2);
			}
		}

		@Nullable
		private static Double square(@Nullable Float float_) {
			return float_ == null ? null : float_.doubleValue() * float_.doubleValue();
		}

		private FloatRange(@Nullable Float float_, @Nullable Float float2) {
			super(float_, float2);
			this.squaredMin = square(float_);
			this.squaredMax = square(float2);
		}

		public static NumberRange.FloatRange atLeast(float f) {
			return new NumberRange.FloatRange(f, null);
		}

		public boolean test(float f) {
			return this.min != null && this.min > f ? false : this.max == null || !(this.max < f);
		}

		public boolean testSqrt(double d) {
			return this.squaredMin != null && this.squaredMin > d ? false : this.squaredMax == null || !(this.squaredMax < d);
		}

		public static NumberRange.FloatRange fromJson(@Nullable JsonElement jsonElement) {
			return fromJson(jsonElement, ANY, JsonHelper::asFloat, NumberRange.FloatRange::new);
		}

		public static NumberRange.FloatRange parse(StringReader stringReader) throws CommandSyntaxException {
			return parse(stringReader, float_ -> float_);
		}

		public static NumberRange.FloatRange parse(StringReader stringReader, Function<Float, Float> function) throws CommandSyntaxException {
			return parse(stringReader, NumberRange.FloatRange::create, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, function);
		}
	}

	public static class IntRange extends NumberRange<Integer> {
		public static final NumberRange.IntRange ANY = new NumberRange.IntRange(null, null);
		private final Long minSquared;
		private final Long maxSquared;

		private static NumberRange.IntRange parse(StringReader stringReader, @Nullable Integer integer, @Nullable Integer integer2) throws CommandSyntaxException {
			if (integer != null && integer2 != null && integer > integer2) {
				throw EXCEPTION_SWAPPED.createWithContext(stringReader);
			} else {
				return new NumberRange.IntRange(integer, integer2);
			}
		}

		@Nullable
		private static Long squared(@Nullable Integer integer) {
			return integer == null ? null : integer.longValue() * integer.longValue();
		}

		private IntRange(@Nullable Integer integer, @Nullable Integer integer2) {
			super(integer, integer2);
			this.minSquared = squared(integer);
			this.maxSquared = squared(integer2);
		}

		public static NumberRange.IntRange exactly(int i) {
			return new NumberRange.IntRange(i, i);
		}

		public static NumberRange.IntRange atLeast(int i) {
			return new NumberRange.IntRange(i, null);
		}

		public boolean test(int i) {
			return this.min != null && this.min > i ? false : this.max == null || this.max >= i;
		}

		public static NumberRange.IntRange fromJson(@Nullable JsonElement jsonElement) {
			return fromJson(jsonElement, ANY, JsonHelper::asInt, NumberRange.IntRange::new);
		}

		public static NumberRange.IntRange parse(StringReader stringReader) throws CommandSyntaxException {
			return fromStringReader(stringReader, integer -> integer);
		}

		public static NumberRange.IntRange fromStringReader(StringReader stringReader, Function<Integer, Integer> function) throws CommandSyntaxException {
			return parse(stringReader, NumberRange.IntRange::parse, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, function);
		}
	}
}
