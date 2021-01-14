/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
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

    private NbtCompound write(String key, NbtCompound compound) {
        NbtCompound nbtCompound = compound;
        for (Tweaker tweaker : this.write) {
            nbtCompound = tweaker.write(key, nbtCompound);
        }
        return nbtCompound;
    }

    @Override
    public void run(DataCache cache) throws IOException {
        Path path3 = this.root.getOutput();
        ArrayList list = Lists.newArrayList();
        for (Path path22 : this.root.getInputs()) {
            Files.walk(path22, new FileVisitOption[0]).filter(path -> path.toString().endsWith(".snbt")).forEach(path2 -> list.add(CompletableFuture.supplyAsync(() -> this.toCompressedNbt((Path)path2, this.getFileName(path22, (Path)path2)), Util.getMainWorkerExecutor())));
        }
        Util.combine(list).join().stream().filter(Objects::nonNull).forEach(compressedData -> this.write(cache, (CompressedData)compressedData, path3));
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
    @Nullable
    private CompressedData toCompressedNbt(Path path, String name) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(path);){
            String string = IOUtils.toString(bufferedReader);
            NbtCompound nbtCompound = this.write(name, StringNbtReader.parse(string));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            NbtIo.writeCompressed(nbtCompound, byteArrayOutputStream);
            byte[] bs = byteArrayOutputStream.toByteArray();
            String string2 = SHA1.hashBytes(bs).toString();
            String string3 = field_24615 != null ? nbtCompound.toText("    ", 0).getString() + "\n" : null;
            CompressedData compressedData = new CompressedData(name, bs, string3, string2);
            return compressedData;
        } catch (CommandSyntaxException commandSyntaxException) {
            LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", (Object)name, (Object)path, (Object)commandSyntaxException);
            return null;
        } catch (IOException iOException) {
            LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", (Object)name, (Object)path, (Object)iOException);
        }
        return null;
    }

    private void write(DataCache cache, CompressedData data, Path root) {
        Path path;
        if (data.field_24616 != null) {
            path = field_24615.resolve(data.name + ".snbt");
            try {
                FileUtils.write(path.toFile(), (CharSequence)data.field_24616, StandardCharsets.UTF_8);
            } catch (IOException iOException) {
                LOGGER.error("Couldn't write structure SNBT {} at {}", (Object)data.name, (Object)path, (Object)iOException);
            }
        }
        path = root.resolve(data.name + ".nbt");
        try {
            if (!Objects.equals(cache.getOldSha1(path), data.sha1) || !Files.exists(path, new LinkOption[0])) {
                Files.createDirectories(path.getParent(), new FileAttribute[0]);
                try (OutputStream outputStream = Files.newOutputStream(path, new OpenOption[0]);){
                    outputStream.write(data.bytes);
                }
            }
            cache.updateSha1(path, data.sha1);
        } catch (IOException iOException) {
            LOGGER.error("Couldn't write structure {} at {}", (Object)data.name, (Object)path, (Object)iOException);
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

