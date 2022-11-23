package net.minecraft.resource;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Splitter TYPE_NAMESPACE_SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
	private final File backingZipFile;
	@Nullable
	private ZipFile file;
	private boolean failedToOpen;

	public ZipResourcePack(String name, File backingZipFile, boolean alwaysStable) {
		super(name, alwaysStable);
		this.backingZipFile = backingZipFile;
	}

	@Nullable
	private ZipFile getZipFile() {
		if (this.failedToOpen) {
			return null;
		} else {
			if (this.file == null) {
				try {
					this.file = new ZipFile(this.backingZipFile);
				} catch (IOException var2) {
					LOGGER.error("Failed to open pack {}", this.backingZipFile, var2);
					this.failedToOpen = true;
					return null;
				}
			}

			return this.file;
		}
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

	@Nullable
	private InputSupplier<InputStream> openFile(String path) {
		ZipFile zipFile = this.getZipFile();
		if (zipFile == null) {
			return null;
		} else {
			ZipEntry zipEntry = zipFile.getEntry(path);
			return zipEntry == null ? null : InputSupplier.create(zipFile, zipEntry);
		}
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		ZipFile zipFile = this.getZipFile();
		if (zipFile == null) {
			return Set.of();
		} else {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			Set<String> set = Sets.<String>newHashSet();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
				String string = zipEntry.getName();
				if (string.startsWith(type.getDirectory() + "/")) {
					List<String> list = Lists.<String>newArrayList(TYPE_NAMESPACE_SPLITTER.split(string));
					if (list.size() > 1) {
						String string2 = (String)list.get(1);
						if (string2.equals(string2.toLowerCase(Locale.ROOT))) {
							set.add(string2);
						} else {
							LOGGER.warn("Ignored non-lowercase namespace: {} in {}", string2, this.backingZipFile);
						}
					}
				}
			}

			return set;
		}
	}

	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	@Override
	public void close() {
		if (this.file != null) {
			IOUtils.closeQuietly(this.file);
			this.file = null;
		}
	}

	@Override
	public void findResources(ResourceType type, String namespace, String prefix, ResourcePack.ResultConsumer consumer) {
		ZipFile zipFile = this.getZipFile();
		if (zipFile != null) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			String string = type.getDirectory() + "/" + namespace + "/";
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
}
