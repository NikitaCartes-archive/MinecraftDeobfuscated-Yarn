package net.minecraft.world.biome.source;

import java.util.Comparator;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource extends BiomeSource {
	private final OctavePerlinNoiseSampler temperatureNoise;
	private final OctavePerlinNoiseSampler humidityNoise;
	private final OctavePerlinNoiseSampler hillinessNoise;
	private final OctavePerlinNoiseSampler styleNoise;

	public MultiNoiseBiomeSource(MultiNoiseBiomeSourceConfig config) {
		super(config.getBiomes());
		long l = config.getSeed();
		this.temperatureNoise = new OctavePerlinNoiseSampler(new ChunkRandom(l), config.getTemperatureOctaves());
		this.humidityNoise = new OctavePerlinNoiseSampler(new ChunkRandom(l + 1L), config.getHumidityOctaves());
		this.hillinessNoise = new OctavePerlinNoiseSampler(new ChunkRandom(l + 2L), config.getHillinessOctaves());
		this.styleNoise = new OctavePerlinNoiseSampler(new ChunkRandom(l + 3L), config.getStyleOctaves());
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		double d = 1.0181268882175227;
		double e = 1.0;
		double f = (double)biomeX * 1.0181268882175227;
		double g = (double)biomeZ * 1.0181268882175227;
		double h = (double)biomeX * 1.0;
		double i = (double)biomeZ * 1.0;
		Biome.MixedNoisePoint mixedNoisePoint = new Biome.MixedNoisePoint(
			(float)((this.temperatureNoise.sample(f, 0.0, g) + this.temperatureNoise.sample(h, 0.0, i)) * 0.5),
			(float)((this.humidityNoise.sample(f, 0.0, g) + this.humidityNoise.sample(h, 0.0, i)) * 0.5),
			(float)((this.hillinessNoise.sample(f, 0.0, g) + this.hillinessNoise.sample(h, 0.0, i)) * 0.5),
			(float)((this.styleNoise.sample(f, 0.0, g) + this.styleNoise.sample(h, 0.0, i)) * 0.5),
			1.0F
		);
		return (Biome)this.biomes.stream().min(Comparator.comparing(biome -> biome.getNoiseDistance(mixedNoisePoint))).orElse(Biomes.THE_END);
	}
}
