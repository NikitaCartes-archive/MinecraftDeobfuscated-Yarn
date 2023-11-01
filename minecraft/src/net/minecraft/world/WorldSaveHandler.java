package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;

public class WorldSaveHandler {
	private static final Logger LOGGER = LogUtils.getLogger();
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
			Path path = this.playerDataDir.toPath();
			Path path2 = Files.createTempFile(path, player.getUuidAsString() + "-", ".dat");
			NbtIo.writeCompressed(nbtCompound, path2);
			Path path3 = path.resolve(player.getUuidAsString() + ".dat");
			Path path4 = path.resolve(player.getUuidAsString() + ".dat_old");
			Util.backupAndReplace(path3, path2, path4);
		} catch (Exception var7) {
			LOGGER.warn("Failed to save player data for {}", player.getName().getString());
		}
	}

	@Nullable
	public NbtCompound loadPlayerData(PlayerEntity player) {
		NbtCompound nbtCompound = null;

		try {
			File file = new File(this.playerDataDir, player.getUuidAsString() + ".dat");
			if (file.exists() && file.isFile()) {
				nbtCompound = NbtIo.readCompressed(file.toPath(), NbtTagSizeTracker.ofUnlimitedBytes());
			}
		} catch (Exception var4) {
			LOGGER.warn("Failed to load player data for {}", player.getName().getString());
		}

		if (nbtCompound != null) {
			int i = NbtHelper.getDataVersion(nbtCompound, -1);
			player.readNbt(DataFixTypes.PLAYER.update(this.dataFixer, nbtCompound, i));
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
