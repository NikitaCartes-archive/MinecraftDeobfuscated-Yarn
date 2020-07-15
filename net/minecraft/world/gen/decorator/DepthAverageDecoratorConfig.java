/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class DepthAverageDecoratorConfig
implements DecoratorConfig {
    public static final Codec<DepthAverageDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("baseline")).forGetter(depthAverageDecoratorConfig -> depthAverageDecoratorConfig.count), ((MapCodec)Codec.INT.fieldOf("spread")).forGetter(depthAverageDecoratorConfig -> depthAverageDecoratorConfig.spread)).apply((Applicative<DepthAverageDecoratorConfig, ?>)instance, DepthAverageDecoratorConfig::new));
    public final int count;
    public final int spread;

    public DepthAverageDecoratorConfig(int count, int baseline) {
        this.count = count;
        this.spread = baseline;
    }
}

