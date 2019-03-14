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
import net.minecraft.util.SystemUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultResourcePack implements ResourcePack {
	public static Path RESOURCE_PATH;
	private static final Logger LOGGER = LogManager.getLogger();
	public static Class<?> RESOURCE_CLASS;
	private static final Map<ResourceType, FileSystem> field_17917 = SystemUtil.consume(Maps.<ResourceType, FileSystem>newHashMap(), hashMap -> {
		synchronized (DefaultResourcePack.class) {
			for (ResourceType resourceType : ResourceType.values()) {
				URL uRL = DefaultResourcePack.class.getResource("/" + resourceType.getName() + "/.mcassetsroot");

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

	public DefaultResourcePack(String... strings) {
		this.namespaces = ImmutableSet.copyOf(strings);
	}

	@Override
	public InputStream openRoot(String string) throws IOException {
		if (!string.contains("/") && !string.contains("\\")) {
			if (RESOURCE_PATH != null) {
				Path path = RESOURCE_PATH.resolve(string);
				if (Files.exists(path, new LinkOption[0])) {
					return Files.newInputStream(path);
				}
			}

			return this.getInputStream(string);
		} else {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
	}

	@Override
	public InputStream open(ResourceType resourceType, Identifier identifier) throws IOException {
		InputStream inputStream = this.findInputStream(resourceType, identifier);
		if (inputStream != null) {
			return inputStream;
		} else {
			throw new FileNotFoundException(identifier.getPath());
		}
	}

	@Override
	public Collection<Identifier> findResources(ResourceType resourceType, String string, int i, Predicate<String> predicate) {
		Set<Identifier> set = Sets.<Identifier>newHashSet();
		if (RESOURCE_PATH != null) {
			try {
				set.addAll(this.method_14418(i, "minecraft", RESOURCE_PATH.resolve(resourceType.getName()).resolve("minecraft"), string, predicate));
			} catch (IOException var14) {
			}

			if (resourceType == ResourceType.ASSETS) {
				Enumeration<URL> enumeration = null;

				try {
					enumeration = RESOURCE_CLASS.getClassLoader().getResources(resourceType.getName() + "/minecraft");
				} catch (IOException var13) {
				}

				while (enumeration != null && enumeration.hasMoreElements()) {
					try {
						URI uRI = ((URL)enumeration.nextElement()).toURI();
						if ("file".equals(uRI.getScheme())) {
							set.addAll(this.method_14418(i, "minecraft", Paths.get(uRI), string, predicate));
						}
					} catch (IOException | URISyntaxException var12) {
					}
				}
			}
		}

		try {
			URL uRL = DefaultResourcePack.class.getResource("/" + resourceType.getName() + "/.mcassetsroot");
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
				set.addAll(this.method_14418(i, "minecraft", path, string, predicate));
			} else if ("jar".equals(uRI.getScheme())) {
				Path path2 = ((FileSystem)field_17917.get(resourceType)).getPath("/" + resourceType.getName() + "/minecraft");
				set.addAll(this.method_14418(i, "minecraft", path2, string, predicate));
			} else {
				LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", uRI);
			}
		} catch (NoSuchFileException | FileNotFoundException var10) {
		} catch (IOException | URISyntaxException var11) {
			LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)var11);
		}

		return set;
	}

	private Collection<Identifier> method_14418(int i, String string, Path path, String string2, Predicate<String> predicate) throws IOException {
		List<Identifier> list = Lists.<Identifier>newArrayList();
		Iterator<Path> iterator = Files.walk(path.resolve(string2), i, new FileVisitOption[0]).iterator();

		while (iterator.hasNext()) {
			Path path2 = (Path)iterator.next();
			if (!path2.endsWith(".mcmeta") && Files.isRegularFile(path2, new LinkOption[0]) && predicate.test(path2.getFileName().toString())) {
				list.add(new Identifier(string, path.relativize(path2).toString().replaceAll("\\\\", "/")));
			}
		}

		return list;
	}

	@Nullable
	protected InputStream findInputStream(ResourceType resourceType, Identifier identifier) {
		String string = "/" + resourceType.getName() + "/" + identifier.getNamespace() + "/" + identifier.getPath();
		if (RESOURCE_PATH != null) {
			Path path = RESOURCE_PATH.resolve(resourceType.getName() + "/" + identifier.getNamespace() + "/" + identifier.getPath());
			if (Files.exists(path, new LinkOption[0])) {
				try {
					return Files.newInputStream(path);
				} catch (IOException var7) {
				}
			}
		}

		try {
			URL uRL = DefaultResourcePack.class.getResource(string);
			return uRL != null && DirectoryResourcePack.isValidPath(new File(uRL.getFile()), string) ? DefaultResourcePack.class.getResourceAsStream(string) : null;
		} catch (IOException var6) {
			return DefaultResourcePack.class.getResourceAsStream(string);
		}
	}

	@Nullable
	protected InputStream getInputStream(String string) {
		return DefaultResourcePack.class.getResourceAsStream("/" + string);
	}

	@Override
	public boolean contains(ResourceType resourceType, Identifier identifier) {
		InputStream inputStream = this.findInputStream(resourceType, identifier);
		boolean bl = inputStream != null;
		IOUtils.closeQuietly(inputStream);
		return bl;
	}

	@Override
	public Set<String> getNamespaces(ResourceType resourceType) {
		return this.namespaces;
	}

	@Nullable
	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> resourceMetadataReader) throws IOException {
		try {
			InputStream inputStream = this.openRoot("pack.mcmeta");
			Throwable var3 = null;

			Object var4;
			try {
				var4 = AbstractFilenameResourcePack.parseMetadata(resourceMetadataReader, inputStream);
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
