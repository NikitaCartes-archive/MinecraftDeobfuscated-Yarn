package net.minecraft.util.registry;

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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.network.message.MessageType;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
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
		new RegistryLoader.Entry<>(Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC),
		new RegistryLoader.Entry<>(Registry.BIOME_KEY, Biome.CODEC),
		new RegistryLoader.Entry<>(Registry.MESSAGE_TYPE_KEY, MessageType.CODEC),
		new RegistryLoader.Entry<>(Registry.CONFIGURED_CARVER_KEY, ConfiguredCarver.CODEC),
		new RegistryLoader.Entry<>(Registry.CONFIGURED_FEATURE_KEY, ConfiguredFeature.CODEC),
		new RegistryLoader.Entry<>(Registry.PLACED_FEATURE_KEY, PlacedFeature.CODEC),
		new RegistryLoader.Entry<>(Registry.STRUCTURE_KEY, Structure.STRUCTURE_CODEC),
		new RegistryLoader.Entry<>(Registry.STRUCTURE_SET_KEY, StructureSet.CODEC),
		new RegistryLoader.Entry<>(Registry.STRUCTURE_PROCESSOR_LIST_KEY, StructureProcessorType.PROCESSORS_CODEC),
		new RegistryLoader.Entry<>(Registry.STRUCTURE_POOL_KEY, StructurePool.CODEC),
		new RegistryLoader.Entry<>(Registry.CHUNK_GENERATOR_SETTINGS_KEY, ChunkGeneratorSettings.CODEC),
		new RegistryLoader.Entry<>(Registry.NOISE_KEY, DoublePerlinNoiseSampler.NoiseParameters.CODEC),
		new RegistryLoader.Entry<>(Registry.DENSITY_FUNCTION_KEY, DensityFunction.CODEC),
		new RegistryLoader.Entry<>(Registry.WORLD_PRESET_KEY, WorldPreset.CODEC),
		new RegistryLoader.Entry<>(Registry.FLAT_LEVEL_GENERATOR_PRESET_KEY, FlatLevelGeneratorPreset.CODEC)
	);
	public static final List<RegistryLoader.Entry<?>> DIMENSION_REGISTRIES = List.of(new RegistryLoader.Entry<>(Registry.DIMENSION_KEY, DimensionOptions.CODEC));

	public static DynamicRegistryManager.Immutable load(
		ResourceManager resourceManager, DynamicRegistryManager baseRegistryManager, List<RegistryLoader.Entry<?>> entries
	) {
		Map<RegistryKey<?>, Exception> map = new HashMap();
		List<Pair<Registry<?>, RegistryLoader.RegistryLoadable>> list = entries.stream().map(entry -> entry.getLoader(Lifecycle.stable(), map)).toList();
		DynamicRegistryManager dynamicRegistryManager = new DynamicRegistryManager.ImmutableImpl(list.stream().map(Pair::getFirst).toList());
		DynamicRegistryManager dynamicRegistryManager2 = new DynamicRegistryManager.ImmutableImpl(
			Stream.concat(baseRegistryManager.streamAllRegistries(), dynamicRegistryManager.streamAllRegistries())
		);
		list.forEach(loader -> ((RegistryLoader.RegistryLoadable)loader.getSecond()).load(resourceManager, dynamicRegistryManager2));
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
			return dynamicRegistryManager.toImmutable();
		}
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
		DynamicRegistryManager registryManager,
		ResourceManager resourceManager,
		RegistryKey<? extends Registry<E>> registryRef,
		MutableRegistry<E> newRegistry,
		Decoder<E> decoder,
		Map<RegistryKey<?>, Exception> exceptions
	) {
		String string = getPath(registryRef.getValue());
		ResourceFinder resourceFinder = ResourceFinder.json(string);
		RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, registryManager);

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
				exceptions.put(registryKey, new IllegalStateException("Failed to parse %s from pack %s".formatted(identifier, resource.getResourcePackName()), var20));
			}
		}
	}

	public static record Entry<T>(RegistryKey<? extends Registry<T>> key, Codec<T> elementCodec) {
		public Pair<Registry<?>, RegistryLoader.RegistryLoadable> getLoader(Lifecycle lifecycle, Map<RegistryKey<?>, Exception> exceptions) {
			MutableRegistry<T> mutableRegistry = new SimpleRegistry<>(this.key, lifecycle);
			RegistryLoader.RegistryLoadable registryLoadable = (resourceManager, registryManager) -> RegistryLoader.load(
					registryManager, resourceManager, this.key, mutableRegistry, this.elementCodec, exceptions
				);
			return Pair.of(mutableRegistry, registryLoadable);
		}
	}

	interface RegistryLoadable {
		void load(ResourceManager resourceManager, DynamicRegistryManager registryManager);
	}
}
