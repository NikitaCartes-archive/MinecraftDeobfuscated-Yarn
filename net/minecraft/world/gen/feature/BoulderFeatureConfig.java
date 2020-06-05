/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class BoulderFeatureConfig
implements FeatureConfig {
    public static final Codec<BoulderFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(boulderFeatureConfig -> boulderFeatureConfig.state), ((MapCodec)Codec.INT.fieldOf("start_radius")).withDefault(0).forGetter(boulderFeatureConfig -> boulderFeatureConfig.startRadius)).apply((Applicative<BoulderFeatureConfig, ?>)instance, BoulderFeatureConfig::new));
    public final BlockState state;
    public final int startRadius;

    public BoulderFeatureConfig(BlockState state, int startRadius) {
        this.state = state;
        this.startRadius = startRadius;
    }
}

