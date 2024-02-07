package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Map;
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
import org.slf4j.Logger;

public class PersistentStateManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String, PersistentState> loadedStates = Maps.<String, PersistentState>newHashMap();
	private final DataFixer dataFixer;
	private final RegistryWrapper.WrapperLookup registryLookup;
	private final File directory;

	public PersistentStateManager(File directory, DataFixer dataFixer, RegistryWrapper.WrapperLookup registryLookup) {
		this.dataFixer = dataFixer;
		this.directory = directory;
		this.registryLookup = registryLookup;
	}

	private File getFile(String id) {
		return new File(this.directory, id + ".dat");
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
		PersistentState persistentState = (PersistentState)this.loadedStates.get(id);
		if (persistentState == null && !this.loadedStates.containsKey(id)) {
			persistentState = this.readFromFile(type.deserializer(), type.type(), id);
			this.loadedStates.put(id, persistentState);
		}

		return (T)persistentState;
	}

	@Nullable
	private <T extends PersistentState> T readFromFile(
		BiFunction<NbtCompound, RegistryWrapper.WrapperLookup, T> readFunction, DataFixTypes dataFixTypes, String id
	) {
		try {
			File file = this.getFile(id);
			if (file.exists()) {
				NbtCompound nbtCompound = this.readNbt(id, dataFixTypes, SharedConstants.getGameVersion().getSaveVersion().getId());
				return (T)readFunction.apply(nbtCompound.getCompound("data"), this.registryLookup);
			}
		} catch (Exception var6) {
			LOGGER.error("Error loading saved data: {}", id, var6);
		}

		return null;
	}

	public void set(String id, PersistentState state) {
		this.loadedStates.put(id, state);
	}

	public NbtCompound readNbt(String id, DataFixTypes dataFixTypes, int currentSaveVersion) throws IOException {
		File file = this.getFile(id);
		InputStream inputStream = new FileInputStream(file);

		NbtCompound var9;
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
					} catch (Throwable var14) {
						try {
							dataInputStream.close();
						} catch (Throwable var13) {
							var14.addSuppressed(var13);
						}

						throw var14;
					}

					dataInputStream.close();
				}

				int i = NbtHelper.getDataVersion(nbtCompound, 1343);
				var9 = dataFixTypes.update(this.dataFixer, nbtCompound, i, currentSaveVersion);
			} catch (Throwable var15) {
				try {
					pushbackInputStream.close();
				} catch (Throwable var12) {
					var15.addSuppressed(var12);
				}

				throw var15;
			}

			pushbackInputStream.close();
		} catch (Throwable var16) {
			try {
				inputStream.close();
			} catch (Throwable var11) {
				var16.addSuppressed(var11);
			}

			throw var16;
		}

		inputStream.close();
		return var9;
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

	public void save() {
		this.loadedStates.forEach((id, state) -> {
			if (state != null) {
				state.save(this.getFile(id), this.registryLookup);
			}
		});
	}
}
