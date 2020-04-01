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
	public GlowstoneBlobFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, Function<Random, ? extends DefaultFeatureConfig> function2) {
		super(function, function2);
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		if (!iWorld.isAir(blockPos)) {
			return false;
		} else if (iWorld.getBlockState(blockPos.up()).getBlock() != Blocks.NETHERRACK) {
			return false;
		} else {
			iWorld.setBlockState(blockPos, Blocks.GLOWSTONE.getDefaultState(), 2);

			for (int i = 0; i < 1500; i++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
				if (iWorld.getBlockState(blockPos2).isAir()) {
					int j = 0;

					for (Direction direction : Direction.values()) {
						if (iWorld.getBlockState(blockPos2.offset(direction)).getBlock() == Blocks.GLOWSTONE) {
							j++;
						}

						if (j > 1) {
							break;
						}
					}

					if (j == 1) {
						iWorld.setBlockState(blockPos2, Blocks.GLOWSTONE.getDefaultState(), 2);
					}
				}
			}

			return true;
		}
	}
}
