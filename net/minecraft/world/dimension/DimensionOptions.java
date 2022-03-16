/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public final class DimensionOptions {
    public static final Codec<DimensionOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)DimensionType.REGISTRY_CODEC.fieldOf("type")).forGetter(DimensionOptions::getDimensionTypeEntry), ((MapCodec)ChunkGenerator.CODEC.fieldOf("generator")).forGetter(DimensionOptions::getChunkGenerator)).apply((Applicative<DimensionOptions, ?>)instance, instance.stable(DimensionOptions::new)));
    public static final RegistryKey<DimensionOptions> OVERWORLD = RegistryKey.of(Registry.DIMENSION_KEY, new Identifier("overworld"));
    public static final RegistryKey<DimensionOptions> NETHER = RegistryKey.of(Registry.DIMENSION_KEY, new Identifier("the_nether"));
    public static final RegistryKey<DimensionOptions> END = RegistryKey.of(Registry.DIMENSION_KEY, new Identifier("the_end"));
    private static final Set<RegistryKey<DimensionOptions>> BASE_DIMENSIONS = ImmutableSet.of(OVERWORLD, NETHER, END);
    private final RegistryEntry<DimensionType> dimensionTypeEntry;
    private final ChunkGenerator chunkGenerator;

    public DimensionOptions(RegistryEntry<DimensionType> dimensionTypeEntry, ChunkGenerator chunkGenerator) {
        this.dimensionTypeEntry = dimensionTypeEntry;
        this.chunkGenerator = chunkGenerator;
    }

    public RegistryEntry<DimensionType> getDimensionTypeEntry() {
        return this.dimensionTypeEntry;
    }

    public ChunkGenerator getChunkGenerator() {
        return this.chunkGenerator;
    }

    public static Stream<RegistryKey<DimensionOptions>> streamRegistry(Stream<RegistryKey<DimensionOptions>> stream) {
        return Stream.concat(BASE_DIMENSIONS.stream(), stream.filter(registryKey -> !BASE_DIMENSIONS.contains(registryKey)));
    }

    public static Registry<DimensionOptions> createRegistry(Registry<DimensionOptions> registry) {
        SimpleRegistry<DimensionOptions> mutableRegistry = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);
        DimensionOptions.streamRegistry(registry.getKeys().stream()).forEach(registryKey -> {
            DimensionOptions dimensionOptions = (DimensionOptions)registry.get((RegistryKey<DimensionOptions>)registryKey);
            if (dimensionOptions != null) {
                mutableRegistry.add((RegistryKey<DimensionOptions>)registryKey, dimensionOptions, registry.getEntryLifecycle(dimensionOptions));
            }
        });
        return mutableRegistry;
    }

    public static boolean hasDefaultSettings(Registry<DimensionOptions> registry) {
        if (registry.size() != BASE_DIMENSIONS.size()) {
            return false;
        }
        Optional<DimensionOptions> optional = registry.getOrEmpty(OVERWORLD);
        Optional<DimensionOptions> optional2 = registry.getOrEmpty(NETHER);
        Optional<DimensionOptions> optional3 = registry.getOrEmpty(END);
        if (optional.isEmpty() || optional2.isEmpty() || optional3.isEmpty()) {
            return false;
        }
        if (!optional.get().getDimensionTypeEntry().matchesKey(DimensionTypes.OVERWORLD) && !optional.get().getDimensionTypeEntry().matchesKey(DimensionTypes.OVERWORLD_CAVES)) {
            return false;
        }
        if (!optional2.get().getDimensionTypeEntry().matchesKey(DimensionTypes.THE_NETHER)) {
            return false;
        }
        if (!optional3.get().getDimensionTypeEntry().matchesKey(DimensionTypes.THE_END)) {
            return false;
        }
        if (!(optional2.get().getChunkGenerator() instanceof NoiseChunkGenerator) || !(optional3.get().getChunkGenerator() instanceof NoiseChunkGenerator)) {
            return false;
        }
        NoiseChunkGenerator noiseChunkGenerator = (NoiseChunkGenerator)optional2.get().getChunkGenerator();
        NoiseChunkGenerator noiseChunkGenerator2 = (NoiseChunkGenerator)optional3.get().getChunkGenerator();
        if (!noiseChunkGenerator.matchesSettings(ChunkGeneratorSettings.NETHER)) {
            return false;
        }
        if (!noiseChunkGenerator2.matchesSettings(ChunkGeneratorSettings.END)) {
            return false;
        }
        if (!(noiseChunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
            return false;
        }
        MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource)noiseChunkGenerator.getBiomeSource();
        if (!multiNoiseBiomeSource.matchesInstance(MultiNoiseBiomeSource.Preset.NETHER)) {
            return false;
        }
        BiomeSource biomeSource = optional.get().getChunkGenerator().getBiomeSource();
        if (biomeSource instanceof MultiNoiseBiomeSource && !((MultiNoiseBiomeSource)biomeSource).matchesInstance(MultiNoiseBiomeSource.Preset.OVERWORLD)) {
            return false;
        }
        return noiseChunkGenerator2.getBiomeSource() instanceof TheEndBiomeSource;
    }
}

