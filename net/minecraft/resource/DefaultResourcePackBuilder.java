/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataMap;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class DefaultResourcePackBuilder {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static Consumer<DefaultResourcePackBuilder> callback = builder -> {};
    private static final Map<ResourceType, Path> RESOURCE_TYPE_TO_PATH = Util.make(() -> {
        Class<DefaultResourcePack> clazz = DefaultResourcePack.class;
        synchronized (DefaultResourcePack.class) {
            ImmutableMap.Builder<ResourceType, Path> builder = ImmutableMap.builder();
            for (ResourceType resourceType : ResourceType.values()) {
                String string = "/" + resourceType.getDirectory() + "/.mcassetsroot";
                URL uRL = DefaultResourcePack.class.getResource(string);
                if (uRL == null) {
                    LOGGER.error("File {} does not exist in classpath", (Object)string);
                    continue;
                }
                try {
                    URI uRI = uRL.toURI();
                    String string2 = uRI.getScheme();
                    if (!"jar".equals(string2) && !"file".equals(string2)) {
                        LOGGER.warn("Assets URL '{}' uses unexpected schema", (Object)uRI);
                    }
                    Path path = DefaultResourcePackBuilder.toPath(uRI);
                    builder.put(resourceType, path.getParent());
                } catch (Exception exception) {
                    LOGGER.error("Couldn't resolve path to vanilla assets", exception);
                }
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return builder.build();
        }
    });
    private final Set<Path> rootPaths = new LinkedHashSet<Path>();
    private final Map<ResourceType, Set<Path>> paths = new EnumMap<ResourceType, Set<Path>>(ResourceType.class);
    private ResourceMetadataMap metadataMap = ResourceMetadataMap.of();
    private final Set<String> namespaces = new HashSet<String>();

    private static Path toPath(URI uri) throws IOException {
        try {
            return Paths.get(uri);
        } catch (FileSystemNotFoundException fileSystemNotFoundException) {
        } catch (Throwable throwable) {
            LOGGER.warn("Unable to get path for: {}", (Object)uri, (Object)throwable);
        }
        try {
            FileSystems.newFileSystem(uri, Collections.emptyMap());
        } catch (FileSystemAlreadyExistsException fileSystemAlreadyExistsException) {
            // empty catch block
        }
        return Paths.get(uri);
    }

    private boolean exists(Path path) {
        if (!Files.exists(path, new LinkOption[0])) {
            return false;
        }
        if (!Files.isDirectory(path, new LinkOption[0])) {
            throw new IllegalArgumentException("Path " + path.toAbsolutePath() + " is not directory");
        }
        return true;
    }

    private void addRootPath(Path path) {
        if (this.exists(path)) {
            this.rootPaths.add(path);
        }
    }

    private void addPath(ResourceType type, Path path) {
        if (this.exists(path)) {
            this.paths.computeIfAbsent(type, type2 -> new LinkedHashSet()).add(path);
        }
    }

    public DefaultResourcePackBuilder withDefaultPaths() {
        RESOURCE_TYPE_TO_PATH.forEach((type, path) -> {
            this.addRootPath(path.getParent());
            this.addPath((ResourceType)((Object)type), (Path)path);
        });
        return this;
    }

    public DefaultResourcePackBuilder withPaths(ResourceType type, Class<?> clazz) {
        Enumeration<URL> enumeration = null;
        try {
            enumeration = clazz.getClassLoader().getResources(type.getDirectory() + "/");
        } catch (IOException iOException) {
            // empty catch block
        }
        while (enumeration != null && enumeration.hasMoreElements()) {
            URL uRL = enumeration.nextElement();
            try {
                URI uRI = uRL.toURI();
                if (!"file".equals(uRI.getScheme())) continue;
                Path path = Paths.get(uRI);
                this.addRootPath(path.getParent());
                this.addPath(type, path);
            } catch (Exception exception) {
                LOGGER.error("Failed to extract path from {}", (Object)uRL, (Object)exception);
            }
        }
        return this;
    }

    public DefaultResourcePackBuilder runCallback() {
        callback.accept(this);
        return this;
    }

    public DefaultResourcePackBuilder withRoot(Path root) {
        this.addRootPath(root);
        for (ResourceType resourceType : ResourceType.values()) {
            this.addPath(resourceType, root.resolve(resourceType.getDirectory()));
        }
        return this;
    }

    public DefaultResourcePackBuilder withPath(ResourceType type, Path path) {
        this.addRootPath(path);
        this.addPath(type, path);
        return this;
    }

    public DefaultResourcePackBuilder withMetadataMap(ResourceMetadataMap metadataMap) {
        this.metadataMap = metadataMap;
        return this;
    }

    public DefaultResourcePackBuilder withNamespaces(String ... namespaces) {
        this.namespaces.addAll(Arrays.asList(namespaces));
        return this;
    }

    public DefaultResourcePack build() {
        EnumMap<ResourceType, List<Path>> map = new EnumMap<ResourceType, List<Path>>(ResourceType.class);
        for (ResourceType resourceType : ResourceType.values()) {
            List<Path> list = DefaultResourcePackBuilder.reverse(this.paths.getOrDefault((Object)resourceType, Set.of()));
            map.put(resourceType, list);
        }
        return new DefaultResourcePack(this.metadataMap, Set.copyOf(this.namespaces), DefaultResourcePackBuilder.reverse(this.rootPaths), map);
    }

    private static List<Path> reverse(Collection<Path> paths) {
        ArrayList<Path> list = new ArrayList<Path>(paths);
        Collections.reverse(list);
        return List.copyOf(list);
    }
}

