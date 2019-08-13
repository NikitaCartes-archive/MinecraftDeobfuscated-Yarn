package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.TagHelper;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldSaveHandler implements PlayerSaveHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final File worldDir;
	private final File playerDataDir;
	private final long saveStartTime = SystemUtil.getMeasuringTimeMs();
	private final String worldName;
	private final StructureManager structureManager;
	protected final DataFixer dataFixer;

	public WorldSaveHandler(File file, String string, @Nullable MinecraftServer minecraftServer, DataFixer dataFixer) {
		this.dataFixer = dataFixer;
		this.worldDir = new File(file, string);
		this.worldDir.mkdirs();
		this.playerDataDir = new File(this.worldDir, "playerdata");
		this.worldName = string;
		if (minecraftServer != null) {
			this.playerDataDir.mkdirs();
			this.structureManager = new StructureManager(minecraftServer, this.worldDir, dataFixer);
		} else {
			this.structureManager = null;
		}

		this.writeSessionLock();
	}

	public void saveWorld(LevelProperties levelProperties, @Nullable CompoundTag compoundTag) {
		levelProperties.setVersion(19133);
		CompoundTag compoundTag2 = levelProperties.cloneWorldTag(compoundTag);
		CompoundTag compoundTag3 = new CompoundTag();
		compoundTag3.put("Data", compoundTag2);

		try {
			File file = new File(this.worldDir, "level.dat_new");
			File file2 = new File(this.worldDir, "level.dat_old");
			File file3 = new File(this.worldDir, "level.dat");
			NbtIo.writeCompressed(compoundTag3, new FileOutputStream(file));
			if (file2.exists()) {
				file2.delete();
			}

			file3.renameTo(file2);
			if (file3.exists()) {
				file3.delete();
			}

			file.renameTo(file3);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception var8) {
			var8.printStackTrace();
		}
	}

	private void writeSessionLock() {
		try {
			File file = new File(this.worldDir, "session.lock");
			DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));

			try {
				dataOutputStream.writeLong(this.saveStartTime);
			} finally {
				dataOutputStream.close();
			}
		} catch (IOException var7) {
			var7.printStackTrace();
			throw new RuntimeException("Failed to check session lock, aborting");
		}
	}

	public File getWorldDir() {
		return this.worldDir;
	}

	public void checkSessionLock() throws SessionLockException {
		try {
			File file = new File(this.worldDir, "session.lock");
			DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

			try {
				if (dataInputStream.readLong() != this.saveStartTime) {
					throw new SessionLockException("The save is being accessed from another location, aborting");
				}
			} finally {
				dataInputStream.close();
			}
		} catch (IOException var7) {
			throw new SessionLockException("Failed to check session lock, aborting");
		}
	}

	@Nullable
	public LevelProperties readProperties() {
		File file = new File(this.worldDir, "level.dat");
		if (file.exists()) {
			LevelProperties levelProperties = LevelStorage.readLevelProperties(file, this.dataFixer);
			if (levelProperties != null) {
				return levelProperties;
			}
		}

		file = new File(this.worldDir, "level.dat_old");
		return file.exists() ? LevelStorage.readLevelProperties(file, this.dataFixer) : null;
	}

	public void saveWorld(LevelProperties levelProperties) {
		this.saveWorld(levelProperties, null);
	}

	@Override
	public void savePlayerData(PlayerEntity playerEntity) {
		try {
			CompoundTag compoundTag = playerEntity.toTag(new CompoundTag());
			File file = new File(this.playerDataDir, playerEntity.getUuidAsString() + ".dat.tmp");
			File file2 = new File(this.playerDataDir, playerEntity.getUuidAsString() + ".dat");
			NbtIo.writeCompressed(compoundTag, new FileOutputStream(file));
			if (file2.exists()) {
				file2.delete();
			}

			file.renameTo(file2);
		} catch (Exception var5) {
			LOGGER.warn("Failed to save player data for {}", playerEntity.getName().getString());
		}
	}

	@Nullable
	@Override
	public CompoundTag loadPlayerData(PlayerEntity playerEntity) {
		CompoundTag compoundTag = null;

		try {
			File file = new File(this.playerDataDir, playerEntity.getUuidAsString() + ".dat");
			if (file.exists() && file.isFile()) {
				compoundTag = NbtIo.readCompressed(new FileInputStream(file));
			}
		} catch (Exception var4) {
			LOGGER.warn("Failed to load player data for {}", playerEntity.getName().getString());
		}

		if (compoundTag != null) {
			int i = compoundTag.containsKey("DataVersion", 3) ? compoundTag.getInt("DataVersion") : -1;
			playerEntity.fromTag(TagHelper.update(this.dataFixer, DataFixTypes.field_19213, compoundTag, i));
		}

		return compoundTag;
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

	public StructureManager getStructureManager() {
		return this.structureManager;
	}

	public DataFixer getDataFixer() {
		return this.dataFixer;
	}
}
