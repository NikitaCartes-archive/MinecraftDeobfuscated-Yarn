package net.minecraft.util.profiler;

import com.google.common.collect.ImmutableMap;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZipCompressor implements Closeable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Path profilingDirectory;
	private final Path temporaryDirectory;
	private final FileSystem zip;

	public ZipCompressor(Path profilingDirectory) {
		this.profilingDirectory = profilingDirectory;
		this.temporaryDirectory = profilingDirectory.resolveSibling(profilingDirectory.getFileName().toString() + "_tmp");

		try {
			this.zip = Util.zipFileSystemProvider.newFileSystem(this.temporaryDirectory, ImmutableMap.of("create", "true"));
		} catch (IOException var3) {
			throw new UncheckedIOException(var3);
		}
	}

	public void write(Path path, String content) {
		try {
			Path path2 = this.zip.getPath(File.separator);
			Path path3 = path2.resolve(path.toString());
			Files.createDirectories(path3.getParent());
			Files.write(path3, content.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
		} catch (IOException var5) {
			throw new UncheckedIOException(var5);
		}
	}

	public void copy(Path path, File file) {
		try {
			Path path2 = this.zip.getPath(File.separator);
			Path path3 = path2.resolve(path.toString());
			Files.createDirectories(path3.getParent());
			Files.copy(file.toPath(), path3);
		} catch (IOException var5) {
			throw new UncheckedIOException(var5);
		}
	}

	public void copyAll(Path path) {
		try {
			Path path2 = this.zip.getPath(File.separator);
			if (Files.isRegularFile(path, new LinkOption[0])) {
				Path path3 = path2.resolve(path.getParent().relativize(path).toString());
				Files.copy(path3, path);
			} else {
				Stream<Path> stream = Files.find(path, Integer.MAX_VALUE, (pathx, attributes) -> attributes.isRegularFile(), new FileVisitOption[0]);

				try {
					for (Path path4 : (List)stream.collect(Collectors.toList())) {
						Path path5 = path2.resolve(path.relativize(path4).toString());
						Files.createDirectories(path5.getParent());
						Files.copy(path4, path5);
					}
				} catch (Throwable var8) {
					if (stream != null) {
						try {
							stream.close();
						} catch (Throwable var7) {
							var8.addSuppressed(var7);
						}
					}

					throw var8;
				}

				if (stream != null) {
					stream.close();
				}
			}
		} catch (IOException var9) {
			throw new UncheckedIOException(var9);
		}
	}

	public void close() {
		try {
			this.zip.close();
			Files.move(this.temporaryDirectory, this.profilingDirectory);
			LOGGER.info("Compressed to {}", this.profilingDirectory);
		} catch (IOException var2) {
			throw new UncheckedIOException(var2);
		}
	}
}
