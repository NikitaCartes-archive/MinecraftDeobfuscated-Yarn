package net.minecraft.registry;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.util.Identifier;

/**
 * Represents a key for a value in a registry in a context where a
 * root registry is available.
 * 
 * @param <T> the type of the value
 * @see Registries#ROOT
 */
public class RegistryKey<T> {
	/**
	 * A cache of all registry keys ever created.
	 */
	private static final ConcurrentMap<RegistryKey.RegistryIdPair, RegistryKey<?>> INSTANCES = new MapMaker().weakValues().makeMap();
	/**
	 * The identifier of the registry in the root registry.
	 */
	private final Identifier registry;
	/**
	 * The identifier of the value in the registry specified by {@link #registry}.
	 */
	private final Identifier value;

	public static <T> Codec<RegistryKey<T>> createCodec(RegistryKey<? extends Registry<T>> registry) {
		return Identifier.CODEC.xmap(id -> of(registry, id), RegistryKey::getValue);
	}

	/**
	 * Creates a registry key for a value in a registry with a registry key for
	 * the value-holding registry in the root registry and an identifier of the
	 * value.
	 * 
	 * <p>You can call it like {@code RegistryKey.of(Registry.ITEM_KEY, new Identifier("iron_ingot"))}
	 * to create a registry key for iron ingot.
	 * 
	 * @param <T> the type of the value
	 * 
	 * @param value the identifier of the value
	 * @param registry the registry key of the registry in the root registry
	 */
	public static <T> RegistryKey<T> of(RegistryKey<? extends Registry<T>> registry, Identifier value) {
		return of(registry.value, value);
	}

	/**
	 * Creates a registry key for a registry in the root registry (registry of
	 * all registries) with an identifier for the registry.
	 * 
	 * <p>You can call it like {@code RegistryKey.of(new Identifier("block"))}
	 * to create a registry key for the block registry.
	 * 
	 * @param <T> the element type of the registry
	 * 
	 * @param registry the identifier of the registry
	 */
	public static <T> RegistryKey<Registry<T>> ofRegistry(Identifier registry) {
		return of(Registries.ROOT_KEY, registry);
	}

	private static <T> RegistryKey<T> of(Identifier registry, Identifier value) {
		return (RegistryKey<T>)INSTANCES.computeIfAbsent(new RegistryKey.RegistryIdPair(registry, value), pair -> new RegistryKey(pair.registry, pair.id));
	}

	private RegistryKey(Identifier registry, Identifier value) {
		this.registry = registry;
		this.value = value;
	}

	public String toString() {
		return "ResourceKey[" + this.registry + " / " + this.value + "]";
	}

	/**
	 * Returns whether this registry key belongs to the given registry (according to its type, not whether the registry actually contains this key).
	 * 
	 * @param registry the key of the registry that this registry key should be inside
	 */
	public boolean isOf(RegistryKey<? extends Registry<?>> registry) {
		return this.registry.equals(registry.getValue());
	}

	/**
	 * {@return {@code Optional.of(this)} if the key is of {@code registryRef},
	 * otherwise {@link Optional#empty}}
	 * 
	 * @apiNote This can be used to safely cast an unknown key to {@code RegistryKey<E>}
	 * by passing the registry {@code E}.
	 */
	public <E> Optional<RegistryKey<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
		return this.isOf(registryRef) ? Optional.of(this) : Optional.empty();
	}

	public Identifier getValue() {
		return this.value;
	}

	public Identifier getRegistry() {
		return this.registry;
	}

	static record RegistryIdPair(Identifier registry, Identifier id) {
	}
}
