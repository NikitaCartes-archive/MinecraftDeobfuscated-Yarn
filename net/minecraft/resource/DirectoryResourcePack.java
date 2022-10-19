/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DirectoryResourcePack
extends AbstractFileResourcePack {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Joiner SEPARATOR_JOINER = Joiner.on("/");
    private final Path root;

    public DirectoryResourcePack(String name, Path root) {
        super(name);
        this.root = root;
    }

    @Override
    @Nullable
    public InputSupplier<InputStream> openRoot(String ... segments) {
        PathUtil.validatePath(segments);
        Path path = PathUtil.getPath(this.root, List.of(segments));
        if (Files.exists(path, new LinkOption[0])) {
            return InputSupplier.create(path);
        }
        return null;
    }

    public static boolean isValidPath(Path path) {
        return true;
    }

    @Override
    @Nullable
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        Path path = this.root.resolve(type.getDirectory()).resolve(id.getNamespace());
        return DirectoryResourcePack.open(id, path);
    }

    public static InputSupplier<InputStream> open(Identifier id, Path path) {
        return PathUtil.split(id.getPath()).get().map(segments -> {
            Path path2 = PathUtil.getPath(path, segments);
            return DirectoryResourcePack.open(path2);
        }, result -> {
            LOGGER.error("Invalid path {}: {}", (Object)id, (Object)result.message());
            return null;
        });
    }

    @Nullable
    private static InputSupplier<InputStream> open(Path path) {
        if (Files.exists(path, new LinkOption[0]) && DirectoryResourcePack.isValidPath(path)) {
            return InputSupplier.create(path);
        }
        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResourcePack.ResultConsumer consumer) {
        PathUtil.split(prefix).get().ifLeft(prefixSegments -> {
            Path path = this.root.resolve(type.getDirectory()).resolve(namespace);
            DirectoryResourcePack.findResources(namespace, path, prefixSegments, consumer);
        }).ifRight(result -> LOGGER.error("Invalid path {}: {}", (Object)prefix, (Object)result.message()));
    }

    public static void findResources(String namespace, Path path, List<String> prefixSegments, ResourcePack.ResultConsumer consumer) {
        Path path22 = PathUtil.getPath(path, prefixSegments);
        try (Stream<Path> stream2 = Files.find(path22, Integer.MAX_VALUE, (path2, attributes) -> attributes.isRegularFile(), new FileVisitOption[0]);){
            stream2.forEach(foundPath -> {
                String string2 = SEPARATOR_JOINER.join(path.relativize((Path)foundPath));
                Identifier identifier = Identifier.of(namespace, string2);
                if (identifier == null) {
                    Util.error(String.format(Locale.ROOT, "Invalid path in pack: %s:%s, ignoring", namespace, string2));
                } else {
                    consumer.accept(identifier, InputSupplier.create(foundPath));
                }
            });
        } catch (NoSuchFileException stream2) {
        } catch (IOException iOException) {
            LOGGER.error("Failed to list path {}", (Object)path22, (Object)iOException);
        }
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        HashSet<String> set = Sets.newHashSet();
        Path path = this.root.resolve(type.getDirectory());
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);){
            for (Path path2 : directoryStream) {
                String string = path2.getFileName().toString();
                if (string.equals(string.toLowerCase(Locale.ROOT))) {
                    set.add(string);
                    continue;
                }
                LOGGER.warn("Ignored non-lowercase namespace: {} in {}", (Object)string, (Object)this.root);
            }
        } catch (IOException iOException) {
            LOGGER.error("Failed to list path {}", (Object)path, (Object)iOException);
        }
        return set;
    }

    @Override
    public void close() {
    }
}

