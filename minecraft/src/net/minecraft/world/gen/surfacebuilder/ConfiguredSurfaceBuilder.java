package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class ConfiguredSurfaceBuilder<SC extends SurfaceConfig> {
	public static final Codec<ConfiguredSurfaceBuilder<?>> field_25015 = Registry.SURFACE_BUILDER
		.dispatch("name", configuredSurfaceBuilder -> configuredSurfaceBuilder.surfaceBuilder, SurfaceBuilder::method_29003);
	public final SurfaceBuilder<SC> surfaceBuilder;
	public final SC config;

	public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> surfaceBuilder, SC surfaceConfig) {
		this.surfaceBuilder = surfaceBuilder;
		this.config = surfaceConfig;
	}

	public void generate(
		Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState defaultBlock, BlockState defaultFluid, int l, long seed
	) {
		this.surfaceBuilder.generate(random, chunk, biome, i, j, k, d, defaultBlock, defaultFluid, l, seed, this.config);
	}

	public void initSeed(long seed) {
		this.surfaceBuilder.initSeed(seed);
	}

	public SC getConfig() {
		return this.config;
	}
}
