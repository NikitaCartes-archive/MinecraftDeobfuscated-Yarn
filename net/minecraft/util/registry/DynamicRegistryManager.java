/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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
 * manager for server-sent dynamic registries.</p>
 * 
 * <p>The {@link DynamicRegistryManager.Impl}
 * class serves as an immutable implementation of any particular collection
 * or configuration of dynamic registries.</p>
 */
public interface DynamicRegistryManager {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Map<RegistryKey<? extends Registry<?>>, Info<?>> INFOS = Util.make(() -> {
        ImmutableMap.Builder<RegistryKey<Registry<?>>, Info<?>> builder = ImmutableMap.builder();
        DynamicRegistryManager.method_31060(builder, Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC, DimensionType.CODEC);
        DynamicRegistryManager.method_31060(builder, Registry.BIOME_KEY, Biome.CODEC, Biome.field_26633);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN, ConfiguredSurfaceBuilder.field_25878);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_CARVER_WORLDGEN, ConfiguredCarver.field_25832);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_FEATURE_WORLDGEN, ConfiguredFeature.field_25833);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, ConfiguredStructureFeature.CODEC);
        DynamicRegistryManager.register(builder, Registry.PROCESSOR_LIST_WORLDGEN, StructureProcessorType.field_25876);
        DynamicRegistryManager.register(builder, Registry.TEMPLATE_POOL_WORLDGEN, StructurePool.CODEC);
        DynamicRegistryManager.register(builder, Registry.NOISE_SETTINGS_WORLDGEN, ChunkGeneratorSettings.field_24780);
        return builder.build();
    });

    /**
     * Retrieves a registry optionally from this manager.
     */
    public <E> Optional<MutableRegistry<E>> getOptional(RegistryKey<? extends Registry<E>> var1);

    /**
     * Retrieves a registry from this manager, or throws an exception when the
     * registry does not exist.
     * 
     * @throws IllegalStateException if the registry does not exist
     */
    default public <E> MutableRegistry<E> get(RegistryKey<? extends Registry<E>> key) {
        return this.getOptional(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
    }

    default public Registry<DimensionType> getDimensionTypes() {
        return this.get(Registry.DIMENSION_TYPE_KEY);
    }

    public static <E> void register(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, Info<?>> infosBuilder, RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec) {
        infosBuilder.put(registryRef, new Info<E>(registryRef, codec, null));
    }

    public static <E> void method_31060(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, Info<?>> builder, RegistryKey<? extends Registry<E>> registryKey, Codec<E> codec, Codec<E> codec2) {
        builder.put(registryKey, new Info<E>(registryKey, codec, codec2));
    }

    /**
     * Creates a default dynamic registry manager.
     */
    public static Impl create() {
        Impl impl = new Impl();
        DimensionType.addRegistryDefaults(impl);
        INFOS.keySet().stream().filter(registryKey -> !registryKey.equals(Registry.DIMENSION_TYPE_KEY)).forEach(registryKey -> DynamicRegistryManager.setupBuiltin(impl, registryKey));
        return impl;
    }

    /**
     * Add all entries of the registry referred by {@code registryRef} to the
     * corresponding registry within this manager.
     */
    public static <R extends Registry<?>> void setupBuiltin(Impl manager, RegistryKey<R> registryRef) {
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
    public static <E> void addBuiltinEntries(Impl manager, Registry<E> registry) {
        MutableRegistry<E> mutableRegistry = manager.getOptional(registry.getKey()).orElseThrow(() -> new IllegalStateException("Missing registry: " + registry.getKey()));
        for (Map.Entry<RegistryKey<E>, E> entry : registry.getEntries()) {
            mutableRegistry.set(registry.getRawId(entry.getValue()), entry.getKey(), entry.getValue());
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
    public static <E> void load(RegistryOps<?> ops, Impl manager, Info<E> info) {
        RegistryKey registryKey = info.getRegistry();
        SimpleRegistry simpleRegistry2 = Optional.ofNullable(manager.registries.get(registryKey)).map(simpleRegistry -> simpleRegistry).orElseThrow(() -> new IllegalStateException("Missing registry: " + registryKey));
        DataResult<SimpleRegistry<E>> dataResult = ops.loadToRegistry(simpleRegistry2, info.getRegistry(), info.getElementCodec());
        dataResult.error().ifPresent(partialResult -> LOGGER.error("Error loading registry data: {}", (Object)partialResult.message()));
    }

    public static final class Impl
    implements DynamicRegistryManager {
        public static final Codec<Impl> CODEC = Impl.setupCodec();
        private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> registries;

        private static <E> Codec<Impl> setupCodec() {
            Codec<RegistryKey> codec = Identifier.CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);
            Codec<SimpleRegistry> codec2 = codec.partialDispatch("type", simpleRegistry -> DataResult.success(simpleRegistry.getKey()), registryKey -> Impl.getDataResultForCodec(registryKey).map(codec -> SimpleRegistry.method_29098(registryKey, Lifecycle.experimental(), codec)));
            UnboundedMapCodec<RegistryKey, SimpleRegistry> unboundedMapCodec = Codec.unboundedMap(codec, codec2);
            return Impl.fromRegistryCodecs(unboundedMapCodec);
        }

        private static <K extends RegistryKey<? extends Registry<?>>, V extends SimpleRegistry<?>> Codec<Impl> fromRegistryCodecs(UnboundedMapCodec<K, V> unboundedMapCodec) {
            return unboundedMapCodec.xmap(Impl::new, impl -> impl.registries.entrySet().stream().filter(entry -> ((Info)INFOS.get(entry.getKey())).isSynced()).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        private static <E> DataResult<? extends Codec<E>> getDataResultForCodec(RegistryKey<? extends Registry<E>> registryRef) {
            return Optional.ofNullable(INFOS.get(registryRef)).map(info -> info.method_31061()).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown or not serializable registry: " + registryRef));
        }

        public Impl() {
            this(INFOS.keySet().stream().collect(Collectors.toMap(Function.identity(), Impl::createRegistry)));
        }

        private Impl(Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> registries) {
            this.registries = registries;
        }

        private static <E> SimpleRegistry<?> createRegistry(RegistryKey<? extends Registry<?>> registryRef) {
            return new SimpleRegistry(registryRef, Lifecycle.experimental());
        }

        @Override
        public <E> Optional<MutableRegistry<E>> getOptional(RegistryKey<? extends Registry<E>> key) {
            return Optional.ofNullable(this.registries.get(key)).map(simpleRegistry -> simpleRegistry);
        }
    }

    public static final class Info<E> {
        private final RegistryKey<? extends Registry<E>> registry;
        private final Codec<E> elementCodec;
        @Nullable
        private final Codec<E> field_26687;

        public Info(RegistryKey<? extends Registry<E>> registry, Codec<E> codec, @Nullable Codec<E> codec2) {
            this.registry = registry;
            this.elementCodec = codec;
            this.field_26687 = codec2;
        }

        public RegistryKey<? extends Registry<E>> getRegistry() {
            return this.registry;
        }

        public Codec<E> getElementCodec() {
            return this.elementCodec;
        }

        @Nullable
        public Codec<E> method_31061() {
            return this.field_26687;
        }

        public boolean isSynced() {
            return this.field_26687 != null;
        }
    }
}

