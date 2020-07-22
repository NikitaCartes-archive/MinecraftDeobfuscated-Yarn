package net.minecraft.util.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.TemplatePools;
import net.minecraft.structure.processor.ProcessorLists;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Stores a few hardcoded registries with builtin values for datapack-loadable registries,
 * from which a registry tracker can create a new dynamic registry.
 */
public class BuiltinRegistries {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Identifier, Supplier<?>> DEFAULT_VALUE_SUPPLIERS = Maps.<Identifier, Supplier<?>>newLinkedHashMap();
	private static final MutableRegistry<MutableRegistry<?>> ROOT = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier("root")), Lifecycle.experimental());
	public static final Registry<? extends Registry<?>> REGISTRIES = ROOT;
	public static final Registry<ConfiguredSurfaceBuilder<?>> CONFIGURED_SURFACE_BUILDER = addRegistry(
		Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN, () -> ConfiguredSurfaceBuilders.NOPE
	);
	public static final Registry<ConfiguredCarver<?>> CONFIGURED_CARVER = addRegistry(Registry.CONFIGURED_CARVER_WORLDGEN, () -> ConfiguredCarvers.CAVE);
	public static final Registry<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = addRegistry(Registry.CONFIGURED_FEATURE_WORLDGEN, () -> ConfiguredFeatures.OAK);
	public static final Registry<ConfiguredStructureFeature<?, ?>> CONFIGURED_STRUCTURE_FEATURE = addRegistry(
		Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, () -> ConfiguredStructureFeatures.MINESHAFT
	);
	public static final Registry<ImmutableList<StructureProcessor>> PROCESSOR_LIST = addRegistry(
		Registry.PROCESSOR_LIST_WORLDGEN, () -> ProcessorLists.ZOMBIE_PLAINS
	);
	public static final Registry<StructurePool> TEMPLATE_POOL = addRegistry(Registry.TEMPLATE_POOL_WORLDGEN, () -> TemplatePools.EMPTY);
	public static final Registry<Biome> BIOME = addRegistry(Registry.BIOME_KEY, () -> Biomes.DEFAULT);
	public static final Registry<ChunkGeneratorType> field_26375 = addRegistry(Registry.NOISE_SETTINGS_WORLDGEN, () -> ChunkGeneratorType.field_26355);

	private static <T> Registry<T> addRegistry(RegistryKey<? extends Registry<T>> registryRef, Supplier<T> defaultValueSupplier) {
		return addRegistry(registryRef, Lifecycle.experimental(), defaultValueSupplier);
	}

	private static <T> Registry<T> addRegistry(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, Supplier<T> defaultValueSupplier) {
		return addRegistry(registryRef, new SimpleRegistry<>(registryRef, lifecycle), defaultValueSupplier);
	}

	private static <T, R extends MutableRegistry<T>> R addRegistry(RegistryKey<? extends Registry<T>> registryRef, R registry, Supplier<T> defaultValueSupplier) {
		Identifier identifier = registryRef.getValue();
		DEFAULT_VALUE_SUPPLIERS.put(identifier, defaultValueSupplier);
		MutableRegistry<R> mutableRegistry = ROOT;
		return mutableRegistry.add((RegistryKey<R>)registryRef, registry);
	}

	public static <T> T add(Registry<? super T> registry, String id, T object) {
		return add(registry, new Identifier(id), object);
	}

	public static <V, T extends V> T add(Registry<V> registry, Identifier id, T object) {
		return ((MutableRegistry)registry).add(RegistryKey.of(registry.getKey(), id), object);
	}

	public static <V, T extends V> T set(Registry<V> registry, int rawId, String id, T object) {
		return ((MutableRegistry)registry).set(rawId, RegistryKey.of(registry.getKey(), new Identifier(id)), object);
	}

	public static void init() {
	}

	static {
		DEFAULT_VALUE_SUPPLIERS.forEach((identifier, supplier) -> {
			if (supplier.get() == null) {
				LOGGER.error("Unable to bootstrap registry '{}'", identifier);
			}
		});
		Registry.validate(ROOT);
	}
}
