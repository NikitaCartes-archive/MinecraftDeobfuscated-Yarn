/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public class BasaltColumnsFeatureConfig
implements FeatureConfig {
    public static final Codec<BasaltColumnsFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)IntProvider.createValidatingCodec(0, 3).fieldOf("reach")).forGetter(config -> config.reach), ((MapCodec)IntProvider.createValidatingCodec(1, 10).fieldOf("height")).forGetter(config -> config.height)).apply((Applicative<BasaltColumnsFeatureConfig, ?>)instance, BasaltColumnsFeatureConfig::new));
    private final IntProvider reach;
    private final IntProvider height;

    public BasaltColumnsFeatureConfig(IntProvider reach, IntProvider height) {
        this.reach = reach;
        this.height = height;
    }

    public IntProvider getReach() {
        return this.reach;
    }

    public IntProvider getHeight() {
        return this.height;
    }
}

