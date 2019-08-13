package net.minecraft.world.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.math.ChunkPos;

public abstract class RegionBasedStorage implements AutoCloseable {
	protected final Long2ObjectLinkedOpenHashMap<RegionFile> cachedRegionFiles = new Long2ObjectLinkedOpenHashMap<>();
	private final File directory;

	protected RegionBasedStorage(File file) {
		this.directory = file;
	}

	private RegionFile getRegionFile(ChunkPos chunkPos) throws IOException {
		long l = ChunkPos.toLong(chunkPos.getRegionX(), chunkPos.getRegionZ());
		RegionFile regionFile = this.cachedRegionFiles.getAndMoveToFirst(l);
		if (regionFile != null) {
			return regionFile;
		} else {
			if (this.cachedRegionFiles.size() >= 256) {
				this.cachedRegionFiles.removeLast().close();
			}

			if (!this.directory.exists()) {
				this.directory.mkdirs();
			}

			File file = new File(this.directory, "r." + chunkPos.getRegionX() + "." + chunkPos.getRegionZ() + ".mca");
			RegionFile regionFile2 = new RegionFile(file);
			this.cachedRegionFiles.putAndMoveToFirst(l, regionFile2);
			return regionFile2;
		}
	}

	@Nullable
	public CompoundTag getTagAt(ChunkPos chunkPos) throws IOException {
		RegionFile regionFile = this.getRegionFile(chunkPos);
		DataInputStream dataInputStream = regionFile.getChunkDataInputStream(chunkPos);
		Throwable var4 = null;

		Object var5;
		try {
			if (dataInputStream != null) {
				return NbtIo.read(dataInputStream);
			}

			var5 = null;
		} catch (Throwable var15) {
			var4 = var15;
			throw var15;
		} finally {
			if (dataInputStream != null) {
				if (var4 != null) {
					try {
						dataInputStream.close();
					} catch (Throwable var14) {
						var4.addSuppressed(var14);
					}
				} else {
					dataInputStream.close();
				}
			}
		}

		return (CompoundTag)var5;
	}

	protected void setTagAt(ChunkPos chunkPos, CompoundTag compoundTag) throws IOException {
		RegionFile regionFile = this.getRegionFile(chunkPos);
		DataOutputStream dataOutputStream = regionFile.getChunkDataOutputStream(chunkPos);
		Throwable var5 = null;

		try {
			NbtIo.write(compoundTag, dataOutputStream);
		} catch (Throwable var14) {
			var5 = var14;
			throw var14;
		} finally {
			if (dataOutputStream != null) {
				if (var5 != null) {
					try {
						dataOutputStream.close();
					} catch (Throwable var13) {
						var5.addSuppressed(var13);
					}
				} else {
					dataOutputStream.close();
				}
			}
		}
	}

	public void close() throws IOException {
		for (RegionFile regionFile : this.cachedRegionFiles.values()) {
			regionFile.close();
		}
	}
}
