package net.minecraft.world.gen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_6833;
import net.minecraft.class_6834;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureFeature<C extends FeatureConfig> {
	public static final BiMap<String, StructureFeature<?>> STRUCTURES = HashBiMap.create();
	private static final Map<StructureFeature<?>, GenerationStep.Feature> STRUCTURE_TO_GENERATION_STEP = Maps.<StructureFeature<?>, GenerationStep.Feature>newHashMap();
	private static final Logger LOGGER = LogManager.getLogger();
	public static final StructureFeature<StructurePoolFeatureConfig> PILLAGER_OUTPOST = register(
		"Pillager_Outpost", new PillagerOutpostFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<MineshaftFeatureConfig> MINESHAFT = register(
		"Mineshaft", new MineshaftFeature(MineshaftFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> MANSION = register(
		"Mansion", new WoodlandMansionFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> JUNGLE_PYRAMID = register(
		"Jungle_Pyramid", new JungleTempleFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> DESERT_PYRAMID = register(
		"Desert_Pyramid", new DesertPyramidFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> IGLOO = register(
		"Igloo", new IglooFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<RuinedPortalFeatureConfig> RUINED_PORTAL = register(
		"Ruined_Portal", new RuinedPortalFeature(RuinedPortalFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<ShipwreckFeatureConfig> SHIPWRECK = register(
		"Shipwreck", new ShipwreckFeature(ShipwreckFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> SWAMP_HUT = register(
		"Swamp_Hut", new SwampHutFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> STRONGHOLD = register(
		"Stronghold", new StrongholdFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.STRONGHOLDS
	);
	public static final StructureFeature<DefaultFeatureConfig> MONUMENT = register(
		"Monument", new OceanMonumentFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<OceanRuinFeatureConfig> OCEAN_RUIN = register(
		"Ocean_Ruin", new OceanRuinFeature(OceanRuinFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<DefaultFeatureConfig> FORTRESS = register(
		"Fortress", new NetherFortressFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION
	);
	public static final StructureFeature<DefaultFeatureConfig> END_CITY = register(
		"EndCity", new EndCityFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<ProbabilityConfig> BURIED_TREASURE = register(
		"Buried_Treasure", new BuriedTreasureFeature(ProbabilityConfig.CODEC), GenerationStep.Feature.UNDERGROUND_STRUCTURES
	);
	public static final StructureFeature<StructurePoolFeatureConfig> VILLAGE = register(
		"Village", new VillageFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final StructureFeature<RangeDecoratorConfig> NETHER_FOSSIL = register(
		"Nether_Fossil", new NetherFossilFeature(RangeDecoratorConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION
	);
	public static final StructureFeature<StructurePoolFeatureConfig> BASTION_REMNANT = register(
		"Bastion_Remnant", new BastionRemnantFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final List<StructureFeature<?>> LAND_MODIFYING_STRUCTURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE, NETHER_FOSSIL, STRONGHOLD);
	public static final int field_31518 = 8;
	private final Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> codec;
	private final class_6834<C> piecesGenerator;
	private final PostPlacementProcessor postProcessor;

	private static <F extends StructureFeature<?>> F register(String name, F structureFeature, GenerationStep.Feature step) {
		STRUCTURES.put(name.toLowerCase(Locale.ROOT), structureFeature);
		STRUCTURE_TO_GENERATION_STEP.put(structureFeature, step);
		return Registry.register(Registry.STRUCTURE_FEATURE, name.toLowerCase(Locale.ROOT), structureFeature);
	}

	public StructureFeature(Codec<C> configCodec, class_6834<C> piecesGenerator) {
		this(configCodec, piecesGenerator, PostPlacementProcessor.EMPTY);
	}

	public StructureFeature(Codec<C> configCodec, class_6834<C> piecesGenerator, PostPlacementProcessor postPlacementProcessor) {
		this.codec = configCodec.fieldOf("config")
			.<ConfiguredStructureFeature<C, StructureFeature<C>>>xmap(
				config -> new ConfiguredStructureFeature<>(this, (C)config), configuredFeature -> configuredFeature.config
			)
			.codec();
		this.piecesGenerator = piecesGenerator;
		this.postProcessor = postPlacementProcessor;
	}

	/**
	 * Gets the step during which this structure will participate in world generation.
	 * Structures will generate before other features in the same generation step.
	 */
	public GenerationStep.Feature getGenerationStep() {
		return (GenerationStep.Feature)STRUCTURE_TO_GENERATION_STEP.get(this);
	}

	public static void init() {
	}

	@Nullable
	public static StructureStart<?> readStructureStart(StructureContext context, NbtCompound nbt, long worldSeed) {
		String string = nbt.getString("id");
		if ("INVALID".equals(string)) {
			return StructureStart.DEFAULT;
		} else {
			StructureFeature<?> structureFeature = Registry.STRUCTURE_FEATURE.get(new Identifier(string.toLowerCase(Locale.ROOT)));
			if (structureFeature == null) {
				LOGGER.error("Unknown feature id: {}", string);
				return null;
			} else {
				ChunkPos chunkPos = new ChunkPos(nbt.getInt("ChunkX"), nbt.getInt("ChunkZ"));
				int i = nbt.getInt("references");
				NbtList nbtList = nbt.getList("Children", NbtElement.COMPOUND_TYPE);

				try {
					StructurePiecesList structurePiecesList = StructurePiecesList.fromNbt(nbtList, context);
					if (structureFeature == MONUMENT) {
						structurePiecesList = OceanMonumentFeature.modifyPiecesOnRead(chunkPos, worldSeed, structurePiecesList);
					}

					return new StructureStart<>(structureFeature, chunkPos, i, structurePiecesList);
				} catch (Exception var10) {
					LOGGER.error("Failed Start with id {}", string, var10);
					return null;
				}
			}
		}
	}

	public Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> getCodec() {
		return this.codec;
	}

	public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configure(C config) {
		return new ConfiguredStructureFeature<>(this, config);
	}

	/**
	 * {@return a block position for feature location}
	 */
	public BlockPos getLocatedPos(ChunkPos chunkPos) {
		return new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ());
	}

	/**
	 * Tries to find the closest structure of this type near a given block.
	 * <p>
	 * This method relies on the given world generation settings (seed and placement configuration)
	 * to match the time at which the structure was generated, otherwise it will not be found.
	 * <p>
	 * New chunks will only be generated up to the {@link net.minecraft.world.chunk.ChunkStatus#STRUCTURE_STARTS} phase by this method.
	 * 
	 * @return {@code null} if no structure could be found within the given search radius
	 * 
	 * @param searchRadius the search radius in chunks around the chunk the given block position is in; a radius of 0 will only search in the given chunk
	 */
	@Nullable
	public BlockPos locateStructure(
		WorldView worldView,
		StructureAccessor structureAccessor,
		BlockPos searchStartPos,
		int searchRadius,
		boolean skipExistingChunks,
		long worldSeed,
		StructureConfig config
	) {
		int i = config.getSpacing();
		int j = ChunkSectionPos.getSectionCoord(searchStartPos.getX());
		int k = ChunkSectionPos.getSectionCoord(searchStartPos.getZ());

		for (int l = 0; l <= searchRadius; l++) {
			for (int m = -l; m <= l; m++) {
				boolean bl = m == -l || m == l;

				for (int n = -l; n <= l; n++) {
					boolean bl2 = n == -l || n == l;
					if (bl || bl2) {
						int o = j + i * m;
						int p = k + i * n;
						ChunkPos chunkPos = this.getStartChunk(config, worldSeed, o, p);
						class_6833 lv = structureAccessor.method_39783(chunkPos, this, skipExistingChunks);
						if (lv != class_6833.START_NOT_PRESENT) {
							if (!skipExistingChunks && lv == class_6833.START_PRESENT) {
								return this.getLocatedPos(chunkPos);
							}

							Chunk chunk = worldView.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS);
							StructureStart<?> structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk), this, chunk);
							if (structureStart != null && structureStart.hasChildren()) {
								if (skipExistingChunks && structureStart.isInExistingChunk()) {
									structureAccessor.method_39784(structureStart);
									return this.getLocatedPos(structureStart.getPos());
								}

								if (!skipExistingChunks) {
									return this.getLocatedPos(structureStart.getPos());
								}
							}

							if (l == 0) {
								break;
							}
						}
					}
				}

				if (l == 0) {
					break;
				}
			}
		}

		return null;
	}

	/**
	 * If true, this structure's start position will be uniformly distributed within
	 * a placement grid cell. If false, the structure's starting point will be biased
	 * towards the center of the cell.
	 */
	protected boolean isUniformDistribution() {
		return true;
	}

	/**
	 * Determines the cell of the structure placement grid a chunk belongs to, and
	 * returns the chunk within that cell, that this structure will actually be placed at.
	 * 
	 * <p>If the {@link StructureConfig} uses a separation setting greater than 0, the
	 * placement will be constrained to [0, spacing - separation] within the grid cell.
	 * If a non-uniform distribution is used for placement, then this also moves
	 * the center towards the origin.
	 * 
	 * @see #isUniformDistribution()
	 */
	public final ChunkPos getStartChunk(StructureConfig config, long seed, int x, int z) {
		int i = config.getSpacing();
		int j = config.getSeparation();
		int k = Math.floorDiv(x, i);
		int l = Math.floorDiv(z, i);
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setRegionSeed(seed, k, l, config.getSalt());
		int m;
		int n;
		if (this.isUniformDistribution()) {
			m = chunkRandom.nextInt(i - j);
			n = chunkRandom.nextInt(i - j);
		} else {
			m = (chunkRandom.nextInt(i - j) + chunkRandom.nextInt(i - j)) / 2;
			n = (chunkRandom.nextInt(i - j) + chunkRandom.nextInt(i - j)) / 2;
		}

		return new ChunkPos(k * i + m, l * i + n);
	}

	/**
	 * Tries to place a starting point for this type of structure in the given chunk.
	 * <p>
	 * If this structure doesn't have a starting point in the chunk, {@link StructureStart#DEFAULT}
	 * will be returned.
	 */
	public StructureStart<?> tryPlaceStart(
		DynamicRegistryManager registryManager,
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		StructureManager structureManager,
		long worldSeed,
		ChunkPos pos,
		int structureReferences,
		StructureConfig structureConfig,
		C config,
		HeightLimitView world,
		Predicate<Biome> biomeLimit
	) {
		ChunkPos chunkPos = this.getStartChunk(structureConfig, worldSeed, pos.x, pos.z);
		if (pos.x == chunkPos.x && pos.z == chunkPos.z) {
			Optional<StructurePiecesGenerator<C>> optional = this.piecesGenerator
				.createGenerator(new class_6834.class_6835(chunkGenerator, biomeSource, worldSeed, pos, config, world, biomeLimit, structureManager, registryManager));
			if (optional.isPresent()) {
				StructurePiecesCollector structurePiecesCollector = new StructurePiecesCollector();
				ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
				chunkRandom.setCarverSeed(worldSeed, pos.x, pos.z);
				((StructurePiecesGenerator)optional.get())
					.generatePieces(
						structurePiecesCollector, new StructurePiecesGenerator.Context(config, chunkGenerator, structureManager, pos, world, chunkRandom, worldSeed)
					);
				StructureStart<C> structureStart = new StructureStart<>(this, pos, structureReferences, structurePiecesCollector.toList());
				if (structureStart.hasChildren()) {
					return structureStart;
				}
			}
		}

		return StructureStart.DEFAULT;
	}

	public boolean method_39821(
		DynamicRegistryManager dynamicRegistryManager,
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		StructureManager structureManager,
		long l,
		ChunkPos chunkPos,
		C featureConfig,
		HeightLimitView heightLimitView,
		Predicate<Biome> predicate
	) {
		return this.piecesGenerator
			.createGenerator(
				new class_6834.class_6835(chunkGenerator, biomeSource, l, chunkPos, featureConfig, heightLimitView, predicate, structureManager, dynamicRegistryManager)
			)
			.isPresent();
	}

	public PostPlacementProcessor getPostProcessor() {
		return this.postProcessor;
	}

	public String getName() {
		return (String)STRUCTURES.inverse().get(this);
	}

	public BlockBox calculateBoundingBox(BlockBox box) {
		return box;
	}
}
