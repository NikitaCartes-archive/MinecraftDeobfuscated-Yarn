package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
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
	private final Map<DataProvider, DataCache.CachedData> cachedDatas;
	private final Map<DataProvider, DataCache.CachedDataWriter> dataWriters = new HashMap();
	private final Set<Path> paths = new HashSet();
	private final int totalSize;

	private Path getPath(DataProvider dataProvider) {
		return this.cachePath.resolve(Hashing.sha1().hashString(dataProvider.getName(), StandardCharsets.UTF_8).toString());
	}

	public DataCache(Path root, List<DataProvider> dataProviders, GameVersion gameVersion) throws IOException {
		this.versionName = gameVersion.getName();
		this.root = root;
		this.cachePath = root.resolve(".cache");
		Files.createDirectories(this.cachePath);
		Map<DataProvider, DataCache.CachedData> map = new HashMap();
		int i = 0;

		for (DataProvider dataProvider : dataProviders) {
			Path path = this.getPath(dataProvider);
			this.paths.add(path);
			DataCache.CachedData cachedData = parseOrCreateCache(root, path);
			map.put(dataProvider, cachedData);
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

		return new DataCache.CachedData("unknown");
	}

	public boolean isVersionDifferent(DataProvider dataProvider) {
		DataCache.CachedData cachedData = (DataCache.CachedData)this.cachedDatas.get(dataProvider);
		return cachedData == null || !cachedData.version.equals(this.versionName);
	}

	public DataWriter getOrCreateWriter(DataProvider dataProvider) {
		return (DataWriter)this.dataWriters.computeIfAbsent(dataProvider, provider -> {
			DataCache.CachedData cachedData = (DataCache.CachedData)this.cachedDatas.get(provider);
			if (cachedData == null) {
				throw new IllegalStateException("Provider not registered: " + provider.getName());
			} else {
				DataCache.CachedDataWriter cachedDataWriter = new DataCache.CachedDataWriter(this.versionName, cachedData);
				this.cachedDatas.put(provider, cachedDataWriter.newCache);
				return cachedDataWriter;
			}
		});
	}

	public void write() throws IOException {
		MutableInt mutableInt = new MutableInt();
		this.dataWriters.forEach((dataProvider, writer) -> {
			Path path = this.getPath(dataProvider);
			writer.newCache.write(this.root, path, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + "\t" + dataProvider.getName());
			mutableInt.add(writer.cacheMissCount);
		});
		Set<Path> set = new HashSet();
		this.cachedDatas.values().forEach(cachedData -> set.addAll(cachedData.data().keySet()));
		set.add(this.root.resolve("version.json"));
		MutableInt mutableInt2 = new MutableInt();
		MutableInt mutableInt3 = new MutableInt();
		Stream<Path> stream = Files.walk(this.root);

		try {
			stream.forEach(path -> {
				if (!Files.isDirectory(path, new LinkOption[0])) {
					if (!this.paths.contains(path)) {
						mutableInt2.increment();
						if (!set.contains(path)) {
							try {
								Files.delete(path);
							} catch (IOException var6) {
								LOGGER.warn("Failed to delete file {}", path, var6);
							}

							mutableInt3.increment();
						}
					}
				}
			});
		} catch (Throwable var9) {
			if (stream != null) {
				try {
					stream.close();
				} catch (Throwable var8) {
					var9.addSuppressed(var8);
				}
			}

			throw var9;
		}

		if (stream != null) {
			stream.close();
		}

		LOGGER.info(
			"Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}", mutableInt2, this.totalSize, set.size(), mutableInt3, mutableInt
		);
	}

	static record CachedData(String version, Map<Path, String> data) {

		CachedData(String version) {
			this(version, new HashMap());
		}

		@Nullable
		public String get(Path path) {
			return (String)this.data.get(path);
		}

		public void put(Path path, String value) {
			this.data.put(path, value);
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
				Map<Path, String> map = new HashMap();
				bufferedReader.lines().forEach(line -> {
					int i = line.indexOf(32);
					map.put(root.resolve(line.substring(i + 1)), line.substring(0, i));
				});
				var7 = new DataCache.CachedData(string2, Map.copyOf(map));
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

					for (Entry<Path, String> entry : this.data.entrySet()) {
						bufferedWriter.write((String)entry.getValue());
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

	static class CachedDataWriter implements DataWriter {
		private final DataCache.CachedData oldCache;
		final DataCache.CachedData newCache;
		int cacheMissCount;

		CachedDataWriter(String versionName, DataCache.CachedData cachedData) {
			this.oldCache = cachedData;
			this.newCache = new DataCache.CachedData(versionName);
		}

		private boolean isCacheInvalid(Path path, String hash) {
			return !Objects.equals(this.oldCache.get(path), hash) || !Files.exists(path, new LinkOption[0]);
		}

		@Override
		public void write(Path path, String data) throws IOException {
			String string = Hashing.sha1().hashUnencodedChars(data).toString();
			if (this.isCacheInvalid(path, string)) {
				this.cacheMissCount++;
				Files.createDirectories(path.getParent());
				BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8);

				try {
					bufferedWriter.write(data);
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
			}

			this.newCache.put(path, string);
		}

		@Override
		public void write(Path path, byte[] data, String hash) throws IOException {
			if (this.isCacheInvalid(path, hash)) {
				this.cacheMissCount++;
				Files.createDirectories(path.getParent());
				OutputStream outputStream = Files.newOutputStream(path);

				try {
					outputStream.write(data);
				} catch (Throwable var8) {
					if (outputStream != null) {
						try {
							outputStream.close();
						} catch (Throwable var7) {
							var8.addSuppressed(var7);
						}
					}

					throw var8;
				}

				if (outputStream != null) {
					outputStream.close();
				}
			}

			this.newCache.put(path, hash);
		}
	}
}
