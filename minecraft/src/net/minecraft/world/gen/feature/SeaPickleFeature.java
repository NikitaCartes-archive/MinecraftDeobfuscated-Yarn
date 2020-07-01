package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SeaPickleFeature extends Feature<SeaPickleFeatureConfig> {
	public SeaPickleFeature(Codec<SeaPickleFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SeaPickleFeatureConfig seaPickleFeatureConfig
	) {
		int i = 0;

		for (int j = 0; j < seaPickleFeatureConfig.count; j++) {
			int k = random.nextInt(8) - random.nextInt(8);
			int l = random.nextInt(8) - random.nextInt(8);
			int m = serverWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX() + k, blockPos.getZ() + l);
			BlockPos blockPos2 = new BlockPos(blockPos.getX() + k, m, blockPos.getZ() + l);
			BlockState blockState = Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, Integer.valueOf(random.nextInt(4) + 1));
			if (serverWorldAccess.getBlockState(blockPos2).isOf(Blocks.WATER) && blockState.canPlaceAt(serverWorldAccess, blockPos2)) {
				serverWorldAccess.setBlockState(blockPos2, blockState, 2);
				i++;
			}
		}

		return i > 0;
	}
}
