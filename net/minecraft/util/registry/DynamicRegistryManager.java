/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

/**
 * A manager of dynamic registries. It allows users to access non-hardcoded
 * registries reliably.
 * 
 * <p>Each minecraft server has a dynamic registry manager for file-loaded
 * registries, while each client play network handler has a dynamic registry
 * manager for server-sent dynamic registries.
 * 
 * <p>The {@link DynamicRegistryManager.Impl}
 * class serves as an immutable implementation of any particular collection
 * or configuration of dynamic registries.
 */
public abstract class DynamicRegistryManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<RegistryKey<? extends Registry<?>>, Info<?>> INFOS = Util.make(() -> {
        ImmutableMap.Builder<RegistryKey<Registry<?>>, Info<?>> builder = ImmutableMap.builder();
        DynamicRegistryManager.register(builder, Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC, DimensionType.CODEC);
        DynamicRegistryManager.register(builder, Registry.BIOME_KEY, Biome.CODEC, Biome.field_26633);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN, ConfiguredSurfaceBuilder.CODEC);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_CARVER_WORLDGEN, ConfiguredCarver.CODEC);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_FEATURE_WORLDGEN, ConfiguredFeature.CODEC);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, ConfiguredStructureFeature.CODEC);
        DynamicRegistryManager.register(builder, Registry.PROCESSOR_LIST_WORLDGEN, StructureProcessorType.field_25876);
        DynamicRegistryManager.register(builder, Registry.TEMPLATE_POOL_WORLDGEN, StructurePool.CODEC);
        DynamicRegistryManager.register(builder, Registry.NOISE_SETTINGS_WORLDGEN, ChunkGeneratorSettings.CODEC);
        return builder.build();
    });
    private static final Impl BUILTIN = Util.make(() -> {
        Impl impl = new Impl();
        DimensionType.addRegistryDefaults(impl);
        INFOS.keySet().stream().filter(registryKey -> !registryKey.equals(Registry.DIMENSION_TYPE_KEY)).forEach(registryKey -> DynamicRegistryManager.copyFromBuiltin(impl, registryKey));
        return impl;
    });

    /**
     * Retrieves a registry optionally from this manager.
     */
    public abstract <E> Optional<MutableRegistry<E>> getOptionalMutable(RegistryKey<? extends Registry<E>> var1);

    /**
     * Retrieves a registry from this manager, or throws an exception when the
     * registry does not exist.
     * 
     * @throws IllegalStateException if the registry does not exist
     */
    public <E> MutableRegistry<E> get(RegistryKey<? extends Registry<E>> key) {
        return this.getOptionalMutable(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
    }

    public Registry<DimensionType> getDimensionTypes() {
        return this.get(Registry.DIMENSION_TYPE_KEY);
    }

    private static <E> void register(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, Info<?>> infosBuilder, RegistryKey<? extends Registry<E>> registryRef, Codec<E> entryCodec) {
        infosBuilder.put(registryRef, new Info<E>(registryRef, entryCodec, null));
    }

    private static <E> void register(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, Info<?>> infosBuilder, RegistryKey<? extends Registry<E>> registryRef, Codec<E> entryCodec, Codec<E> networkEntryCodec) {
        infosBuilder.put(registryRef, new Info<E>(registryRef, entryCodec, networkEntryCodec));
    }

    /**
     * Creates a default dynamic registry manager.
     */
    public static Impl create() {
        Impl impl = new Impl();
        RegistryOps.EntryLoader.Impl impl2 = new RegistryOps.EntryLoader.Impl();
        for (Info<?> info : INFOS.values()) {
            DynamicRegistryManager.method_31141(impl, impl2, info);
        }
        RegistryOps.of(JsonOps.INSTANCE, impl2, impl);
        return impl;
    }

    private static <E> void method_31141(Impl impl, RegistryOps.EntryLoader.Impl impl2, Info<E> info) {
        RegistryKey<Registry<E>> registryKey = info.getRegistry();
        boolean bl = !registryKey.equals(Registry.NOISE_SETTINGS_WORLDGEN) && !registryKey.equals(Registry.DIMENSION_TYPE_KEY);
        MutableRegistry<E> registry = BUILTIN.get(registryKey);
        MutableRegistry<E> mutableRegistry = impl.get(registryKey);
        for (Map.Entry entry : registry.getEntries()) {
            Object object = entry.getValue();
            if (bl) {
                impl2.add(BUILTIN, entry.getKey(), info.getEntryCodec(), registry.getRawId(object), object, registry.getEntryLifecycle(object));
                continue;
            }
            mutableRegistry.set(registry.getRawId(object), entry.getKey(), object, registry.getEntryLifecycle(object));
        }
    }

    /**
     * Add all entries of the registry referred by {@code registryRef} to the
     * corresponding registry within this manager.
     */
    private static <R extends Registry<?>> void copyFromBuiltin(Impl manager, RegistryKey<R> registryRef) {
        Registry<Registry<?>> registry = BuiltinRegistries.REGISTRIES;
        Registry<?> registry2 = registry.get(registryRef);
        if (registry2 == null) {
            throw new IllegalStateException("Missing builtin registry: " + registryRef);
        }
        DynamicRegistryManager.addBuiltinEntries(manager, registry2);
    }

    /**
     * Add all entries of the {@code registry} to the corresponding registry
     * within this manager.
     */
    private static <E> void addBuiltinEntries(Impl manager, Registry<E> registry) {
        MutableRegistry<E> mutableRegistry = manager.getOptionalMutable(registry.getKey()).orElseThrow(() -> new IllegalStateException("Missing registry: " + registry.getKey()));
        for (Map.Entry<RegistryKey<E>, E> entry : registry.getEntries()) {
            E object = entry.getValue();
            mutableRegistry.set(registry.getRawId(object), entry.getKey(), object, registry.getEntryLifecycle(object));
        }
    }

    /**
     * Loads a dynamic registry manager from the resource manager's data files.
     */
    public static void load(Impl impl, RegistryOps<?> registryOps) {
        for (Info<?> info : INFOS.values()) {
            DynamicRegistryManager.load(registryOps, impl, info);
        }
    }

    /**
     * Loads elements from the {@code ops} into the registry specified by {@code
     * info} within the {@code manager}. Note that the resource manager instance
     * is kept within the {@code ops}.
     */
    private static <E> void load(RegistryOps<?> ops, Impl manager, Info<E> info) {
        RegistryKey registryKey = info.getRegistry();
        SimpleRegistry simpleRegistry2 = Optional.ofNullable(manager.registries.get(registryKey)).map(simpleRegistry -> simpleRegistry).orElseThrow(() -> new IllegalStateException("Missing registry: " + registryKey));
        DataResult<SimpleRegistry<E>> dataResult = ops.loadToRegistry(simpleRegistry2, info.getRegistry(), info.getEntryCodec());
        dataResult.error().ifPresent(partialResult -> LOGGER.error("Error loading registry data: {}", (Object)partialResult.message()));
    }

    public static final class Impl
    extends DynamicRegistryManager {
        public static final Codec<Impl> CODEC = Impl.setupCodec();
        private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> registries;

        private static <E> Codec<Impl> setupCodec() {
            Codec<RegistryKey> codec = Identifier.CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);
            Codec<SimpleRegistry> codec2 = codec.partialDispatch("type", simpleRegistry -> DataResult.success(simpleRegistry.getKey()), registryKey -> Impl.getDataResultForCodec(registryKey).map(codec -> SimpleRegistry.createRegistryManagerCodec(registryKey, Lifecycle.experimental(), codec)));
            UnboundedMapCodec<RegistryKey, SimpleRegistry> unboundedMapCodec = Codec.unboundedMap(codec, codec2);
            return Impl.fromRegistryCodecs(unboundedMapCodec);
        }

        private static <K extends RegistryKey<? extends Registry<?>>, V extends SimpleRegistry<?>> Codec<Impl> fromRegistryCodecs(UnboundedMapCodec<K, V> unboundedMapCodec) {
            return unboundedMapCodec.xmap(Impl::new, impl -> impl.registries.entrySet().stream().filter(entry -> ((Info)INFOS.get(entry.getKey())).isSynced()).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        private static <E> DataResult<? extends Codec<E>> getDataResultForCodec(RegistryKey<? extends Registry<E>> registryRef) {
            return Optional.ofNullable(INFOS.get(registryRef)).map(info -> info.getNetworkEntryCodec()).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown or not serializable registry: " + registryRef));
        }

        public Impl() {
            this(INFOS.keySet().stream().collect(Collectors.toMap(Function.identity(), Impl::createRegistry)));
        }

        private Impl(Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> registries) {
            this.registries = registries;
        }

        private static <E> SimpleRegistry<?> createRegistry(RegistryKey<? extends Registry<?>> registryRef) {
            return new SimpleRegistry(registryRef, Lifecycle.stable());
        }

        @Override
        public <E> Optional<MutableRegistry<E>> getOptionalMutable(RegistryKey<? extends Registry<E>> key) {
            return Optional.ofNullable(this.registries.get(key)).map(simpleRegistry -> simpleRegistry);
        }
    }

    static final class Info<E> {
        private final RegistryKey<? extends Registry<E>> registry;
        private final Codec<E> entryCodec;
        @Nullable
        private final Codec<E> networkEntryCodec;

        public Info(RegistryKey<? extends Registry<E>> registry, Codec<E> entryCodec, @Nullable Codec<E> networkEntryCodec) {
            this.registry = registry;
            this.entryCodec = entryCodec;
            this.networkEntryCodec = networkEntryCodec;
        }

        public RegistryKey<? extends Registry<E>> getRegistry() {
            return this.registry;
        }

        public Codec<E> getEntryCodec() {
            return this.entryCodec;
        }

        @Nullable
        public Codec<E> getNetworkEntryCodec() {
            return this.networkEntryCodec;
        }

        public boolean isSynced() {
            return this.networkEntryCodec != null;
        }
    }
}

