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
import net.minecraft.util.SystemUtil;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirectoryResourcePack extends AbstractFileResourcePack {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final boolean IS_WINDOWS = SystemUtil.getOperatingSystem() == SystemUtil.OperatingSystem.field_1133;
	private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is('\\');

	public DirectoryResourcePack(File file) {
		super(file);
	}

	public static boolean isValidPath(File file, String string) throws IOException {
		String string2 = file.getCanonicalPath();
		if (IS_WINDOWS) {
			string2 = BACKSLASH_MATCHER.replaceFrom(string2, '/');
		}

		return string2.endsWith(string);
	}

	@Override
	protected InputStream openFile(String string) throws IOException {
		File file = this.getFile(string);
		if (file == null) {
			throw new ResourceNotFoundException(this.base, string);
		} else {
			return new FileInputStream(file);
		}
	}

	@Override
	protected boolean containsFile(String string) {
		return this.getFile(string) != null;
	}

	@Nullable
	private File getFile(String string) {
		try {
			File file = new File(this.base, string);
			if (file.isFile() && isValidPath(file, string)) {
				return file;
			}
		} catch (IOException var3) {
		}

		return null;
	}

	@Override
	public Set<String> getNamespaces(ResourceType resourceType) {
		Set<String> set = Sets.<String>newHashSet();
		File file = new File(this.base, resourceType.getName());
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
	public Collection<Identifier> findResources(ResourceType resourceType, String string, int i, Predicate<String> predicate) {
		File file = new File(this.base, resourceType.getName());
		List<Identifier> list = Lists.<Identifier>newArrayList();

		for (String string2 : this.getNamespaces(resourceType)) {
			this.findFiles(new File(new File(file, string2), string), i, string2, list, string + "/", predicate);
		}

		return list;
	}

	private void findFiles(File file, int i, String string, List<Identifier> list, String string2, Predicate<String> predicate) {
		File[] files = file.listFiles();
		if (files != null) {
			for (File file2 : files) {
				if (file2.isDirectory()) {
					if (i > 0) {
						this.findFiles(file2, i - 1, string, list, string2 + file2.getName() + "/", predicate);
					}
				} else if (!file2.getName().endsWith(".mcmeta") && predicate.test(file2.getName())) {
					try {
						list.add(new Identifier(string, string2 + file2.getName()));
					} catch (InvalidIdentifierException var13) {
						LOGGER.error(var13.getMessage());
					}
				}
			}
		}
	}
}
