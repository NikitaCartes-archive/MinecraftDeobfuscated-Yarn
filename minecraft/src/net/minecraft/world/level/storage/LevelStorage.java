package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelStorage {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
		.appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
		.appendLiteral('-')
		.appendValue(ChronoField.MONTH_OF_YEAR, 2)
		.appendLiteral('-')
		.appendValue(ChronoField.DAY_OF_MONTH, 2)
		.appendLiteral('_')
		.appendValue(ChronoField.HOUR_OF_DAY, 2)
		.appendLiteral('-')
		.appendValue(ChronoField.MINUTE_OF_HOUR, 2)
		.appendLiteral('-')
		.appendValue(ChronoField.SECOND_OF_MINUTE, 2)
		.toFormatter();
	private final Path savesDirectory;
	private final Path backupsDirectory;
	private final DataFixer dataFixer;

	public LevelStorage(Path savesDirectory, Path backupsDirectory, DataFixer dataFixer) {
		this.dataFixer = dataFixer;

		try {
			Files.createDirectories(Files.exists(savesDirectory, new LinkOption[0]) ? savesDirectory.toRealPath() : savesDirectory);
		} catch (IOException var5) {
			throw new RuntimeException(var5);
		}

		this.savesDirectory = savesDirectory;
		this.backupsDirectory = backupsDirectory;
	}

	@Environment(EnvType.CLIENT)
	public List<LevelSummary> getLevelList() throws LevelStorageException {
		if (!Files.isDirectory(this.savesDirectory, new LinkOption[0])) {
			throw new LevelStorageException(new TranslatableText("selectWorld.load_folder_access").getString());
		} else {
			List<LevelSummary> list = Lists.<LevelSummary>newArrayList();
			File[] files = this.savesDirectory.toFile().listFiles();

			for (File file : files) {
				if (file.isDirectory()) {
					String string = file.getName();
					LevelProperties levelProperties = this.getLevelProperties(string);
					if (levelProperties != null && (levelProperties.getVersion() == 19132 || levelProperties.getVersion() == 19133)) {
						boolean bl = levelProperties.getVersion() != this.getCurrentVersion();
						String string2 = levelProperties.getLevelName();
						if (StringUtils.isEmpty(string2)) {
							string2 = string;
						}

						long l = 0L;
						list.add(new LevelSummary(levelProperties, string, string2, 0L, bl));
					}
				}
			}

			return list;
		}
	}

	private int getCurrentVersion() {
		return 19133;
	}

	public WorldSaveHandler createSaveHandler(String name, @Nullable MinecraftServer server) {
		return createSaveHandler(this.savesDirectory, this.dataFixer, name, server);
	}

	protected static WorldSaveHandler createSaveHandler(Path savesDirectory, DataFixer dataFixer, String name, @Nullable MinecraftServer server) {
		return new WorldSaveHandler(savesDirectory.toFile(), name, server, dataFixer);
	}

	public boolean requiresConversion(String name) {
		LevelProperties levelProperties = this.getLevelProperties(name);
		return levelProperties != null && levelProperties.getVersion() != this.getCurrentVersion();
	}

	public boolean convertLevel(String name, ProgressListener progressListener) {
		return AnvilLevelStorage.convertLevel(this.savesDirectory, this.dataFixer, name, progressListener);
	}

	@Nullable
	public LevelProperties getLevelProperties(String string) {
		return getLevelProperties(this.savesDirectory, this.dataFixer, string);
	}

	@Nullable
	protected static LevelProperties getLevelProperties(Path savesDirectory, DataFixer dataFixer, String name) {
		File file = new File(savesDirectory.toFile(), name);
		if (!file.exists()) {
			return null;
		} else {
			File file2 = new File(file, "special_level.dat");
			if (file2.exists()) {
				LevelProperties levelProperties = readLevelProperties(file2, dataFixer);
				if (levelProperties != null) {
					return levelProperties;
				}
			}

			file2 = new File(file, "special_level.dat_old");
			return file2.exists() ? readLevelProperties(file2, dataFixer) : null;
		}
	}

	@Nullable
	public static LevelProperties readLevelProperties(File file, DataFixer dataFixer) {
		try {
			CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file));
			CompoundTag compoundTag2 = compoundTag.getCompound("Data");
			CompoundTag compoundTag3 = compoundTag2.contains("Player", 10) ? compoundTag2.getCompound("Player") : null;
			compoundTag2.remove("Player");
			int i = compoundTag2.contains("DataVersion", 99) ? compoundTag2.getInt("DataVersion") : -1;
			return new LevelProperties(NbtHelper.update(dataFixer, DataFixTypes.LEVEL, compoundTag2, i), dataFixer, i, compoundTag3);
		} catch (Exception var6) {
			LOGGER.error("Exception reading {}", file, var6);
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	public void renameLevel(String name, String newName) {
		File file = new File(this.savesDirectory.toFile(), name);
		if (file.exists()) {
			File file2 = new File(file, "special_level.dat");
			if (file2.exists()) {
				try {
					CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file2));
					CompoundTag compoundTag2 = compoundTag.getCompound("Data");
					compoundTag2.putString("LevelName", newName);
					NbtIo.writeCompressed(compoundTag, new FileOutputStream(file2));
				} catch (Exception var7) {
					var7.printStackTrace();
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean isLevelNameValid(String string) {
		try {
			Path path = this.savesDirectory.resolve(string);
			Files.createDirectory(path);
			Files.deleteIfExists(path);
			return true;
		} catch (IOException var3) {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean deleteLevel(String name) {
		File file = new File(this.savesDirectory.toFile(), name);
		if (!file.exists()) {
			return true;
		} else {
			LOGGER.info("Deleting level {}", name);

			for (int i = 1; i <= 5; i++) {
				LOGGER.info("Attempt {}...", i);
				if (deleteFilesRecursively(file.listFiles())) {
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
	private static boolean deleteFilesRecursively(File[] files) {
		for (File file : files) {
			LOGGER.debug("Deleting {}", file);
			if (file.isDirectory() && !deleteFilesRecursively(file.listFiles())) {
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

	@Environment(EnvType.CLIENT)
	public boolean levelExists(String name) {
		return Files.isDirectory(this.savesDirectory.resolve(name), new LinkOption[0]);
	}

	@Environment(EnvType.CLIENT)
	public Path getSavesDirectory() {
		return this.savesDirectory;
	}

	public File resolveFile(String string, String string2) {
		return this.savesDirectory.resolve(string).resolve(string2).toFile();
	}

	@Environment(EnvType.CLIENT)
	private Path resolvePath(String string) {
		return this.savesDirectory.resolve(string);
	}

	@Environment(EnvType.CLIENT)
	public Path getBackupsDirectory() {
		return this.backupsDirectory;
	}

	@Environment(EnvType.CLIENT)
	public long backupLevel(String name) throws IOException {
		final Path path = this.resolvePath(name);
		String string = LocalDateTime.now().format(TIME_FORMATTER) + "_" + name;
		Path path2 = this.getBackupsDirectory();

		try {
			Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath() : path2);
		} catch (IOException var18) {
			throw new RuntimeException(var18);
		}

		Path path3 = path2.resolve(FileNameUtil.getNextUniqueName(path2, string, ".zip"));
		final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path3)));
		Throwable var7 = null;

		try {
			final Path path4 = Paths.get(name);
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
					String string = path4.resolve(path.relativize(path)).toString().replace('\\', '/');
					ZipEntry zipEntry = new ZipEntry(string);
					zipOutputStream.putNextEntry(zipEntry);
					com.google.common.io.Files.asByteSource(path.toFile()).copyTo(zipOutputStream);
					zipOutputStream.closeEntry();
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (Throwable var17) {
			var7 = var17;
			throw var17;
		} finally {
			if (zipOutputStream != null) {
				if (var7 != null) {
					try {
						zipOutputStream.close();
					} catch (Throwable var16) {
						var7.addSuppressed(var16);
					}
				} else {
					zipOutputStream.close();
				}
			}
		}

		return Files.size(path3);
	}
}
