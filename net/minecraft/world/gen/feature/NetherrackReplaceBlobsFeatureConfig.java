/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.FeatureConfig;

public class NetherrackReplaceBlobsFeatureConfig
implements FeatureConfig {
    public static final Codec<NetherrackReplaceBlobsFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockState.CODEC.fieldOf("target")).forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.target), ((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.state), ((MapCodec)UniformIntDistribution.CODEC.fieldOf("radius")).forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.radius)).apply((Applicative<NetherrackReplaceBlobsFeatureConfig, ?>)instance, NetherrackReplaceBlobsFeatureConfig::new));
    public final BlockState target;
    public final BlockState state;
    private final UniformIntDistribution radius;

    public NetherrackReplaceBlobsFeatureConfig(BlockState target, BlockState state, UniformIntDistribution radius) {
        this.target = target;
        this.state = state;
        this.radius = radius;
    }

    public UniformIntDistribution getRadius() {
        return this.radius;
    }
}

