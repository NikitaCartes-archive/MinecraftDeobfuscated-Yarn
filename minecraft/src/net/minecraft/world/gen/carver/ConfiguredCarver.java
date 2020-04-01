package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class ConfiguredCarver<WC extends CarverConfig> {
	public final Carver<WC> carver;
	public final WC config;

	public ConfiguredCarver(Carver<WC> carver, WC config) {
		this.carver = carver;
		this.config = config;
	}

	public <T> Dynamic<T> method_26581(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("name"),
					dynamicOps.createString(Registry.CARVER.getId(this.carver).toString()),
					dynamicOps.createString("config"),
					this.config.serialize(dynamicOps).getValue()
				)
			)
		);
	}

	public boolean shouldCarve(Random random, int chunkX, int chunkZ) {
		return this.carver.shouldCarve(random, chunkX, chunkZ, this.config);
	}

	public boolean carve(Chunk chunk, Function<BlockPos, Biome> function, Random random, int i, int j, int k, int l, int m, BitSet bitSet) {
		return this.carver.carve(chunk, function, random, i, j, k, l, m, bitSet, this.config);
	}
}
