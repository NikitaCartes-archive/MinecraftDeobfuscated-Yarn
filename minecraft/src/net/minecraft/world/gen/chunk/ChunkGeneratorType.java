package net.minecraft.world.gen.chunk;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.StructureFeature;

public final class ChunkGeneratorType {
	public static final Codec<ChunkGeneratorType> field_24780 = RecordCodecBuilder.create(
		instance -> instance.group(
					StructuresConfig.CODEC.fieldOf("structures").forGetter(ChunkGeneratorType::getConfig),
					NoiseConfig.CODEC.fieldOf("noise").forGetter(ChunkGeneratorType::method_28559),
					BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorType::getDefaultBlock),
					BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorType::getDefaultFluid),
					Codec.intRange(-20, 276).fieldOf("bedrock_roof_position").forGetter(ChunkGeneratorType::getBedrockCeilingY),
					Codec.intRange(-20, 276).fieldOf("bedrock_floor_position").forGetter(ChunkGeneratorType::getBedrockFloorY),
					Codec.intRange(0, 255).fieldOf("sea_level").forGetter(ChunkGeneratorType::method_28561),
					Codec.BOOL.fieldOf("disable_mob_generation").forGetter(ChunkGeneratorType::method_28562)
				)
				.apply(instance, ChunkGeneratorType::new)
	);
	public static final Codec<ChunkGeneratorType> field_24781 = Codec.either(ChunkGeneratorType.Preset.field_24788, field_24780)
		.xmap(
			either -> either.map(ChunkGeneratorType.Preset::getChunkGeneratorType, Function.identity()),
			chunkGeneratorType -> (Either)chunkGeneratorType.field_24787.map(Either::left).orElseGet(() -> Either.right(chunkGeneratorType))
		);
	private final StructuresConfig config;
	private final NoiseConfig field_24782;
	private final BlockState defaultBlock;
	private final BlockState defaultFluid;
	private final int bedrockCeilingY;
	private final int bedrockFloorY;
	private final int field_24785;
	private final boolean field_24786;
	private final Optional<ChunkGeneratorType.Preset> field_24787;

	private ChunkGeneratorType(StructuresConfig config, NoiseConfig noiseConfig, BlockState defaultBlock, BlockState defaultFluid, int i, int j, int k, boolean bl) {
		this(config, noiseConfig, defaultBlock, defaultFluid, i, j, k, bl, Optional.empty());
	}

	private ChunkGeneratorType(
		StructuresConfig config,
		NoiseConfig noiseConfig,
		BlockState defaultBlock,
		BlockState defaultFluid,
		int i,
		int j,
		int k,
		boolean bl,
		Optional<ChunkGeneratorType.Preset> optional
	) {
		this.config = config;
		this.field_24782 = noiseConfig;
		this.defaultBlock = defaultBlock;
		this.defaultFluid = defaultFluid;
		this.bedrockCeilingY = i;
		this.bedrockFloorY = j;
		this.field_24785 = k;
		this.field_24786 = bl;
		this.field_24787 = optional;
	}

	public StructuresConfig getConfig() {
		return this.config;
	}

	public NoiseConfig method_28559() {
		return this.field_24782;
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

	public int method_28561() {
		return this.field_24785;
	}

	@Deprecated
	protected boolean method_28562() {
		return this.field_24786;
	}

	public boolean method_28555(ChunkGeneratorType.Preset preset) {
		return Objects.equals(this.field_24787, Optional.of(preset));
	}

	public static class Preset {
		private static final Map<Identifier, ChunkGeneratorType.Preset> BY_ID = Maps.<Identifier, ChunkGeneratorType.Preset>newHashMap();
		public static final Codec<ChunkGeneratorType.Preset> field_24788 = Identifier.CODEC
			.<ChunkGeneratorType.Preset>flatXmap(
				identifier -> (DataResult)Optional.ofNullable(BY_ID.get(identifier))
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error("Unknown preset: " + identifier)),
				preset -> DataResult.success(preset.id)
			)
			.stable();
		public static final ChunkGeneratorType.Preset OVERWORLD = new ChunkGeneratorType.Preset(
			"overworld", preset -> createOverworldType(new StructuresConfig(true), false, preset)
		);
		public static final ChunkGeneratorType.Preset AMPLIFIED = new ChunkGeneratorType.Preset(
			"amplified", preset -> createOverworldType(new StructuresConfig(true), true, preset)
		);
		public static final ChunkGeneratorType.Preset NETHER = new ChunkGeneratorType.Preset(
			"nether", preset -> createCavesType(new StructuresConfig(false), Blocks.NETHERRACK.getDefaultState(), Blocks.LAVA.getDefaultState(), preset)
		);
		public static final ChunkGeneratorType.Preset END = new ChunkGeneratorType.Preset(
			"end", preset -> createIslandsType(new StructuresConfig(false), Blocks.END_STONE.getDefaultState(), Blocks.AIR.getDefaultState(), preset, true, true)
		);
		public static final ChunkGeneratorType.Preset CAVES = new ChunkGeneratorType.Preset(
			"caves", preset -> createCavesType(new StructuresConfig(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), preset)
		);
		public static final ChunkGeneratorType.Preset FLOATING_ISLANDS = new ChunkGeneratorType.Preset(
			"floating_islands",
			preset -> createIslandsType(new StructuresConfig(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), preset, false, false)
		);
		private final Text text;
		private final Identifier id;
		private final ChunkGeneratorType chunkGeneratorType;

		public Preset(String id, Function<ChunkGeneratorType.Preset, ChunkGeneratorType> generatorTypeGetter) {
			this.id = new Identifier(id);
			this.text = new TranslatableText("generator.noise." + id);
			this.chunkGeneratorType = (ChunkGeneratorType)generatorTypeGetter.apply(this);
			BY_ID.put(this.id, this);
		}

		public ChunkGeneratorType getChunkGeneratorType() {
			return this.chunkGeneratorType;
		}

		private static ChunkGeneratorType createIslandsType(
			StructuresConfig config, BlockState defaultBlock, BlockState defaultFluid, ChunkGeneratorType.Preset preset, boolean bl, boolean bl2
		) {
			return new ChunkGeneratorType(
				config,
				new NoiseConfig(
					128, new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0), new SlideConfig(-3000, 64, -46), new SlideConfig(-30, 7, 1), 2, 1, 0.0, 0.0, true, false, bl2, false
				),
				defaultBlock,
				defaultFluid,
				-10,
				-10,
				0,
				bl,
				Optional.of(preset)
			);
		}

		private static ChunkGeneratorType createCavesType(StructuresConfig config, BlockState defaultBlock, BlockState defaultFluid, ChunkGeneratorType.Preset preset) {
			Map<StructureFeature<?>, StructureConfig> map = Maps.<StructureFeature<?>, StructureConfig>newHashMap(StructuresConfig.DEFAULT_STRUCTURES);
			map.put(StructureFeature.RUINED_PORTAL, new StructureConfig(25, 10, 34222645));
			return new ChunkGeneratorType(
				new StructuresConfig(Optional.ofNullable(config.getStronghold()), map),
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
				defaultBlock,
				defaultFluid,
				0,
				0,
				32,
				false,
				Optional.of(preset)
			);
		}

		private static ChunkGeneratorType createOverworldType(StructuresConfig structuresConfig, boolean bl, ChunkGeneratorType.Preset preset) {
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
				Optional.of(preset)
			);
		}
	}
}
