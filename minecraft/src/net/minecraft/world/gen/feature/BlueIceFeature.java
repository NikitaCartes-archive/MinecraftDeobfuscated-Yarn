package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BlueIceFeature extends Feature<DefaultFeatureConfig> {
	public BlueIceFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean method_12818(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		if (blockPos.getY() > structureWorldAccess.getSeaLevel() - 1) {
			return false;
		} else if (!structureWorldAccess.getBlockState(blockPos).isOf(Blocks.field_10382)
			&& !structureWorldAccess.getBlockState(blockPos.method_10074()).isOf(Blocks.field_10382)) {
			return false;
		} else {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.field_11033 && structureWorldAccess.getBlockState(blockPos.offset(direction)).isOf(Blocks.field_10225)) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				return false;
			} else {
				structureWorldAccess.setBlockState(blockPos, Blocks.field_10384.getDefaultState(), 2);

				for (int i = 0; i < 200; i++) {
					int j = random.nextInt(5) - random.nextInt(6);
					int k = 3;
					if (j < 2) {
						k += j / 2;
					}

					if (k >= 1) {
						BlockPos blockPos2 = blockPos.add(random.nextInt(k) - random.nextInt(k), j, random.nextInt(k) - random.nextInt(k));
						BlockState blockState = structureWorldAccess.getBlockState(blockPos2);
						if (blockState.getMaterial() == Material.AIR
							|| blockState.isOf(Blocks.field_10382)
							|| blockState.isOf(Blocks.field_10225)
							|| blockState.isOf(Blocks.field_10295)) {
							for (Direction direction2 : Direction.values()) {
								BlockState blockState2 = structureWorldAccess.getBlockState(blockPos2.offset(direction2));
								if (blockState2.isOf(Blocks.field_10384)) {
									structureWorldAccess.setBlockState(blockPos2, Blocks.field_10384.getDefaultState(), 2);
									break;
								}
							}
						}
					}
				}

				return true;
			}
		}
	}
}
