/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SimpleBlockFeatureConfig
implements FeatureConfig {
    public static final Codec<SimpleBlockFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockState.CODEC.fieldOf("to_place")).forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.toPlace), ((MapCodec)BlockState.CODEC.listOf().fieldOf("place_on")).forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeOn), ((MapCodec)BlockState.CODEC.listOf().fieldOf("place_in")).forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeIn), ((MapCodec)BlockState.CODEC.listOf().fieldOf("place_under")).forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeUnder)).apply((Applicative<SimpleBlockFeatureConfig, ?>)instance, SimpleBlockFeatureConfig::new));
    public final BlockState toPlace;
    public final List<BlockState> placeOn;
    public final List<BlockState> placeIn;
    public final List<BlockState> placeUnder;

    public SimpleBlockFeatureConfig(BlockState blockState, List<BlockState> list, List<BlockState> list2, List<BlockState> list3) {
        this.toPlace = blockState;
        this.placeOn = list;
        this.placeIn = list2;
        this.placeUnder = list3;
    }
}

