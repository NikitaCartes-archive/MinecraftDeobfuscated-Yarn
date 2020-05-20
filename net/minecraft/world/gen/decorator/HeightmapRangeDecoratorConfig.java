/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class HeightmapRangeDecoratorConfig
implements DecoratorConfig {
    public static final Codec<HeightmapRangeDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("min")).forGetter(heightmapRangeDecoratorConfig -> heightmapRangeDecoratorConfig.min), ((MapCodec)Codec.INT.fieldOf("max")).forGetter(heightmapRangeDecoratorConfig -> heightmapRangeDecoratorConfig.max)).apply((Applicative<HeightmapRangeDecoratorConfig, ?>)instance, HeightmapRangeDecoratorConfig::new));
    public final int min;
    public final int max;

    public HeightmapRangeDecoratorConfig(int min, int max) {
        this.min = min;
        this.max = max;
    }
}

