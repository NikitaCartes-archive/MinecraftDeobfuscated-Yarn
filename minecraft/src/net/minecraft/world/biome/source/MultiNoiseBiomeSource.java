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
	private final DoublePerlinNoiseSampler hillinessNoise;
	private final DoublePerlinNoiseSampler styleNoise;
	private final List<Pair<Biome.MixedNoisePoint, Biome>> field_24115;
	private final boolean field_24116;

	public MultiNoiseBiomeSource(MultiNoiseBiomeSourceConfig config) {
		super((Set<Biome>)config.method_27347().stream().map(Pair::getSecond).collect(Collectors.toSet()));
		long l = config.getSeed();
		this.temperatureNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l), config.getTemperatureOctaves());
		this.humidityNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 1L), config.getHumidityOctaves());
		this.hillinessNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 2L), config.getHillinessOctaves());
		this.styleNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 3L), config.getStyleOctaves());
		this.field_24115 = config.method_27347();
		this.field_24116 = config.method_27351();
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		int i = this.field_24116 ? biomeY : 0;
		Biome.MixedNoisePoint mixedNoisePoint = new Biome.MixedNoisePoint(
			(float)this.temperatureNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			(float)this.humidityNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			(float)this.hillinessNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			(float)this.styleNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			0.0F
		);
		return (Biome)this.field_24115
			.stream()
			.min(Comparator.comparing(pair -> ((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(mixedNoisePoint)))
			.map(Pair::getSecond)
			.orElse(Biomes.THE_VOID);
	}
}
