package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class GravellyMountainSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public GravellyMountainSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
		super(function);
	}

	public void method_15245(
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
		if (d < -1.0 || d > 2.0) {
			SurfaceBuilder.DEFAULT.method_15305(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, SurfaceBuilder.field_15673);
		} else if (d > 1.0) {
			SurfaceBuilder.DEFAULT.method_15305(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, SurfaceBuilder.field_15670);
		} else {
			SurfaceBuilder.DEFAULT.method_15305(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, SurfaceBuilder.field_15677);
		}
	}
}
