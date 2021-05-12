/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.floatprovider;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.FloatProviderType;

public class ClampedNormalFloatProvider
extends FloatProvider {
    public static final Codec<ClampedNormalFloatProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.FLOAT.fieldOf("mean")).forGetter(clampedNormalFloatProvider -> Float.valueOf(clampedNormalFloatProvider.mean)), ((MapCodec)Codec.FLOAT.fieldOf("deviation")).forGetter(clampedNormalFloatProvider -> Float.valueOf(clampedNormalFloatProvider.deviation)), ((MapCodec)Codec.FLOAT.fieldOf("min")).forGetter(clampedNormalFloatProvider -> Float.valueOf(clampedNormalFloatProvider.min)), ((MapCodec)Codec.FLOAT.fieldOf("max")).forGetter(clampedNormalFloatProvider -> Float.valueOf(clampedNormalFloatProvider.max))).apply((Applicative<ClampedNormalFloatProvider, ?>)instance, ClampedNormalFloatProvider::new)).comapFlatMap(clampedNormalFloatProvider -> {
        if (clampedNormalFloatProvider.max < clampedNormalFloatProvider.min) {
            return DataResult.error("Max must be larger than min: [" + clampedNormalFloatProvider.min + ", " + clampedNormalFloatProvider.max + "]");
        }
        return DataResult.success(clampedNormalFloatProvider);
    }, Function.identity());
    private float mean;
    private float deviation;
    private float min;
    private float max;

    public static ClampedNormalFloatProvider create(float mean, float deviation, float min, float max) {
        return new ClampedNormalFloatProvider(mean, deviation, min, max);
    }

    private ClampedNormalFloatProvider(float mean, float deviation, float min, float max) {
        this.mean = mean;
        this.deviation = deviation;
        this.min = min;
        this.max = max;
    }

    @Override
    public float get(Random random) {
        return ClampedNormalFloatProvider.get(random, this.mean, this.deviation, this.min, this.max);
    }

    public static float get(Random random, float mean, float deviation, float min, float max) {
        return MathHelper.clamp(MathHelper.nextGaussian(random, mean, deviation), min, max);
    }

    @Override
    public float getMin() {
        return this.min;
    }

    @Override
    public float getMax() {
        return this.max;
    }

    @Override
    public FloatProviderType<?> getType() {
        return FloatProviderType.CLAMPED_NORMAL;
    }

    public String toString() {
        return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min + "-" + this.max + "]";
    }
}

