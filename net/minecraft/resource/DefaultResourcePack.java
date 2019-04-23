/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class DefaultResourcePack
implements ResourcePack {
    public static Path RESOURCE_PATH;
    private static final Logger LOGGER;
    public static Class<?> RESOURCE_CLASS;
    private static final Map<ResourceType, FileSystem> typeToFileSystem;
    public final Set<String> namespaces;

    public DefaultResourcePack(String ... strings) {
        this.namespaces = ImmutableSet.copyOf(strings);
    }

    @Override
    public InputStream openRoot(String string) throws IOException {
        Path path;
        if (string.contains("/") || string.contains("\\")) {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
        if (RESOURCE_PATH != null && Files.exists(path = RESOURCE_PATH.resolve(string), new LinkOption[0])) {
            return Files.newInputStream(path, new OpenOption[0]);
        }
        return this.getInputStream(string);
    }

    @Override
    public InputStream open(ResourceType resourceType, Identifier identifier) throws IOException {
        InputStream inputStream = this.findInputStream(resourceType, identifier);
        if (inputStream != null) {
            return inputStream;
        }
        throw new FileNotFoundException(identifier.getPath());
    }

    @Override
    public Collection<Identifier> findResources(ResourceType resourceType, String string, int i, Predicate<String> predicate) {
        URI uRI;
        HashSet<Identifier> set = Sets.newHashSet();
        if (RESOURCE_PATH != null) {
            try {
                set.addAll(this.getIdentifiers(i, "minecraft", RESOURCE_PATH.resolve(resourceType.getName()).resolve("minecraft"), string, predicate));
            } catch (IOException iOException) {
                // empty catch block
            }
            if (resourceType == ResourceType.CLIENT_RESOURCES) {
                Enumeration<URL> enumeration = null;
                try {
                    enumeration = RESOURCE_CLASS.getClassLoader().getResources(resourceType.getName() + "/minecraft");
                } catch (IOException iOException) {
                    // empty catch block
                }
                while (enumeration != null && enumeration.hasMoreElements()) {
                    try {
                        uRI = ((URL)enumeration.nextElement()).toURI();
                        if (!"file".equals(uRI.getScheme())) continue;
                        set.addAll(this.getIdentifiers(i, "minecraft", Paths.get(uRI), string, predicate));
                    } catch (IOException | URISyntaxException uRI2) {}
                }
            }
        }
        try {
            URL uRL = DefaultResourcePack.class.getResource("/" + resourceType.getName() + "/.mcassetsroot");
            if (uRL == null) {
                LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
                return set;
            }
            uRI = uRL.toURI();
            if ("file".equals(uRI.getScheme())) {
                URL uRL2 = new URL(uRL.toString().substring(0, uRL.toString().length() - ".mcassetsroot".length()) + "minecraft");
                if (uRL2 == null) {
                    return set;
                }
                Path path = Paths.get(uRL2.toURI());
                set.addAll(this.getIdentifiers(i, "minecraft", path, string, predicate));
            } else if ("jar".equals(uRI.getScheme())) {
                Path path2 = typeToFileSystem.get((Object)resourceType).getPath("/" + resourceType.getName() + "/minecraft", new String[0]);
                set.addAll(this.getIdentifiers(i, "minecraft", path2, string, predicate));
            } else {
                LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", (Object)uRI);
            }
        } catch (FileNotFoundException | NoSuchFileException uRL) {
        } catch (IOException | URISyntaxException exception) {
            LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)exception);
        }
        return set;
    }

    private Collection<Identifier> getIdentifiers(int i, String string, Path path, String string2, Predicate<String> predicate) throws IOException {
        ArrayList<Identifier> list = Lists.newArrayList();
        Iterator iterator = Files.walk(path.resolve(string2), i, new FileVisitOption[0]).iterator();
        while (iterator.hasNext()) {
            Path path2 = (Path)iterator.next();
            if (path2.endsWith(".mcmeta") || !Files.isRegularFile(path2, new LinkOption[0]) || !predicate.test(path2.getFileName().toString())) continue;
            list.add(new Identifier(string, path.relativize(path2).toString().replaceAll("\\\\", "/")));
        }
        return list;
    }

    @Nullable
    protected InputStream findInputStream(ResourceType resourceType, Identifier identifier) {
        Path path;
        String string = "/" + resourceType.getName() + "/" + identifier.getNamespace() + "/" + identifier.getPath();
        if (RESOURCE_PATH != null && Files.exists(path = RESOURCE_PATH.resolve(resourceType.getName() + "/" + identifier.getNamespace() + "/" + identifier.getPath()), new LinkOption[0])) {
            try {
                return Files.newInputStream(path, new OpenOption[0]);
            } catch (IOException iOException) {
                // empty catch block
            }
        }
        try {
            URL uRL = DefaultResourcePack.class.getResource(string);
            if (uRL != null && DirectoryResourcePack.isValidPath(new File(uRL.getFile()), string)) {
                return DefaultResourcePack.class.getResourceAsStream(string);
            }
        } catch (IOException iOException) {
            return DefaultResourcePack.class.getResourceAsStream(string);
        }
        return null;
    }

    @Nullable
    protected InputStream getInputStream(String string) {
        return DefaultResourcePack.class.getResourceAsStream("/" + string);
    }

    @Override
    public boolean contains(ResourceType resourceType, Identifier identifier) {
        InputStream inputStream = this.findInputStream(resourceType, identifier);
        boolean bl = inputStream != null;
        IOUtils.closeQuietly(inputStream);
        return bl;
    }

    @Override
    public Set<String> getNamespaces(ResourceType resourceType) {
        return this.namespaces;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    @Nullable
    public <T> T parseMetadata(ResourceMetadataReader<T> resourceMetadataReader) throws IOException {
        try (InputStream inputStream = this.openRoot("pack.mcmeta");){
            T t = AbstractFileResourcePack.parseMetadata(resourceMetadataReader, inputStream);
            return t;
        } catch (FileNotFoundException | RuntimeException exception) {
            return null;
        }
    }

    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public void close() {
    }

    static {
        LOGGER = LogManager.getLogger();
        typeToFileSystem = SystemUtil.consume(Maps.newHashMap(), hashMap -> {
            Class<DefaultResourcePack> clazz = DefaultResourcePack.class;
            synchronized (DefaultResourcePack.class) {
                for (ResourceType resourceType : ResourceType.values()) {
                    URL uRL = DefaultResourcePack.class.getResource("/" + resourceType.getName() + "/.mcassetsroot");
                    try {
                        FileSystem fileSystem;
                        URI uRI = uRL.toURI();
                        if (!"jar".equals(uRI.getScheme())) continue;
                        try {
                            fileSystem = FileSystems.getFileSystem(uRI);
                        } catch (FileSystemNotFoundException fileSystemNotFoundException) {
                            fileSystem = FileSystems.newFileSystem(uRI, Collections.emptyMap());
                        }
                        hashMap.put(resourceType, fileSystem);
                    } catch (IOException | URISyntaxException exception) {
                        LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)exception);
                    }
                }
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return;
            }
        });
    }
}

