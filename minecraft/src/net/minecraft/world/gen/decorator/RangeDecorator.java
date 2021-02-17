package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class RangeDecorator extends BaseRangeDecorator {
	public RangeDecorator(Codec<RangeDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	protected int getY(Random random, int bottomY, int topY) {
		return MathHelper.nextInt(random, bottomY, topY);
	}
}
