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
import net.minecraft.datafixers.DataFixTypes;
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

    public LevelStorage(Path path, Path path2, DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        try {
            Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath(new LinkOption[0]) : path, new FileAttribute[0]);
        } catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
        this.savesDirectory = path;
        this.backupsDirectory = path2;
    }

    @Environment(value=EnvType.CLIENT)
    public String getName() {
        return "Anvil";
    }

    @Environment(value=EnvType.CLIENT)
    public List<LevelSummary> getLevelList() throws LevelStorageException {
        File[] files;
        if (!Files.isDirectory(this.savesDirectory, new LinkOption[0])) {
            throw new LevelStorageException(new TranslatableText("selectWorld.load_folder_access", new Object[0]).getString());
        }
        ArrayList<LevelSummary> list = Lists.newArrayList();
        for (File file : files = this.savesDirectory.toFile().listFiles()) {
            String string;
            LevelProperties levelProperties;
            if (!file.isDirectory() || (levelProperties = this.getLevelProperties(string = file.getName())) == null || levelProperties.getVersion() != 19132 && levelProperties.getVersion() != 19133) continue;
            boolean bl = levelProperties.getVersion() != this.getCurrentVersion();
            String string2 = levelProperties.getLevelName();
            if (StringUtils.isEmpty(string2)) {
                string2 = string;
            }
            long l = 0L;
            list.add(new LevelSummary(levelProperties, string, string2, 0L, bl));
        }
        return list;
    }

    private int getCurrentVersion() {
        return 19133;
    }

    public WorldSaveHandler createSaveHandler(String string, @Nullable MinecraftServer minecraftServer) {
        return LevelStorage.createSaveHandler(this.savesDirectory, this.dataFixer, string, minecraftServer);
    }

    protected static WorldSaveHandler createSaveHandler(Path path, DataFixer dataFixer, String string, @Nullable MinecraftServer minecraftServer) {
        return new WorldSaveHandler(path.toFile(), string, minecraftServer, dataFixer);
    }

    public boolean requiresConversion(String string) {
        LevelProperties levelProperties = this.getLevelProperties(string);
        return levelProperties != null && levelProperties.getVersion() != this.getCurrentVersion();
    }

    public boolean convertLevel(String string, ProgressListener progressListener) {
        return AnvilLevelStorage.convertLevel(this.savesDirectory, this.dataFixer, string, progressListener);
    }

    @Nullable
    public LevelProperties getLevelProperties(String string) {
        return LevelStorage.getLevelProperties(this.savesDirectory, this.dataFixer, string);
    }

    @Nullable
    protected static LevelProperties getLevelProperties(Path path, DataFixer dataFixer, String string) {
        LevelProperties levelProperties;
        File file = new File(path.toFile(), string);
        if (!file.exists()) {
            return null;
        }
        File file2 = new File(file, "level.dat");
        if (file2.exists() && (levelProperties = LevelStorage.readLevelProperties(file2, dataFixer)) != null) {
            return levelProperties;
        }
        file2 = new File(file, "level.dat_old");
        if (file2.exists()) {
            return LevelStorage.readLevelProperties(file2, dataFixer);
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
    public void renameLevel(String string, String string2) {
        File file = new File(this.savesDirectory.toFile(), string);
        if (!file.exists()) {
            return;
        }
        File file2 = new File(file, "level.dat");
        if (file2.exists()) {
            try {
                CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file2));
                CompoundTag compoundTag2 = compoundTag.getCompound("Data");
                compoundTag2.putString("LevelName", string2);
                NbtIo.writeCompressed(compoundTag, new FileOutputStream(file2));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLevelNameValid(String string) {
        try {
            Path path = this.savesDirectory.resolve(string);
            Files.createDirectory(path, new FileAttribute[0]);
            Files.deleteIfExists(path);
            return true;
        } catch (IOException iOException) {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean deleteLevel(String string) {
        File file = new File(this.savesDirectory.toFile(), string);
        if (!file.exists()) {
            return true;
        }
        LOGGER.info("Deleting level {}", (Object)string);
        for (int i = 1; i <= 5; ++i) {
            LOGGER.info("Attempt {}...", (Object)i);
            if (LevelStorage.deleteFilesRecursively(file.listFiles())) break;
            LOGGER.warn("Unsuccessful in deleting contents.");
            if (i >= 5) continue;
            try {
                Thread.sleep(500L);
                continue;
            } catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        return file.delete();
    }

    @Environment(value=EnvType.CLIENT)
    private static boolean deleteFilesRecursively(File[] files) {
        for (File file : files) {
            LOGGER.debug("Deleting {}", (Object)file);
            if (file.isDirectory() && !LevelStorage.deleteFilesRecursively(file.listFiles())) {
                LOGGER.warn("Couldn't delete directory {}", (Object)file);
                return false;
            }
            if (file.delete()) continue;
            LOGGER.warn("Couldn't delete file {}", (Object)file);
            return false;
        }
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean levelExists(String string) {
        return Files.isDirectory(this.savesDirectory.resolve(string), new LinkOption[0]);
    }

    @Environment(value=EnvType.CLIENT)
    public Path getSavesDirectory() {
        return this.savesDirectory;
    }

    public File resolveFile(String string, String string2) {
        return this.savesDirectory.resolve(string).resolve(string2).toFile();
    }

    @Environment(value=EnvType.CLIENT)
    private Path resolvePath(String string) {
        return this.savesDirectory.resolve(string);
    }

    @Environment(value=EnvType.CLIENT)
    public Path getBackupsDirectory() {
        return this.backupsDirectory;
    }

    @Environment(value=EnvType.CLIENT)
    public long backupLevel(String string) throws IOException {
        final Path path = this.resolvePath(string);
        String string2 = LocalDateTime.now().format(TIME_FORMATTER) + "_" + string;
        Path path2 = this.getBackupsDirectory();
        try {
            Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath(new LinkOption[0]) : path2, new FileAttribute[0]);
        } catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
        Path path3 = path2.resolve(FileNameUtil.getNextUniqueName(path2, string2, ".zip"));
        try (final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path3, new OpenOption[0])));){
            final Path path4 = Paths.get(string, new String[0]);
            Files.walkFileTree(path, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                public FileVisitResult method_246(Path path2, BasicFileAttributes basicFileAttributes) throws IOException {
                    String string = path4.resolve(path.relativize(path2)).toString().replace('\\', '/');
                    ZipEntry zipEntry = new ZipEntry(string);
                    zipOutputStream.putNextEntry(zipEntry);
                    com.google.common.io.Files.asByteSource(path2.toFile()).copyTo(zipOutputStream);
                    zipOutputStream.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public /* synthetic */ FileVisitResult visitFile(Object object, BasicFileAttributes basicFileAttributes) throws IOException {
                    return this.method_246((Path)object, basicFileAttributes);
                }
            });
        }
        return Files.size(path3);
    }
}

