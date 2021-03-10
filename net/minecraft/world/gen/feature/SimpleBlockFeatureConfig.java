/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class SimpleBlockFeatureConfig
implements FeatureConfig {
    public static final Codec<SimpleBlockFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("to_place")).forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.toPlace), ((MapCodec)BlockState.CODEC.listOf().fieldOf("place_on")).orElse(ImmutableList.of()).forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeOn), ((MapCodec)BlockState.CODEC.listOf().fieldOf("place_in")).orElse(ImmutableList.of()).forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeIn), ((MapCodec)BlockState.CODEC.listOf().fieldOf("place_under")).orElse(ImmutableList.of()).forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeUnder)).apply((Applicative<SimpleBlockFeatureConfig, ?>)instance, SimpleBlockFeatureConfig::new));
    public final BlockStateProvider toPlace;
    public final List<BlockState> placeOn;
    public final List<BlockState> placeIn;
    public final List<BlockState> placeUnder;

    public SimpleBlockFeatureConfig(BlockStateProvider blockStateProvider, List<BlockState> placeOn, List<BlockState> placeIn, List<BlockState> placeUnder) {
        this.toPlace = blockStateProvider;
        this.placeOn = placeOn;
        this.placeIn = placeIn;
        this.placeUnder = placeUnder;
    }

    public SimpleBlockFeatureConfig(BlockStateProvider blockStateProvider) {
        this(blockStateProvider, ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
    }
}

