package net.minecraft.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.GameVersion;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class DataCache {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final String HEADER = "// ";
	private final Path root;
	private final Path cachePath;
	private final String versionName;
	private final Map<String, DataCache.CachedData> cachedDatas;
	private final Set<String> dataWriters = new HashSet();
	final Set<Path> paths = new HashSet();
	private final int totalSize;
	private int totalCacheMissCount;

	private Path getPath(String providerName) {
		return this.cachePath.resolve(Hashing.sha1().hashString(providerName, StandardCharsets.UTF_8).toString());
	}

	public DataCache(Path root, Collection<String> providerNames, GameVersion gameVersion) throws IOException {
		this.versionName = gameVersion.getName();
		this.root = root;
		this.cachePath = root.resolve(".cache");
		Files.createDirectories(this.cachePath);
		Map<String, DataCache.CachedData> map = new HashMap();
		int i = 0;

		for (String string : providerNames) {
			Path path = this.getPath(string);
			this.paths.add(path);
			DataCache.CachedData cachedData = parseOrCreateCache(root, path);
			map.put(string, cachedData);
			i += cachedData.size();
		}

		this.cachedDatas = map;
		this.totalSize = i;
	}

	private static DataCache.CachedData parseOrCreateCache(Path root, Path dataProviderPath) {
		if (Files.isReadable(dataProviderPath)) {
			try {
				return DataCache.CachedData.parseCache(root, dataProviderPath);
			} catch (Exception var3) {
				LOGGER.warn("Failed to parse cache {}, discarding", dataProviderPath, var3);
			}
		}

		return new DataCache.CachedData("unknown", ImmutableMap.of());
	}

	public boolean isVersionDifferent(String providerName) {
		DataCache.CachedData cachedData = (DataCache.CachedData)this.cachedDatas.get(providerName);
		return cachedData == null || !cachedData.version.equals(this.versionName);
	}

	public CompletableFuture<DataCache.RunResult> run(String providerName, DataCache.Runner runner) {
		DataCache.CachedData cachedData = (DataCache.CachedData)this.cachedDatas.get(providerName);
		if (cachedData == null) {
			throw new IllegalStateException("Provider not registered: " + providerName);
		} else {
			DataCache.CachedDataWriter cachedDataWriter = new DataCache.CachedDataWriter(providerName, this.versionName, cachedData);
			return runner.update(cachedDataWriter).thenApply(void_ -> cachedDataWriter.finish());
		}
	}

	public void store(DataCache.RunResult runResult) {
		this.cachedDatas.put(runResult.providerName(), runResult.cache());
		this.dataWriters.add(runResult.providerName());
		this.totalCacheMissCount = this.totalCacheMissCount + runResult.cacheMissCount();
	}

	public void write() throws IOException {
		final Set<Path> set = new HashSet();
		this.cachedDatas.forEach((providerName, cachedData) -> {
			if (this.dataWriters.contains(providerName)) {
				Path path = this.getPath(providerName);
				cachedData.write(this.root, path, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + "\t" + providerName);
			}

			set.addAll(cachedData.data().keySet());
		});
		set.add(this.root.resolve("version.json"));
		final MutableInt mutableInt = new MutableInt();
		final MutableInt mutableInt2 = new MutableInt();
		Files.walkFileTree(this.root, new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
				if (DataCache.this.paths.contains(path)) {
					return FileVisitResult.CONTINUE;
				} else {
					mutableInt.increment();
					if (set.contains(path)) {
						return FileVisitResult.CONTINUE;
					} else {
						try {
							Files.delete(path);
						} catch (IOException var4) {
							DataCache.LOGGER.warn("Failed to delete file {}", path, var4);
						}

						mutableInt2.increment();
						return FileVisitResult.CONTINUE;
					}
				}
			}
		});
		LOGGER.info(
			"Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}",
			mutableInt,
			this.totalSize,
			set.size(),
			mutableInt2,
			this.totalCacheMissCount
		);
	}

	static record CachedData(String version, ImmutableMap<Path, HashCode> data) {

		@Nullable
		public HashCode get(Path path) {
			return this.data.get(path);
		}

		public int size() {
			return this.data.size();
		}

		public static DataCache.CachedData parseCache(Path root, Path dataProviderPath) throws IOException {
			BufferedReader bufferedReader = Files.newBufferedReader(dataProviderPath, StandardCharsets.UTF_8);

			DataCache.CachedData var7;
			try {
				String string = bufferedReader.readLine();
				if (!string.startsWith("// ")) {
					throw new IllegalStateException("Missing cache file header");
				}

				String[] strings = string.substring("// ".length()).split("\t", 2);
				String string2 = strings[0];
				Builder<Path, HashCode> builder = ImmutableMap.builder();
				bufferedReader.lines().forEach(line -> {
					int i = line.indexOf(32);
					builder.put(root.resolve(line.substring(i + 1)), HashCode.fromString(line.substring(0, i)));
				});
				var7 = new DataCache.CachedData(string2, builder.build());
			} catch (Throwable var9) {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (Throwable var8) {
						var9.addSuppressed(var8);
					}
				}

				throw var9;
			}

			if (bufferedReader != null) {
				bufferedReader.close();
			}

			return var7;
		}

		public void write(Path root, Path dataProviderPath, String description) {
			try {
				BufferedWriter bufferedWriter = Files.newBufferedWriter(dataProviderPath, StandardCharsets.UTF_8);

				try {
					bufferedWriter.write("// ");
					bufferedWriter.write(this.version);
					bufferedWriter.write(9);
					bufferedWriter.write(description);
					bufferedWriter.newLine();

					for (Entry<Path, HashCode> entry : this.data.entrySet()) {
						bufferedWriter.write(((HashCode)entry.getValue()).toString());
						bufferedWriter.write(32);
						bufferedWriter.write(root.relativize((Path)entry.getKey()).toString());
						bufferedWriter.newLine();
					}
				} catch (Throwable var8) {
					if (bufferedWriter != null) {
						try {
							bufferedWriter.close();
						} catch (Throwable var7) {
							var8.addSuppressed(var7);
						}
					}

					throw var8;
				}

				if (bufferedWriter != null) {
					bufferedWriter.close();
				}
			} catch (IOException var9) {
				DataCache.LOGGER.warn("Unable write cachefile {}: {}", dataProviderPath, var9);
			}
		}
	}

	class CachedDataWriter implements DataWriter {
		private final String providerName;
		private final DataCache.CachedData oldCache;
		private final DataCache.IntermediaryCache newCache;
		private final AtomicInteger cacheMissCount = new AtomicInteger();
		private volatile boolean closed;

		CachedDataWriter(final String providerName, final String version, final DataCache.CachedData oldCache) {
			this.providerName = providerName;
			this.oldCache = oldCache;
			this.newCache = new DataCache.IntermediaryCache(version);
		}

		private boolean isCacheInvalid(Path path, HashCode hashCode) {
			return !Objects.equals(this.oldCache.get(path), hashCode) || !Files.exists(path, new LinkOption[0]);
		}

		@Override
		public void write(Path path, byte[] data, HashCode hashCode) throws IOException {
			if (this.closed) {
				throw new IllegalStateException("Cannot write to cache as it has already been closed");
			} else {
				if (this.isCacheInvalid(path, hashCode)) {
					this.cacheMissCount.incrementAndGet();
					Files.createDirectories(path.getParent());
					Files.write(path, data, new OpenOption[0]);
				}

				this.newCache.put(path, hashCode);
			}
		}

		public DataCache.RunResult finish() {
			this.closed = true;
			return new DataCache.RunResult(this.providerName, this.newCache.toCachedData(), this.cacheMissCount.get());
		}
	}

	static record IntermediaryCache(String version, ConcurrentMap<Path, HashCode> data) {
		IntermediaryCache(String version) {
			this(version, new ConcurrentHashMap());
		}

		public void put(Path path, HashCode hashCode) {
			this.data.put(path, hashCode);
		}

		public DataCache.CachedData toCachedData() {
			return new DataCache.CachedData(this.version, ImmutableMap.copyOf(this.data));
		}
	}

	public static record RunResult(String providerName, DataCache.CachedData cache, int cacheMissCount) {
	}

	@FunctionalInterface
	public interface Runner {
		CompletableFuture<?> update(DataWriter writer);
	}
}
