package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.FeatureUpdater;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class VersionedChunkStorage implements AutoCloseable {
	private final StorageIoWorker worker;
	protected final DataFixer dataFixer;
	@Nullable
	private FeatureUpdater featureUpdater;

	public VersionedChunkStorage(File directory, DataFixer dataFixer, boolean dsync) {
		this.dataFixer = dataFixer;
		this.worker = new StorageIoWorker(directory, dsync, "chunk");
	}

	public NbtCompound updateChunkNbt(RegistryKey<World> worldKey, Supplier<PersistentStateManager> persistentStateManagerFactory, NbtCompound nbt) {
		int i = getDataVersion(nbt);
		int j = 1493;
		if (i < 1493) {
			nbt = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, nbt, i, 1493);
			if (nbt.getCompound("Level").getBoolean("hasLegacyStructureData")) {
				if (this.featureUpdater == null) {
					this.featureUpdater = FeatureUpdater.create(worldKey, (PersistentStateManager)persistentStateManagerFactory.get());
				}

				nbt = this.featureUpdater.getUpdatedReferences(nbt);
			}
		}

		nbt = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, nbt, Math.max(1493, i));
		if (i < SharedConstants.getGameVersion().getWorldVersion()) {
			nbt.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		}

		return nbt;
	}

	public static int getDataVersion(NbtCompound nbt) {
		return nbt.contains("DataVersion", NbtElement.NUMBER_TYPE) ? nbt.getInt("DataVersion") : -1;
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
		this.worker.completeAll().join();
	}

	public void close() throws IOException {
		this.worker.close();
	}
}
