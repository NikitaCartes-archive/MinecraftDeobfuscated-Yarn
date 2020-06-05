/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class EmeraldOreFeatureConfig
implements FeatureConfig {
    public static final Codec<EmeraldOreFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockState.CODEC.fieldOf("target")).forGetter(emeraldOreFeatureConfig -> emeraldOreFeatureConfig.target), ((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(emeraldOreFeatureConfig -> emeraldOreFeatureConfig.state)).apply((Applicative<EmeraldOreFeatureConfig, ?>)instance, EmeraldOreFeatureConfig::new));
    public final BlockState target;
    public final BlockState state;

    public EmeraldOreFeatureConfig(BlockState target, BlockState state) {
        this.target = target;
        this.state = state;
    }
}

