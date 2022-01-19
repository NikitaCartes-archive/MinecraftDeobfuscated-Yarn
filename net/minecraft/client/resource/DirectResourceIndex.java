/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource;

import com.mojang.logging.LogUtils;
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
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class DirectResourceIndex
extends ResourceIndex {
    private static final Logger field_36375 = LogUtils.getLogger();
    private final File assetDir;

    public DirectResourceIndex(File assetDir) {
        this.assetDir = assetDir;
    }

    @Override
    public File getResource(Identifier identifier) {
        return new File(this.assetDir, identifier.toString().replace(':', '/'));
    }

    @Override
    public File findFile(String path) {
        return new File(this.assetDir, path);
    }

    @Override
    public Collection<Identifier> getFilesRecursively(String prefix, String namespace, int maxDepth, Predicate<String> pathFilter) {
        block10: {
            Collection collection;
            block9: {
                Path path2 = this.assetDir.toPath().resolve(namespace);
                Stream<Path> stream2 = Files.walk(path2.resolve(prefix), maxDepth, new FileVisitOption[0]);
                try {
                    collection = stream2.filter(path -> Files.isRegularFile(path, new LinkOption[0])).filter(path -> !path.endsWith(".mcmeta")).filter(path -> pathFilter.test(path.getFileName().toString())).map(path -> new Identifier(namespace, path2.relativize((Path)path).toString().replaceAll("\\\\", "/"))).collect(Collectors.toList());
                    if (stream2 == null) break block9;
                } catch (Throwable throwable) {
                    try {
                        if (stream2 != null) {
                            try {
                                stream2.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    } catch (NoSuchFileException stream2) {
                        break block10;
                    } catch (IOException iOException) {
                        field_36375.warn("Unable to getFiles on {}", (Object)prefix, (Object)iOException);
                    }
                }
                stream2.close();
            }
            return collection;
        }
        return Collections.emptyList();
    }
}

