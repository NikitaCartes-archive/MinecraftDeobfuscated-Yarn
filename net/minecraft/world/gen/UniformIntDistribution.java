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

public class UniformIntDistribution {
    public static final Codec<UniformIntDistribution> CODEC = Codec.either(Codec.INT, RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("base")).forGetter(uniformIntDistribution -> uniformIntDistribution.base), ((MapCodec)Codec.INT.fieldOf("spread")).forGetter(uniformIntDistribution -> uniformIntDistribution.spread)).apply((Applicative<UniformIntDistribution, ?>)instance, UniformIntDistribution::new)).comapFlatMap(uniformIntDistribution -> {
        if (uniformIntDistribution.spread < 0) {
            return DataResult.error("Spread must be non-negative, got: " + uniformIntDistribution.spread);
        }
        return DataResult.success(uniformIntDistribution);
    }, Function.identity())).xmap(either -> either.map(UniformIntDistribution::of, uniformIntDistribution -> uniformIntDistribution), uniformIntDistribution -> uniformIntDistribution.spread == 0 ? Either.left(uniformIntDistribution.base) : Either.right(uniformIntDistribution));
    private final int base;
    private final int spread;

    public static Codec<UniformIntDistribution> createValidatedCodec(int minBase, int maxBase, int maxSpread) {
        Function<UniformIntDistribution, DataResult> function = uniformIntDistribution -> {
            if (uniformIntDistribution.base >= minBase && uniformIntDistribution.base <= maxBase) {
                if (uniformIntDistribution.spread <= maxSpread) {
                    return DataResult.success(uniformIntDistribution);
                }
                return DataResult.error("Spread too big: " + uniformIntDistribution.spread + " > " + maxSpread);
            }
            return DataResult.error("Base value out of range: " + uniformIntDistribution.base + " [" + minBase + "-" + maxBase + "]");
        };
        return CODEC.flatXmap(function, function);
    }

    private UniformIntDistribution(int base, int spread) {
        this.base = base;
        this.spread = spread;
    }

    /**
     * Creates a distribution with a constant value.
     * 
     * @param value the constant value
     */
    public static UniformIntDistribution of(int value) {
        return new UniformIntDistribution(value, 0);
    }

    public static UniformIntDistribution of(int base, int spread) {
        return new UniformIntDistribution(base, spread);
    }

    public int getValue(Random random) {
        if (this.spread == 0) {
            return this.base;
        }
        return this.base + random.nextInt(this.spread + 1);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        UniformIntDistribution uniformIntDistribution = (UniformIntDistribution)object;
        return this.base == uniformIntDistribution.base && this.spread == uniformIntDistribution.spread;
    }

    public int hashCode() {
        return Objects.hash(this.base, this.spread);
    }

    public String toString() {
        return "[" + this.base + '-' + (this.base + this.spread) + ']';
    }
}

