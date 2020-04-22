/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import net.minecraft.predicate.NumberRange;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public class FloatRangeArgument {
    public static final FloatRangeArgument ANY = new FloatRangeArgument(null, null);
    public static final SimpleCommandExceptionType ONLY_INTS_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.range.ints"));
    private final Float min;
    private final Float max;

    public FloatRangeArgument(@Nullable Float float_, @Nullable Float float2) {
        this.min = float_;
        this.max = float2;
    }

    @Nullable
    public Float getMin() {
        return this.min;
    }

    @Nullable
    public Float getMax() {
        return this.max;
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
    private static Float mapFloat(@Nullable Float float_, Function<Float, Float> function) {
        return float_ == null ? null : function.apply(float_);
    }
}

