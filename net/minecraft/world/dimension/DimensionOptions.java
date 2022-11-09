/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public record DimensionOptions(RegistryEntry<DimensionType> dimensionTypeEntry, ChunkGenerator chunkGenerator) {
    public static final Codec<DimensionOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)DimensionType.REGISTRY_CODEC.fieldOf("type")).forGetter(DimensionOptions::dimensionTypeEntry), ((MapCodec)ChunkGenerator.CODEC.fieldOf("generator")).forGetter(DimensionOptions::chunkGenerator)).apply((Applicative<DimensionOptions, ?>)instance, instance.stable(DimensionOptions::new)));
    public static final RegistryKey<DimensionOptions> OVERWORLD = RegistryKey.of(RegistryKeys.DIMENSION, new Identifier("overworld"));
    public static final RegistryKey<DimensionOptions> NETHER = RegistryKey.of(RegistryKeys.DIMENSION, new Identifier("the_nether"));
    public static final RegistryKey<DimensionOptions> END = RegistryKey.of(RegistryKeys.DIMENSION, new Identifier("the_end"));
}

