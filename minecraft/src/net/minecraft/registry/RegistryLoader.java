package net.minecraft.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.network.message.MessageType;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.structure.Structure;
import org.slf4j.Logger;

public class RegistryLoader {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final List<RegistryLoader.Entry<?>> DYNAMIC_REGISTRIES = List.of(
		new RegistryLoader.Entry<>(RegistryKeys.DIMENSION_TYPE, DimensionType.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.BIOME, Biome.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.MESSAGE_TYPE, MessageType.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.CONFIGURED_CARVER, ConfiguredCarver.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.CONFIGURED_FEATURE, ConfiguredFeature.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.PLACED_FEATURE, PlacedFeature.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.STRUCTURE, Structure.STRUCTURE_CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.STRUCTURE_SET, StructureSet.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.PROCESSOR_LIST, StructureProcessorType.PROCESSORS_CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.TEMPLATE_POOL, StructurePool.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.CHUNK_GENERATOR_SETTINGS, ChunkGeneratorSettings.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.NOISE_PARAMETERS, DoublePerlinNoiseSampler.NoiseParameters.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.DENSITY_FUNCTION, DensityFunction.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.WORLD_PRESET, WorldPreset.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.TRIM_PATTERN, ArmorTrimPattern.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterial.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.DAMAGE_TYPE, DamageType.CODEC),
		new RegistryLoader.Entry<>(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.CODEC)
	);
	public static final List<RegistryLoader.Entry<?>> DIMENSION_REGISTRIES = List.of(new RegistryLoader.Entry<>(RegistryKeys.DIMENSION, DimensionOptions.CODEC));

	public static DynamicRegistryManager.Immutable load(
		ResourceManager resourceManager, DynamicRegistryManager baseRegistryManager, List<RegistryLoader.Entry<?>> entries
	) {
		Map<RegistryKey<?>, Exception> map = new HashMap();
		List<Pair<MutableRegistry<?>, RegistryLoader.RegistryLoadable>> list = entries.stream().map(entry -> entry.getLoader(Lifecycle.stable(), map)).toList();
		RegistryOps.RegistryInfoGetter registryInfoGetter = createInfoGetter(baseRegistryManager, list);
		list.forEach(loader -> ((RegistryLoader.RegistryLoadable)loader.getSecond()).load(resourceManager, registryInfoGetter));
		list.forEach(loader -> {
			Registry<?> registry = (Registry<?>)loader.getFirst();

			try {
				registry.freeze();
			} catch (Exception var4x) {
				map.put(registry.getKey(), var4x);
			}
		});
		if (!map.isEmpty()) {
			writeLoadingError(map);
			throw new IllegalStateException("Failed to load registries due to above errors");
		} else {
			return new DynamicRegistryManager.ImmutableImpl(list.stream().map(Pair::getFirst).toList()).toImmutable();
		}
	}

