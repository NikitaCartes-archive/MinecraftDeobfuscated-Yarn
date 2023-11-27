package net.minecraft.util.path;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;

public class CacheFiles {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static void clear(Path directory, int maxRetained) {
		try {
			List<CacheFiles.CacheFile> list = findCacheFiles(directory);
			int i = list.size() - maxRetained;
			if (i <= 0) {
				return;
			}

			list.sort(CacheFiles.CacheFile.COMPARATOR);
			List<CacheFiles.CacheEntry> list2 = toCacheEntries(list);
			Collections.reverse(list2);
			list2.sort(CacheFiles.CacheEntry.COMPARATOR);
			Set<Path> set = new HashSet();

			for (int j = 0; j < i; j++) {
				CacheFiles.CacheEntry cacheEntry = (CacheFiles.CacheEntry)list2.get(j);
				Path path = cacheEntry.path;

				try {
					Files.delete(path);
					if (cacheEntry.removalPriority == 0) {
						set.add(path.getParent());
					}
				} catch (IOException var12) {
					LOGGER.warn("Failed to delete cache file {}", path, var12);
				}
			}

			set.remove(directory);

			for (Path path2 : set) {
				try {
					Files.delete(path2);
				} catch (DirectoryNotEmptyException var10) {
				} catch (IOException var11) {
					LOGGER.warn("Failed to delete empty(?) cache directory {}", path2, var11);
				}
			}
		} catch (UncheckedIOException | IOException var13) {
			LOGGER.error("Failed to vacuum cache dir {}", directory, var13);
		}
	}

	private static List<CacheFiles.CacheFile> findCacheFiles(Path directory) throws IOException {
		try {
			final List<CacheFiles.CacheFile> list = new ArrayList();
			Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
				public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
					if (basicFileAttributes.isRegularFile() && !path.getParent().equals(directory)) {
						FileTime fileTime = basicFileAttributes.lastModifiedTime();
						list.add(new CacheFiles.CacheFile(path, fileTime));
					}

					return FileVisitResult.CONTINUE;
				}
			});
			return list;
		} catch (NoSuchFileException var2) {
			return List.of();
		}
	}

	private static List<CacheFiles.CacheEntry> toCacheEntries(List<CacheFiles.CacheFile> files) {
		List<CacheFiles.CacheEntry> list = new ArrayList();
		Object2IntOpenHashMap<Path> object2IntOpenHashMap = new Object2IntOpenHashMap<>();

		for (CacheFiles.CacheFile cacheFile : files) {
			int i = object2IntOpenHashMap.addTo(cacheFile.path.getParent(), 1);
			list.add(new CacheFiles.CacheEntry(cacheFile.path, i));
		}

		return list;
	}

	static record CacheEntry(Path path, int removalPriority) {
		public static final Comparator<CacheFiles.CacheEntry> COMPARATOR = Comparator.comparing(CacheFiles.CacheEntry::removalPriority).reversed();
	}

	static record CacheFile(Path path, FileTime modifiedTime) {
		public static final Comparator<CacheFiles.CacheFile> COMPARATOR = Comparator.comparing(CacheFiles.CacheFile::modifiedTime).reversed();
	}
}
