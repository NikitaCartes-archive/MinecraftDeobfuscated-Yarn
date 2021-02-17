package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseRangeDecorator extends AbstractRangeDecorator<RangeDecoratorConfig> {
	private static final Logger LOGGER = LogManager.getLogger();

	public BaseRangeDecorator(Codec<RangeDecoratorConfig> codec) {
		super(codec);
	}

	protected int getY(DecoratorContext decoratorContext, Random random, RangeDecoratorConfig rangeDecoratorConfig, int i) {
		int j = rangeDecoratorConfig.getBottom().getY(decoratorContext);
		int k = rangeDecoratorConfig.getTop().getY(decoratorContext);
		if (j >= k) {
			LOGGER.warn("Empty range decorator: {} [{}-{}]", this, j, k);
			return j;
		} else {
			return this.getY(random, j, k);
		}
	}

	protected abstract int getY(Random random, int bottomY, int topY);
}
