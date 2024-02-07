package net.minecraft.resource;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadataMap;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import org.slf4j.Logger;

public class DefaultResourcePack implements ResourcePack {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final ResourcePackInfo info;
	private final ResourceMetadataMap metadata;
	private final Set<String> namespaces;
	private final List<Path> rootPaths;
	private final Map<ResourceType, List<Path>> namespacePaths;

	DefaultResourcePack(
		ResourcePackInfo info, ResourceMetadataMap metadata, Set<String> namespaces, List<Path> rootPaths, Map<ResourceType, List<Path>> namespacePaths
	) {
		this.info = info;
		this.metadata = metadata;
		this.namespaces = namespaces;
		this.rootPaths = rootPaths;
		this.namespacePaths = namespacePaths;
	}

	@Nullable
	@Override
	public InputSupplier<InputStream> openRoot(String... segments) {
		PathUtil.validatePath(segments);
		List<String> list = List.of(segments);

		for (Path path : this.rootPaths) {
			Path path2 = PathUtil.getPath(path, list);
			if (Files.exists(path2, new LinkOption[0]) && DirectoryResourcePack.isValidPath(path2)) {
				return InputSupplier.create(path2);
			}
		}

		return null;
	}

	public void forEachNamespacedPath(ResourceType type, Identifier path, Consumer<Path> consumer) {
		PathUtil.split(path.getPath()).get().ifLeft(segments -> {
			String string = path.getNamespace();

			for (Path pathx : (List)this.namespacePaths.get(type)) {
				Path path2 = pathx.resolve(string);
				consumer.accept(PathUtil.getPath(path2, segments));
			}
		}).ifRight(result -> LOGGER.error("Invalid path {}: {}", path, result.message()));
	}

	@Override
	public void findResources(ResourceType type, String namespace, String prefix, ResourcePack.ResultConsumer consumer) {
		PathUtil.split(prefix).get().ifLeft(segments -> {
			List<Path> list = (List<Path>)this.namespacePaths.get(type);
			int i = list.size();
			if (i == 1) {
				collectIdentifiers(consumer, namespace, (Path)list.get(0), segments);
			} else if (i > 1) {
				Map<Identifier, InputSupplier<InputStream>> map = new HashMap();

				for (int j = 0; j < i - 1; j++) {
					collectIdentifiers(map::putIfAbsent, namespace, (Path)list.get(j), segments);
				}

				Path path = (Path)list.get(i - 1);
				if (map.isEmpty()) {
					collectIdentifiers(consumer, namespace, path, segments);
				} else {
					collectIdentifiers(map::putIfAbsent, namespace, path, segments);
					map.forEach(consumer);
				}
			}
		}).ifRight(result -> LOGGER.error("Invalid path {}: {}", prefix, result.message()));
	}

	private static void collectIdentifiers(ResourcePack.ResultConsumer consumer, String namespace, Path root, List<String> prefixSegments) {
		Path path = root.resolve(namespace);
		DirectoryResourcePack.findResources(namespace, path, prefixSegments, consumer);
	}

	@Nullable
	@Override
	public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
		return PathUtil.split(id.getPath()).get().map(segments -> {
			String string = id.getNamespace();

			for (Path path : (List)this.namespacePaths.get(type)) {
				Path path2 = PathUtil.getPath(path.resolve(string), segments);
				if (Files.exists(path2, new LinkOption[0]) && DirectoryResourcePack.isValidPath(path2)) {
					return InputSupplier.create(path2);
				}
			}

			return null;
		}, result -> {
			LOGGER.error("Invalid path {}: {}", id, result.message());
			return null;
		});
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return this.namespaces;
	}

	@Nullable
	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) {
		InputSupplier<InputStream> inputSupplier = this.openRoot("pack.mcmeta");
		if (inputSupplier != null) {
			try {
				InputStream inputStream = inputSupplier.get();

				Object var5;
				label53: {
					try {
						T object = AbstractFileResourcePack.parseMetadata(metaReader, inputStream);
						if (object != null) {
							var5 = object;
							break label53;
						}
					} catch (Throwable var7) {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (Throwable var6) {
								var7.addSuppressed(var6);
							}
						}

						throw var7;
					}

					if (inputStream != null) {
						inputStream.close();
					}

					return this.metadata.get(metaReader);
				}

				if (inputStream != null) {
					inputStream.close();
				}

				return (T)var5;
			} catch (IOException var8) {
			}
		}

		return this.metadata.get(metaReader);
	}

	@Override
	public ResourcePackInfo getInfo() {
		return this.info;
	}

	@Override
	public void close() {
	}

	public ResourceFactory getFactory() {
		return id -> Optional.ofNullable(this.open(ResourceType.CLIENT_RESOURCES, id)).map(stream -> new Resource(this, stream));
	}
}
