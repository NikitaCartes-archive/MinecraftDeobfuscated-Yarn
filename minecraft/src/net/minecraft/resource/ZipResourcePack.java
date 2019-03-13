package net.minecraft.resource;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

public class ZipResourcePack extends AbstractFilenameResourcePack {
	public static final Splitter TYPE_NAMESPACE_SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
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
	protected InputStream openFilename(String string) throws IOException {
		ZipFile zipFile = this.getZipFile();
		ZipEntry zipEntry = zipFile.getEntry(string);
		if (zipEntry == null) {
			throw new ResourceNotFoundException(this.base, string);
		} else {
			return zipFile.getInputStream(zipEntry);
		}
	}

	@Override
	public boolean containsFilename(String string) {
		try {
			return this.getZipFile().getEntry(string) != null;
		} catch (IOException var3) {
			return false;
		}
	}

	@Override
	public Set<String> method_14406(ResourceType resourceType) {
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
			if (string.startsWith(resourceType.getName() + "/")) {
				List<String> list = Lists.<String>newArrayList(TYPE_NAMESPACE_SPLITTER.split(string));
				if (list.size() > 1) {
					String string2 = (String)list.get(1);
					if (string2.equals(string2.toLowerCase(Locale.ROOT))) {
						set.add(string2);
					} else {
						this.warnNonLowercaseNamespace(string2);
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

	public void close() {
		if (this.file != null) {
			IOUtils.closeQuietly(this.file);
			this.file = null;
		}
	}

	@Override
	public Collection<Identifier> method_14408(ResourceType resourceType, String string, int i, Predicate<String> predicate) {
		ZipFile zipFile;
		try {
			zipFile = this.getZipFile();
		} catch (IOException var15) {
			return Collections.emptySet();
		}

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		List<Identifier> list = Lists.<Identifier>newArrayList();
		String string2 = resourceType.getName() + "/";

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
			if (!zipEntry.isDirectory() && zipEntry.getName().startsWith(string2)) {
				String string3 = zipEntry.getName().substring(string2.length());
				if (!string3.endsWith(".mcmeta")) {
					int j = string3.indexOf(47);
					if (j >= 0) {
						String string4 = string3.substring(j + 1);
						if (string4.startsWith(string + "/")) {
							String[] strings = string4.substring(string.length() + 2).split("/");
							if (strings.length >= i + 1 && predicate.test(string4)) {
								String string5 = string3.substring(0, j);
								list.add(new Identifier(string5, string4));
							}
						}
					}
				}
			}
		}

		return list;
	}
}
