package net.minecraft.server.dedicated;

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

public abstract class AbstractPropertiesHandler<T extends AbstractPropertiesHandler<T>> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Properties properties;

	public AbstractPropertiesHandler(Properties properties) {
		this.properties = properties;
	}

	public static Properties load(Path path) {
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
			LOGGER.error("Failed to load properties from file: " + path);
		}

		return properties;
	}

	public void store(Path path) {
		try {
			OutputStream outputStream = Files.newOutputStream(path);
			Throwable var3 = null;

			try {
				this.properties.store(outputStream, "Minecraft server properties");
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
			LOGGER.error("Failed to store properties to file: " + path);
		}
	}

	private static <V extends Number> Function<String, V> wrapNumberParsingFunction(Function<String, V> function) {
		return string -> {
			try {
				return (Number)function.apply(string);
			} catch (NumberFormatException var3) {
				return null;
			}
		};
	}

	protected static <V> Function<String, V> wrapIntParsingFunction(IntFunction<V> intFunction, Function<String, V> function) {
		return string -> {
			try {
				return intFunction.apply(Integer.parseInt(string));
			} catch (NumberFormatException var4) {
				return function.apply(string);
			}
		};
	}

	@Nullable
	private String getStringValue(String string) {
		return (String)this.properties.get(string);
	}

	@Nullable
	protected <V> V getDeprecated(String string, Function<String, V> function) {
		String string2 = this.getStringValue(string);
		if (string2 == null) {
			return null;
		} else {
			this.properties.remove(string);
			return (V)function.apply(string2);
		}
	}

	protected <V> V get(String string, Function<String, V> function, Function<V, String> function2, V object) {
		String string2 = this.getStringValue(string);
		V object2 = MoreObjects.firstNonNull((V)(string2 != null ? function.apply(string2) : null), object);
		this.properties.put(string, function2.apply(object2));
		return object2;
	}

	protected <V> AbstractPropertiesHandler<T>.PropertyAccessor<V> accessor(String string, Function<String, V> function, Function<V, String> function2, V object) {
		String string2 = this.getStringValue(string);
		V object2 = MoreObjects.firstNonNull((V)(string2 != null ? function.apply(string2) : null), object);
		this.properties.put(string, function2.apply(object2));
		return new AbstractPropertiesHandler.PropertyAccessor<>(string, object2, function2);
	}

	protected <V> V getWithOperation(String string, Function<String, V> function, UnaryOperator<V> unaryOperator, Function<V, String> function2, V object) {
		return this.get(string, stringx -> {
			V objectx = (V)function.apply(stringx);
			return objectx != null ? unaryOperator.apply(objectx) : null;
		}, function2, object);
	}

	protected <V> V getString(String string, Function<String, V> function, V object) {
		return this.get(string, function, Objects::toString, object);
	}

	protected <V> AbstractPropertiesHandler<T>.PropertyAccessor<V> accessor(String string, Function<String, V> function, V object) {
		return this.accessor(string, function, Objects::toString, object);
	}

	protected String getString(String string, String string2) {
		return this.get(string, Function.identity(), Function.identity(), string2);
	}

	@Nullable
	protected String getDeprecatedString(String string) {
		return this.getDeprecated(string, Function.identity());
	}

	protected int getInt(String string, int i) {
		return this.getString(string, wrapNumberParsingFunction(Integer::parseInt), i);
	}

	protected AbstractPropertiesHandler<T>.PropertyAccessor<Integer> intAccessor(String string, int i) {
		return this.accessor(string, wrapNumberParsingFunction(Integer::parseInt), i);
	}

	protected int parseIntWithOperation(String string, UnaryOperator<Integer> unaryOperator, int i) {
		return this.getWithOperation(string, wrapNumberParsingFunction(Integer::parseInt), unaryOperator, Objects::toString, i);
	}

	protected long parseLong(String string, long l) {
		return this.getString(string, wrapNumberParsingFunction(Long::parseLong), l);
	}

	protected boolean parseBoolean(String string, boolean bl) {
		return this.getString(string, Boolean::valueOf, bl);
	}

	protected AbstractPropertiesHandler<T>.PropertyAccessor<Boolean> booleanAccessor(String string, boolean bl) {
		return this.accessor(string, Boolean::valueOf, bl);
	}

	@Nullable
	protected Boolean getDeprecatedBoolean(String string) {
		return this.getDeprecated(string, Boolean::valueOf);
	}

	protected Properties getProperties() {
		Properties properties = new Properties();
		properties.putAll(this.properties);
		return properties;
	}

	protected abstract T create(Properties properties);

	public class PropertyAccessor<V> implements Supplier<V> {
		private final String key;
		private final V value;
		private final Function<V, String> stringify;

		private PropertyAccessor(String string, V object, Function<V, String> function) {
			this.key = string;
			this.value = object;
			this.stringify = function;
		}

		public V get() {
			return this.value;
		}

		public T set(V object) {
			Properties properties = AbstractPropertiesHandler.this.getProperties();
			properties.put(this.key, this.stringify.apply(object));
			return AbstractPropertiesHandler.this.create(properties);
		}
	}
}
