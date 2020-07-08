package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class ChanceDecorator extends SimpleDecorator<ChanceDecoratorConfig> {
	public ChanceDecorator(Codec<ChanceDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos) {
		return random.nextFloat() < 1.0F / (float)chanceDecoratorConfig.chance ? Stream.of(blockPos) : Stream.empty();
	}
}
