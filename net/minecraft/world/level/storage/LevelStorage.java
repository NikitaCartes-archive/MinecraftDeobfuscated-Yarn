/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5315;
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
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.AnvilLevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.SessionLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class LevelStorage {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
    private static final ImmutableList<String> field_25020 = ImmutableList.of("RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest");
    private static final Gson field_25021 = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    private final Path savesDirectory;
    private final Path backupsDirectory;
    private final DataFixer dataFixer;

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

    private static Pair<GeneratorOptions, Lifecycle> method_29010(Dynamic<?> dynamic, DataFixer dataFixer, int i) {
        Dynamic<?> dynamic2 = dynamic.get("WorldGenSettings").orElseEmptyMap();
        for (String string : field_25020) {
            Optional<Dynamic<?>> optional = dynamic.get(string).result();
            if (!optional.isPresent()) continue;
            dynamic2 = dynamic2.set(string, optional.get());
        }
        Dynamic<?> dynamic3 = dataFixer.update(TypeReferences.CHUNK_GENERATOR_SETTINGS, dynamic2, i, SharedConstants.getGameVersion().getWorldVersion());
        DataResult dataResult = GeneratorOptions.CODEC.parse(dynamic3);
        return Pair.of(dataResult.resultOrPartial(Util.method_29188("WorldGenSettings: ", LOGGER::error)).orElseGet(GeneratorOptions::getDefaultOptions), dataResult.lifecycle());
    }

    @Environment(value=EnvType.CLIENT)
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
            LevelSummary levelSummary = this.readLevelProperties(file, this.method_29014(file, bl));
            if (levelSummary == null) continue;
            list.add(levelSummary);
        }
        return list;
    }

    private int getCurrentVersion() {
        return 19133;
    }

    @Nullable
    private <T> T readLevelProperties(File file, BiFunction<File, DataFixer, T> biFunction) {
        T object;
        if (!file.exists()) {
            return null;
        }
        File file2 = new File(file, "level.dat");
        if (file2.exists() && (object = biFunction.apply(file2, this.dataFixer)) != null) {
            return object;
        }
        file2 = new File(file, "level.dat_old");
        if (file2.exists()) {
            return biFunction.apply(file2, this.dataFixer);
        }
        return null;
    }

    @Nullable
    private static SaveProperties readLevelProperties(File file, DataFixer dataFixer) {
        try {
            CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file));
            CompoundTag compoundTag2 = compoundTag.getCompound("Data");
            CompoundTag compoundTag3 = compoundTag2.contains("Player", 10) ? compoundTag2.getCompound("Player") : null;
            compoundTag2.remove("Player");
            int i = compoundTag2.contains("DataVersion", 99) ? compoundTag2.getInt("DataVersion") : -1;
            Dynamic<Tag> dynamic = dataFixer.update(DataFixTypes.LEVEL.getTypeReference(), new Dynamic<CompoundTag>(NbtOps.INSTANCE, compoundTag2), i, SharedConstants.getGameVersion().getWorldVersion());
            Pair<GeneratorOptions, Lifecycle> pair = LevelStorage.method_29010(dynamic, dataFixer, i);
            class_5315 lv = class_5315.method_29023(dynamic);
            LevelInfo levelInfo = LevelInfo.method_28383(dynamic, pair.getFirst());
            return LevelProperties.method_29029(dynamic, dataFixer, i, compoundTag3, levelInfo, lv);
        } catch (Exception exception) {
            LOGGER.error("Exception reading {}", (Object)file, (Object)exception);
            return null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private BiFunction<File, DataFixer, LevelSummary> method_29014(File file, boolean bl) {
        return (file2, dataFixer) -> {
            try {
                CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream((File)file2));
                CompoundTag compoundTag2 = compoundTag.getCompound("Data");
                compoundTag2.remove("Player");
                int i = compoundTag2.contains("DataVersion", 99) ? compoundTag2.getInt("DataVersion") : -1;
                Dynamic<CompoundTag> dynamic = dataFixer.update(DataFixTypes.LEVEL.getTypeReference(), new Dynamic<CompoundTag>(NbtOps.INSTANCE, compoundTag2), i, SharedConstants.getGameVersion().getWorldVersion());
                class_5315 lv = class_5315.method_29023(dynamic);
                int j = lv.method_29022();
                if (j == 19132 || j == 19133) {
                    boolean bl2 = j != this.getCurrentVersion();
                    File file3 = new File(file, "icon.png");
                    Pair<GeneratorOptions, Lifecycle> pair = LevelStorage.method_29010(dynamic, dataFixer, i);
                    LevelInfo levelInfo = LevelInfo.method_28383(dynamic, pair.getFirst());
                    return new LevelSummary(levelInfo, lv, file.getName(), bl2, bl, file3, pair.getSecond());
                }
                return null;
            } catch (Exception exception) {
                LOGGER.error("Exception reading {}", file2, (Object)exception);
                return null;
            }
        };
    }

    @Environment(value=EnvType.CLIENT)
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

    @Environment(value=EnvType.CLIENT)
    public boolean levelExists(String name) {
        return Files.isDirectory(this.savesDirectory.resolve(name), new LinkOption[0]);
    }

    @Environment(value=EnvType.CLIENT)
    public Path getSavesDirectory() {
        return this.savesDirectory;
    }

    @Environment(value=EnvType.CLIENT)
    public Path getBackupsDirectory() {
        return this.backupsDirectory;
    }

    public Session createSession(String directoryName) throws IOException {
        return new Session(directoryName);
    }

    public class Session
    implements AutoCloseable {
        private final SessionLock lock;
        private final Path directory;
        private final String directoryName;
        private final Map<WorldSavePath, Path> field_24190 = Maps.newHashMap();

        public Session(String string) throws IOException {
            this.directoryName = string;
            this.directory = LevelStorage.this.savesDirectory.resolve(string);
            this.lock = SessionLock.create(this.directory);
        }

        public String getDirectoryName() {
            return this.directoryName;
        }

        public Path getDirectory(WorldSavePath worldSavePath2) {
            return this.field_24190.computeIfAbsent(worldSavePath2, worldSavePath -> this.directory.resolve(worldSavePath.getRelativePath()));
        }

        public File method_27424(RegistryKey<DimensionType> registryKey) {
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
            SaveProperties saveProperties = this.readLevelProperties();
            return saveProperties != null && saveProperties.getVersion() != LevelStorage.this.getCurrentVersion();
        }

        public boolean convert(ProgressListener progressListener) {
            this.checkValid();
            return AnvilLevelStorage.convertLevel(this, progressListener);
        }

        @Nullable
        public SaveProperties readLevelProperties() {
            this.checkValid();
            return (SaveProperties)LevelStorage.this.readLevelProperties(this.directory.toFile(), (file, dataFixer) -> LevelStorage.readLevelProperties(file, dataFixer));
        }

        public void method_27425(SaveProperties saveProperties) {
            this.method_27426(saveProperties, null);
        }

        public void method_27426(SaveProperties saveProperties, @Nullable CompoundTag compoundTag) {
            File file = this.directory.toFile();
            CompoundTag compoundTag2 = saveProperties.cloneWorldTag(compoundTag);
            CompoundTag compoundTag3 = new CompoundTag();
            compoundTag3.put("Data", compoundTag2);
            try {
                File file2 = File.createTempFile("level", ".dat", file);
                NbtIo.writeCompressed(compoundTag3, new FileOutputStream(file2));
                File file3 = new File(file, "level.dat_old");
                File file4 = new File(file, "level.dat");
                Util.method_27760(file4, file2, file3);
            } catch (Exception exception) {
                LOGGER.error("Failed to save level {}", (Object)file, (Object)exception);
            }
        }

        public File getIconFile() {
            this.checkValid();
            return this.directory.resolve("icon.png").toFile();
        }

        @Environment(value=EnvType.CLIENT)
        public void deleteSessionLock() throws IOException {
            this.checkValid();
            final Path path = this.directory.resolve("session.lock");
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
                        public /* synthetic */ FileVisitResult postVisitDirectory(Object object, IOException iOException) throws IOException {
                            return this.postVisitDirectory((Path)object, iOException);
                        }

                        @Override
                        public /* synthetic */ FileVisitResult visitFile(Object object, BasicFileAttributes basicFileAttributes) throws IOException {
                            return this.visitFile((Path)object, basicFileAttributes);
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

        @Environment(value=EnvType.CLIENT)
        public void save(String name) throws IOException {
            this.checkValid();
            File file = new File(LevelStorage.this.savesDirectory.toFile(), this.directoryName);
            if (!file.exists()) {
                return;
            }
            File file2 = new File(file, "level.dat");
            if (file2.exists()) {
                CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file2));
                CompoundTag compoundTag2 = compoundTag.getCompound("Data");
                compoundTag2.putString("LevelName", name);
                NbtIo.writeCompressed(compoundTag, new FileOutputStream(file2));
            }
        }

        @Environment(value=EnvType.CLIENT)
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
                    public /* synthetic */ FileVisitResult visitFile(Object object, BasicFileAttributes basicFileAttributes) throws IOException {
                        return this.visitFile((Path)object, basicFileAttributes);
                    }
                });
            }
            return Files.size(path2);
        }

        @Environment(value=EnvType.CLIENT)
        public DataResult<String> method_29019() {
            this.checkValid();
            File file = this.directory.toFile();
            LevelSummary levelSummary = (LevelSummary)LevelStorage.this.readLevelProperties(file, LevelStorage.this.method_29014(file, false));
            if (levelSummary == null) {
                return DataResult.error("Could not parse level data!");
            }
            DataResult<JsonElement> dataResult = GeneratorOptions.CODEC.encodeStart(JsonOps.INSTANCE, levelSummary.method_29021());
            return dataResult.flatMap(jsonElement -> {
                Path path = this.directory.resolve("worldgen_settings_export.json");
                try (JsonWriter jsonWriter = field_25021.newJsonWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8, new OpenOption[0]));){
                    field_25021.toJson((JsonElement)jsonElement, jsonWriter);
                } catch (JsonIOException | IOException exception) {
                    return DataResult.error("Error writing file: " + exception.getMessage());
                }
                return DataResult.success(path.toString());
            });
        }

        @Override
        public void close() throws IOException {
            this.lock.close();
        }
    }
}

