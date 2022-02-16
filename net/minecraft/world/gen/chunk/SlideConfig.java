/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

public record SlideConfig(double target, int size, int offset) {
    public static final Codec<SlideConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.DOUBLE.fieldOf("target")).forGetter(slideConfig -> slideConfig.target), ((MapCodec)Codecs.NONNEGATIVE_INT.fieldOf("size")).forGetter(slideConfig -> slideConfig.size), ((MapCodec)Codec.INT.fieldOf("offset")).forGetter(slideConfig -> slideConfig.offset)).apply((Applicative<SlideConfig, ?>)instance, SlideConfig::new));

    public double method_38414(double d, double e) {
        if (this.size <= 0) {
            return d;
        }
        double f = (e - (double)this.offset) / (double)this.size;
        return MathHelper.clampedLerp(this.target, d, f);
    }
}

