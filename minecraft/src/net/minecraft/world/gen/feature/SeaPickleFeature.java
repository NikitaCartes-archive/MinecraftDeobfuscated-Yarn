package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SeaPickleFeature extends Feature<CountConfig> {
	public SeaPickleFeature(Codec<CountConfig> codec) {
		super(codec);
	}

	public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, CountConfig countConfig) {
		int i = 0;
		int j = countConfig.getCount().getValue(random);

		for (int k = 0; k < j; k++) {
			int l = random.nextInt(8) - random.nextInt(8);
			int m = random.nextInt(8) - random.nextInt(8);
			int n = structureWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX() + l, blockPos.getZ() + m);
			BlockPos blockPos2 = new BlockPos(blockPos.getX() + l, n, blockPos.getZ() + m);
			BlockState blockState = Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, Integer.valueOf(random.nextInt(4) + 1));
			if (structureWorldAccess.getBlockState(blockPos2).isOf(Blocks.WATER) && blockState.canPlaceAt(structureWorldAccess, blockPos2)) {
				structureWorldAccess.setBlockState(blockPos2, blockState, 2);
				i++;
			}
		}

		return i > 0;
	}
}
