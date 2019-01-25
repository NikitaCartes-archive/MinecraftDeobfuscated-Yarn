package net.minecraft.world.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.chunk.ChunkPos;

public abstract class RegionFileCache implements AutoCloseable {
	protected final Long2ObjectLinkedOpenHashMap<RegionFile> cachedRegionFiles = new Long2ObjectLinkedOpenHashMap<>();

	private RegionFile getRegionFile(ChunkPos chunkPos) throws IOException {
		long l = ChunkPos.toLong(chunkPos.getRegionX(), chunkPos.getRegionZ());
		RegionFile regionFile = this.cachedRegionFiles.getAndMoveToFirst(l);
		if (regionFile != null) {
			return regionFile;
		} else {
			if (this.cachedRegionFiles.size() >= 256) {
				this.cachedRegionFiles.removeLast();
			}

			File file = this.getRegionDir();
			if (!file.exists()) {
				file.mkdirs();
			}

			File file2 = new File(file, "r." + chunkPos.getRegionX() + "." + chunkPos.getRegionZ() + ".mca");
			RegionFile regionFile2 = new RegionFile(file2);
			this.cachedRegionFiles.putAndMoveToFirst(l, regionFile2);
			return regionFile2;
		}
	}

	@Nullable
	public CompoundTag getChunkTag(ChunkPos chunkPos) throws IOException {
		RegionFile regionFile = this.getRegionFile(chunkPos);
		DataInputStream dataInputStream = regionFile.getChunkDataInputStream(chunkPos);
		Throwable var5 = null;

		Object var6;
		try {
			if (dataInputStream != null) {
				return NbtIo.read(dataInputStream);
			}

			var6 = null;
		} catch (Throwable var16) {
			var5 = var16;
			throw var16;
		} finally {
			if (dataInputStream != null) {
				if (var5 != null) {
					try {
						dataInputStream.close();
					} catch (Throwable var15) {
						var5.addSuppressed(var15);
					}
				} else {
					dataInputStream.close();
				}
			}
		}

		return (CompoundTag)var6;
	}

	protected void setChunkTag(ChunkPos chunkPos, CompoundTag compoundTag) throws IOException {
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

	protected abstract File getRegionDir();
}
