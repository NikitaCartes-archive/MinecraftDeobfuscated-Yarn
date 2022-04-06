/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.scanner.NbtScanQuery;
import net.minecraft.nbt.scanner.SelectiveNbtCollector;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
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
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.storage.NbtScannable;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class StructureLocator {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int START_NOT_PRESENT_REFERENCE = -1;
    private final NbtScannable chunkIoWorker;
    private final DynamicRegistryManager registryManager;
    private final Registry<Biome> biomeRegistry;
    private final Registry<StructureType> configuredStructureFeatureRegistry;
    private final StructureManager structureManager;
    private final RegistryKey<World> worldKey;
    private final ChunkGenerator chunkGenerator;
    private final NoiseConfig field_37750;
    private final HeightLimitView world;
    private final BiomeSource biomeSource;
    private final long seed;
    private final DataFixer dataFixer;
    private final Long2ObjectMap<Object2IntMap<StructureType>> cachedFeaturesByChunkPos = new Long2ObjectOpenHashMap<Object2IntMap<StructureType>>();
    private final Map<StructureType, Long2BooleanMap> generationPossibilityByFeature = new HashMap<StructureType, Long2BooleanMap>();

    public StructureLocator(NbtScannable chunkIoWorker, DynamicRegistryManager registryManager, StructureManager structureManager, RegistryKey<World> worldKey, ChunkGenerator chunkGenerator, NoiseConfig noiseConfig, HeightLimitView heightLimitView, BiomeSource biomeSource, long l, DataFixer dataFixer) {
        this.chunkIoWorker = chunkIoWorker;
        this.registryManager = registryManager;
        this.structureManager = structureManager;
        this.worldKey = worldKey;
        this.chunkGenerator = chunkGenerator;
        this.field_37750 = noiseConfig;
        this.world = heightLimitView;
        this.biomeSource = biomeSource;
        this.seed = l;
        this.dataFixer = dataFixer;
        this.biomeRegistry = registryManager.getManaged(Registry.BIOME_KEY);
        this.configuredStructureFeatureRegistry = registryManager.getManaged(Registry.STRUCTURE_KEY);
    }

    public StructurePresence getStructurePresence(ChunkPos chunkPos, StructureType structureType, boolean skipExistingChunk) {
        long l2 = chunkPos.toLong();
        Object2IntMap object2IntMap = (Object2IntMap)this.cachedFeaturesByChunkPos.get(l2);
        if (object2IntMap != null) {
            return this.getStructurePresence(object2IntMap, structureType, skipExistingChunk);
        }
        StructurePresence structurePresence = this.getStructurePresence(chunkPos, structureType, skipExistingChunk, l2);
        if (structurePresence != null) {
            return structurePresence;
        }
        boolean bl = this.generationPossibilityByFeature.computeIfAbsent(structureType, feature -> new Long2BooleanOpenHashMap()).computeIfAbsent(l2, l -> this.isGenerationPossible(chunkPos, structureType));
        if (!bl) {
            return StructurePresence.START_NOT_PRESENT;
        }
        return StructurePresence.CHUNK_LOAD_NEEDED;
    }

    /**
     * {@return whether {@code feature} is able to generate in {@code pos}}
     * 
     * <p>This method only performs simple checks like biomes.
     */
    private boolean isGenerationPossible(ChunkPos pos, StructureType feature) {
        return feature.getStructurePosition(new StructureType.Context(this.registryManager, this.chunkGenerator, this.biomeSource, this.field_37750, this.structureManager, this.seed, pos, this.world, feature.getValidBiomes()::contains)).isPresent();
    }

    @Nullable
    private StructurePresence getStructurePresence(ChunkPos pos, StructureType feature, boolean skipExistingChunk, long posLong) {
        NbtCompound nbtCompound2;
        SelectiveNbtCollector selectiveNbtCollector = new SelectiveNbtCollector(new NbtScanQuery(NbtInt.TYPE, "DataVersion"), new NbtScanQuery("Level", "Structures", NbtCompound.TYPE, "Starts"), new NbtScanQuery("structures", NbtCompound.TYPE, "starts"));
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
        Object2IntMap<StructureType> object2IntMap = this.collectStructuresAndReferences(nbtCompound2);
        if (object2IntMap == null) {
            return null;
        }
        this.cache(posLong, object2IntMap);
        return this.getStructurePresence(object2IntMap, feature, skipExistingChunk);
    }

    @Nullable
    private Object2IntMap<StructureType> collectStructuresAndReferences(NbtCompound nbt) {
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
        Object2IntOpenHashMap<StructureType> object2IntMap = new Object2IntOpenHashMap<StructureType>();
        Registry<StructureType> registry = this.registryManager.get(Registry.STRUCTURE_KEY);
        for (String string : nbtCompound2.getKeys()) {
            String string2;
            NbtCompound nbtCompound3;
            StructureType structureType;
            Identifier identifier = Identifier.tryParse(string);
            if (identifier == null || (structureType = registry.get(identifier)) == null || (nbtCompound3 = nbtCompound2.getCompound(string)).isEmpty() || "INVALID".equals(string2 = nbtCompound3.getString("id"))) continue;
            int i = nbtCompound3.getInt("references");
            object2IntMap.put(structureType, i);
        }
        return object2IntMap;
    }

    private static Object2IntMap<StructureType> createMapIfEmpty(Object2IntMap<StructureType> map) {
        return map.isEmpty() ? Object2IntMaps.emptyMap() : map;
    }

    private StructurePresence getStructurePresence(Object2IntMap<StructureType> referencesByStructure, StructureType feature, boolean skipExistingChunk) {
        int i = referencesByStructure.getOrDefault((Object)feature, -1);
        return i != -1 && (!skipExistingChunk || i == 0) ? StructurePresence.START_PRESENT : StructurePresence.START_NOT_PRESENT;
    }

    public void cache(ChunkPos pos, Map<StructureType, StructureStart> structureStarts) {
        long l = pos.toLong();
        Object2IntOpenHashMap<StructureType> object2IntMap = new Object2IntOpenHashMap<StructureType>();
        structureStarts.forEach((start, structureStart) -> {
            if (structureStart.hasChildren()) {
                object2IntMap.put((StructureType)start, structureStart.getReferences());
            }
        });
        this.cache(l, object2IntMap);
    }

    private void cache(long pos, Object2IntMap<StructureType> referencesByStructure) {
        this.cachedFeaturesByChunkPos.put(pos, StructureLocator.createMapIfEmpty(referencesByStructure));
        this.generationPossibilityByFeature.values().forEach(generationPossibilityByChunkPos -> generationPossibilityByChunkPos.remove(pos));
    }

    public void incrementReferences(ChunkPos pos2, StructureType feature) {
        this.cachedFeaturesByChunkPos.compute(pos2.toLong(), (pos, referencesByStructure) -> {
            if (referencesByStructure == null || referencesByStructure.isEmpty()) {
                referencesByStructure = new Object2IntOpenHashMap<StructureType>();
            }
            referencesByStructure.computeInt(feature, (feature, references) -> references == null ? 1 : references + 1);
            return referencesByStructure;
        });
    }
}

