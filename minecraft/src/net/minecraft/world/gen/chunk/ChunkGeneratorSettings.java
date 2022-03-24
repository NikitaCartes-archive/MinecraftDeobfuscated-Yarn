package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.surfacebuilder.VanillaSurfaceRules;

public record ChunkGeneratorSettings(
	GenerationShapeConfig generationShapeConfig,
	BlockState defaultBlock,
	BlockState defaultFluid,
	NoiseRouter noiseRouter,
	MaterialRules.MaterialRule surfaceRule,
	List<MultiNoiseUtil.NoiseHypercube> spawnTarget,
	int seaLevel,
	@Deprecated boolean mobGenerationDisabled,
	boolean aquifers,
	boolean oreVeins,
	boolean usesLegacyRandom
) {
	public static final Codec<ChunkGeneratorSettings> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					GenerationShapeConfig.CODEC.fieldOf("noise").forGetter(ChunkGeneratorSettings::generationShapeConfig),
					BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorSettings::defaultBlock),
					BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorSettings::defaultFluid),
					NoiseRouter.field_37683.fieldOf("noise_router").forGetter(ChunkGeneratorSettings::noiseRouter),
					MaterialRules.MaterialRule.CODEC.fieldOf("surface_rule").forGetter(ChunkGeneratorSettings::surfaceRule),
					MultiNoiseUtil.NoiseHypercube.CODEC.listOf().fieldOf("spawn_target").forGetter(ChunkGeneratorSettings::spawnTarget),
					Codec.INT.fieldOf("sea_level").forGetter(ChunkGeneratorSettings::seaLevel),
					Codec.BOOL.fieldOf("disable_mob_generation").forGetter(ChunkGeneratorSettings::mobGenerationDisabled),
					Codec.BOOL.fieldOf("aquifers_enabled").forGetter(ChunkGeneratorSettings::hasAquifers),
					Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(ChunkGeneratorSettings::oreVeins),
					Codec.BOOL.fieldOf("legacy_random_source").forGetter(ChunkGeneratorSettings::usesLegacyRandom)
				)
				.apply(instance, ChunkGeneratorSettings::new)
	);
	public static final Codec<RegistryEntry<ChunkGeneratorSettings>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, CODEC);
	public static final RegistryKey<ChunkGeneratorSettings> OVERWORLD = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("overworld"));
	public static final RegistryKey<ChunkGeneratorSettings> LARGE_BIOMES = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("large_biomes"));
	public static final RegistryKey<ChunkGeneratorSettings> AMPLIFIED = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("amplified"));
	public static final RegistryKey<ChunkGeneratorSettings> NETHER = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("nether"));
	public static final RegistryKey<ChunkGeneratorSettings> END = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("end"));
	public static final RegistryKey<ChunkGeneratorSettings> CAVES = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("caves"));
	public static final RegistryKey<ChunkGeneratorSettings> FLOATING_ISLANDS = RegistryKey.of(
		Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("floating_islands")
	);

	public boolean hasAquifers() {
		return this.aquifers;
	}

	public ChunkRandom.RandomProvider getRandomProvider() {
		return this.usesLegacyRandom ? ChunkRandom.RandomProvider.LEGACY : ChunkRandom.RandomProvider.XOROSHIRO;
	}

	private static void register(RegistryKey<ChunkGeneratorSettings> registryKey, ChunkGeneratorSettings settings) {
		BuiltinRegistries.add(BuiltinRegistries.CHUNK_GENERATOR_SETTINGS, registryKey.getValue(), settings);
	}

	public static RegistryEntry<ChunkGeneratorSettings> getInstance() {
		return (RegistryEntry<ChunkGeneratorSettings>)BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.streamEntries().iterator().next();
	}

	private static ChunkGeneratorSettings createEndSettings() {
		return new ChunkGeneratorSettings(
			GenerationShapeConfig.END,
			Blocks.END_STONE.getDefaultState(),
			Blocks.AIR.getDefaultState(),
			DensityFunctions.createEndNoiseRouter(),
			VanillaSurfaceRules.getEndStoneRule(),
			List.of(),
			0,
			true,
			false,
			false,
			true
		);
	}

	private static ChunkGeneratorSettings createNetherSettings() {
		return new ChunkGeneratorSettings(
			GenerationShapeConfig.NETHER,
			Blocks.NETHERRACK.getDefaultState(),
			Blocks.LAVA.getDefaultState(),
			DensityFunctions.createNetherNoiseRouter(),
			VanillaSurfaceRules.createNetherSurfaceRule(),
			List.of(),
			32,
			false,
			false,
			false,
			true
		);
	}

	private static ChunkGeneratorSettings createSurfaceSettings(boolean amplified, boolean largeBiomes) {
		return new ChunkGeneratorSettings(
			GenerationShapeConfig.SURFACE,
			Blocks.STONE.getDefaultState(),
			Blocks.WATER.getDefaultState(),
			DensityFunctions.createSurfaceNoiseRouter(largeBiomes, amplified),
			VanillaSurfaceRules.createOverworldSurfaceRule(),
			new VanillaBiomeParameters().getSpawnSuitabilityNoises(),
			63,
			false,
			true,
			true,
			false
		);
	}

	private static ChunkGeneratorSettings createCavesSettings() {
		return new ChunkGeneratorSettings(
			GenerationShapeConfig.CAVES,
			Blocks.STONE.getDefaultState(),
			Blocks.WATER.getDefaultState(),
			DensityFunctions.createCavesNoiseRouter(),
			VanillaSurfaceRules.createDefaultRule(false, true, true),
			List.of(),
			32,
			false,
			false,
			false,
			true
		);
	}

	private static ChunkGeneratorSettings createFloatingIslandsSettings() {
		return new ChunkGeneratorSettings(
			GenerationShapeConfig.FLOATING_ISLANDS,
			Blocks.STONE.getDefaultState(),
			Blocks.WATER.getDefaultState(),
			DensityFunctions.createFloatingIslandsNoiseRouter(),
			VanillaSurfaceRules.createDefaultRule(false, false, false),
			List.of(),
			-64,
			false,
			false,
			false,
			true
		);
	}

	static {
		register(OVERWORLD, createSurfaceSettings(false, false));
		register(LARGE_BIOMES, createSurfaceSettings(false, true));
		register(AMPLIFIED, createSurfaceSettings(true, false));
		register(NETHER, createNetherSettings());
		register(END, createEndSettings());
		register(CAVES, createCavesSettings());
		register(FLOATING_ISLANDS, createFloatingIslandsSettings());
	}
}
