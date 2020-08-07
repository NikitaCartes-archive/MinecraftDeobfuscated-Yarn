package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class GlowstoneBlobFeature extends Feature<DefaultFeatureConfig> {
	public GlowstoneBlobFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean method_13239(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		if (!structureWorldAccess.isAir(blockPos)) {
			return false;
		} else {
			BlockState blockState = structureWorldAccess.getBlockState(blockPos.up());
			if (!blockState.isOf(Blocks.field_10515) && !blockState.isOf(Blocks.field_22091) && !blockState.isOf(Blocks.field_23869)) {
				return false;
			} else {
				structureWorldAccess.setBlockState(blockPos, Blocks.field_10171.getDefaultState(), 2);

				for (int i = 0; i < 1500; i++) {
					BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
					if (structureWorldAccess.getBlockState(blockPos2).isAir()) {
						int j = 0;

						for (Direction direction : Direction.values()) {
							if (structureWorldAccess.getBlockState(blockPos2.offset(direction)).isOf(Blocks.field_10171)) {
								j++;
							}

							if (j > 1) {
								break;
							}
						}

						if (j == 1) {
							structureWorldAccess.setBlockState(blockPos2, Blocks.field_10171.getDefaultState(), 2);
						}
					}
				}

				return true;
			}
		}
	}
}
