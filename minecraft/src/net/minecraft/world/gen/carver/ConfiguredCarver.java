package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.chunk.Chunk;

public class ConfiguredCarver<WC extends CarverConfig> {
	public final Carver<WC> field_13279;
	public final WC config;

	public ConfiguredCarver(Carver<WC> carver, WC carverConfig) {
		this.field_13279 = carver;
		this.config = carverConfig;
	}

	public boolean shouldCarve(Random random, int i, int j) {
		return this.field_13279.shouldCarve(random, i, j, this.config);
	}

	public boolean carve(Chunk chunk, Random random, int i, int j, int k, int l, int m, BitSet bitSet) {
		return this.field_13279.carve(chunk, random, i, j, k, l, m, bitSet, this.config);
	}
}
