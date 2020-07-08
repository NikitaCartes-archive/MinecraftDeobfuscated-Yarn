package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SeagrassFeature extends Feature<ProbabilityConfig> {
	public SeagrassFeature(Codec<ProbabilityConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, ProbabilityConfig probabilityConfig
	) {
		boolean bl = false;
		int i = random.nextInt(8) - random.nextInt(8);
		int j = random.nextInt(8) - random.nextInt(8);
		int k = serverWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX() + i, blockPos.getZ() + j);
		BlockPos blockPos2 = new BlockPos(blockPos.getX() + i, k, blockPos.getZ() + j);
		if (serverWorldAccess.getBlockState(blockPos2).isOf(Blocks.WATER)) {
			boolean bl2 = random.nextDouble() < (double)probabilityConfig.probability;
			BlockState blockState = bl2 ? Blocks.TALL_SEAGRASS.getDefaultState() : Blocks.SEAGRASS.getDefaultState();
			if (blockState.canPlaceAt(serverWorldAccess, blockPos2)) {
				if (bl2) {
					BlockState blockState2 = blockState.with(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
					BlockPos blockPos3 = blockPos2.up();
					if (serverWorldAccess.getBlockState(blockPos3).isOf(Blocks.WATER)) {
						serverWorldAccess.setBlockState(blockPos2, blockState, 2);
						serverWorldAccess.setBlockState(blockPos3, blockState2, 2);
					}
				} else {
					serverWorldAccess.setBlockState(blockPos2, blockState, 2);
				}

				bl = true;
			}
		}

		return bl;
	}
}
