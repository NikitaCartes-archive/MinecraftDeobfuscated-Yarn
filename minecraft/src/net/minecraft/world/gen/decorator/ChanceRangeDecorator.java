package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.class_3667;
import net.minecraft.util.math.BlockPos;

public class ChanceRangeDecorator extends class_3667<ChanceRangeDecoratorConfig> {
	public ChanceRangeDecorator(Function<Dynamic<?>, ? extends ChanceRangeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15944(Random random, ChanceRangeDecoratorConfig chanceRangeDecoratorConfig, BlockPos blockPos) {
		if (random.nextFloat() < chanceRangeDecoratorConfig.chance) {
			int i = random.nextInt(16);
			int j = random.nextInt(chanceRangeDecoratorConfig.top - chanceRangeDecoratorConfig.topOffset) + chanceRangeDecoratorConfig.bottomOffset;
			int k = random.nextInt(16);
			return Stream.of(blockPos.add(i, j, k));
		} else {
			return Stream.empty();
		}
	}
}
