/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.io.BufferedReader;
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
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
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
    public static final List<Entry<?>> DYNAMIC_REGISTRIES = List.of(new Entry<DimensionType>(Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC), new Entry<Biome>(Registry.BIOME_KEY, Biome.CODEC), new Entry<MessageType>(Registry.MESSAGE_TYPE_KEY, MessageType.CODEC), new Entry(Registry.CONFIGURED_CARVER_KEY, ConfiguredCarver.CODEC), new Entry(Registry.CONFIGURED_FEATURE_KEY, ConfiguredFeature.CODEC), new Entry<PlacedFeature>(Registry.PLACED_FEATURE_KEY, PlacedFeature.CODEC), new Entry<Structure>(Registry.STRUCTURE_KEY, Structure.STRUCTURE_CODEC), new Entry<StructureSet>(Registry.STRUCTURE_SET_KEY, StructureSet.CODEC), new Entry<StructureProcessorList>(Registry.STRUCTURE_PROCESSOR_LIST_KEY, StructureProcessorType.PROCESSORS_CODEC), new Entry<StructurePool>(Registry.STRUCTURE_POOL_KEY, StructurePool.CODEC), new Entry<ChunkGeneratorSettings>(Registry.CHUNK_GENERATOR_SETTINGS_KEY, ChunkGeneratorSettings.CODEC), new Entry<DoublePerlinNoiseSampler.NoiseParameters>(Registry.NOISE_KEY, DoublePerlinNoiseSampler.NoiseParameters.CODEC), new Entry<DensityFunction>(Registry.DENSITY_FUNCTION_KEY, DensityFunction.CODEC), new Entry<WorldPreset>(Registry.WORLD_PRESET_KEY, WorldPreset.CODEC), new Entry<FlatLevelGeneratorPreset>(Registry.FLAT_LEVEL_GENERATOR_PRESET_KEY, FlatLevelGeneratorPreset.CODEC));
    public static final List<Entry<?>> DIMENSION_REGISTRIES = List.of(new Entry<DimensionOptions>(Registry.DIMENSION_KEY, DimensionOptions.CODEC));

    public static DynamicRegistryManager.Immutable load(ResourceManager resourceManager, DynamicRegistryManager baseRegistryManager, List<Entry<?>> entries) {
        HashMap map = new HashMap();
        List<Pair> list = entries.stream().map(entry -> entry.getLoader(Lifecycle.stable(), map)).toList();
        DynamicRegistryManager.ImmutableImpl dynamicRegistryManager = new DynamicRegistryManager.ImmutableImpl(list.stream().map(Pair::getFirst).toList());
        DynamicRegistryManager.ImmutableImpl dynamicRegistryManager2 = new DynamicRegistryManager.ImmutableImpl(Stream.concat(baseRegistryManager.streamAllRegistries(), dynamicRegistryManager.streamAllRegistries()));
        list.forEach(loader -> ((RegistryLoadable)loader.getSecond()).load(resourceManager, dynamicRegistryManager2));
        list.forEach(loader -> {
            Registry registry = (Registry)loader.getFirst();
            try {
                registry.freeze();
            } catch (Exception exception) {
                map.put(registry.getKey(), exception);
            }
        });
        if (!map.isEmpty()) {
            RegistryLoader.writeLoadingError(map);
            throw new IllegalStateException("Failed to load registries due to above errors");
        }
        return dynamicRegistryManager.toImmutable();
    }

    private static void writeLoadingError(Map<RegistryKey<?>, Exception> exceptions) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        Map<Identifier, Map<Identifier, Exception>> map = exceptions.entrySet().stream().collect(Collectors.groupingBy(entry -> ((RegistryKey)entry.getKey()).getRegistry(), Collectors.toMap(entry -> ((RegistryKey)entry.getKey()).getValue(), Map.Entry::getValue)));
        map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            printWriter.printf("> Errors in registry %s:%n", entry.getKey());
            ((Map)entry.getValue()).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(elementEntry -> {
                printWriter.printf(">> Errors in element %s:%n", elementEntry.getKey());
                ((Exception)elementEntry.getValue()).printStackTrace(printWriter);
            });
        });
        printWriter.flush();
        LOGGER.error("Registry loading errors:\n{}", (Object)stringWriter);
    }

    private static String getPath(Identifier id) {
        return id.getPath();
    }

    static <E> void load(DynamicRegistryManager registryManager, ResourceManager resourceManager, RegistryKey<? extends Registry<E>> registryRef, MutableRegistry<E> newRegistry, Decoder<E> decoder, Map<RegistryKey<?>, Exception> exceptions) {
        String string = RegistryLoader.getPath(registryRef.getValue());
        ResourceFinder resourceFinder = ResourceFinder.json(string);
        RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, registryManager);
        for (Map.Entry<Identifier, Resource> entry : resourceFinder.findResources(resourceManager).entrySet()) {
            Identifier identifier = entry.getKey();
            RegistryKey registryKey = RegistryKey.of(registryRef, resourceFinder.toResourceId(identifier));
            Resource resource = entry.getValue();
            try {
                BufferedReader reader = resource.getReader();
                try {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    DataResult<E> dataResult = decoder.parse(registryOps, jsonElement);
                    E object = dataResult.getOrThrow(false, error -> {});
                    newRegistry.add(registryKey, object, resource.isAlwaysStable() ? Lifecycle.stable() : dataResult.lifecycle());
                } finally {
                    if (reader == null) continue;
                    ((Reader)reader).close();
                }
            } catch (Exception exception) {
                exceptions.put(registryKey, new IllegalStateException("Failed to parse %s from pack %s".formatted(identifier, resource.getResourcePackName()), exception));
            }
        }
    }

    static interface RegistryLoadable {
        public void load(ResourceManager var1, DynamicRegistryManager var2);
    }

    public record Entry<T>(RegistryKey<? extends Registry<T>> key, Codec<T> elementCodec) {
        public Pair<Registry<?>, RegistryLoadable> getLoader(Lifecycle lifecycle, Map<RegistryKey<?>, Exception> exceptions) {
            SimpleRegistry mutableRegistry = new SimpleRegistry(this.key, lifecycle);
            RegistryLoadable registryLoadable = (resourceManager, registryManager) -> RegistryLoader.load(registryManager, resourceManager, this.key, mutableRegistry, this.elementCodec, exceptions);
            return Pair.of(mutableRegistry, registryLoadable);
        }
    }
}

