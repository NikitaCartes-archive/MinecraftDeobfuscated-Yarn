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
	private final Block ICE = Blocks.field_10225;

	public IcePatchFeature(Function<Dynamic<?>, ? extends IcePatchFeatureConfig> function) {
		super(function);
	}

	public boolean method_13385(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, IcePatchFeatureConfig icePatchFeatureConfig
	) {
		while (iWorld.isAir(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.down();
		}

		if (iWorld.getBlockState(blockPos).getBlock() != Blocks.field_10491) {
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
							if (Block.isNaturalDirt(block) || block == Blocks.field_10491 || block == Blocks.field_10295) {
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
