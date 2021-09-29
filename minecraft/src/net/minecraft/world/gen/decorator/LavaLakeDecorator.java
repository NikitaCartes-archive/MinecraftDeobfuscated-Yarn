package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;

public class LavaLakeDecorator extends ConditionalDecorator<ChanceDecoratorConfig> {
	public LavaLakeDecorator(Codec<ChanceDecoratorConfig> codec) {
		super(codec);
	}

	protected boolean shouldPlace(DecoratorContext decoratorContext, Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos) {
		return blockPos.getY() < 63 || random.nextInt(10) == 0;
	}
}
