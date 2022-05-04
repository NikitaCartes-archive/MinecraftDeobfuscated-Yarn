package net.minecraft.resource;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class ZipResourcePack extends AbstractFileResourcePack {
	private static final Logger field_39096 = LogUtils.getLogger();
	public static final Splitter TYPE_NAMESPACE_SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
	@Nullable
	private ZipFile file;

	public ZipResourcePack(File file) {
		super(file);
	}

	private ZipFile getZipFile() throws IOException {
		if (this.file == null) {
			this.file = new ZipFile(this.base);
		}

		return this.file;
	}

	@Override
	protected InputStream openFile(String name) throws IOException {
		ZipFile zipFile = this.getZipFile();
		ZipEntry zipEntry = zipFile.getEntry(name);
		if (zipEntry == null) {
			throw new ResourceNotFoundException(this.base, name);
		} else {
			return zipFile.getInputStream(zipEntry);
		}
	}

	@Override
	public boolean containsFile(String name) {
		try {
			return this.getZipFile().getEntry(name) != null;
		} catch (IOException var3) {
			return false;
		}
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		ZipFile zipFile;
		try {
			zipFile = this.getZipFile();
		} catch (IOException var9) {
			return Collections.emptySet();
		}

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
						this.warnNonLowerCaseNamespace(string2);
					}
				}
			}
		}

		return set;
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
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, Predicate<Identifier> allowedPathPredicate) {
		ZipFile zipFile;
		try {
			zipFile = this.getZipFile();
		} catch (IOException var14) {
			return Collections.emptySet();
		}

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		List<Identifier> list = Lists.<Identifier>newArrayList();
		String string = type.getDirectory() + "/" + namespace + "/";
		String string2 = string + prefix + "/";

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
			if (!zipEntry.isDirectory()) {
				String string3 = zipEntry.getName();
				if (!string3.endsWith(".mcmeta") && string3.startsWith(string2)) {
					String string4 = string3.substring(string.length());
					Identifier identifier = Identifier.of(namespace, string4);
					if (identifier == null) {
						field_39096.warn("Invalid path in datapack: {}:{}, ignoring", namespace, string4);
					} else if (allowedPathPredicate.test(identifier)) {
						list.add(identifier);
					}
				}
			}
		}

		return list;
	}
}
