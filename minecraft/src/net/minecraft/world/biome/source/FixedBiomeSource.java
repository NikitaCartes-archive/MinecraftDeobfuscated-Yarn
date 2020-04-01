package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class FixedBiomeSource extends BiomeSource {
	private final Biome biome;

	public FixedBiomeSource(FixedBiomeSourceConfig config) {
		super(ImmutableSet.of(config.getBiome()));
		this.biome = config.getBiome();
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.biome;
	}

	@Nullable
	@Override
	public BlockPos method_24385(int i, int j, int k, int l, int m, List<Biome> list, Random random, boolean bl) {
		if (list.contains(this.biome)) {
			return bl ? new BlockPos(i, j, k) : new BlockPos(i - l + random.nextInt(l * 2 + 1), j, k - l + random.nextInt(l * 2 + 1));
		} else {
			return null;
		}
	}

	@Override
	public Set<Biome> getBiomesInArea(int x, int y, int z, int radius) {
		return Sets.<Biome>newHashSet(this.biome);
	}

	@Override
	public BiomeSourceType<?, ?> method_26467() {
		return BiomeSourceType.FIXED;
	}

	@Override
	public <T> Dynamic<T> method_26466(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("biome"), dynamicOps.createString(Registry.BIOME.getId(this.biome).toString())))
		);
	}
}
