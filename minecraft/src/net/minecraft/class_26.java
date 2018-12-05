package net.minecraft;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.PersistedState;
import net.minecraft.util.TagHelper;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_26 {
	private static final Logger field_136 = LogManager.getLogger();
	private final DimensionType field_135;
	private final Map<String, PersistedState> field_134 = Maps.<String, PersistedState>newHashMap();
	private final Object2IntMap<String> field_137 = new Object2IntOpenHashMap<>();
	@Nullable
	private final WorldSaveHandler field_138;

	public class_26(DimensionType dimensionType, @Nullable WorldSaveHandler worldSaveHandler) {
		this.field_135 = dimensionType;
		this.field_138 = worldSaveHandler;
		this.field_137.defaultReturnValue(-1);
	}

	@Nullable
	public <T extends PersistedState> T method_120(Function<String, T> function, String string) {
		PersistedState persistedState = (PersistedState)this.field_134.get(string);
		if (persistedState == null && this.field_138 != null) {
			try {
				File file = this.field_138.getDataFile(this.field_135, string);
				if (file != null && file.exists()) {
					persistedState = (PersistedState)function.apply(string);
					persistedState.deserialize(method_119(this.field_138, this.field_135, string, SharedConstants.getGameVersion().getWorldVersion()).getCompound("data"));
					this.field_134.put(string, persistedState);
				}
			} catch (Exception var5) {
				field_136.error("Error loading saved data: {}", string, var5);
			}
		}

		return (T)persistedState;
	}

	public void method_123(String string, PersistedState persistedState) {
		this.field_134.put(string, persistedState);
	}

	public void method_122() {
		try {
			this.field_137.clear();
			if (this.field_138 == null) {
				return;
			}

			File file = this.field_138.getDataFile(this.field_135, "idcounts");
			if (file != null && file.exists()) {
				DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
				CompoundTag compoundTag = NbtIo.read(dataInputStream);
				dataInputStream.close();

				for (String string : compoundTag.getKeys()) {
					if (compoundTag.containsKey(string, 99)) {
						this.field_137.put(string, compoundTag.getInt(string));
					}
				}
			}
		} catch (Exception var6) {
			field_136.error("Could not load aux values", (Throwable)var6);
		}
	}

	public int method_124(String string) {
		int i = this.field_137.getInt(string) + 1;
		this.field_137.put(string, i);
		if (this.field_138 == null) {
			return i;
		} else {
			try {
				File file = this.field_138.getDataFile(this.field_135, "idcounts");
				if (file != null) {
					CompoundTag compoundTag = new CompoundTag();

					for (Entry<String> entry : this.field_137.object2IntEntrySet()) {
						compoundTag.putInt((String)entry.getKey(), entry.getIntValue());
					}

					DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
					NbtIo.write(compoundTag, dataOutputStream);
					dataOutputStream.close();
				}
			} catch (Exception var7) {
				field_136.error("Could not get free aux value {}", string, var7);
			}

			return i;
		}
	}

	public static CompoundTag method_119(WorldSaveHandler worldSaveHandler, DimensionType dimensionType, String string, int i) throws IOException {
		File file = worldSaveHandler.getDataFile(dimensionType, string);
		FileInputStream fileInputStream = new FileInputStream(file);
		Throwable var6 = null;

		CompoundTag var9;
		try {
			CompoundTag compoundTag = NbtIo.readCompressed(fileInputStream);
			int j = compoundTag.containsKey("DataVersion", 99) ? compoundTag.getInt("DataVersion") : 1343;
			var9 = TagHelper.update(worldSaveHandler.method_130(), DataFixTypes.SAVED_DATA, compoundTag, j, i);
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

	public void method_125() {
		if (this.field_138 != null) {
			for (PersistedState persistedState : this.field_134.values()) {
				if (persistedState.getDirty()) {
					this.method_121(persistedState);
					persistedState.setDirty(false);
				}
			}
		}
	}

	private void method_121(PersistedState persistedState) {
		if (this.field_138 != null) {
			try {
				File file = this.field_138.getDataFile(this.field_135, persistedState.getKey());
				if (file != null) {
					CompoundTag compoundTag = new CompoundTag();
					compoundTag.put("data", persistedState.serialize(new CompoundTag()));
					compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					NbtIo.writeCompressed(compoundTag, fileOutputStream);
					fileOutputStream.close();
				}
			} catch (Exception var5) {
				field_136.error("Could not save data {}", persistedState, var5);
			}
		}
	}
}
