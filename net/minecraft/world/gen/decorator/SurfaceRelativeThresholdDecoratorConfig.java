/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class SurfaceRelativeThresholdDecoratorConfig
implements DecoratorConfig {
    public static final Codec<SurfaceRelativeThresholdDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Heightmap.Type.CODEC.fieldOf("heightmap")).forGetter(surfaceRelativeThresholdDecoratorConfig -> surfaceRelativeThresholdDecoratorConfig.heightmap), Codec.INT.optionalFieldOf("min_inclusive", Integer.MIN_VALUE).forGetter(surfaceRelativeThresholdDecoratorConfig -> surfaceRelativeThresholdDecoratorConfig.min), Codec.INT.optionalFieldOf("max_inclusive", Integer.MAX_VALUE).forGetter(surfaceRelativeThresholdDecoratorConfig -> surfaceRelativeThresholdDecoratorConfig.max)).apply((Applicative<SurfaceRelativeThresholdDecoratorConfig, ?>)instance, SurfaceRelativeThresholdDecoratorConfig::new));
    public final Heightmap.Type heightmap;
    public final int min;
    public final int max;

    public SurfaceRelativeThresholdDecoratorConfig(Heightmap.Type heightmap, int min, int max) {
        this.heightmap = heightmap;
        this.min = min;
        this.max = max;
    }
}

