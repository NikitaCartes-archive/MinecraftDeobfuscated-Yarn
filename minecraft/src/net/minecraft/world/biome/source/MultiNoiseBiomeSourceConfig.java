package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.IntStream;
import net.minecraft.world.biome.Biome;

public class MultiNoiseBiomeSourceConfig implements BiomeSourceConfig {
	private final long seed;
	private ImmutableList<Integer> temperatureOctaves = (ImmutableList<Integer>)IntStream.rangeClosed(-8, -1).boxed().collect(ImmutableList.toImmutableList());
	private ImmutableList<Integer> humidityOctaves = (ImmutableList<Integer>)IntStream.rangeClosed(-8, -1).boxed().collect(ImmutableList.toImmutableList());
	private ImmutableList<Integer> hillinessOctaves = (ImmutableList<Integer>)IntStream.rangeClosed(-9, -1).boxed().collect(ImmutableList.toImmutableList());
	private ImmutableList<Integer> styleOctaves = (ImmutableList<Integer>)IntStream.rangeClosed(-8, -1).boxed().collect(ImmutableList.toImmutableList());
	private Set<Biome> biomes = ImmutableSet.of();

	public MultiNoiseBiomeSourceConfig(long seed) {
		this.seed = seed;
	}

	public MultiNoiseBiomeSourceConfig withBiomes(Set<Biome> biomes) {
		this.biomes = biomes;
		return this;
	}

	public Set<Biome> getBiomes() {
		return this.biomes;
	}

	public long getSeed() {
		return this.seed;
	}

	public ImmutableList<Integer> getTemperatureOctaves() {
		return this.temperatureOctaves;
	}

	public ImmutableList<Integer> getHumidityOctaves() {
		return this.humidityOctaves;
	}

	public ImmutableList<Integer> getHillinessOctaves() {
		return this.hillinessOctaves;
	}

	public ImmutableList<Integer> getStyleOctaves() {
		return this.styleOctaves;
	}
}
