package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
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
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5315;
import net.minecraft.class_5359;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionTracker;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
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
	private static final ImmutableList<String> field_25020 = ImmutableList.of(
		"RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest"
	);
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

	private static Pair<GeneratorOptions, Lifecycle> method_29010(Dynamic<?> dynamic, DataFixer dataFixer, int i) {
		Dynamic<?> dynamic2 = dynamic.get("WorldGenSettings").orElseEmptyMap();

		for (String string : field_25020) {
			Optional<? extends Dynamic<?>> optional = dynamic.get(string).result();
			if (optional.isPresent()) {
				dynamic2 = dynamic2.set(string, (Dynamic<?>)optional.get());
			}
		}

		Dynamic<?> dynamic3 = dataFixer.update(TypeReferences.CHUNK_GENERATOR_SETTINGS, dynamic2, i, SharedConstants.getGameVersion().getWorldVersion());
		DataResult<GeneratorOptions> dataResult = GeneratorOptions.CODEC.parse(dynamic3);
		return Pair.of(
			(GeneratorOptions)dataResult.resultOrPartial(Util.method_29188("WorldGenSettings: ", LOGGER::error)).orElseGet(GeneratorOptions::getDefaultOptions),
			dataResult.lifecycle()
		);
	}

	private static class_5359 method_29580(Dynamic<?> dynamic) {
		return (class_5359)class_5359.field_25394.parse(dynamic).resultOrPartial(LOGGER::error).orElse(class_5359.field_25393);
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
					boolean bl;
					try {
						bl = SessionLock.isLocked(file.toPath());
					} catch (Exception var9) {
						LOGGER.warn("Failed to read {} lock", file, var9);
						continue;
					}

					LevelSummary levelSummary = this.readLevelProperties(file, this.method_29014(file, bl));
					if (levelSummary != null) {
						list.add(levelSummary);
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
	private <T> T readLevelProperties(File file, BiFunction<File, DataFixer, T> biFunction) {
		if (!file.exists()) {
			return null;
		} else {
			File file2 = new File(file, "level.dat");
			if (file2.exists()) {
				T object = (T)biFunction.apply(file2, this.dataFixer);
				if (object != null) {
					return object;
				}
			}

			file2 = new File(file, "level.dat_old");
			return (T)(file2.exists() ? biFunction.apply(file2, this.dataFixer) : null);
		}
	}

	@Nullable
	private static class_5359 method_29583(File file, DataFixer dataFixer) {
		try {
			CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file));
			CompoundTag compoundTag2 = compoundTag.getCompound("Data");
			compoundTag2.remove("Player");
			int i = compoundTag2.contains("DataVersion", 99) ? compoundTag2.getInt("DataVersion") : -1;
			Dynamic<Tag> dynamic = dataFixer.update(
				DataFixTypes.LEVEL.getTypeReference(), new Dynamic<>(NbtOps.INSTANCE, compoundTag2), i, SharedConstants.getGameVersion().getWorldVersion()
			);
			return (class_5359)dynamic.get("DataPacks").result().map(LevelStorage::method_29580).orElse(class_5359.field_25393);
		} catch (Exception var6) {
			LOGGER.error("Exception reading {}", file, var6);
			return null;
		}
	}

	private static BiFunction<File, DataFixer, LevelProperties> readLevelProperties(DynamicOps<Tag> dynamicOps, class_5359 arg) {
		return (file, dataFixer) -> {
			try {
				CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file));
				CompoundTag compoundTag2 = compoundTag.getCompound("Data");
				CompoundTag compoundTag3 = compoundTag2.contains("Player", 10) ? compoundTag2.getCompound("Player") : null;
				compoundTag2.remove("Player");
				int i = compoundTag2.contains("DataVersion", 99) ? compoundTag2.getInt("DataVersion") : -1;
				Dynamic<Tag> dynamic = dataFixer.update(
					DataFixTypes.LEVEL.getTypeReference(), new Dynamic<>(dynamicOps, compoundTag2), i, SharedConstants.getGameVersion().getWorldVersion()
				);
				Pair<GeneratorOptions, Lifecycle> pair = method_29010(dynamic, dataFixer, i);
				class_5315 lv = class_5315.method_29023(dynamic);
				LevelInfo levelInfo = LevelInfo.method_28383(dynamic, arg);
				return LevelProperties.method_29029(dynamic, dataFixer, i, compoundTag3, levelInfo, lv, pair.getFirst(), pair.getSecond());
			} catch (Exception var12) {
				LOGGER.error("Exception reading {}", file, var12);
				return null;
			}
		};
	}

	private BiFunction<File, DataFixer, LevelSummary> method_29014(File file, boolean bl) {
		return (file2, dataFixer) -> {
			try {
				CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file2));
				CompoundTag compoundTag2 = compoundTag.getCompound("Data");
				compoundTag2.remove("Player");
				int i = compoundTag2.contains("DataVersion", 99) ? compoundTag2.getInt("DataVersion") : -1;
				Dynamic<Tag> dynamic = dataFixer.update(
					DataFixTypes.LEVEL.getTypeReference(), new Dynamic<>(NbtOps.INSTANCE, compoundTag2), i, SharedConstants.getGameVersion().getWorldVersion()
				);
				class_5315 lv = class_5315.method_29023(dynamic);
				int j = lv.method_29022();
				if (j != 19132 && j != 19133) {
					return null;
				} else {
					boolean bl2 = j != this.getCurrentVersion();
					File file3 = new File(file, "icon.png");
					class_5359 lv2 = (class_5359)dynamic.get("DataPacks").result().map(LevelStorage::method_29580).orElse(class_5359.field_25393);
					LevelInfo levelInfo = LevelInfo.method_28383(dynamic, lv2);
					return new LevelSummary(levelInfo, lv, file.getName(), bl2, bl, file3);
				}
			} catch (Exception var15) {
				LOGGER.error("Exception reading {}", file2, var15);
				return null;
			}
		};
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
		private final Map<WorldSavePath, Path> field_24190 = Maps.<WorldSavePath, Path>newHashMap();

		public Session(String string) throws IOException {
			this.directoryName = string;
			this.directory = LevelStorage.this.savesDirectory.resolve(string);
			this.lock = SessionLock.create(this.directory);
		}

		public String getDirectoryName() {
			return this.directoryName;
		}

		public Path getDirectory(WorldSavePath worldSavePath) {
			return (Path)this.field_24190.computeIfAbsent(worldSavePath, worldSavePathx -> this.directory.resolve(worldSavePathx.getRelativePath()));
		}

		public File method_27424(RegistryKey<World> registryKey) {
			return DimensionType.getSaveDirectory(registryKey, this.directory.toFile());
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
			LevelSummary levelSummary = this.method_29584();
			return levelSummary != null && levelSummary.method_29586().method_29022() != LevelStorage.this.getCurrentVersion();
		}

		public boolean convert(ProgressListener progressListener) {
			this.checkValid();
			return AnvilLevelStorage.convertLevel(this, progressListener);
		}

		@Nullable
		public LevelSummary method_29584() {
			this.checkValid();
			return LevelStorage.this.readLevelProperties(this.directory.toFile(), LevelStorage.this.method_29014(this.directory.toFile(), false));
		}

		@Nullable
		public SaveProperties readLevelProperties(DynamicOps<Tag> dynamicOps, class_5359 arg) {
			this.checkValid();
			return LevelStorage.this.readLevelProperties(this.directory.toFile(), LevelStorage.readLevelProperties(dynamicOps, arg));
		}

		@Nullable
		public class_5359 method_29585() {
			this.checkValid();
			return LevelStorage.this.readLevelProperties(this.directory.toFile(), (file, dataFixer) -> LevelStorage.method_29583(file, dataFixer));
		}

		public void method_27425(DimensionTracker dimensionTracker, SaveProperties saveProperties) {
			this.method_27426(dimensionTracker, saveProperties, null);
		}

		public void method_27426(DimensionTracker dimensionTracker, SaveProperties saveProperties, @Nullable CompoundTag compoundTag) {
			File file = this.directory.toFile();
			CompoundTag compoundTag2 = saveProperties.cloneWorldTag(dimensionTracker, compoundTag);
			CompoundTag compoundTag3 = new CompoundTag();
			compoundTag3.put("Data", compoundTag2);

			try {
				File file2 = File.createTempFile("level", ".dat", file);
				NbtIo.writeCompressed(compoundTag3, new FileOutputStream(file2));
				File file3 = new File(file, "level.dat_old");
				File file4 = new File(file, "level.dat");
				Util.method_27760(file4, file2, file3);
			} catch (Exception var10) {
				LevelStorage.LOGGER.error("Failed to save level {}", file, var10);
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
