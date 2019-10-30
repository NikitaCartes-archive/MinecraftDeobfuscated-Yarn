package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PersistentStateManager {
	private static final Logger LOGGER = LogManager.getLogger();
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

	public <T extends PersistentState> T getOrCreate(Supplier<T> factory, String id) {
		T persistentState = this.get(factory, id);
		if (persistentState != null) {
			return persistentState;
		} else {
			T persistentState2 = (T)factory.get();
			this.set(persistentState2);
			return persistentState2;
		}
	}

	@Nullable
	public <T extends PersistentState> T get(Supplier<T> factory, String id) {
		PersistentState persistentState = (PersistentState)this.loadedStates.get(id);
		if (persistentState == null && !this.loadedStates.containsKey(id)) {
			persistentState = this.readFromFile(factory, id);
			this.loadedStates.put(id, persistentState);
		}

		return (T)persistentState;
	}

	@Nullable
	private <T extends PersistentState> T readFromFile(Supplier<T> factory, String id) {
		try {
			File file = this.getFile(id);
			if (file.exists()) {
				T persistentState = (T)factory.get();
				CompoundTag compoundTag = this.readTag(id, SharedConstants.getGameVersion().getWorldVersion());
				persistentState.fromTag(compoundTag.getCompound("data"));
				return persistentState;
			}
		} catch (Exception var6) {
			LOGGER.error("Error loading saved data: {}", id, var6);
		}

		return null;
	}

	public void set(PersistentState state) {
		this.loadedStates.put(state.getId(), state);
	}

	public CompoundTag readTag(String id, int dataVersion) throws IOException {
		File file = this.getFile(id);
		PushbackInputStream pushbackInputStream = new PushbackInputStream(new FileInputStream(file), 2);
		Throwable var5 = null;

		CompoundTag var36;
		try {
			CompoundTag compoundTag;
			if (this.isCompressed(pushbackInputStream)) {
				compoundTag = NbtIo.readCompressed(pushbackInputStream);
			} else {
				DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);
				Throwable var8 = null;

				try {
					compoundTag = NbtIo.read(dataInputStream);
				} catch (Throwable var31) {
					var8 = var31;
					throw var31;
				} finally {
					if (dataInputStream != null) {
						if (var8 != null) {
							try {
								dataInputStream.close();
							} catch (Throwable var30) {
								var8.addSuppressed(var30);
							}
						} else {
							dataInputStream.close();
						}
					}
				}
			}

			int i = compoundTag.contains("DataVersion", 99) ? compoundTag.getInt("DataVersion") : 1343;
			var36 = NbtHelper.update(this.dataFixer, DataFixTypes.SAVED_DATA, compoundTag, i, dataVersion);
		} catch (Throwable var33) {
			var5 = var33;
			throw var33;
		} finally {
			if (pushbackInputStream != null) {
				if (var5 != null) {
					try {
						pushbackInputStream.close();
					} catch (Throwable var29) {
						var5.addSuppressed(var29);
					}
				} else {
					pushbackInputStream.close();
				}
			}
		}

		return var36;
	}

	private boolean isCompressed(PushbackInputStream pushbackInputStream) throws IOException {
		byte[] bs = new byte[2];
		boolean bl = false;
		int i = pushbackInputStream.read(bs, 0, 2);
		if (i == 2) {
			int j = (bs[1] & 255) << 8 | bs[0] & 255;
			if (j == 35615) {
				bl = true;
			}
		}

		if (i != 0) {
			pushbackInputStream.unread(bs, 0, i);
		}

		return bl;
	}

	public void save() {
		for (PersistentState persistentState : this.loadedStates.values()) {
			if (persistentState != null) {
				persistentState.save(this.getFile(persistentState.getId()));
			}
		}
	}
}
