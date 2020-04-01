package net.minecraft.world.gen.decorator;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class HellFireDecorator extends SimpleDecorator<CountDecoratorConfig> {
	public HellFireDecorator(Function<Dynamic<?>, ? extends CountDecoratorConfig> function, Function<Random, ? extends CountDecoratorConfig> function2) {
		super(function, function2);
	}

	public Stream<BlockPos> getPositions(Random random, CountDecoratorConfig countDecoratorConfig, BlockPos blockPos) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();

		for (int i = 0; i < random.nextInt(random.nextInt(countDecoratorConfig.count) + 1) + 1; i++) {
			int j = random.nextInt(16) + blockPos.getX();
			int k = random.nextInt(16) + blockPos.getZ();
			int l = random.nextInt(120) + 4;
			list.add(new BlockPos(j, l, k));
		}

		return list.stream();
	}
}
