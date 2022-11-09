/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.level;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;

public record WorldGenSettings(GeneratorOptions generatorOptions, DimensionOptionsRegistryHolder dimensionOptionsRegistryHolder) {
    public static final Codec<WorldGenSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(GeneratorOptions.CODEC.forGetter(WorldGenSettings::generatorOptions), DimensionOptionsRegistryHolder.CODEC.forGetter(WorldGenSettings::dimensionOptionsRegistryHolder)).apply((Applicative<WorldGenSettings, ?>)instance, instance.stable(WorldGenSettings::new)));

    public static <T> DataResult<T> encode(DynamicOps<T> registryOps, GeneratorOptions generatorOptions, DimensionOptionsRegistryHolder dimensionOptionsRegistryHolder) {
        return CODEC.encodeStart(registryOps, new WorldGenSettings(generatorOptions, dimensionOptionsRegistryHolder));
    }

    public static <T> DataResult<T> encode(DynamicOps<T> registryOps, GeneratorOptions generatorOptions, DynamicRegistryManager dynamicRegistryManager) {
        return WorldGenSettings.encode(registryOps, generatorOptions, new DimensionOptionsRegistryHolder(dynamicRegistryManager.get(RegistryKeys.DIMENSION)));
    }
}

