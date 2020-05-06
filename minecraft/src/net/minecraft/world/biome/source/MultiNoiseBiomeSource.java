package net.minecraft.world.biome.source;

import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource extends BiomeSource {
	private final DoublePerlinNoiseSampler temperatureNoise;
	private final DoublePerlinNoiseSampler humidityNoise;
	private final DoublePerlinNoiseSampler altitudeNoise;
	private final DoublePerlinNoiseSampler weirdnessNoise;
	private final List<Pair<Biome.MixedNoisePoint, Biome>> biomePoints;
	private final boolean threeDimensionalSampling;

	public MultiNoiseBiomeSource(MultiNoiseBiomeSourceConfig config) {
		super((Set<Biome>)config.getBiomePoints().stream().map(Pair::getSecond).collect(Collectors.toSet()));
		long l = config.getSeed();
		this.temperatureNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l), config.getTemperatureOctaves());
		this.humidityNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 1L), config.getHumidityOctaves());
		this.altitudeNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 2L), config.getAltitudeOctaves());
		this.weirdnessNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 3L), config.getWeirdnessOctaves());
		this.biomePoints = config.getBiomePoints();
		this.threeDimensionalSampling = config.useThreeDimensionalSampling();
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		int i = this.threeDimensionalSampling ? biomeY : 0;
		Biome.MixedNoisePoint mixedNoisePoint = new Biome.MixedNoisePoint(
			(float)this.temperatureNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			(float)this.humidityNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			(float)this.altitudeNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			(float)this.weirdnessNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			0.0F
		);
		return (Biome)this.biomePoints
			.stream()
			.min(Comparator.comparing(pair -> ((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(mixedNoisePoint)))
			.map(Pair::getSecond)
			.orElse(Biomes.THE_VOID);
	}
}
