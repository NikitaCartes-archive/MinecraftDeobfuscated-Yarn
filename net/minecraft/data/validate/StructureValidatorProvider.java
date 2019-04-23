/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.validate;

import com.google.common.base.Charsets;
import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.stream.Stream;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.datafixers.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.structure.Structure;
import net.minecraft.util.TagHelper;
import org.apache.commons.io.IOUtils;

public class StructureValidatorProvider
implements DataProvider {
    private final DataGenerator generator;

    public StructureValidatorProvider(DataGenerator dataGenerator) {
        this.generator = dataGenerator;
    }

    @Override
    public void run(DataCache dataCache) throws IOException {
        for (Path path : this.generator.getInputs()) {
            Path path2 = path.resolve("data/minecraft/structures/");
            if (!Files.isDirectory(path2, new LinkOption[0])) continue;
            StructureValidatorProvider.method_16879(Schemas.getFixer(), path2);
        }
    }

    @Override
    public String getName() {
        return "Structure validator";
    }

    private static void method_16879(DataFixer dataFixer, Path path2) throws IOException {
        try (Stream<Path> stream = Files.walk(path2, new FileVisitOption[0]);){
            stream.forEach(path -> {
                if (Files.isRegularFile(path, new LinkOption[0])) {
                    StructureValidatorProvider.method_16881(dataFixer, path);
                }
            });
        }
    }

    private static void method_16881(DataFixer dataFixer, Path path) {
        block4: {
            try {
                String string = path.getFileName().toString();
                if (string.endsWith(".snbt")) {
                    StructureValidatorProvider.method_16882(dataFixer, path);
                    break block4;
                }
                if (string.endsWith(".nbt")) {
                    StructureValidatorProvider.method_16883(dataFixer, path);
                    break block4;
                }
                throw new IllegalArgumentException("Unrecognized format of file");
            } catch (Exception exception) {
                throw new class_3844(path, (Throwable)exception);
            }
        }
    }

    private static void method_16882(DataFixer dataFixer, Path path) throws Exception {
        CompoundTag compoundTag;
        try (InputStream inputStream = Files.newInputStream(path, new OpenOption[0]);){
            String string = IOUtils.toString(inputStream, Charsets.UTF_8);
            compoundTag = StringNbtReader.parse(string);
        }
        StructureValidatorProvider.method_16878(dataFixer, StructureValidatorProvider.method_16880(compoundTag));
    }

    private static void method_16883(DataFixer dataFixer, Path path) throws Exception {
        CompoundTag compoundTag;
        try (InputStream inputStream = Files.newInputStream(path, new OpenOption[0]);){
            compoundTag = NbtIo.readCompressed(inputStream);
        }
        StructureValidatorProvider.method_16878(dataFixer, StructureValidatorProvider.method_16880(compoundTag));
    }

    private static CompoundTag method_16880(CompoundTag compoundTag) {
        if (!compoundTag.containsKey("DataVersion", 99)) {
            compoundTag.putInt("DataVersion", 500);
        }
        return compoundTag;
    }

    private static CompoundTag method_16878(DataFixer dataFixer, CompoundTag compoundTag) {
        Structure structure = new Structure();
        structure.fromTag(TagHelper.update(dataFixer, DataFixTypes.STRUCTURE, compoundTag, compoundTag.getInt("DataVersion")));
        return structure.toTag(new CompoundTag());
    }

    static class class_3844
    extends RuntimeException {
        public class_3844(Path path, Throwable throwable) {
            super("Failed to process file: " + path.toAbsolutePath().toString(), throwable);
        }
    }
}

