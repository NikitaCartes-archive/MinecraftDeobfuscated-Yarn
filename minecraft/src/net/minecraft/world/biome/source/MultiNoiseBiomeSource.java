package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource extends BiomeSource {
	private final OctavePerlinNoiseSampler temperatureNoise;
	private final OctavePerlinNoiseSampler humidityNoise;
	private final OctavePerlinNoiseSampler hillinessNoise;
	private final OctavePerlinNoiseSampler styleNoise;
	private final Map<Biome, List<Biome.MixedNoisePoint>> field_23419;

	public MultiNoiseBiomeSource(MultiNoiseBiomeSourceConfig config) {
		super(config.getBiomes().keySet());
		long l = config.getSeed();
		this.temperatureNoise = new OctavePerlinNoiseSampler(new ChunkRandom(l), config.getTemperatureOctaves());
		this.humidityNoise = new OctavePerlinNoiseSampler(new ChunkRandom(l + 1L), config.getHumidityOctaves());
		this.hillinessNoise = new OctavePerlinNoiseSampler(new ChunkRandom(l + 2L), config.getHillinessOctaves());
		this.styleNoise = new OctavePerlinNoiseSampler(new ChunkRandom(l + 3L), config.getStyleOctaves());
		this.field_23419 = config.getBiomes();
	}

	private float method_26472(Biome biome, Biome.MixedNoisePoint mixedNoisePoint) {
		return (Float)((List)this.field_23419.get(biome))
			.stream()
			.map(mixedNoisePoint2 -> mixedNoisePoint2.calculateDistanceTo(mixedNoisePoint))
			.min(Float::compare)
			.orElse(Float.POSITIVE_INFINITY);
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
		return (Biome)this.biomes.stream().min(Comparator.comparing(biome -> this.method_26472(biome, mixedNoisePoint))).orElse(Biomes.THE_END);
	}

	@Override
	public BiomeSourceType<?, ?> method_26467() {
		return BiomeSourceType.MULTI_NOISE;
	}

	@Override
	public <T> Dynamic<T> method_26466(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createMap(
			(Map<T, T>)this.field_23419
				.entrySet()
				.stream()
				.collect(
					ImmutableMap.toImmutableMap(
						entry -> dynamicOps.createString(Registry.BIOME.getId((Biome)entry.getKey()).toString()),
						entry -> dynamicOps.createList(((List)entry.getValue()).stream().map(mixedNoisePoint -> mixedNoisePoint.method_26457(dynamicOps).getValue()))
					)
				)
		);
		T object2 = dynamicOps.createMap(
			ImmutableMap.<T, T>builder()
				.put(dynamicOps.createString("temperature"), dynamicOps.createList(this.temperatureNoise.method_26682().stream().map(dynamicOps::createInt)))
				.put(dynamicOps.createString("humidity"), dynamicOps.createList(this.humidityNoise.method_26682().stream().map(dynamicOps::createInt)))
				.put(dynamicOps.createString("altitude"), dynamicOps.createList(this.hillinessNoise.method_26682().stream().map(dynamicOps::createInt)))
				.put(dynamicOps.createString("weirdness"), dynamicOps.createList(this.styleNoise.method_26682().stream().map(dynamicOps::createInt)))
				.build()
		);
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("biomes"), object, dynamicOps.createString("noises"), object2)));
	}
}
