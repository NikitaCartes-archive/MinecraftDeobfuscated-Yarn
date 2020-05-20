package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NetherForestVegetationFeature extends Feature<BlockPileFeatureConfig> {
	public NetherForestVegetationFeature(Codec<BlockPileFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		BlockPileFeatureConfig blockPileFeatureConfig
	) {
		return method_26264(serverWorldAccess, random, blockPos, blockPileFeatureConfig, 8, 4);
	}

	public static boolean method_26264(WorldAccess worldAccess, Random random, BlockPos blockPos, BlockPileFeatureConfig blockPileFeatureConfig, int i, int j) {
		for (Block block = worldAccess.getBlockState(blockPos.down()).getBlock();
			!block.isIn(BlockTags.NYLIUM) && blockPos.getY() > 0;
			block = worldAccess.getBlockState(blockPos).getBlock()
		) {
			blockPos = blockPos.down();
		}

		int k = blockPos.getY();
		if (k >= 1 && k + 1 < 256) {
			int l = 0;

			for (int m = 0; m < i * i; m++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(i) - random.nextInt(i), random.nextInt(j) - random.nextInt(j), random.nextInt(i) - random.nextInt(i));
				BlockState blockState = blockPileFeatureConfig.stateProvider.getBlockState(random, blockPos2);
				if (worldAccess.isAir(blockPos2) && blockPos2.getY() > 0 && blockState.canPlaceAt(worldAccess, blockPos2)) {
					worldAccess.setBlockState(blockPos2, blockState, 2);
					l++;
				}
			}

			return l > 0;
		} else {
			return false;
		}
	}
}
