package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class BlockPileFeatureConfig implements FeatureConfig {
	public static final Codec<BlockPileFeatureConfig> CODEC = BlockStateProvider.CODEC
		.fieldOf("state_provider")
		.<BlockPileFeatureConfig>xmap(BlockPileFeatureConfig::new, blockPileFeatureConfig -> blockPileFeatureConfig.stateProvider)
		.codec();
	public final BlockStateProvider stateProvider;

	public BlockPileFeatureConfig(BlockStateProvider blockStateProvider) {
		this.stateProvider = blockStateProvider;
	}
}
