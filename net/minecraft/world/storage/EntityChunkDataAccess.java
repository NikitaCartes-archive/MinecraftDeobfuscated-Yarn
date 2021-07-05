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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.storage.ChunkDataAccess;
import net.minecraft.world.storage.ChunkDataList;
import net.minecraft.world.storage.StorageIoWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityChunkDataAccess
implements ChunkDataAccess<Entity> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String ENTITIES_KEY = "Entities";
    private static final String POSITION_KEY = "Position";
    private final ServerWorld world;
    private final StorageIoWorker dataLoadWorker;
    private final LongSet emptyChunks = new LongOpenHashSet();
    private final TaskExecutor<Runnable> field_34056;
    protected final DataFixer dataFixer;

    public EntityChunkDataAccess(ServerWorld world, File chunkFile, DataFixer dataFixer, boolean dsync, Executor executor) {
        this.world = world;
        this.dataFixer = dataFixer;
        this.field_34056 = TaskExecutor.create(executor, "entity-deserializer");
        this.dataLoadWorker = new StorageIoWorker(chunkFile, dsync, "entities");
    }

    @Override
    public CompletableFuture<ChunkDataList<Entity>> readChunkData(ChunkPos pos) {
        if (this.emptyChunks.contains(pos.toLong())) {
            return CompletableFuture.completedFuture(EntityChunkDataAccess.emptyDataList(pos));
        }
        return this.dataLoadWorker.readChunkData(pos).thenApplyAsync(nbtCompound -> {
            if (nbtCompound == null) {
                this.emptyChunks.add(pos.toLong());
                return EntityChunkDataAccess.emptyDataList(pos);
            }
            try {
                ChunkPos chunkPos2 = EntityChunkDataAccess.getChunkPos(nbtCompound);
                if (!Objects.equals(pos, chunkPos2)) {
                    LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", (Object)pos, (Object)pos, (Object)chunkPos2);
                }
            } catch (Exception exception) {
                LOGGER.warn("Failed to parse chunk {} position info", (Object)pos, (Object)exception);
            }
            NbtCompound nbtCompound2 = this.fixChunkData((NbtCompound)nbtCompound);
            NbtList nbtList = nbtCompound2.getList(ENTITIES_KEY, 10);
            List list = EntityType.streamFromNbt(nbtList, this.world).collect(ImmutableList.toImmutableList());
            return new ChunkDataList(pos, list);
        }, this.field_34056::send);
    }

    private static ChunkPos getChunkPos(NbtCompound chunkTag) {
        int[] is = chunkTag.getIntArray(POSITION_KEY);
        return new ChunkPos(is[0], is[1]);
    }

    private static void putChunkPos(NbtCompound chunkTag, ChunkPos pos) {
        chunkTag.put(POSITION_KEY, new NbtIntArray(new int[]{pos.x, pos.z}));
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
        NbtList nbtList = new NbtList();
        dataList.stream().forEach(entity -> {
            NbtCompound nbtCompound = new NbtCompound();
            if (entity.saveNbt(nbtCompound)) {
                nbtList.add(nbtCompound);
            }
        });
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        nbtCompound.put(ENTITIES_KEY, nbtList);
        EntityChunkDataAccess.putChunkPos(nbtCompound, chunkPos);
        this.dataLoadWorker.setResult(chunkPos, nbtCompound).exceptionally(throwable -> {
            LOGGER.error("Failed to store chunk {}", (Object)chunkPos, throwable);
            return null;
        });
        this.emptyChunks.remove(chunkPos.toLong());
    }

    @Override
    public void awaitAll(boolean bl) {
        this.dataLoadWorker.completeAll(bl).join();
        this.field_34056.method_37477();
    }

    private NbtCompound fixChunkData(NbtCompound chunkTag) {
        int i = EntityChunkDataAccess.getChunkDataVersion(chunkTag);
        return NbtHelper.update(this.dataFixer, DataFixTypes.ENTITY_CHUNK, chunkTag, i);
    }

    public static int getChunkDataVersion(NbtCompound chunkTag) {
        return chunkTag.contains("DataVersion", 99) ? chunkTag.getInt("DataVersion") : -1;
    }

    @Override
    public void close() throws IOException {
        this.dataLoadWorker.close();
    }
}