	private static RegistryOps.RegistryInfoGetter createInfoGetter(
		DynamicRegistryManager baseRegistryManager, List<Pair<MutableRegistry<?>, RegistryLoader.RegistryLoadable>> additionalRegistries
	) {
		final Map<RegistryKey<? extends Registry<?>>, RegistryOps.RegistryInfo<?>> map = new HashMap();
		baseRegistryManager.streamAllRegistries().forEach(entry -> map.put(entry.key(), createInfo(entry.value())));
		additionalRegistries.forEach(pair -> map.put(((MutableRegistry)pair.getFirst()).getKey(), createInfo((MutableRegistry)pair.getFirst())));
		return new RegistryOps.RegistryInfoGetter() {
			@Override
			public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef) {
				return Optional.ofNullable((RegistryOps.RegistryInfo)map.get(registryRef));
			}
		};
	}

	private static <T> RegistryOps.RegistryInfo<T> createInfo(MutableRegistry<T> registry) {
		return new RegistryOps.RegistryInfo<>(registry.getReadOnlyWrapper(), registry.createMutableEntryLookup(), registry.getLifecycle());
	}

	private static <T> RegistryOps.RegistryInfo<T> createInfo(Registry<T> registry) {
		return new RegistryOps.RegistryInfo<>(registry.getReadOnlyWrapper(), registry.getTagCreatingWrapper(), registry.getLifecycle());
	}

	private static void writeLoadingError(Map<RegistryKey<?>, Exception> exceptions) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		Map<Identifier, Map<Identifier, Exception>> map = (Map<Identifier, Map<Identifier, Exception>>)exceptions.entrySet()
			.stream()
			.collect(
				Collectors.groupingBy(
					entry -> ((RegistryKey)entry.getKey()).getRegistry(), Collectors.toMap(entry -> ((RegistryKey)entry.getKey()).getValue(), java.util.Map.Entry::getValue)
				)
			);
		map.entrySet().stream().sorted(java.util.Map.Entry.comparingByKey()).forEach(entry -> {
			printWriter.printf("> Errors in registry %s:%n", entry.getKey());
			((Map)entry.getValue()).entrySet().stream().sorted(java.util.Map.Entry.comparingByKey()).forEach(elementEntry -> {
				printWriter.printf(">> Errors in element %s:%n", elementEntry.getKey());
				((Exception)elementEntry.getValue()).printStackTrace(printWriter);
			});
		});
		printWriter.flush();
		LOGGER.error("Registry loading errors:\n{}", stringWriter);
	}

	private static String getPath(Identifier id) {
		return id.getPath();
	}

	static <E> void load(
		RegistryOps.RegistryInfoGetter registryInfoGetter,
		ResourceManager resourceManager,
		RegistryKey<? extends Registry<E>> registryRef,
		MutableRegistry<E> newRegistry,
		Decoder<E> decoder,
		Map<RegistryKey<?>, Exception> exceptions
	) {
		String string = getPath(registryRef.getValue());
		ResourceFinder resourceFinder = ResourceFinder.json(string);
		RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, registryInfoGetter);

		for (java.util.Map.Entry<Identifier, Resource> entry : resourceFinder.findResources(resourceManager).entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			RegistryKey<E> registryKey = RegistryKey.of(registryRef, resourceFinder.toResourceId(identifier));
			Resource resource = (Resource)entry.getValue();

			try {
				Reader reader = resource.getReader();

				try {
					JsonElement jsonElement = JsonParser.parseReader(reader);
					DataResult<E> dataResult = decoder.parse(registryOps, jsonElement);
					E object = dataResult.getOrThrow(false, error -> {
					});
					newRegistry.add(registryKey, object, resource.isAlwaysStable() ? Lifecycle.stable() : dataResult.lifecycle());
				} catch (Throwable var19) {
					if (reader != null) {
						try {
							reader.close();
						} catch (Throwable var18) {
							var19.addSuppressed(var18);
						}
					}

					throw var19;
				}

				if (reader != null) {
					reader.close();
				}
			} catch (Exception var20) {
				exceptions.put(
					registryKey, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse %s from pack %s", identifier, resource.getResourcePackName()), var20)
				);
			}
		}
	}

	public static record Entry<T>(RegistryKey<? extends Registry<T>> key, Codec<T> elementCodec) {
		Pair<MutableRegistry<?>, RegistryLoader.RegistryLoadable> getLoader(Lifecycle lifecycle, Map<RegistryKey<?>, Exception> exceptions) {
			MutableRegistry<T> mutableRegistry = new SimpleRegistry<>(this.key, lifecycle);
			RegistryLoader.RegistryLoadable registryLoadable = (resourceManager, registryInfoGetter) -> RegistryLoader.load(
					registryInfoGetter, resourceManager, this.key, mutableRegistry, this.elementCodec, exceptions
				);
			return Pair.of(mutableRegistry, registryLoadable);
		}
	}

	interface RegistryLoadable {
		void load(ResourceManager resourceManager, RegistryOps.RegistryInfoGetter registryInfoGetter);
	}
}
