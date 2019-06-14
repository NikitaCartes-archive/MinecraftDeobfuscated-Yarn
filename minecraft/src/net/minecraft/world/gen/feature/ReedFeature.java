package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class ReedFeature extends Feature<DefaultFeatureConfig> {
	public ReedFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_13782(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		int i = 0;

		for (int j = 0; j < 20; j++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
			if (iWorld.isAir(blockPos2)) {
				BlockPos blockPos3 = blockPos2.down();
				if (iWorld.method_8316(blockPos3.west()).matches(FluidTags.field_15517)
					|| iWorld.method_8316(blockPos3.east()).matches(FluidTags.field_15517)
					|| iWorld.method_8316(blockPos3.north()).matches(FluidTags.field_15517)
					|| iWorld.method_8316(blockPos3.south()).matches(FluidTags.field_15517)) {
					int k = 2 + random.nextInt(random.nextInt(3) + 1);

					for (int l = 0; l < k; l++) {
						if (Blocks.field_10424.method_9564().canPlaceAt(iWorld, blockPos2)) {
							iWorld.method_8652(blockPos2.up(l), Blocks.field_10424.method_9564(), 2);
							i++;
						}
					}
				}
			}
		}

		return i > 0;
	}
}
