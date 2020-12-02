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
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.StructureFeature;

public final class ChunkGeneratorSettings {
	public static final Codec<ChunkGeneratorSettings> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StructuresConfig.CODEC.fieldOf("structures").forGetter(ChunkGeneratorSettings::getStructuresConfig),
					GenerationShapeConfig.CODEC.fieldOf("noise").forGetter(ChunkGeneratorSettings::getGenerationShapeConfig),
					BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorSettings::getDefaultBlock),
					BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorSettings::getDefaultFluid),
					Codec.INT.fieldOf("bedrock_roof_position").forGetter(ChunkGeneratorSettings::getBedrockCeilingY),
					Codec.INT.fieldOf("bedrock_floor_position").forGetter(ChunkGeneratorSettings::getBedrockFloorY),
					Codec.INT.fieldOf("sea_level").forGetter(ChunkGeneratorSettings::getSeaLevel),
					Codec.BOOL.fieldOf("disable_mob_generation").forGetter(ChunkGeneratorSettings::isMobGenerationDisabled)
				)
				.apply(instance, ChunkGeneratorSettings::new)
	);
	public static final Codec<Supplier<ChunkGeneratorSettings>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.NOISE_SETTINGS_WORLDGEN, CODEC);
	private final StructuresConfig structuresConfig;
	private final GenerationShapeConfig generationShapeConfig;
	private final BlockState defaultBlock;
	private final BlockState defaultFluid;
	private final int bedrockCeilingY;
	private final int bedrockFloorY;
	private final int seaLevel;
	private final boolean mobGenerationDisabled;
	public static final RegistryKey<ChunkGeneratorSettings> OVERWORLD = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("overworld"));
	public static final RegistryKey<ChunkGeneratorSettings> AMPLIFIED = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("amplified"));
	public static final RegistryKey<ChunkGeneratorSettings> NETHER = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("nether"));
	public static final RegistryKey<ChunkGeneratorSettings> END = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("end"));
	public static final RegistryKey<ChunkGeneratorSettings> CAVES = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("caves"));
	public static final RegistryKey<ChunkGeneratorSettings> FLOATING_ISLANDS = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("floating_islands"));
	private static final ChunkGeneratorSettings INSTANCE = register(OVERWORLD, createSurfaceSettings(new StructuresConfig(true), false, OVERWORLD.getValue()));

	private ChunkGeneratorSettings(
		StructuresConfig structuresConfig,
		GenerationShapeConfig generationShapeConfig,
		BlockState defaultBlock,
		BlockState defaultFluid,
		int bedrockCeilingY,
		int bedrockFloorY,
		int seaLevel,
		boolean mobGenerationDisabled
	) {
		this.structuresConfig = structuresConfig;
		this.generationShapeConfig = generationShapeConfig;
		this.defaultBlock = defaultBlock;
		this.defaultFluid = defaultFluid;
		this.bedrockCeilingY = bedrockCeilingY;
		this.bedrockFloorY = bedrockFloorY;
		this.seaLevel = seaLevel;
		this.mobGenerationDisabled = mobGenerationDisabled;
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

	public boolean equals(RegistryKey<ChunkGeneratorSettings> registryKey) {
		return Objects.equals(this, BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.get(registryKey));
	}

	private static ChunkGeneratorSettings register(RegistryKey<ChunkGeneratorSettings> registryKey, ChunkGeneratorSettings settings) {
		BuiltinRegistries.add(BuiltinRegistries.CHUNK_GENERATOR_SETTINGS, registryKey.getValue(), settings);
		return settings;
	}

	public static ChunkGeneratorSettings getInstance() {
		return INSTANCE;
	}

	private static ChunkGeneratorSettings createIslandSettings(
		StructuresConfig structuresConfig,
		BlockState defaultBlock,
		BlockState defaultFluid,
		Identifier id,
		boolean mobGenerationDisabled,
		boolean islandNoiseOverride
	) {
		return new ChunkGeneratorSettings(
			structuresConfig,
			GenerationShapeConfig.method_32994(
				0,
				128,
				new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0),
				new SlideConfig(-3000, 64, -46),
				new SlideConfig(-30, 7, 1),
				2,
				1,
				0.0,
				0.0,
				true,
				false,
				islandNoiseOverride,
				false
			),
			defaultBlock,
			defaultFluid,
			Integer.MIN_VALUE,
			Integer.MIN_VALUE,
			0,
			mobGenerationDisabled
		);
	}

	private static ChunkGeneratorSettings createUndergroundSettings(
		StructuresConfig structuresConfig, BlockState defaultBlock, BlockState defaultFluid, Identifier id
	) {
		Map<StructureFeature<?>, StructureConfig> map = Maps.<StructureFeature<?>, StructureConfig>newHashMap(StructuresConfig.DEFAULT_STRUCTURES);
		map.put(StructureFeature.RUINED_PORTAL, new StructureConfig(25, 10, 34222645));
		return new ChunkGeneratorSettings(
			new StructuresConfig(Optional.ofNullable(structuresConfig.getStronghold()), map),
			GenerationShapeConfig.method_32994(
				0,
				128,
				new NoiseSamplingConfig(1.0, 3.0, 80.0, 60.0),
				new SlideConfig(120, 3, 0),
				new SlideConfig(320, 4, -1),
				1,
				2,
				0.0,
				0.019921875,
				false,
				false,
				false,
				false
			),
			defaultBlock,
			defaultFluid,
			0,
			0,
			32,
			false
		);
	}

	private static ChunkGeneratorSettings createSurfaceSettings(StructuresConfig structuresConfig, boolean amplified, Identifier id) {
		double d = 0.9999999814507745;
		return new ChunkGeneratorSettings(
			structuresConfig,
			GenerationShapeConfig.method_32994(
				0,
				256,
				new NoiseSamplingConfig(0.9999999814507745, 0.9999999814507745, 80.0, 160.0),
				new SlideConfig(-10, 3, 0),
				new SlideConfig(-30, 0, 0),
				1,
				2,
				1.0,
				-0.46875,
				true,
				true,
				false,
				amplified
			),
			Blocks.STONE.getDefaultState(),
			Blocks.WATER.getDefaultState(),
			Integer.MIN_VALUE,
			0,
			63,
			false
		);
	}

	static {
		register(AMPLIFIED, createSurfaceSettings(new StructuresConfig(true), true, AMPLIFIED.getValue()));
		register(
			NETHER, createUndergroundSettings(new StructuresConfig(false), Blocks.NETHERRACK.getDefaultState(), Blocks.LAVA.getDefaultState(), NETHER.getValue())
		);
		register(END, createIslandSettings(new StructuresConfig(false), Blocks.END_STONE.getDefaultState(), Blocks.AIR.getDefaultState(), END.getValue(), true, true));
		register(CAVES, createUndergroundSettings(new StructuresConfig(true), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), CAVES.getValue()));
		register(
			FLOATING_ISLANDS,
			createIslandSettings(new StructuresConfig(true), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), FLOATING_ISLANDS.getValue(), false, false)
		);
	}
}
