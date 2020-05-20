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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TopSolidHeightmapNoiseBiasedDecoratorConfig
implements DecoratorConfig {
    public static final Codec<TopSolidHeightmapNoiseBiasedDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("noise_to_count_ratio")).forGetter(topSolidHeightmapNoiseBiasedDecoratorConfig -> topSolidHeightmapNoiseBiasedDecoratorConfig.noiseToCountRatio), ((MapCodec)Codec.DOUBLE.fieldOf("noise_factor")).forGetter(topSolidHeightmapNoiseBiasedDecoratorConfig -> topSolidHeightmapNoiseBiasedDecoratorConfig.noiseFactor), ((MapCodec)Codec.DOUBLE.fieldOf("noise_offset")).withDefault(0.0).forGetter(topSolidHeightmapNoiseBiasedDecoratorConfig -> topSolidHeightmapNoiseBiasedDecoratorConfig.noiseOffset), ((MapCodec)Heightmap.Type.field_24772.fieldOf("heightmap")).forGetter(topSolidHeightmapNoiseBiasedDecoratorConfig -> topSolidHeightmapNoiseBiasedDecoratorConfig.heightmap)).apply((Applicative<TopSolidHeightmapNoiseBiasedDecoratorConfig, ?>)instance, TopSolidHeightmapNoiseBiasedDecoratorConfig::new));
    private static final Logger LOGGER = LogManager.getLogger();
    public final int noiseToCountRatio;
    public final double noiseFactor;
    public final double noiseOffset;
    public final Heightmap.Type heightmap;

    public TopSolidHeightmapNoiseBiasedDecoratorConfig(int noiseToCountRatio, double noiseFactor, double noiseOffset, Heightmap.Type heightmap) {
        this.noiseToCountRatio = noiseToCountRatio;
        this.noiseFactor = noiseFactor;
        this.noiseOffset = noiseOffset;
        this.heightmap = heightmap;
    }
}

