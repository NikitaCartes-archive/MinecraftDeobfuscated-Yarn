package net.minecraft.world.gen.chunk;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

public final class ChunkGeneratorType {
	public static final MapCodec<ChunkGeneratorType> field_24780 = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					StructuresConfig.CODEC.fieldOf("structures").forGetter(ChunkGeneratorType::getConfig),
					NoiseConfig.CODEC.fieldOf("noise").forGetter(ChunkGeneratorType::getNoiseConfig),
					BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorType::getDefaultBlock),
					BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorType::getDefaultFluid),
					Codec.intRange(-20, 276).fieldOf("bedrock_roof_position").forGetter(ChunkGeneratorType::getBedrockCeilingY),
					Codec.intRange(-20, 276).fieldOf("bedrock_floor_position").forGetter(ChunkGeneratorType::getBedrockFloorY),
					Codec.intRange(0, 255).fieldOf("sea_level").forGetter(ChunkGeneratorType::getSeaLevel),
					Codec.BOOL.fieldOf("disable_mob_generation").forGetter(ChunkGeneratorType::method_28562)
				)
				.apply(instance, ChunkGeneratorType::new)
	);
	public static final Codec<Supplier<ChunkGeneratorType>> field_24781 = RegistryElementCodec.of(Registry.NOISE_SETTINGS_WORLDGEN, field_24780);
	private final StructuresConfig config;
	private final NoiseConfig noiseConfig;
	private final BlockState defaultBlock;
	private final BlockState defaultFluid;
	private final int bedrockCeilingY;
	private final int bedrockFloorY;
	private final int seaLevel;
	private final boolean field_24786;
	private final Optional<Identifier> field_24787;
	public static final ChunkGeneratorType field_26355 = method_30644(method_30643(new StructuresConfig(true), false, new Identifier("overworld")));
	public static final ChunkGeneratorType field_26356 = method_30644(method_30643(new StructuresConfig(true), true, new Identifier("amplified")));
	public static final ChunkGeneratorType field_26357 = method_30644(
		method_30641(new StructuresConfig(false), Blocks.NETHERRACK.getDefaultState(), Blocks.LAVA.getDefaultState(), new Identifier("nether"))
	);
	public static final ChunkGeneratorType field_26358 = method_30644(
		method_30642(new StructuresConfig(false), Blocks.END_STONE.getDefaultState(), Blocks.AIR.getDefaultState(), new Identifier("end"), true, true)
	);
	public static final ChunkGeneratorType field_26359 = method_30644(
		method_30641(new StructuresConfig(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), new Identifier("caves"))
	);
	public static final ChunkGeneratorType field_26360 = method_30644(
		method_30642(new StructuresConfig(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), new Identifier("floating_islands"), false, false)
	);

	private ChunkGeneratorType(
		StructuresConfig config, NoiseConfig noiseConfig, BlockState defaultBlock, BlockState defaultFluid, int i, int j, int seaLevel, boolean bl
	) {
		this(config, noiseConfig, defaultBlock, defaultFluid, i, j, seaLevel, bl, Optional.empty());
	}

	private ChunkGeneratorType(
		StructuresConfig config,
		NoiseConfig noiseConfig,
		BlockState defaultBlock,
		BlockState defaultFluid,
		int i,
		int j,
		int seaLevel,
		boolean bl,
		Optional<Identifier> optional
	) {
		this.config = config;
		this.noiseConfig = noiseConfig;
		this.defaultBlock = defaultBlock;
		this.defaultFluid = defaultFluid;
		this.bedrockCeilingY = i;
		this.bedrockFloorY = j;
		this.seaLevel = seaLevel;
		this.field_24786 = bl;
		this.field_24787 = optional;
	}

	public StructuresConfig getConfig() {
		return this.config;
	}

	public NoiseConfig getNoiseConfig() {
		return this.noiseConfig;
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
	protected boolean method_28562() {
		return this.field_24786;
	}

	public boolean method_28555(ChunkGeneratorType chunkGeneratorType) {
		return Objects.equals(this.field_24787, chunkGeneratorType.field_24787);
	}

	private static ChunkGeneratorType method_30644(ChunkGeneratorType chunkGeneratorType) {
		BuiltinRegistries.add(BuiltinRegistries.field_26375, (Identifier)chunkGeneratorType.field_24787.orElseThrow(IllegalStateException::new), chunkGeneratorType);
		return chunkGeneratorType;
	}

	private static ChunkGeneratorType method_30642(
		StructuresConfig structuresConfig, BlockState blockState, BlockState blockState2, Identifier identifier, boolean bl, boolean bl2
	) {
		return new ChunkGeneratorType(
			structuresConfig,
			new NoiseConfig(
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

	private static ChunkGeneratorType method_30641(StructuresConfig structuresConfig, BlockState blockState, BlockState blockState2, Identifier identifier) {
		Map<StructureFeature<?>, StructureConfig> map = Maps.<StructureFeature<?>, StructureConfig>newHashMap(StructuresConfig.DEFAULT_STRUCTURES);
		map.put(StructureFeature.RUINED_PORTAL, new StructureConfig(25, 10, 34222645));
		return new ChunkGeneratorType(
			new StructuresConfig(Optional.ofNullable(structuresConfig.getStronghold()), map),
			new NoiseConfig(
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

	private static ChunkGeneratorType method_30643(StructuresConfig structuresConfig, boolean bl, Identifier identifier) {
		double d = 0.9999999814507745;
		return new ChunkGeneratorType(
			structuresConfig,
			new NoiseConfig(
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
