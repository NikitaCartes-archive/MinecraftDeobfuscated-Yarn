package net.minecraft.registry;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

/**
 * A registry is used to register various in-game components. Almost all parts of the
 * game - from blocks, items, and entity types, to cat types, goat horn instruments,
 * and structure pools - are registered in registries. Registry system allows the game
 * to enumerate all known types of something, and to assign a unique identifier to each
 * of those. Therefore, registering an object in the registry plays a very important
 * role, and failure to register new instances of registerable object usually results
 * in a bug or even a crash.
 * 
 * <h2 id="terms">Terminologies</h2>
 * <p>A <strong>registry</strong> is an object that holds the mapping between three things:
 * the string ID, the numeric ID, and the registered value. There are many registries
 * for different types of registerable objects, and a registry's type parameter indicates
 * the accepted type. For example, you register your {@link net.minecraft.block.Block} to {@code
 * Registry<Block>}. It's important to note that registries themselves are registered
 * in a "registry of registries", {@link Registries#ROOT}.
 * 
 * <p>The <strong>string ID</strong>, usually just called "ID", is a human-readable
 * {@link Identifier} that uniquely identifies the registered value in a registry.
 * This should stay the same between two game versions, and is usually used for disk
 * storage.
 * 
 * <p>The <strong>numeric ID</strong> or <strong>raw ID</strong> is an integer
 * assigned automatically by the registry to each registered value. This is not
 * guaranteed to stay the same between two game versions, and is usually used for
 * networking purposes.
 * 
 * <p>The <strong>registered value</strong>, often just called "value" in the code,
 * is the value added to the registry. The registry's type parameter determines
 * the type of the registered value.
 * 
 * <p>Each registered value can also be identified with a <strong>{@linkplain RegistryKey
 * registry key}</strong>. A registry key is a combination of the registry's ID and
 * the registered value's ID. Using a registry key makes the type of the ID's
 * associated value clear, as the type parameter contains the type.
 * 
 * <p>A <strong>{@linkplain RegistryEntry registry entry}</strong> is an object
 * holding a value that can be registered in a registry. In most cases, the
 * value is already registered in a registry ("reference entry"), hence the name;
 * however, it is possible to create a registry entry by direct reference
 * ("direct entry"). This is useful for data packs, as they can define
 * one-time use values directly without having to register them every time.
 * 
 * <p>A <strong>{@link RegistryEntryList registry entry list}</strong> is a list
 * of registry entries. This, is either a direct reference to each item, or
 * a reference to a tag. A <strong>tag</strong> is a way to dynamically
 * define a list of registered values. Anything registered in a registry
 * can be tagged, and each registry holds a list of tags it recognizes.
 * 
 * <h2 id="static-and-dynamic-registries">Static and dynamic registries</h2>
 * <p>There are two kinds of registries: static and dynamic.
 * 
 * <ul>
 * <li>A <strong>static registry</strong> is a registry whose values are hard-coded
 * in the game and cannot be added or modified through data packs. Most registries
 * are static. Since they cannot be modified (without mods), it is a singleton,
 * and exists in this class. During the game bootstrap, vanilla objects are
 * registered, after which the registry gets frozen to prohibit further changes.</li>
 * 
 * <li>A <strong>dynamic registry</strong> is a registry whose values can be
 * added or replaced through data packs. A dynamic registry is bound to a server,
 * and multiple registries for the same type of registerable object can exist during
 * the lifetime of the game. When a player joins, the server sends the contents of
 * the dynamic registry manager to the client, but only "network serializable"
 * registries are sent. To access a dynamic registry, first get an instance of the
 * dynamic registry manager, then call the {@link DynamicRegistryManager#get} method.</li>
 * </ul>
 * 
 * <h2 id="using">Using Registry</h2>
 * <h3 id="reading">Reading Registry</h3>
 * <p>A registry is also an {@link IndexedIterable}. Therefore, registries can be
 * iterated using, e.g. {@code for (Block block : Registries.BLOCK)}.
 * 
 * <p>There are several other methods used for reading the contents of the registry:
 * <ul>
 * <li>{@link #entryOf} or {@link #getEntry(RegistryKey)} for getting the registry entry
 * from the key.</li>
 * <li>{@link #get(Identifier)} or {@link #get(RegistryKey)} for getting the registered
 * value from the ID or the registry key.</li>
 * <li>{@link #getId(Object)} for getting the ID of a registered value.</li>
 * <li>{@link #getEntry(int)} for getting the registry entry from the raw ID.</li>
 * <li>{@link #getEntryList} and {@link #iterateEntries} for getting the contents of a tag,</li>
 * <li>{@link #streamTags} for streaming all tags of a registry.</li>
 * </ul>
 * 
 * <h3 id="registering">Registering something to Registry</h3>
 * <p>The steps for registration are different, depending on whether the registry is static
 * or dynamic. For dynamic registries, data packs can usually be used to register a new
 * value or replace one. For static registries, the game's code must be modified.
 * 
 * <p>Static registries are defined in {@link Registries}, and unlike the dynamic registries, it
 * cannot be changed after the game initialization. The game enforces this by "freezing"
 * the registry. Attempting to register a value after freezing causes a crash, such as
 * "Registry is already frozen". Modding APIs usually provide a way to bypass this restriction.
 * 
 * <p>Use {@link #register(Registry, Identifier, Object)} for registering a value to a registry.
 * 
 * <h3 id="intrusive-holders">Intrusive holders</h3>
 * <p>For historical reasons, there are two types of reference registry entries.
 * (This is different from the "direct" and "reference" registry entry types.)
 * 
 * <ul>
 * <li><strong>Intrusive holders</strong> are registry entries tied to a specific
 * registerable object at instantiation time. When instantiating those, it promises
 * that the object is later registered - which, if broken, will result in a crash.
 * This is used for {@link Registries#BLOCK}, {@link Registries#ITEM}, {@link Registries#FLUID},
 * {@link Registries#ENTITY_TYPE}, and {@link Registries#GAME_EVENT} registries.</li>
 * <li><strong>Standalone holders</strong> are registry entries that are not intrusive.
 * There is no restriction on instantiation.</li>
 * </ul>
 * 
 * <p>When a class whose instances are registered as intrusive holders, such as
 * {@link net.minecraft.block.Block} or {@link net.minecraft.item.Item}, are instantiated
 * without registering, the game crashes with "Some intrusive holders were not added to
 * registry" error message. <strong>This includes conditional registration</strong>.
 * For example, the code below can cause a crash:
 * 
 * <pre>{@code
 * Item myItem = new Item(new Item.Settings());
 * if (condition) {
 *     Registry.register(Registries.ITEM, new Identifier("example", "bad"), myItem);
 * }
 * }</pre>
 * 
 * <p>The correct way is to make the instantiation conditional as well:
 * 
 * <pre>{@code
 * if (condition) {
 *     Item myItem = new Item(new Item.Settings());
 *     Registry.register(Registries.ITEM, new Identifier("example", "bad"), myItem);
 * }
 * }</pre>
 */
