package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.TagHelper;
import net.minecraft.world.OldWorldSaveHandler;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldLevelStorage implements LevelStorage {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final Path baseDir;
	protected final Path field_141;
	protected final DataFixer field_143;

	public OldLevelStorage(Path path, Path path2, DataFixer dataFixer) {
		this.field_143 = dataFixer;

		try {
			Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
		} catch (IOException var5) {
			throw new RuntimeException(var5);
		}

		this.baseDir = path;
		this.field_141 = path2;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String getName() {
		return "Old Format";
	}

	@Environment(EnvType.CLIENT)
	@Override
	public List<LevelSummary> getAvailableLevels() throws LevelStorageException {
		List<LevelSummary> list = Lists.<LevelSummary>newArrayList();

		for (int i = 0; i < 5; i++) {
			String string = "World" + (i + 1);
			LevelProperties levelProperties = this.getLevelProperties(string);
			if (levelProperties != null) {
				list.add(new LevelSummary(levelProperties, string, "", levelProperties.getSizeOnDisk(), false));
			}
		}

		return list;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void clearAll() {
	}

	@Nullable
	@Override
	public LevelProperties getLevelProperties(String string) {
		File file = new File(this.baseDir.toFile(), string);
		if (!file.exists()) {
			return null;
		} else {
			File file2 = new File(file, "level.dat");
			if (file2.exists()) {
				LevelProperties levelProperties = method_126(file2, this.field_143);
				if (levelProperties != null) {
					return levelProperties;
				}
			}

			file2 = new File(file, "level.dat_old");
			return file2.exists() ? method_126(file2, this.field_143) : null;
		}
	}

	@Nullable
	public static LevelProperties method_126(File file, DataFixer dataFixer) {
		try {
			CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file));
			CompoundTag compoundTag2 = compoundTag.getCompound("Data");
			CompoundTag compoundTag3 = compoundTag2.containsKey("Player", 10) ? compoundTag2.getCompound("Player") : null;
			compoundTag2.remove("Player");
			int i = compoundTag2.containsKey("DataVersion", 99) ? compoundTag2.getInt("DataVersion") : -1;
			return new LevelProperties(TagHelper.update(dataFixer, DataFixTypes.LEVEL, compoundTag2, i), dataFixer, i, compoundTag3);
		} catch (Exception var6) {
			LOGGER.error("Exception reading {}", file, var6);
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void rename(String string, String string2) {
		File file = new File(this.baseDir.toFile(), string);
		if (file.exists()) {
			File file2 = new File(file, "level.dat");
			if (file2.exists()) {
				try {
					CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file2));
					CompoundTag compoundTag2 = compoundTag.getCompound("Data");
					compoundTag2.putString("LevelName", string2);
					NbtIo.writeCompressed(compoundTag, new FileOutputStream(file2));
				} catch (Exception var7) {
					var7.printStackTrace();
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean canCreate(String string) {
		File file = new File(this.baseDir.toFile(), string);
		if (file.exists()) {
			return false;
		} else {
			try {
				file.mkdir();
				file.delete();
				return true;
			} catch (Throwable var4) {
				LOGGER.warn("Couldn't make new level", var4);
				return false;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean delete(String string) {
		File file = new File(this.baseDir.toFile(), string);
		if (!file.exists()) {
			return true;
		} else {
			LOGGER.info("Deleting level {}", string);

			for (int i = 1; i <= 5; i++) {
				LOGGER.info("Attempt {}...", i);
				if (deleteRecursive(file.listFiles())) {
					break;
				}

				LOGGER.warn("Unsuccessful in deleting contents.");
				if (i < 5) {
					try {
						Thread.sleep(500L);
					} catch (InterruptedException var5) {
					}
				}
			}

			return file.delete();
		}
	}

	@Environment(EnvType.CLIENT)
	protected static boolean deleteRecursive(File[] files) {
		for (File file : files) {
			LOGGER.debug("Deleting {}", file);
			if (file.isDirectory() && !deleteRecursive(file.listFiles())) {
				LOGGER.warn("Couldn't delete directory {}", file);
				return false;
			}

			if (!file.delete()) {
				LOGGER.warn("Couldn't delete file {}", file);
				return false;
			}
		}

		return true;
	}

	@Override
	public WorldSaveHandler method_242(String string, @Nullable MinecraftServer minecraftServer) {
		return new OldWorldSaveHandler(this.baseDir.toFile(), string, minecraftServer, this.field_143);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isConvertible(String string) {
		return false;
	}

	@Override
	public boolean requiresConversion(String string) {
		return false;
	}

	@Override
	public boolean convertLevel(String string, ProgressListener progressListener) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean exists(String string) {
		return Files.isDirectory(this.baseDir.resolve(string), new LinkOption[0]);
	}

	@Override
	public File resolveFile(String string, String string2) {
		return this.baseDir.resolve(string).resolve(string2).toFile();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Path resolvePath(String string) {
		return this.baseDir.resolve(string);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Path method_236() {
		return this.field_141;
	}
}
