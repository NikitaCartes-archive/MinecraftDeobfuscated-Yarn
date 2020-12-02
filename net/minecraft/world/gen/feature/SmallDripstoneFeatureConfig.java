/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SmallDripstoneFeatureConfig
implements FeatureConfig {
    public static final Codec<SmallDripstoneFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.intRange(0, 100).fieldOf("max_placements")).orElse(5).forGetter(smallDripstoneFeatureConfig -> smallDripstoneFeatureConfig.maxPlacements), ((MapCodec)Codec.intRange(0, 20).fieldOf("empty_space_search_radius")).orElse(10).forGetter(smallDripstoneFeatureConfig -> smallDripstoneFeatureConfig.emptySpaceSearchRadius), ((MapCodec)Codec.intRange(0, 20).fieldOf("max_offset_from_origin")).orElse(2).forGetter(smallDripstoneFeatureConfig -> smallDripstoneFeatureConfig.maxOffsetFromOrigin), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_taller_dripstone")).orElse(Float.valueOf(0.2f)).forGetter(smallDripstoneFeatureConfig -> Float.valueOf(smallDripstoneFeatureConfig.chanceOfTallerDripstone))).apply((Applicative<SmallDripstoneFeatureConfig, ?>)instance, SmallDripstoneFeatureConfig::new));
    public final int maxPlacements;
    public final int emptySpaceSearchRadius;
    public final int maxOffsetFromOrigin;
    public final float chanceOfTallerDripstone;

    public SmallDripstoneFeatureConfig(int maxPlacements, int emptySpaceSearchRadius, int maxOffsetFromOrigin, float chanceOfTallerDripstone) {
        this.maxPlacements = maxPlacements;
        this.emptySpaceSearchRadius = emptySpaceSearchRadius;
        this.maxOffsetFromOrigin = maxOffsetFromOrigin;
        this.chanceOfTallerDripstone = chanceOfTallerDripstone;
    }
}

