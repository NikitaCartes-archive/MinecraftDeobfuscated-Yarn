package net.minecraft.resource.fs;

import com.google.common.base.Splitter;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class ResourceFileSystem extends FileSystem {
	private static final Set<String> SUPPORTED_FILE_ATTRIBUTE_VIEWS = Set.of("basic");
	public static final String SEPARATOR = "/";
	private static final Splitter SEPARATOR_SPLITTER = Splitter.on('/');
	private final FileStore store;
	private final FileSystemProvider fileSystemProvider = new ResourceFileSystemProvider();
	private final ResourcePath root;

	ResourceFileSystem(String name, ResourceFileSystem.Directory root) {
		this.store = new ResourceFileStore(name);
		this.root = toResourcePath(root, this, "", null);
	}

	private static ResourcePath toResourcePath(ResourceFileSystem.Directory root, ResourceFileSystem fileSystem, String name, @Nullable ResourcePath parent) {
		Object2ObjectOpenHashMap<String, ResourcePath> object2ObjectOpenHashMap = new Object2ObjectOpenHashMap<>();
		ResourcePath resourcePath = new ResourcePath(fileSystem, name, parent, new ResourceFile.Directory(object2ObjectOpenHashMap));
		root.files
			.forEach((fileName, path) -> object2ObjectOpenHashMap.put(fileName, new ResourcePath(fileSystem, fileName, resourcePath, new ResourceFile.File(path))));
		root.children
			.forEach((directoryName, directory) -> object2ObjectOpenHashMap.put(directoryName, toResourcePath(directory, fileSystem, directoryName, resourcePath)));
		object2ObjectOpenHashMap.trim();
		return resourcePath;
	}

	public FileSystemProvider provider() {
		return this.fileSystemProvider;
	}

	public void close() {
	}

	public boolean isOpen() {
		return true;
	}

	public boolean isReadOnly() {
		return true;
	}

	public String getSeparator() {
		return "/";
	}

	public Iterable<Path> getRootDirectories() {
		return List.of(this.root);
	}

	public Iterable<FileStore> getFileStores() {
		return List.of(this.store);
	}

	public Set<String> supportedFileAttributeViews() {
		return SUPPORTED_FILE_ATTRIBUTE_VIEWS;
	}

	public Path getPath(String first, String... more) {
		Stream<String> stream = Stream.of(first);
		if (more.length > 0) {
			stream = Stream.concat(stream, Stream.of(more));
		}

		String string = (String)stream.collect(Collectors.joining("/"));
		if (string.equals("/")) {
			return this.root;
		} else if (string.startsWith("/")) {
			ResourcePath resourcePath = this.root;

			for (String string2 : SEPARATOR_SPLITTER.split(string.substring(1))) {
				if (string2.isEmpty()) {
					throw new IllegalArgumentException("Empty paths not allowed");
				}

				resourcePath = resourcePath.get(string2);
			}

			return resourcePath;
		} else {
			ResourcePath resourcePath = null;

			for (String string2 : SEPARATOR_SPLITTER.split(string)) {
				if (string2.isEmpty()) {
					throw new IllegalArgumentException("Empty paths not allowed");
				}

				resourcePath = new ResourcePath(this, string2, resourcePath, ResourceFile.RELATIVE);
			}

			if (resourcePath == null) {
				throw new IllegalArgumentException("Empty paths not allowed");
			} else {
				return resourcePath;
			}
		}
	}

	public PathMatcher getPathMatcher(String syntaxAndPattern) {
		throw new UnsupportedOperationException();
	}

	public UserPrincipalLookupService getUserPrincipalLookupService() {
		throw new UnsupportedOperationException();
	}

	public WatchService newWatchService() {
		throw new UnsupportedOperationException();
	}

	public FileStore getStore() {
		return this.store;
	}

	public ResourcePath getRoot() {
		return this.root;
	}

	public static ResourceFileSystem.Builder builder() {
		return new ResourceFileSystem.Builder();
	}

	public static class Builder {
		private final ResourceFileSystem.Directory root = new ResourceFileSystem.Directory();

		public ResourceFileSystem.Builder withFile(List<String> directories, String name, Path path) {
			ResourceFileSystem.Directory directory = this.root;

			for (String string : directories) {
				directory = (ResourceFileSystem.Directory)directory.children.computeIfAbsent(string, directoryx -> new ResourceFileSystem.Directory());
			}

			directory.files.put(name, path);
			return this;
		}

		public ResourceFileSystem.Builder withFile(List<String> directories, Path path) {
			if (directories.isEmpty()) {
				throw new IllegalArgumentException("Path can't be empty");
			} else {
				int i = directories.size() - 1;
				return this.withFile(directories.subList(0, i), (String)directories.get(i), path);
			}
		}

		public FileSystem build(String name) {
			return new ResourceFileSystem(name, this.root);
		}
	}

	static record Directory(Map<String, ResourceFileSystem.Directory> children, Map<String, Path> files) {

		public Directory() {
			this(new HashMap(), new HashMap());
		}
	}
}
