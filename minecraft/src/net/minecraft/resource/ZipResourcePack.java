package net.minecraft.resource;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class ZipResourcePack extends AbstractFileResourcePack {
	static final Logger LOGGER = LogUtils.getLogger();
	private final ZipResourcePack.ZipFileWrapper zipFile;
	private final String overlay;

	ZipResourcePack(ResourcePackInfo info, ZipResourcePack.ZipFileWrapper zipFile, String overlay) {
		super(info);
		this.zipFile = zipFile;
		this.overlay = overlay;
	}

	private static String toPath(ResourceType type, Identifier id) {
		return String.format(Locale.ROOT, "%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath());
	}

	@Nullable
	@Override
	public InputSupplier<InputStream> openRoot(String... segments) {
		return this.openFile(String.join("/", segments));
	}

	@Override
	public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
		return this.openFile(toPath(type, id));
	}

	private String appendOverlayPrefix(String path) {
		return this.overlay.isEmpty() ? path : this.overlay + "/" + path;
	}

	@Nullable
	private InputSupplier<InputStream> openFile(String path) {
		ZipFile zipFile = this.zipFile.open();
		if (zipFile == null) {
			return null;
		} else {
			ZipEntry zipEntry = zipFile.getEntry(this.appendOverlayPrefix(path));
			return zipEntry == null ? null : InputSupplier.create(zipFile, zipEntry);
		}
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		ZipFile zipFile = this.zipFile.open();
		if (zipFile == null) {
			return Set.of();
		} else {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			Set<String> set = Sets.<String>newHashSet();
			String string = this.appendOverlayPrefix(type.getDirectory() + "/");

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
				String string2 = zipEntry.getName();
				String string3 = getNamespace(string, string2);
				if (!string3.isEmpty()) {
					if (Identifier.isNamespaceValid(string3)) {
						set.add(string3);
					} else {
						LOGGER.warn("Non [a-z0-9_.-] character in namespace {} in pack {}, ignoring", string3, this.zipFile.file);
					}
				}
			}

			return set;
		}
	}

	@VisibleForTesting
	public static String getNamespace(String prefix, String entryName) {
		if (!entryName.startsWith(prefix)) {
			return "";
		} else {
			int i = prefix.length();
			int j = entryName.indexOf(47, i);
			return j == -1 ? entryName.substring(i) : entryName.substring(i, j);
		}
	}

	@Override
	public void close() {
		this.zipFile.close();
	}

	@Override
	public void findResources(ResourceType type, String namespace, String prefix, ResourcePack.ResultConsumer consumer) {
		ZipFile zipFile = this.zipFile.open();
		if (zipFile != null) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			String string = this.appendOverlayPrefix(type.getDirectory() + "/" + namespace + "/");
			String string2 = string + prefix + "/";

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
				if (!zipEntry.isDirectory()) {
					String string3 = zipEntry.getName();
					if (string3.startsWith(string2)) {
						String string4 = string3.substring(string.length());
						Identifier identifier = Identifier.of(namespace, string4);
						if (identifier != null) {
							consumer.accept(identifier, InputSupplier.create(zipFile, zipEntry));
						} else {
							LOGGER.warn("Invalid path in datapack: {}:{}, ignoring", namespace, string4);
						}
					}
				}
			}
		}
	}

	public static class ZipBackedFactory implements ResourcePackProfile.PackFactory {
		private final File file;

		public ZipBackedFactory(Path path) {
			this(path.toFile());
		}

		public ZipBackedFactory(File file) {
			this.file = file;
		}

		@Override
		public ResourcePack open(ResourcePackInfo info) {
			ZipResourcePack.ZipFileWrapper zipFileWrapper = new ZipResourcePack.ZipFileWrapper(this.file);
			return new ZipResourcePack(info, zipFileWrapper, "");
		}

		@Override
		public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
			ZipResourcePack.ZipFileWrapper zipFileWrapper = new ZipResourcePack.ZipFileWrapper(this.file);
			ResourcePack resourcePack = new ZipResourcePack(info, zipFileWrapper, "");
			List<String> list = metadata.overlays();
			if (list.isEmpty()) {
				return resourcePack;
			} else {
				List<ResourcePack> list2 = new ArrayList(list.size());

				for (String string : list) {
					list2.add(new ZipResourcePack(info, zipFileWrapper, string));
				}

				return new OverlayResourcePack(resourcePack, list2);
			}
		}
	}

	static class ZipFileWrapper implements AutoCloseable {
		final File file;
		@Nullable
		private ZipFile zip;
		private boolean closed;

		ZipFileWrapper(File file) {
			this.file = file;
		}

		@Nullable
		ZipFile open() {
			if (this.closed) {
				return null;
			} else {
				if (this.zip == null) {
					try {
						this.zip = new ZipFile(this.file);
					} catch (IOException var2) {
						ZipResourcePack.LOGGER.error("Failed to open pack {}", this.file, var2);
						this.closed = true;
						return null;
					}
				}

				return this.zip;
			}
		}

		public void close() {
			if (this.zip != null) {
				IOUtils.closeQuietly(this.zip);
				this.zip = null;
			}
		}

		protected void finalize() throws Throwable {
			this.close();
			super.finalize();
		}
	}
}
