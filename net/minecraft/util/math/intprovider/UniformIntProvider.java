/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.intprovider;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;

public class UniformIntProvider
extends IntProvider {
    public static final Codec<UniformIntProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("min_inclusive")).forGetter(provider -> provider.min), ((MapCodec)Codec.INT.fieldOf("max_inclusive")).forGetter(provider -> provider.max)).apply((Applicative<UniformIntProvider, ?>)instance, UniformIntProvider::new)).comapFlatMap(provider -> {
        if (provider.max < provider.min) {
            return DataResult.error("Max must be at least min, min_inclusive: " + provider.min + ", max_inclusive: " + provider.max);
        }
        return DataResult.success(provider);
    }, Function.identity());
    private final int min;
    private final int max;

    private UniformIntProvider(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static UniformIntProvider create(int min, int max) {
        return new UniformIntProvider(min, max);
    }

    @Override
    public int get(Random random) {
        return MathHelper.nextBetween(random, this.min, this.max);
    }

    @Override
    public int getMin() {
        return this.min;
    }

    @Override
    public int getMax() {
        return this.max;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.UNIFORM;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        UniformIntProvider uniformIntProvider = (UniformIntProvider)object;
        return this.min == uniformIntProvider.min && this.max == uniformIntProvider.max;
    }

    public int hashCode() {
        return Objects.hash(this.min, this.max);
    }

    public String toString() {
        return "[" + this.min + '-' + this.max + ']';
    }
}

