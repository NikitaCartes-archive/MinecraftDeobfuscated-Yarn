package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.KelpBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class KelpFeature extends Feature<DefaultFeatureConfig> {
	public KelpFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		DefaultFeatureConfig defaultFeatureConfig
	) {
		int i = 0;
		int j = serverWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX(), blockPos.getZ());
		BlockPos blockPos2 = new BlockPos(blockPos.getX(), j, blockPos.getZ());
		if (serverWorldAccess.getBlockState(blockPos2).isOf(Blocks.WATER)) {
			BlockState blockState = Blocks.KELP.getDefaultState();
			BlockState blockState2 = Blocks.KELP_PLANT.getDefaultState();
			int k = 1 + random.nextInt(10);

			for (int l = 0; l <= k; l++) {
				if (serverWorldAccess.getBlockState(blockPos2).isOf(Blocks.WATER)
					&& serverWorldAccess.getBlockState(blockPos2.up()).isOf(Blocks.WATER)
					&& blockState2.canPlaceAt(serverWorldAccess, blockPos2)) {
					if (l == k) {
						serverWorldAccess.setBlockState(blockPos2, blockState.with(KelpBlock.AGE, Integer.valueOf(random.nextInt(4) + 20)), 2);
						i++;
					} else {
						serverWorldAccess.setBlockState(blockPos2, blockState2, 2);
					}
				} else if (l > 0) {
					BlockPos blockPos3 = blockPos2.down();
					if (blockState.canPlaceAt(serverWorldAccess, blockPos3) && !serverWorldAccess.getBlockState(blockPos3.down()).isOf(Blocks.KELP)) {
						serverWorldAccess.setBlockState(blockPos3, blockState.with(KelpBlock.AGE, Integer.valueOf(random.nextInt(4) + 20)), 2);
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
