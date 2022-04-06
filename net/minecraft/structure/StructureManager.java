/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.Optional;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.Structure;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;

public class StructureManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String STRUCTURES_DIRECTORY = "structures";
    private static final String NBT_FILE_EXTENSION = ".nbt";
    private static final String SNBT_FILE_EXTENSION = ".snbt";
    private final Map<Identifier, Optional<Structure>> structures = Maps.newConcurrentMap();
    private final DataFixer dataFixer;
    private ResourceManager resourceManager;
    private final Path generatedPath;

    public StructureManager(ResourceManager resourceManager, LevelStorage.Session session, DataFixer dataFixer) {
        this.resourceManager = resourceManager;
        this.dataFixer = dataFixer;
        this.generatedPath = session.getDirectory(WorldSavePath.GENERATED).normalize();
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
        return this.structures.computeIfAbsent(id, identifier -> {
            Optional<Structure> optional = this.loadStructureFromFile((Identifier)identifier);
            return optional.isPresent() ? optional : this.loadStructureFromResource((Identifier)identifier);
        });
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.structures.clear();
    }

    private Optional<Structure> loadStructureFromResource(Identifier id) {
        Optional<Structure> optional;
        block9: {
            Identifier identifier = new Identifier(id.getNamespace(), "structures/" + id.getPath() + NBT_FILE_EXTENSION);
            InputStream inputStream = this.resourceManager.open(identifier);
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
                    LOGGER.error("Couldn't load structure {}: {}", (Object)id, (Object)throwable3.toString());
                    return Optional.empty();
                }
            }
            inputStream.close();
        }
        return optional;
    }

    private Optional<Structure> loadStructureFromFile(Identifier id) {
        Optional<Structure> optional;
        if (!this.generatedPath.toFile().isDirectory()) {
            return Optional.empty();
        }
        Path path = this.getAndCheckStructurePath(id, NBT_FILE_EXTENSION);
        FileInputStream inputStream = new FileInputStream(path.toFile());
        try {
            optional = Optional.of(this.readStructure(inputStream));
        } catch (Throwable throwable) {
            try {
                try {
                    ((InputStream)inputStream).close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            } catch (FileNotFoundException fileNotFoundException) {
                return Optional.empty();
            } catch (IOException iOException) {
                LOGGER.error("Couldn't load structure from {}", (Object)path, (Object)iOException);
                return Optional.empty();
            }
        }
        ((InputStream)inputStream).close();
        return optional;
    }

    private Structure readStructure(InputStream structureInputStream) throws IOException {
        NbtCompound nbtCompound = NbtIo.readCompressed(structureInputStream);
        return this.createStructure(nbtCompound);
    }

    public Structure createStructure(NbtCompound nbt) {
        if (!nbt.contains("DataVersion", 99)) {
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
        Path path = this.getAndCheckStructurePath(id, NBT_FILE_EXTENSION);
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

    public Path getStructurePath(Identifier id, String extension) {
        try {
            Path path = this.generatedPath.resolve(id.getNamespace());
            Path path2 = path.resolve(STRUCTURES_DIRECTORY);
            return FileNameUtil.getResourcePath(path2, id.getPath(), extension);
        } catch (InvalidPathException invalidPathException) {
            throw new InvalidIdentifierException("Invalid resource path: " + id, invalidPathException);
        }
    }

    private Path getAndCheckStructurePath(Identifier id, String extension) {
        if (id.getPath().contains("//")) {
            throw new InvalidIdentifierException("Invalid resource path: " + id);
        }
        Path path = this.getStructurePath(id, extension);
        if (!(path.startsWith(this.generatedPath) && FileNameUtil.isNormal(path) && FileNameUtil.isAllowedName(path))) {
            throw new InvalidIdentifierException("Invalid resource path: " + path);
        }
        return path;
    }

    public void unloadStructure(Identifier id) {
        this.structures.remove(id);
    }
}

