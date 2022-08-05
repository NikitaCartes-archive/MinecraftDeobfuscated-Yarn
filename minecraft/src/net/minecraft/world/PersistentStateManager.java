package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import org.slf4j.Logger;

public class PersistentStateManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String, PersistentState> loadedStates = Maps.<String, PersistentState>newHashMap();
	private final DataFixer dataFixer;
	private final File directory;

	public PersistentStateManager(File directory, DataFixer dataFixer) {
		this.dataFixer = dataFixer;
		this.directory = directory;
	}

	private File getFile(String id) {
		return new File(this.directory, id + ".dat");
	}

	public <T extends PersistentState> T getOrCreate(Function<NbtCompound, T> readFunction, Supplier<T> supplier, String id) {
		T persistentState = this.get(readFunction, id);
		if (persistentState != null) {
			return persistentState;
		} else {
			T persistentState2 = (T)supplier.get();
			this.set(id, persistentState2);
			return persistentState2;
		}
	}

	@Nullable
	public <T extends PersistentState> T get(Function<NbtCompound, T> readFunction, String id) {
		PersistentState persistentState = (PersistentState)this.loadedStates.get(id);
		if (persistentState == null && !this.loadedStates.containsKey(id)) {
			persistentState = this.readFromFile(readFunction, id);
			this.loadedStates.put(id, persistentState);
		}

		return (T)persistentState;
	}

	@Nullable
	private <T extends PersistentState> T readFromFile(Function<NbtCompound, T> readFunction, String id) {
		try {
			File file = this.getFile(id);
			if (file.exists()) {
				NbtCompound nbtCompound = this.readNbt(id, SharedConstants.getGameVersion().getWorldVersion());
				return (T)readFunction.apply(nbtCompound.getCompound("data"));
			}
		} catch (Exception var5) {
			LOGGER.error("Error loading saved data: {}", id, var5);
		}

		return null;
	}

	public void set(String id, PersistentState state) {
		this.loadedStates.put(id, state);
	}

	public NbtCompound readNbt(String id, int dataVersion) throws IOException {
		File file = this.getFile(id);
		FileInputStream fileInputStream = new FileInputStream(file);

		NbtCompound var8;
		try {
			PushbackInputStream pushbackInputStream = new PushbackInputStream(fileInputStream, 2);

			try {
				NbtCompound nbtCompound;
				if (this.isCompressed(pushbackInputStream)) {
					nbtCompound = NbtIo.readCompressed(pushbackInputStream);
				} else {
					DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);

					try {
						nbtCompound = NbtIo.read(dataInputStream);
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

				int i = nbtCompound.contains("DataVersion", NbtElement.NUMBER_TYPE) ? nbtCompound.getInt("DataVersion") : 1343;
				var8 = NbtHelper.update(this.dataFixer, DataFixTypes.SAVED_DATA, nbtCompound, i, dataVersion);
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
			try {
				fileInputStream.close();
			} catch (Throwable var10) {
				var15.addSuppressed(var10);
			}

			throw var15;
		}

		fileInputStream.close();
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

	public void save() {
		this.loadedStates.forEach((id, state) -> {
			if (state != null) {
				state.save(this.getFile(id));
			}
		});
	}
}
