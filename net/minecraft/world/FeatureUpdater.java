/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkUpdateState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class FeatureUpdater {
    private static final Map<String, String> OLD_TO_NEW = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("Village", "Village");
        hashMap.put("Mineshaft", "Mineshaft");
        hashMap.put("Mansion", "Mansion");
        hashMap.put("Igloo", "Temple");
        hashMap.put("Desert_Pyramid", "Temple");
        hashMap.put("Jungle_Pyramid", "Temple");
        hashMap.put("Swamp_Hut", "Temple");
        hashMap.put("Stronghold", "Stronghold");
        hashMap.put("Monument", "Monument");
        hashMap.put("Fortress", "Fortress");
        hashMap.put("EndCity", "EndCity");
    });
    private static final Map<String, String> ANCIENT_TO_OLD = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("Iglu", "Igloo");
        hashMap.put("TeDP", "Desert_Pyramid");
        hashMap.put("TeJP", "Jungle_Pyramid");
        hashMap.put("TeSH", "Swamp_Hut");
    });
    private final boolean needsUpdate;
    private final Map<String, Long2ObjectMap<CompoundTag>> featureIdToChunkTag = Maps.newHashMap();
    private final Map<String, ChunkUpdateState> updateStates = Maps.newHashMap();
    private final List<String> field_17658;
    private final List<String> field_17659;

    public FeatureUpdater(@Nullable PersistentStateManager persistentStateManager, List<String> list, List<String> list2) {
        this.field_17658 = list;
        this.field_17659 = list2;
        this.init(persistentStateManager);
        boolean bl = false;
        for (String string : this.field_17659) {
            bl |= this.featureIdToChunkTag.get(string) != null;
        }
        this.needsUpdate = bl;
    }

    public void markResolved(long l) {
        for (String string : this.field_17658) {
            ChunkUpdateState chunkUpdateState = this.updateStates.get(string);
            if (chunkUpdateState == null || !chunkUpdateState.isRemaining(l)) continue;
            chunkUpdateState.markResolved(l);
            chunkUpdateState.markDirty();
        }
    }

    public CompoundTag getUpdatedReferences(CompoundTag compoundTag) {
        CompoundTag compoundTag2 = compoundTag.getCompound("Level");
        ChunkPos chunkPos = new ChunkPos(compoundTag2.getInt("xPos"), compoundTag2.getInt("zPos"));
        if (this.needsUpdate(chunkPos.x, chunkPos.z)) {
            compoundTag = this.getUpdatedStarts(compoundTag, chunkPos);
        }
        CompoundTag compoundTag3 = compoundTag2.getCompound("Structures");
        CompoundTag compoundTag4 = compoundTag3.getCompound("References");
        for (String string : this.field_17659) {
            StructureFeature structureFeature = (StructureFeature)StructureFeature.STRUCTURES.get(string.toLowerCase(Locale.ROOT));
            if (compoundTag4.contains(string, 12) || structureFeature == null) continue;
            int i = 8;
            LongArrayList longList = new LongArrayList();
            for (int j = chunkPos.x - 8; j <= chunkPos.x + 8; ++j) {
                for (int k = chunkPos.z - 8; k <= chunkPos.z + 8; ++k) {
                    if (!this.needsUpdate(j, k, string)) continue;
                    longList.add(ChunkPos.toLong(j, k));
                }
            }
            compoundTag4.putLongArray(string, longList);
        }
        compoundTag3.put("References", compoundTag4);
        compoundTag2.put("Structures", compoundTag3);
        compoundTag.put("Level", compoundTag2);
        return compoundTag;
    }

    private boolean needsUpdate(int chunkX, int chunkZ, String id) {
        if (!this.needsUpdate) {
            return false;
        }
        return this.featureIdToChunkTag.get(id) != null && this.updateStates.get(OLD_TO_NEW.get(id)).contains(ChunkPos.toLong(chunkX, chunkZ));
    }

    private boolean needsUpdate(int chunkX, int chunkZ) {
        if (!this.needsUpdate) {
            return false;
        }
        for (String string : this.field_17659) {
            if (this.featureIdToChunkTag.get(string) == null || !this.updateStates.get(OLD_TO_NEW.get(string)).isRemaining(ChunkPos.toLong(chunkX, chunkZ))) continue;
            return true;
        }
        return false;
    }

    private CompoundTag getUpdatedStarts(CompoundTag tag, ChunkPos pos) {
        CompoundTag compoundTag = tag.getCompound("Level");
        CompoundTag compoundTag2 = compoundTag.getCompound("Structures");
        CompoundTag compoundTag3 = compoundTag2.getCompound("Starts");
        for (String string : this.field_17659) {
            CompoundTag compoundTag4;
            Long2ObjectMap<CompoundTag> long2ObjectMap = this.featureIdToChunkTag.get(string);
            if (long2ObjectMap == null) continue;
            long l = pos.toLong();
            if (!this.updateStates.get(OLD_TO_NEW.get(string)).isRemaining(l) || (compoundTag4 = (CompoundTag)long2ObjectMap.get(l)) == null) continue;
            compoundTag3.put(string, compoundTag4);
        }
        compoundTag2.put("Starts", compoundTag3);
        compoundTag.put("Structures", compoundTag2);
        tag.put("Level", compoundTag);
        return tag;
    }

    private void init(@Nullable PersistentStateManager persistentStateManager) {
        if (persistentStateManager == null) {
            return;
        }
        for (String string2 : this.field_17658) {
            CompoundTag compoundTag = new CompoundTag();
            try {
                compoundTag = persistentStateManager.readNbt(string2, 1493).getCompound("data").getCompound("Features");
                if (compoundTag.isEmpty()) {
                    continue;
                }
            } catch (IOException iOException) {
                // empty catch block
            }
            for (String string22 : compoundTag.getKeys()) {
                String string3;
                String string4;
                CompoundTag compoundTag2 = compoundTag.getCompound(string22);
                long l = ChunkPos.toLong(compoundTag2.getInt("ChunkX"), compoundTag2.getInt("ChunkZ"));
                ListTag listTag = compoundTag2.getList("Children", 10);
                if (!listTag.isEmpty() && (string4 = ANCIENT_TO_OLD.get(string3 = listTag.getCompound(0).getString("id"))) != null) {
                    compoundTag2.putString("id", string4);
                }
                string3 = compoundTag2.getString("id");
                this.featureIdToChunkTag.computeIfAbsent(string3, string -> new Long2ObjectOpenHashMap()).put(l, compoundTag2);
            }
            String string5 = string2 + "_index";
            ChunkUpdateState chunkUpdateState = persistentStateManager.getOrCreate(ChunkUpdateState::fromNbt, ChunkUpdateState::new, string5);
            if (chunkUpdateState.getAll().isEmpty()) {
                ChunkUpdateState chunkUpdateState2 = new ChunkUpdateState();
                this.updateStates.put(string2, chunkUpdateState2);
                for (String string6 : compoundTag.getKeys()) {
                    CompoundTag compoundTag3 = compoundTag.getCompound(string6);
                    chunkUpdateState2.add(ChunkPos.toLong(compoundTag3.getInt("ChunkX"), compoundTag3.getInt("ChunkZ")));
                }
                chunkUpdateState2.markDirty();
                continue;
            }
            this.updateStates.put(string2, chunkUpdateState);
        }
    }

    public static FeatureUpdater create(RegistryKey<World> registryKey, @Nullable PersistentStateManager persistentStateManager) {
        if (registryKey == World.OVERWORLD) {
            return new FeatureUpdater(persistentStateManager, ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"), ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"));
        }
        if (registryKey == World.NETHER) {
            ImmutableList<String> list = ImmutableList.of("Fortress");
            return new FeatureUpdater(persistentStateManager, list, list);
        }
        if (registryKey == World.END) {
            ImmutableList<String> list = ImmutableList.of("EndCity");
            return new FeatureUpdater(persistentStateManager, list, list);
        }
        throw new RuntimeException(String.format("Unknown dimension type : %s", registryKey));
    }
}

