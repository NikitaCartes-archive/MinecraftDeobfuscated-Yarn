package net.minecraft.world.gen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import org.slf4j.Logger;

public class StructureFeature<C extends FeatureConfig> {
	public static final BiMap<String, StructureFeature<?>> STRUCTURES = HashBiMap.create();
	private static final Map<StructureFeature<?>, GenerationStep.Feature> STRUCTURE_TO_GENERATION_STEP = Maps.<StructureFeature<?>, GenerationStep.Feature>newHashMap();
	private static final Logger LOGGER = LogUtils.getLogger();
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
	public static final StructureFeature<RangeFeatureConfig> NETHER_FOSSIL = register(
		"Nether_Fossil", new NetherFossilFeature(RangeFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION
	);
	public static final StructureFeature<StructurePoolFeatureConfig> BASTION_REMNANT = register(
		"Bastion_Remnant", new BastionRemnantFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES
	);
	public static final List<StructureFeature<?>> LAND_MODIFYING_STRUCTURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE, NETHER_FOSSIL, STRONGHOLD);
	public static final int field_31518 = 8;
	private final Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> codec;
	private final StructureGeneratorFactory<C> piecesGenerator;
	private final PostPlacementProcessor postProcessor;

	private static <F extends StructureFeature<?>> F register(String name, F structureFeature, GenerationStep.Feature step) {
		STRUCTURES.put(name.toLowerCase(Locale.ROOT), structureFeature);
		STRUCTURE_TO_GENERATION_STEP.put(structureFeature, step);
		return Registry.register(Registry.STRUCTURE_FEATURE, name.toLowerCase(Locale.ROOT), structureFeature);
	}

	public StructureFeature(Codec<C> configCodec, StructureGeneratorFactory<C> piecesGenerator) {
		this(configCodec, piecesGenerator, PostPlacementProcessor.EMPTY);
	}

	public StructureFeature(Codec<C> configCodec, StructureGeneratorFactory<C> piecesGenerator, PostPlacementProcessor postPlacementProcessor) {
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
	public static BlockPos getLocatedPos(RandomSpreadStructurePlacement randomSpreadStructurePlacement, ChunkPos chunkPos) {
		return new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ()).add(randomSpreadStructurePlacement.locateOffset());
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
		C featureConfig,
		HeightLimitView heightLimitView,
		Predicate<RegistryEntry<Biome>> predicate
	) {
		Optional<StructurePiecesGenerator<C>> optional = this.piecesGenerator
			.createGenerator(
				new StructureGeneratorFactory.Context<>(
					chunkGenerator, biomeSource, worldSeed, pos, featureConfig, heightLimitView, predicate, structureManager, registryManager
				)
			);
		if (optional.isPresent()) {
			StructurePiecesCollector structurePiecesCollector = new StructurePiecesCollector();
			ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
			chunkRandom.setCarverSeed(worldSeed, pos.x, pos.z);
			((StructurePiecesGenerator)optional.get())
				.generatePieces(
					structurePiecesCollector,
					new StructurePiecesGenerator.Context<>(featureConfig, chunkGenerator, structureManager, pos, heightLimitView, chunkRandom, worldSeed)
				);
			StructureStart<C> structureStart = new StructureStart<>(this, pos, structureReferences, structurePiecesCollector.toList());
			if (structureStart.hasChildren()) {
				return structureStart;
			}
		}

		return StructureStart.DEFAULT;
	}

	public boolean canGenerate(
		DynamicRegistryManager registryManager,
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		StructureManager structureManager,
		long worldSeed,
		ChunkPos pos,
		C config,
		HeightLimitView world,
		Predicate<RegistryEntry<Biome>> biomePredicate
	) {
		return this.piecesGenerator
			.createGenerator(
				new StructureGeneratorFactory.Context<>(chunkGenerator, biomeSource, worldSeed, pos, config, world, biomePredicate, structureManager, registryManager)
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
