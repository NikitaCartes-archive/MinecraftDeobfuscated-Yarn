package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

	public static MultiNoiseBiomeSource method_27986(long l, List<Biome> list) {
		return new MultiNoiseBiomeSource(
			l,
			(List<Pair<Biome.MixedNoisePoint, Biome>>)list.stream()
				.flatMap(biome -> biome.streamNoises().map(mixedNoisePoint -> Pair.of(mixedNoisePoint, biome)))
				.collect(ImmutableList.toImmutableList())
		);
	}

	public MultiNoiseBiomeSource(long l, List<Pair<Biome.MixedNoisePoint, Biome>> list) {
		super((Set<Biome>)list.stream().map(Pair::getSecond).collect(Collectors.toSet()));
		IntStream intStream = IntStream.rangeClosed(-7, -6);
		IntStream intStream2 = IntStream.rangeClosed(-7, -6);
		IntStream intStream3 = IntStream.rangeClosed(-7, -6);
		IntStream intStream4 = IntStream.rangeClosed(-7, -6);
		this.temperatureNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l), intStream);
		this.humidityNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 1L), intStream2);
		this.altitudeNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 2L), intStream3);
		this.weirdnessNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 3L), intStream4);
		this.biomePoints = list;
		this.threeDimensionalSampling = false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public BiomeSource method_27985(long l) {
		return new MultiNoiseBiomeSource(l, this.biomePoints);
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
