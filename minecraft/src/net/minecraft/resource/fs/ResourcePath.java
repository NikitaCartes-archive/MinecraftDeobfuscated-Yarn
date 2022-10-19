package net.minecraft.resource.fs;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

class ResourcePath implements Path {
	private static final BasicFileAttributes DIRECTORY_ATTRIBUTES = new ResourceFileAttributes() {
		public boolean isRegularFile() {
			return false;
		}

		public boolean isDirectory() {
			return true;
		}
	};
	private static final BasicFileAttributes FILE_ATTRIBUTES = new ResourceFileAttributes() {
		public boolean isRegularFile() {
			return true;
		}

		public boolean isDirectory() {
			return false;
		}
	};
	private static final Comparator<ResourcePath> COMPARATOR = Comparator.comparing(ResourcePath::getPathString);
	private final String name;
	private final ResourceFileSystem fileSystem;
	@Nullable
	private final ResourcePath parent;
	@Nullable
	private List<String> names;
	@Nullable
	private String pathString;
	private final ResourceFile file;

	public ResourcePath(ResourceFileSystem fileSystem, String name, @Nullable ResourcePath parent, ResourceFile file) {
		this.fileSystem = fileSystem;
		this.name = name;
		this.parent = parent;
		this.file = file;
	}

	private ResourcePath relativize(@Nullable ResourcePath path, String name) {
		return new ResourcePath(this.fileSystem, name, path, ResourceFile.RELATIVE);
	}

	public ResourceFileSystem getFileSystem() {
		return this.fileSystem;
	}

	public boolean isAbsolute() {
		return this.file != ResourceFile.RELATIVE;
	}

	public File toFile() {
		if (this.file instanceof ResourceFile.File file) {
			return file.contents().toFile();
		} else {
			throw new UnsupportedOperationException("Path " + this.getPathString() + " does not represent file");
		}
	}

	@Nullable
	public ResourcePath getRoot() {
		return this.isAbsolute() ? this.fileSystem.getRoot() : null;
	}

	public ResourcePath getFileName() {
		return this.relativize(null, this.name);
	}

	@Nullable
	public ResourcePath getParent() {
		return this.parent;
	}

	public int getNameCount() {
		return this.getNames().size();
	}

	private List<String> getNames() {
		if (this.name.isEmpty()) {
			return List.of();
		} else {
			if (this.names == null) {
				Builder<String> builder = ImmutableList.builder();
				if (this.parent != null) {
					builder.addAll(this.parent.getNames());
				}

				builder.add(this.name);
				this.names = builder.build();
			}

			return this.names;
		}
	}

