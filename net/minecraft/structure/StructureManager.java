/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.File;
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
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.Structure;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.TagHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class StructureManager
implements SynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<Identifier, Structure> structures = Maps.newHashMap();
    private final DataFixer dataFixer;
    private final MinecraftServer server;
    private final Path generatedPath;

    public StructureManager(MinecraftServer minecraftServer, File file, DataFixer dataFixer) {
        this.server = minecraftServer;
        this.dataFixer = dataFixer;
        this.generatedPath = file.toPath().resolve("generated").normalize();
        minecraftServer.getDataManager().registerListener(this);
    }

    public Structure getStructureOrBlank(Identifier identifier) {
        Structure structure = this.getStructure(identifier);
        if (structure == null) {
            structure = new Structure();
            this.structures.put(identifier, structure);
        }
        return structure;
    }

    @Nullable
    public Structure getStructure(Identifier identifier2) {
        return this.structures.computeIfAbsent(identifier2, identifier -> {
            Structure structure = this.loadStructureFromFile((Identifier)identifier);
            return structure != null ? structure : this.loadStructureFromResource((Identifier)identifier);
        });
    }

    @Override
    public void apply(ResourceManager resourceManager) {
        this.structures.clear();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private Structure loadStructureFromResource(Identifier identifier) {
        Identifier identifier2 = new Identifier(identifier.getNamespace(), "structures/" + identifier.getPath() + ".nbt");
        try (Resource resource = this.server.getDataManager().getResource(identifier2);){
            Structure structure = this.readStructure(resource.getInputStream());
            return structure;
        } catch (FileNotFoundException fileNotFoundException) {
            return null;
        } catch (Throwable throwable6) {
            LOGGER.error("Couldn't load structure {}: {}", (Object)identifier, (Object)throwable6.toString());
            return null;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private Structure loadStructureFromFile(Identifier identifier) {
        if (!this.generatedPath.toFile().isDirectory()) {
            return null;
        }
        Path path = this.getAndCheckStructurePath(identifier, ".nbt");
        try (FileInputStream inputStream = new FileInputStream(path.toFile());){
            Structure structure = this.readStructure(inputStream);
            return structure;
        } catch (FileNotFoundException fileNotFoundException) {
            return null;
        } catch (IOException iOException) {
            LOGGER.error("Couldn't load structure from {}", (Object)path, (Object)iOException);
            return null;
        }
    }

    private Structure readStructure(InputStream inputStream) throws IOException {
        CompoundTag compoundTag = NbtIo.readCompressed(inputStream);
        return this.method_21891(compoundTag);
    }

    public Structure method_21891(CompoundTag compoundTag) {
        if (!compoundTag.containsKey("DataVersion", 99)) {
            compoundTag.putInt("DataVersion", 500);
        }
        Structure structure = new Structure();
        structure.fromTag(TagHelper.update(this.dataFixer, DataFixTypes.STRUCTURE, compoundTag, compoundTag.getInt("DataVersion")));
        return structure;
    }

    public boolean saveStructure(Identifier identifier) {
        Structure structure = this.structures.get(identifier);
        if (structure == null) {
            return false;
        }
        Path path = this.getAndCheckStructurePath(identifier, ".nbt");
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
        CompoundTag compoundTag = structure.toTag(new CompoundTag());
        try (FileOutputStream outputStream = new FileOutputStream(path.toFile());){
            NbtIo.writeCompressed(compoundTag, outputStream);
        } catch (Throwable throwable) {
            return false;
        }
        return true;
    }

    public Path getStructurePath(Identifier identifier, String string) {
        try {
            Path path = this.generatedPath.resolve(identifier.getNamespace());
            Path path2 = path.resolve("structures");
            return FileNameUtil.method_20202(path2, identifier.getPath(), string);
        } catch (InvalidPathException invalidPathException) {
            throw new InvalidIdentifierException("Invalid resource path: " + identifier, invalidPathException);
        }
    }

    private Path getAndCheckStructurePath(Identifier identifier, String string) {
        if (identifier.getPath().contains("//")) {
            throw new InvalidIdentifierException("Invalid resource path: " + identifier);
        }
        Path path = this.getStructurePath(identifier, string);
        if (!(path.startsWith(this.generatedPath) && FileNameUtil.isNormal(path) && FileNameUtil.isAllowedName(path))) {
            throw new InvalidIdentifierException("Invalid resource path: " + path);
        }
        return path;
    }

    public void unloadStructure(Identifier identifier) {
        this.structures.remove(identifier);
    }
}

