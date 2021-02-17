package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class VeryBiasedRangeDecoratorConfig extends AbstractBiasedRangeDecorator {
	public VeryBiasedRangeDecoratorConfig(Codec<BiasedRangedDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	protected int getY(Random random, int bottom, int top, int cutoff) {
		int i = MathHelper.nextInt(random, bottom + cutoff, top);
		int j = MathHelper.nextInt(random, bottom, i - 1);
		return MathHelper.nextInt(random, bottom, j - 1 + cutoff);
	}
}
