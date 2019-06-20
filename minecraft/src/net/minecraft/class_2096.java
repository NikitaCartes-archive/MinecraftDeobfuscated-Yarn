package net.minecraft;

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

public abstract class class_2096<T extends Number> {
	public static final SimpleCommandExceptionType field_9700 = new SimpleCommandExceptionType(new class_2588("argument.range.empty"));
	public static final SimpleCommandExceptionType field_9701 = new SimpleCommandExceptionType(new class_2588("argument.range.swapped"));
	protected final T field_9702;
	protected final T field_9699;

	protected class_2096(@Nullable T number, @Nullable T number2) {
		this.field_9702 = number;
		this.field_9699 = number2;
	}

	@Nullable
	public T method_9038() {
		return this.field_9702;
	}

	@Nullable
	public T method_9042() {
		return this.field_9699;
	}

	public boolean method_9041() {
		return this.field_9702 == null && this.field_9699 == null;
	}

	public JsonElement method_9036() {
		if (this.method_9041()) {
			return JsonNull.INSTANCE;
		} else if (this.field_9702 != null && this.field_9702.equals(this.field_9699)) {
			return new JsonPrimitive(this.field_9702);
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.field_9702 != null) {
				jsonObject.addProperty("min", this.field_9702);
			}

			if (this.field_9699 != null) {
				jsonObject.addProperty("max", this.field_9699);
			}

			return jsonObject;
		}
	}

	protected static <T extends Number, R extends class_2096<T>> R method_9039(
		@Nullable JsonElement jsonElement, R arg, BiFunction<JsonElement, String, T> biFunction, class_2096.class_2097<T, R> arg2
	) {
		if (jsonElement == null || jsonElement.isJsonNull()) {
			return arg;
		} else if (class_3518.method_15275(jsonElement)) {
			T number = (T)biFunction.apply(jsonElement, "value");
			return arg2.create(number, number);
		} else {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "value");
			T number2 = (T)(jsonObject.has("min") ? biFunction.apply(jsonObject.get("min"), "min") : null);
			T number3 = (T)(jsonObject.has("max") ? biFunction.apply(jsonObject.get("max"), "max") : null);
			return arg2.create(number2, number3);
		}
	}

	protected static <T extends Number, R extends class_2096<T>> R method_9043(
		StringReader stringReader,
		class_2096.class_2098<T, R> arg,
		Function<String, T> function,
		Supplier<DynamicCommandExceptionType> supplier,
		Function<T, T> function2
	) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw field_9700.createWithContext(stringReader);
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
						throw field_9700.createWithContext(stringReader);
					}
				} else {
					number2 = number;
				}

				if (number == null && number2 == null) {
					throw field_9700.createWithContext(stringReader);
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

	@FunctionalInterface
	public interface class_2097<T extends Number, R extends class_2096<T>> {
		R create(@Nullable T number, @Nullable T number2);
	}

	@FunctionalInterface
	public interface class_2098<T extends Number, R extends class_2096<T>> {
		R create(StringReader stringReader, @Nullable T number, @Nullable T number2) throws CommandSyntaxException;
	}

	public static class class_2099 extends class_2096<Float> {
		public static final class_2096.class_2099 field_9705 = new class_2096.class_2099(null, null);
		private final Double field_9703;
		private final Double field_9704;

		private static class_2096.class_2099 method_9046(StringReader stringReader, @Nullable Float float_, @Nullable Float float2) throws CommandSyntaxException {
			if (float_ != null && float2 != null && float_ > float2) {
				throw field_9701.createWithContext(stringReader);
			} else {
				return new class_2096.class_2099(float_, float2);
			}
		}

		@Nullable
		private static Double method_9044(@Nullable Float float_) {
			return float_ == null ? null : float_.doubleValue() * float_.doubleValue();
		}

		private class_2099(@Nullable Float float_, @Nullable Float float2) {
			super(float_, float2);
			this.field_9703 = method_9044(float_);
			this.field_9704 = method_9044(float2);
		}

		public static class_2096.class_2099 method_9050(float f) {
			return new class_2096.class_2099(f, null);
		}

		public boolean method_9047(float f) {
			return this.field_9702 != null && this.field_9702 > f ? false : this.field_9699 == null || !(this.field_9699 < f);
		}

		public boolean method_9045(double d) {
			return this.field_9703 != null && this.field_9703 > d ? false : this.field_9704 == null || !(this.field_9704 < d);
		}

		public static class_2096.class_2099 method_9051(@Nullable JsonElement jsonElement) {
			return method_9039(jsonElement, field_9705, class_3518::method_15269, class_2096.class_2099::new);
		}

		public static class_2096.class_2099 method_9049(StringReader stringReader) throws CommandSyntaxException {
			return method_9048(stringReader, float_ -> float_);
		}

		public static class_2096.class_2099 method_9048(StringReader stringReader, Function<Float, Float> function) throws CommandSyntaxException {
			return method_9043(
				stringReader, class_2096.class_2099::method_9046, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, function
			);
		}
	}

	public static class class_2100 extends class_2096<Integer> {
		public static final class_2096.class_2100 field_9708 = new class_2096.class_2100(null, null);
		private final Long field_9706;
		private final Long field_9707;

		private static class_2096.class_2100 method_9055(StringReader stringReader, @Nullable Integer integer, @Nullable Integer integer2) throws CommandSyntaxException {
			if (integer != null && integer2 != null && integer > integer2) {
				throw field_9701.createWithContext(stringReader);
			} else {
				return new class_2096.class_2100(integer, integer2);
			}
		}

		@Nullable
		private static Long method_9059(@Nullable Integer integer) {
			return integer == null ? null : integer.longValue() * integer.longValue();
		}

		private class_2100(@Nullable Integer integer, @Nullable Integer integer2) {
			super(integer, integer2);
			this.field_9706 = method_9059(integer);
			this.field_9707 = method_9059(integer2);
		}

		public static class_2096.class_2100 method_9058(int i) {
			return new class_2096.class_2100(i, i);
		}

		public static class_2096.class_2100 method_9053(int i) {
			return new class_2096.class_2100(i, null);
		}

		public boolean method_9054(int i) {
			return this.field_9702 != null && this.field_9702 > i ? false : this.field_9699 == null || this.field_9699 >= i;
		}

		public static class_2096.class_2100 method_9056(@Nullable JsonElement jsonElement) {
			return method_9039(jsonElement, field_9708, class_3518::method_15257, class_2096.class_2100::new);
		}

		public static class_2096.class_2100 method_9060(StringReader stringReader) throws CommandSyntaxException {
			return method_9057(stringReader, integer -> integer);
		}

		public static class_2096.class_2100 method_9057(StringReader stringReader, Function<Integer, Integer> function) throws CommandSyntaxException {
			return method_9043(
				stringReader, class_2096.class_2100::method_9055, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, function
			);
		}
	}
}
