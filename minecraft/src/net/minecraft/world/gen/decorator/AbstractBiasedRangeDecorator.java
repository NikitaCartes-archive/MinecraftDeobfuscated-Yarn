package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractBiasedRangeDecorator extends AbstractRangeDecorator<BiasedRangedDecoratorConfig> {
	private static final Logger LOGGER = LogManager.getLogger();

	public AbstractBiasedRangeDecorator(Codec<BiasedRangedDecoratorConfig> codec) {
		super(codec);
	}

	protected int getY(DecoratorContext decoratorContext, Random random, BiasedRangedDecoratorConfig biasedRangedDecoratorConfig, int i) {
		int j = biasedRangedDecoratorConfig.getBottom().getY(decoratorContext);
		int k = biasedRangedDecoratorConfig.getTop().getY(decoratorContext);
		if (j >= k) {
			LOGGER.warn("Empty range decorator: {} [{}-{}]", this, j, k);
			return j;
		} else {
			return this.getY(random, j, k, biasedRangedDecoratorConfig.getCutoff());
		}
	}

	protected abstract int getY(Random random, int bottom, int top, int cutoff);
}
