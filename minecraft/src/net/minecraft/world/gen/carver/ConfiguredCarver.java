package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class ConfiguredCarver<WC extends CarverConfig> {
	public static final Codec<ConfiguredCarver<?>> field_24828 = Registry.CARVER
		.dispatch("name", configuredCarver -> configuredCarver.carver, Carver::method_28616);
	public final Carver<WC> carver;
	public final WC config;

	public ConfiguredCarver(Carver<WC> carver, WC config) {
		this.carver = carver;
		this.config = config;
	}

	public boolean shouldCarve(Random random, int chunkX, int chunkZ) {
		return this.carver.shouldCarve(random, chunkX, chunkZ, this.config);
	}

	public boolean carve(Chunk chunk, Function<BlockPos, Biome> function, Random random, int i, int j, int k, int l, int m, BitSet bitSet) {
		return this.carver.carve(chunk, function, random, i, j, k, l, m, bitSet, this.config);
	}
}
