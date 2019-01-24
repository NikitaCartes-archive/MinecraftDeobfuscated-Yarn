package net.minecraft.world.dimension;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.TagHelper;
import net.minecraft.world.PersistentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionalPersistentStateManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<String, PersistentState> keyToState = Maps.<String, PersistentState>newHashMap();
	private final DataFixer field_17663;
	private final File field_17664;

	public DimensionalPersistentStateManager(File file, DataFixer dataFixer) {
		this.field_17663 = dataFixer;
		this.field_17664 = file;
	}

	private File method_17922(String string) {
		return new File(this.field_17664, string + ".dat");
	}

	public <T extends PersistentState> T method_17924(Supplier<T> supplier, String string) {
		T persistentState = this.get(supplier, string);
		if (persistentState != null) {
			return persistentState;
		} else {
			T persistentState2 = (T)supplier.get();
			this.set(persistentState2);
			return persistentState2;
		}
	}

	@Nullable
	public <T extends PersistentState> T get(Supplier<T> supplier, String string) {
		PersistentState persistentState = (PersistentState)this.keyToState.get(string);
		if (persistentState == null) {
			try {
				File file = this.method_17922(string);
				if (file.exists()) {
					persistentState = (PersistentState)supplier.get();
					CompoundTag compoundTag = this.method_17923(string, SharedConstants.getGameVersion().getWorldVersion());
					persistentState.fromTag(compoundTag.getCompound("data"));
					this.keyToState.put(string, persistentState);
				}
			} catch (Exception var6) {
				LOGGER.error("Error loading saved data: {}", string, var6);
			}
		}

		return (T)persistentState;
	}

	public void set(PersistentState persistentState) {
		this.keyToState.put(persistentState.getId(), persistentState);
	}

	public CompoundTag method_17923(String string, int i) throws IOException {
		File file = this.method_17922(string);
		PushbackInputStream pushbackInputStream = new PushbackInputStream(new FileInputStream(file), 2);
		Throwable var5 = null;

		CompoundTag var36;
		try {
			CompoundTag compoundTag;
			if (this.method_17921(pushbackInputStream)) {
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

			int j = compoundTag.containsKey("DataVersion", 99) ? compoundTag.getInt("DataVersion") : 1343;
			var36 = TagHelper.update(this.field_17663, DataFixTypes.SAVED_DATA, compoundTag, j, i);
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

	private boolean method_17921(PushbackInputStream pushbackInputStream) throws IOException {
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
		for (PersistentState persistentState : this.keyToState.values()) {
			persistentState.method_17919(this.method_17922(persistentState.getId()));
		}
	}
}
