/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

public final class DimensionOptions {
    public static final MapCodec<DimensionOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)DimensionType.field_24756.fieldOf("type")).forGetter(DimensionOptions::getDimensionTypeSupplier), ((MapCodec)ChunkGenerator.field_24746.fieldOf("generator")).forGetter(DimensionOptions::getChunkGenerator)).apply((Applicative<DimensionOptions, ?>)instance, instance.stable(DimensionOptions::new)));
    public static final RegistryKey<DimensionOptions> OVERWORLD = RegistryKey.of(Registry.DIMENSION_OPTIONS, new Identifier("overworld"));
    public static final RegistryKey<DimensionOptions> NETHER = RegistryKey.of(Registry.DIMENSION_OPTIONS, new Identifier("the_nether"));
    public static final RegistryKey<DimensionOptions> END = RegistryKey.of(Registry.DIMENSION_OPTIONS, new Identifier("the_end"));
    private static final LinkedHashSet<RegistryKey<DimensionOptions>> BASE_DIMENSIONS = Sets.newLinkedHashSet(ImmutableList.of(OVERWORLD, NETHER, END));
    private final Supplier<DimensionType> dimensionTypeSupplier;
    private final ChunkGenerator chunkGenerator;

    public DimensionOptions(Supplier<DimensionType> typeSupplier, ChunkGenerator chunkGenerator) {
        this.dimensionTypeSupplier = typeSupplier;
        this.chunkGenerator = chunkGenerator;
    }

    public Supplier<DimensionType> getDimensionTypeSupplier() {
        return this.dimensionTypeSupplier;
    }

    public DimensionType getDimensionType() {
        return this.dimensionTypeSupplier.get();
    }

    public ChunkGenerator getChunkGenerator() {
        return this.chunkGenerator;
    }

    public static SimpleRegistry<DimensionOptions> method_29569(SimpleRegistry<DimensionOptions> simpleRegistry) {
        SimpleRegistry<DimensionOptions> simpleRegistry2 = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_OPTIONS, Lifecycle.experimental());
        for (RegistryKey registryKey : BASE_DIMENSIONS) {
            DimensionOptions dimensionOptions = simpleRegistry.get(registryKey);
            if (dimensionOptions == null) continue;
            simpleRegistry2.add(registryKey, dimensionOptions);
            if (!simpleRegistry.isLoaded(registryKey)) continue;
            simpleRegistry2.markLoaded(registryKey);
        }
        for (Map.Entry entry : simpleRegistry.getEntries()) {
            RegistryKey registryKey2 = (RegistryKey)entry.getKey();
            if (BASE_DIMENSIONS.contains(registryKey2)) continue;
            simpleRegistry2.add(registryKey2, entry.getValue());
            if (!simpleRegistry.isLoaded(registryKey2)) continue;
            simpleRegistry2.markLoaded(registryKey2);
        }
        return simpleRegistry2;
    }

    public static boolean method_29567(long seed, SimpleRegistry<DimensionOptions> simpleRegistry) {
        ArrayList<Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions>> list = Lists.newArrayList(simpleRegistry.getEntries());
        if (list.size() != BASE_DIMENSIONS.size()) {
            return false;
        }
        Map.Entry entry = (Map.Entry)list.get(0);
        Map.Entry entry2 = (Map.Entry)list.get(1);
        Map.Entry entry3 = (Map.Entry)list.get(2);
        if (entry.getKey() != OVERWORLD || entry2.getKey() != NETHER || entry3.getKey() != END) {
            return false;
        }
        if (((DimensionOptions)entry.getValue()).getDimensionType() != DimensionType.OVERWORLD && ((DimensionOptions)entry.getValue()).getDimensionType() != DimensionType.OVERWORLD_CAVES) {
            return false;
        }
        if (((DimensionOptions)entry2.getValue()).getDimensionType() != DimensionType.THE_NETHER) {
            return false;
        }
        if (((DimensionOptions)entry3.getValue()).getDimensionType() != DimensionType.THE_END) {
            return false;
        }
        if (!(((DimensionOptions)entry2.getValue()).getChunkGenerator() instanceof SurfaceChunkGenerator) || !(((DimensionOptions)entry3.getValue()).getChunkGenerator() instanceof SurfaceChunkGenerator)) {
            return false;
        }
        SurfaceChunkGenerator surfaceChunkGenerator = (SurfaceChunkGenerator)((DimensionOptions)entry2.getValue()).getChunkGenerator();
        SurfaceChunkGenerator surfaceChunkGenerator2 = (SurfaceChunkGenerator)((DimensionOptions)entry3.getValue()).getChunkGenerator();
        if (!surfaceChunkGenerator.method_28548(seed, ChunkGeneratorType.Preset.NETHER)) {
            return false;
        }
        if (!surfaceChunkGenerator2.method_28548(seed, ChunkGeneratorType.Preset.END)) {
            return false;
        }
        if (!(surfaceChunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
            return false;
        }
        MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource)surfaceChunkGenerator.getBiomeSource();
        if (!multiNoiseBiomeSource.method_28462(seed)) {
            return false;
        }
        if (!(surfaceChunkGenerator2.getBiomeSource() instanceof TheEndBiomeSource)) {
            return false;
        }
        TheEndBiomeSource theEndBiomeSource = (TheEndBiomeSource)surfaceChunkGenerator2.getBiomeSource();
        return theEndBiomeSource.method_28479(seed);
    }
}

