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
import net.minecraft.world.gen.feature.StructureFeature;

public final class ChunkGeneratorSettings {
	public static final Codec<ChunkGeneratorSettings> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StructuresConfig.CODEC.fieldOf("structures").forGetter(ChunkGeneratorSettings::getStructuresConfig),
					GenerationShapeConfig.CODEC.fieldOf("noise").forGetter(ChunkGeneratorSettings::getGenerationShapeConfig),
					BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorSettings::getDefaultBlock),
					BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorSettings::getDefaultFluid),
					Codec.intRange(-20, 276).fieldOf("bedrock_roof_position").forGetter(ChunkGeneratorSettings::getBedrockCeilingY),
					Codec.intRange(-20, 276).fieldOf("bedrock_floor_position").forGetter(ChunkGeneratorSettings::getBedrockFloorY),
					Codec.intRange(0, 255).fieldOf("sea_level").forGetter(ChunkGeneratorSettings::getSeaLevel),
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
	private final Optional<Identifier> id;
	public static final ChunkGeneratorSettings OVERWORLD = register(method_30643(new StructuresConfig(true), false, new Identifier("overworld")));
	public static final ChunkGeneratorSettings AMPLIFIED = register(method_30643(new StructuresConfig(true), true, new Identifier("amplified")));
	public static final ChunkGeneratorSettings NETHER = register(
		method_30641(new StructuresConfig(false), Blocks.NETHERRACK.getDefaultState(), Blocks.LAVA.getDefaultState(), new Identifier("nether"))
	);
	public static final ChunkGeneratorSettings END = register(
		method_30642(new StructuresConfig(false), Blocks.END_STONE.getDefaultState(), Blocks.AIR.getDefaultState(), new Identifier("end"), true, true)
	);
	public static final ChunkGeneratorSettings CAVES = register(
		method_30641(new StructuresConfig(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), new Identifier("caves"))
	);
	public static final ChunkGeneratorSettings FLOATING_ISLANDS = register(
		method_30642(new StructuresConfig(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), new Identifier("floating_islands"), false, false)
	);

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
		this(structuresConfig, generationShapeConfig, defaultBlock, defaultFluid, bedrockCeilingY, bedrockFloorY, seaLevel, mobGenerationDisabled, Optional.empty());
	}

	private ChunkGeneratorSettings(
		StructuresConfig structuresConfig,
		GenerationShapeConfig generationShapeConfig,
		BlockState defaultBlock,
		BlockState defaultFluid,
		int bedrockCeilingY,
		int bedrockFloorY,
		int seaLevel,
		boolean mobGenerationDisabled,
		Optional<Identifier> id
	) {
		this.structuresConfig = structuresConfig;
		this.generationShapeConfig = generationShapeConfig;
		this.defaultBlock = defaultBlock;
		this.defaultFluid = defaultFluid;
		this.bedrockCeilingY = bedrockCeilingY;
		this.bedrockFloorY = bedrockFloorY;
		this.seaLevel = seaLevel;
		this.mobGenerationDisabled = mobGenerationDisabled;
		this.id = id;
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

	@Deprecated
	protected boolean isMobGenerationDisabled() {
		return this.mobGenerationDisabled;
	}

	public boolean isIdEqual(ChunkGeneratorSettings settings) {
		return Objects.equals(this.id, settings.id);
	}

	private static ChunkGeneratorSettings register(ChunkGeneratorSettings settings) {
		BuiltinRegistries.add(BuiltinRegistries.CHUNK_GENERATOR_SETTINGS, (Identifier)settings.id.orElseThrow(IllegalStateException::new), settings);
		return settings;
	}

	private static ChunkGeneratorSettings method_30642(
		StructuresConfig structuresConfig, BlockState blockState, BlockState blockState2, Identifier identifier, boolean bl, boolean bl2
	) {
		return new ChunkGeneratorSettings(
			structuresConfig,
			new GenerationShapeConfig(
				128, new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0), new SlideConfig(-3000, 64, -46), new SlideConfig(-30, 7, 1), 2, 1, 0.0, 0.0, true, false, bl2, false
			),
			blockState,
			blockState2,
			-10,
			-10,
			0,
			bl,
			Optional.of(identifier)
		);
	}

	private static ChunkGeneratorSettings method_30641(StructuresConfig structuresConfig, BlockState blockState, BlockState blockState2, Identifier identifier) {
		Map<StructureFeature<?>, StructureConfig> map = Maps.<StructureFeature<?>, StructureConfig>newHashMap(StructuresConfig.DEFAULT_STRUCTURES);
		map.put(StructureFeature.RUINED_PORTAL, new StructureConfig(25, 10, 34222645));
		return new ChunkGeneratorSettings(
			new StructuresConfig(Optional.ofNullable(structuresConfig.getStronghold()), map),
			new GenerationShapeConfig(
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
			blockState,
			blockState2,
			0,
			0,
			32,
			false,
			Optional.of(identifier)
		);
	}

	private static ChunkGeneratorSettings method_30643(StructuresConfig structuresConfig, boolean bl, Identifier identifier) {
		double d = 0.9999999814507745;
		return new ChunkGeneratorSettings(
			structuresConfig,
			new GenerationShapeConfig(
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
				bl
			),
			Blocks.STONE.getDefaultState(),
			Blocks.WATER.getDefaultState(),
			-10,
			0,
			63,
			false,
			Optional.of(identifier)
		);
	}
}
