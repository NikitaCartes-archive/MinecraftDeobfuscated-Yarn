/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.floatprovider;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.FloatProviderType;

public class UniformFloatProvider
extends FloatProvider {
    public static final Codec<UniformFloatProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.FLOAT.fieldOf("min_inclusive")).forGetter(uniformFloatProvider -> Float.valueOf(uniformFloatProvider.base)), ((MapCodec)Codec.FLOAT.fieldOf("max_exclusive")).forGetter(uniformFloatProvider -> Float.valueOf(uniformFloatProvider.spread))).apply((Applicative<UniformFloatProvider, ?>)instance, UniformFloatProvider::new)).comapFlatMap(uniformFloatProvider -> {
        if (uniformFloatProvider.spread <= uniformFloatProvider.base) {
            return DataResult.error("Max must be larger than min, min_inclusive: " + uniformFloatProvider.base + ", max_exclusive: " + uniformFloatProvider.spread);
        }
        return DataResult.success(uniformFloatProvider);
    }, Function.identity());
    private final float base;
    private final float spread;

    private UniformFloatProvider(float base, float spread) {
        this.base = base;
        this.spread = spread;
    }

    public static UniformFloatProvider create(float base, float spread) {
        if (spread <= base) {
            throw new IllegalArgumentException("Max must exceed min");
        }
        return new UniformFloatProvider(base, spread);
    }

    @Override
    public float get(Random random) {
        return MathHelper.nextBetween(random, this.base, this.spread);
    }

    @Override
    public float getMin() {
        return this.base;
    }

    @Override
    public float getMax() {
        return this.spread;
    }

    @Override
    public FloatProviderType<?> getType() {
        return FloatProviderType.UNIFORM;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        UniformFloatProvider uniformFloatProvider = (UniformFloatProvider)object;
        return this.base == uniformFloatProvider.base && this.spread == uniformFloatProvider.spread;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.base), Float.valueOf(this.spread));
    }

    public String toString() {
        return "[" + this.base + '-' + this.spread + ']';
    }
}

