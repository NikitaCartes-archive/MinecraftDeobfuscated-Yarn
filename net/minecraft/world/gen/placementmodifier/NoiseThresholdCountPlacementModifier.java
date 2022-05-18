/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.placementmodifier.AbstractCountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class NoiseThresholdCountPlacementModifier
extends AbstractCountPlacementModifier {
    public static final Codec<NoiseThresholdCountPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.DOUBLE.fieldOf("noise_level")).forGetter(noiseThresholdCountPlacementModifier -> noiseThresholdCountPlacementModifier.noiseLevel), ((MapCodec)Codec.INT.fieldOf("below_noise")).forGetter(noiseThresholdCountPlacementModifier -> noiseThresholdCountPlacementModifier.belowNoise), ((MapCodec)Codec.INT.fieldOf("above_noise")).forGetter(noiseThresholdCountPlacementModifier -> noiseThresholdCountPlacementModifier.aboveNoise)).apply((Applicative<NoiseThresholdCountPlacementModifier, ?>)instance, NoiseThresholdCountPlacementModifier::new));
    private final double noiseLevel;
    private final int belowNoise;
    private final int aboveNoise;

    private NoiseThresholdCountPlacementModifier(double noiseLevel, int belowNoise, int aboveNoise) {
        this.noiseLevel = noiseLevel;
        this.belowNoise = belowNoise;
        this.aboveNoise = aboveNoise;
    }

    public static NoiseThresholdCountPlacementModifier of(double noiseLevel, int belowNoise, int aboveNoise) {
        return new NoiseThresholdCountPlacementModifier(noiseLevel, belowNoise, aboveNoise);
    }

    @Override
    protected int getCount(Random random, BlockPos pos) {
        double d = Biome.FOLIAGE_NOISE.sample((double)pos.getX() / 200.0, (double)pos.getZ() / 200.0, false);
        return d < this.noiseLevel ? this.belowNoise : this.aboveNoise;
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.NOISE_THRESHOLD_COUNT;
    }
}

