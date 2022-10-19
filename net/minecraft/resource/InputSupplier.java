/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@FunctionalInterface
public interface InputSupplier<T> {
    public static InputSupplier<InputStream> create(Path path) {
        return () -> Files.newInputStream(path, new OpenOption[0]);
    }

    public static InputSupplier<InputStream> create(ZipFile zipFile, ZipEntry zipEntry) {
        return () -> zipFile.getInputStream(zipEntry);
    }

    public T get() throws IOException;
}

