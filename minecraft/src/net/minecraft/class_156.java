package net.minecraft;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_156 {
	public static LongSupplier field_1128 = System::nanoTime;
	private static final Logger field_1129 = LogManager.getLogger();
	private static final Pattern field_1127 = Pattern.compile(
		".*\\.|(?:CON|PRN|AUX|NUL|COM1|COM2|COM3|COM4|COM5|COM6|COM7|COM8|COM9|LPT1|LPT2|LPT3|LPT4|LPT5|LPT6|LPT7|LPT8|LPT9)(?:\\..*)?", 2
	);

	public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> method_664() {
		return Collectors.toMap(Entry::getKey, Entry::getValue);
	}

	public static <T extends Comparable<T>> String method_650(class_2769<T> arg, Object object) {
		return arg.method_11901((T)object);
	}

	public static String method_646(String string, @Nullable class_2960 arg) {
		return arg == null ? string + ".unregistered_sadface" : string + '.' + arg.method_12836() + '.' + arg.method_12832().replace('/', '.');
	}

	public static long method_658() {
		return method_648() / 1000000L;
	}

	public static long method_648() {
		return field_1128.getAsLong();
	}

	public static long method_659() {
		return Instant.now().toEpochMilli();
	}

	public static class_156.class_158 method_668() {
		String string = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (string.contains("win")) {
			return class_156.class_158.field_1133;
		} else if (string.contains("mac")) {
			return class_156.class_158.field_1137;
		} else if (string.contains("solaris")) {
			return class_156.class_158.field_1134;
		} else if (string.contains("sunos")) {
			return class_156.class_158.field_1134;
		} else if (string.contains("linux")) {
			return class_156.class_158.field_1135;
		} else {
			return string.contains("unix") ? class_156.class_158.field_1135 : class_156.class_158.field_1132;
		}
	}

	public static Stream<String> method_651() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		return runtimeMXBean.getInputArguments().stream().filter(string -> string.startsWith("-X"));
	}

	public static boolean method_653(Path path) {
		Path path2 = path.normalize();
		return path2.equals(path);
	}

	public static boolean method_665(Path path) {
		for (Path path2 : path) {
			if (field_1127.matcher(path2.toString()).matches()) {
				return false;
			}
		}

		return true;
	}

	public static Path method_662(Path path, String string, String string2) {
		String string3 = string + string2;
		Path path2 = Paths.get(string3);
		if (path2.endsWith(string2)) {
			throw new InvalidPathException(string3, "empty resource name");
		} else {
			return path.resolve(path2);
		}
	}

	public static <T> T method_660(Iterable<T> iterable, @Nullable T object) {
		Iterator<T> iterator = iterable.iterator();
		T object2 = (T)iterator.next();
		if (object != null) {
			T object3 = object2;

			while (object3 != object) {
				if (iterator.hasNext()) {
					object3 = (T)iterator.next();
				}
			}

			if (iterator.hasNext()) {
				return (T)iterator.next();
			}
		}

		return object2;
	}

	public static <T> T method_645(Iterable<T> iterable, @Nullable T object) {
		Iterator<T> iterator = iterable.iterator();
		T object2 = null;

		while (iterator.hasNext()) {
			T object3 = (T)iterator.next();
			if (object3 == object) {
				if (object2 == null) {
					object2 = iterator.hasNext() ? Iterators.getLast(iterator) : object;
				}
				break;
			}

			object2 = object3;
		}

		return object2;
	}

	public static <T> T method_656(Supplier<T> supplier) {
		return (T)supplier.get();
	}

	public static <T> T method_654(T object, Consumer<T> consumer) {
		consumer.accept(object);
		return object;
	}

	public static <K> Strategy<K> method_655() {
		return class_156.class_157.field_1130;
	}

	public static <V> CompletableFuture<List<V>> method_652(List<? extends CompletableFuture<? extends V>> list) {
		return (CompletableFuture<List<V>>)list.stream()
			.reduce(
				CompletableFuture.completedFuture(Lists.newArrayList()),
				(completableFuture, completableFuture2) -> completableFuture2.thenCombine(completableFuture, (object, listx) -> {
						List<V> list2 = Lists.<V>newArrayListWithCapacity(listx.size() + 1);
						list2.addAll(listx);
						list2.add(object);
						return list2;
					}),
				(completableFuture, completableFuture2) -> completableFuture.thenCombine(completableFuture2, (listx, list2) -> {
						List<V> list3 = Lists.<V>newArrayListWithCapacity(listx.size() + list2.size());
						list3.addAll(listx);
						list3.addAll(list2);
						return list3;
					})
			);
	}

	static enum class_157 implements Strategy<Object> {
		field_1130;

		@Override
		public int hashCode(Object object) {
			return System.identityHashCode(object);
		}

		@Override
		public boolean equals(Object object, Object object2) {
			return object == object2;
		}
	}

	public static enum class_158 {
		field_1135,
		field_1134,
		field_1133 {
			@Environment(EnvType.CLIENT)
			@Override
			protected String[] method_674(URL uRL) {
				return new String[]{"rundll32", "url.dll,FileProtocolHandler", uRL.toString()};
			}
		},
		field_1137 {
			@Environment(EnvType.CLIENT)
			@Override
			protected String[] method_674(URL uRL) {
				return new String[]{"open", uRL.toString()};
			}
		},
		field_1132;

		private class_158() {
		}

		@Environment(EnvType.CLIENT)
		public void method_669(URL uRL) {
			try {
				Process process = (Process)AccessController.doPrivileged(() -> Runtime.getRuntime().exec(this.method_674(uRL)));

				for (String string : IOUtils.readLines(process.getErrorStream())) {
					class_156.field_1129.error(string);
				}

				process.getInputStream().close();
				process.getErrorStream().close();
				process.getOutputStream().close();
			} catch (IOException | PrivilegedActionException var5) {
				class_156.field_1129.error("Couldn't open url '{}'", uRL, var5);
			}
		}

		@Environment(EnvType.CLIENT)
		public void method_673(URI uRI) {
			try {
				this.method_669(uRI.toURL());
			} catch (MalformedURLException var3) {
				class_156.field_1129.error("Couldn't open uri '{}'", uRI, var3);
			}
		}

		@Environment(EnvType.CLIENT)
		public void method_672(File file) {
			try {
				this.method_669(file.toURI().toURL());
			} catch (MalformedURLException var3) {
				class_156.field_1129.error("Couldn't open file '{}'", file, var3);
			}
		}

		@Environment(EnvType.CLIENT)
		protected String[] method_674(URL uRL) {
			String string = uRL.toString();
			if ("file".equals(uRL.getProtocol())) {
				string = string.replace("file:", "file://");
			}

			return new String[]{"xdg-open", string};
		}

		@Environment(EnvType.CLIENT)
		public void method_670(String string) {
			try {
				this.method_669(new URI(string).toURL());
			} catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
				class_156.field_1129.error("Couldn't open uri '{}'", string, var3);
			}
		}
	}
}
