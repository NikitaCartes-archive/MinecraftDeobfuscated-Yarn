package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ChunkPos;
import org.apache.commons.io.FileUtils;

public class RecreationStorage extends ChunkPosKeyedStorage {
	private final StorageIoWorker recreationWorker;
	private final Path outputDirectory;

	public RecreationStorage(
		StorageKey storageKey, Path directory, StorageKey outputStorageKey, Path outputDirectory, DataFixer dataFixer, boolean dsync, DataFixTypes dataFixTypes
	) {
		super(storageKey, directory, dataFixer, dsync, dataFixTypes);
		this.outputDirectory = outputDirectory;
		this.recreationWorker = new StorageIoWorker(outputStorageKey, outputDirectory, dsync);
	}

	@Override
	public CompletableFuture<Void> set(ChunkPos pos, @Nullable NbtCompound nbt) {
		return this.recreationWorker.setResult(pos, nbt);
	}

	@Override
	public void close() throws IOException {
		super.close();
		this.recreationWorker.close();
		if (this.outputDirectory.toFile().exists()) {
			FileUtils.deleteDirectory(this.outputDirectory.toFile());
		}
	}
}
