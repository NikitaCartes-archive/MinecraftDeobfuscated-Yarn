/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
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

public final class class_5363 {
    public static final Codec<class_5363> field_25411 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)DimensionType.field_24756.fieldOf("type")).forGetter(class_5363::method_29566), ((MapCodec)ChunkGenerator.field_24746.fieldOf("generator")).forGetter(class_5363::method_29571)).apply((Applicative<class_5363, ?>)instance, instance.stable(class_5363::new)));
    public static final RegistryKey<class_5363> field_25412 = RegistryKey.of(Registry.field_25490, new Identifier("overworld"));
    public static final RegistryKey<class_5363> field_25413 = RegistryKey.of(Registry.field_25490, new Identifier("the_nether"));
    public static final RegistryKey<class_5363> field_25414 = RegistryKey.of(Registry.field_25490, new Identifier("the_end"));
    private static final LinkedHashSet<RegistryKey<class_5363>> field_25415 = Sets.newLinkedHashSet(ImmutableList.of(field_25412, field_25413, field_25414));
    private final Supplier<DimensionType> field_25416;
    private final ChunkGenerator field_25417;

    public class_5363(Supplier<DimensionType> supplier, ChunkGenerator chunkGenerator) {
        this.field_25416 = supplier;
        this.field_25417 = chunkGenerator;
    }

    public Supplier<DimensionType> method_29566() {
        return this.field_25416;
    }

    public DimensionType method_29570() {
        return this.field_25416.get();
    }

    public ChunkGenerator method_29571() {
        return this.field_25417;
    }

    public static SimpleRegistry<class_5363> method_29569(SimpleRegistry<class_5363> simpleRegistry) {
        SimpleRegistry<class_5363> simpleRegistry2 = new SimpleRegistry<class_5363>(Registry.field_25490, Lifecycle.experimental());
        for (RegistryKey registryKey : field_25415) {
            class_5363 lv = simpleRegistry.get(registryKey);
            if (lv == null) continue;
            simpleRegistry2.add(registryKey, lv);
            if (!simpleRegistry.method_29723(registryKey)) continue;
            simpleRegistry2.method_29725(registryKey);
        }
        for (Map.Entry entry : simpleRegistry.method_29722()) {
            RegistryKey registryKey2 = (RegistryKey)entry.getKey();
            if (field_25415.contains(registryKey2)) continue;
            simpleRegistry2.add(registryKey2, entry.getValue());
            if (!simpleRegistry.method_29723(registryKey2)) continue;
            simpleRegistry2.method_29725(registryKey2);
        }
        return simpleRegistry2;
    }

    public static boolean method_29567(long l, SimpleRegistry<class_5363> simpleRegistry) {
        ArrayList<Map.Entry<RegistryKey<class_5363>, class_5363>> list = Lists.newArrayList(simpleRegistry.method_29722());
        if (list.size() != field_25415.size()) {
            return false;
        }
        Map.Entry entry = (Map.Entry)list.get(0);
        Map.Entry entry2 = (Map.Entry)list.get(1);
        Map.Entry entry3 = (Map.Entry)list.get(2);
        if (entry.getKey() != field_25412 || entry2.getKey() != field_25413 || entry3.getKey() != field_25414) {
            return false;
        }
        if (!(((class_5363)entry.getValue()).method_29570().isOverworld() && ((class_5363)entry2.getValue()).method_29570().isNether() && ((class_5363)entry3.getValue()).method_29570().isEnd())) {
            return false;
        }
        if (!(((class_5363)entry2.getValue()).method_29571() instanceof SurfaceChunkGenerator) || !(((class_5363)entry3.getValue()).method_29571() instanceof SurfaceChunkGenerator)) {
            return false;
        }
        SurfaceChunkGenerator surfaceChunkGenerator = (SurfaceChunkGenerator)((class_5363)entry2.getValue()).method_29571();
        SurfaceChunkGenerator surfaceChunkGenerator2 = (SurfaceChunkGenerator)((class_5363)entry3.getValue()).method_29571();
        if (!surfaceChunkGenerator.method_28548(l, ChunkGeneratorType.Preset.NETHER)) {
            return false;
        }
        if (!surfaceChunkGenerator2.method_28548(l, ChunkGeneratorType.Preset.END)) {
            return false;
        }
        if (!(surfaceChunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
            return false;
        }
        MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource)surfaceChunkGenerator.getBiomeSource();
        if (!multiNoiseBiomeSource.method_28462(l)) {
            return false;
        }
        if (!(surfaceChunkGenerator2.getBiomeSource() instanceof TheEndBiomeSource)) {
            return false;
        }
        TheEndBiomeSource theEndBiomeSource = (TheEndBiomeSource)surfaceChunkGenerator2.getBiomeSource();
        return theEndBiomeSource.method_28479(l);
    }
}

