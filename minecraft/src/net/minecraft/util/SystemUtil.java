package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixUtils;
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
import java.util.Optional;
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
import net.minecraft.state.property.Property;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SystemUtil {
	public static LongSupplier nanoTimeSupplier = System::nanoTime;
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern ILLEGAL_FILE_NAME_PATTERN = Pattern.compile(
		".*\\.|(?:CON|PRN|AUX|NUL|COM1|COM2|COM3|COM4|COM5|COM6|COM7|COM8|COM9|LPT1|LPT2|LPT3|LPT4|LPT5|LPT6|LPT7|LPT8|LPT9)(?:\\..*)?", 2
	);

	public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
		return Collectors.toMap(Entry::getKey, Entry::getValue);
	}

	public static <T extends Comparable<T>> String getValueAsString(Property<T> property, Object object) {
		return property.getValueAsString((T)object);
	}

	public static String createTranslationKey(String string, @Nullable Identifier identifier) {
		return identifier == null ? string + ".unregistered_sadface" : string + '.' + identifier.getNamespace() + '.' + identifier.getPath().replace('/', '.');
	}

	public static long getMeasuringTimeMs() {
		return getMeasuringTimeNano() / 1000000L;
	}

	public static long getMeasuringTimeNano() {
		return nanoTimeSupplier.getAsLong();
	}

	public static long getEpochTimeMs() {
		return Instant.now().toEpochMilli();
	}

	public static SystemUtil.OperatingSystem getOperatingSystem() {
		String string = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (string.contains("win")) {
			return SystemUtil.OperatingSystem.WINDOWS;
		} else if (string.contains("mac")) {
			return SystemUtil.OperatingSystem.MAC;
		} else if (string.contains("solaris")) {
			return SystemUtil.OperatingSystem.SOLARIS;
		} else if (string.contains("sunos")) {
			return SystemUtil.OperatingSystem.SOLARIS;
		} else if (string.contains("linux")) {
			return SystemUtil.OperatingSystem.UNIX;
		} else {
			return string.contains("unix") ? SystemUtil.OperatingSystem.UNIX : SystemUtil.OperatingSystem.UNKNOWN;
		}
	}

	public static Stream<String> getJVMFlags() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		return runtimeMXBean.getInputArguments().stream().filter(string -> string.startsWith("-X"));
	}

	public static boolean method_653(Path path) {
		Path path2 = path.normalize();
		return path2.equals(path);
	}

	public static boolean isPathIllegal(Path path) {
		for (Path path2 : path) {
			if (ILLEGAL_FILE_NAME_PATTERN.matcher(path2.toString()).matches()) {
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

	public static <T> T get(Supplier<T> supplier) {
		return (T)supplier.get();
	}

	public static <T> T consume(T object, Consumer<T> consumer) {
		consumer.accept(object);
		return object;
	}

	public static <K> Strategy<K> identityHashStrategy() {
		return SystemUtil.IdentityHashStrategy.INSTANCE;
	}

	public static <V> CompletableFuture<List<V>> thenCombine(List<? extends CompletableFuture<? extends V>> list) {
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

	public static <T> Stream<T> method_17815(Optional<? extends T> optional) {
		return DataFixUtils.orElseGet(optional.map(Stream::of), Stream::empty);
	}

	static enum IdentityHashStrategy implements Strategy<Object> {
		INSTANCE;

		@Override
		public int hashCode(Object object) {
			return System.identityHashCode(object);
		}

		@Override
		public boolean equals(Object object, Object object2) {
			return object == object2;
		}
	}

	public static enum OperatingSystem {
		UNIX,
		SOLARIS,
		WINDOWS {
			@Environment(EnvType.CLIENT)
			@Override
			protected String[] getURLOpenCommand(URL uRL) {
				return new String[]{"rundll32", "url.dll,FileProtocolHandler", uRL.toString()};
			}
		},
		MAC {
			@Environment(EnvType.CLIENT)
			@Override
			protected String[] getURLOpenCommand(URL uRL) {
				return new String[]{"open", uRL.toString()};
			}
		},
		UNKNOWN;

		private OperatingSystem() {
		}

		@Environment(EnvType.CLIENT)
		public void open(URL uRL) {
			try {
				Process process = (Process)AccessController.doPrivileged(() -> Runtime.getRuntime().exec(this.getURLOpenCommand(uRL)));

				for (String string : IOUtils.readLines(process.getErrorStream())) {
					SystemUtil.LOGGER.error(string);
				}

				process.getInputStream().close();
				process.getErrorStream().close();
				process.getOutputStream().close();
			} catch (IOException | PrivilegedActionException var5) {
				SystemUtil.LOGGER.error("Couldn't open url '{}'", uRL, var5);
			}
		}

		@Environment(EnvType.CLIENT)
		public void open(URI uRI) {
			try {
				this.open(uRI.toURL());
			} catch (MalformedURLException var3) {
				SystemUtil.LOGGER.error("Couldn't open uri '{}'", uRI, var3);
			}
		}

		@Environment(EnvType.CLIENT)
		public void open(File file) {
			try {
				this.open(file.toURI().toURL());
			} catch (MalformedURLException var3) {
				SystemUtil.LOGGER.error("Couldn't open file '{}'", file, var3);
			}
		}

		@Environment(EnvType.CLIENT)
		protected String[] getURLOpenCommand(URL uRL) {
			String string = uRL.toString();
			if ("file".equals(uRL.getProtocol())) {
				string = string.replace("file:", "file://");
			}

			return new String[]{"xdg-open", string};
		}

		@Environment(EnvType.CLIENT)
		public void open(String string) {
			try {
				this.open(new URI(string).toURL());
			} catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
				SystemUtil.LOGGER.error("Couldn't open uri '{}'", string, var3);
			}
		}
	}
}
