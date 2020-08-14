package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class DecoratedDecorator extends Decorator<DecoratedDecoratorConfig> {
	public DecoratedDecorator(Codec<DecoratedDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, DecoratedDecoratorConfig decoratedDecoratorConfig, BlockPos blockPos) {
		return decoratedDecoratorConfig.getOuter()
			.getPositions(decoratorContext, random, blockPos)
			.flatMap(blockPosx -> decoratedDecoratorConfig.getInner().getPositions(decoratorContext, random, blockPosx));
	}
}
