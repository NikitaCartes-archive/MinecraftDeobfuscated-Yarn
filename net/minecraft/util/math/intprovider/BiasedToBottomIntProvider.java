/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.intprovider;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.math.random.AbstractRandom;

public class BiasedToBottomIntProvider
extends IntProvider {
    public static final Codec<BiasedToBottomIntProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("min_inclusive")).forGetter(provider -> provider.min), ((MapCodec)Codec.INT.fieldOf("max_inclusive")).forGetter(provider -> provider.max)).apply((Applicative<BiasedToBottomIntProvider, ?>)instance, BiasedToBottomIntProvider::new)).comapFlatMap(provider -> {
        if (provider.max < provider.min) {
            return DataResult.error("Max must be at least min, min_inclusive: " + provider.min + ", max_inclusive: " + provider.max);
        }
        return DataResult.success(provider);
    }, Function.identity());
    private final int min;
    private final int max;

    private BiasedToBottomIntProvider(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * @param max the maximum value, inclusive
     * @param min the minimum value, inclusive
     */
    public static BiasedToBottomIntProvider create(int min, int max) {
        return new BiasedToBottomIntProvider(min, max);
    }

    @Override
    public int get(AbstractRandom random) {
        return this.min + random.nextInt(random.nextInt(this.max - this.min + 1) + 1);
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
        return IntProviderType.BIASED_TO_BOTTOM;
    }

    public String toString() {
        return "[" + this.min + "-" + this.max + "]";
    }
}

