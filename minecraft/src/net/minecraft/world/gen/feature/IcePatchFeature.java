package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class IcePatchFeature extends Feature<IcePatchFeatureConfig> {
	private final Block ICE = Blocks.PACKED_ICE;

	public IcePatchFeature(Function<Dynamic<?>, ? extends IcePatchFeatureConfig> function) {
		super(function);
	}

	public boolean method_13385(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, IcePatchFeatureConfig icePatchFeatureConfig
	) {
		while (iWorld.method_22347(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.down();
		}

		if (iWorld.getBlockState(blockPos).getBlock() != Blocks.SNOW_BLOCK) {
			return false;
		} else {
			int i = random.nextInt(icePatchFeatureConfig.radius) + 2;
			int j = 1;

			for (int k = blockPos.getX() - i; k <= blockPos.getX() + i; k++) {
				for (int l = blockPos.getZ() - i; l <= blockPos.getZ() + i; l++) {
					int m = k - blockPos.getX();
					int n = l - blockPos.getZ();
					if (m * m + n * n <= i * i) {
						for (int o = blockPos.getY() - 1; o <= blockPos.getY() + 1; o++) {
							BlockPos blockPos2 = new BlockPos(k, o, l);
							Block block = iWorld.getBlockState(blockPos2).getBlock();
							if (Block.isNaturalDirt(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE) {
								iWorld.setBlockState(blockPos2, this.ICE.getDefaultState(), 2);
							}
						}
					}
				}
			}

			return true;
		}
	}
}
