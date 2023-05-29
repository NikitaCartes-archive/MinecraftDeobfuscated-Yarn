package net.minecraft.util.path;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class AllowedSymlinkPathMatcher implements PathMatcher {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String COMMENT_LINE_PREFIX = "#";
	private final List<AllowedSymlinkPathMatcher.Entry> allowedEntries;
	private final Map<String, PathMatcher> matcherCache = new ConcurrentHashMap();

	public AllowedSymlinkPathMatcher(List<AllowedSymlinkPathMatcher.Entry> allowedEntries) {
		this.allowedEntries = allowedEntries;
	}

	public PathMatcher get(FileSystem fileSystem) {
		return (PathMatcher)this.matcherCache.computeIfAbsent(fileSystem.provider().getScheme(), scheme -> {
			List<PathMatcher> list;
			try {
				list = this.allowedEntries.stream().map(entry -> entry.compile(fileSystem)).toList();
			} catch (Exception var5) {
				LOGGER.error("Failed to compile file pattern list", (Throwable)var5);
				return path -> false;
			}
			return switch (list.size()) {
				case 0 -> path -> false;
				case 1 -> (PathMatcher)list.get(0);
				default -> path -> {
				for (PathMatcher pathMatcher : list) {
					if (pathMatcher.matches(path)) {
						return true;
					}
				}

				return false;
			};
			};
		});
	}

	public boolean matches(Path path) {
		return this.get(path.getFileSystem()).matches(path);
	}

	public static AllowedSymlinkPathMatcher fromReader(BufferedReader reader) {
		return new AllowedSymlinkPathMatcher(reader.lines().flatMap(line -> AllowedSymlinkPathMatcher.Entry.readLine(line).stream()).toList());
	}

	public static record Entry(AllowedSymlinkPathMatcher.EntryType type, String pattern) {
		public PathMatcher compile(FileSystem fileSystem) {
			return this.type().compile(fileSystem, this.pattern);
		}

		static Optional<AllowedSymlinkPathMatcher.Entry> readLine(String line) {
			if (line.isBlank() || line.startsWith("#")) {
				return Optional.empty();
			} else if (!line.startsWith("[")) {
				return Optional.of(new AllowedSymlinkPathMatcher.Entry(AllowedSymlinkPathMatcher.EntryType.PREFIX, line));
			} else {
				int i = line.indexOf(93, 1);
				if (i == -1) {
					throw new IllegalArgumentException("Unterminated type in line '" + line + "'");
				} else {
					String string = line.substring(1, i);
					String string2 = line.substring(i + 1);

					return switch (string) {
						case "glob", "regex" -> Optional.of(new AllowedSymlinkPathMatcher.Entry(AllowedSymlinkPathMatcher.EntryType.DEFAULT, string + ":" + string2));
						case "prefix" -> Optional.of(new AllowedSymlinkPathMatcher.Entry(AllowedSymlinkPathMatcher.EntryType.PREFIX, string2));
						default -> throw new IllegalArgumentException("Unsupported definition type in line '" + line + "'");
					};
				}
			}
		}

		static AllowedSymlinkPathMatcher.Entry glob(String pattern) {
			return new AllowedSymlinkPathMatcher.Entry(AllowedSymlinkPathMatcher.EntryType.DEFAULT, "glob:" + pattern);
		}

		static AllowedSymlinkPathMatcher.Entry regex(String pattern) {
			return new AllowedSymlinkPathMatcher.Entry(AllowedSymlinkPathMatcher.EntryType.DEFAULT, "regex:" + pattern);
		}

		static AllowedSymlinkPathMatcher.Entry prefix(String prefix) {
			return new AllowedSymlinkPathMatcher.Entry(AllowedSymlinkPathMatcher.EntryType.PREFIX, prefix);
		}
	}

	@FunctionalInterface
	public interface EntryType {
		AllowedSymlinkPathMatcher.EntryType DEFAULT = FileSystem::getPathMatcher;
		AllowedSymlinkPathMatcher.EntryType PREFIX = (fileSystem, prefix) -> path -> path.toString().startsWith(prefix);

		PathMatcher compile(FileSystem fileSystem, String pattern);
	}
}
