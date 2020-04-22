package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5218;
import net.minecraft.class_5219;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Util;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
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

	public static LevelStorage create(Path path) {
		return new LevelStorage(path, path.resolve("../backups"), Schemas.getFixer());
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

					boolean bl;
					try {
						bl = SessionLock.isLocked(file.toPath());
					} catch (Exception var15) {
						LOGGER.warn("Failed to read {} lock", file, var15);
						continue;
					}

					class_5219 lv = this.readLevelProperties(file);
					if (lv != null && (lv.getVersion() == 19132 || lv.getVersion() == 19133)) {
						boolean bl2 = lv.getVersion() != this.getCurrentVersion();
						String string2 = lv.getLevelName();
						if (StringUtils.isEmpty(string2)) {
							string2 = string;
						}

						long l = 0L;
						File file2 = new File(file, "icon.png");
						list.add(new LevelSummary(lv, string, string2, 0L, bl2, bl, file2));
					}
				}
			}

			return list;
		}
	}

	private int getCurrentVersion() {
		return 19133;
	}

	@Nullable
	private class_5219 readLevelProperties(File file) {
		if (!file.exists()) {
			return null;
		} else {
			File file2 = new File(file, "level.dat");
			if (file2.exists()) {
				class_5219 lv = readLevelProperties(file2, this.dataFixer);
				if (lv != null) {
					return lv;
				}
			}

			file2 = new File(file, "level.dat_old");
			return file2.exists() ? readLevelProperties(file2, this.dataFixer) : null;
		}
	}

	@Nullable
	public static class_5219 readLevelProperties(File file, DataFixer dataFixer) {
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
	public boolean isLevelNameValid(String name) {
		try {
			Path path = this.savesDirectory.resolve(name);
			Files.createDirectory(path);
			Files.deleteIfExists(path);
			return true;
		} catch (IOException var3) {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean levelExists(String name) {
		return Files.isDirectory(this.savesDirectory.resolve(name), new LinkOption[0]);
	}

	@Environment(EnvType.CLIENT)
	public Path getSavesDirectory() {
		return this.savesDirectory;
	}

	@Environment(EnvType.CLIENT)
	public Path getBackupsDirectory() {
		return this.backupsDirectory;
	}

	public LevelStorage.Session createSession(String directoryName) throws IOException {
		return new LevelStorage.Session(directoryName);
	}

	public class Session implements AutoCloseable {
		private final SessionLock lock;
		private final Path directory;
		private final String directoryName;
		private final Map<class_5218, Path> field_24190 = Maps.<class_5218, Path>newHashMap();

		public Session(String string) throws IOException {
			this.directoryName = string;
			this.directory = LevelStorage.this.savesDirectory.resolve(string);
			this.lock = SessionLock.create(this.directory);
		}

		public String getDirectoryName() {
			return this.directoryName;
		}

		public Path getDirectory(class_5218 arg) {
			return (Path)this.field_24190.computeIfAbsent(arg, argx -> this.directory.resolve(argx.method_27423()));
		}

		public File method_27424(DimensionType dimensionType) {
			return dimensionType.getSaveDirectory(this.directory.toFile());
		}

		private void checkValid() {
			if (!this.lock.isValid()) {
				throw new IllegalStateException("Lock is no longer valid");
			}
		}

		public WorldSaveHandler method_27427() {
			this.checkValid();
			return new WorldSaveHandler(this, LevelStorage.this.dataFixer);
		}

		public boolean needsConversion() {
			class_5219 lv = this.readLevelProperties();
			return lv != null && lv.getVersion() != LevelStorage.this.getCurrentVersion();
		}

		public boolean convert(ProgressListener progressListener) {
			this.checkValid();
			return AnvilLevelStorage.convertLevel(this, progressListener);
		}

		@Nullable
		public class_5219 readLevelProperties() {
			this.checkValid();
			return LevelStorage.this.readLevelProperties(this.directory.toFile());
		}

		public void method_27425(class_5219 arg) {
			this.method_27426(arg, null);
		}

		public void method_27426(class_5219 arg, @Nullable CompoundTag compoundTag) {
			File file = this.directory.toFile();
			CompoundTag compoundTag2 = arg.cloneWorldTag(compoundTag);
			CompoundTag compoundTag3 = new CompoundTag();
			compoundTag3.put("Data", compoundTag2);

			try {
				File file2 = File.createTempFile("level", ".dat", file);
				NbtIo.writeCompressed(compoundTag3, new FileOutputStream(file2));
				File file3 = new File(file, "level.dat_old");
				File file4 = new File(file, "level.dat");
				Util.method_27760(file4, file2, file3);
			} catch (Exception var9) {
				LevelStorage.LOGGER.error("Failed to save level {}", file, var9);
			}
		}

		public File getIconFile() {
			this.checkValid();
			return this.directory.resolve("icon.png").toFile();
		}

		@Environment(EnvType.CLIENT)
		public void deleteSessionLock() throws IOException {
			this.checkValid();
			final Path path = this.directory.resolve("session.lock");

			for (int i = 1; i <= 5; i++) {
				LevelStorage.LOGGER.info("Attempt {}...", i);

				try {
					Files.walkFileTree(this.directory, new SimpleFileVisitor<Path>() {
						public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
							if (!path.equals(path)) {
								LevelStorage.LOGGER.debug("Deleting {}", path);
								Files.delete(path);
							}

							return FileVisitResult.CONTINUE;
						}

						public FileVisitResult postVisitDirectory(Path path, IOException iOException) throws IOException {
							if (iOException != null) {
								throw iOException;
							} else {
								if (path.equals(Session.this.directory)) {
									Session.this.lock.close();
									Files.deleteIfExists(path);
								}

								Files.delete(path);
								return FileVisitResult.CONTINUE;
							}
						}
					});
					break;
				} catch (IOException var6) {
					if (i >= 5) {
						throw var6;
					}

					LevelStorage.LOGGER.warn("Failed to delete {}", this.directory, var6);

					try {
						Thread.sleep(500L);
					} catch (InterruptedException var5) {
					}
				}
			}
		}

		@Environment(EnvType.CLIENT)
		public void save(String name) throws IOException {
			this.checkValid();
			File file = new File(LevelStorage.this.savesDirectory.toFile(), this.directoryName);
			if (file.exists()) {
				File file2 = new File(file, "level.dat");
				if (file2.exists()) {
					CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file2));
					CompoundTag compoundTag2 = compoundTag.getCompound("Data");
					compoundTag2.putString("LevelName", name);
					NbtIo.writeCompressed(compoundTag, new FileOutputStream(file2));
				}
			}
		}

		@Environment(EnvType.CLIENT)
		public long createBackup() throws IOException {
			this.checkValid();
			String string = LocalDateTime.now().format(LevelStorage.TIME_FORMATTER) + "_" + this.directoryName;
			Path path = LevelStorage.this.getBackupsDirectory();

			try {
				Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
			} catch (IOException var16) {
				throw new RuntimeException(var16);
			}

			Path path2 = path.resolve(FileNameUtil.getNextUniqueName(path, string, ".zip"));
			final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path2)));
			Throwable var5 = null;

			try {
				final Path path3 = Paths.get(this.directoryName);
				Files.walkFileTree(this.directory, new SimpleFileVisitor<Path>() {
					public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
						if (path.endsWith("session.lock")) {
							return FileVisitResult.CONTINUE;
						} else {
							String string = path3.resolve(Session.this.directory.relativize(path)).toString().replace('\\', '/');
							ZipEntry zipEntry = new ZipEntry(string);
							zipOutputStream.putNextEntry(zipEntry);
							com.google.common.io.Files.asByteSource(path.toFile()).copyTo(zipOutputStream);
							zipOutputStream.closeEntry();
							return FileVisitResult.CONTINUE;
						}
					}
				});
			} catch (Throwable var15) {
				var5 = var15;
				throw var15;
			} finally {
				if (zipOutputStream != null) {
					if (var5 != null) {
						try {
							zipOutputStream.close();
						} catch (Throwable var14) {
							var5.addSuppressed(var14);
						}
					} else {
						zipOutputStream.close();
					}
				}
			}

			return Files.size(path2);
		}

		public void close() throws IOException {
			this.lock.close();
		}
	}
}
