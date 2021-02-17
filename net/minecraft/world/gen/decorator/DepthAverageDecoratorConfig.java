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

public class DepthAverageDecoratorConfig
implements DecoratorConfig {
    public static final Codec<DepthAverageDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)YOffset.OFFSET_CODEC.fieldOf("baseline")).forGetter(DepthAverageDecoratorConfig::getBaseline), ((MapCodec)Codec.INT.fieldOf("spread")).forGetter(DepthAverageDecoratorConfig::getSpread)).apply((Applicative<DepthAverageDecoratorConfig, ?>)instance, DepthAverageDecoratorConfig::new));
    private final YOffset baseline;
    private final int spread;

    public DepthAverageDecoratorConfig(YOffset baseline, int spread) {
        this.baseline = baseline;
        this.spread = spread;
    }

    public YOffset getBaseline() {
        return this.baseline;
    }

    public int getSpread() {
        return this.spread;
    }
}

