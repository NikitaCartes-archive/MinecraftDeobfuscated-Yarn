/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.AnvilLevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.SessionLock;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class LevelStorage {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
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

    @Environment(value=EnvType.CLIENT)
    public List<LevelSummary> getLevelList() throws LevelStorageException {
        File[] files;
        if (!Files.isDirectory(this.savesDirectory, new LinkOption[0])) {
            throw new LevelStorageException(new TranslatableText("selectWorld.load_folder_access", new Object[0]).getString());
        }
        ArrayList<LevelSummary> list = Lists.newArrayList();
        for (File file : files = this.savesDirectory.toFile().listFiles()) {
            boolean bl;
            if (!file.isDirectory()) continue;
            String string = file.getName();
            try {
                bl = SessionLock.isLocked(file.toPath());
            } catch (Exception exception) {
                LOGGER.warn("Failed to read {} lock", (Object)file, (Object)exception);
                continue;
            }
            LevelProperties levelProperties = this.readLevelProperties(file);
            if (levelProperties == null || levelProperties.getVersion() != 19132 && levelProperties.getVersion() != 19133) continue;
            boolean bl2 = levelProperties.getVersion() != this.getCurrentVersion();
            String string2 = levelProperties.getLevelName();
            if (StringUtils.isEmpty(string2)) {
                string2 = string;
            }
            long l = 0L;
            File file2 = new File(file, "icon.png");
            list.add(new LevelSummary(levelProperties, string, string2, 0L, bl2, bl, file2));
        }
        return list;
    }

    private int getCurrentVersion() {
        return 19133;
    }

    @Nullable
    private LevelProperties readLevelProperties(File file) {
        LevelProperties levelProperties;
        if (!file.exists()) {
            return null;
        }
        File file2 = new File(file, "level.dat");
        if (file2.exists() && (levelProperties = LevelStorage.readLevelProperties(file2, this.dataFixer)) != null) {
            return levelProperties;
        }
        file2 = new File(file, "level.dat_old");
        if (file2.exists()) {
            return LevelStorage.readLevelProperties(file2, this.dataFixer);
        }
        return null;
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
        } catch (Exception exception) {
            LOGGER.error("Exception reading {}", (Object)file, (Object)exception);
            return null;
        }
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

        public Session(String directoryName) throws IOException {
            this.directoryName = directoryName;
            this.directory = LevelStorage.this.savesDirectory.resolve(directoryName);
            this.lock = SessionLock.create(this.directory);
        }

        public String getDirectoryName() {
            return this.directoryName;
        }

        public Path getDirectory() {
            return this.directory;
        }

        private void checkValid() {
            if (!this.lock.isValid()) {
                throw new IllegalStateException("Lock is no longer valid");
            }
        }

        public WorldSaveHandler createSaveHandler(@Nullable MinecraftServer server) {
            this.checkValid();
            return new WorldSaveHandler(LevelStorage.this.savesDirectory.toFile(), this.directoryName, server, LevelStorage.this.dataFixer);
        }

        public boolean needsConversion() {
            LevelProperties levelProperties = this.readLevelProperties();
            return levelProperties != null && levelProperties.getVersion() != LevelStorage.this.getCurrentVersion();
        }

        public boolean convert(ProgressListener progressListener) {
            this.checkValid();
            return AnvilLevelStorage.convertLevel(this, progressListener);
        }

        @Nullable
        public LevelProperties readLevelProperties() {
            this.checkValid();
            return LevelStorage.this.readLevelProperties(this.directory.toFile());
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
            final Path path = this.getDirectory();
            String string = LocalDateTime.now().format(TIME_FORMATTER) + "_" + this.directoryName;
            Path path2 = LevelStorage.this.getBackupsDirectory();
            try {
                Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath(new LinkOption[0]) : path2, new FileAttribute[0]);
            } catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            Path path3 = path2.resolve(FileNameUtil.getNextUniqueName(path2, string, ".zip"));
            try (final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path3, new OpenOption[0])));){
                final Path path4 = Paths.get(this.directoryName, new String[0]);
                Files.walkFileTree(path, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                    @Override
                    public FileVisitResult visitFile(Path path2, BasicFileAttributes basicFileAttributes) throws IOException {
                        if (path2.endsWith("session.lock")) {
                            return FileVisitResult.CONTINUE;
                        }
                        String string = path4.resolve(path.relativize(path2)).toString().replace('\\', '/');
                        ZipEntry zipEntry = new ZipEntry(string);
                        zipOutputStream.putNextEntry(zipEntry);
                        com.google.common.io.Files.asByteSource(path2.toFile()).copyTo(zipOutputStream);
                        zipOutputStream.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public /* synthetic */ FileVisitResult visitFile(Object object, BasicFileAttributes basicFileAttributes) throws IOException {
                        return this.visitFile((Path)object, basicFileAttributes);
                    }
                });
            }
            return Files.size(path3);
        }

        @Override
        public void close() throws IOException {
            this.lock.close();
        }
    }
}

