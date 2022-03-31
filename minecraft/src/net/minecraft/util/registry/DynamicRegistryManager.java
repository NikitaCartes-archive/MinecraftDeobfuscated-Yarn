package net.minecraft.util.registry;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.EntryLoader;
import net.minecraft.util.dynamic.RegistryLoader;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.slf4j.Logger;

/**
 * A manager of dynamic registries. It allows users to access non-hardcoded
 * registries reliably.
 * 
 * <p>Each minecraft server has a dynamic registry manager for file-loaded
 * registries, while each client play network handler has a dynamic registry
 * manager for server-sent dynamic registries.
 * 
 * <p>The {@link DynamicRegistryManager.ImmutableImpl}
 * class serves as an immutable implementation of any particular collection
 * or configuration of dynamic registries.
 */
public interface DynamicRegistryManager {
	Logger LOGGER = LogUtils.getLogger();
	Map<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> INFOS = Util.make(() -> {
		Builder<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> builder = ImmutableMap.builder();
		register(builder, Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC, DimensionType.CODEC);
		register(builder, Registry.BIOME_KEY, Biome.CODEC, Biome.field_26633);
		register(builder, Registry.CONFIGURED_CARVER_KEY, ConfiguredCarver.CODEC);
		register(builder, Registry.CONFIGURED_FEATURE_KEY, ConfiguredFeature.CODEC);
		register(builder, Registry.PLACED_FEATURE_KEY, PlacedFeature.CODEC);
		register(builder, Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, StructureFeature.FEATURE_CODEC);
		register(builder, Registry.STRUCTURE_SET_KEY, StructureSet.CODEC);
		register(builder, Registry.STRUCTURE_PROCESSOR_LIST_KEY, StructureProcessorType.field_25876);
		register(builder, Registry.STRUCTURE_POOL_KEY, StructurePool.CODEC);
		register(builder, Registry.CHUNK_GENERATOR_SETTINGS_KEY, ChunkGeneratorSettings.CODEC);
		register(builder, Registry.NOISE_WORLDGEN, DoublePerlinNoiseSampler.NoiseParameters.field_35424);
		register(builder, Registry.DENSITY_FUNCTION_KEY, DensityFunction.field_37057);
		register(builder, Registry.WORLD_PRESET_WORLDGEN, WorldPreset.CODEC);
		register(builder, Registry.FLAT_LEVEL_GENERATOR_PRESET_WORLDGEN, FlatLevelGeneratorPreset.CODEC);
		return builder.build();
	});
	Codec<DynamicRegistryManager> CODEC = createCodec();
	Supplier<DynamicRegistryManager.Immutable> BUILTIN = Suppliers.memoize(() -> createAndLoad().toImmutable());

	/**
	 * Retrieves a registry optionally from this manager.
	 */
	<E> Optional<Registry<E>> getOptionalManaged(RegistryKey<? extends Registry<? extends E>> key);

