package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.Identifier;

/**
 * Represents a key for a value in a registry in a context where a
 * root registry is available.
 * 
 * @param <T> the type of the value
 * @see Registry#ROOT
 */
public class RegistryKey<T> {
	/**
	 * A cache of all registry keys ever created.
	 */
	private static final Map<String, RegistryKey<?>> INSTANCES = Collections.synchronizedMap(Maps.newIdentityHashMap());
	/**
	 * The identifier of the registry in the root registry.
	 */
	private final Identifier registry;
	/**
	 * The identifier of the value in the registry specified by {@link #registry}.
	 */
	private final Identifier value;

	/**
	 * Creates a registry key for a value in a registry with a registry key for
	 * the value-holding registry in the root registry and an identifier of the
	 * value.
	 * 
	 * <p>You can call it like {@code RegistryKey.of(Registry.ITEM_KEY, new Identifier("iron_ingot"))}
	 * to create a registry key for iron ingot.</p>
	 * 
	 * @param <T> the type of the value
	 * 
	 * @param registry the registry key of the registry in the root registry
	 * @param value the identifier of the value
	 */
	public static <T> RegistryKey<T> of(RegistryKey<? extends Registry<T>> registry, Identifier value) {
		return of(registry.value, value);
	}

	/**
	 * Creates a registry key for a registry in the root registry (registry of
	 * all registries) with an identifier for the registry.
	 * 
	 * <p>You can call it like {@code RegistryKey.of(new Identifier("block"))}
	 * to create a registry key for the block registry.</p>
	 * 
	 * @param <T> the element type of the registry
	 * 
	 * @param registry the identifier of the registry
	 */
	public static <T> RegistryKey<Registry<T>> ofRegistry(Identifier registry) {
		return of(Registry.ROOT_KEY, registry);
	}

	private static <T> RegistryKey<T> of(Identifier registry, Identifier value) {
		String string = (registry + ":" + value).intern();
		return (RegistryKey<T>)INSTANCES.computeIfAbsent(string, stringx -> new RegistryKey(registry, value));
	}

	private RegistryKey(Identifier registry, Identifier value) {
		this.registry = registry;
		this.value = value;
	}

	public String toString() {
		return "ResourceKey[" + this.registry + " / " + this.value + ']';
	}

	public boolean method_31163(RegistryKey<? extends Registry<?>> registryKey) {
		return this.registry.equals(registryKey.getValue());
	}

	public Identifier getValue() {
		return this.value;
	}

	/**
	 * Creates a function that converts an identifier to a registry key for the
	 * registry that {@code registry} refers to in the root registry.
	 * 
	 * @param registry the reference to the value-holding registry in the root registry
	 */
	public static <T> Function<Identifier, RegistryKey<T>> createKeyFactory(RegistryKey<? extends Registry<T>> registry) {
		return identifier -> of(registry, identifier);
	}
}
