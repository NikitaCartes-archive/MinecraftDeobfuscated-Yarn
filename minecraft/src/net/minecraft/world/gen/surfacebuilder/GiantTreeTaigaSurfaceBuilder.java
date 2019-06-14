package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class GiantTreeTaigaSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public GiantTreeTaigaSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
		super(function);
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
			SurfaceBuilder.field_15701.method_15305(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, SurfaceBuilder.field_15678);
		} else if (d > -0.95) {
			SurfaceBuilder.field_15701.method_15305(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, SurfaceBuilder.field_15691);
		} else {
			SurfaceBuilder.field_15701.method_15305(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, SurfaceBuilder.field_15677);
		}
	}
}
