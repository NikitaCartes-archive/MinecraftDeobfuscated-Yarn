/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class DataCache {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path root;
    private final Path recordFile;
    private int unchanged;
    private final Map<Path, String> oldSha1 = Maps.newHashMap();
    private final Map<Path, String> newSha1 = Maps.newHashMap();
    private final Set<Path> ignores = Sets.newHashSet();

    public DataCache(Path path2, String string2) throws IOException {
        this.root = path2;
        Path path22 = path2.resolve(".cache");
        Files.createDirectories(path22, new FileAttribute[0]);
        this.recordFile = path22.resolve(string2);
        this.files().forEach(path -> this.oldSha1.put((Path)path, ""));
        if (Files.isReadable(this.recordFile)) {
            IOUtils.readLines(Files.newInputStream(this.recordFile, new OpenOption[0]), Charsets.UTF_8).forEach(string -> {
                int i = string.indexOf(32);
                this.oldSha1.put(path2.resolve(string.substring(i + 1)), string.substring(0, i));
            });
        }
    }

    public void write() throws IOException {
        BufferedWriter writer;
        this.deleteAll();
        try {
            writer = Files.newBufferedWriter(this.recordFile, new OpenOption[0]);
        } catch (IOException iOException) {
            LOGGER.warn("Unable write cachefile {}: {}", (Object)this.recordFile, (Object)iOException.toString());
            return;
        }
        IOUtils.writeLines((Collection)this.newSha1.entrySet().stream().map(entry -> (String)entry.getValue() + ' ' + this.root.relativize((Path)entry.getKey())).collect(Collectors.toList()), System.lineSeparator(), writer);
        ((Writer)writer).close();
        LOGGER.debug("Caching: cache hits: {}, created: {} removed: {}", (Object)this.unchanged, (Object)(this.newSha1.size() - this.unchanged), (Object)this.oldSha1.size());
    }

    @Nullable
    public String getOldSha1(Path path) {
        return this.oldSha1.get(path);
    }

    public void updateSha1(Path path, String sha1) {
        this.newSha1.put(path, sha1);
        if (Objects.equals(this.oldSha1.remove(path), sha1)) {
            ++this.unchanged;
        }
    }

    public boolean contains(Path path) {
        return this.oldSha1.containsKey(path);
    }

    public void ignore(Path path) {
        this.ignores.add(path);
    }

    private void deleteAll() throws IOException {
        this.files().forEach(path -> {
            if (this.contains((Path)path) && !this.ignores.contains(path)) {
                try {
                    Files.delete(path);
                } catch (IOException iOException) {
                    LOGGER.debug("Unable to delete: {} ({})", path, (Object)iOException.toString());
                }
            }
        });
    }

    private Stream<Path> files() throws IOException {
        return Files.walk(this.root, new FileVisitOption[0]).filter(path -> !Objects.equals(this.recordFile, path) && !Files.isDirectory(path, new LinkOption[0]));
    }
}

