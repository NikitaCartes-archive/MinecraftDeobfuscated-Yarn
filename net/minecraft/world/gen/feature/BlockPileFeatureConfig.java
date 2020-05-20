/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class BlockPileFeatureConfig
implements FeatureConfig {
    public static final Codec<BlockPileFeatureConfig> field_24873 = ((MapCodec)BlockStateProvider.field_24937.fieldOf("state_provider")).xmap(BlockPileFeatureConfig::new, blockPileFeatureConfig -> blockPileFeatureConfig.stateProvider).codec();
    public final BlockStateProvider stateProvider;

    public BlockPileFeatureConfig(BlockStateProvider blockStateProvider) {
        this.stateProvider = blockStateProvider;
    }
}

