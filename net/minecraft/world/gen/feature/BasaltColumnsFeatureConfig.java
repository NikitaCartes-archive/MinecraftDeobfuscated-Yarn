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
    public static final Codec<BasaltColumnsFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)UniformIntDistribution.createValidatedCodec(0, 2, 1).fieldOf("reach")).forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.field_25841), ((MapCodec)UniformIntDistribution.createValidatedCodec(1, 5, 5).fieldOf("height")).forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.field_25842)).apply((Applicative<BasaltColumnsFeatureConfig, ?>)instance, BasaltColumnsFeatureConfig::new));
    private final UniformIntDistribution field_25841;
    private final UniformIntDistribution field_25842;

    public BasaltColumnsFeatureConfig(UniformIntDistribution uniformIntDistribution, UniformIntDistribution uniformIntDistribution2) {
        this.field_25841 = uniformIntDistribution;
        this.field_25842 = uniformIntDistribution2;
    }

    public UniformIntDistribution method_30391() {
        return this.field_25841;
    }

    public UniformIntDistribution method_30394() {
        return this.field_25842;
    }
}

