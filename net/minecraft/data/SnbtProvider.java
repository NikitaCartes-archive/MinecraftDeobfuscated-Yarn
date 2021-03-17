/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SnbtProvider
implements DataProvider {
    @Nullable
    private static final Path field_24615 = null;
    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator root;
    private final List<Tweaker> write = Lists.newArrayList();

    public SnbtProvider(DataGenerator dataGenerator) {
        this.root = dataGenerator;
    }

    public SnbtProvider addWriter(Tweaker tweaker) {
        this.write.add(tweaker);
        return this;
    }

    private NbtCompound write(String string, NbtCompound nbtCompound) {
        NbtCompound nbtCompound2 = nbtCompound;
        for (Tweaker tweaker : this.write) {
            nbtCompound2 = tweaker.write(string, nbtCompound2);
        }
        return nbtCompound2;
    }

    @Override
    public void run(DataCache cache) throws IOException {
        Path path3 = this.root.getOutput();
        ArrayList<CompletableFuture> list = Lists.newArrayList();
        for (Path path22 : this.root.getInputs()) {
            Files.walk(path22, new FileVisitOption[0]).filter(path -> path.toString().endsWith(".snbt")).forEach(path2 -> list.add(CompletableFuture.supplyAsync(() -> this.toCompressedNbt((Path)path2, this.getFileName(path22, (Path)path2)), Util.getMainWorkerExecutor())));
        }
        boolean bl = false;
        for (CompletableFuture completableFuture : list) {
            try {
                this.write(cache, (CompressedData)completableFuture.get(), path3);
            } catch (Exception exception) {
                LOGGER.error("Failed to process structure", (Throwable)exception);
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

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private CompressedData toCompressedNbt(Path path, String name) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(path);){
            String string = IOUtils.toString(bufferedReader);
            NbtCompound nbtCompound = this.write(name, NbtHelper.method_32260(string));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            NbtIo.writeCompressed(nbtCompound, byteArrayOutputStream);
            byte[] bs = byteArrayOutputStream.toByteArray();
            String string2 = SHA1.hashBytes(bs).toString();
            String string3 = field_24615 != null ? NbtHelper.toPrettyPrintedString(nbtCompound) : null;
            CompressedData compressedData = new CompressedData(name, bs, string3, string2);
            return compressedData;
        } catch (Throwable throwable) {
            throw new CompressionException(path, throwable);
        }
    }

    private void write(DataCache dataCache, CompressedData compressedData, Path path) {
        Path path2;
        if (compressedData.field_24616 != null) {
            path2 = field_24615.resolve(compressedData.name + ".snbt");
            try {
                NbtProvider.writeTo(path2, compressedData.field_24616);
            } catch (IOException iOException) {
                LOGGER.error("Couldn't write structure SNBT {} at {}", (Object)compressedData.name, (Object)path2, (Object)iOException);
            }
        }
        path2 = path.resolve(compressedData.name + ".nbt");
        try {
            if (!Objects.equals(dataCache.getOldSha1(path2), compressedData.sha1) || !Files.exists(path2, new LinkOption[0])) {
                Files.createDirectories(path2.getParent(), new FileAttribute[0]);
                try (OutputStream outputStream = Files.newOutputStream(path2, new OpenOption[0]);){
                    outputStream.write(compressedData.bytes);
                }
            }
            dataCache.updateSha1(path2, compressedData.sha1);
        } catch (IOException iOException) {
            LOGGER.error("Couldn't write structure {} at {}", (Object)compressedData.name, (Object)path2, (Object)iOException);
        }
    }

    static class CompressionException
    extends RuntimeException {
        public CompressionException(Path path, Throwable cause) {
            super(path.toAbsolutePath().toString(), cause);
        }
    }

    @FunctionalInterface
    public static interface Tweaker {
        public NbtCompound write(String var1, NbtCompound var2);
    }

    static class CompressedData {
        private final String name;
        private final byte[] bytes;
        @Nullable
        private final String field_24616;
        private final String sha1;

        public CompressedData(String name, byte[] bytes, @Nullable String sha1, String string) {
            this.name = name;
            this.bytes = bytes;
            this.field_24616 = sha1;
            this.sha1 = string;
        }
    }
}

