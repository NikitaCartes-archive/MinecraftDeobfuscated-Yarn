package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldSaveHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final File playerDataDir;
	protected final DataFixer dataFixer;

	public WorldSaveHandler(LevelStorage.Session session, DataFixer dataFixer) {
		this.dataFixer = dataFixer;
		this.playerDataDir = session.getDirectory(WorldSavePath.PLAYERDATA).toFile();
		this.playerDataDir.mkdirs();
	}

	public void savePlayerData(PlayerEntity player) {
		try {
			NbtCompound nbtCompound = player.writeNbt(new NbtCompound());
			File file = File.createTempFile(player.getUuidAsString() + "-", ".dat", this.playerDataDir);
			NbtIo.writeCompressed(nbtCompound, file);
			File file2 = new File(this.playerDataDir, player.getUuidAsString() + ".dat");
			File file3 = new File(this.playerDataDir, player.getUuidAsString() + ".dat_old");
			Util.backupAndReplace(file2, file, file3);
		} catch (Exception var6) {
			LOGGER.warn("Failed to save player data for {}", player.getName().getString());
		}
	}

	@Nullable
	public NbtCompound loadPlayerData(PlayerEntity player) {
		NbtCompound nbtCompound = null;

		try {
			File file = new File(this.playerDataDir, player.getUuidAsString() + ".dat");
			if (file.exists() && file.isFile()) {
				nbtCompound = NbtIo.readCompressed(file);
			}
		} catch (Exception var4) {
			LOGGER.warn("Failed to load player data for {}", player.getName().getString());
		}

		if (nbtCompound != null) {
			int i = nbtCompound.contains("DataVersion", 3) ? nbtCompound.getInt("DataVersion") : -1;
			player.readNbt(NbtHelper.update(this.dataFixer, DataFixTypes.PLAYER, nbtCompound, i));
		}

		return nbtCompound;
	}

	public String[] getSavedPlayerIds() {
		String[] strings = this.playerDataDir.list();
		if (strings == null) {
			strings = new String[0];
		}

		for (int i = 0; i < strings.length; i++) {
			if (strings[i].endsWith(".dat")) {
				strings[i] = strings[i].substring(0, strings[i].length() - 4);
			}
		}

		return strings;
	}
}
