/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.resource.fs.ResourceFileSystem;
import net.minecraft.text.Text;
import net.minecraft.util.PathUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class FileResourcePackProvider
implements ResourcePackProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path packsDir;
    private final ResourceType type;
    private final ResourcePackSource source;

    public FileResourcePackProvider(Path packsDir, ResourceType type, ResourcePackSource source) {
        this.packsDir = packsDir;
        this.type = type;
        this.source = source;
    }

    private static String getFileName(Path path) {
        return path.getFileName().toString();
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        try {
            PathUtil.createDirectories(this.packsDir);
            FileResourcePackProvider.forEachProfile(this.packsDir, false, (path, packFactory) -> {
                String string = FileResourcePackProvider.getFileName(path);
                ResourcePackProfile resourcePackProfile = ResourcePackProfile.create("file/" + string, Text.literal(string), false, packFactory, this.type, ResourcePackProfile.InsertionPosition.TOP, this.source);
                if (resourcePackProfile != null) {
                    profileAdder.accept(resourcePackProfile);
                }
            });
        } catch (IOException iOException) {
            LOGGER.warn("Failed to list packs in {}", (Object)this.packsDir, (Object)iOException);
        }
    }

    public static void forEachProfile(Path packsDir, boolean alwaysStable, BiConsumer<Path, ResourcePackProfile.PackFactory> consumer) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(packsDir);){
            for (Path path : directoryStream) {
                ResourcePackProfile.PackFactory packFactory = FileResourcePackProvider.getFactory(path, alwaysStable);
                if (packFactory == null) continue;
                consumer.accept(path, packFactory);
            }
        }
    }

    @Nullable
    public static ResourcePackProfile.PackFactory getFactory(Path path, boolean alwaysStable) {
        FileSystem fileSystem;
        BasicFileAttributes basicFileAttributes;
        try {
            basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class, new LinkOption[0]);
        } catch (NoSuchFileException noSuchFileException) {
            return null;
        } catch (IOException iOException) {
            LOGGER.warn("Failed to read properties of '{}', ignoring", (Object)path, (Object)iOException);
            return null;
        }
        if (basicFileAttributes.isDirectory() && Files.isRegularFile(path.resolve("pack.mcmeta"), new LinkOption[0])) {
            return name -> new DirectoryResourcePack(name, path, alwaysStable);
        }
        if (basicFileAttributes.isRegularFile() && path.getFileName().toString().endsWith(".zip") && ((fileSystem = path.getFileSystem()) == FileSystems.getDefault() || fileSystem instanceof ResourceFileSystem)) {
            File file = path.toFile();
            return name -> new ZipResourcePack(name, file, alwaysStable);
        }
        LOGGER.info("Found non-pack entry '{}', ignoring", (Object)path);
        return null;
    }
}

