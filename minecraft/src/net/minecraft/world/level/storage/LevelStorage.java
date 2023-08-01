package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.scanner.ExclusiveNbtCollector;
import net.minecraft.nbt.scanner.NbtScanQuery;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashMemoryReserve;
import net.minecraft.util.path.AllowedSymlinkPathMatcher;
import net.minecraft.util.path.SymlinkEntry;
import net.minecraft.util.path.SymlinkFinder;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.WorldGenSettings;
import org.slf4j.Logger;

public class LevelStorage {
	static final Logger LOGGER = LogUtils.getLogger();
	static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
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
	private static final ImmutableList<String> GENERATOR_OPTION_KEYS = ImmutableList.of(
		"RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest"
	);
	private static final String DATA_KEY = "Data";
	private static final PathMatcher DEFAULT_ALLOWED_SYMLINK_MATCHER = path -> false;
	public static final String ALLOWED_SYMLINKS_FILE_NAME = "allowed_symlinks.txt";
	private final Path savesDirectory;
	private final Path backupsDirectory;
	final DataFixer dataFixer;
	private final SymlinkFinder symlinkFinder;

	public LevelStorage(Path savesDirectory, Path backupsDirectory, SymlinkFinder symlinkFinder, DataFixer dataFixer) {
		this.dataFixer = dataFixer;

		try {
			PathUtil.createDirectories(savesDirectory);
		} catch (IOException var6) {
			throw new UncheckedIOException(var6);
		}

		this.savesDirectory = savesDirectory;
		this.backupsDirectory = backupsDirectory;
		this.symlinkFinder = symlinkFinder;
	}

	public static SymlinkFinder createSymlinkFinder(Path allowedSymlinksFile) {
		if (Files.exists(allowedSymlinksFile, new LinkOption[0])) {
			try {
				BufferedReader bufferedReader = Files.newBufferedReader(allowedSymlinksFile);

				SymlinkFinder var2;
				try {
					var2 = new SymlinkFinder(AllowedSymlinkPathMatcher.fromReader(bufferedReader));
				} catch (Throwable var5) {
					if (bufferedReader != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var4) {
							var5.addSuppressed(var4);
						}
					}

					throw var5;
				}

				if (bufferedReader != null) {
					bufferedReader.close();
				}

				return var2;
			} catch (Exception var6) {
				LOGGER.error("Failed to parse {}, disallowing all symbolic links", "allowed_symlinks.txt", var6);
			}
		}

