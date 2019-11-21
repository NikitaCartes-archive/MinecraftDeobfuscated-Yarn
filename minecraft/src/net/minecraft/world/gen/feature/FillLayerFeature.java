package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class FillLayerFeature extends Feature<FillLayerFeatureConfig> {
	public FillLayerFeature(Function<Dynamic<?>, ? extends FillLayerFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, FillLayerFeatureConfig fillLayerFeatureConfig
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int k = blockPos.getX() + i;
				int l = blockPos.getZ() + j;
				int m = fillLayerFeatureConfig.height;
				mutable.set(k, m, l);
				if (iWorld.getBlockState(mutable).isAir()) {
					iWorld.setBlockState(mutable, fillLayerFeatureConfig.state, 2);
				}
			}
		}

		return true;
	}
}
