/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.FeatureConfig;

public class FillLayerFeatureConfig
implements FeatureConfig {
    public static final Codec<FillLayerFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.intRange(DimensionType.field_28136, DimensionType.field_28135).fieldOf("height")).forGetter(fillLayerFeatureConfig -> fillLayerFeatureConfig.height), ((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(fillLayerFeatureConfig -> fillLayerFeatureConfig.state)).apply((Applicative<FillLayerFeatureConfig, ?>)instance, FillLayerFeatureConfig::new));
    public final int height;
    public final BlockState state;

    public FillLayerFeatureConfig(int height, BlockState state) {
        this.height = height;
        this.state = state;
    }
}

