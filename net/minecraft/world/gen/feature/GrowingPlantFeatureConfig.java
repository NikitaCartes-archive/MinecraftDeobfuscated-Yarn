/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class GrowingPlantFeatureConfig
implements FeatureConfig {
    public static final Codec<GrowingPlantFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)WeightedList.createCodec(UniformIntDistribution.CODEC).fieldOf("height_distribution")).forGetter(growingPlantFeatureConfig -> growingPlantFeatureConfig.heightDistribution), ((MapCodec)Direction.field_29502.fieldOf("direction")).forGetter(growingPlantFeatureConfig -> growingPlantFeatureConfig.direction), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("body_provider")).forGetter(growingPlantFeatureConfig -> growingPlantFeatureConfig.bodyProvider), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("head_provider")).forGetter(growingPlantFeatureConfig -> growingPlantFeatureConfig.headProvider), ((MapCodec)Codec.BOOL.fieldOf("allow_water")).forGetter(growingPlantFeatureConfig -> growingPlantFeatureConfig.allowWater)).apply((Applicative<GrowingPlantFeatureConfig, ?>)instance, GrowingPlantFeatureConfig::new));
    public final WeightedList<UniformIntDistribution> heightDistribution;
    public final Direction direction;
    public final BlockStateProvider bodyProvider;
    public final BlockStateProvider headProvider;
    public final boolean allowWater;

    public GrowingPlantFeatureConfig(WeightedList<UniformIntDistribution> heightDistribution, Direction direction, BlockStateProvider bodyProvider, BlockStateProvider headProvider, boolean allowWater) {
        this.heightDistribution = heightDistribution;
        this.direction = direction;
        this.bodyProvider = bodyProvider;
        this.headProvider = headProvider;
        this.allowWater = allowWater;
    }
}

