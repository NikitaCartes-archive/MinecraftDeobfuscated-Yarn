package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RandomPatchFeature extends Feature<RandomPatchFeatureConfig> {
	public RandomPatchFeature(Codec<RandomPatchFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<RandomPatchFeatureConfig> featureContext) {
		RandomPatchFeatureConfig randomPatchFeatureConfig = featureContext.getConfig();
		Random random = featureContext.getRandom();
		BlockPos blockPos = featureContext.getPos();
		StructureWorldAccess structureWorldAccess = featureContext.getWorld();
		BlockState blockState = randomPatchFeatureConfig.stateProvider.getBlockState(random, blockPos);
		BlockPos blockPos2;
		if (randomPatchFeatureConfig.project) {
			blockPos2 = structureWorldAccess.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, blockPos);
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
			BlockPos blockPos3 = mutable.down();
			BlockState blockState2 = structureWorldAccess.getBlockState(blockPos3);
			if ((structureWorldAccess.isAir(mutable) || randomPatchFeatureConfig.canReplace && structureWorldAccess.getBlockState(mutable).getMaterial().isReplaceable())
				&& blockState.canPlaceAt(structureWorldAccess, mutable)
				&& (randomPatchFeatureConfig.whitelist.isEmpty() || randomPatchFeatureConfig.whitelist.contains(blockState2.getBlock()))
				&& !randomPatchFeatureConfig.blacklist.contains(blockState2)
				&& (
					!randomPatchFeatureConfig.needsWater
						|| structureWorldAccess.getFluidState(blockPos3.west()).isIn(FluidTags.WATER)
						|| structureWorldAccess.getFluidState(blockPos3.east()).isIn(FluidTags.WATER)
						|| structureWorldAccess.getFluidState(blockPos3.north()).isIn(FluidTags.WATER)
						|| structureWorldAccess.getFluidState(blockPos3.south()).isIn(FluidTags.WATER)
				)) {
				randomPatchFeatureConfig.blockPlacer.generate(structureWorldAccess, mutable, blockState, random);
				i++;
			}
		}

		return i > 0;
	}
}
