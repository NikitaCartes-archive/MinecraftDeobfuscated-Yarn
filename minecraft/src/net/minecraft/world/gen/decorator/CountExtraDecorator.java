package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;

public class CountExtraDecorator extends AbstractCountDecorator<CountExtraDecoratorConfig> {
	public CountExtraDecorator(Codec<CountExtraDecoratorConfig> codec) {
		super(codec);
	}

	protected int getCount(Random random, CountExtraDecoratorConfig countExtraDecoratorConfig, BlockPos blockPos) {
		return countExtraDecoratorConfig.count + (random.nextFloat() < countExtraDecoratorConfig.extraChance ? countExtraDecoratorConfig.extraCount : 0);
	}
}
