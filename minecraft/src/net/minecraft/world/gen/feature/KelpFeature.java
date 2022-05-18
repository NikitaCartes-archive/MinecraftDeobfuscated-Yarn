package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.KelpBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class KelpFeature extends Feature<DefaultFeatureConfig> {
	public KelpFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		int i = 0;
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		Random random = context.getRandom();
		int j = structureWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX(), blockPos.getZ());
		BlockPos blockPos2 = new BlockPos(blockPos.getX(), j, blockPos.getZ());
		if (structureWorldAccess.getBlockState(blockPos2).isOf(Blocks.WATER)) {
			BlockState blockState = Blocks.KELP.getDefaultState();
			BlockState blockState2 = Blocks.KELP_PLANT.getDefaultState();
			int k = 1 + random.nextInt(10);

			for (int l = 0; l <= k; l++) {
				if (structureWorldAccess.getBlockState(blockPos2).isOf(Blocks.WATER)
					&& structureWorldAccess.getBlockState(blockPos2.up()).isOf(Blocks.WATER)
					&& blockState2.canPlaceAt(structureWorldAccess, blockPos2)) {
					if (l == k) {
						structureWorldAccess.setBlockState(blockPos2, blockState.with(KelpBlock.AGE, Integer.valueOf(random.nextInt(4) + 20)), Block.NOTIFY_LISTENERS);
						i++;
					} else {
						structureWorldAccess.setBlockState(blockPos2, blockState2, Block.NOTIFY_LISTENERS);
					}
				} else if (l > 0) {
					BlockPos blockPos3 = blockPos2.down();
					if (blockState.canPlaceAt(structureWorldAccess, blockPos3) && !structureWorldAccess.getBlockState(blockPos3.down()).isOf(Blocks.KELP)) {
						structureWorldAccess.setBlockState(blockPos3, blockState.with(KelpBlock.AGE, Integer.valueOf(random.nextInt(4) + 20)), Block.NOTIFY_LISTENERS);
						i++;
					}
					break;
				}

				blockPos2 = blockPos2.up();
			}
		}

		return i > 0;
	}
}
