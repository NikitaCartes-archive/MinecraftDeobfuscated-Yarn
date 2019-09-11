/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.NumberRange;
import org.jetbrains.annotations.Nullable;

public class FloatRangeArgument {
    public static final FloatRangeArgument ANY = new FloatRangeArgument(null, null);
    public static final SimpleCommandExceptionType ONLY_INTS_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.range.ints", new Object[0]));
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

    public static FloatRangeArgument parse(StringReader stringReader, boolean bl, Function<Float, Float> function) throws CommandSyntaxException {
        Float float2;
        if (!stringReader.canRead()) {
            throw NumberRange.EXCEPTION_EMPTY.createWithContext(stringReader);
        }
        int i = stringReader.getCursor();
        Float float_ = FloatRangeArgument.mapFloat(FloatRangeArgument.parseFloat(stringReader, bl), function);
        if (stringReader.canRead(2) && stringReader.peek() == '.' && stringReader.peek(1) == '.') {
            stringReader.skip();
            stringReader.skip();
            float2 = FloatRangeArgument.mapFloat(FloatRangeArgument.parseFloat(stringReader, bl), function);
            if (float_ == null && float2 == null) {
                stringReader.setCursor(i);
                throw NumberRange.EXCEPTION_EMPTY.createWithContext(stringReader);
            }
        } else {
            if (!bl && stringReader.canRead() && stringReader.peek() == '.') {
                stringReader.setCursor(i);
                throw ONLY_INTS_EXCEPTION.createWithContext(stringReader);
            }
            float2 = float_;
        }
        if (float_ == null && float2 == null) {
            stringReader.setCursor(i);
            throw NumberRange.EXCEPTION_EMPTY.createWithContext(stringReader);
        }
        return new FloatRangeArgument(float_, float2);
    }

    @Nullable
    private static Float parseFloat(StringReader stringReader, boolean bl) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        while (stringReader.canRead() && FloatRangeArgument.peekDigit(stringReader, bl)) {
            stringReader.skip();
        }
        String string = stringReader.getString().substring(i, stringReader.getCursor());
        if (string.isEmpty()) {
            return null;
        }
        try {
            return Float.valueOf(Float.parseFloat(string));
        } catch (NumberFormatException numberFormatException) {
            if (bl) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(stringReader, string);
            }
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(stringReader, string);
        }
    }

    private static boolean peekDigit(StringReader stringReader, boolean bl) {
        char c = stringReader.peek();
        if (c >= '0' && c <= '9' || c == '-') {
            return true;
        }
        if (bl && c == '.') {
            return !stringReader.canRead(2) || stringReader.peek(1) != '.';
        }
        return false;
    }

    @Nullable
    private static Float mapFloat(@Nullable Float float_, Function<Float, Float> function) {
        return float_ == null ? null : function.apply(float_);
    }
}

