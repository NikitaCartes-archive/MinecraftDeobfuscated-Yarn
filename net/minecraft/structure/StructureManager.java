/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
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
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.Structure;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class StructureManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String field_31684 = "structures";
    private static final String field_31685 = ".nbt";
    private static final String field_31686 = ".snbt";
    private final Map<Identifier, Structure> structures = Maps.newHashMap();
    private final DataFixer dataFixer;
    private ResourceManager resourceManager;
    private final Path generatedPath;

    public StructureManager(ResourceManager resourceManager, LevelStorage.Session session, DataFixer dataFixer) {
        this.resourceManager = resourceManager;
        this.dataFixer = dataFixer;
        this.generatedPath = session.getDirectory(WorldSavePath.GENERATED).normalize();
    }

    public Structure getStructureOrBlank(Identifier id) {
        Structure structure = this.getStructure(id);
        if (structure == null) {
            structure = new Structure();
            this.structures.put(id, structure);
        }
        return structure;
    }

    @Nullable
    public Structure getStructure(Identifier id) {
        return this.structures.computeIfAbsent(id, identifier -> {
            Structure structure = this.loadStructureFromFile((Identifier)identifier);
            return structure != null ? structure : this.loadStructureFromResource((Identifier)identifier);
        });
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.structures.clear();
    }

    @Nullable
    private Structure loadStructureFromResource(Identifier id) {
        Structure structure;
        block9: {
            Identifier identifier = new Identifier(id.getNamespace(), "structures/" + id.getPath() + field_31685);
            Resource resource = this.resourceManager.getResource(identifier);
            try {
                structure = this.readStructure(resource.getInputStream());
                if (resource == null) break block9;
            } catch (Throwable throwable) {
                try {
                    if (resource != null) {
                        try {
                            resource.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (FileNotFoundException fileNotFoundException) {
                    return null;
                } catch (Throwable throwable3) {
                    LOGGER.error("Couldn't load structure {}: {}", (Object)id, (Object)throwable3.toString());
                    return null;
                }
            }
            resource.close();
        }
        return structure;
    }

    @Nullable
    private Structure loadStructureFromFile(Identifier id) {
        Structure structure;
        if (!this.generatedPath.toFile().isDirectory()) {
            return null;
        }
        Path path = this.getAndCheckStructurePath(id, field_31685);
        FileInputStream inputStream = new FileInputStream(path.toFile());
        try {
            structure = this.readStructure(inputStream);
        } catch (Throwable throwable) {
            try {
                try {
                    ((InputStream)inputStream).close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            } catch (FileNotFoundException fileNotFoundException) {
                return null;
            } catch (IOException iOException) {
                LOGGER.error("Couldn't load structure from {}", (Object)path, (Object)iOException);
                return null;
            }
        }
        ((InputStream)inputStream).close();
        return structure;
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
        Structure structure = this.structures.get(id);
        if (structure == null) {
            return false;
        }
        Path path = this.getAndCheckStructurePath(id, field_31685);
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
            Path path2 = path.resolve(field_31684);
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

