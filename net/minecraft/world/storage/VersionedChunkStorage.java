/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.FeatureUpdater;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.RegionBasedStorage;
import net.minecraft.world.storage.StorageIoWorker;
import org.jetbrains.annotations.Nullable;

public class VersionedChunkStorage
implements AutoCloseable {
    private final StorageIoWorker worker;
    protected final DataFixer dataFixer;
    @Nullable
    private FeatureUpdater featureUpdater;

    public VersionedChunkStorage(File file, DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        this.worker = new StorageIoWorker(new RegionBasedStorage(file), "chunk");
    }

    public CompoundTag updateChunkTag(DimensionType dimensionType, Supplier<PersistentStateManager> persistentStateManagerFactory, CompoundTag tag) {
        int i = VersionedChunkStorage.getDataVersion(tag);
        int j = 1493;
        if (i < 1493 && (tag = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, tag, i, 1493)).getCompound("Level").getBoolean("hasLegacyStructureData")) {
            if (this.featureUpdater == null) {
                this.featureUpdater = FeatureUpdater.create(dimensionType, persistentStateManagerFactory.get());
            }
            tag = this.featureUpdater.getUpdatedReferences(tag);
        }
        tag = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, tag, Math.max(1493, i));
        if (i < SharedConstants.getGameVersion().getWorldVersion()) {
            tag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        }
        return tag;
    }

    public static int getDataVersion(CompoundTag tag) {
        return tag.contains("DataVersion", 99) ? tag.getInt("DataVersion") : -1;
    }

    @Nullable
    public CompoundTag getNbt(ChunkPos chunkPos) throws IOException {
        return this.worker.getNbt(chunkPos);
    }

    public void setTagAt(ChunkPos chunkPos, CompoundTag compoundTag) {
        this.worker.setResult(chunkPos, compoundTag);
        if (this.featureUpdater != null) {
            this.featureUpdater.markResolved(chunkPos.toLong());
        }
    }

    public void completeAll() {
        this.worker.completeAll().join();
    }

    @Override
    public void close() throws IOException {
        this.worker.close();
    }
}

