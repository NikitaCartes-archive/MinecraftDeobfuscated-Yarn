package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class ChancePassthroughDecorator extends SimpleDecorator<ChanceDecoratorConfig> {
	public ChancePassthroughDecorator(Function<Dynamic<?>, ? extends ChanceDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos) {
		return random.nextFloat() < 1.0F / (float)chanceDecoratorConfig.chance ? Stream.of(blockPos) : Stream.empty();
	}
}
