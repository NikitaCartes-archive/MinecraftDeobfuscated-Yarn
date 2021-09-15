package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.class_6557;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;

public class GiantTreeTaigaSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public GiantTreeTaigaSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	public void generate(
		Random random,
		class_6557 arg,
		Biome biome,
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		int l,
		int m,
		long n,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		if (d > 1.75) {
			SurfaceBuilder.DEFAULT.generate(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, n, SurfaceBuilder.COARSE_DIRT_CONFIG);
		} else if (d > -0.95) {
			SurfaceBuilder.DEFAULT.generate(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, n, SurfaceBuilder.PODZOL_CONFIG);
		} else {
			SurfaceBuilder.DEFAULT.generate(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, n, SurfaceBuilder.GRASS_CONFIG);
		}
	}
}
