package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.noise.BuiltinNoiseParameters;
import org.slf4j.Logger;

/**
 * Stores a few hardcoded registries with builtin values for datapack-loadable registries,
 * from which a registry tracker can create a new dynamic registry.
 * 
 * <p>Note that these registries do not contain the actual entries that the server has,
 * for that you will need to access it from {@link net.minecraft.util.registry.DynamicRegistryManager}.
 * 
 * @see net.minecraft.server.MinecraftServer#getRegistryManager()
 * @see net.minecraft.client.network.ClientPlayNetworkHandler#getRegistryManager()
 * @see net.minecraft.util.registry.DynamicRegistryManager#get(RegistryKey)
 */
public class BuiltinRegistries {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Map<Identifier, Supplier<?>> DEFAULT_VALUE_SUPPLIERS = Maps.<Identifier, Supplier<?>>newLinkedHashMap();
	private static final MutableRegistry<MutableRegistry<?>> ROOT = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier("root")), Lifecycle.experimental());
	public static final Registry<? extends Registry<?>> REGISTRIES = ROOT;
	public static final Registry<ConfiguredCarver<?>> CONFIGURED_CARVER = addRegistry(Registry.CONFIGURED_CARVER_KEY, () -> ConfiguredCarvers.CAVE);
	public static final Registry<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = addRegistry(
		Registry.CONFIGURED_FEATURE_KEY, ConfiguredFeatures::getDefaultConfiguredFeature
	);
	public static final Registry<ConfiguredStructureFeature<?, ?>> CONFIGURED_STRUCTURE_FEATURE = addRegistry(
		Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, ConfiguredStructureFeatures::getDefault
	);
	public static final Registry<PlacedFeature> PLACED_FEATURE = addRegistry(Registry.PLACED_FEATURE_KEY, PlacedFeatures::getDefaultPlacedFeature);
	public static final Registry<StructureProcessorList> STRUCTURE_PROCESSOR_LIST = addRegistry(
		Registry.STRUCTURE_PROCESSOR_LIST_KEY, () -> StructureProcessorLists.ZOMBIE_PLAINS
	);
	public static final Registry<StructurePool> STRUCTURE_POOL = addRegistry(Registry.STRUCTURE_POOL_KEY, StructurePools::initDefaultPools);
	public static final Registry<Biome> BIOME = addRegistry(Registry.BIOME_KEY, () -> BuiltinBiomes.PLAINS);
	public static final Registry<ChunkGeneratorSettings> CHUNK_GENERATOR_SETTINGS = addRegistry(
		Registry.CHUNK_GENERATOR_SETTINGS_KEY, ChunkGeneratorSettings::getInstance
	);
	public static final Registry<DoublePerlinNoiseSampler.NoiseParameters> NOISE_PARAMETERS = addRegistry(Registry.NOISE_WORLDGEN, BuiltinNoiseParameters::init);

	private static <T> Registry<T> addRegistry(RegistryKey<? extends Registry<T>> registryRef, Supplier<T> defaultValueSupplier) {
		return addRegistry(registryRef, Lifecycle.stable(), defaultValueSupplier);
	}

	private static <T> Registry<T> addRegistry(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, Supplier<T> defaultValueSupplier) {
		return addRegistry(registryRef, new SimpleRegistry<>(registryRef, lifecycle), defaultValueSupplier, lifecycle);
	}

	private static <T, R extends MutableRegistry<T>> R addRegistry(
		RegistryKey<? extends Registry<T>> registryRef, R registry, Supplier<T> defaultValueSupplier, Lifecycle lifecycle
	) {
		Identifier identifier = registryRef.getValue();
		DEFAULT_VALUE_SUPPLIERS.put(identifier, defaultValueSupplier);
		MutableRegistry<R> mutableRegistry = ROOT;
		return mutableRegistry.add((RegistryKey<R>)registryRef, registry, lifecycle);
	}

	public static <T> T add(Registry<? super T> registry, String id, T object) {
		return add(registry, new Identifier(id), object);
	}

	public static <V, T extends V> T add(Registry<V> registry, Identifier id, T object) {
		return add(registry, RegistryKey.of(registry.getKey(), id), object);
	}

	public static <V, T extends V> T add(Registry<V> registry, RegistryKey<V> key, T object) {
		return ((MutableRegistry)registry).add(key, object, Lifecycle.stable());
	}

	public static <V, T extends V> T set(Registry<V> registry, RegistryKey<V> key, T object) {
		return ((MutableRegistry)registry).add(key, object, Lifecycle.stable());
	}

	public static void init() {
	}

	static {
		DEFAULT_VALUE_SUPPLIERS.forEach((id, supplier) -> {
			if (supplier.get() == null) {
				LOGGER.error("Unable to bootstrap registry '{}'", id);
			}
		});
		Registry.validate(ROOT);
	}
}
