package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class VersionedChunkStorage implements AutoCloseable {
	public static final int field_36219 = 1493;
	private final StorageIoWorker worker;
	protected final DataFixer dataFixer;
	@Nullable
	private volatile FeatureUpdater featureUpdater;

	public VersionedChunkStorage(Path directory, DataFixer dataFixer, boolean dsync) {
		this.dataFixer = dataFixer;
		this.worker = new StorageIoWorker(directory, dsync, "chunk");
	}

	public boolean needsBlending(ChunkPos chunkPos, int checkRadius) {
		return this.worker.needsBlending(chunkPos, checkRadius);
	}

	public NbtCompound updateChunkNbt(
		RegistryKey<World> worldKey,
		Supplier<PersistentStateManager> persistentStateManagerFactory,
		NbtCompound nbt,
		Optional<RegistryKey<Codec<? extends ChunkGenerator>>> generatorCodecKey
	) {
		int i = getDataVersion(nbt);
		if (i < 1493) {
			nbt = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, nbt, i, 1493);
			if (nbt.getCompound("Level").getBoolean("hasLegacyStructureData")) {
				FeatureUpdater featureUpdater = this.method_43411(worldKey, persistentStateManagerFactory);
				nbt = featureUpdater.getUpdatedReferences(nbt);
			}
		}

		saveContextToNbt(nbt, worldKey, generatorCodecKey);
		nbt = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, nbt, Math.max(1493, i));
		if (i < SharedConstants.getGameVersion().getWorldVersion()) {
			nbt.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		}

		nbt.remove("__context");
		return nbt;
	}

	private FeatureUpdater method_43411(RegistryKey<World> registryKey, Supplier<PersistentStateManager> supplier) {
		FeatureUpdater featureUpdater = this.featureUpdater;
		if (featureUpdater == null) {
			synchronized (this) {
				featureUpdater = this.featureUpdater;
				if (featureUpdater == null) {
					this.featureUpdater = featureUpdater = FeatureUpdater.create(registryKey, (PersistentStateManager)supplier.get());
				}
			}
		}

		return featureUpdater;
	}

	public static void saveContextToNbt(NbtCompound nbt, RegistryKey<World> worldKey, Optional<RegistryKey<Codec<? extends ChunkGenerator>>> generatorCodecKey) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("dimension", worldKey.getValue().toString());
		generatorCodecKey.ifPresent(key -> nbtCompound.putString("generator", key.getValue().toString()));
		nbt.put("__context", nbtCompound);
	}

	public static int getDataVersion(NbtCompound nbt) {
		return nbt.contains("DataVersion", NbtElement.NUMBER_TYPE) ? nbt.getInt("DataVersion") : -1;
	}

	public CompletableFuture<Optional<NbtCompound>> getNbt(ChunkPos chunkPos) {
		return this.worker.readChunkData(chunkPos);
	}

	public void setNbt(ChunkPos chunkPos, NbtCompound nbt) {
		this.worker.setResult(chunkPos, nbt);
		if (this.featureUpdater != null) {
			this.featureUpdater.markResolved(chunkPos.toLong());
		}
	}

	public void completeAll() {
		this.worker.completeAll(true).join();
	}

	public void close() throws IOException {
		this.worker.close();
	}

	public NbtScannable getWorker() {
		return this.worker;
	}
}
