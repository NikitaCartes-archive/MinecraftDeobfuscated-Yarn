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
	protected final Long2ObjectLinkedOpenHashMap<RegionFile> field_17657 = new Long2ObjectLinkedOpenHashMap<>();

	private RegionFile getRegionFile(ChunkPos chunkPos) throws IOException {
		long l = ChunkPos.toLong(chunkPos.method_17885(), chunkPos.method_17886());
		RegionFile regionFile = this.field_17657.getAndMoveToFirst(l);
		if (regionFile != null) {
			return regionFile;
		} else {
			if (this.field_17657.size() >= 256) {
				this.field_17657.removeLast();
			}

			File file = this.method_17912();
			if (!file.exists()) {
				file.mkdirs();
			}

			File file2 = new File(file, "r." + chunkPos.method_17885() + "." + chunkPos.method_17886() + ".mca");
			RegionFile regionFile2 = new RegionFile(file2);
			this.field_17657.putAndMoveToFirst(l, regionFile2);
			return regionFile2;
		}
	}

	@Nullable
	public CompoundTag method_17911(ChunkPos chunkPos) throws IOException {
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

	protected void method_17910(ChunkPos chunkPos, CompoundTag compoundTag) throws IOException {
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
		for (RegionFile regionFile : this.field_17657.values()) {
			regionFile.close();
		}
	}

	protected abstract File method_17912();
}
