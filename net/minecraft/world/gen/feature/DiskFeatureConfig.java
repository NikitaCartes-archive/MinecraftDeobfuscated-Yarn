/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.PredicatedStateProvider;

public record DiskFeatureConfig(PredicatedStateProvider stateProvider, BlockPredicate target, IntProvider radius, int halfHeight) implements FeatureConfig
{
    public static final Codec<DiskFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)PredicatedStateProvider.CODEC.fieldOf("state_provider")).forGetter(DiskFeatureConfig::stateProvider), ((MapCodec)BlockPredicate.BASE_CODEC.fieldOf("target")).forGetter(DiskFeatureConfig::target), ((MapCodec)IntProvider.createValidatingCodec(0, 8).fieldOf("radius")).forGetter(DiskFeatureConfig::radius), ((MapCodec)Codec.intRange(0, 4).fieldOf("half_height")).forGetter(DiskFeatureConfig::halfHeight)).apply((Applicative<DiskFeatureConfig, ?>)instance, DiskFeatureConfig::new));
}

