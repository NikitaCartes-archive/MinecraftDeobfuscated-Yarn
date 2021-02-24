/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_5863;
import net.minecraft.class_5864;
import net.minecraft.util.math.MathHelper;

public class class_5866
extends class_5863 {
    public static final Codec<class_5866> field_29016 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.FLOAT.fieldOf("base")).forGetter(arg -> Float.valueOf(arg.field_29017)), ((MapCodec)Codec.FLOAT.fieldOf("spread")).forGetter(arg -> Float.valueOf(arg.field_29018))).apply((Applicative<class_5866, ?>)instance, class_5866::new)).comapFlatMap(arg -> {
        if (arg.field_29018 < 0.0f) {
            return DataResult.error("Spread must be non-negative, got: " + arg.field_29018);
        }
        return DataResult.success(arg);
    }, Function.identity());
    private final float field_29017;
    private final float field_29018;

    private class_5866(float f, float g) {
        this.field_29017 = f;
        this.field_29018 = g;
    }

    public static class_5866 method_33934(float f, float g) {
        return new class_5866(f, g);
    }

    @Override
    public float method_33920(Random random) {
        if (this.field_29018 == 0.0f) {
            return this.field_29017;
        }
        return MathHelper.nextBetween(random, this.field_29017, this.field_29017 + this.field_29018);
    }

    @Override
    public float method_33915() {
        return this.field_29017;
    }

    @Override
    public float method_33921() {
        return this.field_29017 + this.field_29018;
    }

    @Override
    public class_5864<?> method_33923() {
        return class_5864.field_29009;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        class_5866 lv = (class_5866)object;
        return this.field_29017 == lv.field_29017 && this.field_29018 == lv.field_29018;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.field_29017), Float.valueOf(this.field_29018));
    }

    public String toString() {
        return "[" + this.field_29017 + '-' + (this.field_29017 + this.field_29018) + ']';
    }
}

