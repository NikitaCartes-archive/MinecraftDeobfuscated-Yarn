package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.class_5871;
import net.minecraft.class_5873;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class ConfiguredCarver<WC extends class_5871> {
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

	public boolean shouldCarve(Random random) {
		return this.carver.shouldCarve(this.config, random);
	}

	public boolean carve(class_5873 arg, Chunk chunk, Function<BlockPos, Biome> function, Random random, int chunkX, ChunkPos chunkPos, BitSet bitSet) {
		return this.carver.carve(arg, this.config, chunk, function, random, chunkX, chunkPos, bitSet);
	}
}
