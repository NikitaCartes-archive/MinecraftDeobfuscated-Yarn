/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.GameVersion;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DataCache {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String HEADER = "// ";
    private final Path root;
    private final Path cachePath;
    private final String versionName;
    private final Map<DataProvider, CachedData> cachedDatas;
    private final Map<DataProvider, CachedDataWriter> dataWriters = new HashMap<DataProvider, CachedDataWriter>();
    private final Set<Path> paths = new HashSet<Path>();
    private final int totalSize;

    private Path getPath(DataProvider dataProvider) {
        return this.cachePath.resolve(Hashing.sha1().hashString(dataProvider.getName(), StandardCharsets.UTF_8).toString());
    }

    public DataCache(Path root, List<DataProvider> dataProviders, GameVersion gameVersion) throws IOException {
        this.versionName = gameVersion.getName();
        this.root = root;
        this.cachePath = root.resolve(".cache");
        Files.createDirectories(this.cachePath, new FileAttribute[0]);
        HashMap<DataProvider, CachedData> map = new HashMap<DataProvider, CachedData>();
        int i = 0;
        for (DataProvider dataProvider : dataProviders) {
            Path path = this.getPath(dataProvider);
            this.paths.add(path);
            CachedData cachedData = DataCache.parseOrCreateCache(root, path);
            map.put(dataProvider, cachedData);
            i += cachedData.size();
        }
        this.cachedDatas = map;
        this.totalSize = i;
    }

    private static CachedData parseOrCreateCache(Path root, Path dataProviderPath) {
        if (Files.isReadable(dataProviderPath)) {
            try {
                return CachedData.parseCache(root, dataProviderPath);
            } catch (Exception exception) {
                LOGGER.warn("Failed to parse cache {}, discarding", (Object)dataProviderPath, (Object)exception);
            }
        }
        return new CachedData("unknown");
    }

    public boolean isVersionDifferent(DataProvider dataProvider) {
        CachedData cachedData = this.cachedDatas.get(dataProvider);
        return cachedData == null || !cachedData.version.equals(this.versionName);
    }

    public DataWriter getOrCreateWriter(DataProvider dataProvider) {
        return this.dataWriters.computeIfAbsent(dataProvider, provider -> {
            CachedData cachedData = this.cachedDatas.get(provider);
            if (cachedData == null) {
                throw new IllegalStateException("Provider not registered: " + provider.getName());
            }
            CachedDataWriter cachedDataWriter = new CachedDataWriter(this.versionName, cachedData);
            this.cachedDatas.put((DataProvider)provider, cachedDataWriter.newCache);
            return cachedDataWriter;
        });
    }

    public void write() throws IOException {
        MutableInt mutableInt = new MutableInt();
        this.dataWriters.forEach((dataProvider, writer) -> {
            Path path = this.getPath((DataProvider)dataProvider);
            writer.newCache.write(this.root, path, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + "\t" + dataProvider.getName());
            mutableInt.add(writer.cacheMissCount);
        });
        HashSet<Path> set = new HashSet<Path>();
        this.cachedDatas.values().forEach(cachedData -> set.addAll(cachedData.data().keySet()));
        set.add(this.root.resolve("version.json"));
        MutableInt mutableInt2 = new MutableInt();
        MutableInt mutableInt3 = new MutableInt();
        try (Stream<Path> stream = Files.walk(this.root, new FileVisitOption[0]);){
            stream.forEach(path -> {
                if (Files.isDirectory(path, new LinkOption[0])) {
                    return;
                }
                if (this.paths.contains(path)) {
                    return;
                }
                mutableInt2.increment();
                if (set.contains(path)) {
                    return;
                }
                try {
                    Files.delete(path);
                } catch (IOException iOException) {
                    LOGGER.warn("Failed to delete file {}", path, (Object)iOException);
                }
                mutableInt3.increment();
            });
        }
        LOGGER.info("Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}", mutableInt2, this.totalSize, set.size(), mutableInt3, mutableInt);
    }

    record CachedData(String version, Map<Path, HashCode> data) {
        CachedData(String version) {
            this(version, new HashMap<Path, HashCode>());
        }

        @Nullable
        public HashCode get(Path path) {
            return this.data.get(path);
        }

        public void put(Path path, HashCode hashCode) {
            this.data.put(path, hashCode);
        }

        public int size() {
            return this.data.size();
        }

        public static CachedData parseCache(Path root, Path dataProviderPath) throws IOException {
            try (BufferedReader bufferedReader = Files.newBufferedReader(dataProviderPath, StandardCharsets.UTF_8);){
                String string = bufferedReader.readLine();
                if (!string.startsWith(DataCache.HEADER)) {
                    throw new IllegalStateException("Missing cache file header");
                }
                String[] strings = string.substring(DataCache.HEADER.length()).split("\t", 2);
                String string2 = strings[0];
                HashMap map = new HashMap();
                bufferedReader.lines().forEach(line -> {
                    int i = line.indexOf(32);
                    map.put(root.resolve(line.substring(i + 1)), HashCode.fromString(line.substring(0, i)));
                });
                CachedData cachedData = new CachedData(string2, Map.copyOf(map));
                return cachedData;
            }
        }

        public void write(Path root, Path dataProviderPath, String description) {
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(dataProviderPath, StandardCharsets.UTF_8, new OpenOption[0]);){
                bufferedWriter.write(DataCache.HEADER);
                bufferedWriter.write(this.version);
                bufferedWriter.write(9);
                bufferedWriter.write(description);
                bufferedWriter.newLine();
                for (Map.Entry<Path, HashCode> entry : this.data.entrySet()) {
                    bufferedWriter.write(entry.getValue().toString());
                    bufferedWriter.write(32);
                    bufferedWriter.write(root.relativize(entry.getKey()).toString());
                    bufferedWriter.newLine();
                }
            } catch (IOException iOException) {
                LOGGER.warn("Unable write cachefile {}: {}", (Object)dataProviderPath, (Object)iOException);
            }
        }
    }

    static class CachedDataWriter
    implements DataWriter {
        private final CachedData oldCache;
        final CachedData newCache;
        int cacheMissCount;

        CachedDataWriter(String versionName, CachedData cachedData) {
            this.oldCache = cachedData;
            this.newCache = new CachedData(versionName);
        }

        private boolean isCacheInvalid(Path path, HashCode hashCode) {
            return !Objects.equals(this.oldCache.get(path), hashCode) || !Files.exists(path, new LinkOption[0]);
        }

        @Override
        public void write(Path path, byte[] data, HashCode hashCode) throws IOException {
            if (this.isCacheInvalid(path, hashCode)) {
                ++this.cacheMissCount;
                Files.createDirectories(path.getParent(), new FileAttribute[0]);
                try (OutputStream outputStream = Files.newOutputStream(path, new OpenOption[0]);){
                    outputStream.write(data);
                }
            }
            this.newCache.put(path, hashCode);
        }
    }
}

