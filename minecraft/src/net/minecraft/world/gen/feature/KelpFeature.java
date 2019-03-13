package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.KelpBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class KelpFeature extends Feature<DefaultFeatureConfig> {
	public KelpFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_13460(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		int i = 0;
		int j = iWorld.method_8589(Heightmap.Type.OCEAN_FLOOR, blockPos.getX(), blockPos.getZ());
		BlockPos blockPos2 = new BlockPos(blockPos.getX(), j, blockPos.getZ());
		if (iWorld.method_8320(blockPos2).getBlock() == Blocks.field_10382) {
			BlockState blockState = Blocks.field_9993.method_9564();
			BlockState blockState2 = Blocks.field_10463.method_9564();
			int k = 1 + random.nextInt(10);

			for (int l = 0; l <= k; l++) {
				if (iWorld.method_8320(blockPos2).getBlock() == Blocks.field_10382
					&& iWorld.method_8320(blockPos2.up()).getBlock() == Blocks.field_10382
					&& blockState2.method_11591(iWorld, blockPos2)) {
					if (l == k) {
						iWorld.method_8652(blockPos2, blockState.method_11657(KelpBlock.field_11194, Integer.valueOf(random.nextInt(23))), 2);
						i++;
					} else {
						iWorld.method_8652(blockPos2, blockState2, 2);
					}
				} else if (l > 0) {
					BlockPos blockPos3 = blockPos2.down();
					if (blockState.method_11591(iWorld, blockPos3) && iWorld.method_8320(blockPos3.down()).getBlock() != Blocks.field_9993) {
						iWorld.method_8652(blockPos3, blockState.method_11657(KelpBlock.field_11194, Integer.valueOf(random.nextInt(23))), 2);
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
