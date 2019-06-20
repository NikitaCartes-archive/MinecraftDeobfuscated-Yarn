package net.minecraft;

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
import org.apache.commons.io.IOUtils;

public class class_3258 extends class_3255 {
	public static final Splitter field_14183 = Splitter.on('/').omitEmptyStrings().limit(3);
	private ZipFile field_14184;

	public class_3258(File file) {
		super(file);
	}

	private ZipFile method_14399() throws IOException {
		if (this.field_14184 == null) {
			this.field_14184 = new ZipFile(this.field_14181);
		}

		return this.field_14184;
	}

	@Override
	protected InputStream method_14391(String string) throws IOException {
		ZipFile zipFile = this.method_14399();
		ZipEntry zipEntry = zipFile.getEntry(string);
		if (zipEntry == null) {
			throw new class_3266(this.field_14181, string);
		} else {
			return zipFile.getInputStream(zipEntry);
		}
	}

	@Override
	public boolean method_14393(String string) {
		try {
			return this.method_14399().getEntry(string) != null;
		} catch (IOException var3) {
			return false;
		}
	}

	@Override
	public Set<String> method_14406(class_3264 arg) {
		ZipFile zipFile;
		try {
			zipFile = this.method_14399();
		} catch (IOException var9) {
			return Collections.emptySet();
		}

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		Set<String> set = Sets.<String>newHashSet();

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
			String string = zipEntry.getName();
			if (string.startsWith(arg.method_14413() + "/")) {
				List<String> list = Lists.<String>newArrayList(field_14183.split(string));
				if (list.size() > 1) {
					String string2 = (String)list.get(1);
					if (string2.equals(string2.toLowerCase(Locale.ROOT))) {
						set.add(string2);
					} else {
						this.method_14394(string2);
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
		if (this.field_14184 != null) {
			IOUtils.closeQuietly(this.field_14184);
			this.field_14184 = null;
		}
	}

	@Override
	public Collection<class_2960> method_14408(class_3264 arg, String string, int i, Predicate<String> predicate) {
		ZipFile zipFile;
		try {
			zipFile = this.method_14399();
		} catch (IOException var15) {
			return Collections.emptySet();
		}

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		List<class_2960> list = Lists.<class_2960>newArrayList();
		String string2 = arg.method_14413() + "/";

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
								list.add(new class_2960(string5, string4));
							}
						}
					}
				}
			}
		}

		return list;
	}
}
