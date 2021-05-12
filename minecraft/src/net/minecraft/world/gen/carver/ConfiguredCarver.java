package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.AquiferSampler;

public class ConfiguredCarver<WC extends CarverConfig> {
	public static final Codec<ConfiguredCarver<?>> CODEC = Registry.CARVER.dispatch(configuredCarver -> configuredCarver.carver, Carver::getCodec);
	public static final Codec<Supplier<ConfiguredCarver<?>>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.CONFIGURED_CARVER_KEY, CODEC);
	public static final Codec<List<Supplier<ConfiguredCarver<?>>>> LIST_CODEC = RegistryElementCodec.method_31194(Registry.CONFIGURED_CARVER_KEY, CODEC);
	private final Carver<WC> carver;
	private final WC config;

	public ConfiguredCarver(Carver<WC> carver, WC config) {
		this.carver = carver;
		this.config = config;
	}

	public WC getConfig() {
		return this.config;
	}

	public boolean shouldCarve(Random random) {
		return this.carver.shouldCarve(this.config, random);
	}

	public boolean carve(
		CarverContext context, Chunk chunk, Function<BlockPos, Biome> posToBiome, Random random, AquiferSampler aquiferSampler, ChunkPos pos, BitSet carvingMask
	) {
		return this.carver.carve(context, this.config, chunk, posToBiome, random, aquiferSampler, pos, carvingMask);
	}
}
