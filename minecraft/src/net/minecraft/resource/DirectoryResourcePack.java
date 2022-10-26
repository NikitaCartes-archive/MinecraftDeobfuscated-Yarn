package net.minecraft.resource;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class DirectoryResourcePack extends AbstractFileResourcePack {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Joiner SEPARATOR_JOINER = Joiner.on("/");
	private final Path root;

	public DirectoryResourcePack(String name, Path root) {
		super(name);
		this.root = root;
	}

	@Nullable
	@Override
	public InputSupplier<InputStream> openRoot(String... segments) {
		PathUtil.validatePath(segments);
		Path path = PathUtil.getPath(this.root, List.of(segments));
		return Files.exists(path, new LinkOption[0]) ? InputSupplier.create(path) : null;
	}

	public static boolean isValidPath(Path path) {
		return true;
	}

	@Nullable
	@Override
	public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
		Path path = this.root.resolve(type.getDirectory()).resolve(id.getNamespace());
		return open(id, path);
	}

	public static InputSupplier<InputStream> open(Identifier id, Path path) {
		return PathUtil.split(id.getPath()).get().map(segments -> {
			Path path2 = PathUtil.getPath(path, segments);
			return open(path2);
		}, result -> {
			LOGGER.error("Invalid path {}: {}", id, result.message());
			return null;
		});
	}

	@Nullable
	private static InputSupplier<InputStream> open(Path path) {
		return Files.exists(path, new LinkOption[0]) && isValidPath(path) ? InputSupplier.create(path) : null;
	}

	@Override
	public void findResources(ResourceType type, String namespace, String prefix, ResourcePack.ResultConsumer consumer) {
		PathUtil.split(prefix).get().ifLeft(prefixSegments -> {
			Path path = this.root.resolve(type.getDirectory()).resolve(namespace);
			findResources(namespace, path, prefixSegments, consumer);
		}).ifRight(result -> LOGGER.error("Invalid path {}: {}", prefix, result.message()));
	}

	public static void findResources(String namespace, Path path, List<String> prefixSegments, ResourcePack.ResultConsumer consumer) {
		Path path2 = PathUtil.getPath(path, prefixSegments);

		try {
			Stream<Path> stream = Files.find(path2, Integer.MAX_VALUE, (path2x, attributes) -> attributes.isRegularFile(), new FileVisitOption[0]);

			try {
				stream.forEach(foundPath -> {
					String string2 = SEPARATOR_JOINER.join(path.relativize(foundPath));
					Identifier identifier = Identifier.of(namespace, string2);
					if (identifier == null) {
						Util.error(String.format(Locale.ROOT, "Invalid path in pack: %s:%s, ignoring", namespace, string2));
					} else {
						consumer.accept(identifier, InputSupplier.create(foundPath));
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
		} catch (NoSuchFileException var10) {
		} catch (IOException var11) {
			LOGGER.error("Failed to list path {}", path2, var11);
		}
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		Set<String> set = Sets.<String>newHashSet();
		Path path = this.root.resolve(type.getDirectory());

		try {
			DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);

			try {
				for (Path path2 : directoryStream) {
					String string = path2.getFileName().toString();
					if (string.equals(string.toLowerCase(Locale.ROOT))) {
						set.add(string);
					} else {
						LOGGER.warn("Ignored non-lowercase namespace: {} in {}", string, this.root);
					}
				}
			} catch (Throwable var9) {
				if (directoryStream != null) {
					try {
						directoryStream.close();
					} catch (Throwable var8) {
						var9.addSuppressed(var8);
					}
				}

				throw var9;
			}

			if (directoryStream != null) {
				directoryStream.close();
			}
		} catch (NoSuchFileException var10) {
		} catch (IOException var11) {
			LOGGER.error("Failed to list path {}", path, var11);
		}

		return set;
	}

	@Override
	public void close() {
	}
}
