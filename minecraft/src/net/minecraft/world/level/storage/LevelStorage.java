package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.InvalidNbtException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.scanner.ExclusiveNbtCollector;
import net.minecraft.nbt.scanner.NbtScanQuery;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.SaveLoading;
import net.minecraft.text.Text;
import net.minecraft.util.DateTimeFormatters;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashMemoryReserve;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
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
	static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatters.create();
	private static final String DATA_KEY = "Data";
	private static final PathMatcher DEFAULT_ALLOWED_SYMLINK_MATCHER = path -> false;
	public static final String ALLOWED_SYMLINKS_FILE_NAME = "allowed_symlinks.txt";
	private static final int MAX_LEVEL_DATA_SIZE = 104857600;
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

	public static DataConfiguration parseDataPackSettings(Dynamic<?> dynamic) {
		return (DataConfiguration)DataConfiguration.CODEC.parse(dynamic).resultOrPartial(LOGGER::error).orElse(DataConfiguration.SAFE_MODE);
	}

	public static SaveLoading.DataPacks parseDataPacks(Dynamic<?> dynamic, ResourcePackManager dataPackManager, boolean safeMode) {
		return new SaveLoading.DataPacks(dataPackManager, parseDataPackSettings(dynamic), safeMode, false);
	}

	public static ParsedSaveProperties parseSaveProperties(
		Dynamic<?> dynamic, DataConfiguration dataConfiguration, Registry<DimensionOptions> dimensionsRegistry, DynamicRegistryManager.Immutable registryManager
	) {
		Dynamic<?> dynamic2 = RegistryOps.method_56622(dynamic, registryManager);
		Dynamic<?> dynamic3 = dynamic2.get("WorldGenSettings").orElseEmptyMap();
		WorldGenSettings worldGenSettings = WorldGenSettings.CODEC.parse(dynamic3).getOrThrow(false, Util.addPrefix("WorldGenSettings: ", LOGGER::error));
		LevelInfo levelInfo = LevelInfo.fromDynamic(dynamic2, dataConfiguration);
		DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = worldGenSettings.dimensionOptionsRegistryHolder().toConfig(dimensionsRegistry);
		Lifecycle lifecycle = dimensionsConfig.getLifecycle().add(registryManager.getRegistryLifecycle());
		LevelProperties levelProperties = LevelProperties.readProperties(
			dynamic2, levelInfo, dimensionsConfig.specialWorldProperty(), worldGenSettings.generatorOptions(), lifecycle
		);
		return new ParsedSaveProperties(levelProperties, dimensionsConfig);
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
			list.add(CompletableFuture.supplyAsync(() -> {
				boolean bl;
				try {
					bl = SessionLock.isLocked(levelSave.path());
				} catch (Exception var13) {
					LOGGER.warn("Failed to read {} lock", levelSave.path(), var13);
					return null;
				}

				try {
					return this.readSummary(levelSave, bl);
				} catch (OutOfMemoryError var12) {
					CrashMemoryReserve.releaseMemory();
					System.gc();
					String string = "Ran out of memory trying to read summary of world folder \"" + levelSave.getRootPath() + "\"";
					LOGGER.error(LogUtils.FATAL_MARKER, string);
					OutOfMemoryError outOfMemoryError2 = new OutOfMemoryError("Ran out of memory reading level data");
					outOfMemoryError2.initCause(var12);
					CrashReport crashReport = CrashReport.create(outOfMemoryError2, string);
					CrashReportSection crashReportSection = crashReport.addElement("World details");
					crashReportSection.add("Folder Name", levelSave.getRootPath());

					try {
						long l = Files.size(levelSave.getLevelDatPath());
						crashReportSection.add("level.dat size", l);
					} catch (IOException var11) {
						crashReportSection.add("level.dat size", (Throwable)var11);
					}

					throw new CrashException(crashReport);
				}
			}, Util.getMainWorkerExecutor()));
		}

		return Util.combineCancellable(list).thenApply(summaries -> summaries.stream().filter(Objects::nonNull).sorted().toList());
	}

	private int getCurrentVersion() {
		return 19133;
	}

	static NbtCompound readLevelProperties(Path path) throws IOException {
		return NbtIo.readCompressed(path, NbtSizeTracker.of(104857600L));
	}

	static Dynamic<?> readLevelProperties(Path path, DataFixer dataFixer) throws IOException {
		NbtCompound nbtCompound = readLevelProperties(path);
		NbtCompound nbtCompound2 = nbtCompound.getCompound("Data");
		int i = NbtHelper.getDataVersion(nbtCompound2, -1);
		Dynamic<?> dynamic = DataFixTypes.LEVEL.update(dataFixer, new Dynamic<>(NbtOps.INSTANCE, nbtCompound2), i);
		Dynamic<?> dynamic2 = dynamic.get("Player").orElseEmptyMap();
		Dynamic<?> dynamic3 = DataFixTypes.PLAYER.update(dataFixer, dynamic2, i);
		dynamic = dynamic.set("Player", dynamic3);
		Dynamic<?> dynamic4 = dynamic.get("WorldGenSettings").orElseEmptyMap();
		Dynamic<?> dynamic5 = DataFixTypes.WORLD_GEN_SETTINGS.update(dataFixer, dynamic4, i);
		return dynamic.set("WorldGenSettings", dynamic5);
	}

	private LevelSummary readSummary(LevelStorage.LevelSave save, boolean locked) {
		Path path = save.getLevelDatPath();
		if (Files.exists(path, new LinkOption[0])) {
			try {
				if (Files.isSymbolicLink(path)) {
					List<SymlinkEntry> list = this.symlinkFinder.validate(path);
					if (!list.isEmpty()) {
						LOGGER.warn("{}", SymlinkValidationException.getMessage(path, list));
						return new LevelSummary.SymlinkLevelSummary(save.getRootPath(), save.getIconPath());
					}
				}

				if (loadCompactLevelData(path) instanceof NbtCompound nbtCompound) {
					NbtCompound nbtCompound2 = nbtCompound.getCompound("Data");
					int i = NbtHelper.getDataVersion(nbtCompound2, -1);
					Dynamic<?> dynamic = DataFixTypes.LEVEL.update(this.dataFixer, new Dynamic<>(NbtOps.INSTANCE, nbtCompound2), i);
					return this.parseSummary(dynamic, save, locked);
				}

				LOGGER.warn("Invalid root tag in {}", path);
			} catch (Exception var9) {
				LOGGER.error("Exception reading {}", path, var9);
			}
		}

		return new LevelSummary.RecoveryWarning(save.getRootPath(), save.getIconPath(), getLastModifiedTime(save));
	}

	private static long getLastModifiedTime(LevelStorage.LevelSave save) {
		Instant instant = getLastModifiedTime(save.getLevelDatPath());
		if (instant == null) {
			instant = getLastModifiedTime(save.getLevelDatOldPath());
		}

		return instant == null ? -1L : instant.toEpochMilli();
	}

	@Nullable
	static Instant getLastModifiedTime(Path path) {
		try {
			return Files.getLastModifiedTime(path).toInstant();
		} catch (IOException var2) {
			return null;
		}
	}

	LevelSummary parseSummary(Dynamic<?> dynamic, LevelStorage.LevelSave save, boolean locked) {
		SaveVersionInfo saveVersionInfo = SaveVersionInfo.fromDynamic(dynamic);
		int i = saveVersionInfo.getLevelFormatVersion();
		if (i != 19132 && i != 19133) {
			throw new InvalidNbtException("Unknown data version: " + Integer.toHexString(i));
		} else {
			boolean bl = i != this.getCurrentVersion();
			Path path = save.getIconPath();
			DataConfiguration dataConfiguration = parseDataPackSettings(dynamic);
			LevelInfo levelInfo = LevelInfo.fromDynamic(dynamic, dataConfiguration);
			FeatureSet featureSet = parseEnabledFeatures(dynamic);
			boolean bl2 = FeatureFlags.isNotVanilla(featureSet);
			return new LevelSummary(levelInfo, saveVersionInfo, save.getRootPath(), bl, locked, bl2, path);
		}
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
		NbtIo.scanCompressed(path, exclusiveNbtCollector, NbtSizeTracker.of(104857600L));
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
		try {
			return Files.isDirectory(this.resolve(name), new LinkOption[0]);
		} catch (InvalidPathException var3) {
			return false;
		}
	}

	public Path resolve(String name) {
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

		public Path getRawLevelDatPath(LocalDateTime dateTime) {
			return this.path.resolve(WorldSavePath.LEVEL_DAT.getRelativePath() + "_raw_" + dateTime.format(LevelStorage.TIME_FORMATTER));
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

		public void tryClose() {
			try {
				this.close();
			} catch (IOException var2) {
				LevelStorage.LOGGER.warn("Failed to unlock access to level {}", this.getDirectoryName(), var2);
			}
		}

		public LevelStorage getLevelStorage() {
			return LevelStorage.this;
		}

		public LevelStorage.LevelSave getDirectory() {
			return this.directory;
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

		public LevelSummary getLevelSummary(Dynamic<?> dynamic) {
			this.checkValid();
			return LevelStorage.this.parseSummary(dynamic, this.directory, false);
		}

		public Dynamic<?> readLevelProperties() throws IOException {
			return this.readLevelProperties(false);
		}

		public Dynamic<?> readOldLevelProperties() throws IOException {
			return this.readLevelProperties(true);
		}

		private Dynamic<?> readLevelProperties(boolean old) throws IOException {
			this.checkValid();
			return LevelStorage.readLevelProperties(old ? this.directory.getLevelDatOldPath() : this.directory.getLevelDatPath(), LevelStorage.this.dataFixer);
		}

		public void backupLevelDataFile(DynamicRegistryManager registryManager, SaveProperties saveProperties) {
			this.backupLevelDataFile(registryManager, saveProperties, null);
		}

		public void backupLevelDataFile(DynamicRegistryManager registryManager, SaveProperties saveProperties, @Nullable NbtCompound nbt) {
			NbtCompound nbtCompound = saveProperties.cloneWorldNbt(registryManager, nbt);
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.put("Data", nbtCompound);
			this.save(nbtCompound2);
		}

		private void save(NbtCompound nbt) {
			Path path = this.directory.path();

			try {
				Path path2 = Files.createTempFile(path, "level", ".dat");
				NbtIo.writeCompressed(nbt, path2);
				Path path3 = this.directory.getLevelDatOldPath();
				Path path4 = this.directory.getLevelDatPath();
				Util.backupAndReplace(path4, path2, path3);
			} catch (Exception var6) {
				LevelStorage.LOGGER.error("Failed to save level {}", path, var6);
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
			this.save((Consumer<NbtCompound>)(nbt -> nbt.putString("LevelName", name.trim())));
		}

		public void removePlayerAndSave(String name) throws IOException {
			this.save((Consumer<NbtCompound>)(nbt -> {
				nbt.putString("LevelName", name.trim());
				nbt.remove("Player");
			}));
		}

		private void save(Consumer<NbtCompound> nbtProcessor) throws IOException {
			this.checkValid();
			NbtCompound nbtCompound = LevelStorage.readLevelProperties(this.directory.getLevelDatPath());
			nbtProcessor.accept(nbtCompound.getCompound("Data"));
			this.save(nbtCompound);
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

		public boolean levelDatExists() {
			return Files.exists(this.directory.getLevelDatPath(), new LinkOption[0]) || Files.exists(this.directory.getLevelDatOldPath(), new LinkOption[0]);
		}

		public void close() throws IOException {
			this.lock.close();
		}

		public boolean tryRestoreBackup() {
			return Util.backupAndReplace(
				this.directory.getLevelDatPath(), this.directory.getLevelDatOldPath(), this.directory.getCorruptedLevelDatPath(LocalDateTime.now()), true
			);
		}

		@Nullable
		public Instant getLastModifiedTime(boolean old) {
			return LevelStorage.getLastModifiedTime(old ? this.directory.getLevelDatOldPath() : this.directory.getLevelDatPath());
		}
	}
}
