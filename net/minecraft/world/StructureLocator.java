/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.ImmutableMultimap;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.scanner.SelectiveNbtCollector;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.StructurePresence;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.storage.NbtScannable;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class StructureLocator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int START_NOT_PRESENT_REFERENCE = -1;
    private final NbtScannable chunkIoWorker;
    private final DynamicRegistryManager registryManager;
    private final Registry<Biome> biomeRegistry;
    private final StructureManager structureManager;
    private final RegistryKey<World> worldKey;
    private final ChunkGenerator chunkGenerator;
    private final HeightLimitView world;
    private final BiomeSource biomeSource;
    private final long seed;
    private final DataFixer dataFixer;
    private final Long2ObjectMap<Object2IntMap<StructureFeature<?>>> cachedFeaturesByChunkPos = new Long2ObjectOpenHashMap();
    private final Map<StructureFeature<?>, Long2BooleanMap> generationPossibilityByFeature = new HashMap();

    public StructureLocator(NbtScannable chunkIoWorker, DynamicRegistryManager registryManager, StructureManager structureManager, RegistryKey<World> worldKey, ChunkGenerator chunkGenerator, HeightLimitView world, BiomeSource biomeSource, long seed, DataFixer dataFixer) {
        this.chunkIoWorker = chunkIoWorker;
        this.registryManager = registryManager;
        this.structureManager = structureManager;
        this.worldKey = worldKey;
        this.chunkGenerator = chunkGenerator;
        this.world = world;
        this.biomeSource = biomeSource;
        this.seed = seed;
        this.dataFixer = dataFixer;
        this.biomeRegistry = registryManager.getMutable(Registry.BIOME_KEY);
    }

    public <F extends StructureFeature<?>> StructurePresence getStructurePresence(ChunkPos pos2, F feature2, boolean skipExistingChunk) {
        long l = pos2.toLong();
        Object2IntMap object2IntMap = (Object2IntMap)this.cachedFeaturesByChunkPos.get(l);
        if (object2IntMap != null) {
            return this.getStructurePresence(object2IntMap, feature2, skipExistingChunk);
        }
        StructurePresence structurePresence = this.getStructurePresence(pos2, feature2, skipExistingChunk, l);
        if (structurePresence != null) {
            return structurePresence;
        }
        boolean bl = this.generationPossibilityByFeature.computeIfAbsent(feature2, feature -> new Long2BooleanOpenHashMap()).computeIfAbsent(l, pos -> {
            ImmutableMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> multimap = this.chunkGenerator.getStructuresConfig().getConfiguredStructureFeature(feature2);
            for (Map.Entry entry : multimap.asMap().entrySet()) {
                if (!this.isGenerationPossible(pos2, (ConfiguredStructureFeature)entry.getKey(), entry.getValue())) continue;
                return true;
            }
            return false;
        });
        if (!bl) {
            return StructurePresence.START_NOT_PRESENT;
        }
        return StructurePresence.CHUNK_LOAD_NEEDED;
    }

    /**
     * {@return whether {@code feature} is able to generate in {@code pos}}
     * 
     * <p>This method only performs simple checks like biomes.
     * 
     * @param allowedBiomes the list of biomes where the {@code feature} can generate
     */
    private <FC extends FeatureConfig, F extends StructureFeature<FC>> boolean isGenerationPossible(ChunkPos pos, ConfiguredStructureFeature<FC, F> feature, Collection<RegistryKey<Biome>> allowedBiomes) {
        Predicate<Biome> predicate = biome -> this.biomeRegistry.getKey((Biome)biome).filter(allowedBiomes::contains).isPresent();
        return ((StructureFeature)feature.feature).canGenerate(this.registryManager, this.chunkGenerator, this.biomeSource, this.structureManager, this.seed, pos, feature.config, this.world, predicate);
    }

    @Nullable
    private StructurePresence getStructurePresence(ChunkPos pos, StructureFeature<?> feature, boolean skipExistingChunk, long posLong) {
        NbtCompound nbtCompound2;
        SelectiveNbtCollector selectiveNbtCollector = new SelectiveNbtCollector(new SelectiveNbtCollector.Query(NbtInt.TYPE, "DataVersion"), new SelectiveNbtCollector.Query("Level", "Structures", NbtCompound.TYPE, "Starts"), new SelectiveNbtCollector.Query("structures", NbtCompound.TYPE, "starts"));
        try {
            this.chunkIoWorker.scanChunk(pos, selectiveNbtCollector).join();
        } catch (Exception exception) {
            LOGGER.warn("Failed to read chunk {}", (Object)pos, (Object)exception);
            return StructurePresence.CHUNK_LOAD_NEEDED;
        }
        NbtElement nbtElement = selectiveNbtCollector.getRoot();
        if (!(nbtElement instanceof NbtCompound)) {
            return null;
        }
        NbtCompound nbtCompound = (NbtCompound)nbtElement;
        int i = VersionedChunkStorage.getDataVersion(nbtCompound);
        if (i <= 1493) {
            return StructurePresence.CHUNK_LOAD_NEEDED;
        }
        VersionedChunkStorage.saveContextToNbt(nbtCompound, this.worldKey, this.chunkGenerator.getCodecKey());
        try {
            nbtCompound2 = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, nbtCompound, i);
        } catch (Exception exception2) {
            LOGGER.warn("Failed to partially datafix chunk {}", (Object)pos, (Object)exception2);
            return StructurePresence.CHUNK_LOAD_NEEDED;
        }
        Object2IntMap<StructureFeature<?>> object2IntMap = this.collectStructuresAndReferences(nbtCompound2);
        if (object2IntMap == null) {
            return null;
        }
        this.cache(posLong, object2IntMap);
        return this.getStructurePresence(object2IntMap, feature, skipExistingChunk);
    }

    @Nullable
    private Object2IntMap<StructureFeature<?>> collectStructuresAndReferences(NbtCompound nbt) {
        if (!nbt.contains("structures", 10)) {
            return null;
        }
        NbtCompound nbtCompound = nbt.getCompound("structures");
        if (!nbtCompound.contains("starts", 10)) {
            return null;
        }
        NbtCompound nbtCompound2 = nbtCompound.getCompound("starts");
        if (nbtCompound2.isEmpty()) {
            return Object2IntMaps.emptyMap();
        }
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        for (String string : nbtCompound2.getKeys()) {
            String string3;
            NbtCompound nbtCompound3;
            String string2 = string.toLowerCase(Locale.ROOT);
            StructureFeature structureFeature = (StructureFeature)StructureFeature.STRUCTURES.get(string2);
            if (structureFeature == null || (nbtCompound3 = nbtCompound2.getCompound(string)).isEmpty() || "INVALID".equals(string3 = nbtCompound3.getString("id"))) continue;
            int i = nbtCompound3.getInt("references");
            object2IntMap.put((StructureFeature<?>)structureFeature, i);
        }
        return object2IntMap;
    }

    private static Object2IntMap<StructureFeature<?>> createMapIfEmpty(Object2IntMap<StructureFeature<?>> map) {
        return map.isEmpty() ? Object2IntMaps.emptyMap() : map;
    }

    private StructurePresence getStructurePresence(Object2IntMap<StructureFeature<?>> referencesByStructure, StructureFeature<?> feature, boolean skipExistingChunk) {
        int i = referencesByStructure.getOrDefault((Object)feature, -1);
        return i != -1 && (!skipExistingChunk || i == 0) ? StructurePresence.START_PRESENT : StructurePresence.START_NOT_PRESENT;
    }

    public void cache(ChunkPos pos, Map<StructureFeature<?>, StructureStart<?>> structureStarts) {
        long l = pos.toLong();
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        structureStarts.forEach((start, structureStart) -> {
            if (structureStart.hasChildren()) {
                object2IntMap.put((StructureFeature<?>)start, structureStart.getReferences());
            }
        });
        this.cache(l, object2IntMap);
    }

    private void cache(long pos, Object2IntMap<StructureFeature<?>> referencesByStructure) {
        this.cachedFeaturesByChunkPos.put(pos, StructureLocator.createMapIfEmpty(referencesByStructure));
        this.generationPossibilityByFeature.values().forEach(generationPossibilityByChunkPos -> generationPossibilityByChunkPos.remove(pos));
    }

    public void incrementReferences(ChunkPos pos2, StructureFeature<?> feature) {
        this.cachedFeaturesByChunkPos.compute(pos2.toLong(), (pos, referencesByStructure) -> {
            if (referencesByStructure == null || referencesByStructure.isEmpty()) {
                referencesByStructure = new Object2IntOpenHashMap<StructureFeature>();
            }
            referencesByStructure.computeInt(feature, (feature, references) -> references == null ? 1 : references + 1);
            return referencesByStructure;
        });
    }
}

