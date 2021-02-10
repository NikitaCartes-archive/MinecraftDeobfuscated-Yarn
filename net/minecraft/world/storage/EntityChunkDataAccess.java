/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.ChunkDataAccess;
import net.minecraft.world.storage.ChunkDataList;
import net.minecraft.world.storage.StorageIoWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityChunkDataAccess
implements ChunkDataAccess<Entity> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServerWorld world;
    private final StorageIoWorker dataLoadWorker;
    private final LongSet emptyChunks = new LongOpenHashSet();
    private final Executor executor;
    protected final DataFixer dataFixer;

    public EntityChunkDataAccess(ServerWorld world, File chunkFile, DataFixer dataFixer, boolean dsync, Executor executor) {
        this.world = world;
        this.dataFixer = dataFixer;
        this.executor = executor;
        this.dataLoadWorker = new StorageIoWorker(chunkFile, dsync, "entities");
    }

    @Override
    public CompletableFuture<ChunkDataList<Entity>> readChunkData(ChunkPos pos) {
        if (this.emptyChunks.contains(pos.toLong())) {
            return CompletableFuture.completedFuture(EntityChunkDataAccess.emptyDataList(pos));
        }
        return this.dataLoadWorker.readChunkData(pos).thenApplyAsync(compoundTag -> {
            if (compoundTag == null) {
                this.emptyChunks.add(pos.toLong());
                return EntityChunkDataAccess.emptyDataList(pos);
            }
            try {
                ChunkPos chunkPos2 = EntityChunkDataAccess.getChunkPos(compoundTag);
                if (!Objects.equals(pos, chunkPos2)) {
                    LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", (Object)pos, (Object)pos, (Object)chunkPos2);
                }
            } catch (Exception exception) {
                LOGGER.warn("Failed to parse chunk {} position info", (Object)pos, (Object)exception);
            }
            CompoundTag compoundTag2 = this.fixChunkData((CompoundTag)compoundTag);
            ListTag listTag = compoundTag2.getList("Entities", 10);
            List list = EntityType.streamFromNbt(listTag, this.world).collect(ImmutableList.toImmutableList());
            return new ChunkDataList(pos, list);
        }, this.executor);
    }

    private static ChunkPos getChunkPos(CompoundTag chunkTag) {
        int[] is = chunkTag.getIntArray("Position");
        return new ChunkPos(is[0], is[1]);
    }

    private static void putChunkPos(CompoundTag chunkTag, ChunkPos pos) {
        chunkTag.put("Position", new IntArrayTag(new int[]{pos.x, pos.z}));
    }

    private static ChunkDataList<Entity> emptyDataList(ChunkPos pos) {
        return new ChunkDataList<Entity>(pos, ImmutableList.of());
    }

    @Override
    public void writeChunkData(ChunkDataList<Entity> dataList) {
        ChunkPos chunkPos = dataList.getChunkPos();
        if (dataList.isEmpty()) {
            if (this.emptyChunks.add(chunkPos.toLong())) {
                this.dataLoadWorker.setResult(chunkPos, null);
            }
            return;
        }
        ListTag listTag = new ListTag();
        dataList.stream().forEach(entity -> {
            CompoundTag compoundTag = new CompoundTag();
            if (entity.saveToTag(compoundTag)) {
                listTag.add(compoundTag);
            }
        });
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        compoundTag.put("Entities", listTag);
        EntityChunkDataAccess.putChunkPos(compoundTag, chunkPos);
        this.dataLoadWorker.setResult(chunkPos, compoundTag).exceptionally(throwable -> {
            LOGGER.error("Failed to store chunk {}", (Object)chunkPos, throwable);
            return null;
        });
        this.emptyChunks.remove(chunkPos.toLong());
    }

    @Override
    public void awaitAll() {
        this.dataLoadWorker.completeAll().join();
    }

    private CompoundTag fixChunkData(CompoundTag chunkTag) {
        int i = EntityChunkDataAccess.getChunkDataVersion(chunkTag);
        return NbtHelper.update(this.dataFixer, DataFixTypes.ENTITY_CHUNK, chunkTag, i);
    }

    public static int getChunkDataVersion(CompoundTag chunkTag) {
        return chunkTag.contains("DataVersion", 99) ? chunkTag.getInt("DataVersion") : -1;
    }

    @Override
    public void close() throws IOException {
        this.dataLoadWorker.close();
    }
}

