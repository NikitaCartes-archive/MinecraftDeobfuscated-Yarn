package net.minecraft.world.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.class_6836;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ThrowableDeliverer;
import net.minecraft.util.math.ChunkPos;

public final class RegionBasedStorage implements AutoCloseable {
	public static final String field_31425 = ".mca";
	private static final int field_31426 = 256;
	private final Long2ObjectLinkedOpenHashMap<RegionFile> cachedRegionFiles = new Long2ObjectLinkedOpenHashMap<>();
	private final Path directory;
	private final boolean dsync;

	RegionBasedStorage(Path path, boolean dsync) {
		this.directory = path;
		this.dsync = dsync;
	}

	private RegionFile getRegionFile(ChunkPos pos) throws IOException {
		long l = ChunkPos.toLong(pos.getRegionX(), pos.getRegionZ());
		RegionFile regionFile = this.cachedRegionFiles.getAndMoveToFirst(l);
		if (regionFile != null) {
			return regionFile;
		} else {
			if (this.cachedRegionFiles.size() >= 256) {
				this.cachedRegionFiles.removeLast().close();
			}

			Files.createDirectories(this.directory);
			Path path = this.directory.resolve("r." + pos.getRegionX() + "." + pos.getRegionZ() + ".mca");
			RegionFile regionFile2 = new RegionFile(path, this.directory, this.dsync);
			this.cachedRegionFiles.putAndMoveToFirst(l, regionFile2);
			return regionFile2;
		}
	}

	@Nullable
	public NbtCompound getTagAt(ChunkPos pos) throws IOException {
		RegionFile regionFile = this.getRegionFile(pos);
		DataInputStream dataInputStream = regionFile.getChunkInputStream(pos);

		NbtCompound var8;
		label43: {
			try {
				if (dataInputStream == null) {
					var8 = null;
					break label43;
				}

				var8 = NbtIo.read(dataInputStream);
			} catch (Throwable var7) {
				if (dataInputStream != null) {
					try {
						dataInputStream.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}
				}

				throw var7;
			}

			if (dataInputStream != null) {
				dataInputStream.close();
			}

			return var8;
		}

		if (dataInputStream != null) {
			dataInputStream.close();
		}

		return var8;
	}

	public void method_39802(ChunkPos chunkPos, class_6836 arg) throws IOException {
		RegionFile regionFile = this.getRegionFile(chunkPos);
		DataInputStream dataInputStream = regionFile.getChunkInputStream(chunkPos);

		try {
			if (dataInputStream != null) {
				NbtIo.method_39855(dataInputStream, arg);
			}
		} catch (Throwable var8) {
			if (dataInputStream != null) {
				try {
					dataInputStream.close();
				} catch (Throwable var7) {
					var8.addSuppressed(var7);
				}
			}

			throw var8;
		}

		if (dataInputStream != null) {
			dataInputStream.close();
		}
	}

	protected void write(ChunkPos pos, @Nullable NbtCompound nbt) throws IOException {
		RegionFile regionFile = this.getRegionFile(pos);
		if (nbt == null) {
			regionFile.method_31740(pos);
		} else {
			DataOutputStream dataOutputStream = regionFile.getChunkOutputStream(pos);

			try {
				NbtIo.write(nbt, dataOutputStream);
			} catch (Throwable var8) {
				if (dataOutputStream != null) {
					try {
						dataOutputStream.close();
					} catch (Throwable var7) {
						var8.addSuppressed(var7);
					}
				}

				throw var8;
			}

			if (dataOutputStream != null) {
				dataOutputStream.close();
			}
		}
	}

	public void close() throws IOException {
		ThrowableDeliverer<IOException> throwableDeliverer = new ThrowableDeliverer();

		for (RegionFile regionFile : this.cachedRegionFiles.values()) {
			try {
				regionFile.close();
			} catch (IOException var5) {
				throwableDeliverer.add(var5);
			}
		}

		throwableDeliverer.deliver();
	}

	public void sync() throws IOException {
		for (RegionFile regionFile : this.cachedRegionFiles.values()) {
			regionFile.sync();
		}
	}
}
