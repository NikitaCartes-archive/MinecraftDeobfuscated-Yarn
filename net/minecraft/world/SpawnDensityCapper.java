/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;

public class SpawnDensityCapper {
    private final Long2ObjectMap<List<ServerPlayerEntity>> chunkPosToMobSpawnablePlayers = new Long2ObjectOpenHashMap<List<ServerPlayerEntity>>();
    private final Map<ServerPlayerEntity, DensityCap> playersToDensityCap = Maps.newHashMap();
    private final ThreadedAnvilChunkStorage threadedAnvilChunkStorage;

    public SpawnDensityCapper(ThreadedAnvilChunkStorage threadedAnvilChunkStorage) {
        this.threadedAnvilChunkStorage = threadedAnvilChunkStorage;
    }

    private List<ServerPlayerEntity> getMobSpawnablePlayers(ChunkPos chunkPos) {
        return this.chunkPosToMobSpawnablePlayers.computeIfAbsent(chunkPos.toLong(), pos -> this.threadedAnvilChunkStorage.getPlayersWatchingChunk(chunkPos));
    }

    public void increaseDensity(ChunkPos chunkPos, SpawnGroup spawnGroup) {
        for (ServerPlayerEntity serverPlayerEntity : this.getMobSpawnablePlayers(chunkPos)) {
            this.playersToDensityCap.computeIfAbsent(serverPlayerEntity, player -> new DensityCap()).increaseDensity(spawnGroup);
        }
    }

    public boolean canSpawn(SpawnGroup spawnGroup, ChunkPos chunkPos) {
        for (ServerPlayerEntity serverPlayerEntity : this.getMobSpawnablePlayers(chunkPos)) {
            DensityCap densityCap = this.playersToDensityCap.get(serverPlayerEntity);
            if (densityCap != null && !densityCap.canSpawn(spawnGroup)) continue;
            return true;
        }
        return false;
    }

    static class DensityCap {
        private final Object2IntMap<SpawnGroup> spawnGroupsToDensity = new Object2IntOpenHashMap<SpawnGroup>(SpawnGroup.values().length);

        DensityCap() {
        }

        public void increaseDensity(SpawnGroup spawnGroup) {
            this.spawnGroupsToDensity.computeInt(spawnGroup, (group, density) -> density == null ? 1 : density + 1);
        }

        public boolean canSpawn(SpawnGroup spawnGroup) {
            return this.spawnGroupsToDensity.getOrDefault((Object)spawnGroup, 0) < spawnGroup.getCapacity();
        }
    }
}

