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
import net.minecraft.util.ThrowableDeliverer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionFile;
import org.jetbrains.annotations.Nullable;

public final class RegionBasedStorage
implements AutoCloseable {
    private final Long2ObjectLinkedOpenHashMap<RegionFile> cachedRegionFiles = new Long2ObjectLinkedOpenHashMap();
    private final File directory;
    private final boolean dsync;

    RegionBasedStorage(File directory, boolean dsync) {
        this.directory = directory;
        this.dsync = dsync;
    }

    private RegionFile getRegionFile(ChunkPos pos) throws IOException {
        long l = ChunkPos.toLong(pos.getRegionX(), pos.getRegionZ());
        RegionFile regionFile = this.cachedRegionFiles.getAndMoveToFirst(l);
        if (regionFile != null) {
            return regionFile;
        }
        if (this.cachedRegionFiles.size() >= 256) {
            this.cachedRegionFiles.removeLast().close();
        }
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }
        File file = new File(this.directory, "r." + pos.getRegionX() + "." + pos.getRegionZ() + ".mca");
        RegionFile regionFile2 = new RegionFile(file, this.directory, this.dsync);
        this.cachedRegionFiles.putAndMoveToFirst(l, regionFile2);
        return regionFile2;
    }

    @Nullable
    public CompoundTag getTagAt(ChunkPos pos) throws IOException {
        RegionFile regionFile = this.getRegionFile(pos);
        try (DataInputStream dataInputStream = regionFile.getChunkInputStream(pos);){
            if (dataInputStream == null) {
                CompoundTag compoundTag = null;
                return compoundTag;
            }
            CompoundTag compoundTag = NbtIo.read(dataInputStream);
            return compoundTag;
        }
    }

    protected void write(ChunkPos pos, @Nullable CompoundTag tag) throws IOException {
        RegionFile regionFile = this.getRegionFile(pos);
        if (tag == null) {
            regionFile.method_31740(pos);
        } else {
            try (DataOutputStream dataOutputStream = regionFile.getChunkOutputStream(pos);){
                NbtIo.write(tag, (DataOutput)dataOutputStream);
            }
        }
    }

    @Override
    public void close() throws IOException {
        ThrowableDeliverer<IOException> throwableDeliverer = new ThrowableDeliverer<IOException>();
        for (RegionFile regionFile : this.cachedRegionFiles.values()) {
            try {
                regionFile.close();
            } catch (IOException iOException) {
                throwableDeliverer.add(iOException);
            }
        }
        throwableDeliverer.deliver();
    }

    public void method_26982() throws IOException {
        for (RegionFile regionFile : this.cachedRegionFiles.values()) {
            regionFile.method_26981();
        }
    }
}

