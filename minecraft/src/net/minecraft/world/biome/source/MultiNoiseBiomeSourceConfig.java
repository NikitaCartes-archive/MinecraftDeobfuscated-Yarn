package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.world.biome.Biome;

public class MultiNoiseBiomeSourceConfig implements BiomeSourceConfig {
	private final long seed;
	private ImmutableList<Integer> temperatureOctaves = (ImmutableList<Integer>)IntStream.rangeClosed(-7, -6).boxed().collect(ImmutableList.toImmutableList());
	private ImmutableList<Integer> humidityOctaves = (ImmutableList<Integer>)IntStream.rangeClosed(-7, -6).boxed().collect(ImmutableList.toImmutableList());
	private ImmutableList<Integer> altitudeOctaves = (ImmutableList<Integer>)IntStream.rangeClosed(-7, -6).boxed().collect(ImmutableList.toImmutableList());
	private ImmutableList<Integer> weirdnessOctaves = (ImmutableList<Integer>)IntStream.rangeClosed(-7, -6).boxed().collect(ImmutableList.toImmutableList());
	private boolean threeDimensionalSampling;
	private List<Pair<Biome.MixedNoisePoint, Biome>> biomePoints = ImmutableList.of();

	public MultiNoiseBiomeSourceConfig(long seed) {
		this.seed = seed;
	}

	public MultiNoiseBiomeSourceConfig withBiomes(List<Biome> biomes) {
		return this.withBiomePoints(
			(List<Pair<Biome.MixedNoisePoint, Biome>>)biomes.stream()
				.flatMap(biome -> biome.streamNoises().map(mixedNoisePoint -> Pair.of(mixedNoisePoint, biome)))
				.collect(ImmutableList.toImmutableList())
		);
	}

	public MultiNoiseBiomeSourceConfig withBiomePoints(List<Pair<Biome.MixedNoisePoint, Biome>> biomePoints) {
		this.biomePoints = biomePoints;
		return this;
	}

	public List<Pair<Biome.MixedNoisePoint, Biome>> getBiomePoints() {
		return this.biomePoints;
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

	public ImmutableList<Integer> getAltitudeOctaves() {
		return this.altitudeOctaves;
	}

	public ImmutableList<Integer> getWeirdnessOctaves() {
		return this.weirdnessOctaves;
	}

	public boolean useThreeDimensionalSampling() {
		return this.threeDimensionalSampling;
	}
}
