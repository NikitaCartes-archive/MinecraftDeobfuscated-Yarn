package net.minecraft.util.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A manager of dynamic registries. It allows users to access non-hardcoded
 * registries reliably.
 * 
 * <p>Each minecraft server has a dynamic registry manager for file-loaded
 * registries, while each client play network handler has a dynamic registry
 * manager for server-sent dynamic registries.</p>
 * 
 * <p>The {@link DynamicRegistryManager.Impl}
 * class serves as an immutable implementation of any particular collection
 * or configuration of dynamic registries.</p>
 */
public interface DynamicRegistryManager {
	Logger LOGGER = LogManager.getLogger();
	Map<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> INFOS = Util.make(() -> {
		Builder<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> builder = ImmutableMap.builder();
		register(builder, Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC, true);
		register(builder, Registry.BIOME_KEY, Biome.CODEC, true);
		register(builder, Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN, ConfiguredSurfaceBuilder.field_25878, false);
		register(builder, Registry.CONFIGURED_CARVER_WORLDGEN, ConfiguredCarver.field_25832, false);
		register(builder, Registry.CONFIGURED_FEATURE_WORLDGEN, ConfiguredFeature.field_25833, false);
		register(builder, Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, ConfiguredStructureFeature.CODEC, false);
		register(builder, Registry.PROCESSOR_LIST_WORLDGEN, StructureProcessorType.PROCESSORS, false);
		register(builder, Registry.TEMPLATE_POOL_WORLDGEN, StructurePool.CODEC, false);
		register(builder, Registry.NOISE_SETTINGS_WORLDGEN, ChunkGeneratorType.field_24780, false);
		return builder.build();
	});

	/**
	 * Retrieves a registry optionally from this manager.
	 */
	<E> Optional<MutableRegistry<E>> getOptional(RegistryKey<? extends Registry<E>> key);

