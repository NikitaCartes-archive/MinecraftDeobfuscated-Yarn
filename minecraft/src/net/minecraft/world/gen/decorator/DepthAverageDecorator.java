package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;

public class DepthAverageDecorator extends AbstractRangeDecorator<DepthAverageDecoratorConfig> {
	public DepthAverageDecorator(Codec<DepthAverageDecoratorConfig> codec) {
		super(codec);
	}

	protected int getY(DecoratorContext decoratorContext, Random random, DepthAverageDecoratorConfig depthAverageDecoratorConfig, int i) {
		int j = depthAverageDecoratorConfig.getSpread();
		return random.nextInt(j) + random.nextInt(j) - j + depthAverageDecoratorConfig.getBaseline().getY(decoratorContext);
	}
}
