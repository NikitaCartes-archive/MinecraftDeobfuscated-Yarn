package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class CactusFeature extends Feature<DefaultFeatureConfig> {
	public CactusFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_12853(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.isAir(blockPos2)) {
				int j = 1 + random.nextInt(random.nextInt(3) + 1);

				for (int k = 0; k < j; k++) {
					if (Blocks.field_10029.getDefaultState().canPlaceAt(iWorld, blockPos2)) {
						iWorld.setBlockState(blockPos2.up(k), Blocks.field_10029.getDefaultState(), 2);
					}
				}
			}
		}

		return true;
	}
}
