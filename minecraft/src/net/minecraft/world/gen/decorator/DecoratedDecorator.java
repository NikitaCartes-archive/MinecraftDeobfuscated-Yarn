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
		return decoratedDecoratorConfig.method_30455()
			.method_30444(decoratorContext, random, blockPos)
			.flatMap(blockPosx -> decoratedDecoratorConfig.method_30457().method_30444(decoratorContext, random, blockPosx));
	}
}
