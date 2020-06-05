/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class HugeFungusFeatureConfig
implements FeatureConfig {
    public static final Codec<HugeFungusFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockState.CODEC.fieldOf("valid_base_block")).forGetter(hugeFungusFeatureConfig -> hugeFungusFeatureConfig.validBaseBlock), ((MapCodec)BlockState.CODEC.fieldOf("stem_state")).forGetter(hugeFungusFeatureConfig -> hugeFungusFeatureConfig.stemState), ((MapCodec)BlockState.CODEC.fieldOf("hat_state")).forGetter(hugeFungusFeatureConfig -> hugeFungusFeatureConfig.hatState), ((MapCodec)BlockState.CODEC.fieldOf("decor_state")).forGetter(hugeFungusFeatureConfig -> hugeFungusFeatureConfig.decorationState), ((MapCodec)Codec.BOOL.fieldOf("planted")).withDefault(false).forGetter(hugeFungusFeatureConfig -> hugeFungusFeatureConfig.planted)).apply((Applicative<HugeFungusFeatureConfig, ?>)instance, HugeFungusFeatureConfig::new));
    public static final HugeFungusFeatureConfig CRIMSON_FUNGUS_CONFIG = new HugeFungusFeatureConfig(Blocks.CRIMSON_NYLIUM.getDefaultState(), Blocks.CRIMSON_STEM.getDefaultState(), Blocks.NETHER_WART_BLOCK.getDefaultState(), Blocks.SHROOMLIGHT.getDefaultState(), true);
    public static final HugeFungusFeatureConfig CRIMSON_FUNGUS_NOT_PLANTED_CONFIG = new HugeFungusFeatureConfig(HugeFungusFeatureConfig.CRIMSON_FUNGUS_CONFIG.validBaseBlock, HugeFungusFeatureConfig.CRIMSON_FUNGUS_CONFIG.stemState, HugeFungusFeatureConfig.CRIMSON_FUNGUS_CONFIG.hatState, HugeFungusFeatureConfig.CRIMSON_FUNGUS_CONFIG.decorationState, false);
    public static final HugeFungusFeatureConfig WARPED_FUNGUS_CONFIG = new HugeFungusFeatureConfig(Blocks.WARPED_NYLIUM.getDefaultState(), Blocks.WARPED_STEM.getDefaultState(), Blocks.WARPED_WART_BLOCK.getDefaultState(), Blocks.SHROOMLIGHT.getDefaultState(), true);
    public static final HugeFungusFeatureConfig WARPED_FUNGUS_NOT_PLANTED_CONFIG = new HugeFungusFeatureConfig(HugeFungusFeatureConfig.WARPED_FUNGUS_CONFIG.validBaseBlock, HugeFungusFeatureConfig.WARPED_FUNGUS_CONFIG.stemState, HugeFungusFeatureConfig.WARPED_FUNGUS_CONFIG.hatState, HugeFungusFeatureConfig.WARPED_FUNGUS_CONFIG.decorationState, false);
    public final BlockState validBaseBlock;
    public final BlockState stemState;
    public final BlockState hatState;
    public final BlockState decorationState;
    public final boolean planted;

    public HugeFungusFeatureConfig(BlockState validBaseBlock, BlockState stemState, BlockState hatState, BlockState decorationState, boolean planted) {
        this.validBaseBlock = validBaseBlock;
        this.stemState = stemState;
        this.hatState = hatState;
        this.decorationState = decorationState;
        this.planted = planted;
    }
}

