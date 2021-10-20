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
    public static final Codec<SmallDripstoneFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_taller_dripstone")).orElse(Float.valueOf(0.2f)).forGetter(smallDripstoneFeatureConfig -> Float.valueOf(smallDripstoneFeatureConfig.chanceOfTallerDripstone)), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_directional_spread")).orElse(Float.valueOf(0.7f)).forGetter(smallDripstoneFeatureConfig -> Float.valueOf(smallDripstoneFeatureConfig.field_35416)), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_spread_radius2")).orElse(Float.valueOf(0.5f)).forGetter(smallDripstoneFeatureConfig -> Float.valueOf(smallDripstoneFeatureConfig.field_35417)), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_spread_radius3")).orElse(Float.valueOf(0.5f)).forGetter(smallDripstoneFeatureConfig -> Float.valueOf(smallDripstoneFeatureConfig.field_35418))).apply((Applicative<SmallDripstoneFeatureConfig, ?>)instance, SmallDripstoneFeatureConfig::new));
    public final float chanceOfTallerDripstone;
    public final float field_35416;
    public final float field_35417;
    public final float field_35418;

    public SmallDripstoneFeatureConfig(float f, float g, float h, float chanceOfTallerDripstone) {
        this.chanceOfTallerDripstone = f;
        this.field_35416 = g;
        this.field_35417 = h;
        this.field_35418 = chanceOfTallerDripstone;
    }
}

