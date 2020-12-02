/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;

public class UniformFloatDistribution {
    public static final Codec<UniformFloatDistribution> CODEC = Codec.either(Codec.FLOAT, RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.FLOAT.fieldOf("base")).forGetter(uniformFloatDistribution -> Float.valueOf(uniformFloatDistribution.base)), ((MapCodec)Codec.FLOAT.fieldOf("spread")).forGetter(uniformFloatDistribution -> Float.valueOf(uniformFloatDistribution.spread))).apply((Applicative<UniformFloatDistribution, ?>)instance, UniformFloatDistribution::new)).comapFlatMap(uniformFloatDistribution -> {
        if (uniformFloatDistribution.spread < 0.0f) {
            return DataResult.error("Spread must be non-negative, got: " + uniformFloatDistribution.spread);
        }
        return DataResult.success(uniformFloatDistribution);
    }, Function.identity())).xmap(either -> either.map(UniformFloatDistribution::of, uniformFloatDistribution -> uniformFloatDistribution), uniformFloatDistribution -> uniformFloatDistribution.spread == 0.0f ? Either.left(Float.valueOf(uniformFloatDistribution.base)) : Either.right(uniformFloatDistribution));
    private final float base;
    private final float spread;

    public static Codec<UniformFloatDistribution> createValidatedCodec(float minBase, float maxBase, float maxSpread) {
        Function<UniformFloatDistribution, DataResult> function = uniformFloatDistribution -> {
            if (uniformFloatDistribution.base >= minBase && uniformFloatDistribution.base <= maxBase) {
                if (uniformFloatDistribution.spread <= maxSpread) {
                    return DataResult.success(uniformFloatDistribution);
                }
                return DataResult.error("Spread too big: " + uniformFloatDistribution.spread + " > " + maxSpread);
            }
            return DataResult.error("Base value out of range: " + uniformFloatDistribution.base + " [" + minBase + "-" + maxBase + "]");
        };
        return CODEC.flatXmap(function, function);
    }

    private UniformFloatDistribution(float base, float spread) {
        this.base = base;
        this.spread = spread;
    }

    /**
     * Creates a distribution with a constant value.
     * 
     * @param value the constant value
     */
    public static UniformFloatDistribution of(float value) {
        return new UniformFloatDistribution(value, 0.0f);
    }

    public static UniformFloatDistribution of(float base, float spread) {
        return new UniformFloatDistribution(base, spread);
    }

    public float getValue(Random random) {
        if (this.spread == 0.0f) {
            return this.base;
        }
        return MathHelper.nextBetween(random, this.base, this.base + this.spread);
    }

    public float minValue() {
        return this.base;
    }

    public float maxValue() {
        return this.base + this.spread;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        UniformFloatDistribution uniformFloatDistribution = (UniformFloatDistribution)object;
        return this.base == uniformFloatDistribution.base && this.spread == uniformFloatDistribution.spread;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.base), Float.valueOf(this.spread));
    }

    public String toString() {
        return "[" + this.base + '-' + (this.base + this.spread) + ']';
    }
}

