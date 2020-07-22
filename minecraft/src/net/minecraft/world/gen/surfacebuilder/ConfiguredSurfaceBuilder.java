package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class ConfiguredSurfaceBuilder<SC extends SurfaceConfig> {
	public static final MapCodec<ConfiguredSurfaceBuilder<?>> field_25878 = Registry.SURFACE_BUILDER
		.dispatchMap(configuredSurfaceBuilder -> configuredSurfaceBuilder.surfaceBuilder, SurfaceBuilder::method_29003);
	public static final Codec<Supplier<ConfiguredSurfaceBuilder<?>>> field_25015 = RegistryElementCodec.of(
		Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN, field_25878
	);
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
