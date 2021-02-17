/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class BiasedRangedDecoratorConfig
implements DecoratorConfig {
    public static final Codec<BiasedRangedDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)YOffset.OFFSET_CODEC.fieldOf("bottom_inclusive")).forGetter(BiasedRangedDecoratorConfig::getBottom), ((MapCodec)YOffset.OFFSET_CODEC.fieldOf("top_inclusive")).forGetter(BiasedRangedDecoratorConfig::getTop), ((MapCodec)Codec.INT.fieldOf("cutoff")).forGetter(BiasedRangedDecoratorConfig::getCutoff)).apply((Applicative<BiasedRangedDecoratorConfig, ?>)instance, BiasedRangedDecoratorConfig::new));
    private final YOffset bottom;
    private final YOffset top;
    private final int cutoff;

    public BiasedRangedDecoratorConfig(YOffset bottom, YOffset top, int cutoff) {
        this.bottom = bottom;
        this.cutoff = cutoff;
        this.top = top;
    }

    public YOffset getBottom() {
        return this.bottom;
    }

    public int getCutoff() {
        return this.cutoff;
    }

    public YOffset getTop() {
        return this.top;
    }
}

