package net.minecraft;

import com.google.common.base.MoreObjects;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_3808<T extends class_3808<T>> {
	private static final Logger field_16849 = LogManager.getLogger();
	private final Properties field_16848;

	public class_3808(Properties properties) {
		this.field_16848 = properties;
	}

	public static Properties method_16727(Path path) {
		Properties properties = new Properties();

		try {
			InputStream inputStream = Files.newInputStream(path);
			Throwable var3 = null;

			try {
				properties.load(inputStream);
			} catch (Throwable var13) {
				var3 = var13;
				throw var13;
			} finally {
				if (inputStream != null) {
					if (var3 != null) {
						try {
							inputStream.close();
						} catch (Throwable var12) {
							var3.addSuppressed(var12);
						}
					} else {
						inputStream.close();
					}
				}
			}
		} catch (IOException var15) {
			field_16849.error("Failed to load properties from file: " + path);
		}

		return properties;
	}

	public void method_16728(Path path) {
		try {
			OutputStream outputStream = Files.newOutputStream(path);
			Throwable var3 = null;

			try {
				this.field_16848.store(outputStream, "Minecraft server properties");
			} catch (Throwable var13) {
				var3 = var13;
				throw var13;
			} finally {
				if (outputStream != null) {
					if (var3 != null) {
						try {
							outputStream.close();
						} catch (Throwable var12) {
							var3.addSuppressed(var12);
						}
					} else {
						outputStream.close();
					}
				}
			}
		} catch (IOException var15) {
			field_16849.error("Failed to store properties to file: " + path);
		}
	}

	private static <V extends Number> Function<String, V> method_16721(Function<String, V> function) {
		return string -> {
			try {
				return (Number)function.apply(string);
			} catch (NumberFormatException var3) {
				return null;
			}
		};
	}

	protected static <V> Function<String, V> method_16722(IntFunction<V> intFunction, Function<String, V> function) {
		return string -> {
			try {
				return intFunction.apply(Integer.parseInt(string));
			} catch (NumberFormatException var4) {
				return function.apply(string);
			}
		};
	}

	@Nullable
	private String method_16734(String string) {
		return (String)this.field_16848.get(string);
	}

	@Nullable
	protected <V> V method_16742(String string, Function<String, V> function) {
		String string2 = this.method_16734(string);
		if (string2 == null) {
			return null;
		} else {
			this.field_16848.remove(string);
			return (V)function.apply(string2);
		}
	}

	protected <V> V method_16741(String string, Function<String, V> function, Function<V, String> function2, V object) {
		String string2 = this.method_16734(string);
		V object2 = MoreObjects.firstNonNull((V)(string2 != null ? function.apply(string2) : null), object);
		this.field_16848.put(string, function2.apply(object2));
		return object2;
	}

	protected <V> class_3808<T>.class_3809<V> method_16724(String string, Function<String, V> function, Function<V, String> function2, V object) {
		String string2 = this.method_16734(string);
		V object2 = MoreObjects.firstNonNull((V)(string2 != null ? function.apply(string2) : null), object);
		this.field_16848.put(string, function2.apply(object2));
		return new class_3808.class_3809<>(string, object2, function2);
	}

	protected <V> V method_16735(String string, Function<String, V> function, UnaryOperator<V> unaryOperator, Function<V, String> function2, V object) {
		return this.method_16741(string, stringx -> {
			V objectx = (V)function.apply(stringx);
			return objectx != null ? unaryOperator.apply(objectx) : null;
		}, function2, object);
	}

	protected <V> V method_16737(String string, Function<String, V> function, V object) {
		return this.method_16741(string, function, Objects::toString, object);
	}

	protected <V> class_3808<T>.class_3809<V> method_16730(String string, Function<String, V> function, V object) {
		return this.method_16724(string, function, Objects::toString, object);
	}

	protected String method_16732(String string, String string2) {
		return this.method_16741(string, Function.identity(), Function.identity(), string2);
	}

	@Nullable
	protected String method_16738(String string) {
		return this.method_16742(string, Function.identity());
	}

	protected int method_16726(String string, int i) {
		return this.method_16737(string, method_16721(Integer::parseInt), i);
	}

	protected class_3808<T>.class_3809<Integer> method_16743(String string, int i) {
		return this.method_16730(string, method_16721(Integer::parseInt), i);
	}

	protected int method_16720(String string, UnaryOperator<Integer> unaryOperator, int i) {
		return this.method_16735(string, method_16721(Integer::parseInt), unaryOperator, Objects::toString, i);
	}

	protected long method_16725(String string, long l) {
		return this.method_16737(string, method_16721(Long::parseLong), l);
	}

	protected boolean method_16740(String string, boolean bl) {
		return this.method_16737(string, Boolean::valueOf, bl);
	}

	protected class_3808<T>.class_3809<Boolean> method_16744(String string, boolean bl) {
		return this.method_16730(string, Boolean::valueOf, bl);
	}

	@Nullable
	protected Boolean method_16736(String string) {
		return this.method_16742(string, Boolean::valueOf);
	}

	protected Properties method_16723() {
		Properties properties = new Properties();
		properties.putAll(this.field_16848);
		return properties;
	}

	protected abstract T method_16739(Properties properties);

	public class class_3809<V> implements Supplier<V> {
		private final String field_16852;
		private final V field_16850;
		private final Function<V, String> field_16851;

		private class_3809(String string, V object, Function<V, String> function) {
			this.field_16852 = string;
			this.field_16850 = object;
			this.field_16851 = function;
		}

		public V get() {
			return this.field_16850;
		}

		public T method_16745(V object) {
			Properties properties = class_3808.this.method_16723();
			properties.put(this.field_16852, this.field_16851.apply(object));
			return class_3808.this.method_16739(properties);
		}
	}
}
