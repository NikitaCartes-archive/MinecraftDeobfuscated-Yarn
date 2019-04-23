/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionFile;
import org.jetbrains.annotations.Nullable;

public abstract class RegionBasedStorage
implements AutoCloseable {
    protected final Long2ObjectLinkedOpenHashMap<RegionFile> cachedRegionFiles = new Long2ObjectLinkedOpenHashMap();
    private final File directory;

    protected RegionBasedStorage(File file) {
        this.directory = file;
    }

    private RegionFile getRegionFile(ChunkPos chunkPos) throws IOException {
        long l = ChunkPos.toLong(chunkPos.getRegionX(), chunkPos.getRegionZ());
        RegionFile regionFile = this.cachedRegionFiles.getAndMoveToFirst(l);
        if (regionFile != null) {
            return regionFile;
        }
        if (this.cachedRegionFiles.size() >= 256) {
            this.cachedRegionFiles.removeLast();
        }
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }
        File file = new File(this.directory, "r." + chunkPos.getRegionX() + "." + chunkPos.getRegionZ() + ".mca");
        RegionFile regionFile2 = new RegionFile(file);
        this.cachedRegionFiles.putAndMoveToFirst(l, regionFile2);
        return regionFile2;
    }

    @Nullable
    public CompoundTag getTagAt(ChunkPos chunkPos) throws IOException {
        RegionFile regionFile = this.getRegionFile(chunkPos);
        try (DataInputStream dataInputStream = regionFile.getChunkDataInputStream(chunkPos);){
            if (dataInputStream == null) {
                CompoundTag compoundTag = null;
                return compoundTag;
            }
            CompoundTag compoundTag = NbtIo.read(dataInputStream);
            return compoundTag;
        }
    }

    protected void setTagAt(ChunkPos chunkPos, CompoundTag compoundTag) throws IOException {
        RegionFile regionFile = this.getRegionFile(chunkPos);
        try (DataOutputStream dataOutputStream = regionFile.getChunkDataOutputStream(chunkPos);){
            NbtIo.write(compoundTag, (DataOutput)dataOutputStream);
        }
    }

    @Override
    public void close() throws IOException {
        for (RegionFile regionFile : this.cachedRegionFiles.values()) {
            regionFile.close();
        }
    }
}

