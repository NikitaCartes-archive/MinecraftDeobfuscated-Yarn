package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3268 implements class_3262 {
	public static Path field_14196;
	private static final Logger field_14195 = LogManager.getLogger();
	public static Class<?> field_14194;
	private static final Map<class_3264, FileSystem> field_17917 = class_156.method_654(Maps.<class_3264, FileSystem>newHashMap(), hashMap -> {
		synchronized (class_3268.class) {
			for (class_3264 lv : class_3264.values()) {
				URL uRL = class_3268.class.getResource("/" + lv.method_14413() + "/.mcassetsroot");

				try {
					URI uRI = uRL.toURI();
					if ("jar".equals(uRI.getScheme())) {
						FileSystem fileSystem;
						try {
							fileSystem = FileSystems.getFileSystem(uRI);
						} catch (FileSystemNotFoundException var11) {
							fileSystem = FileSystems.newFileSystem(uRI, Collections.emptyMap());
						}

						hashMap.put(lv, fileSystem);
					}
				} catch (IOException | URISyntaxException var12) {
					field_14195.error("Couldn't get a list of all vanilla resources", (Throwable)var12);
				}
			}
		}
	});
	public final Set<String> field_14193;

	public class_3268(String... strings) {
		this.field_14193 = ImmutableSet.copyOf(strings);
	}

	@Override
	public InputStream method_14410(String string) throws IOException {
		if (!string.contains("/") && !string.contains("\\")) {
			if (field_14196 != null) {
				Path path = field_14196.resolve(string);
				if (Files.exists(path, new LinkOption[0])) {
					return Files.newInputStream(path);
				}
			}

			return this.method_14417(string);
		} else {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
	}

	@Override
	public InputStream method_14405(class_3264 arg, class_2960 arg2) throws IOException {
		InputStream inputStream = this.method_14416(arg, arg2);
		if (inputStream != null) {
			return inputStream;
		} else {
			throw new FileNotFoundException(arg2.method_12832());
		}
	}

	@Override
	public Collection<class_2960> method_14408(class_3264 arg, String string, int i, Predicate<String> predicate) {
		Set<class_2960> set = Sets.<class_2960>newHashSet();
		if (field_14196 != null) {
			try {
				set.addAll(this.method_14418(i, "minecraft", field_14196.resolve(arg.method_14413()).resolve("minecraft"), string, predicate));
			} catch (IOException var14) {
			}

			if (arg == class_3264.field_14188) {
				Enumeration<URL> enumeration = null;

				try {
					enumeration = field_14194.getClassLoader().getResources(arg.method_14413() + "/minecraft");
				} catch (IOException var13) {
				}

				while (enumeration != null && enumeration.hasMoreElements()) {
					try {
						URI uRI = ((URL)enumeration.nextElement()).toURI();
						if ("file".equals(uRI.getScheme())) {
							set.addAll(this.method_14418(i, "minecraft", Paths.get(uRI), string, predicate));
						}
					} catch (IOException | URISyntaxException var12) {
					}
				}
			}
		}

		try {
			URL uRL = class_3268.class.getResource("/" + arg.method_14413() + "/.mcassetsroot");
			if (uRL == null) {
				field_14195.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
				return set;
			}

			URI uRI = uRL.toURI();
			if ("file".equals(uRI.getScheme())) {
				URL uRL2 = new URL(uRL.toString().substring(0, uRL.toString().length() - ".mcassetsroot".length()) + "minecraft");
				if (uRL2 == null) {
					return set;
				}

				Path path = Paths.get(uRL2.toURI());
				set.addAll(this.method_14418(i, "minecraft", path, string, predicate));
			} else if ("jar".equals(uRI.getScheme())) {
				Path path2 = ((FileSystem)field_17917.get(arg)).getPath("/" + arg.method_14413() + "/minecraft");
				set.addAll(this.method_14418(i, "minecraft", path2, string, predicate));
			} else {
				field_14195.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", uRI);
			}
		} catch (NoSuchFileException | FileNotFoundException var10) {
		} catch (IOException | URISyntaxException var11) {
			field_14195.error("Couldn't get a list of all vanilla resources", (Throwable)var11);
		}

		return set;
	}

	private Collection<class_2960> method_14418(int i, String string, Path path, String string2, Predicate<String> predicate) throws IOException {
		List<class_2960> list = Lists.<class_2960>newArrayList();
		Iterator<Path> iterator = Files.walk(path.resolve(string2), i, new FileVisitOption[0]).iterator();

		while (iterator.hasNext()) {
			Path path2 = (Path)iterator.next();
			if (!path2.endsWith(".mcmeta") && Files.isRegularFile(path2, new LinkOption[0]) && predicate.test(path2.getFileName().toString())) {
				list.add(new class_2960(string, path.relativize(path2).toString().replaceAll("\\\\", "/")));
			}
		}

		return list;
	}

	@Nullable
	protected InputStream method_14416(class_3264 arg, class_2960 arg2) {
		String string = "/" + arg.method_14413() + "/" + arg2.method_12836() + "/" + arg2.method_12832();
		if (field_14196 != null) {
			Path path = field_14196.resolve(arg.method_14413() + "/" + arg2.method_12836() + "/" + arg2.method_12832());
			if (Files.exists(path, new LinkOption[0])) {
				try {
					return Files.newInputStream(path);
				} catch (IOException var7) {
				}
			}
		}

		try {
			URL uRL = class_3268.class.getResource(string);
			return uRL != null && class_3259.method_14402(new File(uRL.getFile()), string) ? class_3268.class.getResourceAsStream(string) : null;
		} catch (IOException var6) {
			return class_3268.class.getResourceAsStream(string);
		}
	}

	@Nullable
	protected InputStream method_14417(String string) {
		return class_3268.class.getResourceAsStream("/" + string);
	}

	@Override
	public boolean method_14411(class_3264 arg, class_2960 arg2) {
		InputStream inputStream = this.method_14416(arg, arg2);
		boolean bl = inputStream != null;
		IOUtils.closeQuietly(inputStream);
		return bl;
	}

	@Override
	public Set<String> method_14406(class_3264 arg) {
		return this.field_14193;
	}

	@Nullable
	@Override
	public <T> T method_14407(class_3270<T> arg) throws IOException {
		try {
			InputStream inputStream = this.method_14410("pack.mcmeta");
			Throwable var3 = null;

			Object var4;
			try {
				var4 = class_3255.method_14392(arg, inputStream);
			} catch (Throwable var14) {
				var3 = var14;
				throw var14;
			} finally {
				if (inputStream != null) {
					if (var3 != null) {
						try {
							inputStream.close();
						} catch (Throwable var13) {
							var3.addSuppressed(var13);
						}
					} else {
						inputStream.close();
					}
				}
			}

			return (T)var4;
		} catch (FileNotFoundException | RuntimeException var16) {
			return null;
		}
	}

	@Override
	public String method_14409() {
		return "Default";
	}

	public void close() {
	}
}
