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
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NbtProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DataGenerator root;

    public NbtProvider(DataGenerator root) {
        this.root = root;
    }

    @Override
    public void run(DataWriter cache) throws IOException {
        Path path2 = this.root.getOutput();
        for (Path path22 : this.root.getInputs()) {
            Files.walk(path22, new FileVisitOption[0]).filter(path -> path.toString().endsWith(".nbt")).forEach(path3 -> NbtProvider.convertNbtToSnbt(cache, path3, this.getLocation(path22, (Path)path3), path2));
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
    public static Path convertNbtToSnbt(DataWriter dataWriter, Path path, String string, Path path2) {
        Path path3;
        block8: {
            InputStream inputStream = Files.newInputStream(path, new OpenOption[0]);
            try {
                Path path32 = path2.resolve(string + ".snbt");
                NbtProvider.writeTo(dataWriter, path32, NbtHelper.toNbtProviderString(NbtIo.readCompressed(inputStream)));
                LOGGER.info("Converted {} from NBT to SNBT", (Object)string);
                path3 = path32;
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
                    LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", string, path, iOException);
                    return null;
                }
            }
            inputStream.close();
        }
        return path3;
    }

    public static void writeTo(DataWriter dataWriter, Path path, String string) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
        hashingOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
        hashingOutputStream.write(10);
        dataWriter.write(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
    }
}

