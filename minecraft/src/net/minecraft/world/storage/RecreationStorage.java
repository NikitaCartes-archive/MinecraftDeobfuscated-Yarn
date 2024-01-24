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

	public RecreationStorage(Path directory, Path outputDirectory, DataFixer dataFixer, boolean dsync, String name, DataFixTypes dataFixTypes) {
		super(directory, dataFixer, dsync, name, dataFixTypes);
		this.outputDirectory = outputDirectory;
		this.recreationWorker = new StorageIoWorker(outputDirectory, dsync, name + "-recreating");
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
