package net.minecraft.util;

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
import net.minecraft.text.TranslatableTextComponent;

public abstract class NumberRange<T extends Number> {
	public static final SimpleCommandExceptionType EXCEPTION_EMPTY = new SimpleCommandExceptionType(new TranslatableTextComponent("argument.range.empty"));
	public static final SimpleCommandExceptionType EXCEPTION_SWAPPED = new SimpleCommandExceptionType(new TranslatableTextComponent("argument.range.swapped"));
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

	public JsonElement serialize() {
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

	protected static <T extends Number, R extends NumberRange<T>> R method_9039(
		@Nullable JsonElement jsonElement, R numberRange, BiFunction<JsonElement, String, T> biFunction, NumberRange.class_2097<T, R> arg
	) {
		if (jsonElement == null || jsonElement.isJsonNull()) {
			return numberRange;
		} else if (JsonHelper.isNumber(jsonElement)) {
			T number = (T)biFunction.apply(jsonElement, "value");
			return arg.create(number, number);
		} else {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
			T number2 = (T)(jsonObject.has("min") ? biFunction.apply(jsonObject.get("min"), "min") : null);
			T number3 = (T)(jsonObject.has("max") ? biFunction.apply(jsonObject.get("max"), "max") : null);
			return arg.create(number2, number3);
		}
	}

	protected static <T extends Number, R extends NumberRange<T>> R method_9043(
		StringReader stringReader,
		NumberRange.class_2098<T, R> arg,
		Function<String, T> function,
		Supplier<DynamicCommandExceptionType> supplier,
		Function<T, T> function2
	) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw EXCEPTION_EMPTY.createWithContext(stringReader);
		} else {
			int i = stringReader.getCursor();

			try {
				T number = (T)method_9035(method_9037(stringReader, function, supplier), function2);
				T number2;
				if (stringReader.canRead(2) && stringReader.peek() == '.' && stringReader.peek(1) == '.') {
					stringReader.skip();
					stringReader.skip();
					number2 = (T)method_9035(method_9037(stringReader, function, supplier), function2);
					if (number == null && number2 == null) {
						throw EXCEPTION_EMPTY.createWithContext(stringReader);
					}
				} else {
					number2 = number;
				}

				if (number == null && number2 == null) {
					throw EXCEPTION_EMPTY.createWithContext(stringReader);
				} else {
					return arg.create(stringReader, number, number2);
				}
			} catch (CommandSyntaxException var8) {
				stringReader.setCursor(i);
				throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), i);
			}
		}
	}

	@Nullable
	private static <T extends Number> T method_9037(StringReader stringReader, Function<String, T> function, Supplier<DynamicCommandExceptionType> supplier) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && method_9040(stringReader)) {
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

	private static boolean method_9040(StringReader stringReader) {
		char c = stringReader.peek();
		if ((c < '0' || c > '9') && c != '-') {
			return c != '.' ? false : !stringReader.canRead(2) || stringReader.peek(1) != '.';
		} else {
			return true;
		}
	}

	@Nullable
	private static <T> T method_9035(@Nullable T object, Function<T, T> function) {
		return (T)(object == null ? null : function.apply(object));
	}

	public static class Float extends NumberRange<java.lang.Float> {
		public static final NumberRange.Float ANY = new NumberRange.Float(null, null);
		private final Double minSquared;
		private final Double maxSquared;

		private static NumberRange.Float method_9046(StringReader stringReader, @Nullable java.lang.Float float_, @Nullable java.lang.Float float2) throws CommandSyntaxException {
			if (float_ != null && float2 != null && float_ > float2) {
				throw EXCEPTION_SWAPPED.createWithContext(stringReader);
			} else {
				return new NumberRange.Float(float_, float2);
			}
		}

		@Nullable
		private static Double squared(@Nullable java.lang.Float float_) {
			return float_ == null ? null : float_.doubleValue() * float_.doubleValue();
		}

		private Float(@Nullable java.lang.Float float_, @Nullable java.lang.Float float2) {
			super(float_, float2);
			this.minSquared = squared(float_);
			this.maxSquared = squared(float2);
		}

		public static NumberRange.Float atLeast(float f) {
			return new NumberRange.Float(f, null);
		}

		public boolean matches(float f) {
			return this.min != null && this.min > f ? false : this.max == null || !(this.max < f);
		}

		public boolean matchesSquared(double d) {
			return this.minSquared != null && this.minSquared > d ? false : this.maxSquared == null || !(this.maxSquared < d);
		}

		public static NumberRange.Float fromJson(@Nullable JsonElement jsonElement) {
			return method_9039(jsonElement, ANY, JsonHelper::asFloat, NumberRange.Float::new);
		}

		public static NumberRange.Float method_9049(StringReader stringReader) throws CommandSyntaxException {
			return method_9048(stringReader, float_ -> float_);
		}

		public static NumberRange.Float method_9048(StringReader stringReader, Function<java.lang.Float, java.lang.Float> function) throws CommandSyntaxException {
			return method_9043(
				stringReader, NumberRange.Float::method_9046, java.lang.Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, function
			);
		}
	}

	public static class Integer extends NumberRange<java.lang.Integer> {
		public static final NumberRange.Integer ANY = new NumberRange.Integer(null, null);
		private final Long minSquared;
		private final Long maxSquared;

		private static NumberRange.Integer method_9055(StringReader stringReader, @Nullable java.lang.Integer integer, @Nullable java.lang.Integer integer2) throws CommandSyntaxException {
			if (integer != null && integer2 != null && integer > integer2) {
				throw EXCEPTION_SWAPPED.createWithContext(stringReader);
			} else {
				return new NumberRange.Integer(integer, integer2);
			}
		}

		@Nullable
		private static Long squared(@Nullable java.lang.Integer integer) {
			return integer == null ? null : integer.longValue() * integer.longValue();
		}

		private Integer(@Nullable java.lang.Integer integer, @Nullable java.lang.Integer integer2) {
			super(integer, integer2);
			this.minSquared = squared(integer);
			this.maxSquared = squared(integer2);
		}

		public static NumberRange.Integer exactly(int i) {
			return new NumberRange.Integer(i, i);
		}

		public static NumberRange.Integer atLeast(int i) {
			return new NumberRange.Integer(i, null);
		}

		public boolean test(int i) {
			return this.min != null && this.min > i ? false : this.max == null || this.max >= i;
		}

		public static NumberRange.Integer fromJson(@Nullable JsonElement jsonElement) {
			return method_9039(jsonElement, ANY, JsonHelper::asInt, NumberRange.Integer::new);
		}

		public static NumberRange.Integer method_9060(StringReader stringReader) throws CommandSyntaxException {
			return method_9057(stringReader, integer -> integer);
		}

		public static NumberRange.Integer method_9057(StringReader stringReader, Function<java.lang.Integer, java.lang.Integer> function) throws CommandSyntaxException {
			return method_9043(
				stringReader, NumberRange.Integer::method_9055, java.lang.Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, function
			);
		}
	}

	@FunctionalInterface
	public interface class_2097<T extends Number, R extends NumberRange<T>> {
		R create(@Nullable T number, @Nullable T number2);
	}

	@FunctionalInterface
	public interface class_2098<T extends Number, R extends NumberRange<T>> {
		R create(StringReader stringReader, @Nullable T number, @Nullable T number2) throws CommandSyntaxException;
	}
}
