package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.class_6830;
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
	private FeatureUpdater featureUpdater;

	public VersionedChunkStorage(Path directory, DataFixer dataFixer, boolean dsync) {
		this.dataFixer = dataFixer;
		this.worker = new StorageIoWorker(directory, dsync, "chunk");
	}

	public NbtCompound updateChunkNbt(
		RegistryKey<World> worldKey,
		Supplier<PersistentStateManager> persistentStateManagerFactory,
		NbtCompound nbt,
		Optional<RegistryKey<Codec<? extends ChunkGenerator>>> optional
	) {
		int i = getDataVersion(nbt);
		if (i < 1493) {
			nbt = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, nbt, i, 1493);
			if (nbt.getCompound("Level").getBoolean("hasLegacyStructureData")) {
				if (this.featureUpdater == null) {
					this.featureUpdater = FeatureUpdater.create(worldKey, (PersistentStateManager)persistentStateManagerFactory.get());
				}

				nbt = this.featureUpdater.getUpdatedReferences(nbt);
			}
		}

		method_39799(nbt, worldKey, optional);
		nbt = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, nbt, Math.max(1493, i));
		if (i < SharedConstants.getGameVersion().getWorldVersion()) {
			nbt.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		}

		nbt.remove("__context");
		return nbt;
	}

	public static void method_39799(NbtCompound nbtCompound, RegistryKey<World> registryKey, Optional<RegistryKey<Codec<? extends ChunkGenerator>>> optional) {
		NbtCompound nbtCompound2 = new NbtCompound();
		nbtCompound2.putString("dimension", registryKey.getValue().toString());
		optional.ifPresent(registryKeyx -> nbtCompound2.putString("generator", registryKeyx.getValue().toString()));
		nbtCompound.put("__context", nbtCompound2);
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
		this.worker.completeAll(true).join();
	}

	public void close() throws IOException {
		this.worker.close();
	}

	public class_6830 method_39800() {
		return this.worker;
	}
}
