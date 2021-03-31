package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
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

	public <T extends PersistentState> T getOrCreate(Function<NbtCompound, T> function, Supplier<T> supplier, String string) {
		T persistentState = this.get(function, string);
		if (persistentState != null) {
			return persistentState;
		} else {
			T persistentState2 = (T)supplier.get();
			this.set(string, persistentState2);
			return persistentState2;
		}
	}

	@Nullable
	public <T extends PersistentState> T get(Function<NbtCompound, T> function, String id) {
		PersistentState persistentState = (PersistentState)this.loadedStates.get(id);
		if (persistentState == null && !this.loadedStates.containsKey(id)) {
			persistentState = this.readFromFile(function, id);
			this.loadedStates.put(id, persistentState);
		}

		return (T)persistentState;
	}

	@Nullable
	private <T extends PersistentState> T readFromFile(Function<NbtCompound, T> function, String id) {
		try {
			File file = this.getFile(id);
			if (file.exists()) {
				NbtCompound nbtCompound = this.readNbt(id, SharedConstants.getGameVersion().getWorldVersion());
				return (T)function.apply(nbtCompound.getCompound("data"));
			}
		} catch (Exception var5) {
			LOGGER.error("Error loading saved data: {}", id, var5);
		}

		return null;
	}

	public void set(String string, PersistentState persistentState) {
		this.loadedStates.put(string, persistentState);
	}

	public NbtCompound readNbt(String id, int dataVersion) throws IOException {
		File file = this.getFile(id);
		FileInputStream fileInputStream = new FileInputStream(file);
		Throwable var5 = null;

		NbtCompound var61;
		try {
			PushbackInputStream pushbackInputStream = new PushbackInputStream(fileInputStream, 2);
			Throwable var7 = null;

			try {
				NbtCompound nbtCompound;
				if (this.isCompressed(pushbackInputStream)) {
					nbtCompound = NbtIo.readCompressed(pushbackInputStream);
				} else {
					DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);
					Throwable var10 = null;

					try {
						nbtCompound = NbtIo.read(dataInputStream);
					} catch (Throwable var54) {
						var10 = var54;
						throw var54;
					} finally {
						if (dataInputStream != null) {
							if (var10 != null) {
								try {
									dataInputStream.close();
								} catch (Throwable var53) {
									var10.addSuppressed(var53);
								}
							} else {
								dataInputStream.close();
							}
						}
					}
				}

				int i = nbtCompound.contains("DataVersion", NbtElement.NUMBER_TYPE) ? nbtCompound.getInt("DataVersion") : 1343;
				var61 = NbtHelper.update(this.dataFixer, DataFixTypes.SAVED_DATA, nbtCompound, i, dataVersion);
			} catch (Throwable var56) {
				var7 = var56;
				throw var56;
			} finally {
				if (pushbackInputStream != null) {
					if (var7 != null) {
						try {
							pushbackInputStream.close();
						} catch (Throwable var52) {
							var7.addSuppressed(var52);
						}
					} else {
						pushbackInputStream.close();
					}
				}
			}
		} catch (Throwable var58) {
			var5 = var58;
			throw var58;
		} finally {
			if (fileInputStream != null) {
				if (var5 != null) {
					try {
						fileInputStream.close();
					} catch (Throwable var51) {
						var5.addSuppressed(var51);
					}
				} else {
					fileInputStream.close();
				}
			}
		}

		return var61;
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
		this.loadedStates.forEach((string, persistentState) -> {
			if (persistentState != null) {
				persistentState.save(this.getFile(string));
			}
		});
	}
}