	/**
	 * Retrieves a registry from this manager, or throws an exception when the
	 * registry does not exist.
	 * 
	 * @throws IllegalStateException if the registry does not exist
	 */
	default <E> MutableRegistry<E> get(RegistryKey<? extends Registry<E>> key) {
		return (MutableRegistry<E>)this.getOptional(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
	}

	default Registry<DimensionType> getDimensionTypes() {
		return this.get(Registry.DIMENSION_TYPE_KEY);
	}

	static <E> Builder<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> register(
		Builder<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> infosBuilder,
		RegistryKey<? extends Registry<E>> registryRef,
		MapCodec<E> elementCodec,
		boolean synced
	) {
		return infosBuilder.put(registryRef, new DynamicRegistryManager.Info<>(registryRef, elementCodec, synced));
	}

	/**
	 * Creates a default dynamic registry manager.
	 */
	static DynamicRegistryManager.Impl create() {
		DynamicRegistryManager.Impl impl = new DynamicRegistryManager.Impl();
		DimensionType.addRegistryDefaults(impl);
		INFOS.keySet().stream().filter(registryKey -> !registryKey.equals(Registry.DIMENSION_TYPE_KEY)).forEach(registryKey -> setupBuiltin(impl, registryKey));
		return impl;
	}

	/**
	 * Add all entries of the registry referred by {@code registryRef} to the
	 * corresponding registry within this manager.
	 */
	static <R extends Registry<?>> void setupBuiltin(DynamicRegistryManager.Impl manager, RegistryKey<R> registryRef) {
		Registry<R> registry = (Registry<R>)BuiltinRegistries.REGISTRIES;
		Registry<?> registry2 = registry.get(registryRef);
		if (registry2 == null) {
			throw new IllegalStateException("Missing builtin registry: " + registryRef);
		} else {
			addBuiltinEntries(manager, registry2);
		}
	}

	/**
	 * Add all entries of the {@code registry} to the corresponding registry
	 * within this manager.
	 */
	static <E> void addBuiltinEntries(DynamicRegistryManager.Impl manager, Registry<E> registry) {
		MutableRegistry<E> mutableRegistry = (MutableRegistry<E>)manager.getOptional(registry.getKey())
			.orElseThrow(() -> new IllegalStateException("Missing registry: " + registry.getKey()));

		for (Entry<RegistryKey<E>, E> entry : registry.getEntries()) {
			mutableRegistry.set(registry.getRawId((E)entry.getValue()), (RegistryKey<E>)entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Loads a dynamic registry manager from the resource manager's data files.
	 */
	@Environment(EnvType.CLIENT)
	static DynamicRegistryManager.Impl load(ResourceManager resourceManager) {
		DynamicRegistryManager.Impl impl = create();
		RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, resourceManager, impl);

		for (DynamicRegistryManager.Info<?> info : INFOS.values()) {
			load(registryOps, impl, info);
		}

		return impl;
	}

	/**
	 * Loads elements from the {@code ops} into the registry specified by {@code
	 * info} within the {@code manager}. Note that the resource manager instance
	 * is kept within the {@code ops}.
	 */
	@Environment(EnvType.CLIENT)
	static <E> void load(RegistryOps<JsonElement> ops, DynamicRegistryManager.Impl manager, DynamicRegistryManager.Info<E> info) {
		RegistryKey<? extends Registry<E>> registryKey = info.getRegistry();
		SimpleRegistry<E> simpleRegistry = (SimpleRegistry<E>)Optional.ofNullable(manager.registries.get(registryKey))
			.map(simpleRegistryx -> simpleRegistryx)
			.orElseThrow(() -> new IllegalStateException("Missing registry: " + registryKey));
		DataResult<SimpleRegistry<E>> dataResult = ops.loadToRegistry(simpleRegistry, info.getRegistry(), info.getElementCodec());
		dataResult.error().ifPresent(partialResult -> LOGGER.error("Error loading registry data: {}", partialResult.message()));
	}

	/**
	 * An immutable implementation of the dynamic registry manager, representing
	 * a specialized configuration of registries. It has a codec that allows
	 * conversion from and to data pack JSON or packet NBT.
	 */
	public static final class Impl implements DynamicRegistryManager {
		public static final Codec<DynamicRegistryManager.Impl> CODEC = setupCodec();
		private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> registries;

		private static <E> Codec<DynamicRegistryManager.Impl> setupCodec() {
			Codec<RegistryKey<? extends Registry<E>>> codec = Identifier.CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);
			Codec<SimpleRegistry<E>> codec2 = codec.partialDispatch(
				"type",
				simpleRegistry -> DataResult.success(simpleRegistry.getKey()),
				registryKey -> getDataResultForCodec(registryKey).map(mapCodec -> SimpleRegistry.method_29098(registryKey, Lifecycle.experimental(), mapCodec))
			);
			UnboundedMapCodec<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> unboundedMapCodec = Codec.unboundedMap(codec, codec2);
			return fromRegistryCodecs(unboundedMapCodec);
		}

		private static <K extends RegistryKey<? extends Registry<?>>, V extends SimpleRegistry<?>> Codec<DynamicRegistryManager.Impl> fromRegistryCodecs(
			UnboundedMapCodec<K, V> unboundedMapCodec
		) {
			return unboundedMapCodec.xmap(
				DynamicRegistryManager.Impl::new,
				impl -> (ImmutableMap)impl.registries
						.entrySet()
						.stream()
						.filter(entry -> ((DynamicRegistryManager.Info)INFOS.get(entry.getKey())).isSynced())
						.collect(ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue))
			);
		}

		private static <E> DataResult<? extends MapCodec<E>> getDataResultForCodec(RegistryKey<? extends Registry<E>> registryRef) {
			return (DataResult<? extends MapCodec<E>>)Optional.ofNullable(INFOS.get(registryRef))
				.map(info -> DataResult.success(info.getElementCodec()))
				.orElseGet(() -> DataResult.error("Unknown registry: " + registryRef));
		}

		public Impl() {
			this(
				(Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>>)INFOS.keySet()
					.stream()
					.collect(Collectors.toMap(Function.identity(), DynamicRegistryManager.Impl::createRegistry))
			);
		}

		private Impl(Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> registries) {
			this.registries = registries;
		}

		private static <E> SimpleRegistry<?> createRegistry(RegistryKey<? extends Registry<?>> registryRef) {
			return new SimpleRegistry<>(registryRef, Lifecycle.experimental());
		}

		@Override
		public <E> Optional<MutableRegistry<E>> getOptional(RegistryKey<? extends Registry<E>> key) {
			return Optional.ofNullable(this.registries.get(key)).map(simpleRegistry -> simpleRegistry);
		}
	}

	/**
	 * Represents the serialization behavior of the registries, including the
	 * id of the registry, the codec for its elements, and whether the registry
	 * should be sent to the client.
	 */
	public static final class Info<E> {
		private final RegistryKey<? extends Registry<E>> registry;
		private final MapCodec<E> elementCodec;
		private final boolean synced;

		public Info(RegistryKey<? extends Registry<E>> registry, MapCodec<E> elementCodec, boolean synced) {
			this.registry = registry;
			this.elementCodec = elementCodec;
			this.synced = synced;
		}

		@Environment(EnvType.CLIENT)
		public RegistryKey<? extends Registry<E>> getRegistry() {
			return this.registry;
		}

		public MapCodec<E> getElementCodec() {
			return this.elementCodec;
		}

		public boolean isSynced() {
			return this.synced;
		}
	}
}
