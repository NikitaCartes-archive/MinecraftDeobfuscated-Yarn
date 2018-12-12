package net.minecraft.world.gen.surfacebuilder;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class ConfiguredSurfaceBuilder<SC extends SurfaceConfig> {
	public final SurfaceBuilder<SC> surfaceBuilder;
	public final SC field_15611;

	public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> surfaceBuilder, SC surfaceConfig) {
		this.surfaceBuilder = surfaceBuilder;
		this.field_15611 = surfaceConfig;
	}

	public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m) {
		this.surfaceBuilder.method_15305(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, this.field_15611);
	}

	public void initSeed(long l) {
		this.surfaceBuilder.method_15306(l);
	}

	public SC method_15197() {
		return this.field_15611;
	}
}
