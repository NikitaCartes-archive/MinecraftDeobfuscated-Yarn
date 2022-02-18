/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.scanner.ExclusiveNbtCollector;
import net.minecraft.nbt.scanner.NbtScanQuery;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashMemoryReserve;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.SaveVersionInfo;
import net.minecraft.world.level.storage.SessionLock;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LevelStorage {
    static final Logger LOGGER = LogUtils.getLogger();
    static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
    private static final String DEFAULT_ICON = "icon.png";
    private static final ImmutableList<String> GENERATOR_OPTION_KEYS = ImmutableList.of("RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest");
    private static final String DATA_KEY = "Data";
    final Path savesDirectory;
    private final Path backupsDirectory;
    final DataFixer dataFixer;

    public LevelStorage(Path savesDirectory, Path backupsDirectory, DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        try {
            Files.createDirectories(Files.exists(savesDirectory, new LinkOption[0]) ? savesDirectory.toRealPath(new LinkOption[0]) : savesDirectory, new FileAttribute[0]);
        } catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
        this.savesDirectory = savesDirectory;
        this.backupsDirectory = backupsDirectory;
    }

    public static LevelStorage create(Path path) {
        return new LevelStorage(path, path.resolve("../backups"), Schemas.getFixer());
    }

    private static <T> Pair<GeneratorOptions, Lifecycle> readGeneratorProperties(Dynamic<T> levelData, DataFixer dataFixer, int version) {
        Dynamic<T> dynamic = levelData.get("WorldGenSettings").orElseEmptyMap();
        for (String string : GENERATOR_OPTION_KEYS) {
            Optional<Dynamic<T>> optional = levelData.get(string).result();
            if (!optional.isPresent()) continue;
            dynamic = dynamic.set(string, optional.get());
        }
        Dynamic dynamic2 = dataFixer.update(TypeReferences.CHUNK_GENERATOR_SETTINGS, dynamic, version, SharedConstants.getGameVersion().getWorldVersion());
        DataResult dataResult = GeneratorOptions.CODEC.parse(dynamic2);
        return Pair.of(dataResult.resultOrPartial(Util.addPrefix("WorldGenSettings: ", LOGGER::error)).orElseGet(() -> {
            DynamicRegistryManager dynamicRegistryManager = DynamicRegistryManager.createDynamicRegistryManager(dynamic2);
            return GeneratorOptions.getDefaultOptions(dynamicRegistryManager);
        }), dataResult.lifecycle());
    }

    private static DataPackSettings parseDataPackSettings(Dynamic<?> dynamic) {
        return DataPackSettings.CODEC.parse(dynamic).resultOrPartial(LOGGER::error).orElse(DataPackSettings.SAFE_MODE);
    }

    public String getFormatName() {
        return "Anvil";
    }

    public List<LevelSummary> getLevelList() throws LevelStorageException {
        File[] files;
        if (!Files.isDirectory(this.savesDirectory, new LinkOption[0])) {
            throw new LevelStorageException(new TranslatableText("selectWorld.load_folder_access").getString());
        }
        ArrayList<LevelSummary> list = Lists.newArrayList();
        for (File file : files = this.savesDirectory.toFile().listFiles()) {
            boolean bl;
            if (!file.isDirectory()) continue;
            try {
                bl = SessionLock.isLocked(file.toPath());
            } catch (Exception exception) {
                LOGGER.warn("Failed to read {} lock", (Object)file, (Object)exception);
                continue;
            }
            try {
                LevelSummary levelSummary = this.readLevelProperties(file, this.createLevelDataParser(file, bl));
                if (levelSummary == null) continue;
                list.add(levelSummary);
            } catch (OutOfMemoryError outOfMemoryError) {
                CrashMemoryReserve.releaseMemory();
                System.gc();
                LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of memory trying to read summary of {}", (Object)file);
                throw outOfMemoryError;
            } catch (StackOverflowError stackOverflowError) {
                LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of stack trying to read summary of {}", (Object)file);
                throw stackOverflowError;
            }
        }
        return list;
    }

    private int getCurrentVersion() {
        return 19133;
    }

    @Nullable
    <T> T readLevelProperties(File file, BiFunction<File, DataFixer, T> levelDataParser) {
        T object;
        if (!file.exists()) {
            return null;
        }
        File file2 = new File(file, "level.dat");
        if (file2.exists() && (object = levelDataParser.apply(file2, this.dataFixer)) != null) {
            return object;
        }
        file2 = new File(file, "level.dat_old");
        if (file2.exists()) {
            return levelDataParser.apply(file2, this.dataFixer);
        }
        return null;
    }

    @Nullable
    private static DataPackSettings readDataPackSettings(File file, DataFixer dataFixer) {
        try {
            NbtElement nbtElement = LevelStorage.loadCompactLevelData(file);
            if (nbtElement instanceof NbtCompound) {
                NbtCompound nbtCompound = (NbtCompound)nbtElement;
                NbtCompound nbtCompound2 = nbtCompound.getCompound(DATA_KEY);
                int i = nbtCompound2.contains("DataVersion", 99) ? nbtCompound2.getInt("DataVersion") : -1;
                Dynamic<NbtCompound> dynamic = dataFixer.update(DataFixTypes.LEVEL.getTypeReference(), new Dynamic<NbtCompound>(NbtOps.INSTANCE, nbtCompound2), i, SharedConstants.getGameVersion().getWorldVersion());
                return dynamic.get("DataPacks").result().map(LevelStorage::parseDataPackSettings).orElse(DataPackSettings.SAFE_MODE);
            }
        } catch (Exception exception) {
            LOGGER.error("Exception reading {}", (Object)file, (Object)exception);
        }
        return null;
    }

    static BiFunction<File, DataFixer, LevelProperties> createLevelDataParser(DynamicOps<NbtElement> ops, DataPackSettings dataPackSettings) {
        return (file, dataFixer) -> {
            try {
                NbtCompound nbtCompound = NbtIo.readCompressed(file);
                NbtCompound nbtCompound2 = nbtCompound.getCompound(DATA_KEY);
                NbtCompound nbtCompound3 = nbtCompound2.contains("Player", 10) ? nbtCompound2.getCompound("Player") : null;
                nbtCompound2.remove("Player");
                int i = nbtCompound2.contains("DataVersion", 99) ? nbtCompound2.getInt("DataVersion") : -1;
                Dynamic<NbtElement> dynamic = dataFixer.update(DataFixTypes.LEVEL.getTypeReference(), new Dynamic<NbtCompound>(ops, nbtCompound2), i, SharedConstants.getGameVersion().getWorldVersion());
                Pair<GeneratorOptions, Lifecycle> pair = LevelStorage.readGeneratorProperties(dynamic, dataFixer, i);
                SaveVersionInfo saveVersionInfo = SaveVersionInfo.fromDynamic(dynamic);
                LevelInfo levelInfo = LevelInfo.fromDynamic(dynamic, dataPackSettings);
                return LevelProperties.readProperties(dynamic, dataFixer, i, nbtCompound3, levelInfo, saveVersionInfo, pair.getFirst(), pair.getSecond());
            } catch (Exception exception) {
                LOGGER.error("Exception reading {}", file, (Object)exception);
                return null;
            }
        };
    }

    BiFunction<File, DataFixer, LevelSummary> createLevelDataParser(File file2, boolean locked) {
        return (file, dataFixer) -> {
            try {
                NbtElement nbtElement = LevelStorage.loadCompactLevelData(file);
                if (nbtElement instanceof NbtCompound) {
                    NbtCompound nbtCompound = (NbtCompound)nbtElement;
                    NbtCompound nbtCompound2 = nbtCompound.getCompound(DATA_KEY);
                    int i = nbtCompound2.contains("DataVersion", 99) ? nbtCompound2.getInt("DataVersion") : -1;
                    Dynamic<NbtCompound> dynamic = dataFixer.update(DataFixTypes.LEVEL.getTypeReference(), new Dynamic<NbtCompound>(NbtOps.INSTANCE, nbtCompound2), i, SharedConstants.getGameVersion().getWorldVersion());
                    SaveVersionInfo saveVersionInfo = SaveVersionInfo.fromDynamic(dynamic);
                    int j = saveVersionInfo.getLevelFormatVersion();
                    if (j == 19132 || j == 19133) {
                        boolean bl2 = j != this.getCurrentVersion();
                        File file3 = new File(file2, DEFAULT_ICON);
                        DataPackSettings dataPackSettings = dynamic.get("DataPacks").result().map(LevelStorage::parseDataPackSettings).orElse(DataPackSettings.SAFE_MODE);
                        LevelInfo levelInfo = LevelInfo.fromDynamic(dynamic, dataPackSettings);
                        return new LevelSummary(levelInfo, saveVersionInfo, file2.getName(), bl2, locked, file3);
                    }
                } else {
                    LOGGER.warn("Invalid root tag in {}", file);
                }
                return null;
            } catch (Exception exception) {
                LOGGER.error("Exception reading {}", file, (Object)exception);
                return null;
            }
        };
    }

    /**
     * {@return the compact version of the NBT for the level data {@code file}}
     * 
     * <p>The returned NBT will not have {@code Player} and {@code WorldGenSettings} keys.
     */
    @Nullable
    private static NbtElement loadCompactLevelData(File file) throws IOException {
        ExclusiveNbtCollector exclusiveNbtCollector = new ExclusiveNbtCollector(new NbtScanQuery(DATA_KEY, NbtCompound.TYPE, "Player"), new NbtScanQuery(DATA_KEY, NbtCompound.TYPE, "WorldGenSettings"));
        NbtIo.scanCompressed(file, (NbtScanner)exclusiveNbtCollector);
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

    public class Session
    implements AutoCloseable {
        final SessionLock lock;
        final Path directory;
        private final String directoryName;
        private final Map<WorldSavePath, Path> paths = Maps.newHashMap();

        public Session(String directoryName) throws IOException {
            this.directoryName = directoryName;
            this.directory = LevelStorage.this.savesDirectory.resolve(directoryName);
            this.lock = SessionLock.create(this.directory);
        }

        public String getDirectoryName() {
            return this.directoryName;
        }

        public Path getDirectory(WorldSavePath savePath) {
            return this.paths.computeIfAbsent(savePath, path -> this.directory.resolve(path.getRelativePath()));
        }

        public Path getWorldDirectory(RegistryKey<World> key) {
            return DimensionType.getSaveDirectory(key, this.directory);
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
            return LevelStorage.this.readLevelProperties(this.directory.toFile(), LevelStorage.this.createLevelDataParser(this.directory.toFile(), false));
        }

        @Nullable
        public SaveProperties readLevelProperties(DynamicOps<NbtElement> ops, DataPackSettings dataPackSettings) {
            this.checkValid();
            return LevelStorage.this.readLevelProperties(this.directory.toFile(), LevelStorage.createLevelDataParser(ops, dataPackSettings));
        }

        @Nullable
        public DataPackSettings getDataPackSettings() {
            this.checkValid();
            return LevelStorage.this.readLevelProperties(this.directory.toFile(), LevelStorage::readDataPackSettings);
        }

        public void backupLevelDataFile(DynamicRegistryManager registryManager, SaveProperties saveProperties) {
            this.backupLevelDataFile(registryManager, saveProperties, null);
        }

        public void backupLevelDataFile(DynamicRegistryManager registryManager, SaveProperties saveProperties, @Nullable NbtCompound nbt) {
            File file = this.directory.toFile();
            NbtCompound nbtCompound = saveProperties.cloneWorldNbt(registryManager, nbt);
            NbtCompound nbtCompound2 = new NbtCompound();
            nbtCompound2.put(LevelStorage.DATA_KEY, nbtCompound);
            try {
                File file2 = File.createTempFile("level", ".dat", file);
                NbtIo.writeCompressed(nbtCompound2, file2);
                File file3 = new File(file, "level.dat_old");
                File file4 = new File(file, "level.dat");
                Util.backupAndReplace(file4, file2, file3);
            } catch (Exception exception) {
                LOGGER.error("Failed to save level {}", (Object)file, (Object)exception);
            }
        }

        public Optional<Path> getIconFile() {
            if (!this.lock.isValid()) {
                return Optional.empty();
            }
            return Optional.of(this.directory.resolve(LevelStorage.DEFAULT_ICON));
        }

        public void deleteSessionLock() throws IOException {
            this.checkValid();
            final Path path = this.directory.resolve("session.lock");
            LOGGER.info("Deleting level {}", (Object)this.directoryName);
            for (int i = 1; i <= 5; ++i) {
                LOGGER.info("Attempt {}...", (Object)i);
                try {
                    Files.walkFileTree(this.directory, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

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
                            if (path2.equals(Session.this.directory)) {
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
                        LOGGER.warn("Failed to delete {}", (Object)this.directory, (Object)iOException);
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
            File file = new File(LevelStorage.this.savesDirectory.toFile(), this.directoryName);
            if (!file.exists()) {
                return;
            }
            File file2 = new File(file, "level.dat");
            if (file2.exists()) {
                NbtCompound nbtCompound = NbtIo.readCompressed(file2);
                NbtCompound nbtCompound2 = nbtCompound.getCompound(LevelStorage.DATA_KEY);
                nbtCompound2.putString("LevelName", name);
                NbtIo.writeCompressed(nbtCompound, file2);
            }
        }

        public long createBackup() throws IOException {
            this.checkValid();
            String string = LocalDateTime.now().format(TIME_FORMATTER) + "_" + this.directoryName;
            Path path = LevelStorage.this.getBackupsDirectory();
            try {
                Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath(new LinkOption[0]) : path, new FileAttribute[0]);
            } catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            Path path2 = path.resolve(FileNameUtil.getNextUniqueName(path, string, ".zip"));
            try (final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path2, new OpenOption[0])));){
                final Path path3 = Paths.get(this.directoryName, new String[0]);
                Files.walkFileTree(this.directory, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                        if (path.endsWith("session.lock")) {
                            return FileVisitResult.CONTINUE;
                        }
                        String string = path3.resolve(Session.this.directory.relativize(path)).toString().replace('\\', '/');
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

