/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record SimpleBlockFeatureConfig(BlockStateProvider toPlace) implements FeatureConfig
{
    public static final Codec<SimpleBlockFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("to_place")).forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.toPlace)).apply((Applicative<SimpleBlockFeatureConfig, ?>)instance, SimpleBlockFeatureConfig::new));
}

