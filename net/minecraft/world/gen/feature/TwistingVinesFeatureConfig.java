/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.FeatureConfig;

public record TwistingVinesFeatureConfig(int spreadWidth, int spreadHeight, int maxHeight) implements FeatureConfig
{
    public static final Codec<TwistingVinesFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codecs.POSITIVE_INT.fieldOf("spread_width")).forGetter(TwistingVinesFeatureConfig::spreadWidth), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("spread_height")).forGetter(TwistingVinesFeatureConfig::spreadHeight), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("max_height")).forGetter(TwistingVinesFeatureConfig::maxHeight)).apply((Applicative<TwistingVinesFeatureConfig, ?>)instance, TwistingVinesFeatureConfig::new));
}

