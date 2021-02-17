package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;

public class ChanceDecorator extends AbstractCountDecorator<ChanceDecoratorConfig> {
	public ChanceDecorator(Codec<ChanceDecoratorConfig> codec) {
		super(codec);
	}

	protected int getCount(Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos) {
		return random.nextFloat() < 1.0F / (float)chanceDecoratorConfig.chance ? 1 : 0;
	}
}
