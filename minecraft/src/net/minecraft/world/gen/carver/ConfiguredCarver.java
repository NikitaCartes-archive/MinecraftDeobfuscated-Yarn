package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.config.carver.CarverConfig;

public class ConfiguredCarver<WC extends CarverConfig> {
	public final Carver<WC> carver;
	public final WC config;

	public ConfiguredCarver(Carver<WC> carver, WC carverConfig) {
		this.carver = carver;
		this.config = carverConfig;
	}

	public boolean method_12669(Random random, int i, int j) {
		return this.carver.method_12705(random, i, j, this.config);
	}

	public boolean method_12668(Chunk chunk, Random random, int i, int j, int k, int l, int m, BitSet bitSet) {
		return this.carver.method_12702(chunk, random, i, j, k, l, m, bitSet, this.config);
	}
}
