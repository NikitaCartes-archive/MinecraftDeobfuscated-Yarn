package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class FixedBiomeSource extends BiomeSource {
	private final Biome biome;

	public FixedBiomeSource(FixedBiomeSourceConfig fixedBiomeSourceConfig) {
		super(ImmutableSet.of(fixedBiomeSourceConfig.getBiome()));
		this.biome = fixedBiomeSourceConfig.getBiome();
	}

	@Override
	public Biome getStoredBiome(int i, int j, int k) {
		return this.biome;
	}

	@Nullable
	@Override
	public BlockPos locateBiome(int i, int j, int k, int l, List<Biome> list, Random random) {
		return list.contains(this.biome) ? new BlockPos(i - l + random.nextInt(l * 2 + 1), j, k - l + random.nextInt(l * 2 + 1)) : null;
	}

	@Override
	public Set<Biome> getBiomesInArea(int i, int j, int k, int l) {
		return Sets.<Biome>newHashSet(this.biome);
	}
}
