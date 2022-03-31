/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public record SculkPatchFeatureConfig(int chargeCount, int amountPerCharge, int spreadAttempts, int growthRounds, int spreadRounds, IntProvider extraRareGrowths, float catalystChance) implements FeatureConfig
{
    public static final Codec<SculkPatchFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.intRange(1, 32).fieldOf("charge_count")).forGetter(SculkPatchFeatureConfig::chargeCount), ((MapCodec)Codec.intRange(1, 500).fieldOf("amount_per_charge")).forGetter(SculkPatchFeatureConfig::amountPerCharge), ((MapCodec)Codec.intRange(1, 64).fieldOf("spread_attempts")).forGetter(SculkPatchFeatureConfig::spreadAttempts), ((MapCodec)Codec.intRange(0, 8).fieldOf("growth_rounds")).forGetter(SculkPatchFeatureConfig::growthRounds), ((MapCodec)Codec.intRange(0, 8).fieldOf("spread_rounds")).forGetter(SculkPatchFeatureConfig::spreadRounds), ((MapCodec)IntProvider.VALUE_CODEC.fieldOf("extra_rare_growths")).forGetter(SculkPatchFeatureConfig::extraRareGrowths), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("catalyst_chance")).forGetter(SculkPatchFeatureConfig::catalystChance)).apply((Applicative<SculkPatchFeatureConfig, ?>)instance, SculkPatchFeatureConfig::new));
}

