package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class ConfiguredSurfaceBuilder<SC extends SurfaceConfig> {
	public final SurfaceBuilder<SC> surfaceBuilder;
	public final SC config;

	public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> surfaceBuilder, SC surfaceConfig) {
		this.surfaceBuilder = surfaceBuilder;
		this.config = surfaceConfig;
	}

	public <T> Dynamic<T> method_26678(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("name"),
					dynamicOps.createString(Registry.SURFACE_BUILDER.getId(this.surfaceBuilder).toString()),
					dynamicOps.createString("config"),
					this.config.method_26681(dynamicOps).getValue()
				)
			)
		);
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
