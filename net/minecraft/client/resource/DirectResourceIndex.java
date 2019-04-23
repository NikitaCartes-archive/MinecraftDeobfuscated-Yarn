/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DirectResourceIndex
extends ResourceIndex {
    private final File assetDir;

    public DirectResourceIndex(File file) {
        this.assetDir = file;
    }

    @Override
    public File getResource(Identifier identifier) {
        return new File(this.assetDir, identifier.toString().replace(':', '/'));
    }

    @Override
    public File findFile(String string) {
        return new File(this.assetDir, string);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Collection<String> getFilesRecursively(String string2, int i, Predicate<String> predicate) {
        Path path2 = this.assetDir.toPath().resolve("minecraft/");
        try (Stream<Path> stream2222 = Files.walk(path2.resolve(string2), i, new FileVisitOption[0]);){
            Collection collection = stream2222.filter(path -> Files.isRegularFile(path, new LinkOption[0])).filter(path -> !path.endsWith(".mcmeta")).map(path2::relativize).map(Object::toString).map(string -> string.replaceAll("\\\\", "/")).filter(predicate).collect(Collectors.toList());
            return collection;
        } catch (NoSuchFileException stream2222) {
            return Collections.emptyList();
        } catch (IOException iOException) {
            LOGGER.warn("Unable to getFiles on {}", (Object)string2, (Object)iOException);
        }
        return Collections.emptyList();
    }
}

