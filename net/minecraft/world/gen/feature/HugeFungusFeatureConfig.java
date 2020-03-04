/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class HugeFungusFeatureConfig
implements FeatureConfig {
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

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<T>(ops, ops.createMap(ImmutableMap.of(ops.createString("valid_base_block"), BlockState.serialize(ops, this.validBaseBlock).getValue(), ops.createString("stem_state"), BlockState.serialize(ops, this.stemState).getValue(), ops.createString("hat_state"), BlockState.serialize(ops, this.hatState).getValue(), ops.createString("decor_state"), BlockState.serialize(ops, this.decorationState).getValue(), ops.createString("planted"), ops.createBoolean(this.planted))));
    }

    public static <T> HugeFungusFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockState blockState = dynamic.get("valid_base_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState blockState2 = dynamic.get("stem_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState blockState3 = dynamic.get("hat_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState blockState4 = dynamic.get("decor_state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        boolean bl = dynamic.get("planted").asBoolean(false);
        return new HugeFungusFeatureConfig(blockState, blockState2, blockState3, blockState4, bl);
    }
}

