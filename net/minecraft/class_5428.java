/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class class_5428 {
    public static final Codec<class_5428> field_25809 = Codec.either(Codec.INT, RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("base")).forGetter(arg -> arg.field_25810), ((MapCodec)Codec.INT.fieldOf("spread")).forGetter(arg -> arg.field_25811)).apply((Applicative<class_5428, ?>)instance, class_5428::new)).comapFlatMap(arg -> {
        if (arg.field_25811 < 0) {
            return DataResult.error("Spread must be non-negative, got: " + arg.field_25811);
        }
        return DataResult.success(arg);
    }, Function.identity())).xmap(either -> either.map(class_5428::method_30314, arg -> arg), arg -> arg.field_25811 == 0 ? Either.left(arg.field_25810) : Either.right(arg));
    private final int field_25810;
    private final int field_25811;

    public static Codec<class_5428> method_30316(int i, int j, int k) {
        Function<class_5428, DataResult> function = arg -> {
            if (arg.field_25810 >= i && arg.field_25810 <= j) {
                if (arg.field_25811 <= k) {
                    return DataResult.success(arg);
                }
                return DataResult.error("Spread too big: " + arg.field_25811 + " > " + k);
            }
            return DataResult.error("Base value out of range: " + arg.field_25810 + " [" + i + "-" + j + "]");
        };
        return field_25809.flatXmap(function, function);
    }

    private class_5428(int i, int j) {
        this.field_25810 = i;
        this.field_25811 = j;
    }

    public static class_5428 method_30314(int i) {
        return new class_5428(i, 0);
    }

    public static class_5428 method_30315(int i, int j) {
        return new class_5428(i, j);
    }

    public int method_30321(Random random) {
        if (this.field_25811 == 0) {
            return this.field_25810;
        }
        return this.field_25810 + random.nextInt(this.field_25811 + 1);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        class_5428 lv = (class_5428)object;
        return this.field_25810 == lv.field_25810 && this.field_25811 == lv.field_25811;
    }

    public int hashCode() {
        return Objects.hash(this.field_25810, this.field_25811);
    }

    public String toString() {
        return "[" + this.field_25810 + '-' + (this.field_25810 + this.field_25811) + ']';
    }
}

