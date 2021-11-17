/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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
import net.minecraft.class_6830;
import net.minecraft.class_6833;
import net.minecraft.class_6841;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class class_6832 {
    private static final Logger field_36225 = LogManager.getLogger();
    private static final int field_36226 = -1;
    private final class_6830 field_36227;
    private final DynamicRegistryManager field_36228;
    private final Registry<Biome> field_36229;
    private final StructureManager field_36230;
    private final RegistryKey<World> field_36231;
    private final ChunkGenerator field_36232;
    private final HeightLimitView field_36233;
    private final BiomeSource field_36234;
    private final long field_36235;
    private final DataFixer field_36236;
    private final Long2ObjectMap<Object2IntMap<StructureFeature<?>>> field_36237 = new Long2ObjectOpenHashMap();
    private final Map<StructureFeature<?>, Long2BooleanMap> field_36238 = new HashMap();

    public class_6832(class_6830 arg, DynamicRegistryManager dynamicRegistryManager, StructureManager structureManager, RegistryKey<World> registryKey, ChunkGenerator chunkGenerator, HeightLimitView heightLimitView, BiomeSource biomeSource, long l, DataFixer dataFixer) {
        this.field_36227 = arg;
        this.field_36228 = dynamicRegistryManager;
        this.field_36230 = structureManager;
        this.field_36231 = registryKey;
        this.field_36232 = chunkGenerator;
        this.field_36233 = heightLimitView;
        this.field_36234 = biomeSource;
        this.field_36235 = l;
        this.field_36236 = dataFixer;
        this.field_36229 = dynamicRegistryManager.getMutable(Registry.BIOME_KEY);
    }

    public <F extends StructureFeature<?>> class_6833 method_39831(ChunkPos chunkPos, F structureFeature2, boolean bl) {
        long l2 = chunkPos.toLong();
        Object2IntMap object2IntMap = (Object2IntMap)this.field_36237.get(l2);
        if (object2IntMap != null) {
            return this.method_39840(object2IntMap, structureFeature2, bl);
        }
        class_6833 lv = this.method_39832(chunkPos, structureFeature2, bl, l2);
        if (lv != null) {
            return lv;
        }
        boolean bl2 = this.field_36238.computeIfAbsent(structureFeature2, structureFeature -> new Long2BooleanOpenHashMap()).computeIfAbsent(l2, l -> {
            ImmutableMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> multimap = this.field_36232.getStructuresConfig().getConfiguredStructureFeature(structureFeature2);
            for (Map.Entry entry : multimap.asMap().entrySet()) {
                if (!this.method_39829(chunkPos, (ConfiguredStructureFeature)entry.getKey(), entry.getValue())) continue;
                return true;
            }
            return false;
        });
        if (!bl2) {
            return class_6833.START_NOT_PRESENT;
        }
        return class_6833.CHUNK_LOAD_NEEDED;
    }

    private <FC extends FeatureConfig, F extends StructureFeature<FC>> boolean method_39829(ChunkPos chunkPos, ConfiguredStructureFeature<FC, F> configuredStructureFeature, Collection<RegistryKey<Biome>> collection) {
        Predicate<Biome> predicate = biome -> this.field_36229.getKey((Biome)biome).filter(collection::contains).isPresent();
        return ((StructureFeature)configuredStructureFeature.feature).method_39821(this.field_36228, this.field_36232, this.field_36234, this.field_36230, this.field_36235, chunkPos, configuredStructureFeature.config, this.field_36233, predicate);
    }

    @Nullable
    private class_6833 method_39832(ChunkPos chunkPos, StructureFeature<?> structureFeature, boolean bl, long l) {
        NbtCompound nbtCompound2;
        class_6841 lv = new class_6841(new class_6841.class_6843(NbtInt.TYPE, "DataVersion"), new class_6841.class_6843("Level", "Structures", NbtCompound.TYPE, "Starts"), new class_6841.class_6843("structures", NbtCompound.TYPE, "starts"));
        try {
            this.field_36227.method_39795(chunkPos, lv).join();
        } catch (Exception exception) {
            field_36225.warn("Failed to read chunk {}", (Object)chunkPos, (Object)exception);
            return class_6833.CHUNK_LOAD_NEEDED;
        }
        NbtElement nbtElement = lv.method_39887();
        if (!(nbtElement instanceof NbtCompound)) {
            return null;
        }
        NbtCompound nbtCompound = (NbtCompound)nbtElement;
        int i = VersionedChunkStorage.getDataVersion(nbtCompound);
        if (i <= 1493) {
            return class_6833.CHUNK_LOAD_NEEDED;
        }
        VersionedChunkStorage.method_39799(nbtCompound, this.field_36231, this.field_36232.getCodecKey());
        try {
            nbtCompound2 = NbtHelper.update(this.field_36236, DataFixTypes.CHUNK, nbtCompound, i);
        } catch (Exception exception2) {
            field_36225.warn("Failed to partially datafix chunk {}", (Object)chunkPos, (Object)exception2);
            return class_6833.CHUNK_LOAD_NEEDED;
        }
        Object2IntMap<StructureFeature<?>> object2IntMap = this.method_39842(nbtCompound2);
        if (object2IntMap == null) {
            return null;
        }
        this.method_39828(l, object2IntMap);
        return this.method_39840(object2IntMap, structureFeature, bl);
    }

    @Nullable
    private Object2IntMap<StructureFeature<?>> method_39842(NbtCompound nbtCompound) {
        if (!nbtCompound.contains("structures", 10)) {
            return null;
        }
        NbtCompound nbtCompound2 = nbtCompound.getCompound("structures");
        if (!nbtCompound2.contains("starts", 10)) {
            return null;
        }
        NbtCompound nbtCompound3 = nbtCompound2.getCompound("starts");
        if (nbtCompound3.isEmpty()) {
            return Object2IntMaps.emptyMap();
        }
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        for (String string : nbtCompound3.getKeys()) {
            String string3;
            NbtCompound nbtCompound4;
            String string2 = string.toLowerCase(Locale.ROOT);
            StructureFeature structureFeature = (StructureFeature)StructureFeature.STRUCTURES.get(string2);
            if (structureFeature == null || (nbtCompound4 = nbtCompound3.getCompound(string)).isEmpty() || "INVALID".equals(string3 = nbtCompound4.getString("id"))) continue;
            int i = nbtCompound4.getInt("references");
            object2IntMap.put((StructureFeature<?>)structureFeature, i);
        }
        return object2IntMap;
    }

    private static Object2IntMap<StructureFeature<?>> method_39838(Object2IntMap<StructureFeature<?>> object2IntMap) {
        return object2IntMap.isEmpty() ? Object2IntMaps.emptyMap() : object2IntMap;
    }

    private class_6833 method_39840(Object2IntMap<StructureFeature<?>> object2IntMap, StructureFeature<?> structureFeature, boolean bl) {
        int i = object2IntMap.getOrDefault((Object)structureFeature, -1);
        return i != -1 && (!bl || i == 0) ? class_6833.START_PRESENT : class_6833.START_NOT_PRESENT;
    }

    public void method_39833(ChunkPos chunkPos, Map<StructureFeature<?>, StructureStart<?>> map) {
        long l = chunkPos.toLong();
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        map.forEach((structureFeature, structureStart) -> {
            if (structureStart.hasChildren()) {
                object2IntMap.put((StructureFeature<?>)structureFeature, structureStart.getReferences());
            }
        });
        this.method_39828(l, object2IntMap);
    }

    private void method_39828(long l, Object2IntMap<StructureFeature<?>> object2IntMap) {
        this.field_36237.put(l, class_6832.method_39838(object2IntMap));
        this.field_36238.values().forEach(long2BooleanMap -> long2BooleanMap.remove(l));
    }

    public void method_39830(ChunkPos chunkPos, StructureFeature<?> structureFeature) {
        this.field_36237.compute(chunkPos.toLong(), (long_, object2IntMap) -> {
            if (object2IntMap == null || object2IntMap.isEmpty()) {
                object2IntMap = new Object2IntOpenHashMap<StructureFeature>();
            }
            object2IntMap.computeInt(structureFeature, (structureFeature, integer) -> integer == null ? 1 : integer + 1);
            return object2IntMap;
        });
    }
}

