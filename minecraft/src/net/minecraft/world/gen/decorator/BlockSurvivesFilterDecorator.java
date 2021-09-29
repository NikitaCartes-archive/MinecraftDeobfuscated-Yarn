package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;

public class BlockSurvivesFilterDecorator extends ConditionalDecorator<BlockSurvivesFilterDecoratorConfig> {
	public BlockSurvivesFilterDecorator(Codec<BlockSurvivesFilterDecoratorConfig> codec) {
		super(codec);
	}

	protected boolean shouldPlace(
		DecoratorContext decoratorContext, Random random, BlockSurvivesFilterDecoratorConfig blockSurvivesFilterDecoratorConfig, BlockPos blockPos
	) {
		return blockSurvivesFilterDecoratorConfig.state().canPlaceAt(decoratorContext.getWorld(), blockPos);
	}
}
