package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class PersistentStateManager implements AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String, Optional<PersistentState>> loadedStates = new HashMap();
	private final DataFixer dataFixer;
	private final RegistryWrapper.WrapperLookup registries;
	private final Path directory;
	private CompletableFuture<?> savingFuture = CompletableFuture.completedFuture(null);

	public PersistentStateManager(Path directory, DataFixer dataFixer, RegistryWrapper.WrapperLookup registries) {
		this.dataFixer = dataFixer;
		this.directory = directory;
		this.registries = registries;
	}

	private Path getFile(String id) {
		return this.directory.resolve(id + ".dat");
	}

	public <T extends PersistentState> T getOrCreate(PersistentState.Type<T> type, String id) {
		T persistentState = this.get(type, id);
		if (persistentState != null) {
			return persistentState;
		} else {
			T persistentState2 = (T)type.constructor().get();
			this.set(id, persistentState2);
			return persistentState2;
		}
	}

	@Nullable
	public <T extends PersistentState> T get(PersistentState.Type<T> type, String id) {
		Optional<PersistentState> optional = (Optional<PersistentState>)this.loadedStates.get(id);
		if (optional == null) {
			optional = Optional.ofNullable(this.readFromFile(type.deserializer(), type.type(), id));
			this.loadedStates.put(id, optional);
		}

		return (T)optional.orElse(null);
	}

	@Nullable
	private <T extends PersistentState> T readFromFile(
		BiFunction<NbtCompound, RegistryWrapper.WrapperLookup, T> readFunction, DataFixTypes dataFixTypes, String id
	) {
		try {
			Path path = this.getFile(id);
			if (Files.exists(path, new LinkOption[0])) {
				NbtCompound nbtCompound = this.readNbt(id, dataFixTypes, SharedConstants.getGameVersion().getSaveVersion().getId());
				return (T)readFunction.apply(nbtCompound.getCompound("data"), this.registries);
			}
		} catch (Exception var6) {
			LOGGER.error("Error loading saved data: {}", id, var6);
		}

		return null;
	}

	public void set(String id, PersistentState state) {
		this.loadedStates.put(id, Optional.of(state));
		state.markDirty();
	}

	public NbtCompound readNbt(String id, DataFixTypes dataFixTypes, int currentSaveVersion) throws IOException {
		InputStream inputStream = Files.newInputStream(this.getFile(id));

		NbtCompound var8;
		try {
			PushbackInputStream pushbackInputStream = new PushbackInputStream(new FixedBufferInputStream(inputStream), 2);

			try {
				NbtCompound nbtCompound;
				if (this.isCompressed(pushbackInputStream)) {
					nbtCompound = NbtIo.readCompressed(pushbackInputStream, NbtSizeTracker.ofUnlimitedBytes());
				} else {
					DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);

					try {
						nbtCompound = NbtIo.readCompound(dataInputStream);
					} catch (Throwable var13) {
						try {
							dataInputStream.close();
						} catch (Throwable var12) {
							var13.addSuppressed(var12);
						}

						throw var13;
					}

					dataInputStream.close();
				}

				int i = NbtHelper.getDataVersion(nbtCompound, 1343);
				var8 = dataFixTypes.update(this.dataFixer, nbtCompound, i, currentSaveVersion);
			} catch (Throwable var14) {
				try {
					pushbackInputStream.close();
				} catch (Throwable var11) {
					var14.addSuppressed(var11);
				}

				throw var14;
			}

			pushbackInputStream.close();
		} catch (Throwable var15) {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable var10) {
					var15.addSuppressed(var10);
				}
			}

			throw var15;
		}

		if (inputStream != null) {
			inputStream.close();
		}

		return var8;
	}

	private boolean isCompressed(PushbackInputStream stream) throws IOException {
		byte[] bs = new byte[2];
		boolean bl = false;
		int i = stream.read(bs, 0, 2);
		if (i == 2) {
			int j = (bs[1] & 255) << 8 | bs[0] & 255;
			if (j == 35615) {
				bl = true;
			}
		}

		if (i != 0) {
			stream.unread(bs, 0, i);
		}

		return bl;
	}

	public CompletableFuture<?> startSaving() {
		Map<Path, NbtCompound> map = this.collectStatesToSave();
		if (map.isEmpty()) {
			return CompletableFuture.completedFuture(null);
		} else {
			this.savingFuture = this.savingFuture
				.thenCompose(
					v -> CompletableFuture.allOf(
							(CompletableFuture[])map.entrySet().stream().map(entry -> save((Path)entry.getKey(), (NbtCompound)entry.getValue())).toArray(CompletableFuture[]::new)
						)
				);
			return this.savingFuture;
		}
	}

	private Map<Path, NbtCompound> collectStatesToSave() {
		Map<Path, NbtCompound> map = new Object2ObjectArrayMap<>();
		this.loadedStates
			.forEach((id, state) -> state.filter(PersistentState::isDirty).ifPresent(state2 -> map.put(this.getFile(id), state2.toNbt(this.registries))));
		return map;
	}

	private static CompletableFuture<Void> save(Path path, NbtCompound nbt) {
		return CompletableFuture.runAsync(() -> {
			try {
				NbtIo.writeCompressed(nbt, path);
			} catch (IOException var3) {
				LOGGER.error("Could not save data to {}", path.getFileName(), var3);
			}
		}, Util.getIoWorkerExecutor());
	}

	public void save() {
		this.startSaving().join();
	}

	public void close() {
		this.save();
	}
}
