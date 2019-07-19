package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultResourcePack implements ResourcePack {
	public static Path resourcePath;
	private static final Logger LOGGER = LogManager.getLogger();
	public static Class<?> resourceClass;
	private static final Map<ResourceType, FileSystem> typeToFileSystem = Util.make(Maps.<ResourceType, FileSystem>newHashMap(), hashMap -> {
		synchronized (DefaultResourcePack.class) {
			for (ResourceType resourceType : ResourceType.values()) {
				URL uRL = DefaultResourcePack.class.getResource("/" + resourceType.getDirectory() + "/.mcassetsroot");

				try {
					URI uRI = uRL.toURI();
					if ("jar".equals(uRI.getScheme())) {
						FileSystem fileSystem;
						try {
							fileSystem = FileSystems.getFileSystem(uRI);
						} catch (FileSystemNotFoundException var11) {
							fileSystem = FileSystems.newFileSystem(uRI, Collections.emptyMap());
						}

						hashMap.put(resourceType, fileSystem);
					}
				} catch (IOException | URISyntaxException var12) {
					LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)var12);
				}
			}
		}
	});
	public final Set<String> namespaces;

	public DefaultResourcePack(String... namespaces) {
		this.namespaces = ImmutableSet.copyOf(namespaces);
	}

	@Override
	public InputStream openRoot(String fileName) throws IOException {
		if (!fileName.contains("/") && !fileName.contains("\\")) {
			if (resourcePath != null) {
				Path path = resourcePath.resolve(fileName);
				if (Files.exists(path, new LinkOption[0])) {
					return Files.newInputStream(path);
				}
			}

			return this.getInputStream(fileName);
		} else {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
	}

	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		InputStream inputStream = this.findInputStream(type, id);
		if (inputStream != null) {
			return inputStream;
		} else {
			throw new FileNotFoundException(id.getPath());
		}
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, int maxDepth, Predicate<String> pathFilter) {
		Set<Identifier> set = Sets.<Identifier>newHashSet();
		if (resourcePath != null) {
			try {
				set.addAll(this.getIdentifiers(maxDepth, "minecraft", resourcePath.resolve(type.getDirectory()).resolve("minecraft"), namespace, pathFilter));
			} catch (IOException var14) {
			}

			if (type == ResourceType.CLIENT_RESOURCES) {
				Enumeration<URL> enumeration = null;

				try {
					enumeration = resourceClass.getClassLoader().getResources(type.getDirectory() + "/minecraft");
				} catch (IOException var13) {
				}

				while (enumeration != null && enumeration.hasMoreElements()) {
					try {
						URI uRI = ((URL)enumeration.nextElement()).toURI();
						if ("file".equals(uRI.getScheme())) {
							set.addAll(this.getIdentifiers(maxDepth, "minecraft", Paths.get(uRI), namespace, pathFilter));
						}
					} catch (IOException | URISyntaxException var12) {
					}
				}
			}
		}

		try {
			URL uRL = DefaultResourcePack.class.getResource("/" + type.getDirectory() + "/.mcassetsroot");
			if (uRL == null) {
				LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
				return set;
			}

			URI uRI = uRL.toURI();
			if ("file".equals(uRI.getScheme())) {
				URL uRL2 = new URL(uRL.toString().substring(0, uRL.toString().length() - ".mcassetsroot".length()) + "minecraft");
				if (uRL2 == null) {
					return set;
				}

				Path path = Paths.get(uRL2.toURI());
				set.addAll(this.getIdentifiers(maxDepth, "minecraft", path, namespace, pathFilter));
			} else if ("jar".equals(uRI.getScheme())) {
				Path path2 = ((FileSystem)typeToFileSystem.get(type)).getPath("/" + type.getDirectory() + "/minecraft");
				set.addAll(this.getIdentifiers(maxDepth, "minecraft", path2, namespace, pathFilter));
			} else {
				LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", uRI);
			}
		} catch (NoSuchFileException | FileNotFoundException var10) {
		} catch (IOException | URISyntaxException var11) {
			LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)var11);
		}

		return set;
	}

	private Collection<Identifier> getIdentifiers(int maxDepth, String namespace, Path path, String searchLocation, Predicate<String> pathFilter) throws IOException {
		List<Identifier> list = Lists.<Identifier>newArrayList();
		Iterator<Path> iterator = Files.walk(path.resolve(searchLocation), maxDepth, new FileVisitOption[0]).iterator();

		while (iterator.hasNext()) {
			Path path2 = (Path)iterator.next();
			if (!path2.endsWith(".mcmeta") && Files.isRegularFile(path2, new LinkOption[0]) && pathFilter.test(path2.getFileName().toString())) {
				list.add(new Identifier(namespace, path.relativize(path2).toString().replaceAll("\\\\", "/")));
			}
		}

		return list;
	}

	@Nullable
	protected InputStream findInputStream(ResourceType type, Identifier id) {
		String string = method_20729(type, id);
		if (resourcePath != null) {
			Path path = resourcePath.resolve(type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath());
			if (Files.exists(path, new LinkOption[0])) {
				try {
					return Files.newInputStream(path);
				} catch (IOException var7) {
				}
			}
		}

		try {
			URL uRL = DefaultResourcePack.class.getResource(string);
			return method_20728(string, uRL) ? uRL.openStream() : null;
		} catch (IOException var6) {
			return DefaultResourcePack.class.getResourceAsStream(string);
		}
	}

	private static String method_20729(ResourceType resourceType, Identifier identifier) {
		return "/" + resourceType.getDirectory() + "/" + identifier.getNamespace() + "/" + identifier.getPath();
	}

	private static boolean method_20728(String string, @Nullable URL uRL) throws IOException {
		return uRL != null && (uRL.getProtocol().equals("jar") || DirectoryResourcePack.isValidPath(new File(uRL.getFile()), string));
	}

	@Nullable
	protected InputStream getInputStream(String path) {
		return DefaultResourcePack.class.getResourceAsStream("/" + path);
	}

	@Override
	public boolean contains(ResourceType type, Identifier id) {
		String string = method_20729(type, id);
		if (resourcePath != null) {
			Path path = resourcePath.resolve(type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath());
			if (Files.exists(path, new LinkOption[0])) {
				return true;
			}
		}

		try {
			URL uRL = DefaultResourcePack.class.getResource(string);
			return method_20728(string, uRL);
		} catch (IOException var5) {
			return false;
		}
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return this.namespaces;
	}

	@Nullable
	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		try {
			InputStream inputStream = this.openRoot("pack.mcmeta");
			Throwable var3 = null;

			Object var4;
			try {
				var4 = AbstractFileResourcePack.parseMetadata(metaReader, inputStream);
			} catch (Throwable var14) {
				var3 = var14;
				throw var14;
			} finally {
				if (inputStream != null) {
					if (var3 != null) {
						try {
							inputStream.close();
						} catch (Throwable var13) {
							var3.addSuppressed(var13);
						}
					} else {
						inputStream.close();
					}
				}
			}

			return (T)var4;
		} catch (FileNotFoundException | RuntimeException var16) {
			return null;
		}
	}

	@Override
	public String getName() {
		return "Default";
	}

	public void close() {
	}
}
