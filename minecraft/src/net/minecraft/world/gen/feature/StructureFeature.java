package net.minecraft.world.gen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_6621;
import net.minecraft.class_6622;
import net.minecraft.class_6624;
import net.minecraft.class_6625;
import net.minecraft.class_6626;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
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
import net.minecraft.world.gen.random.RandomSeed;
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
	private final class_6622<C> field_34929;
	private final class_6621 field_34930;

	private static <F extends StructureFeature<?>> F register(String name, F structureFeature, GenerationStep.Feature step) {
		STRUCTURES.put(name.toLowerCase(Locale.ROOT), structureFeature);
		STRUCTURE_TO_GENERATION_STEP.put(structureFeature, step);
		return Registry.register(Registry.STRUCTURE_FEATURE, name.toLowerCase(Locale.ROOT), structureFeature);
	}

	public StructureFeature(Codec<C> codec, class_6622<C> arg) {
		this(codec, arg, class_6621.field_34938);
	}

	public StructureFeature(Codec<C> codec, class_6622<C> arg, class_6621 arg2) {
		this.codec = codec.fieldOf("config")
			.<ConfiguredStructureFeature<C, StructureFeature<C>>>xmap(
				featureConfig -> new ConfiguredStructureFeature<>(this, (C)featureConfig), configuredStructureFeature -> configuredStructureFeature.config
			)
			.codec();
		this.field_34929 = arg;
		this.field_34930 = arg2;
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
	public static StructureStart<?> readStructureStart(class_6625 arg, NbtCompound nbt, long worldSeed) {
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
					class_6624 lv = class_6624.method_38711(nbtList, arg);
					if (structureFeature == MONUMENT) {
						lv = OceanMonumentFeature.method_38680(chunkPos, worldSeed, lv);
					}

					return new StructureStart<>(structureFeature, chunkPos, i, lv);
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

	public BlockPos method_38671(ChunkPos chunkPos) {
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
		WorldView world,
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
		int l = 0;

		for (ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed())); l <= searchRadius; l++) {
			for (int m = -l; m <= l; m++) {
				boolean bl = m == -l || m == l;

				for (int n = -l; n <= l; n++) {
					boolean bl2 = n == -l || n == l;
					if (bl || bl2) {
						int o = j + i * m;
						int p = k + i * n;
						ChunkPos chunkPos = this.getStartChunk(config, worldSeed, chunkRandom, o, p);
						Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS);
						StructureStart<?> structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk), this, chunk);
						if (structureStart != null && structureStart.hasChildren()) {
							if (skipExistingChunks && structureStart.isInExistingChunk()) {
								structureStart.incrementReferences();
								return this.method_38671(structureStart.getPos());
							}

							if (!skipExistingChunks) {
								return this.method_38671(structureStart.getPos());
							}
						}

						if (l == 0) {
							break;
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
	 * <p>
	 * If the {@link StructureConfig} uses a separation setting greater than 0, the
	 * placement will be constrained to [0, spacing - separation] within the grid cell.
	 * If a non-uniform distribution is used for placement, then this also moves
	 * the center towards the origin.
	 * 
	 * @see #isUniformDistribution()
	 */
	public final ChunkPos getStartChunk(StructureConfig config, long worldSeed, ChunkRandom placementRandom, int chunkX, int chunkY) {
		int i = config.getSpacing();
		int j = config.getSeparation();
		int k = Math.floorDiv(chunkX, i);
		int l = Math.floorDiv(chunkY, i);
		placementRandom.setRegionSeed(worldSeed, k, l, config.getSalt());
		int m;
		int n;
		if (this.isUniformDistribution()) {
			m = placementRandom.nextInt(i - j);
			n = placementRandom.nextInt(i - j);
		} else {
			m = (placementRandom.nextInt(i - j) + placementRandom.nextInt(i - j)) / 2;
			n = (placementRandom.nextInt(i - j) + placementRandom.nextInt(i - j)) / 2;
		}

		return new ChunkPos(k * i + m, l * i + n);
	}

	/**
	 * Checks if this structure can <em>actually</em> be placed at a potential structure position determined via
	 * {@link #getStartChunk}. Specific structures override this method to reduce the spawn probability or
	 * restrict the spawn in some other way.
	 */
	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long worldSeed,
		ChunkRandom random,
		ChunkPos pos,
		ChunkPos chunkPos,
		C featureConfig,
		HeightLimitView heightLimitView
	) {
		return true;
	}

	/**
	 * Tries to place a starting point for this type of structure in the given chunk.
	 * <p>
	 * If this structure doesn't have a starting point in the chunk, {@link StructureStart#DEFAULT}
	 * will be returned.
	 */
	public StructureStart<?> tryPlaceStart(
		DynamicRegistryManager dynamicRegistryManager,
		ChunkGenerator generator,
		BiomeSource biomeSource,
		StructureManager manager,
		long worldSeed,
		ChunkPos pos,
		int i,
		ChunkRandom chunkRandom,
		StructureConfig structureConfig,
		C featureConfig,
		HeightLimitView heightLimitView,
		Predicate<Biome> predicate
	) {
		ChunkPos chunkPos = this.getStartChunk(structureConfig, worldSeed, chunkRandom, pos.x, pos.z);
		if (pos.x == chunkPos.x
			&& pos.z == chunkPos.z
			&& this.shouldStartAt(generator, biomeSource, worldSeed, chunkRandom, pos, chunkPos, featureConfig, heightLimitView)) {
			class_6626 lv = new class_6626();
			this.field_34929
				.generatePieces(
					lv,
					featureConfig,
					new class_6622.class_6623(
						dynamicRegistryManager,
						generator,
						manager,
						pos,
						predicate,
						heightLimitView,
						Util.make(new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed())), chunkRandomx -> chunkRandomx.setCarverSeed(worldSeed, pos.x, pos.z)),
						worldSeed
					)
				);
			StructureStart<C> structureStart = new StructureStart<>(this, pos, i, lv.method_38714());
			if (structureStart.hasChildren()) {
				return structureStart;
			}
		}

		return StructureStart.DEFAULT;
	}

	public class_6621 method_38690() {
		return this.field_34930;
	}

	public String getName() {
		return (String)STRUCTURES.inverse().get(this);
	}

	public BlockBox calculateBoundingBox(BlockBox blockBox) {
		return blockBox;
	}
}
