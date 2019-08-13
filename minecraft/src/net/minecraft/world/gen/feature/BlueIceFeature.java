package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class BlueIceFeature extends Feature<DefaultFeatureConfig> {
	public BlueIceFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_12818(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		if (blockPos.getY() > iWorld.getSeaLevel() - 1) {
			return false;
		} else if (iWorld.getBlockState(blockPos).getBlock() != Blocks.field_10382 && iWorld.getBlockState(blockPos.down()).getBlock() != Blocks.field_10382) {
			return false;
		} else {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.field_11033 && iWorld.getBlockState(blockPos.offset(direction)).getBlock() == Blocks.field_10225) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				return false;
			} else {
				iWorld.setBlockState(blockPos, Blocks.field_10384.getDefaultState(), 2);

				for (int i = 0; i < 200; i++) {
					int j = random.nextInt(5) - random.nextInt(6);
					int k = 3;
					if (j < 2) {
						k += j / 2;
					}

					if (k >= 1) {
						BlockPos blockPos2 = blockPos.add(random.nextInt(k) - random.nextInt(k), j, random.nextInt(k) - random.nextInt(k));
						BlockState blockState = iWorld.getBlockState(blockPos2);
						Block block = blockState.getBlock();
						if (blockState.getMaterial() == Material.AIR || block == Blocks.field_10382 || block == Blocks.field_10225 || block == Blocks.field_10295) {
							for (Direction direction2 : Direction.values()) {
								Block block2 = iWorld.getBlockState(blockPos2.offset(direction2)).getBlock();
								if (block2 == Blocks.field_10384) {
									iWorld.setBlockState(blockPos2, Blocks.field_10384.getDefaultState(), 2);
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
