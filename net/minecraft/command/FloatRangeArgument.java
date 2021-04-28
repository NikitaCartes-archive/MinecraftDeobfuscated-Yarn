/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import net.minecraft.predicate.NumberRange;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class FloatRangeArgument {
    public static final FloatRangeArgument ANY = new FloatRangeArgument(null, null);
    public static final SimpleCommandExceptionType ONLY_INTS_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.range.ints"));
    private final Float min;
    private final Float max;

    public FloatRangeArgument(@Nullable Float min, @Nullable Float max) {
        this.min = min;
        this.max = max;
    }

    public static FloatRangeArgument exactly(float value) {
        return new FloatRangeArgument(Float.valueOf(value), Float.valueOf(value));
    }

    public static FloatRangeArgument between(float min, float max) {
        return new FloatRangeArgument(Float.valueOf(min), Float.valueOf(max));
    }

    public static FloatRangeArgument atLeast(float value) {
        return new FloatRangeArgument(Float.valueOf(value), null);
    }

    public static FloatRangeArgument atMost(float value) {
        return new FloatRangeArgument(null, Float.valueOf(value));
    }

    public boolean isInRange(float value) {
        if (this.min != null && this.max != null && this.min.floatValue() > this.max.floatValue() && this.min.floatValue() > value && this.max.floatValue() < value) {
            return false;
        }
        if (this.min != null && this.min.floatValue() > value) {
            return false;
        }
        return this.max == null || !(this.max.floatValue() < value);
    }

    public boolean isInSquaredRange(double value) {
        if (this.min != null && this.max != null && this.min.floatValue() > this.max.floatValue() && (double)(this.min.floatValue() * this.min.floatValue()) > value && (double)(this.max.floatValue() * this.max.floatValue()) < value) {
            return false;
        }
        if (this.min != null && (double)(this.min.floatValue() * this.min.floatValue()) > value) {
            return false;
        }
        return this.max == null || !((double)(this.max.floatValue() * this.max.floatValue()) < value);
    }

    @Nullable
    public Float getMin() {
        return this.min;
    }

    @Nullable
    public Float getMax() {
        return this.max;
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        if (this.min != null && this.max != null && this.min.equals(this.max)) {
            return new JsonPrimitive(this.min);
        }
        JsonObject jsonObject = new JsonObject();
        if (this.min != null) {
            jsonObject.addProperty("min", this.min);
        }
        if (this.max != null) {
            jsonObject.addProperty("max", this.min);
        }
        return jsonObject;
    }

    public static FloatRangeArgument fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        if (JsonHelper.isNumber(json)) {
            float f = JsonHelper.asFloat(json, "value");
            return new FloatRangeArgument(Float.valueOf(f), Float.valueOf(f));
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "value");
        Float float_ = jsonObject.has("min") ? Float.valueOf(JsonHelper.getFloat(jsonObject, "min")) : null;
        Float float2 = jsonObject.has("max") ? Float.valueOf(JsonHelper.getFloat(jsonObject, "max")) : null;
        return new FloatRangeArgument(float_, float2);
    }

    public static FloatRangeArgument parse(StringReader reader, boolean allowFloats) throws CommandSyntaxException {
        return FloatRangeArgument.parse(reader, allowFloats, value -> value);
    }

    public static FloatRangeArgument parse(StringReader reader, boolean allowFloats, Function<Float, Float> transform) throws CommandSyntaxException {
        Float float2;
        if (!reader.canRead()) {
            throw NumberRange.EXCEPTION_EMPTY.createWithContext(reader);
        }
        int i = reader.getCursor();
        Float float_ = FloatRangeArgument.mapFloat(FloatRangeArgument.parseFloat(reader, allowFloats), transform);
        if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.') {
            reader.skip();
            reader.skip();
            float2 = FloatRangeArgument.mapFloat(FloatRangeArgument.parseFloat(reader, allowFloats), transform);
            if (float_ == null && float2 == null) {
                reader.setCursor(i);
                throw NumberRange.EXCEPTION_EMPTY.createWithContext(reader);
            }
        } else {
            if (!allowFloats && reader.canRead() && reader.peek() == '.') {
                reader.setCursor(i);
                throw ONLY_INTS_EXCEPTION.createWithContext(reader);
            }
            float2 = float_;
        }
        if (float_ == null && float2 == null) {
            reader.setCursor(i);
            throw NumberRange.EXCEPTION_EMPTY.createWithContext(reader);
        }
        return new FloatRangeArgument(float_, float2);
    }

    @Nullable
    private static Float parseFloat(StringReader reader, boolean allowFloats) throws CommandSyntaxException {
        int i = reader.getCursor();
        while (reader.canRead() && FloatRangeArgument.peekDigit(reader, allowFloats)) {
            reader.skip();
        }
        String string = reader.getString().substring(i, reader.getCursor());
        if (string.isEmpty()) {
            return null;
        }
        try {
            return Float.valueOf(Float.parseFloat(string));
        } catch (NumberFormatException numberFormatException) {
            if (allowFloats) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(reader, string);
            }
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(reader, string);
        }
    }

    private static boolean peekDigit(StringReader reader, boolean allowFloats) {
        char c = reader.peek();
        if (c >= '0' && c <= '9' || c == '-') {
            return true;
        }
        if (allowFloats && c == '.') {
            return !reader.canRead(2) || reader.peek(1) != '.';
        }
        return false;
    }

    @Nullable
    private static Float mapFloat(@Nullable Float value, Function<Float, Float> function) {
        return value == null ? null : function.apply(value);
    }
}

