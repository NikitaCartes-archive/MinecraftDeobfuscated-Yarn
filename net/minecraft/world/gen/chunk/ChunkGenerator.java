/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.google.common.base.Stopwatch;
import com.google.common.base.Suppliers;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructurePresence;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.StructureType;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * In charge of shaping, adding biome specific surface blocks, and carving chunks,
 * as well as populating the generated chunks with {@linkplain net.minecraft.world.gen.feature.Feature features} and {@linkplain net.minecraft.entity.Entity entities}.
 * Biome placement starts here, however all vanilla and most modded chunk generators delegate this to a {@linkplain net.minecraft.world.biome.source.BiomeSource biome source}.
 */
public abstract class ChunkGenerator {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<ChunkGenerator> CODEC = Registry.CHUNK_GENERATOR.getCodec().dispatchStable(ChunkGenerator::getCodec, Function.identity());
    protected final Registry<StructureSet> structureSetRegistry;
    protected final BiomeSource biomeSource;
    private final Supplier<List<PlacedFeatureIndexer.IndexedFeatures>> indexedFeaturesListSupplier;
    protected final Optional<RegistryEntryList<StructureSet>> structureOverrides;
    private final Function<RegistryEntry<Biome>, GenerationSettings> generationSettingsGetter;
    private final Map<StructureType, List<StructurePlacement>> structurePlacements = new Object2ObjectOpenHashMap<StructureType, List<StructurePlacement>>();
    private final Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>> concentricRingPositions = new Object2ObjectArrayMap<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>>();
    private boolean hasComputedStructurePlacements;

    protected static <T extends ChunkGenerator> Products.P1<RecordCodecBuilder.Mu<T>, Registry<StructureSet>> createStructureSetRegistryGetter(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(RegistryOps.createRegistryCodec(Registry.STRUCTURE_SET_KEY).forGetter(chunkGenerator -> chunkGenerator.structureSetRegistry));
    }

    public ChunkGenerator(Registry<StructureSet> structureSetRegistry, Optional<RegistryEntryList<StructureSet>> structureOverrides, BiomeSource biomeSource) {
        this(structureSetRegistry, structureOverrides, biomeSource, biomeEntry -> ((Biome)biomeEntry.value()).getGenerationSettings());
    }

    public ChunkGenerator(Registry<StructureSet> structureSetRegistry, Optional<RegistryEntryList<StructureSet>> structureOverrides, BiomeSource biomeSource, Function<RegistryEntry<Biome>, GenerationSettings> generationSettingsGetter) {
        this.structureSetRegistry = structureSetRegistry;
        this.biomeSource = biomeSource;
        this.generationSettingsGetter = generationSettingsGetter;
        this.structureOverrides = structureOverrides;
        this.indexedFeaturesListSupplier = Suppliers.memoize(() -> PlacedFeatureIndexer.collectIndexedFeatures(List.copyOf(biomeSource.getBiomes()), biomeEntry -> ((GenerationSettings)generationSettingsGetter.apply((RegistryEntry<Biome>)biomeEntry)).getFeatures(), true));
    }

    public Stream<RegistryEntry<StructureSet>> streamStructureSets() {
        if (this.structureOverrides.isPresent()) {
            return this.structureOverrides.get().stream();
        }
        return this.structureSetRegistry.streamEntries().map(RegistryEntry::upcast);
    }

    private void computeStructurePlacements(NoiseConfig noiseConfig) {
        Set<RegistryEntry<Biome>> set = this.biomeSource.getBiomes();
        this.streamStructureSets().forEach(structureSet -> {
            StructurePlacement structurePlacement;
            StructureSet structureSet2 = (StructureSet)structureSet.value();
            boolean bl = false;
            for (StructureSet.WeightedEntry weightedEntry : structureSet2.structures()) {
                StructureType structureType2 = weightedEntry.structure().value();
                if (!structureType2.getValidBiomes().stream().anyMatch(set::contains)) continue;
                this.structurePlacements.computeIfAbsent(structureType2, structureType -> new ArrayList()).add(structureSet2.placement());
                bl = true;
            }
            if (bl && (structurePlacement = structureSet2.placement()) instanceof ConcentricRingsStructurePlacement) {
                ConcentricRingsStructurePlacement concentricRingsStructurePlacement = (ConcentricRingsStructurePlacement)structurePlacement;
                this.concentricRingPositions.put(concentricRingsStructurePlacement, this.generateConcentricRingPositions((RegistryEntry<StructureSet>)structureSet, noiseConfig, concentricRingsStructurePlacement));
            }
        });
    }

