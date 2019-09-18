/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public abstract class NumberRange<T extends Number> {
    public static final SimpleCommandExceptionType EXCEPTION_EMPTY = new SimpleCommandExceptionType(new TranslatableText("argument.range.empty", new Object[0]));
    public static final SimpleCommandExceptionType EXCEPTION_SWAPPED = new SimpleCommandExceptionType(new TranslatableText("argument.range.swapped", new Object[0]));
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
        }
        if (this.min != null && this.min.equals(this.max)) {
            return new JsonPrimitive((Number)this.min);
        }
        JsonObject jsonObject = new JsonObject();
        if (this.min != null) {
            jsonObject.addProperty("min", (Number)this.min);
        }
        if (this.max != null) {
            jsonObject.addProperty("max", (Number)this.max);
        }
        return jsonObject;
    }

    protected static <T extends Number, R extends NumberRange<T>> R fromJson(@Nullable JsonElement jsonElement, R numberRange, BiFunction<JsonElement, String, T> biFunction, Factory<T, R> factory) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return numberRange;
        }
        if (JsonHelper.isNumber(jsonElement)) {
            Number number = (Number)biFunction.apply(jsonElement, "value");
            return factory.create(number, number);
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
        Number number2 = jsonObject.has("min") ? (Number)((Number)biFunction.apply(jsonObject.get("min"), "min")) : (Number)null;
        Number number3 = jsonObject.has("max") ? (Number)((Number)biFunction.apply(jsonObject.get("max"), "max")) : (Number)null;
        return factory.create(number2, number3);
    }

    protected static <T extends Number, R extends NumberRange<T>> R parse(StringReader stringReader, CommandFactory<T, R> commandFactory, Function<String, T> function, Supplier<DynamicCommandExceptionType> supplier, Function<T, T> function2) throws CommandSyntaxException {
        if (!stringReader.canRead()) {
            throw EXCEPTION_EMPTY.createWithContext(stringReader);
        }
        int i = stringReader.getCursor();
        try {
            Number number2;
            Number number = (Number)NumberRange.map(NumberRange.fromStringReader(stringReader, function, supplier), function2);
            if (stringReader.canRead(2) && stringReader.peek() == '.' && stringReader.peek(1) == '.') {
                stringReader.skip();
                stringReader.skip();
                number2 = (Number)NumberRange.map(NumberRange.fromStringReader(stringReader, function, supplier), function2);
                if (number == null && number2 == null) {
                    throw EXCEPTION_EMPTY.createWithContext(stringReader);
                }
            } else {
                number2 = number;
            }
            if (number == null && number2 == null) {
                throw EXCEPTION_EMPTY.createWithContext(stringReader);
            }
            return commandFactory.create(stringReader, number, number2);
        } catch (CommandSyntaxException commandSyntaxException) {
            stringReader.setCursor(i);
            throw new CommandSyntaxException(commandSyntaxException.getType(), commandSyntaxException.getRawMessage(), commandSyntaxException.getInput(), i);
        }
    }

    @Nullable
    private static <T extends Number> T fromStringReader(StringReader stringReader, Function<String, T> function, Supplier<DynamicCommandExceptionType> supplier) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        while (stringReader.canRead() && NumberRange.isNextCharValid(stringReader)) {
            stringReader.skip();
        }
        String string = stringReader.getString().substring(i, stringReader.getCursor());
        if (string.isEmpty()) {
            return null;
        }
        try {
            return (T)((Number)function.apply(string));
        } catch (NumberFormatException numberFormatException) {
            throw supplier.get().createWithContext(stringReader, string);
        }
    }

    private static boolean isNextCharValid(StringReader stringReader) {
        char c = stringReader.peek();
        if (c >= '0' && c <= '9' || c == '-') {
            return true;
        }
        if (c == '.') {
            return !stringReader.canRead(2) || stringReader.peek(1) != '.';
        }
        return false;
    }

    @Nullable
    private static <T> T map(@Nullable T object, Function<T, T> function) {
        return object == null ? null : (T)function.apply(object);
    }

    @FunctionalInterface
    public static interface CommandFactory<T extends Number, R extends NumberRange<T>> {
        public R create(StringReader var1, @Nullable T var2, @Nullable T var3) throws CommandSyntaxException;
    }

    @FunctionalInterface
    public static interface Factory<T extends Number, R extends NumberRange<T>> {
        public R create(@Nullable T var1, @Nullable T var2);
    }

    public static class FloatRange
    extends NumberRange<Float> {
        public static final FloatRange ANY = new FloatRange(null, null);
        private final Double squaredMin;
        private final Double squaredMax;

        private static FloatRange create(StringReader stringReader, @Nullable Float float_, @Nullable Float float2) throws CommandSyntaxException {
            if (float_ != null && float2 != null && float_.floatValue() > float2.floatValue()) {
                throw EXCEPTION_SWAPPED.createWithContext(stringReader);
            }
            return new FloatRange(float_, float2);
        }

        @Nullable
        private static Double square(@Nullable Float float_) {
            return float_ == null ? null : Double.valueOf(float_.doubleValue() * float_.doubleValue());
        }

        private FloatRange(@Nullable Float float_, @Nullable Float float2) {
            super(float_, float2);
            this.squaredMin = FloatRange.square(float_);
            this.squaredMax = FloatRange.square(float2);
        }

        public static FloatRange atLeast(float f) {
            return new FloatRange(Float.valueOf(f), null);
        }

        public boolean test(float f) {
            if (this.min != null && ((Float)this.min).floatValue() > f) {
                return false;
            }
            return this.max == null || !(((Float)this.max).floatValue() < f);
        }

        public boolean testSqrt(double d) {
            if (this.squaredMin != null && this.squaredMin > d) {
                return false;
            }
            return this.squaredMax == null || !(this.squaredMax < d);
        }

        public static FloatRange fromJson(@Nullable JsonElement jsonElement) {
            return FloatRange.fromJson(jsonElement, ANY, JsonHelper::asFloat, FloatRange::new);
        }

        public static FloatRange parse(StringReader stringReader) throws CommandSyntaxException {
            return FloatRange.parse(stringReader, float_ -> float_);
        }

        public static FloatRange parse(StringReader stringReader, Function<Float, Float> function) throws CommandSyntaxException {
            return FloatRange.parse(stringReader, FloatRange::create, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, function);
        }
    }

    public static class IntRange
    extends NumberRange<Integer> {
        public static final IntRange ANY = new IntRange(null, null);
        private final Long minSquared;
        private final Long maxSquared;

        private static IntRange parse(StringReader stringReader, @Nullable Integer integer, @Nullable Integer integer2) throws CommandSyntaxException {
            if (integer != null && integer2 != null && integer > integer2) {
                throw EXCEPTION_SWAPPED.createWithContext(stringReader);
            }
            return new IntRange(integer, integer2);
        }

        @Nullable
        private static Long squared(@Nullable Integer integer) {
            return integer == null ? null : Long.valueOf(integer.longValue() * integer.longValue());
        }

        private IntRange(@Nullable Integer integer, @Nullable Integer integer2) {
            super(integer, integer2);
            this.minSquared = IntRange.squared(integer);
            this.maxSquared = IntRange.squared(integer2);
        }

        public static IntRange exactly(int i) {
            return new IntRange(i, i);
        }

        public static IntRange atLeast(int i) {
            return new IntRange(i, null);
        }

        public boolean test(int i) {
            if (this.min != null && (Integer)this.min > i) {
                return false;
            }
            return this.max == null || (Integer)this.max >= i;
        }

        public static IntRange fromJson(@Nullable JsonElement jsonElement) {
            return IntRange.fromJson(jsonElement, ANY, JsonHelper::asInt, IntRange::new);
        }

        public static IntRange parse(StringReader stringReader) throws CommandSyntaxException {
            return IntRange.fromStringReader(stringReader, integer -> integer);
        }

        public static IntRange fromStringReader(StringReader stringReader, Function<Integer, Integer> function) throws CommandSyntaxException {
            return IntRange.parse(stringReader, IntRange::parse, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, function);
        }
    }
}

