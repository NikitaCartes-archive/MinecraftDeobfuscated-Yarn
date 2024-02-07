package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.ChunkPos;

public class ChunkPosKeyedStorage implements AutoCloseable {
	private final StorageIoWorker worker;
	private final DataFixer dataFixer;
	private final DataFixTypes dataFixTypes;

	public ChunkPosKeyedStorage(StorageKey storageKey, Path directory, DataFixer dataFixer, boolean dsync, DataFixTypes dataFixTypes) {
		this.dataFixer = dataFixer;
		this.dataFixTypes = dataFixTypes;
		this.worker = new StorageIoWorker(storageKey, directory, dsync);
	}

	public CompletableFuture<Optional<NbtCompound>> read(ChunkPos pos) {
		return this.worker.readChunkData(pos);
	}

	public CompletableFuture<Void> set(ChunkPos pos, @Nullable NbtCompound nbt) {
		return this.worker.setResult(pos, nbt);
	}

	public NbtCompound update(NbtCompound nbt, int oldVersion) {
		int i = NbtHelper.getDataVersion(nbt, oldVersion);
		return this.dataFixTypes.update(this.dataFixer, nbt, i);
	}

	public Dynamic<NbtElement> update(Dynamic<NbtElement> nbt, int oldVersion) {
		return this.dataFixTypes.update(this.dataFixer, nbt, oldVersion);
	}

	public CompletableFuture<Void> completeAll(boolean sync) {
		return this.worker.completeAll(sync);
	}

	public void close() throws IOException {
		this.worker.close();
	}
}
