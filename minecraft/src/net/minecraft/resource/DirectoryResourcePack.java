package net.minecraft.resource;

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
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Util;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirectoryResourcePack extends AbstractFileResourcePack {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final boolean IS_WINDOWS = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS;
	private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is('\\');

	public DirectoryResourcePack(File file) {
		super(file);
	}

	public static boolean isValidPath(File file, String filename) throws IOException {
		String string = file.getCanonicalPath();
		if (IS_WINDOWS) {
			string = BACKSLASH_MATCHER.replaceFrom(string, '/');
		}

		return string.endsWith(filename);
	}

	@Override
	protected InputStream openFile(String name) throws IOException {
		File file = this.getFile(name);
		if (file == null) {
			throw new ResourceNotFoundException(this.base, name);
		} else {
			return new FileInputStream(file);
		}
	}

	@Override
	protected boolean containsFile(String name) {
		return this.getFile(name) != null;
	}

	@Nullable
	private File getFile(String name) {
		try {
			File file = new File(this.base, name);
			if (file.isFile() && isValidPath(file, name)) {
				return file;
			}
		} catch (IOException var3) {
		}

		return null;
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		Set<String> set = Sets.<String>newHashSet();
		File file = new File(this.base, type.getDirectory());
		File[] files = file.listFiles(DirectoryFileFilter.DIRECTORY);
		if (files != null) {
			for (File file2 : files) {
				String string = relativize(file, file2);
				if (string.equals(string.toLowerCase(Locale.ROOT))) {
					set.add(string.substring(0, string.length() - 1));
				} else {
					this.warnNonLowercaseNamespace(string);
				}
			}
		}

		return set;
	}

	public void close() throws IOException {
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String string, int i, Predicate<String> predicate) {
		File file = new File(this.base, type.getDirectory());
		List<Identifier> list = Lists.<Identifier>newArrayList();
		this.findFiles(new File(new File(file, namespace), string), i, namespace, list, string + "/", predicate);
		return list;
	}

	private void findFiles(File file, int maxDepth, String namespace, List<Identifier> found, String prefix, Predicate<String> pathFilter) {
		File[] files = file.listFiles();
		if (files != null) {
			for (File file2 : files) {
				if (file2.isDirectory()) {
					if (maxDepth > 0) {
						this.findFiles(file2, maxDepth - 1, namespace, found, prefix + file2.getName() + "/", pathFilter);
					}
				} else if (!file2.getName().endsWith(".mcmeta") && pathFilter.test(file2.getName())) {
					try {
						found.add(new Identifier(namespace, prefix + file2.getName()));
					} catch (InvalidIdentifierException var13) {
						LOGGER.error(var13.getMessage());
					}
				}
			}
		}
	}
}
