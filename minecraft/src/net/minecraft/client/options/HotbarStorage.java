package net.minecraft.client.options;

import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.TagHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class HotbarStorage {
	private static final Logger LOGGER = LogManager.getLogger();
	private final File file;
	private final DataFixer dataFixer;
	private final HotbarStorageEntry[] field_1644 = new HotbarStorageEntry[9];
	private boolean loaded;

	public HotbarStorage(File file, DataFixer dataFixer) {
		this.file = new File(file, "hotbar.nbt");
		this.dataFixer = dataFixer;

		for (int i = 0; i < 9; i++) {
			this.field_1644[i] = new HotbarStorageEntry();
		}
	}

	private void load() {
		try {
			CompoundTag compoundTag = NbtIo.read(this.file);
			if (compoundTag == null) {
				return;
			}

			if (!compoundTag.containsKey("DataVersion", 99)) {
				compoundTag.putInt("DataVersion", 1343);
			}

			compoundTag = TagHelper.update(this.dataFixer, DataFixTypes.HOTBAR, compoundTag, compoundTag.getInt("DataVersion"));

			for (int i = 0; i < 9; i++) {
				this.field_1644[i].method_3152(compoundTag.method_10554(String.valueOf(i), 10));
			}
		} catch (Exception var3) {
			LOGGER.error("Failed to load creative mode options", (Throwable)var3);
		}
	}

	public void save() {
		try {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());

			for (int i = 0; i < 9; i++) {
				compoundTag.method_10566(String.valueOf(i), this.method_1410(i).method_3153());
			}

			NbtIo.write(compoundTag, this.file);
		} catch (Exception var3) {
			LOGGER.error("Failed to save creative mode options", (Throwable)var3);
		}
	}

	public HotbarStorageEntry method_1410(int i) {
		if (!this.loaded) {
			this.load();
			this.loaded = true;
		}

		return this.field_1644[i];
	}
}
