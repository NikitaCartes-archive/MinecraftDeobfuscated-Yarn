package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.util.DateTimeFormatters;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;

public class PlayerSaveHandler {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final File playerDataDir;
	protected final DataFixer dataFixer;
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatters.create();

	public PlayerSaveHandler(LevelStorage.Session session, DataFixer dataFixer) {
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

	private void backupCorruptedPlayerData(PlayerEntity player, String extension) {
		Path path = this.playerDataDir.toPath();
		Path path2 = path.resolve(player.getUuidAsString() + extension);
		Path path3 = path.resolve(player.getUuidAsString() + "_corrupted_" + LocalDateTime.now().format(DATE_TIME_FORMATTER) + extension);
		if (Files.isRegularFile(path2, new LinkOption[0])) {
			try {
				Files.copy(path2, path3, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			} catch (Exception var7) {
				LOGGER.warn("Failed to copy the player.dat file for {}", player.getName().getString(), var7);
			}
		}
	}

	private Optional<NbtCompound> loadPlayerData(PlayerEntity player, String extension) {
		File file = new File(this.playerDataDir, player.getUuidAsString() + extension);
		if (file.exists() && file.isFile()) {
			try {
				return Optional.of(NbtIo.readCompressed(file.toPath(), NbtSizeTracker.ofUnlimitedBytes()));
			} catch (Exception var5) {
				LOGGER.warn("Failed to load player data for {}", player.getName().getString());
			}
		}

		return Optional.empty();
	}

	public Optional<NbtCompound> loadPlayerData(PlayerEntity player) {
		Optional<NbtCompound> optional = this.loadPlayerData(player, ".dat");
		if (optional.isEmpty()) {
			this.backupCorruptedPlayerData(player, ".dat");
		}

		return optional.or(() -> this.loadPlayerData(player, ".dat_old")).map(nbt -> {
			int i = NbtHelper.getDataVersion(nbt, -1);
			nbt = DataFixTypes.PLAYER.update(this.dataFixer, nbt, i);
			player.readNbt(nbt);
			return nbt;
		});
	}
}
