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
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Objects;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SnbtProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator root;
    private final List<class_4460> field_20309 = Lists.newArrayList();

    public SnbtProvider(DataGenerator dataGenerator) {
        this.root = dataGenerator;
    }

    public SnbtProvider method_21672(class_4460 arg) {
        this.field_20309.add(arg);
        return this;
    }

    private CompoundTag method_21673(String string, CompoundTag compoundTag) {
        CompoundTag compoundTag2 = compoundTag;
        for (class_4460 lv : this.field_20309) {
            compoundTag2 = lv.method_21674(string, compoundTag2);
        }
        return compoundTag2;
    }

    @Override
    public void run(DataCache dataCache) throws IOException {
        Path path2 = this.root.getOutput();
        for (Path path22 : this.root.getInputs()) {
            Files.walk(path22, new FileVisitOption[0]).filter(path -> path.toString().endsWith(".snbt")).forEach(path3 -> this.method_10497(dataCache, (Path)path3, this.method_10500(path22, (Path)path3), path2));
        }
    }

    @Override
    public String getName() {
        return "SNBT -> NBT";
    }

    private String method_10500(Path path, Path path2) {
        String string = path.relativize(path2).toString().replaceAll("\\\\", "/");
        return string.substring(0, string.length() - ".snbt".length());
    }

    private void method_10497(DataCache dataCache, Path path, String string, Path path2) {
        try {
            Path path3 = path2.resolve(string + ".nbt");
            try (BufferedReader bufferedReader = Files.newBufferedReader(path);){
                String string2 = IOUtils.toString(bufferedReader);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                NbtIo.writeCompressed(this.method_21673(string, StringNbtReader.parse(string2)), byteArrayOutputStream);
                String string3 = SHA1.hashBytes(byteArrayOutputStream.toByteArray()).toString();
                if (!Objects.equals(dataCache.getOldSha1(path3), string3) || !Files.exists(path3, new LinkOption[0])) {
                    Files.createDirectories(path3.getParent(), new FileAttribute[0]);
                    try (OutputStream outputStream = Files.newOutputStream(path3, new OpenOption[0]);){
                        outputStream.write(byteArrayOutputStream.toByteArray());
                    }
                }
                dataCache.updateSha1(path3, string3);
            }
        } catch (CommandSyntaxException commandSyntaxException) {
            LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", (Object)string, (Object)path, (Object)commandSyntaxException);
        } catch (IOException iOException) {
            LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", (Object)string, (Object)path, (Object)iOException);
        }
    }

    @FunctionalInterface
    public static interface class_4460 {
        public CompoundTag method_21674(String var1, CompoundTag var2);
    }
}

