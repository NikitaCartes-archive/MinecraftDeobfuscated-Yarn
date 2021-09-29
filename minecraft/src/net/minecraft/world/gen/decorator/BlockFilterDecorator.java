package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;

public class BlockFilterDecorator extends ConditionalDecorator<BlockFilterDecoratorConfig> {
	public BlockFilterDecorator(Codec<BlockFilterDecoratorConfig> codec) {
		super(codec);
	}

	protected boolean shouldPlace(DecoratorContext decoratorContext, Random random, BlockFilterDecoratorConfig blockFilterDecoratorConfig, BlockPos blockPos) {
		return blockFilterDecoratorConfig.getPredicate().test(decoratorContext.getWorld(), blockPos);
	}
}
