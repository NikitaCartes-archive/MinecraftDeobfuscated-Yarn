package net.minecraft.world.gen.surfacebuilder;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class ConfiguredSurfaceBuilder<SC extends SurfaceConfig> {
	public final SurfaceBuilder<SC> field_15610;
	public final SC field_15611;

	public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> surfaceBuilder, SC surfaceConfig) {
		this.field_15610 = surfaceBuilder;
		this.field_15611 = surfaceConfig;
	}

	public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m) {
		this.field_15610.method_15305(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, this.field_15611);
	}

	public void initSeed(long l) {
		this.field_15610.initSeed(l);
	}

	public SC method_15197() {
		return this.field_15611;
	}
}