    private CompletableFuture<List<ChunkPos>> generateConcentricRingPositions(RegistryEntry<StructureSet> structureSet, NoiseConfig noiseConfig, ConcentricRingsStructurePlacement concentricRingsStructurePlacement) {
        if (concentricRingsStructurePlacement.getCount() == 0) {
            return CompletableFuture.completedFuture(List.of());
        }
        return CompletableFuture.supplyAsync(Util.debugSupplier("placement calculation", () -> {
            Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
            ArrayList<ChunkPos> list = new ArrayList<ChunkPos>();
            int i = concentricRingsStructurePlacement.getDistance();
            int j = concentricRingsStructurePlacement.getCount();
            int k = concentricRingsStructurePlacement.getSpread();
            RegistryEntryList<Biome> registryEntryList = concentricRingsStructurePlacement.getPreferredBiomes();
            Random random = Random.create();
            random.setSeed(this instanceof FlatChunkGenerator ? 0L : noiseConfig.getLegacyWorldSeed());
            double d = random.nextDouble() * Math.PI * 2.0;
            int l = 0;
            int m = 0;
            for (int n = 0; n < j; ++n) {
                double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * ((double)i * 2.5);
                int o = (int)Math.round(Math.cos(d) * e);
                int p = (int)Math.round(Math.sin(d) * e);
                Pair<BlockPos, RegistryEntry<Biome>> pair = this.biomeSource.locateBiome(ChunkSectionPos.getOffsetPos(o, 8), 0, ChunkSectionPos.getOffsetPos(p, 8), 112, registryEntryList::contains, random, noiseConfig.getMultiNoiseSampler());
                if (pair != null) {
                    BlockPos blockPos = pair.getFirst();
                    o = ChunkSectionPos.getSectionCoord(blockPos.getX());
                    p = ChunkSectionPos.getSectionCoord(blockPos.getZ());
                }
                list.add(new ChunkPos(o, p));
                d += Math.PI * 2 / (double)k;
                if (++l != k) continue;
                l = 0;
                k += 2 * k / (++m + 1);
                k = Math.min(k, j - n);
                d += random.nextDouble() * Math.PI * 2.0;
            }
            double f = (double)stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) / 1000.0;
            LOGGER.debug("Calculation for {} took {}s", (Object)structureSet, (Object)f);
            return list;
        }), Util.getMainWorkerExecutor());
    }

    protected abstract Codec<? extends ChunkGenerator> getCodec();

    public Optional<RegistryKey<Codec<? extends ChunkGenerator>>> getCodecKey() {
        return Registry.CHUNK_GENERATOR.getKey(this.getCodec());
    }

    public CompletableFuture<Chunk> populateBiomes(Registry<Biome> biomeRegistry, Executor executor, NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.supplyAsync(Util.debugSupplier("init_biomes", () -> {
            chunk.populateBiomes(this.biomeSource, noiseConfig.getMultiNoiseSampler());
            return chunk;
        }), Util.getMainWorkerExecutor());
    }

    /**
     * Generates caves for the given chunk.
     */
    public abstract void carve(ChunkRegion var1, long var2, NoiseConfig var4, BiomeAccess var5, StructureAccessor var6, Chunk var7, GenerationStep.Carver var8);

    /**
     * Tries to find the closest structure of a given type near a given block.
     * <p>
     * New chunks will only be generated up to the {@link net.minecraft.world.chunk.ChunkStatus#STRUCTURE_STARTS} phase by this method.
     * <p>
     * The radius is ignored for strongholds.
     * 
     * @return {@code null} if no structure could be found within the given search radius
     * 
     * @param radius the search radius in chunks around the chunk the given block position is in; a radius of 0 will only search in the given chunk
     * @param skipReferencedStructures whether to exclude structures that were previously located (has positive
     * {@link StructureStart#references})
     */
    @Nullable
    public Pair<BlockPos, RegistryEntry<StructureType>> locateStructure(ServerWorld world, RegistryEntryList<StructureType> structures, BlockPos center, int radius, boolean skipReferencedStructures) {
        Object2ObjectArrayMap<StructurePlacement, Set> map = new Object2ObjectArrayMap<StructurePlacement, Set>();
        for (RegistryEntry registryEntry : structures) {
            for (StructurePlacement structurePlacement : this.getStructurePlacement(registryEntry, world.getChunkManager().getNoiseConfig())) {
                map.computeIfAbsent(structurePlacement, placement -> new ObjectArraySet()).add(registryEntry);
            }
        }
        if (map.isEmpty()) {
            return null;
        }
        Pair<BlockPos, RegistryEntry<StructureType>> pair = null;
        double d = Double.MAX_VALUE;
        StructureAccessor structureAccessor = world.getStructureAccessor();
        ArrayList list = new ArrayList(map.size());
        for (Map.Entry entry : map.entrySet()) {
            StructurePlacement structurePlacement2 = (StructurePlacement)entry.getKey();
            if (structurePlacement2 instanceof ConcentricRingsStructurePlacement) {
                ConcentricRingsStructurePlacement concentricRingsStructurePlacement = (ConcentricRingsStructurePlacement)structurePlacement2;
                Pair<BlockPos, RegistryEntry<StructureType>> pair2 = this.locateConcentricRingsStructure((Set)entry.getValue(), world, structureAccessor, center, skipReferencedStructures, concentricRingsStructurePlacement);
                BlockPos blockPos = pair2.getFirst();
                double e = center.getSquaredDistance(blockPos);
                if (!(e < d)) continue;
                d = e;
                pair = pair2;
                continue;
            }
            if (!(structurePlacement2 instanceof RandomSpreadStructurePlacement)) continue;
            list.add(entry);
        }
        if (!list.isEmpty()) {
            int i = ChunkSectionPos.getSectionCoord(center.getX());
            int j = ChunkSectionPos.getSectionCoord(center.getZ());
            for (int k = 0; k <= radius; ++k) {
                boolean bl = false;
                for (Map.Entry entry : list) {
                    RandomSpreadStructurePlacement randomSpreadStructurePlacement = (RandomSpreadStructurePlacement)entry.getKey();
                    Pair<BlockPos, RegistryEntry<StructureType>> pair3 = ChunkGenerator.locateRandomSpreadStructure((Set)entry.getValue(), world, structureAccessor, i, j, k, skipReferencedStructures, world.getSeed(), randomSpreadStructurePlacement);
                    if (pair3 == null) continue;
                    bl = true;
                    double f = center.getSquaredDistance(pair3.getFirst());
                    if (!(f < d)) continue;
                    d = f;
                    pair = pair3;
                }
                if (!bl) continue;
                return pair;
            }
        }
        return pair;
    }

    @Nullable
    private Pair<BlockPos, RegistryEntry<StructureType>> locateConcentricRingsStructure(Set<RegistryEntry<StructureType>> structures, ServerWorld world, StructureAccessor structureAccessor, BlockPos center, boolean skipReferencedStructures, ConcentricRingsStructurePlacement placement) {
        List<ChunkPos> list = this.getConcentricRingsStartChunks(placement, world.getChunkManager().getNoiseConfig());
        if (list == null) {
            throw new IllegalStateException("Somehow tried to find structures for a placement that doesn't exist");
        }
        Pair<BlockPos, RegistryEntry<StructureType>> pair = null;
        double d = Double.MAX_VALUE;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (ChunkPos chunkPos : list) {
            Pair<BlockPos, RegistryEntry<StructureType>> pair2;
            mutable.set(ChunkSectionPos.getOffsetPos(chunkPos.x, 8), 32, ChunkSectionPos.getOffsetPos(chunkPos.z, 8));
            double e = mutable.getSquaredDistance(center);
            boolean bl = pair == null || e < d;
            if (!bl || (pair2 = ChunkGenerator.locateStructure(structures, world, structureAccessor, skipReferencedStructures, placement, chunkPos)) == null) continue;
            pair = pair2;
            d = e;
        }
        return pair;
    }

    @Nullable
    private static Pair<BlockPos, RegistryEntry<StructureType>> locateRandomSpreadStructure(Set<RegistryEntry<StructureType>> structures, WorldView world, StructureAccessor structureAccessor, int centerChunkX, int centerChunkZ, int radius, boolean skipReferencedStructures, long seed, RandomSpreadStructurePlacement placement) {
        int i = placement.method_41632();
        for (int j = -radius; j <= radius; ++j) {
            boolean bl = j == -radius || j == radius;
            for (int k = -radius; k <= radius; ++k) {
                int m;
                int l;
                ChunkPos chunkPos;
                Pair<BlockPos, RegistryEntry<StructureType>> pair;
                boolean bl2;
                boolean bl3 = bl2 = k == -radius || k == radius;
                if (!bl && !bl2 || (pair = ChunkGenerator.locateStructure(structures, world, structureAccessor, skipReferencedStructures, placement, chunkPos = placement.getStartChunk(seed, l = centerChunkX + i * j, m = centerChunkZ + i * k))) == null) continue;
                return pair;
            }
        }
        return null;
    }

    @Nullable
    private static Pair<BlockPos, RegistryEntry<StructureType>> locateStructure(Set<RegistryEntry<StructureType>> structures, WorldView world, StructureAccessor structureAccessor, boolean skipReferencedStructures, StructurePlacement placement, ChunkPos pos) {
        for (RegistryEntry<StructureType> registryEntry : structures) {
            StructurePresence structurePresence = structureAccessor.getStructurePresence(pos, registryEntry.value(), skipReferencedStructures);
            if (structurePresence == StructurePresence.START_NOT_PRESENT) continue;
            if (!skipReferencedStructures && structurePresence == StructurePresence.START_PRESENT) {
                return Pair.of(placement.getLocatePos(pos), registryEntry);
            }
            Chunk chunk = world.getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_STARTS);
            StructureStart structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk), registryEntry.value(), chunk);
            if (structureStart == null || !structureStart.hasChildren() || skipReferencedStructures && !ChunkGenerator.checkNotReferenced(structureAccessor, structureStart)) continue;
            return Pair.of(placement.getLocatePos(structureStart.getPos()), registryEntry);
        }
        return null;
    }

    private static boolean checkNotReferenced(StructureAccessor structureAccessor, StructureStart start) {
        if (start.isNeverReferenced()) {
            structureAccessor.incrementReferences(start);
            return true;
        }
        return false;
    }

    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        ChunkPos chunkPos2 = chunk.getPos();
        if (SharedConstants.method_37896(chunkPos2)) {
            return;
        }
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos2, world.getBottomSectionCoord());
        BlockPos blockPos = chunkSectionPos.getMinPos();
        Registry<StructureType> registry = world.getRegistryManager().get(Registry.STRUCTURE_KEY);
        Map<Integer, List<StructureType>> map = registry.stream().collect(Collectors.groupingBy(structureType -> structureType.getFeatureGenerationStep().ordinal()));
        List<PlacedFeatureIndexer.IndexedFeatures> list = this.indexedFeaturesListSupplier.get();
        ChunkRandom chunkRandom = new ChunkRandom(new Xoroshiro128PlusPlusRandom(RandomSeed.getSeed()));
        long l = chunkRandom.setPopulationSeed(world.getSeed(), blockPos.getX(), blockPos.getZ());
        ObjectArraySet set = new ObjectArraySet();
        ChunkPos.stream(chunkSectionPos.toChunkPos(), 1).forEach(chunkPos -> {
            Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
            for (ChunkSection chunkSection : chunk.getSectionArray()) {
                chunkSection.getBiomeContainer().method_39793(set::add);
            }
        });
        set.retainAll(this.biomeSource.getBiomes());
        int i = list.size();
        try {
            Registry<PlacedFeature> registry2 = world.getRegistryManager().get(Registry.PLACED_FEATURE_KEY);
            int j = Math.max(GenerationStep.Feature.values().length, i);
            for (int k = 0; k < j; ++k) {
                int m = 0;
                if (structureAccessor.shouldGenerateStructures()) {
                    List list2 = map.getOrDefault(k, Collections.emptyList());
                    for (StructureType structureType2 : list2) {
                        chunkRandom.setDecoratorSeed(l, m, k);
                        Supplier<String> supplier = () -> registry.getKey(structureType2).map(Object::toString).orElseGet(structureType2::toString);
                        try {
                            world.setCurrentlyGeneratingStructureName(supplier);
                            structureAccessor.getStructureStarts(chunkSectionPos, structureType2).forEach(start -> start.place(world, structureAccessor, this, chunkRandom, ChunkGenerator.getBlockBoxForChunk(chunk), chunkPos2));
                        } catch (Exception exception) {
                            CrashReport crashReport = CrashReport.create(exception, "Feature placement");
                            crashReport.addElement("Feature").add("Description", supplier::get);
                            throw new CrashException(crashReport);
                        }
                        ++m;
                    }
                }
                if (k >= i) continue;
                IntArraySet intSet = new IntArraySet();
                for (RegistryEntry registryEntry : set) {
                    List<RegistryEntryList<PlacedFeature>> list3 = this.generationSettingsGetter.apply(registryEntry).getFeatures();
                    if (k >= list3.size()) continue;
                    RegistryEntryList<PlacedFeature> registryEntryList = list3.get(k);
                    PlacedFeatureIndexer.IndexedFeatures indexedFeatures = list.get(k);
                    registryEntryList.stream().map(RegistryEntry::value).forEach(placedFeature -> intSet.add(indexedFeatures.indexMapping().applyAsInt((PlacedFeature)placedFeature)));
                }
                int n = intSet.size();
                int[] is = intSet.toIntArray();
                Arrays.sort(is);
                PlacedFeatureIndexer.IndexedFeatures indexedFeatures2 = list.get(k);
                for (int o = 0; o < n; ++o) {
                    int p = is[o];
                    PlacedFeature placedFeature2 = indexedFeatures2.features().get(p);
                    Supplier<String> supplier2 = () -> registry2.getKey(placedFeature2).map(Object::toString).orElseGet(placedFeature2::toString);
                    chunkRandom.setDecoratorSeed(l, p, k);
                    try {
                        world.setCurrentlyGeneratingStructureName(supplier2);
                        placedFeature2.generate(world, this, chunkRandom, blockPos);
                        continue;
                    } catch (Exception exception2) {
                        CrashReport crashReport2 = CrashReport.create(exception2, "Feature placement");
                        crashReport2.addElement("Feature").add("Description", supplier2::get);
                        throw new CrashException(crashReport2);
                    }
                }
            }
            world.setCurrentlyGeneratingStructureName(null);
        } catch (Exception exception3) {
            CrashReport crashReport3 = CrashReport.create(exception3, "Biome decoration");
            crashReport3.addElement("Generation").add("CenterX", chunkPos2.x).add("CenterZ", chunkPos2.z).add("Seed", l);
            throw new CrashException(crashReport3);
        }
    }

    public boolean shouldStructureGenerateInRange(RegistryEntry<StructureSet> structureSet, NoiseConfig noiseConfig, long seed, int chunkX, int chunkZ, int chunkRange) {
        StructureSet structureSet2 = structureSet.value();
        if (structureSet2 == null) {
            return false;
        }
        StructurePlacement structurePlacement = structureSet2.placement();
        for (int i = chunkX - chunkRange; i <= chunkX + chunkRange; ++i) {
            for (int j = chunkZ - chunkRange; j <= chunkZ + chunkRange; ++j) {
                if (!structurePlacement.shouldGenerate(this, noiseConfig, seed, i, j)) continue;
                return true;
            }
        }
        return false;
    }

    private static BlockBox getBlockBoxForChunk(Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();
        HeightLimitView heightLimitView = chunk.getHeightLimitView();
        int k = heightLimitView.getBottomY() + 1;
        int l = heightLimitView.getTopY() - 1;
        return new BlockBox(i, k, j, i + 15, l, j + 15);
    }

    /**
     * Places the surface blocks of the biomes after the noise has been generated.
     */
    public abstract void buildSurface(ChunkRegion var1, StructureAccessor var2, NoiseConfig var3, Chunk var4);

    public abstract void populateEntities(ChunkRegion var1);

    public int getSpawnHeight(HeightLimitView world) {
        return 64;
    }

    public BiomeSource getBiomeSource() {
        return this.biomeSource;
    }

    public abstract int getWorldHeight();

    public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(RegistryEntry<Biome> biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        Map<StructureType, LongSet> map = accessor.getStructureReferences(pos);
        for (Map.Entry<StructureType, LongSet> entry : map.entrySet()) {
            StructureType structureType = entry.getKey();
            StructureSpawns structureSpawns = structureType.getStructureSpawns().get(group);
            if (structureSpawns == null) continue;
            MutableBoolean mutableBoolean = new MutableBoolean(false);
            Predicate<StructureStart> predicate = structureSpawns.boundingBox() == StructureSpawns.BoundingBox.PIECE ? start -> accessor.structureContains(pos, (StructureStart)start) : start -> start.getBoundingBox().contains(pos);
            accessor.method_41032(structureType, entry.getValue(), start -> {
                if (mutableBoolean.isFalse() && predicate.test((StructureStart)start)) {
                    mutableBoolean.setTrue();
                }
            });
            if (!mutableBoolean.isTrue()) continue;
            return structureSpawns.spawns();
        }
        return biome.value().getSpawnSettings().getSpawnEntries(group);
    }

    /**
     * Determines which structures should start in the given chunk and creates their starting points.
     */
    public void setStructureStarts(DynamicRegistryManager registryManager, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk, StructureManager structureManager, long seed) {
        ChunkPos chunkPos = chunk.getPos();
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk);
        this.streamStructureSets().forEach(structureSet -> {
            StructurePlacement structurePlacement = ((StructureSet)structureSet.value()).placement();
            List<StructureSet.WeightedEntry> list = ((StructureSet)structureSet.value()).structures();
            for (StructureSet.WeightedEntry weightedEntry : list) {
                StructureStart structureStart = structureAccessor.getStructureStart(chunkSectionPos, weightedEntry.structure().value(), chunk);
                if (structureStart == null || !structureStart.hasChildren()) continue;
                return;
            }
            if (!structurePlacement.shouldGenerate(this, noiseConfig, seed, chunkPos.x, chunkPos.z)) {
                return;
            }
            if (list.size() == 1) {
                this.trySetStructureStart(list.get(0), structureAccessor, registryManager, noiseConfig, structureManager, seed, chunk, chunkPos, chunkSectionPos);
                return;
            }
            ArrayList<StructureSet.WeightedEntry> arrayList = new ArrayList<StructureSet.WeightedEntry>(list.size());
            arrayList.addAll(list);
            ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
            chunkRandom.setCarverSeed(seed, chunkPos.x, chunkPos.z);
            int i = 0;
            for (StructureSet.WeightedEntry weightedEntry2 : arrayList) {
                i += weightedEntry2.weight();
            }
            while (!arrayList.isEmpty()) {
                StructureSet.WeightedEntry weightedEntry3;
                int j = chunkRandom.nextInt(i);
                int k = 0;
                Iterator iterator = arrayList.iterator();
                while (iterator.hasNext() && (j -= (weightedEntry3 = (StructureSet.WeightedEntry)iterator.next()).weight()) >= 0) {
                    ++k;
                }
                StructureSet.WeightedEntry weightedEntry4 = (StructureSet.WeightedEntry)arrayList.get(k);
                if (this.trySetStructureStart(weightedEntry4, structureAccessor, registryManager, noiseConfig, structureManager, seed, chunk, chunkPos, chunkSectionPos)) {
                    return;
                }
                arrayList.remove(k);
                i -= weightedEntry4.weight();
            }
        });
    }

    private boolean trySetStructureStart(StructureSet.WeightedEntry weightedEntry, StructureAccessor structureAccessor, DynamicRegistryManager dynamicRegistryManager, NoiseConfig noiseConfig, StructureManager structureManager, long seed, Chunk chunk, ChunkPos pos, ChunkSectionPos sectionPos) {
        StructureType structureType = weightedEntry.structure().value();
        int i = ChunkGenerator.getStructureReferences(structureAccessor, chunk, sectionPos, structureType);
        RegistryEntryList<Biome> registryEntryList = structureType.getValidBiomes();
        Predicate<RegistryEntry<Biome>> predicate = registryEntryList::contains;
        StructureStart structureStart = structureType.createStructureStart(dynamicRegistryManager, this, this.biomeSource, noiseConfig, structureManager, seed, pos, i, chunk, predicate);
        if (structureStart.hasChildren()) {
            structureAccessor.setStructureStart(sectionPos, structureType, structureStart, chunk);
            return true;
        }
        return false;
    }

    private static int getStructureReferences(StructureAccessor structureAccessor, Chunk chunk, ChunkSectionPos sectionPos, StructureType structure) {
        StructureStart structureStart = structureAccessor.getStructureStart(sectionPos, structure, chunk);
        return structureStart != null ? structureStart.getReferences() : 0;
    }

    /**
     * Finds all structures that the given chunk intersects, and adds references to their starting chunks to it.
     * A radius of 8 chunks around the given chunk will be searched for structure starts.
     */
    public void addStructureReferences(StructureWorldAccess world, StructureAccessor structureAccessor, Chunk chunk) {
        int i = 8;
        ChunkPos chunkPos = chunk.getPos();
        int j = chunkPos.x;
        int k = chunkPos.z;
        int l = chunkPos.getStartX();
        int m = chunkPos.getStartZ();
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk);
        for (int n = j - 8; n <= j + 8; ++n) {
            for (int o = k - 8; o <= k + 8; ++o) {
                long p = ChunkPos.toLong(n, o);
                for (StructureStart structureStart : world.getChunk(n, o).getStructureStarts().values()) {
                    try {
                        if (!structureStart.hasChildren() || !structureStart.getBoundingBox().intersectsXZ(l, m, l + 15, m + 15)) continue;
                        structureAccessor.addStructureReference(chunkSectionPos, structureStart.getFeature(), p, chunk);
                        DebugInfoSender.sendStructureStart(world, structureStart);
                    } catch (Exception exception) {
                        CrashReport crashReport = CrashReport.create(exception, "Generating structure reference");
                        CrashReportSection crashReportSection = crashReport.addElement("Structure");
                        Optional<Registry<StructureType>> optional = world.getRegistryManager().getOptional(Registry.STRUCTURE_KEY);
                        crashReportSection.add("Id", () -> optional.map(structureTypeRegistry -> structureTypeRegistry.getId(structureStart.getFeature()).toString()).orElse("UNKNOWN"));
                        crashReportSection.add("Name", () -> Registry.STRUCTURE_TYPE.getId(structureStart.getFeature().getType()).toString());
                        crashReportSection.add("Class", () -> structureStart.getFeature().getClass().getCanonicalName());
                        throw new CrashException(crashReport);
                    }
                }
            }
        }
    }

    /**
     * Generates the base shape of the chunk out of the basic block states as decided by this chunk generator's config.
     */
    public abstract CompletableFuture<Chunk> populateNoise(Executor var1, Blender var2, NoiseConfig var3, StructureAccessor var4, Chunk var5);

    public abstract int getSeaLevel();

    public abstract int getMinimumY();

    /**
     * Returns the raw noise height of a column for use in structure generation.
     */
    public abstract int getHeight(int var1, int var2, Heightmap.Type var3, HeightLimitView var4, NoiseConfig var5);

    /**
     * Returns a sample of all the block states in a column for use in structure generation.
     */
    public abstract VerticalBlockSample getColumnSample(int var1, int var2, HeightLimitView var3, NoiseConfig var4);

    public int getHeightOnGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return this.getHeight(x, z, heightmap, world, noiseConfig);
    }

    public int getHeightInGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return this.getHeight(x, z, heightmap, world, noiseConfig) - 1;
    }

    public void computeStructurePlacementsIfNeeded(NoiseConfig noiseConfig) {
        if (!this.hasComputedStructurePlacements) {
            this.computeStructurePlacements(noiseConfig);
            this.hasComputedStructurePlacements = true;
        }
    }

    @Nullable
    public List<ChunkPos> getConcentricRingsStartChunks(ConcentricRingsStructurePlacement structurePlacement, NoiseConfig noiseConfig) {
        this.computeStructurePlacementsIfNeeded(noiseConfig);
        CompletableFuture<List<ChunkPos>> completableFuture = this.concentricRingPositions.get(structurePlacement);
        return completableFuture != null ? completableFuture.join() : null;
    }

    private List<StructurePlacement> getStructurePlacement(RegistryEntry<StructureType> structureEntry, NoiseConfig noiseConfig) {
        this.computeStructurePlacementsIfNeeded(noiseConfig);
        return this.structurePlacements.getOrDefault(structureEntry.value(), List.of());
    }

    public abstract void getDebugHudText(List<String> var1, NoiseConfig var2, BlockPos var3);

    @Deprecated
    public GenerationSettings getGenerationSettings(RegistryEntry<Biome> biomeEntry) {
        return this.generationSettingsGetter.apply(biomeEntry);
    }
}

