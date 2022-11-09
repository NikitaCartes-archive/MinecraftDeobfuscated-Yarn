/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

public record RandomPatchFeatureConfig(int tries, int xzSpread, int ySpread, RegistryEntry<PlacedFeature> feature) implements FeatureConfig
{
    public static final Codec<RandomPatchFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codecs.POSITIVE_INT.fieldOf("tries")).orElse(128).forGetter(RandomPatchFeatureConfig::tries), ((MapCodec)Codecs.NONNEGATIVE_INT.fieldOf("xz_spread")).orElse(7).forGetter(RandomPatchFeatureConfig::xzSpread), ((MapCodec)Codecs.NONNEGATIVE_INT.fieldOf("y_spread")).orElse(3).forGetter(RandomPatchFeatureConfig::ySpread), ((MapCodec)PlacedFeature.REGISTRY_CODEC.fieldOf("feature")).forGetter(RandomPatchFeatureConfig::feature)).apply((Applicative<RandomPatchFeatureConfig, ?>)instance, RandomPatchFeatureConfig::new));
}

