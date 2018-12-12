package net.minecraft.world.dimension;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.TagHelper;
import net.minecraft.world.PersistentState;
import net.minecraft.world.WorldSaveHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionalPersistentStateManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final DimensionType type;
	private final Map<String, PersistentState> keyToState = Maps.<String, PersistentState>newHashMap();
	private final Object2IntMap<String> keyToAvailableId = new Object2IntOpenHashMap<>();
	@Nullable
	private final WorldSaveHandler saveHandler;

	public DimensionalPersistentStateManager(DimensionType dimensionType, @Nullable WorldSaveHandler worldSaveHandler) {
		this.type = dimensionType;
		this.saveHandler = worldSaveHandler;
		this.keyToAvailableId.defaultReturnValue(-1);
	}

	@Nullable
	public <T extends PersistentState> T get(Function<String, T> function, String string) {
		PersistentState persistentState = (PersistentState)this.keyToState.get(string);
		if (persistentState == null && this.saveHandler != null) {
			try {
				File file = this.saveHandler.getDataFile(this.type, string);
				if (file != null && file.exists()) {
					persistentState = (PersistentState)function.apply(string);
					persistentState.fromTag(update(this.saveHandler, this.type, string, SharedConstants.getGameVersion().getWorldVersion()).getCompound("data"));
					this.keyToState.put(string, persistentState);
				}
			} catch (Exception var5) {
				LOGGER.error("Error loading saved data: {}", string, var5);
			}
		}

		return (T)persistentState;
	}

	public void set(String string, PersistentState persistentState) {
		this.keyToState.put(string, persistentState);
	}

	public void readIdCounts() {
		try {
			this.keyToAvailableId.clear();
			if (this.saveHandler == null) {
				return;
			}

			File file = this.saveHandler.getDataFile(this.type, "idcounts");
			if (file != null && file.exists()) {
				DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
				CompoundTag compoundTag = NbtIo.read(dataInputStream);
				dataInputStream.close();

				for (String string : compoundTag.getKeys()) {
					if (compoundTag.containsKey(string, 99)) {
						this.keyToAvailableId.put(string, compoundTag.getInt(string));
					}
				}
			}
		} catch (Exception var6) {
			LOGGER.error("Could not load aux values", (Throwable)var6);
		}
	}

	public int getNextAvailableId(String string) {
		int i = this.keyToAvailableId.getInt(string) + 1;
		this.keyToAvailableId.put(string, i);
		if (this.saveHandler == null) {
			return i;
		} else {
			try {
				File file = this.saveHandler.getDataFile(this.type, "idcounts");
				if (file != null) {
					CompoundTag compoundTag = new CompoundTag();

					for (Entry<String> entry : this.keyToAvailableId.object2IntEntrySet()) {
						compoundTag.putInt((String)entry.getKey(), entry.getIntValue());
					}

					DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
					NbtIo.write(compoundTag, dataOutputStream);
					dataOutputStream.close();
				}
			} catch (Exception var7) {
				LOGGER.error("Could not get free aux value {}", string, var7);
			}

			return i;
		}
	}

	public static CompoundTag update(WorldSaveHandler worldSaveHandler, DimensionType dimensionType, String string, int i) throws IOException {
		File file = worldSaveHandler.getDataFile(dimensionType, string);
		FileInputStream fileInputStream = new FileInputStream(file);
		Throwable var6 = null;

		CompoundTag var9;
		try {
			CompoundTag compoundTag = NbtIo.readCompressed(fileInputStream);
			int j = compoundTag.containsKey("DataVersion", 99) ? compoundTag.getInt("DataVersion") : 1343;
			var9 = TagHelper.update(worldSaveHandler.getDataFixer(), DataFixTypes.SAVED_DATA, compoundTag, j, i);
		} catch (Throwable var18) {
			var6 = var18;
			throw var18;
		} finally {
			if (fileInputStream != null) {
				if (var6 != null) {
					try {
						fileInputStream.close();
					} catch (Throwable var17) {
						var6.addSuppressed(var17);
					}
				} else {
					fileInputStream.close();
				}
			}
		}

		return var9;
	}

	public void save() {
		if (this.saveHandler != null) {
			for (PersistentState persistentState : this.keyToState.values()) {
				if (persistentState.isDirty()) {
					this.save(persistentState);
					persistentState.setDirty(false);
				}
			}
		}
	}

	private void save(PersistentState persistentState) {
		if (this.saveHandler != null) {
			try {
				File file = this.saveHandler.getDataFile(this.type, persistentState.getId());
				if (file != null) {
					CompoundTag compoundTag = new CompoundTag();
					compoundTag.put("data", persistentState.toTag(new CompoundTag()));
					compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					NbtIo.writeCompressed(compoundTag, fileOutputStream);
					fileOutputStream.close();
				}
			} catch (Exception var5) {
				LOGGER.error("Could not save data {}", persistentState, var5);
			}
		}
	}
}
