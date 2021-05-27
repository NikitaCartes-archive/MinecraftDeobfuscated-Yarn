/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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

public class class_6397
implements Closeable {
    private static final Logger field_33864 = LogManager.getLogger();
    private final Path field_33865;
    private final Path field_33866;
    private final FileSystem field_33867;

    public class_6397(Path path) {
        this.field_33865 = path;
        this.field_33866 = path.resolveSibling(path.getFileName().toString() + "_tmp");
        try {
            this.field_33867 = Util.field_33859.newFileSystem(this.field_33866, ImmutableMap.of("create", "true"));
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }

    public void method_37163(Path path, String string) {
        try {
            Path path2 = this.field_33867.getPath(File.separator, new String[0]);
            Path path3 = path2.resolve(path.toString());
            Files.createDirectories(path3.getParent(), new FileAttribute[0]);
            Files.write(path3, string.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }

    public void method_37162(Path path, File file) {
        try {
            Path path2 = this.field_33867.getPath(File.separator, new String[0]);
            Path path3 = path2.resolve(path.toString());
            Files.createDirectories(path3.getParent(), new FileAttribute[0]);
            Files.copy(file.toPath(), path3, new CopyOption[0]);
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }

    public void method_37161(Path path2) {
        try {
            Path path22 = this.field_33867.getPath(File.separator, new String[0]);
            if (Files.isRegularFile(path2, new LinkOption[0])) {
                Path path3 = path22.resolve(path2.getParent().relativize(path2).toString());
                Files.copy(path3, path2, new CopyOption[0]);
                return;
            }
            try (Stream<Path> stream = Files.find(path2, Integer.MAX_VALUE, (path, basicFileAttributes) -> basicFileAttributes.isRegularFile(), new FileVisitOption[0]);){
                for (Path path4 : stream.collect(Collectors.toList())) {
                    Path path5 = path22.resolve(path2.relativize(path4).toString());
                    Files.createDirectories(path5.getParent(), new FileAttribute[0]);
                    Files.copy(path4, path5, new CopyOption[0]);
                }
            }
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }

    @Override
    public void close() {
        try {
            this.field_33867.close();
            Files.move(this.field_33866, this.field_33865, new CopyOption[0]);
            field_33864.info("Compressed to {}", (Object)this.field_33865);
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }
}

