package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class GlowstoneBlobFeature extends Feature<DefaultFeatureConfig> {
	public GlowstoneBlobFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_13239(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		if (!iWorld.isAir(blockPos)) {
			return false;
		} else if (iWorld.getBlockState(blockPos.up()).getBlock() != Blocks.field_10515) {
			return false;
		} else {
			iWorld.setBlockState(blockPos, Blocks.field_10171.getDefaultState(), 2);

			for (int i = 0; i < 1500; i++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
				if (iWorld.getBlockState(blockPos2).isAir()) {
					int j = 0;

					for (Direction direction : Direction.values()) {
						if (iWorld.getBlockState(blockPos2.offset(direction)).getBlock() == Blocks.field_10171) {
							j++;
						}

						if (j > 1) {
							break;
						}
					}

					if (j == 1) {
						iWorld.setBlockState(blockPos2, Blocks.field_10171.getDefaultState(), 2);
					}
				}
			}

			return true;
		}
	}
}
