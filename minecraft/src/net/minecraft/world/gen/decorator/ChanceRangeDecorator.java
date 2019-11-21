package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class ChanceRangeDecorator extends SimpleDecorator<ChanceRangeDecoratorConfig> {
	public ChanceRangeDecorator(Function<Dynamic<?>, ? extends ChanceRangeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(Random random, ChanceRangeDecoratorConfig chanceRangeDecoratorConfig, BlockPos blockPos) {
		if (random.nextFloat() < chanceRangeDecoratorConfig.chance) {
			int i = random.nextInt(16) + blockPos.getX();
			int j = random.nextInt(16) + blockPos.getZ();
			int k = random.nextInt(chanceRangeDecoratorConfig.top - chanceRangeDecoratorConfig.topOffset) + chanceRangeDecoratorConfig.bottomOffset;
			return Stream.of(new BlockPos(i, k, j));
		} else {
			return Stream.empty();
		}
	}
}
