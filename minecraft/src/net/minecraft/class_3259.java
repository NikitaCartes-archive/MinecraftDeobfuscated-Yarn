package net.minecraft;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3259 extends class_3255 {
	private static final Logger field_14187 = LogManager.getLogger();
	private static final boolean field_14186 = class_156.method_668() == class_156.class_158.field_1133;
	private static final CharMatcher field_14185 = CharMatcher.is('\\');

	public class_3259(File file) {
		super(file);
	}

	public static boolean method_14402(File file, String string) throws IOException {
		String string2 = file.getCanonicalPath();
		if (field_14186) {
			string2 = field_14185.replaceFrom(string2, '/');
		}

		return string2.endsWith(string);
	}

	@Override
	protected InputStream method_14391(String string) throws IOException {
		File file = this.method_14401(string);
		if (file == null) {
			throw new class_3266(this.field_14181, string);
		} else {
			return new FileInputStream(file);
		}
	}

	@Override
	protected boolean method_14393(String string) {
		return this.method_14401(string) != null;
	}

	@Nullable
	private File method_14401(String string) {
		try {
			File file = new File(this.field_14181, string);
			if (file.isFile() && method_14402(file, string)) {
				return file;
			}
		} catch (IOException var3) {
		}

		return null;
	}

	@Override
	public Set<String> method_14406(class_3264 arg) {
		Set<String> set = Sets.<String>newHashSet();
		File file = new File(this.field_14181, arg.method_14413());
		File[] files = file.listFiles(DirectoryFileFilter.DIRECTORY);
		if (files != null) {
			for (File file2 : files) {
				String string = method_14396(file, file2);
				if (string.equals(string.toLowerCase(Locale.ROOT))) {
					set.add(string.substring(0, string.length() - 1));
				} else {
					this.method_14394(string);
				}
			}
		}

		return set;
	}

	public void close() throws IOException {
	}

	@Override
	public Collection<class_2960> method_14408(class_3264 arg, String string, int i, Predicate<String> predicate) {
		File file = new File(this.field_14181, arg.method_14413());
		List<class_2960> list = Lists.<class_2960>newArrayList();

		for (String string2 : this.method_14406(arg)) {
			this.method_14400(new File(new File(file, string2), string), i, string2, list, string + "/", predicate);
		}

		return list;
	}

	private void method_14400(File file, int i, String string, List<class_2960> list, String string2, Predicate<String> predicate) {
		File[] files = file.listFiles();
		if (files != null) {
			for (File file2 : files) {
				if (file2.isDirectory()) {
					if (i > 0) {
						this.method_14400(file2, i - 1, string, list, string2 + file2.getName() + "/", predicate);
					}
				} else if (!file2.getName().endsWith(".mcmeta") && predicate.test(file2.getName())) {
					try {
						list.add(new class_2960(string, string2 + file2.getName()));
					} catch (class_151 var13) {
						field_14187.error(var13.getMessage());
					}
				}
			}
		}
	}
}
