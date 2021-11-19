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
    public static final Codec<SmallDripstoneFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_taller_dripstone")).orElse(Float.valueOf(0.2f)).forGetter(smallDripstoneFeatureConfig -> Float.valueOf(smallDripstoneFeatureConfig.chanceOfTallerDripstone)), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_directional_spread")).orElse(Float.valueOf(0.7f)).forGetter(smallDripstoneFeatureConfig -> Float.valueOf(smallDripstoneFeatureConfig.chanceOfDirectionalSpread)), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_spread_radius2")).orElse(Float.valueOf(0.5f)).forGetter(smallDripstoneFeatureConfig -> Float.valueOf(smallDripstoneFeatureConfig.chanceOfSpreadRadius2)), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_spread_radius3")).orElse(Float.valueOf(0.5f)).forGetter(smallDripstoneFeatureConfig -> Float.valueOf(smallDripstoneFeatureConfig.chanceOfSpreadRadius3))).apply((Applicative<SmallDripstoneFeatureConfig, ?>)instance, SmallDripstoneFeatureConfig::new));
    public final float chanceOfTallerDripstone;
    public final float chanceOfDirectionalSpread;
    public final float chanceOfSpreadRadius2;
    public final float chanceOfSpreadRadius3;

    public SmallDripstoneFeatureConfig(float chanceOfTallerDripstone, float chanceOfDirectionalSpread, float chanceOfSpreadRadius2, float chanceOfSpreadRadius3) {
        this.chanceOfTallerDripstone = chanceOfTallerDripstone;
        this.chanceOfDirectionalSpread = chanceOfDirectionalSpread;
        this.chanceOfSpreadRadius2 = chanceOfSpreadRadius2;
        this.chanceOfSpreadRadius3 = chanceOfSpreadRadius3;
    }
}

