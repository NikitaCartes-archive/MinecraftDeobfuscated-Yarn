package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.class_6557;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;

public class GravellyMountainSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public GravellyMountainSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
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
		if (d < -1.0 || d > 2.0) {
			SurfaceBuilder.DEFAULT.generate(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, n, SurfaceBuilder.GRAVEL_CONFIG);
		} else if (d > 1.0) {
			SurfaceBuilder.DEFAULT.generate(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, n, SurfaceBuilder.STONE_CONFIG);
		} else {
			SurfaceBuilder.DEFAULT.generate(random, arg, biome, i, j, k, d, blockState, blockState2, l, m, n, SurfaceBuilder.GRASS_CONFIG);
		}
	}
}
