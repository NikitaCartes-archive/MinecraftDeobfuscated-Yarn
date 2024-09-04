package net.minecraft.util;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.logging.LogWriter;
import net.minecraft.util.path.CacheFiles;
import net.minecraft.util.thread.SimpleConsecutiveExecutor;
import org.slf4j.Logger;

public class Downloader implements AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int MAX_RETAINED_CACHE_FILES = 20;
	private final Path directory;
	private final LogWriter<Downloader.LogEntry> logWriter;
	private final SimpleConsecutiveExecutor executor = new SimpleConsecutiveExecutor(Util.getDownloadWorkerExecutor(), "download-queue");

	public Downloader(Path directory) throws IOException {
		this.directory = directory;
		PathUtil.createDirectories(directory);
		this.logWriter = LogWriter.create(Downloader.LogEntry.CODEC, directory.resolve("log.json"));
		CacheFiles.clear(directory, 20);
	}

	private Downloader.DownloadResult download(Downloader.Config config, Map<UUID, Downloader.DownloadEntry> entries) {
		Downloader.DownloadResult downloadResult = new Downloader.DownloadResult();
		entries.forEach(
			(id, entry) -> {
				Path path = this.directory.resolve(id.toString());
				Path path2 = null;

				try {
					path2 = NetworkUtils.download(path, entry.url, config.headers, config.hashFunction, entry.hash, config.maxSize, config.proxy, config.listener);
					downloadResult.downloaded.put(id, path2);
				} catch (Exception var9) {
					LOGGER.error("Failed to download {}", entry.url, var9);
					downloadResult.failed.add(id);
				}

				try {
					this.logWriter
						.write(
							new Downloader.LogEntry(
								id,
								entry.url.toString(),
								Instant.now(),
								Optional.ofNullable(entry.hash).map(HashCode::toString),
								path2 != null ? this.getFileInfo(path2) : Either.left("download_failed")
							)
						);
				} catch (Exception var8) {
					LOGGER.error("Failed to log download of {}", entry.url, var8);
				}
			}
		);
		return downloadResult;
	}

	private Either<String, Downloader.FileInfo> getFileInfo(Path path) {
		try {
			long l = Files.size(path);
			Path path2 = this.directory.relativize(path);
			return Either.right(new Downloader.FileInfo(path2.toString(), l));
		} catch (IOException var5) {
			LOGGER.error("Failed to get file size of {}", path, var5);
			return Either.left("no_access");
		}
	}

	public CompletableFuture<Downloader.DownloadResult> downloadAsync(Downloader.Config config, Map<UUID, Downloader.DownloadEntry> entries) {
		return CompletableFuture.supplyAsync(() -> this.download(config, entries), this.executor::send);
	}

	public void close() throws IOException {
		this.executor.close();
		this.logWriter.close();
	}

	public static record Config(HashFunction hashFunction, int maxSize, Map<String, String> headers, Proxy proxy, NetworkUtils.DownloadListener listener) {
	}

	public static record DownloadEntry(URL url, @Nullable HashCode hash) {
	}

	public static record DownloadResult(Map<UUID, Path> downloaded, Set<UUID> failed) {

		public DownloadResult() {
			this(new HashMap(), new HashSet());
		}
	}

	static record FileInfo(String name, long size) {
		public static final Codec<Downloader.FileInfo> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.STRING.fieldOf("name").forGetter(Downloader.FileInfo::name), Codec.LONG.fieldOf("size").forGetter(Downloader.FileInfo::size)
					)
					.apply(instance, Downloader.FileInfo::new)
		);
	}

	static record LogEntry(UUID id, String url, Instant time, Optional<String> hash, Either<String, Downloader.FileInfo> errorOrFileInfo) {
		public static final Codec<Downloader.LogEntry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Uuids.STRING_CODEC.fieldOf("id").forGetter(Downloader.LogEntry::id),
						Codec.STRING.fieldOf("url").forGetter(Downloader.LogEntry::url),
						Codecs.INSTANT.fieldOf("time").forGetter(Downloader.LogEntry::time),
						Codec.STRING.optionalFieldOf("hash").forGetter(Downloader.LogEntry::hash),
						Codec.mapEither(Codec.STRING.fieldOf("error"), Downloader.FileInfo.CODEC.fieldOf("file")).forGetter(Downloader.LogEntry::errorOrFileInfo)
					)
					.apply(instance, Downloader.LogEntry::new)
		);
	}
}
