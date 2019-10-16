package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FlowerFeatureConfig;

public class class_4628 extends Feature<FlowerFeatureConfig> {
	public class_4628(Function<Dynamic<?>, ? extends FlowerFeatureConfig> function) {
		super(function);
	}

	public boolean method_23401(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, FlowerFeatureConfig flowerFeatureConfig
	) {
		BlockState blockState = flowerFeatureConfig.field_21237.getBlockState(random, blockPos);
		BlockPos blockPos2;
		if (flowerFeatureConfig.field_21246) {
			blockPos2 = iWorld.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, blockPos);
		} else {
			blockPos2 = blockPos;
		}

		int i = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = 0; j < flowerFeatureConfig.field_21241; j++) {
			mutable.set(blockPos2)
				.setOffset(
					random.nextInt(flowerFeatureConfig.field_21242 + 1) - random.nextInt(flowerFeatureConfig.field_21242 + 1),
					random.nextInt(flowerFeatureConfig.field_21243 + 1) - random.nextInt(flowerFeatureConfig.field_21243 + 1),
					random.nextInt(flowerFeatureConfig.field_21244 + 1) - random.nextInt(flowerFeatureConfig.field_21244 + 1)
				);
			BlockPos blockPos3 = mutable.method_10074();
			BlockState blockState2 = iWorld.getBlockState(blockPos3);
			if ((iWorld.isAir(mutable) || flowerFeatureConfig.field_21245 && iWorld.getBlockState(mutable).getMaterial().isReplaceable())
				&& blockState.canPlaceAt(iWorld, mutable)
				&& (flowerFeatureConfig.field_21239.isEmpty() || flowerFeatureConfig.field_21239.contains(blockState2.getBlock()))
				&& !flowerFeatureConfig.field_21240.contains(blockState2)
				&& (
					!flowerFeatureConfig.field_21247
						|| iWorld.getFluidState(blockPos3.west()).matches(FluidTags.WATER)
						|| iWorld.getFluidState(blockPos3.east()).matches(FluidTags.WATER)
						|| iWorld.getFluidState(blockPos3.north()).matches(FluidTags.WATER)
						|| iWorld.getFluidState(blockPos3.south()).matches(FluidTags.WATER)
				)) {
				flowerFeatureConfig.field_21238.method_23403(iWorld, mutable, blockState, random);
				i++;
			}
		}

		return i > 0;
	}
}
