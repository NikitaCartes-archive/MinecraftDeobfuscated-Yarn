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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.math.random.Random;

public class ClampedNormalIntProvider
extends IntProvider {
    public static final Codec<ClampedNormalIntProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.FLOAT.fieldOf("mean")).forGetter(provider -> Float.valueOf(provider.mean)), ((MapCodec)Codec.FLOAT.fieldOf("deviation")).forGetter(provider -> Float.valueOf(provider.deviation)), ((MapCodec)Codec.INT.fieldOf("min_inclusive")).forGetter(provider -> provider.min), ((MapCodec)Codec.INT.fieldOf("max_inclusive")).forGetter(provider -> provider.max)).apply((Applicative<ClampedNormalIntProvider, ?>)instance, ClampedNormalIntProvider::new)).comapFlatMap(provider -> {
        if (provider.max < provider.min) {
            return DataResult.error(() -> "Max must be larger than min: [" + clampedNormalIntProvider.min + ", " + clampedNormalIntProvider.max + "]");
        }
        return DataResult.success(provider);
    }, Function.identity());
    private final float mean;
    private final float deviation;
    private final int min;
    private final int max;

    public static ClampedNormalIntProvider of(float mean, float deviation, int min, int max) {
        return new ClampedNormalIntProvider(mean, deviation, min, max);
    }

    private ClampedNormalIntProvider(float mean, float deviation, int min, int max) {
        this.mean = mean;
        this.deviation = deviation;
        this.min = min;
        this.max = max;
    }

    @Override
    public int get(Random random) {
        return ClampedNormalIntProvider.next(random, this.mean, this.deviation, this.min, this.max);
    }

    public static int next(Random random, float mean, float deviation, float min, float max) {
        return (int)MathHelper.clamp(MathHelper.nextGaussian(random, mean, deviation), min, max);
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
        return IntProviderType.CLAMPED_NORMAL;
    }

    public String toString() {
        return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min + "-" + this.max + "]";
    }
}

