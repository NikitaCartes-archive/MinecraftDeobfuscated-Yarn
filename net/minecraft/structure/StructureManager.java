/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.Structure;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String STRUCTURES_DIRECTORY = "structures";
    private static final String field_39416 = "gameteststructures";
    private static final String NBT_FILE_EXTENSION = ".nbt";
    private static final String SNBT_FILE_EXTENSION = ".snbt";
    private final Map<Identifier, Optional<Structure>> structures = Maps.newConcurrentMap();
    private final DataFixer dataFixer;
    private ResourceManager resourceManager;
    private final Path generatedPath;
    private final List<class_7514> field_39417;

    public StructureManager(ResourceManager resourceManager, LevelStorage.Session session, DataFixer dataFixer) {
        this.resourceManager = resourceManager;
        this.dataFixer = dataFixer;
        this.generatedPath = session.getDirectory(WorldSavePath.GENERATED).normalize();
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add(new class_7514(this::loadStructureFromFile, this::method_44243));
        if (SharedConstants.isDevelopment) {
            builder.add(new class_7514(this::method_44246, this::method_44241));
        }
        builder.add(new class_7514(this::loadStructureFromResource, this::method_44239));
        this.field_39417 = builder.build();
    }

    public Structure getStructureOrBlank(Identifier id) {
        Optional<Structure> optional = this.getStructure(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        Structure structure = new Structure();
        this.structures.put(id, Optional.of(structure));
        return structure;
    }

    public Optional<Structure> getStructure(Identifier id) {
        return this.structures.computeIfAbsent(id, this::method_44245);
    }

    public Stream<Identifier> method_44226() {
        return this.field_39417.stream().flatMap(arg -> arg.lister().get()).distinct();
    }

    private Optional<Structure> method_44245(Identifier identifier) {
        for (class_7514 lv : this.field_39417) {
            try {
                Optional<Structure> optional = lv.loader().apply(identifier);
                if (!optional.isPresent()) continue;
                return optional;
            } catch (Exception exception) {
            }
        }
        return Optional.empty();
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.structures.clear();
    }

    private Optional<Structure> loadStructureFromResource(Identifier id) {
        Identifier identifier = new Identifier(id.getNamespace(), "structures/" + id.getPath() + NBT_FILE_EXTENSION);
        return this.method_44231(() -> this.resourceManager.open(identifier), throwable -> LOGGER.error("Couldn't load structure {}", (Object)id, throwable));
    }

    private Stream<Identifier> method_44239() {
        return this.resourceManager.findResources(STRUCTURES_DIRECTORY, identifier -> true).keySet().stream().map(identifier -> new Identifier(identifier.getNamespace(), identifier.getPath().substring(STRUCTURES_DIRECTORY.length() + 1, identifier.getPath().length() - NBT_FILE_EXTENSION.length())));
    }

    private Optional<Structure> method_44246(Identifier identifier) {
        return this.method_44230(identifier, Paths.get(field_39416, new String[0]));
    }

    private Stream<Identifier> method_44241() {
        return this.method_44236(Paths.get(field_39416, new String[0]), "minecraft", SNBT_FILE_EXTENSION);
    }

    private Optional<Structure> loadStructureFromFile(Identifier id) {
        if (!Files.isDirectory(this.generatedPath, new LinkOption[0])) {
            return Optional.empty();
        }
        Path path = StructureManager.getAndCheckStructurePath(this.generatedPath, id, NBT_FILE_EXTENSION);
        return this.method_44231(() -> new FileInputStream(path.toFile()), throwable -> LOGGER.error("Couldn't load structure from {}", (Object)path, throwable));
    }

    private Stream<Identifier> method_44243() {
        if (!Files.isDirectory(this.generatedPath, new LinkOption[0])) {
            return Stream.empty();
        }
        try {
            return Files.list(this.generatedPath).filter(path -> Files.isDirectory(path, new LinkOption[0])).flatMap(path -> this.method_44235((Path)path));
        } catch (IOException iOException) {
            return Stream.empty();
        }
    }

    private Stream<Identifier> method_44235(Path path) {
        Path path2 = path.resolve(STRUCTURES_DIRECTORY);
        return this.method_44236(path2, path.getFileName().toString(), NBT_FILE_EXTENSION);
    }

    private Stream<Identifier> method_44236(Path path3, String string2, String string22) {
        if (!Files.isDirectory(path3, new LinkOption[0])) {
            return Stream.empty();
        }
        int i = string22.length();
        Function<String, String> function = string -> string.substring(0, string.length() - i);
        try {
            return Files.walk(path3, new FileVisitOption[0]).filter(path -> path.toString().endsWith(string22)).mapMulti((path2, consumer) -> {
                try {
                    consumer.accept(new Identifier(string2, (String)function.apply(this.method_44238(path3, (Path)path2))));
                } catch (InvalidIdentifierException invalidIdentifierException) {
                    LOGGER.error("Invalid location while listing pack contents", invalidIdentifierException);
                }
            });
        } catch (IOException iOException) {
            LOGGER.error("Failed to list folder contents", iOException);
            return Stream.empty();
        }
    }

    private String method_44238(Path path, Path path2) {
        return path.relativize(path2).toString().replace(File.separator, "/");
    }

    private Optional<Structure> method_44230(Identifier identifier, Path path) {
        Optional<Structure> optional;
        block10: {
            if (!Files.isDirectory(path, new LinkOption[0])) {
                return Optional.empty();
            }
            Path path2 = FileNameUtil.getResourcePath(path, identifier.getPath(), SNBT_FILE_EXTENSION);
            BufferedReader bufferedReader = Files.newBufferedReader(path2);
            try {
                String string = IOUtils.toString(bufferedReader);
                optional = Optional.of(this.createStructure(NbtHelper.fromNbtProviderString(string)));
                if (bufferedReader == null) break block10;
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
                } catch (NoSuchFileException noSuchFileException) {
                    return Optional.empty();
                } catch (CommandSyntaxException | IOException exception) {
                    LOGGER.error("Couldn't load structure from {}", (Object)path2, (Object)exception);
                    return Optional.empty();
                }
            }
            bufferedReader.close();
        }
        return optional;
    }

    private Optional<Structure> method_44231(class_7513 arg, Consumer<Throwable> consumer) {
        Optional<Structure> optional;
        block9: {
            InputStream inputStream = arg.open();
            try {
                optional = Optional.of(this.readStructure(inputStream));
                if (inputStream == null) break block9;
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
                } catch (FileNotFoundException fileNotFoundException) {
                    return Optional.empty();
                } catch (Throwable throwable3) {
                    consumer.accept(throwable3);
                    return Optional.empty();
                }
            }
            inputStream.close();
        }
        return optional;
    }

    private Structure readStructure(InputStream structureInputStream) throws IOException {
        NbtCompound nbtCompound = NbtIo.readCompressed(structureInputStream);
        return this.createStructure(nbtCompound);
    }

    public Structure createStructure(NbtCompound nbt) {
        if (!nbt.contains("DataVersion", NbtElement.NUMBER_TYPE)) {
            nbt.putInt("DataVersion", 500);
        }
        Structure structure = new Structure();
        structure.readNbt(NbtHelper.update(this.dataFixer, DataFixTypes.STRUCTURE, nbt, nbt.getInt("DataVersion")));
        return structure;
    }

    public boolean saveStructure(Identifier id) {
        Optional<Structure> optional = this.structures.get(id);
        if (!optional.isPresent()) {
            return false;
        }
        Structure structure = optional.get();
        Path path = StructureManager.getAndCheckStructurePath(this.generatedPath, id, NBT_FILE_EXTENSION);
        Path path2 = path.getParent();
        if (path2 == null) {
            return false;
        }
        try {
            Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath(new LinkOption[0]) : path2, new FileAttribute[0]);
        } catch (IOException iOException) {
            LOGGER.error("Failed to create parent directory: {}", (Object)path2);
            return false;
        }
        NbtCompound nbtCompound = structure.writeNbt(new NbtCompound());
        try (FileOutputStream outputStream = new FileOutputStream(path.toFile());){
            NbtIo.writeCompressed(nbtCompound, outputStream);
        } catch (Throwable throwable) {
            return false;
        }
        return true;
    }

    public Path method_44228(Identifier identifier, String string) {
        return StructureManager.getStructurePath(this.generatedPath, identifier, string);
    }

    public static Path getStructurePath(Path path, Identifier id, String extension) {
        try {
            Path path2 = path.resolve(id.getNamespace());
            Path path3 = path2.resolve(STRUCTURES_DIRECTORY);
            return FileNameUtil.getResourcePath(path3, id.getPath(), extension);
        } catch (InvalidPathException invalidPathException) {
            throw new InvalidIdentifierException("Invalid resource path: " + id, invalidPathException);
        }
    }

    private static Path getAndCheckStructurePath(Path path, Identifier id, String extension) {
        if (id.getPath().contains("//")) {
            throw new InvalidIdentifierException("Invalid resource path: " + id);
        }
        Path path2 = StructureManager.getStructurePath(path, id, extension);
        if (!(path2.startsWith(path) && FileNameUtil.isNormal(path2) && FileNameUtil.isAllowedName(path2))) {
            throw new InvalidIdentifierException("Invalid resource path: " + path2);
        }
        return path2;
    }

    public void unloadStructure(Identifier id) {
        this.structures.remove(id);
    }

    record class_7514(Function<Identifier, Optional<Structure>> loader, Supplier<Stream<Identifier>> lister) {
    }

    @FunctionalInterface
    static interface class_7513 {
        public InputStream open() throws IOException;
    }
}

