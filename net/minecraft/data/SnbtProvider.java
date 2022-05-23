/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SnbtProvider
implements DataProvider {
    @Nullable
    private static final Path DEBUG_OUTPUT_DIRECTORY = null;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DataGenerator root;
    private final List<Tweaker> write = Lists.newArrayList();

    public SnbtProvider(DataGenerator generator) {
        this.root = generator;
    }

    public SnbtProvider addWriter(Tweaker tweaker) {
        this.write.add(tweaker);
        return this;
    }

    private NbtCompound write(String key, NbtCompound compound) {
        NbtCompound nbtCompound = compound;
        for (Tweaker tweaker : this.write) {
            nbtCompound = tweaker.write(key, nbtCompound);
        }
        return nbtCompound;
    }

    @Override
    public void run(DataWriter writer) throws IOException {
        Path path2 = this.root.getOutput();
        ArrayList<CompletableFuture> list = Lists.newArrayList();
        for (Path path22 : this.root.getInputs()) {
            Files.walk(path22, new FileVisitOption[0]).filter(path -> path.toString().endsWith(".snbt")).forEach(path -> list.add(CompletableFuture.supplyAsync(() -> this.toCompressedNbt((Path)path, this.getFileName(path22, (Path)path)), Util.getMainWorkerExecutor())));
        }
        boolean bl = false;
        for (CompletableFuture completableFuture : list) {
            try {
                this.write(writer, (CompressedData)completableFuture.get(), path2);
            } catch (Exception exception) {
                LOGGER.error("Failed to process structure", exception);
                bl = true;
            }
        }
        if (bl) {
            throw new IllegalStateException("Failed to convert all structures, aborting");
        }
    }

    @Override
    public String getName() {
        return "SNBT -> NBT";
    }

    private String getFileName(Path root, Path file) {
        String string = root.relativize(file).toString().replaceAll("\\\\", "/");
        return string.substring(0, string.length() - ".snbt".length());
    }

    private CompressedData toCompressedNbt(Path path, String name) {
        CompressedData compressedData;
        block8: {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            try {
                String string = IOUtils.toString(bufferedReader);
                NbtCompound nbtCompound = this.write(name, NbtHelper.fromNbtProviderString(string));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
                NbtIo.writeCompressed(nbtCompound, hashingOutputStream);
                byte[] bs = byteArrayOutputStream.toByteArray();
                HashCode hashCode = hashingOutputStream.hash();
                String string2 = DEBUG_OUTPUT_DIRECTORY != null ? NbtHelper.toNbtProviderString(nbtCompound) : null;
                compressedData = new CompressedData(name, bs, string2, hashCode);
                if (bufferedReader == null) break block8;
            } catch (Throwable throwable) {
                try {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (Throwable throwable3) {
                    throw new CompressionException(path, throwable3);
                }
            }
            bufferedReader.close();
        }
        return compressedData;
    }

    private void write(DataWriter cache, CompressedData data, Path root) {
        Path path;
        if (data.snbtContent != null) {
            path = DEBUG_OUTPUT_DIRECTORY.resolve(data.name + ".snbt");
            try {
                NbtProvider.writeTo(DataWriter.UNCACHED, path, data.snbtContent);
            } catch (IOException iOException) {
                LOGGER.error("Couldn't write structure SNBT {} at {}", data.name, path, iOException);
            }
        }
        path = root.resolve(data.name + ".nbt");
        try {
            cache.write(path, data.bytes, data.sha1);
        } catch (IOException iOException) {
            LOGGER.error("Couldn't write structure {} at {}", data.name, path, iOException);
        }
    }

    @FunctionalInterface
    public static interface Tweaker {
        public NbtCompound write(String var1, NbtCompound var2);
    }

    record CompressedData(String name, byte[] bytes, @Nullable String snbtContent, HashCode sha1) {
        @Nullable
        public String snbtContent() {
            return this.snbtContent;
        }
    }

    static class CompressionException
    extends RuntimeException {
        public CompressionException(Path path, Throwable cause) {
            super(path.toAbsolutePath().toString(), cause);
        }
    }
}

