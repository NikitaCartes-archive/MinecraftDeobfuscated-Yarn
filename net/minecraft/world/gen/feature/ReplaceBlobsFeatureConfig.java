/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ReplaceBlobsFeatureConfig
implements FeatureConfig {
    public static final Codec<ReplaceBlobsFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockState.CODEC.fieldOf("target")).forGetter(replaceBlobsFeatureConfig -> replaceBlobsFeatureConfig.target), ((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(replaceBlobsFeatureConfig -> replaceBlobsFeatureConfig.state), ((MapCodec)IntProvider.VALUE_CODEC.fieldOf("radius")).forGetter(replaceBlobsFeatureConfig -> replaceBlobsFeatureConfig.radius)).apply((Applicative<ReplaceBlobsFeatureConfig, ?>)instance, ReplaceBlobsFeatureConfig::new));
    public final BlockState target;
    public final BlockState state;
    private final IntProvider radius;

    public ReplaceBlobsFeatureConfig(BlockState target, BlockState state, IntProvider radius) {
        this.target = target;
        this.state = state;
        this.radius = radius;
    }

    public IntProvider getRadius() {
        return this.radius;
    }
}

