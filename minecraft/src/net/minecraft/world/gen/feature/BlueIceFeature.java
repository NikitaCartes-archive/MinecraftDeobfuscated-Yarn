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
		} else if (iWorld.method_8320(blockPos).getBlock() != Blocks.field_10382 && iWorld.method_8320(blockPos.down()).getBlock() != Blocks.field_10382) {
			return false;
		} else {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.DOWN && iWorld.method_8320(blockPos.method_10093(direction)).getBlock() == Blocks.field_10225) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				return false;
			} else {
				iWorld.method_8652(blockPos, Blocks.field_10384.method_9564(), 2);

				for (int i = 0; i < 200; i++) {
					int j = random.nextInt(5) - random.nextInt(6);
					int k = 3;
					if (j < 2) {
						k += j / 2;
					}

					if (k >= 1) {
						BlockPos blockPos2 = blockPos.add(random.nextInt(k) - random.nextInt(k), j, random.nextInt(k) - random.nextInt(k));
						BlockState blockState = iWorld.method_8320(blockPos2);
						Block block = blockState.getBlock();
						if (blockState.method_11620() == Material.AIR || block == Blocks.field_10382 || block == Blocks.field_10225 || block == Blocks.field_10295) {
							for (Direction direction2 : Direction.values()) {
								Block block2 = iWorld.method_8320(blockPos2.method_10093(direction2)).getBlock();
								if (block2 == Blocks.field_10384) {
									iWorld.method_8652(blockPos2, Blocks.field_10384.method_9564(), 2);
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
