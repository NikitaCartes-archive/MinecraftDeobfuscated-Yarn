package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;

public record BlockSurvivesFilterDecoratorConfig() implements DecoratorConfig {
	private final BlockState state;
	public static final Codec<BlockSurvivesFilterDecoratorConfig> CODEC = BlockState.CODEC
		.fieldOf("state")
		.<BlockSurvivesFilterDecoratorConfig>xmap(BlockSurvivesFilterDecoratorConfig::new, BlockSurvivesFilterDecoratorConfig::state)
		.codec();

	public BlockSurvivesFilterDecoratorConfig(BlockState blockState) {
		this.state = blockState;
	}
}
