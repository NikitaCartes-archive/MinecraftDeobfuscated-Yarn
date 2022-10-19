/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.dev;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Collection;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NbtProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Iterable<Path> field_40660;
    private final DataOutput field_40661;

    public NbtProvider(DataOutput dataOutput, Collection<Path> collection) {
        this.field_40660 = collection;
        this.field_40661 = dataOutput;
    }

    @Override
    public void run(DataWriter writer) throws IOException {
        Path path2 = this.field_40661.getPath();
        for (Path path22 : this.field_40660) {
            Files.walk(path22, new FileVisitOption[0]).filter(path -> path.toString().endsWith(".nbt")).forEach(path -> NbtProvider.convertNbtToSnbt(writer, path, this.getLocation(path22, (Path)path), path2));
        }
    }

    @Override
    public String getName() {
        return "NBT to SNBT";
    }

    private String getLocation(Path targetPath, Path rootPath) {
        String string = targetPath.relativize(rootPath).toString().replaceAll("\\\\", "/");
        return string.substring(0, string.length() - ".nbt".length());
    }

    @Nullable
    public static Path convertNbtToSnbt(DataWriter writer, Path inputPath, String filename, Path outputPath) {
        Path path;
        block8: {
            InputStream inputStream = Files.newInputStream(inputPath, new OpenOption[0]);
            try {
                Path path2 = outputPath.resolve(filename + ".snbt");
                NbtProvider.writeTo(writer, path2, NbtHelper.toNbtProviderString(NbtIo.readCompressed(inputStream)));
                LOGGER.info("Converted {} from NBT to SNBT", (Object)filename);
                path = path2;
                if (inputStream == null) break block8;
            } catch (Throwable throwable) {
                try {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (IOException iOException) {
                    LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", filename, inputPath, iOException);
                    return null;
                }
            }
            inputStream.close();
        }
        return path;
    }

    public static void writeTo(DataWriter writer, Path path, String content) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
        hashingOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
        hashingOutputStream.write(10);
        writer.write(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
    }
}

