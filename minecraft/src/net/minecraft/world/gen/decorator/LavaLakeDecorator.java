package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;

public class LavaLakeDecorator extends AbstractCountDecorator<ChanceDecoratorConfig> {
	public LavaLakeDecorator(Codec<ChanceDecoratorConfig> codec) {
		super(codec);
	}

	protected int getCount(Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos) {
		return blockPos.getY() >= 63 && random.nextInt(10) != 0 ? 0 : 1;
	}
}
