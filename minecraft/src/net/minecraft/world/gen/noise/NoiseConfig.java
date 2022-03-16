package net.minecraft.world.gen.noise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_7139;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.random.RandomDeriver;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public record NoiseConfig(
	RandomDeriver random,
	long legacyWorldSeed,
	Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersRegistry,
	NoiseRouter router,
	MultiNoiseUtil.MultiNoiseSampler sampler,
	SurfaceBuilder surfaceBuilder,
	RandomDeriver aquiferRandom,
	RandomDeriver oreRandom,
	Map<RegistryKey<DoublePerlinNoiseSampler.NoiseParameters>, DoublePerlinNoiseSampler> noiseIntances,
	Map<Identifier, RandomDeriver> randomDerivers
) {
	@Deprecated
	public NoiseConfig(
		RandomDeriver random,
		long legacyWorldSeed,
		Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersRegistry,
		NoiseRouter router,
		MultiNoiseUtil.MultiNoiseSampler sampler,
		SurfaceBuilder surfaceBuilder,
		RandomDeriver aquiferRandom,
		RandomDeriver oreRandom,
		Map<RegistryKey<DoublePerlinNoiseSampler.NoiseParameters>, DoublePerlinNoiseSampler> noiseIntances,
		Map<Identifier, RandomDeriver> randomDerivers
	) {
		this.random = random;
		this.legacyWorldSeed = legacyWorldSeed;
		this.noiseParametersRegistry = noiseParametersRegistry;
		this.router = router;
		this.sampler = sampler;
		this.surfaceBuilder = surfaceBuilder;
		this.aquiferRandom = aquiferRandom;
		this.oreRandom = oreRandom;
		this.noiseIntances = noiseIntances;
		this.randomDerivers = randomDerivers;
	}

	public static NoiseConfig create(
		DynamicRegistryManager dynamicRegistryManager, RegistryKey<ChunkGeneratorSettings> chunkGeneratorSettingsKey, long legacyWorldSeed
	) {
		return create(
			dynamicRegistryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).getOrThrow(chunkGeneratorSettingsKey),
			dynamicRegistryManager.get(Registry.NOISE_WORLDGEN),
			legacyWorldSeed
		);
	}

	public static NoiseConfig create(
		ChunkGeneratorSettings chunkGeneratorSettings, Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersRegistry, long legacyWorldSeed
	) {
		RandomDeriver randomDeriver = chunkGeneratorSettings.getRandomProvider().create(legacyWorldSeed).createRandomDeriver();
		NoiseRouter noiseRouter = chunkGeneratorSettings.method_41542(
			noiseParametersRegistry, new class_7139(randomDeriver, chunkGeneratorSettings.usesLegacyRandom(), legacyWorldSeed)
		);
		MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler = new MultiNoiseUtil.MultiNoiseSampler(
			noiseRouter.temperature(),
			noiseRouter.vegetation(),
			noiseRouter.continents(),
			noiseRouter.erosion(),
			noiseRouter.depth(),
			noiseRouter.ridges(),
			chunkGeneratorSettings.spawnTarget()
		);
		return new NoiseConfig(
			randomDeriver,
			legacyWorldSeed,
			noiseParametersRegistry,
			noiseRouter,
			multiNoiseSampler,
			new SurfaceBuilder(noiseParametersRegistry, chunkGeneratorSettings.defaultBlock(), chunkGeneratorSettings.seaLevel(), randomDeriver),
			randomDeriver.createRandom(new Identifier("aquifer")).createRandomDeriver(),
			randomDeriver.createRandom(new Identifier("ore")).createRandomDeriver(),
			new ConcurrentHashMap(),
			new ConcurrentHashMap()
		);
	}

	public DoublePerlinNoiseSampler getOrCreateSampler(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersKey) {
		return (DoublePerlinNoiseSampler)this.noiseIntances
			.computeIfAbsent(noiseParametersKey, key -> NoiseParametersKeys.createNoiseSampler(this.noiseParametersRegistry, this.random, noiseParametersKey));
	}

	public RandomDeriver getOrCreateRandomDeriver(Identifier id) {
		return (RandomDeriver)this.randomDerivers.computeIfAbsent(id, id2 -> this.random.createRandom(id).createRandomDeriver());
	}
}