		return new SymlinkFinder(DEFAULT_ALLOWED_SYMLINK_MATCHER);
	}

	public static LevelStorage create(Path path) {
		SymlinkFinder symlinkFinder = createSymlinkFinder(path.resolve("allowed_symlinks.txt"));
		return new LevelStorage(path, path.resolve("../backups"), symlinkFinder, Schemas.getFixer());
	}

	private static <T> DataResult<WorldGenSettings> readGeneratorProperties(Dynamic<T> levelData, DataFixer dataFixer, int version) {
		Dynamic<T> dynamic = levelData.get("WorldGenSettings").orElseEmptyMap();

		for (String string : GENERATOR_OPTION_KEYS) {
			Optional<Dynamic<T>> optional = levelData.get(string).result();
			if (optional.isPresent()) {
				dynamic = dynamic.set(string, (Dynamic<?>)optional.get());
			}
		}

		Dynamic<T> dynamic2 = DataFixTypes.WORLD_GEN_SETTINGS.update(dataFixer, dynamic, version);
		return WorldGenSettings.CODEC.parse(dynamic2);
	}

	private static DataConfiguration parseDataPackSettings(Dynamic<?> dynamic) {
		return (DataConfiguration)DataConfiguration.CODEC.parse(dynamic).resultOrPartial(LOGGER::error).orElse(DataConfiguration.SAFE_MODE);
	}

	public String getFormatName() {
		return "Anvil";
	}

	public LevelStorage.LevelList getLevelList() throws LevelStorageException {
		if (!Files.isDirectory(this.savesDirectory, new LinkOption[0])) {
			throw new LevelStorageException(Text.translatable("selectWorld.load_folder_access"));
		} else {
			try {
				Stream<Path> stream = Files.list(this.savesDirectory);

				LevelStorage.LevelList var3;
				try {
					List<LevelStorage.LevelSave> list = stream.filter(path -> Files.isDirectory(path, new LinkOption[0]))
						.map(LevelStorage.LevelSave::new)
						.filter(
							levelSave -> Files.isRegularFile(levelSave.getLevelDatPath(), new LinkOption[0])
									|| Files.isRegularFile(levelSave.getLevelDatOldPath(), new LinkOption[0])
						)
						.toList();
					var3 = new LevelStorage.LevelList(list);
				} catch (Throwable var5) {
					if (stream != null) {
						try {
							stream.close();
						} catch (Throwable var4) {
							var5.addSuppressed(var4);
						}
					}

					throw var5;
				}

				if (stream != null) {
					stream.close();
				}

				return var3;
			} catch (IOException var6) {
				throw new LevelStorageException(Text.translatable("selectWorld.load_folder_access"));
			}
		}
	}

	public CompletableFuture<List<LevelSummary>> loadSummaries(LevelStorage.LevelList levels) {
		List<CompletableFuture<LevelSummary>> list = new ArrayList(levels.levels.size());

		for (LevelStorage.LevelSave levelSave : levels.levels) {
			list.add(
				CompletableFuture.supplyAsync(
					() -> {
						boolean bl;
						try {
							bl = SessionLock.isLocked(levelSave.path());
						} catch (Exception var6) {
							LOGGER.warn("Failed to read {} lock", levelSave.path(), var6);
							return null;
						}

						try {
							LevelSummary levelSummary = this.readLevelProperties(levelSave, this.createLevelDataParser(levelSave, bl));
							return levelSummary != null ? levelSummary : null;
						} catch (OutOfMemoryError var4x) {
							CrashMemoryReserve.releaseMemory();
							System.gc();
							LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of memory trying to read summary of {}", levelSave.getRootPath());
							throw var4x;
						} catch (StackOverflowError var5) {
							LOGGER.error(
								LogUtils.FATAL_MARKER,
								"Ran out of stack trying to read summary of {}. Assuming corruption; attempting to restore from from level.dat_old.",
								levelSave.getRootPath()
							);
							Util.backupAndReplace(levelSave.getLevelDatPath(), levelSave.getLevelDatOldPath(), levelSave.getCorruptedLevelDatPath(LocalDateTime.now()), true);
							throw var5;
						}
					},
					Util.getMainWorkerExecutor()
				)
			);
		}

		return Util.combineCancellable(list).thenApply(summaries -> summaries.stream().filter(Objects::nonNull).sorted().toList());
	}

	private int getCurrentVersion() {
		return 19133;
	}

	@Nullable
	<T> T readLevelProperties(LevelStorage.LevelSave levelSave, BiFunction<Path, DataFixer, T> levelDataParser) {
		if (!Files.exists(levelSave.path(), new LinkOption[0])) {
			return null;
		} else {
			Path path = levelSave.getLevelDatPath();
			if (Files.exists(path, new LinkOption[0])) {
				T object = (T)levelDataParser.apply(path, this.dataFixer);
				if (object != null) {
					return object;
				}
			}

			path = levelSave.getLevelDatOldPath();
			return (T)(Files.exists(path, new LinkOption[0]) ? levelDataParser.apply(path, this.dataFixer) : null);
		}
	}

	@Nullable
	private static DataConfiguration readDataPackSettings(Path path, DataFixer dataFixer) {
		try {
			if (loadCompactLevelData(path) instanceof NbtCompound nbtCompound) {
				NbtCompound nbtCompound2 = nbtCompound.getCompound("Data");
				int i = NbtHelper.getDataVersion(nbtCompound2, -1);
				Dynamic<?> dynamic = DataFixTypes.LEVEL.update(dataFixer, new Dynamic<>(NbtOps.INSTANCE, nbtCompound2), i);
				return parseDataPackSettings(dynamic);
			}
		} catch (Exception var7) {
			LOGGER.error("Exception reading {}", path, var7);
		}

		return null;
	}

	static BiFunction<Path, DataFixer, Pair<SaveProperties, DimensionOptionsRegistryHolder.DimensionsConfig>> createLevelDataParser(
		DynamicOps<NbtElement> ops, DataConfiguration dataConfiguration, Registry<DimensionOptions> dimensionOptionsRegistry, Lifecycle lifecycle
	) {
		return (path, dataFixer) -> {
			NbtCompound nbtCompound;
			try {
				nbtCompound = NbtIo.readCompressed(path.toFile());
			} catch (IOException var17) {
				throw new UncheckedIOException(var17);
			}

			NbtCompound nbtCompound2 = nbtCompound.getCompound("Data");
			NbtCompound nbtCompound3 = nbtCompound2.contains("Player", NbtElement.COMPOUND_TYPE) ? nbtCompound2.getCompound("Player") : null;
			nbtCompound2.remove("Player");
			int i = NbtHelper.getDataVersion(nbtCompound2, -1);
			Dynamic<?> dynamic = DataFixTypes.LEVEL.update(dataFixer, new Dynamic<>(ops, nbtCompound2), i);
			WorldGenSettings worldGenSettings = readGeneratorProperties(dynamic, dataFixer, i).getOrThrow(false, Util.addPrefix("WorldGenSettings: ", LOGGER::error));
			SaveVersionInfo saveVersionInfo = SaveVersionInfo.fromDynamic(dynamic);
			LevelInfo levelInfo = LevelInfo.fromDynamic(dynamic, dataConfiguration);
			DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = worldGenSettings.dimensionOptionsRegistryHolder().toConfig(dimensionOptionsRegistry);
			Lifecycle lifecycle2 = dimensionsConfig.getLifecycle().add(lifecycle);
			LevelProperties levelProperties = LevelProperties.readProperties(
				dynamic, dataFixer, i, nbtCompound3, levelInfo, saveVersionInfo, dimensionsConfig.specialWorldProperty(), worldGenSettings.generatorOptions(), lifecycle2
			);
			return Pair.of(levelProperties, dimensionsConfig);
		};
	}

	BiFunction<Path, DataFixer, LevelSummary> createLevelDataParser(LevelStorage.LevelSave levelSave, boolean locked) {
		return (path, dataFixer) -> {
			try {
				if (Files.isSymbolicLink(path)) {
					List<SymlinkEntry> list = this.symlinkFinder.validate(path);
					if (!list.isEmpty()) {
						LOGGER.warn("{}", SymlinkValidationException.getMessage(path, list));
						return new LevelSummary.SymlinkLevelSummary(levelSave.getRootPath(), levelSave.getIconPath());
					}
				}

				if (loadCompactLevelData(path) instanceof NbtCompound nbtCompound) {
					NbtCompound nbtCompound2 = nbtCompound.getCompound("Data");
					int i = NbtHelper.getDataVersion(nbtCompound2, -1);
					Dynamic<?> dynamic = DataFixTypes.LEVEL.update(dataFixer, new Dynamic<>(NbtOps.INSTANCE, nbtCompound2), i);
					SaveVersionInfo saveVersionInfo = SaveVersionInfo.fromDynamic(dynamic);
					int j = saveVersionInfo.getLevelFormatVersion();
					if (j == 19132 || j == 19133) {
						boolean bl2 = j != this.getCurrentVersion();
						Path path2 = levelSave.getIconPath();
						DataConfiguration dataConfiguration = parseDataPackSettings(dynamic);
						LevelInfo levelInfo = LevelInfo.fromDynamic(dynamic, dataConfiguration);
						FeatureSet featureSet = parseEnabledFeatures(dynamic);
						boolean bl3 = FeatureFlags.isNotVanilla(featureSet);
						return new LevelSummary(levelInfo, saveVersionInfo, levelSave.getRootPath(), bl2, locked, bl3, path2);
					}
				} else {
					LOGGER.warn("Invalid root tag in {}", path);
				}

				return null;
			} catch (Exception var18) {
				LOGGER.error("Exception reading {}", path, var18);
				return null;
			}
		};
	}

	private static FeatureSet parseEnabledFeatures(Dynamic<?> levelData) {
		Set<Identifier> set = (Set<Identifier>)levelData.get("enabled_features")
			.asStream()
			.flatMap(featureFlag -> featureFlag.asString().result().map(Identifier::tryParse).stream())
			.collect(Collectors.toSet());
		return FeatureFlags.FEATURE_MANAGER.featureSetOf(set, id -> {
		});
	}

	/**
	 * {@return the compact version of the NBT for the level data {@code file}}
	 * 
	 * <p>The returned NBT will not have {@code Player} and {@code WorldGenSettings} keys.
	 */
	@Nullable
	private static NbtElement loadCompactLevelData(Path path) throws IOException {
		ExclusiveNbtCollector exclusiveNbtCollector = new ExclusiveNbtCollector(
			new NbtScanQuery("Data", NbtCompound.TYPE, "Player"), new NbtScanQuery("Data", NbtCompound.TYPE, "WorldGenSettings")
		);
		NbtIo.scanCompressed(path.toFile(), exclusiveNbtCollector);
		return exclusiveNbtCollector.getRoot();
	}

	public boolean isLevelNameValid(String name) {
		try {
			Path path = this.resolve(name);
			Files.createDirectory(path);
			Files.deleteIfExists(path);
			return true;
		} catch (IOException var3) {
			return false;
		}
	}

	public boolean levelExists(String name) {
		return Files.isDirectory(this.resolve(name), new LinkOption[0]);
	}

	private Path resolve(String name) {
		return this.savesDirectory.resolve(name);
	}

	public Path getSavesDirectory() {
		return this.savesDirectory;
	}

	public Path getBackupsDirectory() {
		return this.backupsDirectory;
	}

	public LevelStorage.Session createSession(String directoryName) throws IOException, SymlinkValidationException {
		Path path = this.resolve(directoryName);
		List<SymlinkEntry> list = this.symlinkFinder.collect(path, true);
		if (!list.isEmpty()) {
			throw new SymlinkValidationException(path, list);
		} else {
			return new LevelStorage.Session(directoryName, path);
		}
	}

	public LevelStorage.Session createSessionWithoutSymlinkCheck(String directoryName) throws IOException {
		Path path = this.resolve(directoryName);
		return new LevelStorage.Session(directoryName, path);
	}

	public SymlinkFinder getSymlinkFinder() {
		return this.symlinkFinder;
	}

	public static record LevelList(List<LevelStorage.LevelSave> levels) implements Iterable<LevelStorage.LevelSave> {

		public boolean isEmpty() {
			return this.levels.isEmpty();
		}

		public Iterator<LevelStorage.LevelSave> iterator() {
			return this.levels.iterator();
		}
	}

	public static record LevelSave(Path path) {
		public String getRootPath() {
			return this.path.getFileName().toString();
		}

		public Path getLevelDatPath() {
			return this.getPath(WorldSavePath.LEVEL_DAT);
		}

		public Path getLevelDatOldPath() {
			return this.getPath(WorldSavePath.LEVEL_DAT_OLD);
		}

		public Path getCorruptedLevelDatPath(LocalDateTime dateTime) {
			return this.path.resolve(WorldSavePath.LEVEL_DAT.getRelativePath() + "_corrupted_" + dateTime.format(LevelStorage.TIME_FORMATTER));
		}

		public Path getIconPath() {
			return this.getPath(WorldSavePath.ICON_PNG);
		}

		public Path getSessionLockPath() {
			return this.getPath(WorldSavePath.SESSION_LOCK);
		}

		public Path getPath(WorldSavePath savePath) {
			return this.path.resolve(savePath.getRelativePath());
		}
	}

	public class Session implements AutoCloseable {
		final SessionLock lock;
		final LevelStorage.LevelSave directory;
		private final String directoryName;
		private final Map<WorldSavePath, Path> paths = Maps.<WorldSavePath, Path>newHashMap();

		Session(String directoryName, Path path) throws IOException {
			this.directoryName = directoryName;
			this.directory = new LevelStorage.LevelSave(path);
			this.lock = SessionLock.create(path);
		}

		public LevelStorage getLevelStorage() {
			return LevelStorage.this;
		}

		public String getDirectoryName() {
			return this.directoryName;
		}

		public Path getDirectory(WorldSavePath savePath) {
			return (Path)this.paths.computeIfAbsent(savePath, this.directory::getPath);
		}

		public Path getWorldDirectory(RegistryKey<World> key) {
			return DimensionType.getSaveDirectory(key, this.directory.path());
		}

		private void checkValid() {
			if (!this.lock.isValid()) {
				throw new IllegalStateException("Lock is no longer valid");
			}
		}

		public WorldSaveHandler createSaveHandler() {
			this.checkValid();
			return new WorldSaveHandler(this, LevelStorage.this.dataFixer);
		}

		@Nullable
		public LevelSummary getLevelSummary() {
			this.checkValid();
			return LevelStorage.this.readLevelProperties(this.directory, LevelStorage.this.createLevelDataParser(this.directory, false));
		}

		@Nullable
		public Pair<SaveProperties, DimensionOptionsRegistryHolder.DimensionsConfig> readLevelProperties(
			DynamicOps<NbtElement> ops, DataConfiguration dataConfiguration, Registry<DimensionOptions> dimensionOptionsRegistry, Lifecycle lifecycle
		) {
			this.checkValid();
			return LevelStorage.this.readLevelProperties(this.directory, LevelStorage.createLevelDataParser(ops, dataConfiguration, dimensionOptionsRegistry, lifecycle));
		}

		@Nullable
		public DataConfiguration getDataPackSettings() {
			this.checkValid();
			return LevelStorage.this.readLevelProperties(this.directory, LevelStorage::readDataPackSettings);
		}

		public void backupLevelDataFile(DynamicRegistryManager registryManager, SaveProperties saveProperties) {
			this.backupLevelDataFile(registryManager, saveProperties, null);
		}

		public void backupLevelDataFile(DynamicRegistryManager registryManager, SaveProperties saveProperties, @Nullable NbtCompound nbt) {
			File file = this.directory.path().toFile();
			NbtCompound nbtCompound = saveProperties.cloneWorldNbt(registryManager, nbt);
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.put("Data", nbtCompound);

			try {
				File file2 = File.createTempFile("level", ".dat", file);
				NbtIo.writeCompressed(nbtCompound2, file2);
				File file3 = this.directory.getLevelDatOldPath().toFile();
				File file4 = this.directory.getLevelDatPath().toFile();
				Util.backupAndReplace(file4, file2, file3);
			} catch (Exception var10) {
				LevelStorage.LOGGER.error("Failed to save level {}", file, var10);
			}
		}

		public Optional<Path> getIconFile() {
			return !this.lock.isValid() ? Optional.empty() : Optional.of(this.directory.getIconPath());
		}

		public void deleteSessionLock() throws IOException {
			this.checkValid();
			final Path path = this.directory.getSessionLockPath();
			LevelStorage.LOGGER.info("Deleting level {}", this.directoryName);

			for (int i = 1; i <= 5; i++) {
				LevelStorage.LOGGER.info("Attempt {}...", i);

				try {
					Files.walkFileTree(this.directory.path(), new SimpleFileVisitor<Path>() {
						public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
							if (!path.equals(path)) {
								LevelStorage.LOGGER.debug("Deleting {}", path);
								Files.delete(path);
							}

							return FileVisitResult.CONTINUE;
						}

						public FileVisitResult postVisitDirectory(Path path, @Nullable IOException iOException) throws IOException {
							if (iOException != null) {
								throw iOException;
							} else {
								if (path.equals(Session.this.directory.path())) {
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

					LevelStorage.LOGGER.warn("Failed to delete {}", this.directory.path(), var6);

					try {
						Thread.sleep(500L);
					} catch (InterruptedException var5) {
					}
				}
			}
		}

		public void save(String name) throws IOException {
			this.checkValid();
			Path path = this.directory.getLevelDatPath();
			if (Files.exists(path, new LinkOption[0])) {
				NbtCompound nbtCompound = NbtIo.readCompressed(path.toFile());
				NbtCompound nbtCompound2 = nbtCompound.getCompound("Data");
				nbtCompound2.putString("LevelName", name);
				NbtIo.writeCompressed(nbtCompound, path.toFile());
			}
		}

		public long createBackup() throws IOException {
			this.checkValid();
			String string = LocalDateTime.now().format(LevelStorage.TIME_FORMATTER) + "_" + this.directoryName;
			Path path = LevelStorage.this.getBackupsDirectory();

			try {
				PathUtil.createDirectories(path);
			} catch (IOException var9) {
				throw new RuntimeException(var9);
			}

			Path path2 = path.resolve(PathUtil.getNextUniqueName(path, string, ".zip"));
			final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path2)));

			try {
				final Path path3 = Paths.get(this.directoryName);
				Files.walkFileTree(this.directory.path(), new SimpleFileVisitor<Path>() {
					public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
						if (path.endsWith("session.lock")) {
							return FileVisitResult.CONTINUE;
						} else {
							String string = path3.resolve(Session.this.directory.path().relativize(path)).toString().replace('\\', '/');
							ZipEntry zipEntry = new ZipEntry(string);
							zipOutputStream.putNextEntry(zipEntry);
							com.google.common.io.Files.asByteSource(path.toFile()).copyTo(zipOutputStream);
							zipOutputStream.closeEntry();
							return FileVisitResult.CONTINUE;
						}
					}
				});
			} catch (Throwable var8) {
				try {
					zipOutputStream.close();
				} catch (Throwable var7) {
					var8.addSuppressed(var7);
				}

				throw var8;
			}

			zipOutputStream.close();
			return Files.size(path2);
		}

		public void close() throws IOException {
			this.lock.close();
		}
	}
}
