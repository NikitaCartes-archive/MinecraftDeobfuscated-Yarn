package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class DefaultFlowerFeature extends FlowerFeature<FlowerFeatureConfig> {
	public DefaultFlowerFeature(Function<Dynamic<?>, ? extends FlowerFeatureConfig> configFactory) {
		super(configFactory);
	}

	public boolean method_23390(IWorld iWorld, BlockPos blockPos, FlowerFeatureConfig flowerFeatureConfig) {
		return !flowerFeatureConfig.field_21240.contains(iWorld.getBlockState(blockPos));
	}

	public int method_23391(FlowerFeatureConfig flowerFeatureConfig) {
		return flowerFeatureConfig.field_21241;
	}

	public BlockPos method_23392(Random random, BlockPos blockPos, FlowerFeatureConfig flowerFeatureConfig) {
		return blockPos.add(
			random.nextInt(flowerFeatureConfig.field_21242) - random.nextInt(flowerFeatureConfig.field_21242),
			random.nextInt(flowerFeatureConfig.field_21243) - random.nextInt(flowerFeatureConfig.field_21243),
			random.nextInt(flowerFeatureConfig.field_21244) - random.nextInt(flowerFeatureConfig.field_21244)
		);
	}

	public BlockState method_23393(Random random, BlockPos blockPos, FlowerFeatureConfig flowerFeatureConfig) {
		return flowerFeatureConfig.field_21237.getBlockState(random, blockPos);
	}
}
