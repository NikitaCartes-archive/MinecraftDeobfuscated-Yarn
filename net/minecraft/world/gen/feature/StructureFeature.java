/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.nbt.NbtCompound;
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
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.StructurePresence;
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
import net.minecraft.world.gen.feature.BastionRemnantFeature;
import net.minecraft.world.gen.feature.BuriedTreasureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.DesertPyramidFeature;
import net.minecraft.world.gen.feature.EndCityFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.IglooFeature;
import net.minecraft.world.gen.feature.JungleTempleFeature;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import net.minecraft.world.gen.feature.NetherFossilFeature;
import net.minecraft.world.gen.feature.OceanMonumentFeature;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.PillagerOutpostFeature;
import net.minecraft.world.gen.feature.RangeFeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeature;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.StrongholdFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import net.minecraft.world.gen.feature.SwampHutFeature;
import net.minecraft.world.gen.feature.VillageFeature;
import net.minecraft.world.gen.feature.WoodlandMansionFeature;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class StructureFeature<C extends FeatureConfig> {
    public static final BiMap<String, StructureFeature<?>> STRUCTURES = HashBiMap.create();
    private static final Map<StructureFeature<?>, GenerationStep.Feature> STRUCTURE_TO_GENERATION_STEP = Maps.newHashMap();
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final StructureFeature<StructurePoolFeatureConfig> PILLAGER_OUTPOST = StructureFeature.register("Pillager_Outpost", new PillagerOutpostFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<MineshaftFeatureConfig> MINESHAFT = StructureFeature.register("Mineshaft", new MineshaftFeature(MineshaftFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> MANSION = StructureFeature.register("Mansion", new WoodlandMansionFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> JUNGLE_PYRAMID = StructureFeature.register("Jungle_Pyramid", new JungleTempleFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> DESERT_PYRAMID = StructureFeature.register("Desert_Pyramid", new DesertPyramidFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> IGLOO = StructureFeature.register("Igloo", new IglooFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<RuinedPortalFeatureConfig> RUINED_PORTAL = StructureFeature.register("Ruined_Portal", new RuinedPortalFeature(RuinedPortalFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<ShipwreckFeatureConfig> SHIPWRECK = StructureFeature.register("Shipwreck", new ShipwreckFeature(ShipwreckFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> SWAMP_HUT = StructureFeature.register("Swamp_Hut", new SwampHutFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> STRONGHOLD = StructureFeature.register("Stronghold", new StrongholdFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.STRONGHOLDS);
    public static final StructureFeature<DefaultFeatureConfig> MONUMENT = StructureFeature.register("Monument", new OceanMonumentFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<OceanRuinFeatureConfig> OCEAN_RUIN = StructureFeature.register("Ocean_Ruin", new OceanRuinFeature(OceanRuinFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> FORTRESS = StructureFeature.register("Fortress", new NetherFortressFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION);
    public static final StructureFeature<DefaultFeatureConfig> END_CITY = StructureFeature.register("EndCity", new EndCityFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<ProbabilityConfig> BURIED_TREASURE = StructureFeature.register("Buried_Treasure", new BuriedTreasureFeature(ProbabilityConfig.CODEC), GenerationStep.Feature.UNDERGROUND_STRUCTURES);
    public static final StructureFeature<StructurePoolFeatureConfig> VILLAGE = StructureFeature.register("Village", new VillageFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<RangeFeatureConfig> NETHER_FOSSIL = StructureFeature.register("Nether_Fossil", new NetherFossilFeature(RangeFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION);
    public static final StructureFeature<StructurePoolFeatureConfig> BASTION_REMNANT = StructureFeature.register("Bastion_Remnant", new BastionRemnantFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final List<StructureFeature<?>> LAND_MODIFYING_STRUCTURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE, NETHER_FOSSIL, STRONGHOLD);
    public static final int field_31518 = 8;
    private final Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> codec;
    private final StructureGeneratorFactory<C> piecesGenerator;
    private final PostPlacementProcessor postProcessor;

    private static <F extends StructureFeature<?>> F register(String name, F structureFeature, GenerationStep.Feature step) {
        STRUCTURES.put(name.toLowerCase(Locale.ROOT), structureFeature);
        STRUCTURE_TO_GENERATION_STEP.put(structureFeature, step);
        return (F)Registry.register(Registry.STRUCTURE_FEATURE, name.toLowerCase(Locale.ROOT), structureFeature);
    }

    public StructureFeature(Codec<C> configCodec, StructureGeneratorFactory<C> piecesGenerator) {
        this(configCodec, piecesGenerator, PostPlacementProcessor.EMPTY);
    }

    public StructureFeature(Codec<C> configCodec, StructureGeneratorFactory<C> piecesGenerator, PostPlacementProcessor postPlacementProcessor) {
        this.codec = ((MapCodec)configCodec.fieldOf("config")).xmap(config -> new ConfiguredStructureFeature<FeatureConfig, StructureFeature>(this, (FeatureConfig)config), configuredFeature -> configuredFeature.config).codec();
        this.piecesGenerator = piecesGenerator;
        this.postProcessor = postPlacementProcessor;
    }

    /**
     * Gets the step during which this structure will participate in world generation.
     * Structures will generate before other features in the same generation step.
     */
    public GenerationStep.Feature getGenerationStep() {
        return STRUCTURE_TO_GENERATION_STEP.get(this);
    }

    public static void init() {
    }

    @Nullable
    public static StructureStart<?> readStructureStart(StructureContext context, NbtCompound nbt, long worldSeed) {
        String string = nbt.getString("id");
        if ("INVALID".equals(string)) {
            return StructureStart.DEFAULT;
        }
        StructureFeature<?> structureFeature = Registry.STRUCTURE_FEATURE.get(new Identifier(string.toLowerCase(Locale.ROOT)));
        if (structureFeature == null) {
            LOGGER.error("Unknown feature id: {}", (Object)string);
            return null;
        }
        ChunkPos chunkPos = new ChunkPos(nbt.getInt("ChunkX"), nbt.getInt("ChunkZ"));
        int i = nbt.getInt("references");
        NbtList nbtList = nbt.getList("Children", 10);
        try {
            StructurePiecesList structurePiecesList = StructurePiecesList.fromNbt(nbtList, context);
            if (structureFeature == MONUMENT) {
                structurePiecesList = OceanMonumentFeature.modifyPiecesOnRead(chunkPos, worldSeed, structurePiecesList);
            }
            return new StructureStart(structureFeature, chunkPos, i, structurePiecesList);
        } catch (Exception exception) {
            LOGGER.error("Failed Start with id {}", (Object)string, (Object)exception);
            return null;
        }
    }

    public Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> getCodec() {
        return this.codec;
    }

    public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configure(C config) {
        return new ConfiguredStructureFeature<C, StructureFeature>(this, config);
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
    public BlockPos locateStructure(WorldView world, StructureAccessor structureAccessor, BlockPos searchStartPos, int searchRadius, boolean skipExistingChunks, long worldSeed, StructureConfig config) {
        int i = config.getSpacing();
        int j = ChunkSectionPos.getSectionCoord(searchStartPos.getX());
        int k = ChunkSectionPos.getSectionCoord(searchStartPos.getZ());
        block0: for (int l = 0; l <= searchRadius; ++l) {
            for (int m = -l; m <= l; ++m) {
                boolean bl = m == -l || m == l;
                for (int n = -l; n <= l; ++n) {
                    int p;
                    int o;
                    ChunkPos chunkPos;
                    StructurePresence structurePresence;
                    boolean bl2;
                    boolean bl3 = bl2 = n == -l || n == l;
                    if (!bl && !bl2 || (structurePresence = structureAccessor.getStructurePresence(chunkPos = this.getStartChunk(config, worldSeed, o = j + i * m, p = k + i * n), this, skipExistingChunks)) == StructurePresence.START_NOT_PRESENT) continue;
                    if (!skipExistingChunks && structurePresence == StructurePresence.START_PRESENT) {
                        return this.getLocatedPos(chunkPos);
                    }
                    Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS);
                    StructureStart<?> structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk), this, chunk);
                    if (structureStart != null && structureStart.hasChildren()) {
                        if (skipExistingChunks && structureStart.isInExistingChunk()) {
                            structureAccessor.incrementReferences(structureStart);
                            return this.getLocatedPos(structureStart.getPos());
                        }
                        if (!skipExistingChunks) {
                            return this.getLocatedPos(structureStart.getPos());
                        }
                    }
                    if (l == 0) break;
                }
                if (l == 0) continue block0;
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
        int n;
        int m;
        int i = config.getSpacing();
        int j = config.getSeparation();
        int k = Math.floorDiv(x, i);
        int l = Math.floorDiv(z, i);
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
        chunkRandom.setRegionSeed(seed, k, l, config.getSalt());
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
    public StructureStart<?> tryPlaceStart(DynamicRegistryManager registryManager, ChunkGenerator chunkGenerator, BiomeSource biomeSource, StructureManager structureManager, long worldSeed, ChunkPos pos, int structureReferences, StructureConfig structureConfig, C config, HeightLimitView world, Predicate<Biome> biomePredicate) {
        Optional<StructurePiecesGenerator<C>> optional;
        ChunkPos chunkPos = this.getStartChunk(structureConfig, worldSeed, pos.x, pos.z);
        if (pos.x == chunkPos.x && pos.z == chunkPos.z && (optional = this.piecesGenerator.createGenerator(new StructureGeneratorFactory.Context<C>(chunkGenerator, biomeSource, worldSeed, pos, config, world, biomePredicate, structureManager, registryManager))).isPresent()) {
            StructurePiecesCollector structurePiecesCollector = new StructurePiecesCollector();
            ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
            chunkRandom.setCarverSeed(worldSeed, pos.x, pos.z);
            optional.get().generatePieces(structurePiecesCollector, new StructurePiecesGenerator.Context<C>(config, chunkGenerator, structureManager, pos, world, chunkRandom, worldSeed));
            StructureStart structureStart = new StructureStart(this, pos, structureReferences, structurePiecesCollector.toList());
            if (structureStart.hasChildren()) {
                return structureStart;
            }
        }
        return StructureStart.DEFAULT;
    }

    public boolean canGenerate(DynamicRegistryManager registryManager, ChunkGenerator chunkGenerator, BiomeSource biomeSource, StructureManager structureManager, long worldSeed, ChunkPos pos, C config, HeightLimitView world, Predicate<Biome> biomePredicate) {
        return this.piecesGenerator.createGenerator(new StructureGeneratorFactory.Context<C>(chunkGenerator, biomeSource, worldSeed, pos, config, world, biomePredicate, structureManager, registryManager)).isPresent();
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

