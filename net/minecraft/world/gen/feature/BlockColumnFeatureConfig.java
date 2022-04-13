/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record BlockColumnFeatureConfig(List<Layer> layers, Direction direction, BlockPredicate allowedPlacement, boolean prioritizeTip) implements FeatureConfig
{
    public static final Codec<BlockColumnFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Layer.CODEC.listOf().fieldOf("layers")).forGetter(BlockColumnFeatureConfig::layers), ((MapCodec)Direction.CODEC.fieldOf("direction")).forGetter(BlockColumnFeatureConfig::direction), ((MapCodec)BlockPredicate.BASE_CODEC.fieldOf("allowed_placement")).forGetter(BlockColumnFeatureConfig::allowedPlacement), ((MapCodec)Codec.BOOL.fieldOf("prioritize_tip")).forGetter(BlockColumnFeatureConfig::prioritizeTip)).apply((Applicative<BlockColumnFeatureConfig, ?>)instance, BlockColumnFeatureConfig::new));

    public static Layer createLayer(IntProvider height, BlockStateProvider state) {
        return new Layer(height, state);
    }

    public static BlockColumnFeatureConfig create(IntProvider height, BlockStateProvider state) {
        return new BlockColumnFeatureConfig(List.of(BlockColumnFeatureConfig.createLayer(height, state)), Direction.UP, BlockPredicate.IS_AIR, false);
    }

    public record Layer(IntProvider height, BlockStateProvider state) {
        public static final Codec<Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)IntProvider.NON_NEGATIVE_CODEC.fieldOf("height")).forGetter(Layer::height), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("provider")).forGetter(Layer::state)).apply((Applicative<Layer, ?>)instance, Layer::new));
    }
}

