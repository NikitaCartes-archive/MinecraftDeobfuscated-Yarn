package net.minecraft.world.gen.chunk;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.MultiNoiseParameters;
import net.minecraft.world.gen.feature.StructureFeature;

public final class ChunkGeneratorSettings {
	public static final Codec<ChunkGeneratorSettings> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StructuresConfig.CODEC.fieldOf("structures").forGetter(ChunkGeneratorSettings::getStructuresConfig),
					GenerationShapeConfig.CODEC.fieldOf("noise").forGetter(ChunkGeneratorSettings::getGenerationShapeConfig),
					MultiNoiseParameters.CODEC.fieldOf("octaves").forGetter(ChunkGeneratorSettings::getBiomeSource),
					BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorSettings::getDefaultBlock),
					BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorSettings::getDefaultFluid),
					Codec.INT.fieldOf("bedrock_roof_position").forGetter(ChunkGeneratorSettings::getBedrockCeilingY),
					Codec.INT.fieldOf("bedrock_floor_position").forGetter(ChunkGeneratorSettings::getBedrockFloorY),
					Codec.INT.fieldOf("sea_level").forGetter(ChunkGeneratorSettings::getSeaLevel),
					Codec.BOOL.fieldOf("disable_mob_generation").forGetter(ChunkGeneratorSettings::isMobGenerationDisabled),
					Codec.BOOL.fieldOf("aquifers_enabled").forGetter(ChunkGeneratorSettings::hasAquifers),
					Codec.BOOL.fieldOf("noise_caves_enabled").forGetter(ChunkGeneratorSettings::hasNoiseCaves),
					Codec.BOOL.fieldOf("deepslate_enabled").forGetter(ChunkGeneratorSettings::hasDeepslate),
					Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(ChunkGeneratorSettings::hasOreVeins),
					Codec.BOOL.fieldOf("noodle_caves_enabled").forGetter(ChunkGeneratorSettings::hasNoodleCaves)
				)
				.apply(instance, ChunkGeneratorSettings::new)
	);
	public static final Codec<Supplier<ChunkGeneratorSettings>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, CODEC);
	private final StructuresConfig structuresConfig;
	private final GenerationShapeConfig generationShapeConfig;
	private final MultiNoiseParameters biomeSource;
	private final BlockState defaultBlock;
	private final BlockState defaultFluid;
	private final int bedrockCeilingY;
	private final int bedrockFloorY;
	private final int seaLevel;
	private final boolean mobGenerationDisabled;
	private final boolean aquifers;
	private final boolean noiseCaves;
	private final boolean deepslate;
	private final boolean oreVeins;
	private final boolean noodleCaves;
	public static final RegistryKey<ChunkGeneratorSettings> OVERWORLD = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("overworld"));
	public static final RegistryKey<ChunkGeneratorSettings> AMPLIFIED = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("amplified"));
	public static final RegistryKey<ChunkGeneratorSettings> NETHER = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("nether"));
	public static final RegistryKey<ChunkGeneratorSettings> END = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("end"));
	public static final RegistryKey<ChunkGeneratorSettings> CAVES = RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("caves"));
	public static final RegistryKey<ChunkGeneratorSettings> FLOATING_ISLANDS = RegistryKey.of(
		Registry.CHUNK_GENERATOR_SETTINGS_KEY, new Identifier("floating_islands")
	);
	private static final ChunkGeneratorSettings INSTANCE = register(OVERWORLD, createSurfaceSettings(new StructuresConfig(true), false));

	private ChunkGeneratorSettings(
		StructuresConfig structuresConfig,
		GenerationShapeConfig generationShapeConfig,
		MultiNoiseParameters biomeSource,
		BlockState defaultBlock,
		BlockState defaultFluid,
		int bedrockFloorY,
		int seaLevel,
		int minSurfaceLevel,
		boolean mobGenerationDisabled,
		boolean aquifers,
		boolean noiseCaves,
		boolean deepslate,
		boolean oreVeins,
		boolean noodleCaves
	) {
		this.structuresConfig = structuresConfig;
		this.generationShapeConfig = generationShapeConfig;
		this.biomeSource = biomeSource;
		this.defaultBlock = defaultBlock;
		this.defaultFluid = defaultFluid;
		this.bedrockCeilingY = bedrockFloorY;
		this.bedrockFloorY = seaLevel;
		this.seaLevel = minSurfaceLevel;
		this.mobGenerationDisabled = mobGenerationDisabled;
		this.aquifers = aquifers;
		this.noiseCaves = noiseCaves;
		this.deepslate = deepslate;
		this.oreVeins = oreVeins;
		this.noodleCaves = noodleCaves;
	}

	public StructuresConfig getStructuresConfig() {
		return this.structuresConfig;
	}

	public GenerationShapeConfig getGenerationShapeConfig() {
		return this.generationShapeConfig;
	}

	public MultiNoiseParameters getBiomeSource() {
		return this.biomeSource;
	}

	public BlockState getDefaultBlock() {
		return this.defaultBlock;
	}

	public BlockState getDefaultFluid() {
		return this.defaultFluid;
	}

	/**
	 * Returns the Y level of the bedrock ceiling.
	 * 
	 * <p>If a number less than 1 is returned, the ceiling will not be generated.
	 */
	public int getBedrockCeilingY() {
		return this.bedrockCeilingY;
	}

	/**
	 * Returns the Y level of the bedrock floor.
	 * 
	 * <p>If a number greater than 255 is returned, the floor will not be generated.
	 */
	public int getBedrockFloorY() {
		return this.bedrockFloorY;
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

	public boolean hasDeepslate() {
		return this.deepslate;
	}

	public boolean hasOreVeins() {
		return this.oreVeins;
	}

	public boolean hasNoodleCaves() {
		return this.noodleCaves;
	}

	public boolean equals(RegistryKey<ChunkGeneratorSettings> registryKey) {
		return Objects.equals(this, BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.get(registryKey));
	}

	private static ChunkGeneratorSettings register(RegistryKey<ChunkGeneratorSettings> registryKey, ChunkGeneratorSettings settings) {
		return BuiltinRegistries.add(BuiltinRegistries.CHUNK_GENERATOR_SETTINGS, registryKey.getValue(), settings);
	}

	public static ChunkGeneratorSettings getInstance() {
		return INSTANCE;
	}

	private static ChunkGeneratorSettings createIslandSettings(
		StructuresConfig structuresConfig, BlockState defaultBlock, BlockState defaultFluid, boolean bl, boolean bl2
	) {
		return new ChunkGeneratorSettings(
			structuresConfig,
			GenerationShapeConfig.create(
				0,
				128,
				new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0),
				new SlideConfig(-23.4375, 64, -46),
				new SlideConfig(-0.234375, 7, 1),
				2,
				1,
				0.0,
				0.0,
				true,
				false,
				bl2,
				false,
				true
			),
			new MultiNoiseParameters(
				new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0),
				new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0),
				new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0),
				new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0),
				new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0),
				new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0)
			),
			defaultBlock,
			defaultFluid,
			Integer.MIN_VALUE,
			Integer.MIN_VALUE,
			0,
			bl,
			false,
			false,
			false,
			false,
			false
		);
	}

	private static ChunkGeneratorSettings createUndergroundSettings(StructuresConfig structuresConfig, BlockState defaultBlock, BlockState defaultFluid) {
		Map<StructureFeature<?>, StructureConfig> map = Maps.<StructureFeature<?>, StructureConfig>newHashMap(StructuresConfig.DEFAULT_STRUCTURES);
		map.put(StructureFeature.RUINED_PORTAL, new StructureConfig(25, 10, 34222645));
		return new ChunkGeneratorSettings(
			new StructuresConfig(Optional.ofNullable(structuresConfig.getStronghold()), map),
			GenerationShapeConfig.create(
				0,
				128,
				new NoiseSamplingConfig(1.0, 3.0, 80.0, 60.0),
				new SlideConfig(0.9375, 3, 0),
				new SlideConfig(2.5, 4, -1),
				1,
				2,
				0.0,
				-0.030078125,
				false,
				false,
				false,
				false,
				true
			),
			new MultiNoiseParameters(
				new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0),
				new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0),
				new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0),
				new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0),
				new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0),
				new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0)
			),
			defaultBlock,
			defaultFluid,
			0,
			0,
			32,
			false,
			false,
			false,
			false,
			false,
			false
		);
	}

	private static ChunkGeneratorSettings createSurfaceSettings(StructuresConfig structuresConfig, boolean amplified) {
		double d = 0.9999999814507745;
		return new ChunkGeneratorSettings(
			structuresConfig,
			GenerationShapeConfig.create(
				-64,
				384,
				new NoiseSamplingConfig(0.9999999814507745, 0.9999999814507745, 80.0, 160.0),
				new SlideConfig(-0.078125, 2, 8),
				new SlideConfig(0.1171875, 3, 0),
				1,
				2,
				1.0,
				-0.51875,
				true,
				true,
				false,
				amplified,
				false
			),
			new MultiNoiseParameters(
				new DoublePerlinNoiseSampler.NoiseParameters(-9, 1.5, 0.0, 1.0, 0.0, 0.0, 0.0),
				new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0),
				new DoublePerlinNoiseSampler.NoiseParameters(-9, 1.0, 1.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0),
				new DoublePerlinNoiseSampler.NoiseParameters(-9, 1.0, 1.0, 0.0, 1.0, 1.0),
				new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0),
				new DoublePerlinNoiseSampler.NoiseParameters(-3, 1.0, 1.0, 1.0, 0.0)
			),
			Blocks.STONE.getDefaultState(),
			Blocks.WATER.getDefaultState(),
			Integer.MIN_VALUE,
			0,
			63,
			false,
			true,
			true,
			true,
			true,
			true
		);
	}

	static {
		register(AMPLIFIED, createSurfaceSettings(new StructuresConfig(true), true));
		register(NETHER, createUndergroundSettings(new StructuresConfig(false), Blocks.NETHERRACK.getDefaultState(), Blocks.LAVA.getDefaultState()));
		register(END, createIslandSettings(new StructuresConfig(false), Blocks.END_STONE.getDefaultState(), Blocks.AIR.getDefaultState(), true, true));
		register(CAVES, createUndergroundSettings(new StructuresConfig(true), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState()));
		register(FLOATING_ISLANDS, createIslandSettings(new StructuresConfig(true), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), false, false));
	}
}
