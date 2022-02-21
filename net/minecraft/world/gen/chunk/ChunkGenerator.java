/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.google.common.base.Stopwatch;
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
import java.util.Random;
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
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * In charge of shaping, adding biome specific surface blocks, and carving chunks,
 * as well as populating the generated chunks with {@linkplain net.minecraft.world.gen.feature.Feature features} and {@linkplain net.minecraft.entity.Entity entities}.
 * Biome placement starts here, however all vanilla and most modded chunk generators delegate this to a {@linkplain net.minecraft.world.biome.source.BiomeSource biome source}.
 */
public abstract class ChunkGenerator
implements BiomeAccess.Storage {
    private static final Logger LOGGER;
    public static final Codec<ChunkGenerator> CODEC;
    protected final Registry<StructureSet> field_37053;
    /**
     * Used to control the population step without replacing the actual biome that comes from the original {@link #biomeSource}.
     * 
     * <p>This is used by {@link FlatChunkGenerator} to overwrite biome properties like whether lakes generate, while preserving the original biome ID.
     */
    protected final BiomeSource populationSource;
    protected final BiomeSource biomeSource;
    protected final Optional<RegistryEntryList<StructureSet>> field_37054;
    private final Map<ConfiguredStructureFeature<?, ?>, List<StructurePlacement>> field_37055 = new Object2ObjectOpenHashMap();
    private final Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>> field_36405 = new Object2ObjectArrayMap<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>>();
    private boolean field_37056;
    private final long generationSeed;

    protected static final <T extends ChunkGenerator> Products.P1<RecordCodecBuilder.Mu<T>, Registry<StructureSet>> method_41042(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(RegistryOps.createRegistryCodec(Registry.STRUCTURE_SET_KEY).forGetter(chunkGenerator -> chunkGenerator.field_37053));
    }

    public ChunkGenerator(Registry<StructureSet> registry, Optional<RegistryEntryList<StructureSet>> optional, BiomeSource biomeSource) {
        this(registry, optional, biomeSource, biomeSource, 0L);
    }

    public ChunkGenerator(Registry<StructureSet> registry, Optional<RegistryEntryList<StructureSet>> optional, BiomeSource biomeSource, BiomeSource biomeSource2, long l) {
        this.field_37053 = registry;
        this.populationSource = biomeSource;
        this.biomeSource = biomeSource2;
        this.field_37054 = optional;
        this.generationSeed = l;
    }

    public Stream<RegistryEntry<StructureSet>> method_41039() {
        if (this.field_37054.isPresent()) {
            return this.field_37054.get().stream();
        }
        return this.field_37053.streamEntries().map(RegistryEntry::upcast);
    }

    private void method_41057() {
        Set<RegistryEntry<Biome>> set = this.biomeSource.getBiomes();
        this.method_41039().forEach(registryEntry -> {
            StructureSet structureSet = (StructureSet)registryEntry.value();
            for (StructureSet.class_7060 lv : structureSet.structures()) {
                this.field_37055.computeIfAbsent(lv.structure().value(), configuredStructureFeature -> new ArrayList()).add(structureSet.placement());
            }
            StructurePlacement structurePlacement = structureSet.placement();
            if (structurePlacement instanceof ConcentricRingsStructurePlacement) {
                ConcentricRingsStructurePlacement concentricRingsStructurePlacement = (ConcentricRingsStructurePlacement)structurePlacement;
                if (structureSet.structures().stream().anyMatch(arg -> arg.method_41148(set::contains))) {
                    this.field_36405.put(concentricRingsStructurePlacement, this.generateStrongholdPositions((RegistryEntry<StructureSet>)registryEntry, concentricRingsStructurePlacement));
                }
            }
        });
    }

    private CompletableFuture<List<ChunkPos>> generateStrongholdPositions(RegistryEntry<StructureSet> strongholdSet, ConcentricRingsStructurePlacement placement) {
        if (placement.count() == 0) {
            return CompletableFuture.completedFuture(List.of());
        }
        return CompletableFuture.supplyAsync(Util.debugSupplier("placement calculation", () -> {
            Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
            ArrayList<ChunkPos> list = new ArrayList<ChunkPos>();
            Set set = ((StructureSet)strongholdSet.value()).structures().stream().flatMap(entry -> entry.structure().value().getBiomes().stream()).collect(Collectors.toSet());
            int i = placement.distance();
            int j = placement.count();
            int k = placement.spread();
            Random random = new Random();
            random.setSeed(this.generationSeed);
            double d = random.nextDouble() * Math.PI * 2.0;
            int l = 0;
            int m = 0;
            for (int n = 0; n < j; ++n) {
                double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * ((double)i * 2.5);
                int o = (int)Math.round(Math.cos(d) * e);
                int p = (int)Math.round(Math.sin(d) * e);
                Pair<BlockPos, RegistryEntry<Biome>> pair = this.populationSource.locateBiome(ChunkSectionPos.getOffsetPos(o, 8), 0, ChunkSectionPos.getOffsetPos(p, 8), 112, set::contains, random, this.getMultiNoiseSampler());
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
            LOGGER.debug("Calculation for {} took {}s", (Object)strongholdSet, (Object)f);
            return list;
        }), Util.getMainWorkerExecutor());
    }

    protected abstract Codec<? extends ChunkGenerator> getCodec();

    public Optional<RegistryKey<Codec<? extends ChunkGenerator>>> getCodecKey() {
        return Registry.CHUNK_GENERATOR.getKey(this.getCodec());
    }

    public abstract ChunkGenerator withSeed(long var1);

    public CompletableFuture<Chunk> populateBiomes(Registry<Biome> biomeRegistry, Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.supplyAsync(Util.debugSupplier("init_biomes", () -> {
            chunk.populateBiomes(this.biomeSource::getBiome, this.getMultiNoiseSampler());
            return chunk;
        }), Util.getMainWorkerExecutor());
    }

    public abstract MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler();

    @Override
    public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.getBiomeSource().getBiome(biomeX, biomeY, biomeZ, this.getMultiNoiseSampler());
    }

    /**
     * Generates caves for the given chunk.
     */
    public abstract void carve(ChunkRegion var1, long var2, BiomeAccess var4, StructureAccessor var5, Chunk var6, GenerationStep.Carver var7);

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
     * @param skipExistingChunks whether only structures that are not referenced by generated chunks (chunks past the STRUCTURE_STARTS stage) are returned, excluding strongholds
     */
    @Nullable
    public Pair<BlockPos, RegistryEntry<ConfiguredStructureFeature<?, ?>>> locateStructure(ServerWorld serverWorld, RegistryEntryList<ConfiguredStructureFeature<?, ?>> registryEntryList, BlockPos center, int radius, boolean skipExistingChunks) {
        Set set = registryEntryList.stream().flatMap(registryEntry -> ((ConfiguredStructureFeature)registryEntry.value()).getBiomes().stream()).collect(Collectors.toSet());
        if (set.isEmpty()) {
            return null;
        }
        Set<RegistryEntry<Biome>> set2 = this.biomeSource.getBiomes();
        if (Collections.disjoint(set2, set)) {
            return null;
        }
        Pair<BlockPos, RegistryEntry> pair = null;
        double d = Double.MAX_VALUE;
        Object2ObjectArrayMap<StructurePlacement, Set> map = new Object2ObjectArrayMap<StructurePlacement, Set>();
        for (RegistryEntry registryEntry2 : registryEntryList) {
            if (set2.stream().noneMatch(((ConfiguredStructureFeature)registryEntry2.value()).getBiomes()::contains)) continue;
            for (StructurePlacement structurePlacement2 : this.method_41055(registryEntry2)) {
                map.computeIfAbsent(structurePlacement2, structurePlacement -> new ObjectArraySet()).add(registryEntry2);
            }
        }
        ArrayList list = new ArrayList(map.size());
        for (Map.Entry entry : map.entrySet()) {
            StructurePlacement structurePlacement2 = (StructurePlacement)entry.getKey();
            if (structurePlacement2 instanceof ConcentricRingsStructurePlacement) {
                ConcentricRingsStructurePlacement concentricRingsStructurePlacement = (ConcentricRingsStructurePlacement)structurePlacement2;
                BlockPos blockPos = this.method_40148(center, concentricRingsStructurePlacement);
                double e = center.getSquaredDistance(blockPos);
                if (!(e < d)) continue;
                d = e;
                pair = Pair.of(blockPos, (RegistryEntry)((Set)entry.getValue()).iterator().next());
                continue;
            }
            if (!(structurePlacement2 instanceof RandomSpreadStructurePlacement)) continue;
            list.add(entry);
        }
        if (!list.isEmpty()) {
            int n = ChunkSectionPos.getSectionCoord(center.getX());
            int n2 = ChunkSectionPos.getSectionCoord(center.getZ());
            for (int k = 0; k <= radius; ++k) {
                boolean bl = false;
                for (Map.Entry entry2 : list) {
                    RandomSpreadStructurePlacement randomSpreadStructurePlacement = (RandomSpreadStructurePlacement)entry2.getKey();
                    Pair<BlockPos, RegistryEntry<ConfiguredStructureFeature<?, ?>>> pair2 = ChunkGenerator.method_40146((Set)entry2.getValue(), serverWorld, serverWorld.getStructureAccessor(), n, n2, k, skipExistingChunks, serverWorld.getSeed(), randomSpreadStructurePlacement);
                    if (pair2 == null) continue;
                    bl = true;
                    double f = center.getSquaredDistance(pair2.getFirst());
                    if (!(f < d)) continue;
                    d = f;
                    pair = pair2;
                }
                if (!bl) continue;
                return pair;
            }
        }
        return pair;
    }

    @Nullable
    private BlockPos method_40148(BlockPos blockPos, ConcentricRingsStructurePlacement concentricRingsStructurePlacement) {
        List<ChunkPos> list = this.getConcentricRingsStartChunks(concentricRingsStructurePlacement);
        if (list == null) {
            throw new IllegalStateException("Somehow tried to find structures for a placement that doesn't exist");
        }
        BlockPos blockPos2 = null;
        double d = Double.MAX_VALUE;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (ChunkPos chunkPos : list) {
            mutable.set(ChunkSectionPos.getOffsetPos(chunkPos.x, 8), 32, ChunkSectionPos.getOffsetPos(chunkPos.z, 8));
            double e = mutable.getSquaredDistance(blockPos);
            if (blockPos2 == null) {
                blockPos2 = new BlockPos(mutable);
                d = e;
                continue;
            }
            if (!(e < d)) continue;
            blockPos2 = new BlockPos(mutable);
            d = e;
        }
        return blockPos2;
    }

    @Nullable
    private static Pair<BlockPos, RegistryEntry<ConfiguredStructureFeature<?, ?>>> method_40146(Set<RegistryEntry<ConfiguredStructureFeature<?, ?>>> set, WorldView worldView, StructureAccessor structureAccessor, int i, int j, int k, boolean bl, long l, RandomSpreadStructurePlacement randomSpreadStructurePlacement) {
        int m = randomSpreadStructurePlacement.spacing();
        for (int n = -k; n <= k; ++n) {
            boolean bl2 = n == -k || n == k;
            for (int o = -k; o <= k; ++o) {
                boolean bl3;
                boolean bl4 = bl3 = o == -k || o == k;
                if (!bl2 && !bl3) continue;
                int p = i + m * n;
                int q = j + m * o;
                ChunkPos chunkPos = randomSpreadStructurePlacement.getStartChunk(l, p, q);
                for (RegistryEntry<ConfiguredStructureFeature<?, ?>> registryEntry : set) {
                    StructurePresence structurePresence = structureAccessor.getStructurePresence(chunkPos, registryEntry.value(), bl);
                    if (structurePresence == StructurePresence.START_NOT_PRESENT) continue;
                    if (!bl && structurePresence == StructurePresence.START_PRESENT) {
                        return Pair.of(StructureFeature.getLocatedPos(randomSpreadStructurePlacement, chunkPos), registryEntry);
                    }
                    Chunk chunk = worldView.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS);
                    StructureStart structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk), registryEntry.value(), chunk);
                    if (structureStart == null || !structureStart.hasChildren()) continue;
                    if (bl && structureStart.isInExistingChunk()) {
                        structureAccessor.incrementReferences(structureStart);
                        return Pair.of(StructureFeature.getLocatedPos(randomSpreadStructurePlacement, structureStart.getPos()), registryEntry);
                    }
                    if (bl) continue;
                    return Pair.of(StructureFeature.getLocatedPos(randomSpreadStructurePlacement, structureStart.getPos()), registryEntry);
                }
            }
        }
        return null;
    }

    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        ChunkPos chunkPos2 = chunk.getPos();
        if (SharedConstants.method_37896(chunkPos2)) {
            return;
        }
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos2, world.getBottomSectionCoord());
        BlockPos blockPos = chunkSectionPos.getMinPos();
        Registry<ConfiguredStructureFeature<?, ?>> registry = world.getRegistryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
        Map<Integer, List<ConfiguredStructureFeature>> map = registry.stream().collect(Collectors.groupingBy(configuredStructureFeature -> ((StructureFeature)configuredStructureFeature.feature).getGenerationStep().ordinal()));
        List<BiomeSource.IndexedFeatures> list = this.populationSource.getIndexedFeatures();
        ChunkRandom chunkRandom = new ChunkRandom(new Xoroshiro128PlusPlusRandom(RandomSeed.getSeed()));
        long l = chunkRandom.setPopulationSeed(world.getSeed(), blockPos.getX(), blockPos.getZ());
        ObjectArraySet set = new ObjectArraySet();
        if (this instanceof FlatChunkGenerator) {
            this.populationSource.getBiomes().stream().map(RegistryEntry::value).forEach(set::add);
        } else {
            ChunkPos.stream(chunkSectionPos.toChunkPos(), 1).forEach(chunkPos -> {
                Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
                for (ChunkSection chunkSection : chunk.getSectionArray()) {
                    chunkSection.getBiomeContainer().method_39793(registryEntry -> set.add((Biome)registryEntry.value()));
                }
            });
            set.retainAll(this.populationSource.getBiomes().stream().map(RegistryEntry::value).collect(Collectors.toSet()));
        }
        int i = list.size();
        try {
            Registry<PlacedFeature> registry2 = world.getRegistryManager().get(Registry.PLACED_FEATURE_KEY);
            int j = Math.max(GenerationStep.Feature.values().length, i);
            for (int k = 0; k < j; ++k) {
                int m = 0;
                if (structureAccessor.shouldGenerateStructures()) {
                    List list2 = map.getOrDefault(k, Collections.emptyList());
                    for (ConfiguredStructureFeature configuredStructureFeature2 : list2) {
                        chunkRandom.setDecoratorSeed(l, m, k);
                        Supplier<String> supplier = () -> registry.getKey(configuredStructureFeature2).map(Object::toString).orElseGet(configuredStructureFeature2::toString);
                        try {
                            world.setCurrentlyGeneratingStructureName(supplier);
                            structureAccessor.getStructureStarts(chunkSectionPos, configuredStructureFeature2).forEach(structureStart -> structureStart.place(world, structureAccessor, this, chunkRandom, ChunkGenerator.getBlockBoxForChunk(chunk), chunkPos2));
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
                for (Biome biome : set) {
                    List<RegistryEntryList<PlacedFeature>> list3 = biome.getGenerationSettings().getFeatures();
                    if (k >= list3.size()) continue;
                    RegistryEntryList<PlacedFeature> registryEntryList = list3.get(k);
                    BiomeSource.IndexedFeatures indexedFeatures = list.get(k);
                    registryEntryList.stream().map(RegistryEntry::value).forEach(placedFeature -> intSet.add(indexedFeatures.indexMapping().applyAsInt((PlacedFeature)placedFeature)));
                }
                int n = intSet.size();
                int[] is = intSet.toIntArray();
                Arrays.sort(is);
                BiomeSource.IndexedFeatures indexedFeatures2 = list.get(k);
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

    public boolean method_41053(RegistryKey<StructureSet> registryKey, int i, int j, int k) {
        StructureSet structureSet = this.field_37053.get(registryKey);
        if (structureSet == null) {
            return false;
        }
        StructurePlacement structurePlacement = structureSet.placement();
        for (int l = i - k; l <= i + k; ++l) {
            for (int m = j - k; m <= j + k; ++m) {
                if (!structurePlacement.isStartChunk(this, l, m)) continue;
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
    public abstract void buildSurface(ChunkRegion var1, StructureAccessor var2, Chunk var3);

    public abstract void populateEntities(ChunkRegion var1);

    public int getSpawnHeight(HeightLimitView world) {
        return 64;
    }

    public BiomeSource getBiomeSource() {
        return this.biomeSource;
    }

    public abstract int getWorldHeight();

    public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(RegistryEntry<Biome> biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        Map<ConfiguredStructureFeature<?, ?>, LongSet> map = accessor.method_41037(pos);
        for (Map.Entry<ConfiguredStructureFeature<?, ?>, LongSet> entry : map.entrySet()) {
            ConfiguredStructureFeature<?, ?> configuredStructureFeature = entry.getKey();
            StructureSpawns structureSpawns = configuredStructureFeature.field_37143.get(group);
            if (structureSpawns == null) continue;
            MutableBoolean mutableBoolean = new MutableBoolean(false);
            Predicate<StructureStart> predicate = structureSpawns.boundingBox() == StructureSpawns.BoundingBox.PIECE ? structureStart -> accessor.method_41033(pos, (StructureStart)structureStart) : structureStart -> structureStart.getBoundingBox().contains(pos);
            accessor.method_41032(configuredStructureFeature, entry.getValue(), structureStart -> {
                if (mutableBoolean.isFalse() && predicate.test((StructureStart)structureStart)) {
                    mutableBoolean.setTrue();
                }
            });
            if (!mutableBoolean.isTrue()) continue;
            return structureSpawns.spawns();
        }
        return biome.value().getSpawnSettings().getSpawnEntries(group);
    }

    public static Stream<ConfiguredStructureFeature<?, ?>> method_41049(Registry<ConfiguredStructureFeature<?, ?>> registry, StructureFeature<?> structureFeature) {
        return registry.stream().filter(configuredStructureFeature -> configuredStructureFeature.feature == structureFeature);
    }

    /**
     * Determines which structures should start in the given chunk and creates their starting points.
     */
    public void setStructureStarts(DynamicRegistryManager registryManager, StructureAccessor world, Chunk chunk, StructureManager structureManager, long worldSeed) {
        ChunkPos chunkPos = chunk.getPos();
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk);
        this.method_41039().forEach(registryEntry -> {
            StructurePlacement structurePlacement = ((StructureSet)registryEntry.value()).placement();
            List<StructureSet.class_7060> list = ((StructureSet)registryEntry.value()).structures();
            for (StructureSet.class_7060 lv : list) {
                StructureStart structureStart = world.getStructureStart(chunkSectionPos, lv.structure().value(), chunk);
                if (structureStart == null || !structureStart.hasChildren()) continue;
                return;
            }
            if (!structurePlacement.isStartChunk(this, chunkPos.x, chunkPos.z)) {
                return;
            }
            if (list.size() == 1) {
                this.method_41044(list.get(0), world, registryManager, structureManager, worldSeed, chunk, chunkPos, chunkSectionPos);
                return;
            }
            ArrayList<StructureSet.class_7060> arrayList = new ArrayList<StructureSet.class_7060>(list.size());
            arrayList.addAll(list);
            ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
            chunkRandom.setCarverSeed(worldSeed, chunkPos.x, chunkPos.z);
            int i = 0;
            for (StructureSet.class_7060 lv2 : arrayList) {
                i += lv2.weight();
            }
            while (!arrayList.isEmpty()) {
                StructureSet.class_7060 lv3;
                int j = chunkRandom.nextInt(i);
                int k = 0;
                Iterator iterator = arrayList.iterator();
                while (iterator.hasNext() && (j -= (lv3 = (StructureSet.class_7060)iterator.next()).weight()) >= 0) {
                    ++k;
                }
                StructureSet.class_7060 lv4 = (StructureSet.class_7060)arrayList.get(k);
                if (this.method_41044(lv4, world, registryManager, structureManager, worldSeed, chunk, chunkPos, chunkSectionPos)) {
                    return;
                }
                arrayList.remove(k);
                i -= lv4.weight();
            }
        });
    }

    private boolean method_41044(StructureSet.class_7060 arg, StructureAccessor structureAccessor, DynamicRegistryManager dynamicRegistryManager, StructureManager structureManager, long l, Chunk chunk, ChunkPos chunkPos, ChunkSectionPos chunkSectionPos) {
        RegistryEntryList<Biome> registryEntryList;
        Predicate<RegistryEntry<Biome>> predicate;
        int i;
        ConfiguredStructureFeature<?, ?> configuredStructureFeature = arg.structure().value();
        StructureStart structureStart = configuredStructureFeature.tryPlaceStart(dynamicRegistryManager, this, this.populationSource, structureManager, l, chunkPos, i = ChunkGenerator.getStructureReferences(structureAccessor, chunk, chunkSectionPos, configuredStructureFeature), chunk, predicate = arg_0 -> this.method_41048(registryEntryList = configuredStructureFeature.getBiomes(), arg_0));
        if (structureStart.hasChildren()) {
            structureAccessor.setStructureStart(chunkSectionPos, configuredStructureFeature, structureStart, chunk);
            return true;
        }
        return false;
    }

    private static int getStructureReferences(StructureAccessor structureAccessor, Chunk chunk, ChunkSectionPos sectionPos, ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
        StructureStart structureStart = structureAccessor.getStructureStart(sectionPos, configuredStructureFeature, chunk);
        return structureStart != null ? structureStart.getReferences() : 0;
    }

    protected RegistryEntry<Biome> filterBiome(RegistryEntry<Biome> biome) {
        return biome;
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
                        Optional<Registry<ConfiguredStructureFeature<?, ?>>> optional = world.getRegistryManager().getOptional(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
                        crashReportSection.add("Id", () -> optional.map(registry -> registry.getId(structureStart.getFeature()).toString()).orElse("UNKNOWN"));
                        crashReportSection.add("Name", () -> Registry.STRUCTURE_FEATURE.getId((StructureFeature<?>)structureStart.getFeature().feature).toString());
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
    public abstract CompletableFuture<Chunk> populateNoise(Executor var1, Blender var2, StructureAccessor var3, Chunk var4);

    public abstract int getSeaLevel();

    public abstract int getMinimumY();

    /**
     * Returns the raw noise height of a column for use in structure generation.
     */
    public abstract int getHeight(int var1, int var2, Heightmap.Type var3, HeightLimitView var4);

    /**
     * Returns a sample of all the block states in a column for use in structure generation.
     */
    public abstract VerticalBlockSample getColumnSample(int var1, int var2, HeightLimitView var3);

    public int getHeightOnGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
        return this.getHeight(x, z, heightmap, world);
    }

    public int getHeightInGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
        return this.getHeight(x, z, heightmap, world) - 1;
    }

    public void method_41058() {
        if (!this.field_37056) {
            this.method_41057();
            this.field_37056 = true;
        }
    }

    @Nullable
    public List<ChunkPos> getConcentricRingsStartChunks(ConcentricRingsStructurePlacement structurePlacement) {
        this.method_41058();
        CompletableFuture<List<ChunkPos>> completableFuture = this.field_36405.get(structurePlacement);
        return completableFuture != null ? completableFuture.join() : null;
    }

    private List<StructurePlacement> method_41055(RegistryEntry<ConfiguredStructureFeature<?, ?>> registryEntry) {
        this.method_41058();
        return this.field_37055.getOrDefault(registryEntry.value(), List.of());
    }

    public long getSeed() {
        return this.generationSeed;
    }

    public abstract void getDebugHudText(List<String> var1, BlockPos var2);

    private /* synthetic */ boolean method_41048(RegistryEntryList registryEntryList, RegistryEntry registryEntry) {
        return registryEntryList.contains(this.filterBiome(registryEntry));
    }

    static {
        Registry.register(Registry.CHUNK_GENERATOR, "noise", NoiseChunkGenerator.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, "flat", FlatChunkGenerator.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, "debug", DebugChunkGenerator.CODEC);
        LOGGER = LogUtils.getLogger();
        CODEC = Registry.CHUNK_GENERATOR.getCodec().dispatchStable(ChunkGenerator::getCodec, Function.identity());
    }
}

