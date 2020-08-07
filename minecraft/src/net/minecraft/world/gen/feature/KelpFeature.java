package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.KelpBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class KelpFeature extends Feature<DefaultFeatureConfig> {
	public KelpFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean method_13460(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		int i = 0;
		int j = structureWorldAccess.getTopY(Heightmap.Type.field_13200, blockPos.getX(), blockPos.getZ());
		BlockPos blockPos2 = new BlockPos(blockPos.getX(), j, blockPos.getZ());
		if (structureWorldAccess.getBlockState(blockPos2).isOf(Blocks.field_10382)) {
			BlockState blockState = Blocks.field_9993.getDefaultState();
			BlockState blockState2 = Blocks.field_10463.getDefaultState();
			int k = 1 + random.nextInt(10);

			for (int l = 0; l <= k; l++) {
				if (structureWorldAccess.getBlockState(blockPos2).isOf(Blocks.field_10382)
					&& structureWorldAccess.getBlockState(blockPos2.up()).isOf(Blocks.field_10382)
					&& blockState2.canPlaceAt(structureWorldAccess, blockPos2)) {
					if (l == k) {
						structureWorldAccess.setBlockState(blockPos2, blockState.with(KelpBlock.AGE, Integer.valueOf(random.nextInt(4) + 20)), 2);
						i++;
					} else {
						structureWorldAccess.setBlockState(blockPos2, blockState2, 2);
					}
				} else if (l > 0) {
					BlockPos blockPos3 = blockPos2.method_10074();
					if (blockState.canPlaceAt(structureWorldAccess, blockPos3) && !structureWorldAccess.getBlockState(blockPos3.method_10074()).isOf(Blocks.field_9993)) {
						structureWorldAccess.setBlockState(blockPos3, blockState.with(KelpBlock.AGE, Integer.valueOf(random.nextInt(4) + 20)), 2);
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
