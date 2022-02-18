/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class NetherForestVegetationFeatureConfig
extends BlockPileFeatureConfig {
    public static final Codec<NetherForestVegetationFeatureConfig> VEGETATION_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("state_provider")).forGetter(config -> config.stateProvider), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("spread_width")).forGetter(config -> config.spreadWidth), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("spread_height")).forGetter(config -> config.spreadHeight)).apply((Applicative<NetherForestVegetationFeatureConfig, ?>)instance, NetherForestVegetationFeatureConfig::new));
    public final int spreadWidth;
    public final int spreadHeight;

    public NetherForestVegetationFeatureConfig(BlockStateProvider stateProvider, int spreadWidth, int spreadHeight) {
        super(stateProvider);
        this.spreadWidth = spreadWidth;
        this.spreadHeight = spreadHeight;
    }
}