	public ResourcePath getName(int i) {
		List<String> list = this.getNames();
		if (i >= 0 && i < list.size()) {
			return this.relativize(null, (String)list.get(i));
		} else {
			throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public ResourcePath subpath(int i, int j) {
		List<String> list = this.getNames();
		if (i >= 0 && j <= list.size() && i < j) {
			ResourcePath resourcePath = null;

			for (int k = i; k < j; k++) {
				resourcePath = this.relativize(resourcePath, (String)list.get(k));
			}

			return resourcePath;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public boolean startsWith(Path other) {
		if (other.isAbsolute() != this.isAbsolute()) {
			return false;
		} else if (other instanceof ResourcePath resourcePath) {
			if (resourcePath.fileSystem != this.fileSystem) {
				return false;
			} else {
				List<String> list = this.getNames();
				List<String> list2 = resourcePath.getNames();
				int i = list2.size();
				if (i > list.size()) {
					return false;
				} else {
					for (int j = 0; j < i; j++) {
						if (!((String)list2.get(j)).equals(list.get(j))) {
							return false;
						}
					}

					return true;
				}
			}
		} else {
			return false;
		}
	}

	public boolean endsWith(Path other) {
		if (other.isAbsolute() && !this.isAbsolute()) {
			return false;
		} else if (other instanceof ResourcePath resourcePath) {
			if (resourcePath.fileSystem != this.fileSystem) {
				return false;
			} else {
				List<String> list = this.getNames();
				List<String> list2 = resourcePath.getNames();
				int i = list2.size();
				int j = list.size() - i;
				if (j < 0) {
					return false;
				} else {
					for (int k = i - 1; k >= 0; k--) {
						if (!((String)list2.get(k)).equals(list.get(j + k))) {
							return false;
						}
					}

					return true;
				}
			}
		} else {
			return false;
		}
	}

	public ResourcePath normalize() {
		return this;
	}

	public ResourcePath resolve(Path path) {
		ResourcePath resourcePath = this.toResourcePath(path);
		return path.isAbsolute() ? resourcePath : this.get(resourcePath.getNames());
	}

	private ResourcePath get(List<String> paths) {
		ResourcePath resourcePath = this;

		for (String string : paths) {
			resourcePath = resourcePath.get(string);
		}

		return resourcePath;
	}

	ResourcePath get(String name) {
		if (isSpecial(this.file)) {
			return new ResourcePath(this.fileSystem, name, this, this.file);
		} else if (this.file instanceof ResourceFile.Directory directory) {
			ResourcePath resourcePath = (ResourcePath)directory.children().get(name);
			return resourcePath != null ? resourcePath : new ResourcePath(this.fileSystem, name, this, ResourceFile.EMPTY);
		} else if (this.file instanceof ResourceFile.File) {
			return new ResourcePath(this.fileSystem, name, this, ResourceFile.EMPTY);
		} else {
			throw new AssertionError("All content types should be already handled");
		}
	}

	private static boolean isSpecial(ResourceFile file) {
		return file == ResourceFile.EMPTY || file == ResourceFile.RELATIVE;
	}

	public ResourcePath relativize(Path path) {
		ResourcePath resourcePath = this.toResourcePath(path);
		if (this.isAbsolute() != resourcePath.isAbsolute()) {
			throw new IllegalArgumentException("absolute mismatch");
		} else {
			List<String> list = this.getNames();
			List<String> list2 = resourcePath.getNames();
			if (list.size() >= list2.size()) {
				throw new IllegalArgumentException();
			} else {
				for (int i = 0; i < list.size(); i++) {
					if (!((String)list.get(i)).equals(list2.get(i))) {
						throw new IllegalArgumentException();
					}
				}

				return resourcePath.subpath(list.size(), list2.size());
			}
		}
	}

	public URI toUri() {
		try {
			return new URI("x-mc-link", this.fileSystem.getStore().name(), this.getPathString(), null);
		} catch (URISyntaxException var2) {
			throw new AssertionError("Failed to create URI", var2);
		}
	}

	public ResourcePath toAbsolutePath() {
		return this.isAbsolute() ? this : this.fileSystem.getRoot().resolve(this);
	}

	public ResourcePath toRealPath(LinkOption... linkOptions) {
		return this.toAbsolutePath();
	}

	public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers) {
		throw new UnsupportedOperationException();
	}

	public int compareTo(Path path) {
		ResourcePath resourcePath = this.toResourcePath(path);
		return COMPARATOR.compare(this, resourcePath);
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof ResourcePath resourcePath) {
			if (this.fileSystem != resourcePath.fileSystem) {
				return false;
			} else {
				boolean bl = this.isNormal();
				if (bl != resourcePath.isNormal()) {
					return false;
				} else {
					return bl ? this.file == resourcePath.file : Objects.equals(this.parent, resourcePath.parent) && Objects.equals(this.name, resourcePath.name);
				}
			}
		} else {
			return false;
		}
	}

	private boolean isNormal() {
		return !isSpecial(this.file);
	}

	public int hashCode() {
		return this.isNormal() ? this.file.hashCode() : this.name.hashCode();
	}

	public String toString() {
		return this.getPathString();
	}

	private String getPathString() {
		if (this.pathString == null) {
			StringBuilder stringBuilder = new StringBuilder();
			if (this.isAbsolute()) {
				stringBuilder.append("/");
			}

			Joiner.on("/").appendTo(stringBuilder, this.getNames());
			this.pathString = stringBuilder.toString();
		}

		return this.pathString;
	}

	private ResourcePath toResourcePath(@Nullable Path path) {
		if (path == null) {
			throw new NullPointerException();
		} else {
			if (path instanceof ResourcePath resourcePath && resourcePath.fileSystem == this.fileSystem) {
				return resourcePath;
			}

			throw new ProviderMismatchException();
		}
	}

	public boolean isReadable() {
		return this.isNormal();
	}

	@Nullable
	public Path toPath() {
		return this.file instanceof ResourceFile.File file ? file.contents() : null;
	}

	@Nullable
	public ResourceFile.Directory toDirectory() {
		return this.file instanceof ResourceFile.Directory directory ? directory : null;
	}

	public BasicFileAttributeView getAttributeView() {
		return new BasicFileAttributeView() {
			public String name() {
				return "basic";
			}

			public BasicFileAttributes readAttributes() throws IOException {
				return ResourcePath.this.getAttributes();
			}

			public void setTimes(FileTime lastModifiedTime, FileTime lastAccessFile, FileTime createTime) {
				throw new ReadOnlyFileSystemException();
			}
		};
	}

	public BasicFileAttributes getAttributes() throws IOException {
		if (this.file instanceof ResourceFile.Directory) {
			return DIRECTORY_ATTRIBUTES;
		} else if (this.file instanceof ResourceFile.File) {
			return FILE_ATTRIBUTES;
		} else {
			throw new NoSuchFileException(this.getPathString());
		}
	}
}
