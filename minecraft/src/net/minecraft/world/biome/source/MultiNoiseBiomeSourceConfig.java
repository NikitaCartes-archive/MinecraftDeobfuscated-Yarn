package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import net.minecraft.world.biome.Biome;

public class MultiNoiseBiomeSourceConfig implements BiomeSourceConfig {
	private final long seed;
	private ImmutableList<Integer> temperatureOctaves = (ImmutableList<Integer>)IntStream.rangeClosed(-8, -1).boxed().collect(ImmutableList.toImmutableList());
	private ImmutableList<Integer> humidityOctaves = (ImmutableList<Integer>)IntStream.rangeClosed(-8, -1).boxed().collect(ImmutableList.toImmutableList());
	private ImmutableList<Integer> hillinessOctaves = (ImmutableList<Integer>)IntStream.rangeClosed(-9, -1).boxed().collect(ImmutableList.toImmutableList());
	private ImmutableList<Integer> styleOctaves = (ImmutableList<Integer>)IntStream.rangeClosed(-8, -1).boxed().collect(ImmutableList.toImmutableList());
	private Map<Biome, List<Biome.MixedNoisePoint>> biomes = ImmutableMap.of();

	public MultiNoiseBiomeSourceConfig(long seed) {
		this.seed = seed;
	}

	public MultiNoiseBiomeSourceConfig withBiomes(Map<Biome, List<Biome.MixedNoisePoint>> map) {
		this.biomes = map;
		return this;
	}

	public Map<Biome, List<Biome.MixedNoisePoint>> getBiomes() {
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
