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
import net.minecraft.util.registry.DynamicRegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractPropertiesHandler<T extends AbstractPropertiesHandler<T>> {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final Properties properties;

	public AbstractPropertiesHandler(Properties properties) {
		this.properties = properties;
	}

	/**
	 * Loads a map of properties from the {@code path}.
	 */
	public static Properties loadProperties(Path path) {
		Properties properties = new Properties();

		try {
			InputStream inputStream = Files.newInputStream(path);

			try {
				properties.load(inputStream);
			} catch (Throwable var6) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}
				}

				throw var6;
			}

			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException var7) {
			LOGGER.error("Failed to load properties from file: {}", path);
		}

		return properties;
	}

	/**
	 * Saves the properties of this handler to the {@code path}.
	 */
	public void saveProperties(Path path) {
		try {
			OutputStream outputStream = Files.newOutputStream(path);

			try {
				this.properties.store(outputStream, "Minecraft server properties");
			} catch (Throwable var6) {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}
				}

				throw var6;
			}

			if (outputStream != null) {
				outputStream.close();
			}
		} catch (IOException var7) {
			LOGGER.error("Failed to store properties to file: {}", path);
		}
	}

	private static <V extends Number> Function<String, V> wrapNumberParser(Function<String, V> parser) {
		return string -> {
			try {
				return (Number)parser.apply(string);
			} catch (NumberFormatException var3) {
				return null;
			}
		};
	}

	protected static <V> Function<String, V> combineParser(IntFunction<V> intParser, Function<String, V> fallbackParser) {
		return string -> {
			try {
				return intParser.apply(Integer.parseInt(string));
			} catch (NumberFormatException var4) {
				return fallbackParser.apply(string);
			}
		};
	}

	@Nullable
	private String getStringValue(String key) {
		return (String)this.properties.get(key);
	}

	@Nullable
	protected <V> V getDeprecated(String key, Function<String, V> stringifier) {
		String string = this.getStringValue(key);
		if (string == null) {
			return null;
		} else {
			this.properties.remove(key);
			return (V)stringifier.apply(string);
		}
	}

	protected <V> V get(String key, Function<String, V> parser, Function<V, String> stringifier, V fallback) {
		String string = this.getStringValue(key);
		V object = MoreObjects.firstNonNull((V)(string != null ? parser.apply(string) : null), fallback);
		this.properties.put(key, stringifier.apply(object));
		return object;
	}

	protected <V> AbstractPropertiesHandler<T>.PropertyAccessor<V> accessor(String key, Function<String, V> parser, Function<V, String> stringifier, V fallback) {
		String string = this.getStringValue(key);
		V object = MoreObjects.firstNonNull((V)(string != null ? parser.apply(string) : null), fallback);
		this.properties.put(key, stringifier.apply(object));
		return new AbstractPropertiesHandler.PropertyAccessor<>(key, object, stringifier);
	}

	protected <V> V get(String key, Function<String, V> parser, UnaryOperator<V> parsedTransformer, Function<V, String> stringifier, V fallback) {
		return this.get(key, value -> {
			V object = (V)parser.apply(value);
			return object != null ? parsedTransformer.apply(object) : null;
		}, stringifier, fallback);
	}

	protected <V> V get(String key, Function<String, V> parser, V fallback) {
		return this.get(key, parser, Objects::toString, fallback);
	}

	protected <V> AbstractPropertiesHandler<T>.PropertyAccessor<V> accessor(String key, Function<String, V> parser, V fallback) {
		return this.accessor(key, parser, Objects::toString, fallback);
	}

	protected String getString(String key, String fallback) {
		return this.get(key, Function.identity(), Function.identity(), fallback);
	}

	@Nullable
	protected String getDeprecatedString(String key) {
		return this.getDeprecated(key, Function.identity());
	}

	protected int getInt(String key, int fallback) {
		return this.get(key, wrapNumberParser(Integer::parseInt), fallback);
	}

	protected AbstractPropertiesHandler<T>.PropertyAccessor<Integer> intAccessor(String key, int fallback) {
		return this.accessor(key, wrapNumberParser(Integer::parseInt), fallback);
	}

	protected int transformedParseInt(String key, UnaryOperator<Integer> transformer, int fallback) {
		return this.get(key, wrapNumberParser(Integer::parseInt), transformer, Objects::toString, fallback);
	}

	protected long parseLong(String key, long fallback) {
		return this.get(key, wrapNumberParser(Long::parseLong), fallback);
	}

	protected boolean parseBoolean(String key, boolean fallback) {
		return this.get(key, Boolean::valueOf, fallback);
	}

	protected AbstractPropertiesHandler<T>.PropertyAccessor<Boolean> booleanAccessor(String key, boolean fallback) {
		return this.accessor(key, Boolean::valueOf, fallback);
	}

	@Nullable
	protected Boolean getDeprecatedBoolean(String key) {
		return this.getDeprecated(key, Boolean::valueOf);
	}

	protected Properties copyProperties() {
		Properties properties = new Properties();
		properties.putAll(this.properties);
		return properties;
	}

	/**
	 * Creates another property handler with the same type as this one from the
	 * passed new map of properties.
	 */
	protected abstract T create(DynamicRegistryManager registryManager, Properties properties);

	public class PropertyAccessor<V> implements Supplier<V> {
		private final String key;
		private final V value;
		private final Function<V, String> stringifier;

		PropertyAccessor(String key, V value, Function<V, String> stringifier) {
			this.key = key;
			this.value = value;
			this.stringifier = stringifier;
		}

		public V get() {
			return this.value;
		}

		/**
		 * Returns a new property handler with another map of property in which
		 * the property handled by this accessor is updated.
		 * 
		 * <p>This method does not mutate the original property where this accessor
		 * is from.
		 */
		public T set(DynamicRegistryManager registryManager, V value) {
			Properties properties = AbstractPropertiesHandler.this.copyProperties();
			properties.put(this.key, this.stringifier.apply(value));
			return AbstractPropertiesHandler.this.create(registryManager, properties);
		}
	}
}
