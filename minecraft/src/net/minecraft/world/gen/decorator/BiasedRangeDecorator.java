package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class BiasedRangeDecorator extends AbstractBiasedRangeDecorator {
	public BiasedRangeDecorator(Codec<BiasedRangedDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	protected int getY(Random random, int bottom, int top, int cutoff) {
		int i = MathHelper.nextInt(random, bottom + cutoff, top);
		return MathHelper.nextInt(random, bottom, i - 1);
	}
}
