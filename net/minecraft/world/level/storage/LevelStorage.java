/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.scanner.ExclusiveNbtCollector;
import net.minecraft.nbt.scanner.NbtScanQuery;
import net.minecraft.nbt.scanner.NbtScanner;
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
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.SaveVersionInfo;
import net.minecraft.world.level.storage.SessionLock;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LevelStorage {
    static final Logger LOGGER = LogUtils.getLogger();
    static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
    private static final ImmutableList<String> GENERATOR_OPTION_KEYS = ImmutableList.of("RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest");
    private static final String DATA_KEY = "Data";
    final Path savesDirectory;
    private final Path backupsDirectory;
    final DataFixer dataFixer;

    public LevelStorage(Path savesDirectory, Path backupsDirectory, DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        try {
            PathUtil.createDirectories(savesDirectory);
        } catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
        this.savesDirectory = savesDirectory;
        this.backupsDirectory = backupsDirectory;
    }

    public static LevelStorage create(Path path) {
        return new LevelStorage(path, path.resolve("../backups"), Schemas.getFixer());
    }

    private static <T> DataResult<WorldGenSettings> readGeneratorProperties(Dynamic<T> levelData, DataFixer dataFixer, int version) {
        Dynamic<T> dynamic = levelData.get("WorldGenSettings").orElseEmptyMap();
        for (String string : GENERATOR_OPTION_KEYS) {
            Optional<Dynamic<T>> optional = levelData.get(string).result();
            if (!optional.isPresent()) continue;
            dynamic = dynamic.set(string, optional.get());
        }
        Dynamic<T> dynamic2 = DataFixTypes.WORLD_GEN_SETTINGS.update(dataFixer, dynamic, version);
        return WorldGenSettings.CODEC.parse(dynamic2);
    }

    private static DataConfiguration parseDataPackSettings(Dynamic<?> dynamic) {
        return DataConfiguration.CODEC.parse(dynamic).resultOrPartial(LOGGER::error).orElse(DataConfiguration.SAFE_MODE);
    }

    public String getFormatName() {
        return "Anvil";
    }

    public LevelList getLevelList() throws LevelStorageException {
        if (!Files.isDirectory(this.savesDirectory, new LinkOption[0])) {
            throw new LevelStorageException(Text.translatable("selectWorld.load_folder_access"));
        }
        try {
            List<LevelSave> list = Files.list(this.savesDirectory).filter(path -> Files.isDirectory(path, new LinkOption[0])).map(LevelSave::new).filter(levelSave -> Files.isRegularFile(levelSave.getLevelDatPath(), new LinkOption[0]) || Files.isRegularFile(levelSave.getLevelDatOldPath(), new LinkOption[0])).toList();
            return new LevelList(list);
        } catch (IOException iOException) {
            throw new LevelStorageException(Text.translatable("selectWorld.load_folder_access"));
        }
    }

    public CompletableFuture<List<LevelSummary>> loadSummaries(LevelList levels) {
        ArrayList<CompletableFuture<LevelSummary>> list = new ArrayList<CompletableFuture<LevelSummary>>(levels.levels.size());
        for (LevelSave levelSave : levels.levels) {
            list.add(CompletableFuture.supplyAsync(() -> {
                boolean bl;
                try {
                    bl = SessionLock.isLocked(levelSave.path());
                } catch (Exception exception) {
                    LOGGER.warn("Failed to read {} lock", (Object)levelSave.path(), (Object)exception);
                    return null;
                }
                try {
                    LevelSummary levelSummary = this.readLevelProperties(levelSave, this.createLevelDataParser(levelSave, bl));
                    if (levelSummary != null) {
                        return levelSummary;
                    }
                } catch (OutOfMemoryError outOfMemoryError) {
                    CrashMemoryReserve.releaseMemory();
                    System.gc();
                    LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of memory trying to read summary of {}", (Object)levelSave.getRootPath());
                    throw outOfMemoryError;
                } catch (StackOverflowError stackOverflowError) {
                    LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of stack trying to read summary of {}. Assuming corruption; attempting to restore from from level.dat_old.", (Object)levelSave.getRootPath());
                    Util.backupAndReplace(levelSave.getLevelDatPath(), levelSave.getLevelDatOldPath(), levelSave.getCorruptedLevelDatPath(LocalDateTime.now()), true);
                    throw stackOverflowError;
                }
                return null;
            }, Util.getMainWorkerExecutor()));
        }
        return Util.combineCancellable(list).thenApply(summaries -> summaries.stream().filter(Objects::nonNull).sorted().toList());
    }

    private int getCurrentVersion() {
        return 19133;
    }

    @Nullable
    <T> T readLevelProperties(LevelSave levelSave, BiFunction<Path, DataFixer, T> levelDataParser) {
        T object;
        if (!Files.exists(levelSave.path(), new LinkOption[0])) {
            return null;
        }
        Path path = levelSave.getLevelDatPath();
        if (Files.exists(path, new LinkOption[0]) && (object = levelDataParser.apply(path, this.dataFixer)) != null) {
            return object;
        }
        path = levelSave.getLevelDatOldPath();
        if (Files.exists(path, new LinkOption[0])) {
            return levelDataParser.apply(path, this.dataFixer);
        }
        return null;
    }

    @Nullable
    private static DataConfiguration readDataPackSettings(Path path, DataFixer dataFixer) {
        try {
            NbtElement nbtElement = LevelStorage.loadCompactLevelData(path);
            if (nbtElement instanceof NbtCompound) {
                NbtCompound nbtCompound = (NbtCompound)nbtElement;
                NbtCompound nbtCompound2 = nbtCompound.getCompound(DATA_KEY);
                int i = NbtHelper.getDataVersion(nbtCompound2, -1);
                Dynamic<NbtCompound> dynamic = DataFixTypes.LEVEL.update(dataFixer, new Dynamic<NbtCompound>(NbtOps.INSTANCE, nbtCompound2), i);
                return LevelStorage.parseDataPackSettings(dynamic);
            }
        } catch (Exception exception) {
            LOGGER.error("Exception reading {}", (Object)path, (Object)exception);
        }
        return null;
    }

    static BiFunction<Path, DataFixer, Pair<SaveProperties, DimensionOptionsRegistryHolder.DimensionsConfig>> createLevelDataParser(DynamicOps<NbtElement> ops, DataConfiguration dataConfiguration, Registry<DimensionOptions> dimensionOptionsRegistry, Lifecycle lifecycle) {
        return (path, dataFixer) -> {
            NbtCompound nbtCompound;
            try {
                nbtCompound = NbtIo.readCompressed(path.toFile());
            } catch (IOException iOException) {
                throw new UncheckedIOException(iOException);
            }
            NbtCompound nbtCompound2 = nbtCompound.getCompound(DATA_KEY);
            NbtCompound nbtCompound3 = nbtCompound2.contains("Player", NbtElement.COMPOUND_TYPE) ? nbtCompound2.getCompound("Player") : null;
            nbtCompound2.remove("Player");
            int i = NbtHelper.getDataVersion(nbtCompound2, -1);
            Dynamic<NbtCompound> dynamic = DataFixTypes.LEVEL.update((DataFixer)dataFixer, new Dynamic<NbtCompound>(ops, nbtCompound2), i);
            WorldGenSettings worldGenSettings = LevelStorage.readGeneratorProperties(dynamic, dataFixer, i).getOrThrow(false, Util.addPrefix("WorldGenSettings: ", LOGGER::error));
            SaveVersionInfo saveVersionInfo = SaveVersionInfo.fromDynamic(dynamic);
            LevelInfo levelInfo = LevelInfo.fromDynamic(dynamic, dataConfiguration);
            DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = worldGenSettings.dimensionOptionsRegistryHolder().toConfig(dimensionOptionsRegistry);
            Lifecycle lifecycle2 = dimensionsConfig.getLifecycle().add(lifecycle);
            LevelProperties levelProperties = LevelProperties.readProperties(dynamic, dataFixer, i, nbtCompound3, levelInfo, saveVersionInfo, dimensionsConfig.specialWorldProperty(), worldGenSettings.generatorOptions(), lifecycle2);
            return Pair.of(levelProperties, dimensionsConfig);
        };
    }

    BiFunction<Path, DataFixer, LevelSummary> createLevelDataParser(LevelSave levelSave, boolean locked) {
        return (path, dataFixer) -> {
            try {
                NbtElement nbtElement = LevelStorage.loadCompactLevelData(path);
                if (nbtElement instanceof NbtCompound) {
                    int i;
                    NbtCompound nbtCompound = (NbtCompound)nbtElement;
                    NbtCompound nbtCompound2 = nbtCompound.getCompound(DATA_KEY);
                    Dynamic<NbtCompound> dynamic = DataFixTypes.LEVEL.update((DataFixer)dataFixer, new Dynamic<NbtCompound>(NbtOps.INSTANCE, nbtCompound2), i = NbtHelper.getDataVersion(nbtCompound2, -1));
                    SaveVersionInfo saveVersionInfo = SaveVersionInfo.fromDynamic(dynamic);
                    int j = saveVersionInfo.getLevelFormatVersion();
                    if (j == 19132 || j == 19133) {
                        boolean bl2 = j != this.getCurrentVersion();
                        Path path2 = levelSave.getIconPath();
                        DataConfiguration dataConfiguration = LevelStorage.parseDataPackSettings(dynamic);
                        LevelInfo levelInfo = LevelInfo.fromDynamic(dynamic, dataConfiguration);
                        FeatureSet featureSet = LevelStorage.parseEnabledFeatures(dynamic);
                        boolean bl3 = FeatureFlags.isNotVanilla(featureSet);
                        return new LevelSummary(levelInfo, saveVersionInfo, levelSave.getRootPath(), bl2, locked, bl3, path2);
                    }
                } else {
                    LOGGER.warn("Invalid root tag in {}", path);
                }
                return null;
            } catch (Exception exception) {
                LOGGER.error("Exception reading {}", path, (Object)exception);
                return null;
            }
        };
    }

    private static FeatureSet parseEnabledFeatures(Dynamic<?> levelData) {
        Set<Identifier> set = levelData.get("enabled_features").asStream().flatMap(featureFlag -> featureFlag.asString().result().map(Identifier::tryParse).stream()).collect(Collectors.toSet());
        return FeatureFlags.FEATURE_MANAGER.featureSetOf(set, id -> {});
    }

    /**
     * {@return the compact version of the NBT for the level data {@code file}}
     * 
     * <p>The returned NBT will not have {@code Player} and {@code WorldGenSettings} keys.
     */
    @Nullable
    private static NbtElement loadCompactLevelData(Path path) throws IOException {
        ExclusiveNbtCollector exclusiveNbtCollector = new ExclusiveNbtCollector(new NbtScanQuery(DATA_KEY, NbtCompound.TYPE, "Player"), new NbtScanQuery(DATA_KEY, NbtCompound.TYPE, "WorldGenSettings"));
        NbtIo.scanCompressed(path.toFile(), (NbtScanner)exclusiveNbtCollector);
        return exclusiveNbtCollector.getRoot();
    }

    public boolean isLevelNameValid(String name) {
        try {
            Path path = this.savesDirectory.resolve(name);
            Files.createDirectory(path, new FileAttribute[0]);
            Files.deleteIfExists(path);
            return true;
        } catch (IOException iOException) {
            return false;
        }
    }

    public boolean levelExists(String name) {
        return Files.isDirectory(this.savesDirectory.resolve(name), new LinkOption[0]);
    }

    public Path getSavesDirectory() {
        return this.savesDirectory;
    }

    public Path getBackupsDirectory() {
        return this.backupsDirectory;
    }

    public Session createSession(String directoryName) throws IOException {
        return new Session(directoryName);
    }

    public record LevelList(List<LevelSave> levels) implements Iterable<LevelSave>
    {
        public boolean isEmpty() {
            return this.levels.isEmpty();
        }

        @Override
        public Iterator<LevelSave> iterator() {
            return this.levels.iterator();
        }
    }

    public record LevelSave(Path path) {
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
            return this.path.resolve(WorldSavePath.LEVEL_DAT.getRelativePath() + "_corrupted_" + dateTime.format(TIME_FORMATTER));
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

    public class Session
    implements AutoCloseable {
        final SessionLock lock;
        final LevelSave directory;
        private final String directoryName;
        private final Map<WorldSavePath, Path> paths = Maps.newHashMap();

        public Session(String directoryName) throws IOException {
            this.directoryName = directoryName;
            this.directory = new LevelSave(LevelStorage.this.savesDirectory.resolve(directoryName));
            this.lock = SessionLock.create(this.directory.path());
        }

        public String getDirectoryName() {
            return this.directoryName;
        }

        public Path getDirectory(WorldSavePath savePath) {
            return this.paths.computeIfAbsent(savePath, this.directory::getPath);
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
        public Pair<SaveProperties, DimensionOptionsRegistryHolder.DimensionsConfig> readLevelProperties(DynamicOps<NbtElement> ops, DataConfiguration dataConfiguration, Registry<DimensionOptions> dimensionOptionsRegistry, Lifecycle lifecycle) {
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
            nbtCompound2.put(LevelStorage.DATA_KEY, nbtCompound);
            try {
                File file2 = File.createTempFile("level", ".dat", file);
                NbtIo.writeCompressed(nbtCompound2, file2);
                File file3 = this.directory.getLevelDatOldPath().toFile();
                File file4 = this.directory.getLevelDatPath().toFile();
                Util.backupAndReplace(file4, file2, file3);
            } catch (Exception exception) {
                LOGGER.error("Failed to save level {}", (Object)file, (Object)exception);
            }
        }

        public Optional<Path> getIconFile() {
            if (!this.lock.isValid()) {
                return Optional.empty();
            }
            return Optional.of(this.directory.getIconPath());
        }

        public void deleteSessionLock() throws IOException {
            this.checkValid();
            final Path path = this.directory.getSessionLockPath();
            LOGGER.info("Deleting level {}", (Object)this.directoryName);
            for (int i = 1; i <= 5; ++i) {
                LOGGER.info("Attempt {}...", (Object)i);
                try {
                    Files.walkFileTree(this.directory.path(), (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                        @Override
                        public FileVisitResult visitFile(Path path2, BasicFileAttributes basicFileAttributes) throws IOException {
                            if (!path2.equals(path)) {
                                LOGGER.debug("Deleting {}", (Object)path2);
                                Files.delete(path2);
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path path2, IOException iOException) throws IOException {
                            if (iOException != null) {
                                throw iOException;
                            }
                            if (path2.equals(Session.this.directory.path())) {
                                Session.this.lock.close();
                                Files.deleteIfExists(path);
                            }
                            Files.delete(path2);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public /* synthetic */ FileVisitResult postVisitDirectory(Object path2, IOException exception) throws IOException {
                            return this.postVisitDirectory((Path)path2, exception);
                        }

                        @Override
                        public /* synthetic */ FileVisitResult visitFile(Object path2, BasicFileAttributes attributes) throws IOException {
                            return this.visitFile((Path)path2, attributes);
                        }
                    });
                    break;
                } catch (IOException iOException) {
                    if (i < 5) {
                        LOGGER.warn("Failed to delete {}", (Object)this.directory.path(), (Object)iOException);
                        try {
                            Thread.sleep(500L);
                        } catch (InterruptedException interruptedException) {}
                        continue;
                    }
                    throw iOException;
                }
            }
        }

        public void save(String name) throws IOException {
            this.checkValid();
            Path path = this.directory.getLevelDatPath();
            if (Files.exists(path, new LinkOption[0])) {
                NbtCompound nbtCompound = NbtIo.readCompressed(path.toFile());
                NbtCompound nbtCompound2 = nbtCompound.getCompound(LevelStorage.DATA_KEY);
                nbtCompound2.putString("LevelName", name);
                NbtIo.writeCompressed(nbtCompound, path.toFile());
            }
        }

        public long createBackup() throws IOException {
            this.checkValid();
            String string = LocalDateTime.now().format(TIME_FORMATTER) + "_" + this.directoryName;
            Path path = LevelStorage.this.getBackupsDirectory();
            try {
                PathUtil.createDirectories(path);
            } catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            Path path2 = path.resolve(PathUtil.getNextUniqueName(path, string, ".zip"));
            try (final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path2, new OpenOption[0])));){
                final Path path3 = Paths.get(this.directoryName, new String[0]);
                Files.walkFileTree(this.directory.path(), (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                        if (path.endsWith("session.lock")) {
                            return FileVisitResult.CONTINUE;
                        }
                        String string = path3.resolve(Session.this.directory.path().relativize(path)).toString().replace('\\', '/');
                        ZipEntry zipEntry = new ZipEntry(string);
                        zipOutputStream.putNextEntry(zipEntry);
                        com.google.common.io.Files.asByteSource(path.toFile()).copyTo(zipOutputStream);
                        zipOutputStream.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public /* synthetic */ FileVisitResult visitFile(Object path, BasicFileAttributes attributes) throws IOException {
                        return this.visitFile((Path)path, attributes);
                    }
                });
            }
            return Files.size(path2);
        }

        @Override
        public void close() throws IOException {
            this.lock.close();
        }
    }
}

