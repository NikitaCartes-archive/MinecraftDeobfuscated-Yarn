package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class GiantTreeTaigaSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public GiantTreeTaigaSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	public void method_15220(
		Random random,
		Chunk chunk,
		Biome biome,
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		int l,
		long m,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		if (d > 1.75) {
			SurfaceBuilder.field_15701.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, SurfaceBuilder.COARSE_DIRT_CONFIG);
		} else if (d > -0.95) {
			SurfaceBuilder.field_15701.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, SurfaceBuilder.PODZOL_CONFIG);
		} else {
			SurfaceBuilder.field_15701.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, SurfaceBuilder.GRASS_CONFIG);
		}
	}
}
