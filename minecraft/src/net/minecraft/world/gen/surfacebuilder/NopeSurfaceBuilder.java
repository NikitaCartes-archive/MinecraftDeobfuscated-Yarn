package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class NopeSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public NopeSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function, Function<Random, ? extends TernarySurfaceConfig> function2) {
		super(function, function2);
	}

	public void generate(
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
	}
}