	/**
	 * Retrieves a registry from this manager,
	 * or throws an exception when the registry does not exist.
	 * 
	 * @throws IllegalStateException if the registry does not exist
	 */
	default <E> Registry<E> getManaged(RegistryKey<? extends Registry<? extends E>> key) {
		return (Registry<E>)this.getOptionalManaged(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
	}

	default <E> Optional<? extends Registry<E>> getOptional(RegistryKey<? extends Registry<? extends E>> key) {
		Optional<? extends Registry<E>> optional = this.getOptionalManaged(key);
		return optional.isPresent() ? optional : Registry.REGISTRIES.getOrEmpty(key.getValue());
	}

	/**
	 * Retrieves a registry from this manager or {@link Registry#REGISTRIES},
	 * or throws an exception when the registry does not exist.
	 * 
	 * @throws IllegalStateException if the registry does not exist
	 */
	default <E> Registry<E> get(RegistryKey<? extends Registry<? extends E>> key) {
		return (Registry<E>)this.getOptional(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
	}

	private static <E> void register(
		Builder<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> infosBuilder, RegistryKey<? extends Registry<E>> registryRef, Codec<E> entryCodec
	) {
		infosBuilder.put(registryRef, new DynamicRegistryManager.Info<>(registryRef, entryCodec, null));
	}

	private static <E> void register(
		Builder<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> infosBuilder,
		RegistryKey<? extends Registry<E>> registryRef,
		Codec<E> entryCodec,
		Codec<E> networkEntryCodec
	) {
		infosBuilder.put(registryRef, new DynamicRegistryManager.Info<>(registryRef, entryCodec, networkEntryCodec));
	}

	static Iterable<DynamicRegistryManager.Info<?>> getInfos() {
		return INFOS.values();
	}

	Stream<DynamicRegistryManager.Entry<?>> streamManagedRegistries();

	private static Stream<DynamicRegistryManager.Entry<Object>> streamStaticRegistries() {
		return Registry.REGISTRIES.streamEntries().map(DynamicRegistryManager.Entry::of);
	}

	default Stream<DynamicRegistryManager.Entry<?>> streamAllRegistries() {
		return Stream.concat(this.streamManagedRegistries(), streamStaticRegistries());
	}

	default Stream<DynamicRegistryManager.Entry<?>> streamSyncedRegistries() {
		return Stream.concat(this.streamSyncedManagedRegistries(), streamStaticRegistries());
	}

	private static <E> Codec<DynamicRegistryManager> createCodec() {
		Codec<RegistryKey<? extends Registry<E>>> codec = Identifier.CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);
		Codec<Registry<E>> codec2 = codec.partialDispatch(
			"type",
			registry -> DataResult.success(registry.getKey()),
			registryRef -> getNetworkEntryCodec(registryRef).map(codecx -> RegistryCodecs.createRegistryCodec(registryRef, Lifecycle.experimental(), codecx))
		);
		UnboundedMapCodec<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> unboundedMapCodec = Codec.unboundedMap(codec, codec2);
		return createCodec(unboundedMapCodec);
	}

	private static <K extends RegistryKey<? extends Registry<?>>, V extends Registry<?>> Codec<DynamicRegistryManager> createCodec(
		UnboundedMapCodec<K, V> originalCodec
	) {
		return originalCodec.xmap(
			DynamicRegistryManager.ImmutableImpl::new,
			dynamicRegistryManager -> (Map)dynamicRegistryManager.streamSyncedManagedRegistries()
					.collect(ImmutableMap.toImmutableMap(entry -> entry.key(), entry -> entry.value()))
		);
	}

	private Stream<DynamicRegistryManager.Entry<?>> streamSyncedManagedRegistries() {
		return this.streamManagedRegistries().filter(entry -> ((DynamicRegistryManager.Info)INFOS.get(entry.key)).isSynced());
	}

	private static <E> DataResult<? extends Codec<E>> getNetworkEntryCodec(RegistryKey<? extends Registry<E>> registryKey) {
		return (DataResult<? extends Codec<E>>)Optional.ofNullable((DynamicRegistryManager.Info)INFOS.get(registryKey))
			.map(info -> info.networkEntryCodec())
			.map(DataResult::success)
			.orElseGet(() -> DataResult.error("Unknown or not serializable registry: " + registryKey));
	}

	private static Map<RegistryKey<? extends Registry<?>>, ? extends MutableRegistry<?>> createMutableRegistries() {
		return (Map<RegistryKey<? extends Registry<?>>, ? extends MutableRegistry<?>>)INFOS.keySet()
			.stream()
			.collect(Collectors.toMap(Function.identity(), DynamicRegistryManager::createSimpleRegistry));
	}

	private static DynamicRegistryManager.Mutable createMutableRegistryManager() {
		return new DynamicRegistryManager.MutableImpl(createMutableRegistries());
	}

	static DynamicRegistryManager.Immutable of(Registry<? extends Registry<?>> registries) {
		return new DynamicRegistryManager.Immutable() {
			@Override
			public <T> Optional<Registry<T>> getOptionalManaged(RegistryKey<? extends Registry<? extends T>> key) {
				Registry<Registry<T>> registry = (Registry<Registry<T>>)registries;
				return registry.getOrEmpty((RegistryKey<Registry<T>>)key);
			}

			@Override
			public Stream<DynamicRegistryManager.Entry<?>> streamManagedRegistries() {
				return registries.getEntrySet().stream().map(DynamicRegistryManager.Entry::of);
			}
		};
	}

	static DynamicRegistryManager.Mutable createAndLoad() {
		DynamicRegistryManager.Mutable mutable = createMutableRegistryManager();
		EntryLoader.Impl impl = new EntryLoader.Impl();

		for (java.util.Map.Entry<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> entry : INFOS.entrySet()) {
			addEntriesToLoad(impl, (DynamicRegistryManager.Info)entry.getValue());
		}

		RegistryOps.ofLoaded(JsonOps.INSTANCE, mutable, impl);
		return mutable;
	}

	private static <E> void addEntriesToLoad(EntryLoader.Impl entryLoader, DynamicRegistryManager.Info<E> info) {
		RegistryKey<? extends Registry<E>> registryKey = info.registry();
		Registry<E> registry = BuiltinRegistries.DYNAMIC_REGISTRY_MANAGER.get(registryKey);

		for (java.util.Map.Entry<RegistryKey<E>, E> entry : registry.getEntrySet()) {
			RegistryKey<E> registryKey2 = (RegistryKey<E>)entry.getKey();
			E object = (E)entry.getValue();
			entryLoader.add(
				BuiltinRegistries.DYNAMIC_REGISTRY_MANAGER, registryKey2, info.entryCodec(), registry.getRawId(object), object, registry.getEntryLifecycle(object)
			);
		}
	}

	/**
	 * Loads a dynamic registry manager from the resource manager's data files.
	 */
	static void load(DynamicRegistryManager.Mutable dynamicRegistryManager, DynamicOps<JsonElement> ops, RegistryLoader registryLoader) {
		RegistryLoader.LoaderAccess loaderAccess = registryLoader.createAccess(dynamicRegistryManager);

		for (DynamicRegistryManager.Info<?> info : INFOS.values()) {
			load(ops, loaderAccess, info);
		}
	}

	/**
	 * Loads elements from the {@code ops} into the registry specified by {@code
	 * info} within the {@code manager}. Note that the resource manager instance
	 * is kept within the {@code ops}.
	 */
	private static <E> void load(DynamicOps<JsonElement> ops, RegistryLoader.LoaderAccess loaderAccess, DynamicRegistryManager.Info<E> info) {
		DataResult<? extends Registry<E>> dataResult = loaderAccess.load(info.registry(), info.entryCodec(), ops);
		dataResult.error().ifPresent(partialResult -> {
			throw new JsonParseException("Error loading registry data: " + partialResult.message());
		});
	}

	static DynamicRegistryManager createDynamicRegistryManager(Dynamic<?> dynamic) {
		return new DynamicRegistryManager.ImmutableImpl(
			(Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>>)INFOS.keySet()
				.stream()
				.collect(Collectors.toMap(Function.identity(), registryRef -> createRegistry(registryRef, dynamic)))
		);
	}

	static <E> Registry<E> createRegistry(RegistryKey<? extends Registry<? extends E>> registryRef, Dynamic<?> dynamic) {
		return (Registry<E>)RegistryOps.createRegistryCodec(registryRef)
			.codec()
			.parse(dynamic)
			.resultOrPartial(Util.addPrefix(registryRef + " registry: ", LOGGER::error))
			.orElseThrow(() -> new IllegalStateException("Failed to get " + registryRef + " registry"));
	}

	static <E> MutableRegistry<?> createSimpleRegistry(RegistryKey<? extends Registry<?>> registryRef) {
		return new SimpleRegistry<>(registryRef, Lifecycle.stable(), null);
	}

	default DynamicRegistryManager.Immutable toImmutable() {
		return new DynamicRegistryManager.ImmutableImpl(this.streamManagedRegistries().map(DynamicRegistryManager.Entry::freeze));
	}

	default Lifecycle getRegistryLifecycle() {
		return (Lifecycle)this.streamManagedRegistries().map(entry -> entry.value.getLifecycle()).reduce(Lifecycle.stable(), Lifecycle::add);
	}

	public static record Entry<T>(RegistryKey<? extends Registry<T>> key, Registry<T> value) {

		private static <T, R extends Registry<? extends T>> DynamicRegistryManager.Entry<T> of(
			java.util.Map.Entry<? extends RegistryKey<? extends Registry<?>>, R> entry
		) {
			return of((RegistryKey<? extends Registry<?>>)entry.getKey(), (Registry<?>)entry.getValue());
		}

		private static <T> DynamicRegistryManager.Entry<T> of(RegistryEntry.Reference<? extends Registry<? extends T>> entry) {
			return of(entry.registryKey(), (Registry<?>)entry.value());
		}

		private static <T> DynamicRegistryManager.Entry<T> of(RegistryKey<? extends Registry<?>> key, Registry<?> value) {
			return new DynamicRegistryManager.Entry<>((RegistryKey<? extends Registry<T>>)key, (Registry<T>)value);
		}

		private DynamicRegistryManager.Entry<T> freeze() {
			return new DynamicRegistryManager.Entry<>(this.key, this.value.freeze());
		}
	}

	public interface Immutable extends DynamicRegistryManager {
		@Override
		default DynamicRegistryManager.Immutable toImmutable() {
			return this;
		}
	}

	public static final class ImmutableImpl implements DynamicRegistryManager.Immutable {
		private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> registries;

		public ImmutableImpl(Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> registries) {
			this.registries = Map.copyOf(registries);
		}

		ImmutableImpl(Stream<DynamicRegistryManager.Entry<?>> stream) {
			this.registries = (Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>>)stream.collect(
				ImmutableMap.toImmutableMap(DynamicRegistryManager.Entry::key, DynamicRegistryManager.Entry::value)
			);
		}

		@Override
		public <E> Optional<Registry<E>> getOptionalManaged(RegistryKey<? extends Registry<? extends E>> key) {
			return Optional.ofNullable((Registry)this.registries.get(key)).map(registry -> registry);
		}

		@Override
		public Stream<DynamicRegistryManager.Entry<?>> streamManagedRegistries() {
			return this.registries.entrySet().stream().map(DynamicRegistryManager.Entry::of);
		}
	}

	/**
	 * Represents the serialization behavior of the registries, including the
	 * id of the registry, the codec for its elements, and whether the registry
	 * should be sent to the client.
	 */
	public static record Info<E>(RegistryKey<? extends Registry<E>> registry, Codec<E> entryCodec, @Nullable Codec<E> networkEntryCodec) {
		public boolean isSynced() {
			return this.networkEntryCodec != null;
		}
	}

	public interface Mutable extends DynamicRegistryManager {
		<E> Optional<MutableRegistry<E>> getOptionalMutable(RegistryKey<? extends Registry<? extends E>> key);

		default <E> MutableRegistry<E> getMutable(RegistryKey<? extends Registry<? extends E>> key) {
			return (MutableRegistry<E>)this.getOptionalMutable(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
		}
	}

	public static final class MutableImpl implements DynamicRegistryManager.Mutable {
		private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends MutableRegistry<?>> mutableRegistries;

		MutableImpl(Map<? extends RegistryKey<? extends Registry<?>>, ? extends MutableRegistry<?>> mutableRegistries) {
			this.mutableRegistries = mutableRegistries;
		}

		@Override
		public <E> Optional<Registry<E>> getOptionalManaged(RegistryKey<? extends Registry<? extends E>> key) {
			return Optional.ofNullable((MutableRegistry)this.mutableRegistries.get(key)).map(registry -> registry);
		}

		@Override
		public <E> Optional<MutableRegistry<E>> getOptionalMutable(RegistryKey<? extends Registry<? extends E>> key) {
			return Optional.ofNullable((MutableRegistry)this.mutableRegistries.get(key)).map(registry -> registry);
		}

		@Override
		public Stream<DynamicRegistryManager.Entry<?>> streamManagedRegistries() {
			return this.mutableRegistries.entrySet().stream().map(DynamicRegistryManager.Entry::of);
		}
	}
}
