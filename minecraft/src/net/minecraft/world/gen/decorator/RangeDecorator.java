package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;

public class RangeDecorator extends AbstractRangeDecorator<RangeDecoratorConfig> {
	public RangeDecorator(Codec<RangeDecoratorConfig> codec) {
		super(codec);
	}

	protected int getY(DecoratorContext decoratorContext, Random random, RangeDecoratorConfig rangeDecoratorConfig, int i) {
		return rangeDecoratorConfig.heightProvider.get(random, decoratorContext);
	}
}
