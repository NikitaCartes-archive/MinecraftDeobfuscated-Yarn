/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.FeatureUpdater;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.storage.StorageIoWorker;
import org.jetbrains.annotations.Nullable;

public class VersionedChunkStorage
implements AutoCloseable {
    private final StorageIoWorker worker;
    protected final DataFixer dataFixer;
    @Nullable
    private FeatureUpdater featureUpdater;

    public VersionedChunkStorage(File directory, DataFixer dataFixer, boolean dsync) {
        this.dataFixer = dataFixer;
        this.worker = new StorageIoWorker(directory, dsync, "chunk");
    }

    public NbtCompound updateChunkNbt(RegistryKey<World> worldKey, Supplier<PersistentStateManager> persistentStateManagerFactory, NbtCompound nbt) {
        int i = VersionedChunkStorage.getDataVersion(nbt);
        int j = 1493;
        if (i < 1493 && (nbt = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, nbt, i, 1493)).getCompound("Level").getBoolean("hasLegacyStructureData")) {
            if (this.featureUpdater == null) {
                this.featureUpdater = FeatureUpdater.create(worldKey, persistentStateManagerFactory.get());
            }
            nbt = this.featureUpdater.getUpdatedReferences(nbt);
        }
        nbt.getCompound("Level").putString("__dimension", worldKey.getValue().toString());
        nbt = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, nbt, Math.max(1493, i));
        if (i < SharedConstants.getGameVersion().getWorldVersion()) {
            nbt.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        }
        nbt.getCompound("Level").remove("__dimension");
        return nbt;
    }

    public static int getDataVersion(NbtCompound nbt) {
        return nbt.contains("DataVersion", 99) ? nbt.getInt("DataVersion") : -1;
    }

    @Nullable
    public NbtCompound getNbt(ChunkPos chunkPos) throws IOException {
        return this.worker.getNbt(chunkPos);
    }

    public void setNbt(ChunkPos chunkPos, NbtCompound nbt) {
        this.worker.setResult(chunkPos, nbt);
        if (this.featureUpdater != null) {
            this.featureUpdater.markResolved(chunkPos.toLong());
        }
    }

    public void completeAll() {
        this.worker.completeAll(true).join();
    }

    @Override
    public void close() throws IOException {
        this.worker.close();
    }
}

