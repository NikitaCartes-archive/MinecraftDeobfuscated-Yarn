package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class ShatteredSavannaSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public ShatteredSavannaSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
		super(function);
	}

	public void method_15308(
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
			SurfaceBuilder.field_15701.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, SurfaceBuilder.STONE_CONFIG);
		} else if (d > -0.5) {
			SurfaceBuilder.field_15701.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, SurfaceBuilder.COARSE_DIRT_CONFIG);
		} else {
			SurfaceBuilder.field_15701.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, SurfaceBuilder.GRASS_CONFIG);
		}
	}
}
