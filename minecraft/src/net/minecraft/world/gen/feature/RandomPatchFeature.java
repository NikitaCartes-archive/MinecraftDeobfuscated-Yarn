package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RandomPatchFeature extends Feature<RandomPatchFeatureConfig> {
	public RandomPatchFeature(Codec<RandomPatchFeatureConfig> codec) {
		super(codec);
	}

	public boolean method_23401(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig
	) {
		BlockState blockState = randomPatchFeatureConfig.stateProvider.getBlockState(random, blockPos);
		BlockPos blockPos2;
		if (randomPatchFeatureConfig.project) {
			blockPos2 = structureWorldAccess.getTopPosition(Heightmap.Type.field_13194, blockPos);
		} else {
			blockPos2 = blockPos;
		}

		int i = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = 0; j < randomPatchFeatureConfig.tries; j++) {
			mutable.set(
				blockPos2,
				random.nextInt(randomPatchFeatureConfig.spreadX + 1) - random.nextInt(randomPatchFeatureConfig.spreadX + 1),
				random.nextInt(randomPatchFeatureConfig.spreadY + 1) - random.nextInt(randomPatchFeatureConfig.spreadY + 1),
				random.nextInt(randomPatchFeatureConfig.spreadZ + 1) - random.nextInt(randomPatchFeatureConfig.spreadZ + 1)
			);
			BlockPos blockPos3 = mutable.method_10074();
			BlockState blockState2 = structureWorldAccess.getBlockState(blockPos3);
			if ((structureWorldAccess.isAir(mutable) || randomPatchFeatureConfig.canReplace && structureWorldAccess.getBlockState(mutable).getMaterial().isReplaceable())
				&& blockState.canPlaceAt(structureWorldAccess, mutable)
				&& (randomPatchFeatureConfig.whitelist.isEmpty() || randomPatchFeatureConfig.whitelist.contains(blockState2.getBlock()))
				&& !randomPatchFeatureConfig.blacklist.contains(blockState2)
				&& (
					!randomPatchFeatureConfig.needsWater
						|| structureWorldAccess.getFluidState(blockPos3.west()).isIn(FluidTags.field_15517)
						|| structureWorldAccess.getFluidState(blockPos3.east()).isIn(FluidTags.field_15517)
						|| structureWorldAccess.getFluidState(blockPos3.north()).isIn(FluidTags.field_15517)
						|| structureWorldAccess.getFluidState(blockPos3.south()).isIn(FluidTags.field_15517)
				)) {
				randomPatchFeatureConfig.blockPlacer.method_23403(structureWorldAccess, mutable, blockState, random);
				i++;
			}
		}

		return i > 0;
	}
}
