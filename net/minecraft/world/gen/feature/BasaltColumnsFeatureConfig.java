/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.FeatureConfig;

public class BasaltColumnsFeatureConfig
implements FeatureConfig {
    public static final Codec<BasaltColumnsFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)UniformIntDistribution.createValidatedCodec(0, 2, 1).fieldOf("reach")).forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.reach), ((MapCodec)UniformIntDistribution.createValidatedCodec(1, 5, 5).fieldOf("height")).forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.height)).apply((Applicative<BasaltColumnsFeatureConfig, ?>)instance, BasaltColumnsFeatureConfig::new));
    private final UniformIntDistribution reach;
    private final UniformIntDistribution height;

    public BasaltColumnsFeatureConfig(UniformIntDistribution reach, UniformIntDistribution height) {
        this.reach = reach;
        this.height = height;
    }

    public UniformIntDistribution getReach() {
        return this.reach;
    }

    public UniformIntDistribution getHeight() {
        return this.height;
    }
}

