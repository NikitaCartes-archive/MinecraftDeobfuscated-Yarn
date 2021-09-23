/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A ZIP compressor builds up a ZIP file. It completes the ZIP file when it is
 * {@linkplain #close() closed}. All its methods and constructors throw
 * {@link java.io.UncheckedIOException} when an I/O error occurs.
 * 
 * @implSpec The compressor writes the contents of the ZIP to a {@link #temp} file
 * first; then, it replaces the desired {@link #file} with the temp file when
 * closed.
 */
public class ZipCompressor
implements Closeable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path file;
    private final Path temp;
    private final FileSystem zip;

    /**
     * Creates a ZIP compressor.
     * 
     * @param file the path of the ZIP file
     */
    public ZipCompressor(Path file) {
        this.file = file;
        this.temp = file.resolveSibling(file.getFileName().toString() + "_tmp");
        try {
            this.zip = Util.JAR_FILE_SYSTEM_PROVIDER.newFileSystem(this.temp, ImmutableMap.of("create", "true"));
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }

    /**
     * Writes the {@code content}, in UTF-8 encoding, to the {@code target} path
     * within the ZIP.
     * 
     * <p>The {@code target} should be a relative path, as it will be resolved
     * against the root of the ZIP.
     * 
     * @param target the target path in the ZIP
     * @param content the file content to write in UTF-8
     */
    public void write(Path target, String content) {
        try {
            Path path = this.zip.getPath(File.separator, new String[0]);
            Path path2 = path.resolve(target.toString());
            Files.createDirectories(path2.getParent(), new FileAttribute[0]);
            Files.write(path2, content.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }

    /**
     * Copies a {@code source} file to the {@code target} path within the ZIP.
     * 
     * <p>If the {@code source} is a directory, then an empty directory would be
     * copied. The {@code target} should be a relative path, as it will be resolved
     * against the root of the ZIP.
     * 
     * @param source the source file to copy
     * @param target the target path in the ZIP
     */
    public void copy(Path target, File source) {
        try {
            Path path = this.zip.getPath(File.separator, new String[0]);
            Path path2 = path.resolve(target.toString());
            Files.createDirectories(path2.getParent(), new FileAttribute[0]);
            Files.copy(source.toPath(), path2, new CopyOption[0]);
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }

    /**
     * Copies the {@code source} file or directory to the root of the ZIP.
     * 
     * @param source the source file or directory to copy
     */
    public void copyAll(Path source) {
        try {
            Path path2 = this.zip.getPath(File.separator, new String[0]);
            if (Files.isRegularFile(source, new LinkOption[0])) {
                Path path22 = path2.resolve(source.getParent().relativize(source).toString());
                Files.copy(path22, source, new CopyOption[0]);
                return;
            }
            try (Stream<Path> stream = Files.find(source, Integer.MAX_VALUE, (path, attributes) -> attributes.isRegularFile(), new FileVisitOption[0]);){
                for (Path path3 : stream.collect(Collectors.toList())) {
                    Path path4 = path2.resolve(source.relativize(path3).toString());
                    Files.createDirectories(path4.getParent(), new FileAttribute[0]);
                    Files.copy(path3, path4, new CopyOption[0]);
                }
            }
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }

    @Override
    public void close() {
        try {
            this.zip.close();
            Files.move(this.temp, this.file, new CopyOption[0]);
            LOGGER.info("Compressed to {}", (Object)this.file);
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }
}

