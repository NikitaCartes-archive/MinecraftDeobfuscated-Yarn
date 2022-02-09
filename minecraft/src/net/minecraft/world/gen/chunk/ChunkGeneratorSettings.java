package net.minecraft.world.gen.chunk;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.util.VanillaTerrainParametersCreator;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.surfacebuilder.VanillaSurfaceRules;

public final class ChunkGeneratorSettings {
	public static final Codec<ChunkGeneratorSettings> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StructuresConfig.CODEC.fieldOf("structures").forGetter(ChunkGeneratorSettings::getStructuresConfig),
					GenerationShapeConfig.CODEC.fieldOf("noise").forGetter(ChunkGeneratorSettings::getGenerationShapeConfig),
					BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorSettings::getDefaultBlock),
					BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorSettings::getDefaultFluid),
					MaterialRules.MaterialRule.CODEC.fieldOf("surface_rule").forGetter(ChunkGeneratorSettings::getSurfaceRule),
					Codec.INT.fieldOf("sea_level").forGetter(ChunkGeneratorSettings::getSeaLevel),
					Codec.BOOL.fieldOf("disable_mob_generation").forGetter(ChunkGeneratorSettings::isMobGenerationDisabled),
					Codec.BOOL.fieldOf("aquifers_enabled").forGetter(ChunkGeneratorSettings::hasAquifers),
					Codec.BOOL.fieldOf("noise_caves_enabled").forGetter(ChunkGeneratorSettings::hasNoiseCaves),
					Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(ChunkGeneratorSettings::hasOreVeins),
					Codec.BOOL.fieldOf("noodle_caves_enabled").forGetter(ChunkGeneratorSettings::hasNoodleCaves),
					Codec.BOOL.fieldOf("legacy_random_source").forGetter(ChunkGeneratorSettings::usesLegacyRandom)
				)
				.apply(instance, ChunkGeneratorSettings::new)
	);
	public static final Codec<RegistryEntry<ChunkGeneratorSettings>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, CODEC);
	private final ChunkRandom.RandomProvider randomProvider;
	private final StructuresConfig structuresConfig;
	private final GenerationShapeConfig generationShapeConfig;
	private final BlockState defaultBlock;
	private final BlockState defaultFluid;
	private final MaterialRules.MaterialRule surfaceRule;
	private final int seaLevel;
	private final boolean mobGenerationDisabled;
	private final boolean aquifers;
	private final boolean noiseCaves;
	private final boolean oreVeins;
	private final boolean noodleCaves;
	public static final RegistryKey<ChunkGeneratorSettings> OVERWORLD = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("overworld"));
	public static final RegistryKey<ChunkGeneratorSettings> LARGE_BIOMES = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("large_biomes"));
	public static final RegistryKey<ChunkGeneratorSettings> AMPLIFIED = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("amplified"));
	public static final RegistryKey<ChunkGeneratorSettings> NETHER = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("nether"));
	public static final RegistryKey<ChunkGeneratorSettings> END = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("end"));
	public static final RegistryKey<ChunkGeneratorSettings> CAVES = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("caves"));
	public static final RegistryKey<ChunkGeneratorSettings> FLOATING_ISLANDS = RegistryKey.of(
		Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("floating_islands")
	);

	private ChunkGeneratorSettings(
		StructuresConfig structuresConfig,
		GenerationShapeConfig generationShapeConfig,
		BlockState defaultBlock,
		BlockState defaultFluid,
		MaterialRules.MaterialRule surfaceRule,
		int seaLevel,
		boolean mobGenerationDisabled,
		boolean aquifers,
		boolean noiseCaves,
		boolean oreVeins,
		boolean noodleCaves,
		boolean useLegacyRandom
	) {
		this.structuresConfig = structuresConfig;
		this.generationShapeConfig = generationShapeConfig;
		this.defaultBlock = defaultBlock;
		this.defaultFluid = defaultFluid;
		this.surfaceRule = surfaceRule;
		this.seaLevel = seaLevel;
		this.mobGenerationDisabled = mobGenerationDisabled;
		this.aquifers = aquifers;
		this.noiseCaves = noiseCaves;
		this.oreVeins = oreVeins;
		this.noodleCaves = noodleCaves;
		this.randomProvider = useLegacyRandom ? ChunkRandom.RandomProvider.LEGACY : ChunkRandom.RandomProvider.XOROSHIRO;
	}

	public StructuresConfig getStructuresConfig() {
		return this.structuresConfig;
	}

	public GenerationShapeConfig getGenerationShapeConfig() {
		return this.generationShapeConfig;
	}

	public BlockState getDefaultBlock() {
		return this.defaultBlock;
	}

	public BlockState getDefaultFluid() {
		return this.defaultFluid;
	}

	public MaterialRules.MaterialRule getSurfaceRule() {
		return this.surfaceRule;
	}

	public int getSeaLevel() {
		return this.seaLevel;
	}

	/**
	 * Whether entities will be generated during chunk population.
	 * 
	 * <p>It does not control whether spawns will occur during gameplay.
	 */
	@Deprecated
	protected boolean isMobGenerationDisabled() {
		return this.mobGenerationDisabled;
	}

	public boolean hasAquifers() {
		return this.aquifers;
	}

	public boolean hasNoiseCaves() {
		return this.noiseCaves;
	}

	public boolean hasOreVeins() {
		return this.oreVeins;
	}

	public boolean hasNoodleCaves() {
		return this.noodleCaves;
	}

	public boolean usesLegacyRandom() {
		return this.randomProvider == ChunkRandom.RandomProvider.LEGACY;
	}

	public AbstractRandom createRandom(long seed) {
		return this.getRandomProvider().create(seed);
	}

	public ChunkRandom.RandomProvider getRandomProvider() {
		return this.randomProvider;
	}

	public boolean equals(RegistryKey<ChunkGeneratorSettings> registryKey) {
		return Objects.equals(this, BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.get(registryKey));
	}

	private static void register(RegistryKey<ChunkGeneratorSettings> registryKey, ChunkGeneratorSettings settings) {
		BuiltinRegistries.add(BuiltinRegistries.CHUNK_GENERATOR_SETTINGS, registryKey.getValue(), settings);
	}

	public static RegistryEntry<ChunkGeneratorSettings> getInstance() {
		return (RegistryEntry<ChunkGeneratorSettings>)BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.streamEntries().iterator().next();
	}

	private static ChunkGeneratorSettings createEndSettings() {
		return new ChunkGeneratorSettings(
			new StructuresConfig(false),
			GenerationShapeConfig.create(
				0,
				128,
				new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0),
				new SlideConfig(-23.4375, 64, -46),
				new SlideConfig(-0.234375, 7, 1),
				2,
				1,
				true,
				false,
				false,
				VanillaTerrainParametersCreator.createEndParameters()
			),
			Blocks.END_STONE.getDefaultState(),
			Blocks.AIR.getDefaultState(),
			VanillaSurfaceRules.getEndStoneRule(),
			0,
			true,
			false,
			false,
			false,
			false,
			true
		);
	}

	private static ChunkGeneratorSettings createNetherSettings() {
		Map<StructureFeature<?>, StructurePlacement> map = Maps.<StructureFeature<?>, StructurePlacement>newHashMap(StructuresConfig.DEFAULT_PLACEMENTS);
		map.put(StructureFeature.RUINED_PORTAL, new RandomSpreadStructurePlacement(25, 10, SpreadType.LINEAR, 34222645));
		return new ChunkGeneratorSettings(
			new StructuresConfig(map),
			GenerationShapeConfig.create(
				0,
				128,
				new NoiseSamplingConfig(1.0, 3.0, 80.0, 60.0),
				new SlideConfig(0.9375, 3, 0),
				new SlideConfig(2.5, 4, -1),
				1,
				2,
				false,
				false,
				false,
				VanillaTerrainParametersCreator.createNetherParameters()
			),
			Blocks.NETHERRACK.getDefaultState(),
			Blocks.LAVA.getDefaultState(),
			VanillaSurfaceRules.createNetherSurfaceRule(),
			32,
			false,
			false,
			false,
			false,
			false,
			true
		);
	}

	private static ChunkGeneratorSettings createSurfaceSettings(boolean amplified, boolean largeBiomes) {
		return new ChunkGeneratorSettings(
			new StructuresConfig(true),
			GenerationShapeConfig.create(
				-64,
				384,
				new NoiseSamplingConfig(1.0, 1.0, 80.0, 160.0),
				new SlideConfig(-0.078125, 2, amplified ? 0 : 8),
				new SlideConfig(amplified ? 0.4 : 0.1171875, 3, 0),
				1,
				2,
				false,
				amplified,
				largeBiomes,
				VanillaTerrainParametersCreator.createSurfaceParameters(amplified)
			),
			Blocks.STONE.getDefaultState(),
			Blocks.WATER.getDefaultState(),
			VanillaSurfaceRules.createOverworldSurfaceRule(),
			63,
			false,
			true,
			true,
			true,
			true,
			false
		);
	}

	private static ChunkGeneratorSettings createCavesSettings() {
		return new ChunkGeneratorSettings(
			new StructuresConfig(false),
			GenerationShapeConfig.create(
				-64,
				192,
				new NoiseSamplingConfig(1.0, 3.0, 80.0, 60.0),
				new SlideConfig(0.9375, 3, 0),
				new SlideConfig(2.5, 4, -1),
				1,
				2,
				false,
				false,
				false,
				VanillaTerrainParametersCreator.createCavesParameters()
			),
			Blocks.STONE.getDefaultState(),
			Blocks.WATER.getDefaultState(),
			VanillaSurfaceRules.createDefaultRule(false, true, true),
			32,
			false,
			false,
			false,
			false,
			false,
			true
		);
	}

	private static ChunkGeneratorSettings createFloatingIslandsSettings() {
		return new ChunkGeneratorSettings(
			new StructuresConfig(true),
			GenerationShapeConfig.create(
				0,
				256,
				new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0),
				new SlideConfig(-23.4375, 64, -46),
				new SlideConfig(-0.234375, 7, 1),
				2,
				1,
				false,
				false,
				false,
				VanillaTerrainParametersCreator.createFloatingIslandsParameters()
			),
			Blocks.STONE.getDefaultState(),
			Blocks.WATER.getDefaultState(),
			VanillaSurfaceRules.createDefaultRule(false, false, false),
			-64,
			false,
			false,
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
