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
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SeagrassFeature extends Feature<SeagrassFeatureConfig> {
	public SeagrassFeature(Codec<SeagrassFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		SeagrassFeatureConfig seagrassFeatureConfig
	) {
		int i = 0;

		for (int j = 0; j < seagrassFeatureConfig.count; j++) {
			int k = random.nextInt(8) - random.nextInt(8);
			int l = random.nextInt(8) - random.nextInt(8);
			int m = serverWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX() + k, blockPos.getZ() + l);
			BlockPos blockPos2 = new BlockPos(blockPos.getX() + k, m, blockPos.getZ() + l);
			if (serverWorldAccess.getBlockState(blockPos2).isOf(Blocks.WATER)) {
				boolean bl = random.nextDouble() < seagrassFeatureConfig.tallSeagrassProbability;
				BlockState blockState = bl ? Blocks.TALL_SEAGRASS.getDefaultState() : Blocks.SEAGRASS.getDefaultState();
				if (blockState.canPlaceAt(serverWorldAccess, blockPos2)) {
					if (bl) {
						BlockState blockState2 = blockState.with(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
						BlockPos blockPos3 = blockPos2.up();
						if (serverWorldAccess.getBlockState(blockPos3).isOf(Blocks.WATER)) {
							serverWorldAccess.setBlockState(blockPos2, blockState, 2);
							serverWorldAccess.setBlockState(blockPos3, blockState2, 2);
						}
					} else {
						serverWorldAccess.setBlockState(blockPos2, blockState, 2);
					}

					i++;
				}
			}
		}

		return i > 0;
	}
}
