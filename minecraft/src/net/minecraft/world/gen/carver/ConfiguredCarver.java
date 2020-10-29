package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class ConfiguredCarver<WC extends CarverConfig> {
	public static final Codec<ConfiguredCarver<?>> CODEC = Registry.CARVER.dispatch(configuredCarver -> configuredCarver.carver, Carver::getCodec);
	public static final Codec<Supplier<ConfiguredCarver<?>>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.CONFIGURED_CARVER_WORLDGEN, CODEC);
	public static final Codec<List<Supplier<ConfiguredCarver<?>>>> field_26755 = RegistryElementCodec.method_31194(Registry.CONFIGURED_CARVER_WORLDGEN, CODEC);
	private final Carver<WC> carver;
	private final WC config;

	public ConfiguredCarver(Carver<WC> carver, WC config) {
		this.carver = carver;
		this.config = config;
	}

	public WC getConfig() {
		return this.config;
	}

	public boolean shouldCarve(Random random, int chunkX, int chunkZ) {
		return this.carver.shouldCarve(random, chunkX, chunkZ, this.config);
	}

	public boolean carve(
		Chunk chunk, Function<BlockPos, Biome> posToBiome, Random random, int seaLevel, int chunkX, int chunkZ, int mainChunkX, int mainChunkZ, BitSet carvingMask
	) {
		return this.carver.carve(chunk, posToBiome, random, seaLevel, chunkX, chunkZ, mainChunkX, mainChunkZ, carvingMask, this.config);
	}
}