public interface Registry<T> extends Keyable, IndexedIterable<T> {
	/**
	 * {@return the registry key that identifies this registry}
	 */
	RegistryKey<? extends Registry<T>> getKey();

	/**
	 * {@return the codec for serializing {@code T}}
	 * 
	 * @implNote This serializes a value using the ID or (if compressed) the raw ID.
	 */
	default Codec<T> getCodec() {
		Codec<T> codec = Identifier.CODEC
			.flatXmap(
				id -> (DataResult)Optional.ofNullable(this.get(id))
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + this.getKey() + ": " + id)),
				value -> (DataResult)this.getKey((T)value)
						.map(RegistryKey::getValue)
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error(() -> "Unknown registry element in " + this.getKey() + ":" + value))
			);
		Codec<T> codec2 = Codecs.rawIdChecked(value -> this.getKey((T)value).isPresent() ? this.getRawId((T)value) : -1, this::get, -1);
		return Codecs.withLifecycle(Codecs.orCompressed(codec, codec2), this::getEntryLifecycle, this::getEntryLifecycle);
	}

	/**
	 * {@return the codec for serializing the registry entry of {@code T}}
	 * 
	 * @implNote This serializes a registry entry using the ID.
	 */
	default Codec<RegistryEntry<T>> createEntryCodec() {
		Codec<RegistryEntry<T>> codec = Identifier.CODEC
			.flatXmap(
				id -> (DataResult)this.getEntry(id)
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + this.getKey() + ": " + id)),
				entry -> (DataResult)entry.getKey()
						.map(RegistryKey::getValue)
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error(() -> "Unknown registry element in " + this.getKey() + ":" + entry))
			);
		return Codecs.withLifecycle(codec, entry -> this.getEntryLifecycle((T)entry.value()), entry -> this.getEntryLifecycle((T)entry.value()));
	}

	@Override
	default <U> Stream<U> keys(DynamicOps<U> ops) {
		return this.getIds().stream().map(id -> ops.createString(id.toString()));
	}

	/**
	 * {@return the ID assigned to {@code value}, or {@code null} if it is not registered}
	 */
	@Nullable
	Identifier getId(T value);

	/**
	 * {@return the registry key of {@code value}, or an empty optional if it is not registered}
	 */
	Optional<RegistryKey<T>> getKey(T entry);

	@Override
	int getRawId(@Nullable T value);

	/**
	 * {@return the value that is assigned {@code key}, or {@code null} if there is none}
	 */
	@Nullable
	T get(@Nullable RegistryKey<T> key);

	/**
	 * {@return the value that is assigned {@code id}, or {@code null} if there is none}
	 */
	@Nullable
	T get(@Nullable Identifier id);

	/**
	 * Gets the lifecycle of a registry entry.
	 */
	Lifecycle getEntryLifecycle(T entry);

	Lifecycle getLifecycle();

	/**
	 * {@return the value that is assigned {@code id}, or an empty optional if there is none}
	 */
	default Optional<T> getOrEmpty(@Nullable Identifier id) {
		return Optional.ofNullable(this.get(id));
	}

	/**
	 * {@return the value that is assigned {@code key}, or an empty optional if there is none}
	 */
	default Optional<T> getOrEmpty(@Nullable RegistryKey<T> key) {
		return Optional.ofNullable(this.get(key));
	}

	/**
	 * {@return the value that is assigned {@code key}}
	 * 
	 * @throws IllegalStateException if there is no value with {@code key} in the registry
	 */
	default T getOrThrow(RegistryKey<T> key) {
		T object = this.get(key);
		if (object == null) {
			throw new IllegalStateException("Missing key in " + this.getKey() + ": " + key);
		} else {
			return object;
		}
	}

	/**
	 * {@return the set of all IDs registered in a registry}
	 */
	Set<Identifier> getIds();

	/**
	 * {@return the set containing {@link Map.Entry} of the registry keys and values registered
	 * in this registry}
	 */
	Set<Entry<RegistryKey<T>, T>> getEntrySet();

	/**
	 * {@return the set of all registry keys registered in a registry}
	 */
	Set<RegistryKey<T>> getKeys();

	/**
	 * {@return a random registry entry from this registry, or an empty optional if the
	 * registry is empty}
	 */
	Optional<RegistryEntry.Reference<T>> getRandom(Random random);

	/**
	 * {@return a stream of all values of this registry}
	 */
	default Stream<T> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	/**
	 * {@return whether {@code id} is registered in this registry}
	 */
	boolean containsId(Identifier id);

	/**
	 * {@return whether {@code key} is registered in this registry}
	 */
	boolean contains(RegistryKey<T> key);

	static <T> T register(Registry<? super T> registry, String id, T entry) {
		return register(registry, new Identifier(id), entry);
	}

	/**
	 * Registers {@code entry} to {@code registry} under {@code id}.
	 * 
	 * @return the passed {@code entry}
	 */
	static <V, T extends V> T register(Registry<V> registry, Identifier id, T entry) {
		return register(registry, RegistryKey.of(registry.getKey(), id), entry);
	}

	/**
	 * Registers {@code entry} to {@code registry} under {@code key}.
	 * 
	 * @return the passed {@code entry}
	 */
	static <V, T extends V> T register(Registry<V> registry, RegistryKey<V> key, T entry) {
		((MutableRegistry)registry).add(key, (V)entry, Lifecycle.stable());
		return entry;
	}

	static <T> RegistryEntry.Reference<T> registerReference(Registry<T> registry, RegistryKey<T> key, T entry) {
		return ((MutableRegistry)registry).add(key, entry, Lifecycle.stable());
	}

	static <T> RegistryEntry.Reference<T> registerReference(Registry<T> registry, Identifier id, T entry) {
		return registerReference(registry, RegistryKey.of(registry.getKey(), id), entry);
	}

	Registry<T> freeze();

	RegistryEntry.Reference<T> createEntry(T value);

	/**
	 * {@return the reference registry entry for the value assigned {@code rawId}, or an
	 * empty optional if there is no such value}
	 */
	Optional<RegistryEntry.Reference<T>> getEntry(int rawId);

	Optional<RegistryEntry.Reference<T>> getEntry(Identifier id);

	/**
	 * {@return the reference registry entry for the value assigned {@code key}, or an
	 * empty optional if there is no such value}
	 * 
	 * @see #entryOf
	 */
	Optional<RegistryEntry.Reference<T>> getEntry(RegistryKey<T> key);

	RegistryEntry<T> getEntry(T value);

	/**
	 * {@return the reference registry entry for the value assigned {@code key}}
	 * 
	 * @throws IllegalStateException if there is no value that is assigned {@code key}
	 * 
	 * @see #getEntry(RegistryKey)
	 */
	default RegistryEntry.Reference<T> entryOf(RegistryKey<T> key) {
		return (RegistryEntry.Reference<T>)this.getEntry(key).orElseThrow(() -> new IllegalStateException("Missing key in " + this.getKey() + ": " + key));
	}

	/**
	 * {@return a stream of reference registry entries of this registry}
	 */
	Stream<RegistryEntry.Reference<T>> streamEntries();

	/**
	 * {@return the registry entry list of values that are assigned {@code tag}, or an empty
	 * optional if the tag is not known to the registry}
	 */
	Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> tag);

	/**
	 * {@return an iterable of values that are assigned {@code tag}, or an empty iterable
	 * if the tag is not known to the registry}
	 */
	default Iterable<RegistryEntry<T>> iterateEntries(TagKey<T> tag) {
		return DataFixUtils.orElse(this.getEntryList(tag), List.of());
	}

	/**
	 * {@return a random entry from {@code tag}, or an empty {@link Optional} if the
	 * tag is empty}
	 */
	default Optional<RegistryEntry<T>> getRandomEntry(TagKey<T> tag, Random random) {
		return this.getEntryList(tag).flatMap(entryList -> entryList.getRandom(random));
	}

	RegistryEntryList.Named<T> getOrCreateEntryList(TagKey<T> tag);

	Stream<Pair<TagKey<T>, RegistryEntryList.Named<T>>> streamTagsAndEntries();

	/**
	 * {@return a stream of all tag keys known to this registry}
	 */
	Stream<TagKey<T>> streamTags();

	void clearTags();

	void populateTags(Map<TagKey<T>, List<RegistryEntry<T>>> tagEntries);

	default IndexedIterable<RegistryEntry<T>> getIndexedEntries() {
		return new IndexedIterable<RegistryEntry<T>>() {
			public int getRawId(RegistryEntry<T> registryEntry) {
				return Registry.this.getRawId(registryEntry.value());
			}

			@Nullable
			public RegistryEntry<T> get(int i) {
				return (RegistryEntry<T>)Registry.this.getEntry(i).orElse(null);
			}

			@Override
			public int size() {
				return Registry.this.size();
			}

			public Iterator<RegistryEntry<T>> iterator() {
				return Registry.this.streamEntries().map(entry -> entry).iterator();
			}
		};
	}

	RegistryEntryOwner<T> getEntryOwner();

	/**
	 * {@return a registry wrapper that does not mutate the backing registry under
	 * any circumstances}
	 */
	RegistryWrapper.Impl<T> getReadOnlyWrapper();

	/**
	 * {@return a registry wrapper that creates and stores a new registry entry list
	 * when handling an unknown tag key}
	 */
	default RegistryWrapper.Impl<T> getTagCreatingWrapper() {
		return new RegistryWrapper.Impl.Delegating<T>() {
			@Override
			protected RegistryWrapper.Impl<T> getBase() {
				return Registry.this.getReadOnlyWrapper();
			}

			@Override
			public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
				return Optional.of(this.getOrThrow(tag));
			}

			@Override
			public RegistryEntryList.Named<T> getOrThrow(TagKey<T> tag) {
				return Registry.this.getOrCreateEntryList(tag);
			}
		};
	}
}
